package com.yelelen.sfish.contract;

import java.util.List;

/**
 * Created by yelelen on 17-10-1.
 */

public interface DbDataListener<T> {
    void onSave(List<T> datas);
    void onDelete(List<T> datas);
    void onLocalDone(List<T> datas);
    void onLocalFailed(String reason);
}
