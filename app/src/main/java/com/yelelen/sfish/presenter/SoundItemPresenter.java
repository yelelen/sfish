package com.yelelen.sfish.presenter;

import android.text.TextUtils;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.yelelen.sfish.App;
import com.yelelen.sfish.Model.DB;
import com.yelelen.sfish.Model.SoundItemModel;
import com.yelelen.sfish.Model.SoundItemModel_Table;
import com.yelelen.sfish.Model.SoundLabelIndexModel;
import com.yelelen.sfish.Model.SoundLabelIndexModel_Table;
import com.yelelen.sfish.contract.LoadContent;
import com.yelelen.sfish.helper.Contant;
import com.yelelen.sfish.helper.SoundDbHelper;
import com.yelelen.sfish.helper.ThreadPoolHelper;
import com.yelelen.sfish.parser.JsonParser;
import com.yelelen.sfish.parser.SoundParser;
import com.yelelen.sfish.parser.SoundSuggestParser;
import com.yelelen.sfish.runnable.SoundImageRunnable;
import com.yelelen.sfish.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by yelelen on 17-10-13.
 */

public class SoundItemPresenter extends BasePresenter<SoundItemModel> {
    private static final String SOUND_ALBUM_URL = Contant.ES_URL + "audio/album/_search";
    public static Map<String, String> mHeader;
    private int mLabelIndex = 0;
    private String mLastLabel = "";
    private int mZhuboAlbumIndex = 0;
    private LoadContent<SoundItemModel> mListener;
    private boolean mNoMore = false;

    public SoundItemPresenter(LoadContent<SoundItemModel> listener) {
        super(SOUND_ALBUM_URL, SoundDbHelper.getInstance(), listener);
        mListener = listener;
        mHeader = new HashMap<>();
        mHeader.put("Referer", "http://www.ximalaya.com");
        mHeader.put("User-Agent",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)" +
                        " Chrome/60.0.3112.101 Safari/537.36");
    }

    @Override
    public void handleData(SoundItemModel data) {
        String cover = data.getCover();
        boolean isJpg = cover.contains(".jpg");
        boolean isJPG = cover.contains(".JPG");
        boolean isPng = cover.contains(".png");
        boolean isPNG = cover.contains(".PNG");
        boolean endJpg = cover.endsWith(".jpg");
        boolean endJPG = cover.endsWith(".JPG");
        boolean endPng = cover.endsWith(".png");
        boolean endPNG = cover.endsWith(".PNG");

        if (!endJpg && !endJPG && !endPng && !endPNG) {
            if (isJpg)
                cover = handleCoverUrl(cover, ".jpg");
            if (isJPG)
                cover = handleCoverUrl(cover, ".JPG");
            if (isPng)
                cover = handleCoverUrl(cover, ".png");
            if (isPNG)
                cover = handleCoverUrl(cover, ".PNG");
        }
        data.setCover(cover);
        File dir = new File(App.mSoundAlbumBasePath);
        String fileName = Utils.getMD5(data.getCover());
        String path = dir.getAbsolutePath() + File.separator + fileName;
        if (!new File(path).exists()) {
            SoundImageRunnable runnable = new SoundImageRunnable(App.mAppContext,
                    data.getCover(), dir, fileName, this, mHeader);
            ThreadPoolHelper.getInstance().start(runnable);
        }
        data.setPath(path);
    }

    private String handleCoverUrl(String url, String type) {
        String[] covers = url.split(type);
        return covers[0] + type;
    }

    @Override
    protected Class<SoundItemModel> getModelClass() {
        return SoundItemModel.class;
    }

    @Override
    protected JsonParser<SoundItemModel> getParser(int type) {
        if (type == Contant.PARSER_TYPE_SUGGEST) {
            return new SoundSuggestParser();
        } else if (type == Contant.PARSER_TYPE_MORE || type == Contant.PARSER_TYPE_ONE) {
            return new SoundParser();
        }
        return null;
    }

    //    {"query":{"match_all":{}}, "sort": {"order":{"order":"desc"}}, "size":5}
    private String buildLatestJson(int count, String indexField, int index, String orderField, String order) {
        return "{\"query\":{\"range\":{\"" + indexField + "\": {\"gt\":" + index + "}}}," + "\"sort\": {\"" +
                orderField + "\":{\"order\":\"" +
                order + "\"}}, \"size\":" + count + "}";
    }

    private String buildMoreJson(int count, String indexField, int index, String orderField, String order) {
        return "{\"query\":{\"range\":{\"" + indexField + "\": {\"lt\":" + index + "}}}," + "\"sort\": {\"" +
                orderField + "\":{\"order\":\"" +
                order + "\"}}, \"size\":" + count + "}";
    }

    //    {"_source": ["tags", "title", "order","seen_num", "fav_num", "total_num", "first_image_url"],
