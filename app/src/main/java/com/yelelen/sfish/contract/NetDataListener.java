package com.yelelen.sfish.contract;

import java.util.List;

/**
 * Created by yelelen on 17-10-1.
 */

public interface NetDataListener<T> {
    void onNetFailed(String reason);
    void onNetDone(List<T> datas);
}
