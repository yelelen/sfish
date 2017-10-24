package com.yelelen.sfish.contract;

/**
 * Created by yelelen on 17-10-24.
 */

public interface SoundDownloadListener {
    void onProgress(int progress);
    void onFailed();
    void onSuccess();
}
