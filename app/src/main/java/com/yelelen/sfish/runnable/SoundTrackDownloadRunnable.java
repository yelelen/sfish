package com.yelelen.sfish.runnable;

import com.yelelen.sfish.contract.DownloadListener;
import com.yelelen.sfish.utils.Utils;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yelelen on 17-10-24.
 */

public class SoundTrackDownloadRunnable implements Runnable {
    private DownloadListener mListener;
    private String mDir;
    private String mFileName;
    private String mUrl;

    public SoundTrackDownloadRunnable(String url, DownloadListener listener, String dir, String fileName) {
        mListener = listener;
        mDir = dir;
        mFileName = fileName;
        mUrl = url;
    }

    @Override
    public void run() {
        if (mListener == null)
            return;

        InputStream is = null;
        File file = null;
        RandomAccessFile raf = null;

        try {
            long downloadLength = 0;
            File dir = new File(mDir);
            if (!dir.exists())
                dir.mkdirs();
            file = new File(dir.getAbsolutePath() + File.separator + mFileName);
            if (file.exists())
                downloadLength = file.length();

            long contentLength = getContentLength(mUrl);
            if (contentLength == 0)
                mListener.onFailed();
            if (contentLength == downloadLength)
                mListener.onSuccess();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .addHeader("RANGE", "bytes=" + downloadLength + "-")
                    .url(mUrl)
                    .build();
            Response response = client.newCall(request).execute();
            if (response != null && response.isSuccessful()) {
                is = response.body().byteStream();
                raf = new RandomAccessFile(file, "rw");
                raf.seek(downloadLength);
                int total = 0;
                int len;
                byte[] bytes = new byte[4096];
                while ((len = is.read(bytes)) != -1) {
                    total += len;
                    raf.write(bytes, 0, len);
                    int progress = (int) ((total + downloadLength) * 100 / contentLength);
                    mListener.onProgress(progress);
                }
                response.close();
                mListener.onSuccess();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Utils.close(is);
                if (raf != null)
                    raf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        mListener.onFailed();
    }

    private long getContentLength(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response != null && response.isSuccessful()) {
                long contentLength = response.body().contentLength();
                response.close();
                return contentLength;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;

    }
}
