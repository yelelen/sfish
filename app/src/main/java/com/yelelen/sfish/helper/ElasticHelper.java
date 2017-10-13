package com.yelelen.sfish.helper;

import com.yelelen.sfish.parser.JsonParser;
import com.yelelen.sfish.contract.NetDataListener;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by yelelen on 17-9-15.
 */

public class ElasticHelper<T> implements Callback {

    private String mUrl;
    private static OkHttpClient mClient;
    private JsonParser<T> mParser;
    private NetDataListener<T> mDataListener;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public ElasticHelper(String url, NetDataListener<T> listener) {
        mDataListener = listener;
        mClient = new OkHttpClient();
        mUrl = url;
    }

    public void setParser(JsonParser<T> parser) {
        mParser = parser;
    }

    public void fetchByGet() {
        Request request = new Request.Builder().url(mUrl).build();
        mClient.newCall(request).enqueue(this);
    }

    public void fetchByPost(String json) {
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(mUrl).post(requestBody).build();
        mClient.newCall(request).enqueue(this);
    }


    @Override
    public void onFailure(Call call, IOException e) {
        mDataListener.onNetFailed("网络异常");
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (response.code() == 200) {
            String body = response.body().string();
            if (mParser != null) {
                List<T> ts = mParser.parse(body);
                mDataListener.onNetDone(ts);
            } else
                mDataListener.onNetDone(null);
        } else {
            mDataListener.onNetFailed("服务器内部异常");
        }
    }
}
