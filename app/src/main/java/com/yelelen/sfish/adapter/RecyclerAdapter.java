package com.yelelen.sfish.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yelelen.sfish.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Created by yelelen on 17-9-4.
 */

public abstract class RecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerAdapter.BaseViewHolder<T>>
        implements View.OnClickListener, View.OnLongClickListener, AdapterCallback<T> {
    public List<T> mDatas;
    private AdapterListener<T> mAdapterListener;

    public RecyclerAdapter() {
        this(null);
    }

    public RecyclerAdapter(AdapterListener<T> adapterListener) {
        mDatas = new ArrayList<>();
        mAdapterListener = adapterListener;
    }

    public RecyclerAdapter(List<T> datas, AdapterListener<T> adapterListener) {
        mDatas = datas;
        mAdapterListener = adapterListener;
    }

    @Override
    public BaseViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(getItemLayoutId(), parent, false);
        BaseViewHolder<T> holder = onCreateViewHolderImpl(root, viewType);

        root.setTag(R.id.tag_view_holder, holder);
        root.setOnClickListener(this);
        root.setOnLongClickListener(this);

        holder.mCallback = this;

        return holder;
    }


    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        T data = mDatas.get(position);
        holder.bind(data);
    }

    protected abstract int getItemLayoutId();

    protected abstract BaseViewHolder<T> onCreateViewHolderImpl(View root, int viewType);

    @Override
    public void onClick(View v) {
        if (mAdapterListener != null) {
            BaseViewHolder holder = (BaseViewHolder) (v.getTag(R.id.tag_view_holder));
            int pos = holder.getAdapterPosition();
            mAdapterListener.onItemClick(holder, mDatas.get(pos));
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mAdapterListener != null) {
            BaseViewHolder holder = (BaseViewHolder) (v.getTag(R.id.tag_view_holder));
            int pos = holder.getAdapterPosition();
            mAdapterListener.onItemLongClick(holder, mDatas.get(pos));
            return true;
        }

        return false;
    }

    @Override
    public void update(T data, BaseViewHolder<T> holder) {
        int pos = holder.getAdapterPosition();
        if (pos >= 0) {
            mDatas.remove(pos);
            mDatas.add(pos, data);
            notifyItemChanged(pos);
        }
    }

    public void setAdapterListener(AdapterListener<T> adapterListener) {
        mAdapterListener = adapterListener;
    }

    public void addToEnd(T data) {
        mDatas.add(data);
        notifyItemInserted(mDatas.size() - 1);
    }


    public void addToFirst(T data) {
        mDatas.add(0, data);
        notifyItemInserted(0);
    }

    public void add(T... datas) {
        if (datas != null && datas.length > 0) {
            int startPos = mDatas.size();
            Collections.addAll(mDatas, datas);
            notifyItemRangeChanged(startPos, datas.length);
        }
    }

    public void add(Collection<T> datas) {
        if (datas != null && datas.size() > 0) {
            int startPos = mDatas.size();
            mDatas.addAll(datas);
            notifyItemRangeChanged(startPos, datas.size());
        }
    }

    public void replace(Collection<T> datas) {
        mDatas.clear();

        if (datas == null || datas.size() == 0) {
            notifyDataSetChanged();
            return;
        }

        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public static abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {
        private AdapterCallback<T> mCallback;
        protected T mData;

        public BaseViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(T data) {
            mData = data;
            onBind(data);
        }

        protected abstract void onBind(T data);

        protected void updateData(T data) {
            if (mCallback != null)
                this.mCallback.update(data, this);
        }

    }

    public interface AdapterListener<T> {
        void onItemClick(BaseViewHolder holder, T data);

        void onItemLongClick(BaseViewHolder holder, T data);
    }

    public abstract class AdapterListenerImpl<T> implements AdapterListener<T> {
        @Override
        public void onItemClick(BaseViewHolder holder, T data) {

        }

        @Override
        public void onItemLongClick(BaseViewHolder holder, T data) {

        }
    }
}