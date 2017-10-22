package com.yelelen.sfish.presenter;

import com.yelelen.sfish.App;
import com.yelelen.sfish.Model.SoundZhuboModel;
import com.yelelen.sfish.contract.LoadContent;
import com.yelelen.sfish.contract.SoundZhuboListener;
import com.yelelen.sfish.helper.Contant;
import com.yelelen.sfish.helper.SoundZhuboDbHelper;
import com.yelelen.sfish.helper.ThreadPoolHelper;
import com.yelelen.sfish.parser.JsonParser;
import com.yelelen.sfish.parser.SoundZhuboParser;
import com.yelelen.sfish.runnable.SoundImageRunnable;
import com.yelelen.sfish.utils.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yelelen on 17-10-22.
 */

public class SoundZhuboPresenter extends BasePresenter<SoundZhuboModel>
        implements LoadContent<SoundZhuboModel> {
    public static Map<String, String> mHeader;
    private SoundZhuboListener mListener;
    private static final String SOUND_ZHUBO_URL = Contant.ES_URL + "audio/zhubos/_search";

    public SoundZhuboPresenter(SoundZhuboListener listener) {
        super(SOUND_ZHUBO_URL, SoundZhuboDbHelper.getInstance());
        mListener = listener;
        setListener(this);
        mHeader = new HashMap<>();
        mHeader.put("Referer", "http://www.ximalaya.com");
        mHeader.put("User-Agent",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)" +
                        " Chrome/60.0.3112.101 Safari/537.36");
    }

    @Override
    public void handleData(SoundZhuboModel data) {
        File dir = new File(App.mSoundZhuboBasePath);
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
    protected Class<SoundZhuboModel> getModelClass() {
        return SoundZhuboModel.class;
    }

    @Override
    protected JsonParser<SoundZhuboModel> getParser(int type) {
        return new SoundZhuboParser();
    }

    @Override
    public void onLoadDone(List<SoundZhuboModel> t) {
        if (mListener != null && t != null && t.size() > 0)
            mListener.onZhuboBack(t.get(0));
    }

    @Override
    public void onLoadFailed(String reason) {

    }
    @Override
    protected String buildOneJson(int id) {
        return "{\"query\":{\"term\":{\"az_order\":" + id + "}}}";
    }
}
