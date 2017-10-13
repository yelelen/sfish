package com.yelelen.sfish.adapter;

/**
 * Created by yelelen on 17-9-4.
 */

public interface AdapterCallback<T> {
    void update(T data, RecyclerAdapter.BaseViewHolder<T> holder);
}

