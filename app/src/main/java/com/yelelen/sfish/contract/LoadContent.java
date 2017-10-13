package com.yelelen.sfish.contract;

import java.util.List;

/**
 * Created by yelelen on 17-9-5.
 */

public interface LoadContent<T> {
    void onLoadDone(List<T> t);
    void onLoadFailed(String reason);
}
