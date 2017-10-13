package com.yelelen.sfish.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;

import com.yelelen.sfish.contract.OnChildMovingListener;

/**
 * Created by yelelen on 17-9-12.
 */

public class MmViewPager extends ViewPager implements OnChildMovingListener {

    public MmViewPager(Context context) {
        super(context);
    }

    /**
     * 当前子控件是否处理拖动状态
     */
    private boolean mChildIsBeingDragged = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (mChildIsBeingDragged)
            return false;

        return super.onInterceptTouchEvent(e);
    }

    @Override
    public void startDrag() {
        mChildIsBeingDragged = true;
    }

    @Override
    public void stopDrag() {
        mChildIsBeingDragged = false;
    }

}


