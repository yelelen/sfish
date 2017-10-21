package com.yelelen.sfish.contract;

import com.yelelen.sfish.Model.SoundTrackModel;

import java.util.List;

/**
 * Created by yelelen on 17-10-21.
 */

public interface SoundTrackListener {
    void onTrackBack(List<SoundTrackModel> models);
}
