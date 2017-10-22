package com.yelelen.sfish.presenter;

import com.yelelen.sfish.Model.SoundTrackModel;
import com.yelelen.sfish.contract.LoadContent;
import com.yelelen.sfish.contract.SoundTrackListener;
import com.yelelen.sfish.helper.Contant;
import com.yelelen.sfish.helper.SoundTrackDbHelper;
import com.yelelen.sfish.parser.JsonParser;
import com.yelelen.sfish.parser.SoundTrackParser;

import java.util.List;

/**
 * Created by yelelen on 17-10-16.
 */

public class SoundTrackPresenter extends BasePresenter<SoundTrackModel>
        implements LoadContent<SoundTrackModel> {
    private static final String SOUND_TRACK_URL = Contant.ES_URL + "audio/sounds/_search";
    private SoundTrackListener mListener;

    public SoundTrackPresenter(SoundTrackListener listener) {
        super(SOUND_TRACK_URL, SoundTrackDbHelper.getInstance());
        setListener(this);
        mListener = listener;
    }

    @Override
    public void handleData(SoundTrackModel data) {

    }

    @Override
    protected Class<SoundTrackModel> getModelClass() {
        return SoundTrackModel.class;
    }

    @Override
    protected JsonParser<SoundTrackModel> getParser(int type) {
        return new SoundTrackParser();
    }

    @Override
    public void onLoadDone(List<SoundTrackModel> t) {
        if (mListener != null)
            mListener.onTrackBack(t);
    }

    @Override
    public void onLoadFailed(String reason) {

    }

    @Override
    protected String buildOneJson(int id) {
        return "{\"query\":{\"term\":{\"as_order\":" + id + "}}}";
    }

    @Override
    protected String buildIdsJson(List<Integer> ids) {
        StringBuilder builder = new StringBuilder();
        for (int id : ids) {
            builder.append(String.valueOf(id)).append(",");
        }
        String result = builder.toString();
        result = result.substring(0, result.lastIndexOf(","));
        return "{\"query\":{\"terms\":{\"as_order\":[" + result + "]}}," + "\"size\":" + ids.size() + "}}}";
    }
}
