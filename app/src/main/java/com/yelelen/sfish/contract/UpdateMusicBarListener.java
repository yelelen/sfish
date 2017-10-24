package com.yelelen.sfish.contract;

import com.yelelen.sfish.Model.SoundTrackModel;

/**
 * Created by yelelen on 17-10-24.
 */

public interface UpdateMusicBarListener {
    void onUpdateMusicBar(boolean isPause, SoundTrackModel model);
}
