package com.yelelen.sfish.contract;

/**
 * Created by yelelen on 17-9-12.
 */


public interface MatrixImageViewListener {
        interface MatrixImageViewCallback {
                void onClick();
                void onLongClick(int position);
        }
}