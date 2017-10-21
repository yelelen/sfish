package com.yelelen.sfish.helper;

import com.yelelen.sfish.Model.SoundTrackModel;

/**
 * Created by yelelen on 17-10-21.
 */

public class SoundTrackDbHelper extends BaseDbHelper<SoundTrackModel> {
    public static final SoundTrackDbHelper instance;

    static {
        instance = new SoundTrackDbHelper();
    }

    public static SoundTrackDbHelper getInstance() {
        return instance;
    }

}
