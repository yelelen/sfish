package com.yelelen.sfish.helper;

import android.support.v7.util.DiffUtil;

import java.util.List;

/**
 * Created by yelelen on 17-8-10.
 */

public class DiffUiDataCallback<T extends DiffUiDataCallback.UiDataDiffer<T>> extends DiffUtil.Callback {
    private List<T> oldList, newList;

    public DiffUiDataCallback(List<T> oldList, List<T> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        T oldData = oldList.get(oldItemPosition);
        T newData = newList.get(newItemPosition);
        return newData.isSame(oldData);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        T oldData = oldList.get(oldItemPosition);
        T newData = newList.get(newItemPosition);
        return newData.isContentSame(oldData);
    }

    public interface UiDataDiffer<T>{
        boolean isSame(T old);
        boolean isContentSame(T old);
    }
}
