package com.yelelen.sfish.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yelelen.sfish.App;
import com.yelelen.sfish.Model.SoundTrackModel;
import com.yelelen.sfish.R;
import com.yelelen.sfish.activity.SoundPlayActivity;
import com.yelelen.sfish.contract.DownloadListener;
import com.yelelen.sfish.contract.SoundTrackItemListener;
import com.yelelen.sfish.contract.UpdateMusicBarListener;
import com.yelelen.sfish.helper.ThreadPoolHelper;
import com.yelelen.sfish.runnable.SoundTrackDownloadRunnable;
import com.yelelen.sfish.utils.Utils;
import com.yelelen.sfish.view.MusicBar;

import java.io.File;

/**
 * Created by yelelen on 17-10-21.
 */

public class SoundAlbumTrackAdapter extends RecyclerAdapter<SoundTrackModel>
        implements RecyclerAdapter.AdapterListener<SoundTrackModel> {
    private SoundTrackItemListener mListener;
    private int mLastVisibleItem;

    public SoundAlbumTrackAdapter(SoundTrackItemListener listener, RecyclerView recyclerView) {
        mListener = listener;
        setAdapterListener(this);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItem == getItemCount() - 1) {
                    mListener.onLoadMore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager())
                        .findLastCompletelyVisibleItemPosition();
            }
        });
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_sound_album_track;
    }

    @Override
    protected TrackViewHolder onCreateViewHolderImpl(View root, int viewType) {
        return new TrackViewHolder(root);
    }

    class TrackViewHolder extends RecyclerAdapter.BaseViewHolder<SoundTrackModel>
            implements UpdateMusicBarListener, DownloadListener {
        private TextView mTitle;
        private MusicBar mMusicBar;
        private ImageView mDownload;
        private Context mContext;
        private TextView mPlayCount;
        private TextView mDuration;
        private TextView mFavCount;
        private TextView mProgress;


        public TrackViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mTitle = itemView.findViewById(R.id.sound_album_track_title);
            mMusicBar = itemView.findViewById(R.id.sound_album_track_bar);
            mDownload = itemView.findViewById(R.id.sound_album_track_download);
            mPlayCount = itemView.findViewById(R.id.sound_album_track_play_count);
            mDuration = itemView.findViewById(R.id.sound_album_track_duration);
            mFavCount = itemView.findViewById(R.id.sound_album_track_fav_count);
            mProgress = itemView.findViewById(R.id.sound_album_download_text);
            mProgress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            mDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String dir = App.mSoundTrackBasePath + String.valueOf(mData.getAlbumId());
                    String fileName = String.valueOf(mData.getOrder());
                    String path = dir + File.separator + fileName;
                    if (!new File(path).exists()) {
                        if (App.getInstance().isNetworkConnected()) {
                            mDownload.setVisibility(View.GONE);
                            mProgress.setVisibility(View.VISIBLE);
                            SoundTrackDownloadRunnable runnable = new SoundTrackDownloadRunnable(
                                    mData.getPaths().split(",")[0],
                                    TrackViewHolder.this, dir, fileName);
                            ThreadPoolHelper.getInstance().start(runnable);
                            mProgress.setText("0 %");
                        } else {
                            Utils.showToast(mContext, mContext.getString(R.string.network_unavailable));
                        }
                    }
                }
            });
            SoundPlayActivity.setMusicBarListener(this);
        }

        @Override
        protected void onBind(SoundTrackModel data) {
            mTitle.setText(data.getTitle());
            mPlayCount.setText(String.valueOf(data.getPlayCount()));
            mDuration.setText(Utils.getDurationText(data.getDuration()));
            mFavCount.setText(String.valueOf(data.getFavCount()));
            String path = App.mSoundTrackBasePath + String.valueOf(data.getAlbumId()) +
                    File.separator + String.valueOf(data.getOrder());
            if (new File(path).exists()) {
                mDownload.setImageResource(R.drawable.ic_sound_album_download_done);
            }
        }

        @Override
        public void onUpdateMusicBar(boolean isPause, SoundTrackModel model) {
            if (model.getOrder() == mData.getOrder()) {
                mMusicBar.setVisibility(View.VISIBLE);
                mTitle.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                if (isPause)
                    mMusicBar.stop();
                else
                    mMusicBar.start();
            } else {
                mMusicBar.setVisibility(View.GONE);
                mTitle.setTextColor(Color.WHITE);
            }

        }

        @Override
        public void onProgress(final int progress) {
            SoundPlayActivity activity = (SoundPlayActivity)mContext;
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgress.setText(String.valueOf(progress) + " %");
                    }
                });
            }

        }

        @Override
        public void onSuccess() {
            SoundPlayActivity activity = (SoundPlayActivity)mContext;
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgress.setVisibility(View.GONE);
                        mDownload.setVisibility(View.VISIBLE);
                        mDownload.setImageResource(R.drawable.ic_sound_album_download_done);
                    }
                });
            }

        }

        @Override
        public void onFailed() {

        }

        @Override
        public void onCanceled() {

        }

        @Override
        public void onPaused() {

        }
    }


    @Override
    public void onItemClick(BaseViewHolder holder, SoundTrackModel data) {
        if (mListener != null) {
            mListener.onTrackClick(data, holder.getAdapterPosition());
        }

    }

    @Override
    public void onItemLongClick(BaseViewHolder holder, SoundTrackModel data) {

    }
}
