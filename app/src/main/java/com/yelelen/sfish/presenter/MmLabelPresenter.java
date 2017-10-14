package com.yelelen.sfish.presenter;

import com.yelelen.sfish.App;
import com.yelelen.sfish.Model.MmLabelModel;
import com.yelelen.sfish.contract.LoadContent;
import com.yelelen.sfish.helper.BaseDbHelper;
import com.yelelen.sfish.helper.Contant;
import com.yelelen.sfish.helper.ThreadPoolHelper;
import com.yelelen.sfish.parser.JsonParser;
import com.yelelen.sfish.parser.MmLabelParser;
import com.yelelen.sfish.runnable.MmLoaderImageRunnable;
import com.yelelen.sfish.utils.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yelelen on 17-10-6.
 */

public class MmLabelPresenter extends BasePresenter<MmLabelModel> {
    private static Map<String, String> mHeader;


    public MmLabelPresenter(String url, BaseDbHelper<MmLabelModel> dbHelper,
                            LoadContent<MmLabelModel> listener) {
        super(url, dbHelper, listener);
        mHeader = new HashMap<>();
        mHeader.put("Referer", "http://www.mmjpg.com/mm/489");
        mHeader.put("User-Agent",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)" +
                        " Chrome/60.0.3112.101 Safari/537.36");
    }

    @Override
    public void handleData(MmLabelModel data) {
        File dir = new File(App.mMmImageBasePath + Utils.getMD5(data.getCover()));
        String fileName = Utils.getMD5(data.getCover());
        MmLoaderImageRunnable runnable = new MmLoaderImageRunnable(App.mAppContext,
                data.getCover(), dir, fileName, this, mHeader);
        ThreadPoolHelper.getInstance().start(runnable);
        data.setPath(dir.getAbsolutePath() + File.separator + fileName);
    }

    @Override
    protected JsonParser<MmLabelModel> getParser(int type) {
        if (type == Contant.PARSER_TYPE_SUGGEST) {
            return null;
        }

        if (type == Contant.PARSER_TYPE_MORE) {
            return new MmLabelParser();
        }
        return null;
    }

    @Override
    protected Class<MmLabelModel> getModelClass() {
        return MmLabelModel.class;
    }

    @Override
    protected String buildLatestJson(int count, int index) {
        return "{\"query\":{\"range\":{\"mml_order\": {\"gt\":" + index + "}}}," + "\"sort\": {\"mml_order\":{\"order\":\"" +
                Contant.DESC + "\"}}, \"size\":" + count + "}";
    }

    @Override
    protected String buildMoreJson(int count, int index) {
        return "{\"query\":{\"range\":{\"mml_order\": {\"lt\":" + index + "}}}," + "\"sort\": {\"mml_order\":{\"order\":\"" +
                Contant.DESC + "\"}}, \"size\":" + count + "}";
    }
}
