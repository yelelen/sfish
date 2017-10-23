package com.yelelen.sfish.contract;

import android.media.MediaPlayer;

/**
 * Created by yelelen on 17-10-23.
 */

public interface MediaPlayerListener {
    void onBufferingUpdate(MediaPlayer mp, int percent);
    void onPlayerPlay();
    void onPlayerPause();
    void onCompletion();
    void onSeekDone();
}
