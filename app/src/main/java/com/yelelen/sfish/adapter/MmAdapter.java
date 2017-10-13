package com.yelelen.sfish.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yelelen.sfish.Model.MmItemModel;
import com.yelelen.sfish.R;

/**
 * Created by yelelen on 17-9-5.
 */

public class MmAdapter extends RecyclerAdapter<MmItemModel> {

    public MmAdapter(AdapterListener<MmItemModel> adapterListener) {
        super(adapterListener);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_mm;
    }

    @Override
    protected MmViewHolder onCreateViewHolderImpl(View root, int viewType) {
        return new MmViewHolder(root);
    }

    class MmViewHolder extends RecyclerAdapter.BaseViewHolder<MmItemModel> {
        private ImageView mImageView;
        private Context mContext;
        private TextView mTitle;
        private TextView mSeenNum;
        private TextView mFavNum;

        public MmViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.txt_title);
            mSeenNum = (TextView) itemView.findViewById(R.id.txt_seen_num);
            mFavNum = (TextView) itemView.findViewById(R.id.txt_fav_num);
            mImageView = (ImageView) itemView.findViewById(R.id.im_item_mm);
            mContext = mImageView.getContext();
        }

        @Override
        protected void onBind(MmItemModel data) {
////            Log.e("======>", data.getPath());
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inSampleSize = 3;
//            Bitmap bitmap = BitmapFactory.decodeFile(data.getPath(), options);
//////            Bitmap bitmap = Utils.compressBitmap(data.getPath(), mImageView);
//            mImageView.setImageBitmap(bitmap);
            Glide.with(mContext)
                    .load(data.getPath())
                    .placeholder(R.drawable.ic_mm_wait)
                    .override(300, 600)
                    .fitCenter()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(mImageView);
            mSeenNum.setText(String.valueOf(data.getSeenNum()));
            mFavNum.setText(String.valueOf(data.getFavNum()));
            mTitle.setText(String.valueOf(data.getTitle()));
        }
    }
}
