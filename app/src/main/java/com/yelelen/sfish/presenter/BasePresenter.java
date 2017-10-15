package com.yelelen.sfish.presenter;

import android.text.TextUtils;

import com.yelelen.sfish.App;
import com.yelelen.sfish.contract.DbDataListener;
import com.yelelen.sfish.contract.DownloadImage;
import com.yelelen.sfish.contract.LoadContent;
import com.yelelen.sfish.contract.LocalDataListener;
import com.yelelen.sfish.contract.NetDataListener;
import com.yelelen.sfish.helper.BaseDbHelper;
import com.yelelen.sfish.helper.Contant;
import com.yelelen.sfish.helper.ElasticHelper;
import com.yelelen.sfish.helper.ThreadPoolHelper;
import com.yelelen.sfish.parser.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yelelen on 17-9-14.
 */

public abstract class BasePresenter<T> implements NetDataListener<T>,
        LocalDataListener<T>, DbDataListener<T>, DownloadImage {
    private BaseDbHelper<T> mDbHelper;
    protected LoadContent<T> mListener;
    private int mCount;
    private ElasticHelper<T> mElasticHelper;
    private boolean isFrist = true;
    private static boolean isLabelDataEnd = false;
    private int mLabelStartIndex = 0;
    private static String mLastLabel = " ";
    private static String mCurLabel = " ";

    private static final int REFRESH = 100;
    private static final int LABEL = 101;
    private static final int SUGGEST = 102;
    private static int mCurDataType = REFRESH;


    public BasePresenter(String url, BaseDbHelper<T> dbHelper, LoadContent<T> listener) {
        mDbHelper = dbHelper;
        mListener = listener;
        mElasticHelper = new ElasticHelper<>(url, this);
        registerDbObserver();
    }

    public void registerDbObserver() {
        mDbHelper.registerObserver(getModelClass(), this);
    }

    public void unregisterDbObserver() {
        mDbHelper.unregisterObserver(getModelClass(), this);
    }

    public void loadData(final int count) {
        setDataType(REFRESH);
        mCount = count;
        ThreadPoolHelper.getInstance().start(new Runnable() {
            @Override
            public void run() {
                loadMoreFromNet(count, mDbHelper.LAST_INDEX);
            }
        });
    }

    private void setDataType(int type) {
        mCurDataType = type;
    }

    public void setLastIndex(int index) {
        mDbHelper.LAST_INDEX = index;
    }

    public int getLastIndex() {
        return mDbHelper.LAST_INDEX;
    }

    public void setLabelStartIndex(int index) {
        mLabelStartIndex = index;
    }

    public void loadLocalData(final int count) {
        setDataType(REFRESH);
        mCount = count;
        new Thread(new Runnable() {
            @Override
            public void run() {
                mDbHelper.getFromLocal(count, false);
            }
        }).start();
    }

    public void loadLatestData(final int count) {
        setDataType(REFRESH);
        ThreadPoolHelper.getInstance().start(new Runnable() {
            @Override
            public void run() {
                loadLatestFromNet(count, mDbHelper.getMaxOrder());
            }
        });
    }

    public void loadLabelData(final int count, final String label) {
        setDataType(LABEL);
        mCurLabel = label;
        mCount = count;
        if (!mLastLabel.equals(label)) {
            mLabelStartIndex = 0;
            mLastLabel = label;
            isLabelDataEnd = false;
        }
        if (isLabelDataEnd) {
            mListener.onLoadDone(null);
        } else {
            ThreadPoolHelper.getInstance().start(new Runnable() {
                @Override
                public void run() {
                    fetchByLabel(label, count, mLabelStartIndex);
                }
            });
        }

    }

    public void loadSuggestData(final int count, final String s) {
        setDataType(SUGGEST);
        if (!TextUtils.isEmpty(s) && count > 0) {
            ThreadPoolHelper.getInstance().start(new Runnable() {
                @Override
                public void run() {
                    fetchBySuggest(count, s);
                }
            });
        }
    }

    @Override
    public void onSave(List<T> datas) {
    }

    @Override
    public void onDelete(List<T> datas) {

    }

    @Override
    public void onLocalFailed(String reason) {
        loadMoreFromNet(mCount, mDbHelper.LAST_INDEX);
    }

    @Override
    public void onLocalDone(List<T> datas) {
        if (datas != null && datas.size() > 0)
            mListener.onLoadDone(datas);
        else {
            if (App.getInstance().isNetworkConnected()) {
                loadMoreFromNet(mCount, mDbHelper.LAST_INDEX);
            } else {
                mListener.onLoadFailed(null);
            }
        }
    }

    public void loadMoreFromNet(int count, int lastOrder) {
        if (count > 0 && lastOrder > 0) {
            fetchByOrder(count, lastOrder);
        }
    }

    public void loadLatestFromNet(int count, int maxOrder) {
        if (count > 0 && maxOrder >= 0) {
            fetchLatest(count, maxOrder);
        }
    }


    @Override
    public void onNetFailed(String reason) {
        int type = mCurDataType;
        if (type == REFRESH)
            loadLocalData(mCount);
        if (type == LABEL)
            loadLocalLabelData(mCount, mCurLabel);
        if (type == SUGGEST) {
            mListener.onLoadDone(null);
        }
    }


    @Override
    public void onNetDone(List<T> datas) {
        if (datas != null && datas.size() > 0) {
            List<T> models = new ArrayList<>();
            for (T data : datas) {
                handleData(data);
                models.add(data);
            }
            mDbHelper.save(getModelClass(), models);
            int type = mCurDataType;
            if (type == REFRESH)
                changLastIndex(models);
            if (type == LABEL) {
                if (datas.size() < mCount) {
                    isLabelDataEnd = true;
                }
                mLabelStartIndex += datas.size();
            }
            if (type == SUGGEST) {
                // nothing to do
            }

            mListener.onLoadDone(models);
        } else {
            mListener.onLoadDone(null);
        }
    }

    private void changLastIndex(List<T> models) {
        if (isFrist) {
            isFrist = false;
            mDbHelper.LAST_INDEX = mDbHelper.getMaxOrder() - models.size() + 1;
        } else
            mDbHelper.LAST_INDEX -= models.size();
    }

    @Override
    public void onDownloadDone(String path) {

    }

    @Override
    public void onDownloadFailed(String reason) {

    }

    public int getMaxOrder() {
        return mDbHelper.getMaxOrder();
    }

    protected void fetchLatest(int count, int index) {
        String json = buildLatestJson(count, index);
        mElasticHelper.setParser(getParser(Contant.PARSER_TYPE_MORE));
        mElasticHelper.fetchByPost(json);
    }

    protected void fetchByOrder(int count, int index) {
        String json = buildMoreJson(count, index);
        mElasticHelper.setParser(getParser(Contant.PARSER_TYPE_MORE));
        mElasticHelper.fetchByPost(json);
    }

    protected void fetchByLabel(String label, int count, int startIndex) {
        String json = buildLabelJson(label, count, startIndex);
        mElasticHelper.setParser(getParser(Contant.PARSER_TYPE_MORE));
        mElasticHelper.fetchByPost(json);
    }

    protected void fetchBySuggest(int count, String s) {
        String json = buildSuggestJson(count, s);
        mElasticHelper.setParser(getParser(Contant.PARSER_TYPE_SUGGEST));
        mElasticHelper.fetchByPost(json);
    }

    public abstract void handleData(T data);

    protected abstract Class<T> getModelClass();

    protected String buildSuggestJson(int count, String s) {
        return null;
    }

    protected String buildLatestJson(int count, int index) {
        return null;
    }

    protected String buildMoreJson(int count, int index) {
        return null;
    }

    protected abstract JsonParser<T> getParser(int type);

    protected void loadLocalLabelData(int count, String label) {
        // DO nothing, if need, the child must override the method
    }


    protected String buildLabelJson(String label, int count, int startIndex) {
        // DO nothing, if need, the child must override the method
        return null;
    }


}
