package com.yelelen.sfish.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yelelen.sfish.Model.SoundTrackModel;
import com.yelelen.sfish.R;
import com.yelelen.sfish.contract.SoundTrackItemClickListener;
import com.yelelen.sfish.utils.Utils;
import com.yelelen.sfish.view.MusicBar;

/**
 * Created by yelelen on 17-10-21.
 */

public class SoundAlbumTrackAdapter extends RecyclerAdapter<SoundTrackModel>
    implements RecyclerAdapter.AdapterListener<SoundTrackModel>{
    private SoundTrackItemClickListener mClickListener;

    public SoundAlbumTrackAdapter(SoundTrackItemClickListener listener) {
        mClickListener = listener;
        setAdapterListener(this);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_sound_album_track;
    }

    @Override
    protected TrackViewHolder onCreateViewHolderImpl(View root, int viewType) {
        return new TrackViewHolder(root);
    }

    class TrackViewHolder extends RecyclerAdapter.BaseViewHolder<SoundTrackModel> {
        private TextView mTitle;
        private MusicBar mMusicBar;
        private ImageView mDownload;
        private Context mContext;

        public TrackViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mTitle = itemView.findViewById(R.id.sound_album_track_title);
            mMusicBar = itemView.findViewById(R.id.sound_album_track_bar);
            mDownload = itemView.findViewById(R.id.sound_album_track_download);
            mDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.showToast(mContext, "click download");
                }
            });
        }

        @Override
        protected void onBind(SoundTrackModel data) {
            mTitle.setText(data.getTitle());
        }
    }

    @Override
    public void onItemClick(BaseViewHolder holder, SoundTrackModel data) {
        if (mClickListener != null)
            mClickListener.onTrackClick(data);
    }

    @Override
    public void onItemLongClick(BaseViewHolder holder, SoundTrackModel data) {

    }
}
