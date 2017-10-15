package com.yelelen.sfish.presenter;

import com.yelelen.sfish.Model.SoundTrackModel;
import com.yelelen.sfish.contract.LoadContent;
import com.yelelen.sfish.helper.BaseDbHelper;
import com.yelelen.sfish.parser.JsonParser;

/**
 * Created by yelelen on 17-10-16.
 */

public class SoundTrackPresenter extends BasePresenter<SoundTrackModel> {
    public SoundTrackPresenter(String url, BaseDbHelper<SoundTrackModel> dbHelper, LoadContent<SoundTrackModel> listener) {
        super(url, dbHelper, listener);
    }

    @Override
    public void handleData(SoundTrackModel data) {

    }

    @Override
    protected Class<SoundTrackModel> getModelClass() {
        return null;
    }

    @Override
    protected JsonParser<SoundTrackModel> getParser(int type) {
        return null;
    }
}
