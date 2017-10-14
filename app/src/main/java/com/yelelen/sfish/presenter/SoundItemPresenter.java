package com.yelelen.sfish.presenter;

import com.yelelen.sfish.App;
import com.yelelen.sfish.Model.SoundItemModel;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yelelen on 17-10-13.
 */

public class SoundItemPresenter extends BasePresenter<SoundItemModel> {
    private static final String SOUND_ALBUM_URL = Contant.ES_URL + "audio/album/_search";
    public static Map<String, String> mHeader;


    public SoundItemPresenter(LoadContent<SoundItemModel> listener) {
        super(SOUND_ALBUM_URL, SoundDbHelper.getInstance(), listener);
        mHeader = new HashMap<>();
        mHeader.put("Referer", "http://www.ximalaya.com");
        mHeader.put("User-Agent",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)" +
                        " Chrome/60.0.3112.101 Safari/537.36");
    }

    @Override
    public void handleData(SoundItemModel data) {
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

    @Override
    protected Class<SoundItemModel> getModelClass() {
        return SoundItemModel.class;
    }

    @Override
    protected JsonParser<SoundItemModel> getParser(int type) {
        if (type == Contant.PARSER_TYPE_SUGGEST) {
            return new SoundSuggestParser();
        } else if (type == Contant.PARSER_TYPE_MORE) {
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
}
