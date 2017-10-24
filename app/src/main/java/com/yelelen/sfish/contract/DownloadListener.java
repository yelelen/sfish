package com.yelelen.sfish.contract;

/**
 * Created by yelelen on 17-10-24.
 */

public interface DownloadListener {
    void onProgress(int progress);
    void onSuccess();
    void onFailed();
    void onCanceled();
    void onPaused();
}
