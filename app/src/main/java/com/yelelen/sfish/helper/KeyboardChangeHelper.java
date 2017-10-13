package com.yelelen.sfish.helper;

import android.app.Activity;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by yelelen on 17-10-8.
 */

public class KeyboardChangeHelper implements ViewTreeObserver.OnGlobalLayoutListener {
    private static final String TAG = "ListenerHandler";
    private View mContentView;
    private int mOriginHeight;
    private int mPreHeight;
    private KeyBoardListener mKeyBoardListen;

    public interface KeyBoardListener {
        /**
         * call back
         *
         * @param isShow         true is show else hidden
         * @param keyboardHeight keyboard height
         */
        void onKeyboardChange(boolean isShow, int keyboardHeight);
    }

    public void setKeyBoardListener(KeyBoardListener keyBoardListen) {
        this.mKeyBoardListen = keyBoardListen;
    }

    public KeyboardChangeHelper(Activity contextObj) {
        if (contextObj == null) {
            Log.i(TAG, "contextObj is null");
            return;
        }
        mContentView = findContentView(contextObj);
        if (mContentView != null) {
            addContentTreeObserver();
        }
    }

    private View findContentView(Activity contextObj) {
        return contextObj.findViewById(android.R.id.content);
    }

    private void addContentTreeObserver() {
        mContentView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
            Rect r = new Rect();
        //获取当前界面可视部分
        mContentView.getWindowVisibleDisplayFrame(r);
        //获取屏幕的高度
        int screenHeight = mContentView.getRootView().getHeight();
        //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
        int keyboardHeight = screenHeight - r.bottom;
//        Log.d("Keyboard Size", "Size: " + keyboardHeight);


//        int currHeight = mContentView.getHeight();
//        if (currHeight == 0) {
//            Log.i(TAG, "currHeight is 0");
//            return;
//        }
//        boolean hasChange = false;
//        if (mPreHeight == 0) {
//            mPreHeight = currHeight;
//            mOriginHeight = currHeight;
//        } else {
//            if (mPreHeight != currHeight) {
//                hasChange = true;
//                mPreHeight = currHeight;
//            } else {
//                hasChange = false;
//            }
//        }
//        if (hasChange) {
//            boolean isShow;
//            int keyboardHeight = 0;
//            if (mOriginHeight == currHeight) {
//                //hidden
//                isShow = false;
//            } else {
//                //show
//                keyboardHeight = mOriginHeight - currHeight;
//                isShow = true;
//            }

        boolean isShow = keyboardHeight > 200;

        if (mKeyBoardListen != null) {
            mKeyBoardListen.onKeyboardChange(isShow, keyboardHeight);
        }


    }

    public void destroy() {
        if (mContentView != null) {
            mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    }
}
