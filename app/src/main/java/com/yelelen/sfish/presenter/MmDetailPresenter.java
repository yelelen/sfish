package com.yelelen.sfish.presenter;

import android.content.Context;

import com.yelelen.sfish.App;
import com.yelelen.sfish.R;
import com.yelelen.sfish.activity.MmDetailActivity;
import com.yelelen.sfish.contract.DownloadImage;
import com.yelelen.sfish.helper.Contant;
import com.yelelen.sfish.helper.ThreadPoolHelper;
import com.yelelen.sfish.runnable.MmLoaderImageRunnable;
import com.yelelen.sfish.utils.Utils;

import java.io.File;
import java.util.List;

/**
 * Created by yelelen on 17-9-5.
 */

public class MmDetailPresenter {
    private static DownloadImage mListener;
    private ThreadPoolHelper mPoolHelper;
    private Context mContext;
    private boolean isFirstLoadCache = true;
    private boolean isFirstNetworkNone = true;
    private boolean isFirstNetwork = true;

    public MmDetailPresenter(Context context) {
        mContext = context;
        mPoolHelper = ThreadPoolHelper.getInstance();
    }

    public void setListener(DownloadImage listener) {
        mListener = listener;
    }

    public void loadFromNet(String url, String savePath) {
        if (App.getInstance().isNetworkConnected()) {
            if (App.getInstance().isWifiAvailable()) {
                // nothing
            } else {
                if (isFirstNetwork) {
                    isFirstNetwork = false;
                    ((MmDetailActivity) mContext).getHandler().sendEmptyMessage(Contant.MSG_NETWORK_MOBILE);
                }
            }
            String fileName = Utils.getMD5(url);
            mPoolHelper.start(new MmLoaderImageRunnable(mContext, url, new File(savePath), fileName, mListener,
                    App.mHeader));
        } else {
            if(isFirstNetworkNone) {
                ((MmDetailActivity) mContext).getHandler().sendEmptyMessage(Contant.MSG_NETWORK_UNAVAILABLE);
                isFirstNetworkNone = false;
            }
        }
    }

    public void startLoadForPager(final List<String> urls, final String path) {
        for (int i = 0; i < urls.size(); i++) {
            if (!loadFromCache(urls.get(i), path))
                loadFromNet(urls.get(i), path);
        }
    }

    public boolean loadFromCache(String url, String path) {
        File cacheDir = new File(path);
        if (!cacheDir.exists())
            Utils.createDirectory(path);

        if (cacheDir.exists() && cacheDir.isDirectory()) {
            File file = new File(cacheDir.getAbsolutePath() + File.separator + Utils.getMD5(url));
            if (file.exists()) {
                mListener.onDownloadDone(file.getAbsolutePath());
                return true;
            } else {
                if (isFirstLoadCache) {
                    isFirstLoadCache = false;
                    mListener.onDownloadFailed(mContext.getString(R.string.error_no_cache_image));
                }
                return false;
            }
        }
        return false;
    }

}
