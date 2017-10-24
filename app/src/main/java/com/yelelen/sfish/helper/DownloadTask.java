package com.yelelen.sfish.helper;

import android.os.AsyncTask;

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

public class DownloadTask extends AsyncTask<String, Integer, Integer> {
    private static final int TYPE_SUCCESS = 0;
    private static final int TYPE_FAILED = 1;
    private static final int TYPE_PAUSED = 2;
    private static final int TYPE_CANCELED = 3;

    private DownloadListener mListener;
    private boolean isCanceled = false;
    private boolean isPaused = false;
    private int mLastProgress = 0;
    private String mDir;
    private String mFileName;

    public DownloadTask(DownloadListener listener, String dir, String fileName) {
        mListener = listener;
        mDir = dir;
        mFileName = fileName;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        String url = strings[0];
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

            long contentLength = getContentLength(url);
            if (contentLength == 0)
                return TYPE_FAILED;
            if (contentLength == downloadLength)
                return TYPE_SUCCESS;

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .addHeader("RANGE", "bytes=" + downloadLength + "-")
                    .url(url)
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
                    if (isCanceled)
                        return TYPE_CANCELED;
                    else if (isPaused)
                        return TYPE_PAUSED;
                    else {
                        total += len;
                        raf.write(bytes, 0, len);
                        int progress = (int) ((total + downloadLength) * 100 / contentLength);
                        publishProgress(progress);
                    }
                }
                response.close();
                return TYPE_SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Utils.close(is);
                if (raf != null)
                    raf.close();
                if (isCanceled && file != null)
                    file.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        return TYPE_FAILED;
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

    @Override
    protected void onPostExecute(Integer status) {
        if (mListener != null) {
            switch (status) {
                case TYPE_CANCELED:
                    mListener.onCanceled();
                    break;
                case TYPE_FAILED:
                    mListener.onFailed();
                case TYPE_PAUSED:
                    mListener.onPaused();
                case TYPE_SUCCESS:
                    mListener.onSuccess();
                default:
                    break;
            }
        }

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (mListener != null && values[0] > mLastProgress) {
            mListener.onProgress(values[0]);
            mLastProgress = values[0];
        }
    }

    public void pause() {
        isPaused = true;
    }

    public void cancel() {
        isCanceled = true;
    }
}
