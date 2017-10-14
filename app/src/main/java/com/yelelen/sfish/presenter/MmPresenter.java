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
import com.yelelen.sfish.Model.MmItemModel;
import com.yelelen.sfish.Model.MmItemModel_Table;
import com.yelelen.sfish.Model.MmLabelIndexModel;
import com.yelelen.sfish.Model.MmLabelIndexModel_Table;
import com.yelelen.sfish.contract.LoadContent;
import com.yelelen.sfish.helper.Contant;
import com.yelelen.sfish.helper.ElasticHelper;
import com.yelelen.sfish.helper.MmDbHelper;
import com.yelelen.sfish.helper.ThreadPoolHelper;
import com.yelelen.sfish.parser.JsonParser;
import com.yelelen.sfish.parser.MmParser;
import com.yelelen.sfish.parser.MmSuggestParser;
import com.yelelen.sfish.runnable.MmLoaderImageRunnable;
import com.yelelen.sfish.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.yelelen.sfish.helper.Contant.DESC;

/**
 * Created by yelelen on 17-9-14.
 */

public class MmPresenter extends BasePresenter<MmItemModel> {
    private MmLoaderImageRunnable mImageRunnable;
    private ElasticHelper<MmItemModel> mElasticHelper;
    private static final String MM_URL = Contant.ES_URL + "mm/mmjpg/_search";
    private static Map<String, String> mHeader;
    private static int mLabelIndex = 0;
    private static String mLastLabel = "";

    public MmPresenter(LoadContent<MmItemModel> listener) {
        super(MM_URL, MmDbHelper.getInstance(), listener);
        mHeader = new HashMap<>();
        mHeader.put("Referer", "http://www.mmjpg.com/mm/489");
        mHeader.put("User-Agent",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)" +
                        " Chrome/60.0.3112.101 Safari/537.36");
    }

    @Override
    protected JsonParser<MmItemModel> getParser(int type) {
        if (type == Contant.PARSER_TYPE_SUGGEST) {
            return new MmSuggestParser();
        } else if (type == Contant.PARSER_TYPE_MORE) {
            return new MmParser();
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
        return "{\"_source\": [\"mm_tags\", \"mm_title\", \"mm_order\",\"mm_seen_num\", \"mm_fav_num\", \"mm_total_num\", \"mm_first_image_url\"]," +
                "\"suggest\":{\"mm-suggest\":{\"text\":\"" + label + "\",\"completion\":{\"field\":\"mm_suggest\", \"size\": "+ count + "}}}}";
    }

    private String buildLabelJson(String label, int count, int startIndex, String orderField, String order) {
        return "{\n" +
                "    \"query\" : {\n" +
                "        \"constant_score\" : {\n" +
                "            \"filter\" : {\n" +
                "                \"term\" : { \n" +
                "                    \"mm_tags\" : \""+ label +"\"\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "    },\"from\": " + startIndex + ", \"size\":"+ count +", \"sort\":{\""+ orderField +"\": {\"order\":\""+ order + "\"}}\n" +
                "}";
    }

    @Override
    protected String buildLatestJson(int count, int index) {
        return buildLatestJson(count, "mm_order", index, "mm_order", Contant.DESC);
    }

    @Override
    protected String buildMoreJson(int count, int index) {
        return buildMoreJson(count, "mm_order", index, "mm_order", Contant.DESC);
    }

    protected String buildLabelJson(String label, int count, int startIndex) {
        return buildLabelJson(label, count, startIndex,  "mm_order", Contant.DESC);
    }

    public void fetchByFavNum(int count, int index) {
        String json = buildMoreJson(count, "mm_fav_num", index, "mm_fav_num", DESC);

        mElasticHelper.fetchByPost(json);
    }

    public void fetchBySeenNum(int count, int index) {
        String json = buildMoreJson(count, "mm_seen_num", index, "mm_seen_num", DESC);

        mElasticHelper.fetchByPost(json);
    }

    public void fetchByTotalNum(int count, int index) {
        String json = buildMoreJson(count, "mm_total_num", index, "mm_total_num", DESC);
        mElasticHelper.fetchByPost(json);
    }

    @Override
    public void handleData(MmItemModel data) {
        File dir = new File(App.mMmImageBasePath + Utils.getMD5(data.getUrl()));
        String fileName = Utils.getMD5(data.getUrl());
        String path = dir.getAbsolutePath() + File.separator + fileName;
        if (!new File(path).exists()) {
            mImageRunnable = new MmLoaderImageRunnable(App.mAppContext,
                    data.getUrl(), dir, fileName, this, mHeader);
            ThreadPoolHelper.getInstance().start(mImageRunnable);
        }
        data.setPath(path);
    }

    @Override
    protected Class<MmItemModel> getModelClass() {
        return MmItemModel.class;
    }

    @Override
    protected void loadLocalLabelData(int count, String label) {
        if (!mLastLabel.equals(label)){
            mLastLabel = label;
            mLabelIndex = 0;
        }

        MmLabelIndexModel model = SQLite.select()
                .from(MmLabelIndexModel.class)
                .where(MmLabelIndexModel_Table._id.eq(label))
                .querySingle();
        if (model == null || TextUtils.isEmpty(model.getOrders())) {
            mListener.onLoadDone(null);
            return;
        }

        List<MmItemModel> models = new ArrayList<>();
        String[] labels = model.getOrders().split(" ");
        count = Math.min(count, labels.length - mLabelIndex);
        if (count ==0) {
            mListener.onLoadDone(null);
            return;
        }
        for (int i = mLabelIndex; i < mLabelIndex + count; i++) {
            MmItemModel item = SQLite.select()
                    .from(MmItemModel.class)
                    .where(MmItemModel_Table._id.eq(Integer.valueOf(labels[i])))
                    .querySingle();
            if (item != null)
                models.add(item);
        }
        mLabelIndex += count;
        mListener.onLoadDone(models);

    }

    public void saveLabelIndex(List<MmItemModel> models, String label) {
        if (models != null && models.size() > 0) {
            StringBuilder builder = new StringBuilder();
            final MmLabelIndexModel item = new MmLabelIndexModel(label, "");
            for (MmItemModel model : models) {
                builder.append(model.getOrder()).append(" ");
            }

            item.setOrders(new String(builder));

            final MmLabelIndexModel model = SQLite.select()
                    .from(MmLabelIndexModel.class)
                    .where(MmLabelIndexModel_Table._id.eq(label))
                    .querySingle();
            if (model != null) {
                model.setOrders(model.getOrders() + item.getOrders());
                Set<Integer> set = new HashSet<>();
                for (String str: model.getOrders().split(" ")) {
                    set.add(Integer.valueOf(str));
                }
                builder.delete(0, builder.length());
                for (Integer integer : set) {
                    builder.append(integer).append(" ");
                }

                model.setOrders(new String(builder));
            }

            DatabaseDefinition definition = FlowManager.getDatabase(DB.class);
            definition.beginTransactionAsync(new ITransaction() {
                @Override
                public void execute(DatabaseWrapper databaseWrapper) {
                    ModelAdapter<MmLabelIndexModel> adapter = FlowManager.getModelAdapter(MmLabelIndexModel.class);
                    if (model != null)
                        adapter.save(model);
                    else
                        adapter.save(item);
                }
            }).build().execute();
        }
    }
}
