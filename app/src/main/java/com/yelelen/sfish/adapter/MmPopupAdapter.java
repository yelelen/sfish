package com.yelelen.sfish.adapter;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yelelen.sfish.Model.MmItemModel;
import com.yelelen.sfish.R;

/**
 * Created by yelelen on 17-10-8.
 */

public class MmPopupAdapter extends RecyclerAdapter<MmItemModel> {
    public MmPopupAdapter(AdapterListener<MmItemModel> adapterListener) {
        super(adapterListener);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_mm_popup;
    }

    @Override
    protected MmPopupViewHolder onCreateViewHolderImpl(View root, int viewType) {
        return new MmPopupViewHolder(root);
    }

    class MmPopupViewHolder extends RecyclerAdapter.BaseViewHolder<MmItemModel> {
        private ImageView mImageView;
        private Context mContext;
        private TextView mTitle;

        public MmPopupViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.txt_item_mm_popup);
            mImageView = (ImageView) itemView.findViewById(R.id.im_item_mm_popup);
            mContext = mImageView.getContext();
        }

        @Override
        protected void onBind(MmItemModel data) {
            Glide.with(mContext)
                    .load(data.getPath())
                    .placeholder(R.drawable.ic_mm_wait)
                    .override(100, 200)
                    .fitCenter()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(mImageView);
            mTitle.setText(Html.fromHtml(data.getTitle()));
        }
    }
}
