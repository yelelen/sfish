package com.yelelen.sfish.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yelelen.sfish.Model.SoundAlbumItemModel;
import com.yelelen.sfish.R;

/**
 * Created by yelelen on 17-9-5.
 */

public class SoundAlbumAdapter extends RecyclerAdapter<SoundAlbumItemModel> {

    public SoundAlbumAdapter(AdapterListener<SoundAlbumItemModel> adapterListener) {
        super(adapterListener);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_sound_album;
    }

    @Override
    protected SoundAlbumViewHolder onCreateViewHolderImpl(View root, int viewType) {
        return new SoundAlbumViewHolder(root);
    }

    class SoundAlbumViewHolder extends BaseViewHolder<SoundAlbumItemModel> {
        private ImageView mCover;
        private Context mContext;
        private TextView mTitle;

        public SoundAlbumViewHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.label_sound_album_title);
            mCover = itemView.findViewById(R.id.im_sound_album_cover);
            mContext = mCover.getContext();
        }

        @Override
        protected void onBind(SoundAlbumItemModel data) {
            Glide.with(mContext)
                    .load(data.getCover())
                    .placeholder(R.drawable.ic_sound_wait)
                    .centerCrop()
                    .into(mCover);
            mTitle.setText(data.getTitle());
        }
    }
}
