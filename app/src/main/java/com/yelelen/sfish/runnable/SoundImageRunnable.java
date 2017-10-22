package com.yelelen.sfish.runnable;

import android.content.Context;
import android.util.Log;

import com.yelelen.sfish.R;
import com.yelelen.sfish.contract.DownloadImage;
import com.yelelen.sfish.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yelelen on 17-10-13.
 */

public class SoundImageRunnable implements Runnable {
    private String mUrl;
    private DownloadImage mListener;
    private Context mContext;
    private File mSaveDir;
    private String mFileName;
    private Map<String, String> mHeader;
    private static OkHttpClient mClient = new OkHttpClient();

    private static boolean isFirst = true;

    public SoundImageRunnable(Context context,
                                 String url,
                                 File saveDir,
                                 String saveFileName,
                                 DownloadImage listener,
                                 Map<String, String> header) {
        mListener = listener;
        mUrl = url;
        mContext = context;
        mSaveDir = saveDir;
        mFileName = saveFileName;
        mHeader = header;
    }

    @Override
    public void run() {
        InputStream is = null;
        OutputStream os = null;
        Log.e("TAG", Thread.currentThread().getName());


        if (!mSaveDir.exists())
            mSaveDir.mkdirs();

        File file = new File(mSaveDir, mFileName);
        if (file.exists()) {
            mListener.onDownloadDone(file.getAbsolutePath());
            return;
        }

        try {
            Request.Builder builder = new Request.Builder();
            for (String key : mHeader.keySet()) {
                builder.addHeader(key, mHeader.get(key));
            }
            builder.url(mUrl);
            Request request = builder.build();
            Response response = mClient.newCall(request).execute();
            if (response != null) {
                is = response.body().byteStream();
                os = new FileOutputStream(file);
                byte[] bytes = new byte[4096];
                int len;
                while ((len = is.read(bytes, 0, bytes.length)) != -1) {
                    os.write(bytes, 0, len);
                }
                os.flush();
                mListener.onDownloadDone(file.getAbsolutePath());
            } else {
                mListener.onDownloadFailed(mContext.getString(R.string.error_no_response));
            }
            response.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (isFirst) {
                isFirst = false;
                mListener.onDownloadFailed(mContext.getString(R.string.error_no_response));
            }
        } finally {
            Utils.close(is, os);
        }
    }
}


