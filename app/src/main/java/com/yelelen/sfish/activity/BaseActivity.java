package com.yelelen.sfish.activity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.view.View;
import android.view.Window;

import com.yelelen.sfish.App;
import com.yelelen.sfish.R;
import com.yelelen.sfish.contract.NetworkListener;
import com.yelelen.sfish.frags.BaseFragment;
import com.yelelen.sfish.helper.Contant;
import com.yelelen.sfish.utils.SnackbarUtil;
import com.yelelen.sfish.view.PlaceHolderView;

import java.util.List;

/**
 * Created by yelelen on 17-9-4.
 */

public abstract class BaseActivity extends AppCompatActivity implements NetworkListener {

    protected PlaceHolderView mPlaceHolderView;
    private static boolean isOnStop = false;

    @SuppressLint("HandlerLeak")
    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case Contant.MSG_NETWORK_MOBILE:
                    SnackbarUtil.showNetPrompt(BaseActivity.this, getString(R.string.network_mobile));
                    break;
                case Contant.MSG_NETWORK_UNAVAILABLE:
                    SnackbarUtil.showNetPrompt(BaseActivity.this, getString(R.string.network_unavailable));
                    break;
                case Contant.MSG_MM_SAVE_IMAGE:
                    String s1 = (String) msg.obj;
                    SnackbarUtil.showSaveImagePrompt(BaseActivity.this, s1);
                    break;
                case Contant.MSG_MM_SAVE_IMAGE_FAILED:
                    String s2 = (String) msg.obj;
                    SnackbarUtil.showSaveImagePrompt(BaseActivity.this, s2);
                    break;
                case Contant.MSG_MM_DOWNLOAD_IMAGE_FAILED:
                    String s3 = (String) msg.obj;
                    SnackbarUtil.showDownloadImagePrompt(BaseActivity.this, s3);
                    break;
            }

        }
    };


    @Override
    public void refresh() {
        if (App.getInstance().isNetworkConnected()) {
            if (App.getInstance().isMobileDataAvailable && !App.getInstance().isWifiAvailable) {
                mHandler.sendEmptyMessage(Contant.MSG_NETWORK_MOBILE);
            }
        } else {
            mHandler.sendEmptyMessage(Contant.MSG_NETWORK_UNAVAILABLE);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.registerNetworkListener(this);

        if (initArgs()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            }
            int layoutId = getContentLayoutId();
            setContentView(layoutId);

            initView();

            initData();

            initListener();
        } else {
            finish();
        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }
    }

    protected void initListener() {

    }

    protected void initData() {

    }

    public void showActivity(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivity(intent);
        }
    }

    public void showActivity(Class<?> activityClass, final String tag, Bundle data) {
        Intent intent = new Intent(this, activityClass);
        intent.putExtra(tag, data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivity(intent);
        }
    }

    protected void initView() {
        this.setTransitionsDefault();
    }

    protected void setTransitionsDefault() {
        this.setTransitions(null, null, null);
    }

    protected void setTransitions(Transition exit, Transition enter, Transition reenter) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Transition explode = TransitionInflater.from(this)
//                    .inflateTransition(R.transition.explode);
//            Transition slide = TransitionInflater.from(this)
//                    .inflateTransition(R.transition.slide_end);
//            Transition fade = TransitionInflater.from(this)
//                    .inflateTransition(R.transition.fade);
            getWindow().setExitTransition(exit);
            getWindow().setEnterTransition(enter);
            getWindow().setReenterTransition(reenter);
        }
    }


    /**
     * 初始化相关参数
     * <p>
     * //     * @param bundle 参数Bundle
     *
     * @return 如果参数正确返回True，错误返回False
     */
    protected boolean initArgs() {
        return true;
    }

    protected abstract int getContentLayoutId();

    @Override
    public boolean onSupportNavigateUp() {
        // 当点击界面导航返回时，Finish当前界面
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        // 得到当前Activity下的所有Fragment
        @SuppressLint("RestrictedApi")
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        // 判断是否为空
        if (fragments != null && fragments.size() > 0) {
            for (Fragment fragment : fragments) {
                // 判断是否为我们能够处理的Fragment类型
                if (fragment instanceof BaseFragment) {
                    // 判断是否拦截了返回按钮
                    if (((BaseFragment) fragment).onBackPressed()) {
                        // 如果有直接Return
                        return;
                    }
                }
            }
        }

        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

}
