package com.yelelen.sfish.frags;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yelelen.sfish.App;
import com.yelelen.sfish.R;
import com.yelelen.sfish.contract.NetworkListener;
import com.yelelen.sfish.helper.Contant;
import com.yelelen.sfish.utils.SnackbarUtil;
import com.yelelen.sfish.view.PlaceHolderView;


/**
 * Created by yelelen on 17-9-4.
 */

public abstract class BaseFragment extends Fragment implements NetworkListener{
    protected View mRoot;
    protected PlaceHolderView mPlaceHolderView;
    // 标示是否第一次初始化数据
    protected boolean mIsFirstInitData = true;
    private boolean isOnStop = false;

    @SuppressLint("HandlerLeak")
    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case Contant.MSG_NETWORK_MOBILE:
                    if (!isOnStop)
                        SnackbarUtil.showNetPrompt(getActivity(), getString(R.string.network_mobile));

                    break;
                case Contant.MSG_NETWORK_UNAVAILABLE:
                    if (!isOnStop) {
                        SnackbarUtil.showNetPrompt(getActivity(), getString(R.string.network_unavailable));
                    }
                    break;
                case Contant.MSG_MM_REFRESH_NO_MORE:
                    SnackbarUtil.showRefreshPrompt(getActivity(), getString(R.string.label_mm_refresh_no_more));
                    break;
                case Contant.MSG_MM_LOAD_NO_MORE:
                    SnackbarUtil.showRefreshPrompt(getActivity(), getString(R.string.label_mm_load_no_more));
                    break;
            }

        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // 初始化参数
        initArgs(getArguments());
        App.registerNetworkListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        isOnStop = false;
    }

    @Override
    public void onStop() {
        isOnStop = true;
        super.onStop();
    }

    @Override
    public void refresh() {
        if (App.getInstance().isNetworkConnected()) {
            if (App.getInstance().isMobileDataAvailable) {
                mHandler.sendEmptyMessage(Contant.MSG_NETWORK_MOBILE);
            }
        } else {
            mHandler.sendEmptyMessage(Contant.MSG_NETWORK_UNAVAILABLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRoot == null) {
            int layId = getContentLayoutId();
            // 初始化当前的跟布局，但是不在创建时就添加到container里边
            View root = inflater.inflate(layId, container, false);
            initView(root);
            mRoot = root;
        } else {
            if (mRoot.getParent() != null) {
                // 把当前Root从其父控件中移除
                ((ViewGroup) mRoot.getParent()).removeView(mRoot);
            }
        }

        return mRoot;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mIsFirstInitData) {
            // 触发一次以后就不会触发
            mIsFirstInitData = false;
            // 触发
            onFirstInit();
        }

        // 当View创建完成后初始化数据
        initData();
    }

    /**
     * 初始化相关参数
     */
    protected void initArgs(Bundle bundle) {

    }

    /**
     * 得到当前界面的资源文件Id
     *
     * @return 资源文件Id
     */
    @LayoutRes
    protected abstract int getContentLayoutId();

    /**
     * 初始化控件
     */
    protected void initView(View root) {

    }

    /**
     * 初始化数据
     */
    protected void initData() {

    }

    /**
     * 当首次初始化数据的时候会调用的方法
     */
    protected void onFirstInit() {

    }

    /**
     * 返回按键触发时调用
     *
     * @return 返回True代表我已处理返回逻辑，Activity不用自己finish。
     * 返回False代表我没有处理逻辑，Activity自己走自己的逻辑
     */
    public boolean onBackPressed() {
        return false;
    }


    /**
     * 设置占位布局
     *
     * @param placeHolderView 继承了占位布局规范的View
     */
    public void setPlaceHolderView(PlaceHolderView placeHolderView) {
        this.mPlaceHolderView = placeHolderView;
    }

    @Override
    public void onDestroyView() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }


}
