package com.yelelen.sfish;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;

import com.iflytek.cloud.SpeechUtility;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.yelelen.sfish.contract.NetworkListener;
import com.yelelen.sfish.receiver.NetworkStateReceiver;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by yelelen on 17-9-11.
 */

public class App extends Application {
    public static final String TAG = Application.class.getSimpleName();
    public static String mMmImageBasePath;
    public static String mSoundAlbumBasePath;
    public static String mSoundZhuboBasePath;
    public static Context mAppContext;
    public static final App instance;
    public boolean isWifiEnabled = false;
    public boolean isMobileDataEnabled = false;
    public boolean isNetworkConnected = false;
    public boolean isWifiAvailable = false;
    public boolean isMobileDataAvailable = false;
    private boolean isFirstShowDlg = false;

    private static Set<NetworkListener> mNetworkListeners;
    public NetworkStateReceiver receiver;

    public static void registerNetworkListener(NetworkListener listener) {
        if (listener != null) {
            mNetworkListeners.add(listener);
        }
    }

    public static void unregisterNetworkListener(NetworkListener listener) {
        if (listener != null) {
            mNetworkListeners.remove(listener);
        }
    }

    public boolean isWifiAvailable() {
        return isWifiAvailable;
    }

    public void setWifiAvailable(boolean wifiAvailable) {
        isWifiAvailable = wifiAvailable;
    }

    public boolean isMobileDataAvailable() {
        return isMobileDataAvailable;
    }

    public void setMobileDataAvailable(boolean mobilDataAvailable) {
        isMobileDataAvailable = mobilDataAvailable;
    }

    public boolean isWifiEnabled() {
        return isWifiEnabled;
    }

    public void setWifiEnabled(boolean wifiEnabled) {
        isWifiEnabled = wifiEnabled;
    }

    public boolean isMobileEnabled() {
        return isMobileDataEnabled;
    }

    public void setMobileEnabled(boolean mobieDataEnabled) {
        isMobileDataEnabled = mobieDataEnabled;
    }

    public boolean isNetworkConnected() {
        return isNetworkConnected;
    }

    public void setNetworkConnected(boolean networkConnected) {
        isNetworkConnected = networkConnected;
        for (NetworkListener listener : mNetworkListeners) {
            listener.refresh();
        }
    }

    static {
        instance = new App();
    }

    public static App getInstance() {
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mMmImageBasePath = getCacheDir() + "/mm/";
        mSoundAlbumBasePath = getCacheDir() + "/sound/image/";
        mSoundZhuboBasePath = getCacheDir() + "/sound/zhubo/";
        mAppContext = this;

        // 初始化数据库
        FlowManager.init(new FlowConfig.Builder(this)
                .openDatabasesOnInit(true) // 数据库初始化的时候就开始打开
                .build());

        receiver = new NetworkStateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        registerReceiver(receiver, intentFilter);

        mNetworkListeners = new HashSet<>();

        // 科大讯飞语音
        // 应用程序入口处调用，避免手机内存过小，杀死后台进程后通过历史intent进入Activity造成SpeechUtility对象为null
        // 如在Application中调用初始化，需要在Mainifest中注册该Applicaiton
        // 注意：此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN+"=true"
        // 参数间使用半角“,”分隔。
        // 设置你申请的应用appid,请勿在'='与appid之间添加空格及空转义符

        // 注意： appid 必须和下载的SDK保持一致，否则会出现10407错误

        SpeechUtility.createUtility(this, "appid=59cbc577");

        // 以下语句用于设置日志开关（默认开启），设置成false时关闭语音云SDK日志打印
        // Setting.setShowLog(false);


    }

    @Override
    public void onTerminate() {
        unregisterReceiver(receiver);
        super.onTerminate();
    }

    //    Application 的生命周期
//    @Override
//    public void onCreate() {
//        // 程序创建的时候执行
//        Log.d(TAG, "onCreate");
//        super.onCreate();
//    }
//    @Override
//    public void onTerminate() {
//        // 程序终止的时候执行
//        Log.d(TAG, "onTerminate");
//        super.onTerminate();
//    }
//    @Override
//    public void onLowMemory() {
//        // 低内存的时候执行
//        Log.d(TAG, "onLowMemory");
//        super.onLowMemory();
//    }
//    @Override
//    public void onTrimMemory(int level) {
//        // 程序在内存清理的时候执行
//        Log.d(TAG, "onTrimMemory");
//        super.onTrimMemory(level);
//    }
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        Log.d(TAG, "onConfigurationChanged");
//        super.onConfigurationChanged(newConfig);
//    }
}
