package com.yelelen.sfish.contract;

/**
 * Created by yelelen on 17-9-14.
 */

public interface DownloadImage {
    void onDownloadDone(String path);
    void onDownloadFailed(String reason);
}
