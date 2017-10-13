package com.yelelen.sfish.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yelelen.sfish.Model.MmLabelModel;
import com.yelelen.sfish.R;

/**
 * Created by yelelen on 17-9-5.
 */

public class MmLabelAdapter extends RecyclerAdapter<MmLabelModel> {

    public MmLabelAdapter(AdapterListener<MmLabelModel> adapterListener) {
        super(adapterListener);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_label;
    }

    @Override
    protected MmViewHolder onCreateViewHolderImpl(View root, int viewType) {
        return new MmViewHolder(root);
    }

    class MmViewHolder extends BaseViewHolder<MmLabelModel> {
        private ImageView mImageView;
        private Context mContext;
        private TextView mLabel;

        public MmViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mImageView = itemView.findViewById(R.id.im_item_mm);
            mLabel = itemView.findViewById(R.id.txt_label);
        }

        @Override
        protected void onBind(MmLabelModel data) {
            Glide.with(mContext)
                    .load(data.getPath())
                    .placeholder(R.drawable.ic_mm_wait)
                    .fitCenter()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(mImageView);
            mLabel.setText(data.getLabel());
        }
    }
}
