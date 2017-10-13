package com.yelelen.sfish.contract;

import java.util.List;

/**
 * Created by yelelen on 17-10-1.
 */

public interface LocalDataListener<T> {
    void onLocalFailed(String reason);
    void onLocalDone(List<T> datas);
}