//        "suggest":{"mm-suggest":{"text":"性感","completion":{"field":"suggest", "size": 1000}}}}
    @Override
    protected String buildSuggestJson(int count, String label) {
        return "{\"_source\": [\"tags\", \"title\", \"order\",\"seen_num\", \"fav_num\", \"total_num\", \"first_image_url\"]," +
                "\"suggest\":{\"mm-suggest\":{\"text\":\"" + label + "\",\"completion\":{\"field\":\"suggest\", \"size\": " + count + "}}}}";
    }

    private String buildLabelJson(String label, int count, int startIndex, String orderField, String order) {
        return "{\n" +
                "    \"query\" : {\n" +
                "        \"constant_score\" : {\n" +
                "            \"filter\" : {\n" +
                "                \"term\" : { \n" +
                "                    \"aa_tag\" : \"" + label + "\"\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "    },\"from\": " + startIndex + ", \"size\":" + count + ", \"sort\":{\"" + orderField + "\": {\"order\":\"" + order + "\"}}\n" +
                "}";
    }

    @Override
    protected String buildOneJson(int id) {
        return "{\"query\":{\"term\":{\"aa_order\":" + id + "}}}";
    }

    @Override
    protected String buildLatestJson(int count, int index) {
        return buildLatestJson(count, "order", index, "order", Contant.DESC);
    }

    @Override
    protected String buildMoreJson(int count, int index) {
        return buildMoreJson(count, "aa_play_num", index, "aa_play_num", Contant.DESC);
    }


    protected String buildLabelJson(String label, int count, int startIndex) {
        return buildLabelJson(label, count, startIndex, "aa_play_num", Contant.DESC);
    }


    public void saveLabelIndex(List<SoundItemModel> models, String label) {
        if (models != null && models.size() > 0) {
            StringBuilder builder = new StringBuilder();
            final SoundLabelIndexModel item = new SoundLabelIndexModel(label, "");
            for (SoundItemModel model : models) {
                builder.append(model.getOrder()).append(" ");
            }

            item.setOrders(new String(builder));

            final SoundLabelIndexModel model = SQLite.select()
                    .from(SoundLabelIndexModel.class)
                    .where(SoundLabelIndexModel_Table._id.eq(label))
                    .querySingle();
            if (model != null) {
                model.setOrders(model.getOrders() + item.getOrders());
                Set<Integer> set = new HashSet<>();
                for (String str : model.getOrders().split(" ")) {
                    set.add(Integer.valueOf(str));
                }
                builder.delete(0, builder.length());
                for (Integer integer : set) {
                    builder.append(integer).append(" ");
                }

                model.setOrders(new String(builder));
            }

            DatabaseDefinition definition = FlowManager.getDatabase(DB.class);
            definition.executeTransaction(new ITransaction() {
                @Override
                public void execute(DatabaseWrapper databaseWrapper) {
                    ModelAdapter<SoundLabelIndexModel> adapter = FlowManager.getModelAdapter(SoundLabelIndexModel.class);
                    if (model != null)
                        adapter.save(model);
                    else
                        adapter.save(item);
                }
            });
        }
    }

    @Override
    protected synchronized List<SoundItemModel> loadLocalLabelData(int count, String label) {
        if (!mLastLabel.equals(label)) {
            mLastLabel = label;
            mLabelIndex = 0;
        }

        SoundLabelIndexModel model = SQLite.select()
                .from(SoundLabelIndexModel.class)
                .where(SoundLabelIndexModel_Table._id.eq(label))
                .querySingle();
        if (model == null || TextUtils.isEmpty(model.getOrders())) {
            return null;
        }

        List<SoundItemModel> models = new ArrayList<>();
        String[] ids = model.getOrders().split(" ");
        count = Math.min(count, ids.length - mLabelIndex);
        if (count == 0) {
            return null;
        }
        for (int i = mLabelIndex; i < mLabelIndex + count; i++) {
            SoundItemModel item = SQLite.select()
                    .from(SoundItemModel.class)
                    .where(SoundItemModel_Table._id.eq(Integer.valueOf(ids[i])))
                    .querySingle();
            if (item != null)
                models.add(item);
        }
        mLabelIndex += count;
        return models;
    }

    @Override
    protected List<SoundItemModel> loadLocalOneData(int curOneOrder) {
        SoundItemModel model = SoundDbHelper.getInstance().getByOrder(curOneOrder);
        List<SoundItemModel> models = new ArrayList<SoundItemModel>();
        models.add(model);
        return models;
    }


    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public void loadAlbumByZhuboId(int count, int zhuboId) {
        OkHttpClient client = new OkHttpClient();
        String json = buildZhuboAlbumJson(count, zhuboId);
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(SOUND_ALBUM_URL).post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    String body = response.body().string();
                    SoundParser parser = new SoundParser();
                    List<SoundItemModel> ts = parser.parse(body);
                    if (ts == null || ts.size() == 0)
                        mNoMore = true;
                    if (mNoMore) {
                        mListener.onLoadDone(null);
                    } else {
                        mListener.onLoadDone(ts);
                        mZhuboAlbumIndex += ts.size();
                    }
                } else {
                    mListener.onLoadFailed("服务器内部异常");
                }
            }
        });
    }


    private String buildZhuboAlbumJson(int count, int zhuboId) {
        return "{\"query\":{\"term\":{\"aa_zhubo_id\":" + zhuboId + "}}," +
                " \"sort\":{\"aa_play_num\":{\"order\":\"desc\"}}, \"from\":" + mZhuboAlbumIndex + ", \"size\":" + count + "}";
    }

}
