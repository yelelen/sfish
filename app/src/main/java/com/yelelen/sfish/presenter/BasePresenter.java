package com.yelelen.sfish.presenter;

import android.text.TextUtils;

import com.yelelen.sfish.App;
import com.yelelen.sfish.Model.SoundItemModel;
import com.yelelen.sfish.contract.DbDataListener;
import com.yelelen.sfish.contract.DownloadImage;
import com.yelelen.sfish.contract.LoadContent;
import com.yelelen.sfish.contract.LocalDataListener;
import com.yelelen.sfish.contract.NetDataListener;
import com.yelelen.sfish.helper.BaseDbHelper;
import com.yelelen.sfish.helper.Contant;
import com.yelelen.sfish.helper.ElasticHelper;
import com.yelelen.sfish.parser.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yelelen on 17-9-14.
 */

public abstract class BasePresenter<T> implements NetDataListener<T>,
        LocalDataListener<T>, DbDataListener<T>, DownloadImage {
    private BaseDbHelper<T> mDbHelper;
    private LoadContent<T> mListener;
    private int mCount;
    private ElasticHelper<T> mElasticHelper;
    private boolean isFirst = true;
    private  boolean isLabelDataEnd = false;
    private int mLabelStartIndex = 0;
    private  String mLastLabel = " ";
    private  String mCurLabel = " ";

    private static final int REFRESH = 100;
    private static final int LABEL = 101;
    private static final int SUGGEST = 102;
    private static final int ONE = 103;
    private static final int IDS = 104;
    private int mCurDataType = REFRESH;

    private int mCurOneOrder;
    private List<Integer> mIds;


    public BasePresenter(String url, BaseDbHelper<T> dbHelper, LoadContent<T> listener) {
        mDbHelper = dbHelper;
        mListener = listener;
        mElasticHelper = new ElasticHelper<>(url, this);
        registerDbObserver();
    }

    public BasePresenter(String url, BaseDbHelper<T> dbHelper) {
        this(url, dbHelper, null);
    }

    public void setListener(LoadContent<T> listener) {
        mListener = listener;
    }

    public BaseDbHelper<T> getDbHelper() {
        return mDbHelper;
    }

    public void registerDbObserver() {
        mDbHelper.registerObserver(getModelClass(), this);
    }

    public void unregisterDbObserver() {
        mDbHelper.unregisterObserver(getModelClass(), this);
    }

    public void loadMoreData(final int count) {
        setDataType(REFRESH);
        mCount = count;
        loadMoreFromNet(count, mDbHelper.LAST_INDEX);
    }


    public void loadDataByIds(List<Integer> ids) {
        mIds = ids;
        setDataType(IDS);
        fetchByIds(ids);
    }

    public void loadOneData(final int id) {
        setDataType(ONE);
        mCurOneOrder = id;
        loadOneFromNet(id);
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
        mDbHelper.getFromLocal(count, false);
    }

    public void loadLocalDataByIds(final List<Integer> ids) {
        List<T> models = new ArrayList<>();
        for (Integer id : ids) {
            T t = mDbHelper.getByOrder(id);
            if (t != null)
                models.add(t);
        }
        if (mListener != null)
            mListener.onLoadDone(models);
    }

    public void loadLatestData(final int count) {
        setDataType(REFRESH);
        loadLatestFromNet(count, mDbHelper.getMaxOrder());
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
            if (mListener != null)
                mListener.onLoadDone(null);
        } else {
            fetchByLabel(label, count, mLabelStartIndex);
        }

    }

    public void loadSuggestData(final int count, final String s) {
        setDataType(SUGGEST);
        if (!TextUtils.isEmpty(s) && count > 0) {
            fetchBySuggest(count, s);
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
            if (mListener != null)
                mListener.onLoadDone(datas);
            else {
                if (App.getInstance().isNetworkConnected()) {
                    loadMoreFromNet(mCount, mDbHelper.LAST_INDEX);
                } else {
                    if (mListener != null)
                        mListener.onLoadFailed(null);
                }
            }
    }

    public void loadMoreFromNet(int count, int lastOrder) {
        if (count > 0 && lastOrder > 0) {
            fetchByOrder(count, lastOrder);
        }
    }

    private void loadOneFromNet(int id) {
        fetchById(id);
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
        else if (type == LABEL) {
            if (mListener != null)
                mListener.onLoadDone(loadLocalLabelData(mCount, mCurLabel));
        } else if (type == SUGGEST) {
            if (mListener != null)
                mListener.onLoadDone(null);
        } else if (type == ONE) {
            if (mListener != null)
                mListener.onLoadDone(loadLocalOneData(mCurOneOrder));
        } else if (type == IDS) {
            loadLocalDataByIds(mIds);
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
            else if (type == LABEL) {
                if (datas.size() < mCount) {
                    isLabelDataEnd = true;
                }
                mLabelStartIndex += datas.size();
            } else if (type == SUGGEST || type == ONE) {
                // nothing to do
            }
            if (mListener != null)
                mListener.onLoadDone(models);
        } else {
            if (mListener != null)
                mListener.onLoadDone(null);
        }
    }

    private void changLastIndex(List<T> models) {
        if (isFirst) {
            isFirst = false;
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

    protected void fetchById(int id) {
        String json = buildOneJson(id);
        mElasticHelper.setParser(getParser(Contant.PARSER_TYPE_ONE));
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

    protected void fetchByIds(List<Integer> ids) {
        String json = buildIdsJson(ids);
        mElasticHelper.setParser(getParser(Contant.PARSER_TYPE_MORE));
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

    protected String buildOneJson(int id) {
        return null;
    }

    protected String buildIdsJson(List<Integer> ids) {
        return null;
    }

    protected abstract JsonParser<T> getParser(int type);

    protected List<T> loadLocalLabelData(int count, String label) {
        // DO nothing, if need, the child must override the method
        return null;
    }


    protected String buildLabelJson(String label, int count, int startIndex) {
        // DO nothing, if need, the child must override the method
        return null;
    }

    protected List<T> loadLocalOneData(int curOneOrder) {
        return null;
    }

}
