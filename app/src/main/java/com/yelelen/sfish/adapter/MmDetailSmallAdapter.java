package com.yelelen.sfish.adapter;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yelelen.sfish.R;

/**
 * Created by yelelen on 17-9-11.
 */

public class MmDetailSmallAdapter extends RecyclerAdapter<String> {

    public MmDetailSmallAdapter(AdapterListener<String> adapterListener) {
        super(adapterListener);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_mm_detail_small;
    }

    @Override
    protected BaseViewHolder<String> onCreateViewHolderImpl(View root, int viewType) {
        return new MmDetailViewHolder(root);
    }

    class MmDetailViewHolder extends RecyclerAdapter.BaseViewHolder<String> {
        private ImageView mImageView;

        public MmDetailViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.im_item_mm_detail_small);
        }

        @Override
        protected void onBind(String data) {
//            Bitmap bitmap = BitmapFactory.decodeFile(data.getPath());

            Glide.with(mImageView.getContext())
                    .load(data)
                    .override(200, 300)
                    .into(mImageView);
        }
    }
}
