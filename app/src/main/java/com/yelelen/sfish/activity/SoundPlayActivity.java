package com.yelelen.sfish.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yelelen.sfish.Model.SoundItemModel;
import com.yelelen.sfish.Model.SoundTrackModel;
import com.yelelen.sfish.R;
import com.yelelen.sfish.adapter.SoundAlbumTrackAdapter;
import com.yelelen.sfish.adapter.SoundViewPagerAdapter;
import com.yelelen.sfish.contract.LoadContent;
import com.yelelen.sfish.contract.SoundTrackItemClickListener;
import com.yelelen.sfish.contract.SoundTrackListener;
import com.yelelen.sfish.presenter.SoundItemPresenter;
import com.yelelen.sfish.presenter.SoundTrackPresenter;
import com.yelelen.sfish.utils.BlurUtil;
import com.yelelen.sfish.utils.Utils;
import com.yelelen.sfish.view.CircleSeekBar;
import com.yelelen.sfish.view.RecyclerViewDecoration;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SoundPlayActivity extends BaseActivity implements LoadContent<SoundItemModel>,
        CircleSeekBar.OnStateChangeListener, View.OnClickListener, SoundTrackListener,
        SoundTrackItemClickListener{
    private SoundItemModel mModel;
    private int mAlbumOrder;
    private String mAlbumCoverPath;
    private SoundItemPresenter mItemPresenter;
    private ViewPager mViewPager;
    private SoundViewPagerAdapter mViewPagerAdapter;
    private List<View> mViews;
    private ConstraintLayout mPlayer;
    private RelativeLayout mZhubo;
    private CoordinatorLayout mAlbum;
    private ImageView mIndicator1;
    private ImageView mIndicator2;
    private ImageView mIndicator3;

    private static final int ZHUBO = 0;
    private static final int PLAYER = 1;
    private static final int ALBUM = 2;
    private static final int MSG_COVER_ROTATE = 10;

    private CircleImageView mPlayCover;
    private CircleSeekBar mSeekBar;
    private LinearInterpolator mLinearInterpolator;

    private TextView mTrackTitle;
    private TextView mPlayerNickname;
    private ImageView mBack;
    private ImageView mDownload;
    private ImageView mOrder;
    private ImageView mTime;
    private ImageView mFav;
    private ImageView mBack15;
    private ImageView mPrevious;
    private ImageView mPlay;
    private ImageView mNext;
    private ImageView mForward15;

    private ImageView mAlbumCover;
    private TextView mAlbumTitle;
    private TextView mAlbumTag;
    private TextView mAlbumUpdateTime;
    private TextView mAlbumPlayCount;
    private TextView mAlbumDesc;
    private RecyclerView mAlbumRecycler;
    private SoundAlbumTrackAdapter mTrackAdapter;
    private SoundTrackPresenter mTrackPresenter;

    private List<Integer> mTrackIds;
    private int mCount = 30;
    private int mTrackIndex = 0;


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_COVER_ROTATE:
                    mPlayCover.animate().rotationBy(50)
                            .setDuration(1000)
                            .setInterpolator(mLinearInterpolator)
                            .start();
                    if (mSeekBar.isStart())
                        mHandler.sendEmptyMessageDelayed(MSG_COVER_ROTATE, 1000);
                    break;
                default:
                    break;
            }
            return true;
        }
    });

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_sound_play;
    }

    @Override
    protected boolean initArgs() {
        mAlbumOrder = getIntent().getIntExtra("SoundAlbum", 0);
        mAlbumCoverPath = getIntent().getStringExtra("SoundAlbumCover");
        return true;
    }

    @Override
    protected void initView() {
        super.initView();
        ((ImageView) findViewById(R.id.sound_play_bg)).setImageBitmap(getBlurBitmap());
        mViewPager = findViewById(R.id.sound_play_view_pager);
        mIndicator1 = findViewById(R.id.sound_play_vp_indicator1);
        mIndicator2 = findViewById(R.id.sound_play_vp_indicator2);
        mIndicator3 = findViewById(R.id.sound_play_vp_indicator3);

        mBack = findViewById(R.id.sound_play_back);
        mTrackTitle = findViewById(R.id.sound_play_track_title);
        mPlayerNickname = findViewById(R.id.sound_play_zhubo_nickname);
        mBack.setOnClickListener(this);

        LayoutInflater inflater = getLayoutInflater();
        mZhubo = (RelativeLayout) inflater.inflate(R.layout.vp_sound_zhubo, null, false);
        mPlayer = (ConstraintLayout) inflater.inflate(R.layout.vp_sound_play, null, false);
        mAlbum = (CoordinatorLayout) inflater.inflate(R.layout.vp_sound_album, null, false);

        mPlayCover = mPlayer.findViewById(R.id.sound_play_cover);
        mSeekBar = mPlayer.findViewById(R.id.sound_play_seekbar);
        mSeekBar.setListener(this);
        mLinearInterpolator = new LinearInterpolator();
        mOrder = mPlayer.findViewById(R.id.sound_play_order);
        mTime = mPlayer.findViewById(R.id.sound_play_time);
        mFav = mPlayer.findViewById(R.id.sound_play_fav);
        mBack15 = mPlayer.findViewById(R.id.sound_play_back_15);
        mPrevious = mPlayer.findViewById(R.id.sound_play_previous);
        mPlay = mPlayer.findViewById(R.id.sound_play_play);
        mNext = mPlayer.findViewById(R.id.sound_play_next);
        mForward15 = mPlayer.findViewById(R.id.sound_play_forward_15);

        mAlbumCover = mAlbum.findViewById(R.id.sound_album_cover);
        mAlbumTitle = mAlbum.findViewById(R.id.sound_album_title);
        mAlbumTag = mAlbum.findViewById(R.id.sound_album_tag);
        mAlbumUpdateTime = mAlbum.findViewById(R.id.sound_album_update);
        mAlbumPlayCount = mAlbum.findViewById(R.id.sound_album_play_count);
        mAlbumDesc = mAlbum.findViewById(R.id.sound_album_desc);
        mAlbumRecycler = mAlbum.findViewById(R.id.sound_album_recycler);


        mViews = new ArrayList<>();
        mViews.add(mZhubo);
        mViews.add(mPlayer);
        mViews.add(mAlbum);

    }


    @Override
    protected void initData() {
        super.initData();
        mTrackIds = new ArrayList<>();

        mItemPresenter = new SoundItemPresenter(this);
        mViewPagerAdapter = new SoundViewPagerAdapter(mViews);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setCurrentItem(PLAYER);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        updateIndicator(PLAYER);

        mPlayCover.setImageBitmap(BitmapFactory.decodeFile(mAlbumCoverPath));

        mItemPresenter.loadOneData(mAlbumOrder);

        mTrackAdapter = new SoundAlbumTrackAdapter(this);
        mAlbumRecycler.setAdapter(mTrackAdapter);
        mAlbumRecycler.setLayoutManager(new LinearLayoutManager(this));
        mAlbumRecycler.addItemDecoration(new RecyclerViewDecoration(this,
                RecyclerViewDecoration.VERTICAL_LIST));
        mTrackPresenter = new SoundTrackPresenter(this);
    }

    private void loadTrack(int count) {
        while (--count >= 0) {
            mTrackPresenter.loadOneData(mTrackIds.get(mTrackIndex++));
        }
    }

    private void updateIndicator(int pos) {
        mIndicator1.setImageResource(R.drawable.shape_sound_play_vp_indicator_normal);
        mIndicator2.setImageResource(R.drawable.shape_sound_play_vp_indicator_normal);
        mIndicator3.setImageResource(R.drawable.shape_sound_play_vp_indicator_normal);

        if (pos == 0)
            mIndicator1.setImageResource(R.drawable.shape_sound_play_vp_indicator_check);
        else if (pos == 1)
            mIndicator2.setImageResource(R.drawable.shape_sound_play_vp_indicator_check);
        else
            mIndicator3.setImageResource(R.drawable.shape_sound_play_vp_indicator_check);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sound_play_back:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onSeek(int progress) {

    }

    @Override
    public void onBegin() {
        mHandler.sendEmptyMessage(MSG_COVER_ROTATE);
    }

    @Override
    public void onEnd() {


    }

    private Bitmap getBlurBitmap() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sound_play_bg_small);
        int scaleRatio = 10;
        int blurRadius = 8;
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,
                bitmap.getWidth() / scaleRatio,
                bitmap.getHeight() / scaleRatio,
                false);
        Bitmap blurBitmap = BlurUtil.doBlur(scaledBitmap, blurRadius, true);
        return blurBitmap;
    }

    @Override
    public void onLoadDone(List<SoundItemModel> t) {
        if (t != null && t.size() > 0) {
            mModel = t.get(0);
            updateAlbumUi(mModel);
            String[] ids = mModel.getSounds().split(",");
            for (String id : ids) {
                mTrackIds.add(Integer.valueOf(id));
            }
            loadTrack(mCount);
        }
    }

    private void updateAlbumUi(final SoundItemModel model) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(SoundPlayActivity.this)
                        .load(mAlbumCoverPath)
                        .placeholder(R.drawable.ic_sound_wait)
                        .centerCrop()
                        .into(mAlbumCover);
                mAlbumTitle.setText(model.getTitle());
                mAlbumTag.setText(model.getTag());
                mAlbumUpdateTime.setText(model.getLastUpdateTime());
                mAlbumPlayCount.setText(model.getPlayCount());
                mAlbumDesc.setText(model.getDesc());
            }
        });
    }

    @Override
    public void onLoadFailed(String reason) {

    }

    @Override
    public void onTrackBack(final List<SoundTrackModel> models) {
        if (models != null && models.size() > 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTrackAdapter.add(models);
                }
            });
        }
    }

    @Override
    public void onTrackClick(SoundTrackModel data) {
        Utils.showToast(this, "Track clicked");
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
