package com.yelelen.sfish.contract;

import com.yelelen.sfish.Model.SoundTrackModel;

/**
 * Created by yelelen on 17-10-21.
 */

public interface SoundTrackItemListener {
    void onTrackClick(SoundTrackModel data, int position);
    void onLoadMore();
}
