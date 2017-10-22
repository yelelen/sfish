package com.yelelen.sfish.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yelelen.sfish.App;
import com.yelelen.sfish.Model.SoundAlbumItemModel;
import com.yelelen.sfish.Model.SoundItemModel;
import com.yelelen.sfish.Model.SoundTrackModel;
import com.yelelen.sfish.Model.SoundZhuboModel;
import com.yelelen.sfish.R;
import com.yelelen.sfish.adapter.RecyclerAdapter;
import com.yelelen.sfish.adapter.SoundAlbumAdapter;
import com.yelelen.sfish.adapter.SoundAlbumTrackAdapter;
import com.yelelen.sfish.adapter.SoundViewPagerAdapter;
import com.yelelen.sfish.contract.LoadContent;
import com.yelelen.sfish.contract.SoundTrackItemListener;
import com.yelelen.sfish.contract.SoundTrackListener;
import com.yelelen.sfish.contract.SoundZhuboListener;
import com.yelelen.sfish.helper.Contant;
import com.yelelen.sfish.presenter.SoundItemPresenter;
import com.yelelen.sfish.presenter.SoundTrackPresenter;
import com.yelelen.sfish.presenter.SoundZhuboPresenter;
import com.yelelen.sfish.utils.BlurUtil;
import com.yelelen.sfish.utils.Utils;
import com.yelelen.sfish.view.CircleSeekBar;
import com.yelelen.sfish.view.RecyclerViewDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SoundPlayActivity extends BaseActivity implements LoadContent<SoundItemModel>,
        CircleSeekBar.OnStateChangeListener, View.OnClickListener, SoundTrackListener,
        SoundTrackItemListener, SoundZhuboListener{
    private int mAlbumOrder;
    private String mAlbumCoverPath;
    private SoundItemPresenter mItemPresenter;
    private ViewPager mViewPager;
    private SoundViewPagerAdapter mViewPagerAdapter;
    private List<View> mViews;
    private ConstraintLayout mPlayer;
    private CoordinatorLayout mZhubo;
    private CoordinatorLayout mAlbum;
    private ImageView mIndicator1;
    private ImageView mIndicator2;
    private ImageView mIndicator3;

    private static final int ZHUBO = 0;
    private static final int PLAYER = 1;
    private static final int ALBUM = 2;
    private static final int MSG_COVER_ROTATE = 10;
    private static final int TYPE_CURRENT_ALBUM = 100;
    private static final int TYPE_ZHUBO_ALBUM = 101;
    private int mCurrentType = TYPE_CURRENT_ALBUM;


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
    private ProgressBar mAlbumProgressBar;
    private RecyclerView mAlbumRecycler;
    private SoundAlbumTrackAdapter mTrackAdapter;
    private SoundTrackPresenter mTrackPresenter;

    private List<Integer> mTrackIds;
    private int mTrackCount = 20;
    private int mTrackIndex = 0;
    private int mZhuboAlbumCount = 9;

    private SoundItemModel mCurrentAlbum;
    private SoundTrackModel mCurrentTrack;
    private SoundZhuboModel mCurrentZhubo;

    private boolean mIsFirstTrackBack = true;

    private SoundZhuboPresenter mZhuboPresenter;
    private CircleImageView mZhuboCover;
    private TextView mZhuboNickname;
    private TextView mZhuboFansNum;
    private TextView mZhuboFollowNum;
    private TextView mZhuboZanNum;
    private TextView mZhuboSoundNum;
    private TextView mZhuboDesc;
    private RecyclerView mZhuboRecycler;
    private SoundAlbumAdapter mZhuboAlbumAdapter;
    private int mZhuboAlbumLastVisiableItem;

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
        mZhubo = (CoordinatorLayout) inflater.inflate(R.layout.vp_sound_zhubo, null, false);
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
        mAlbumProgressBar = mAlbum.findViewById(R.id.sound_album_progressbar);

        mZhuboCover = mZhubo.findViewById(R.id.sound_zhubo_cover);
        mZhuboDesc = mZhubo.findViewById(R.id.sound_zhubo_brief);
        mZhuboFansNum = mZhubo.findViewById(R.id.sound_zhubo_fans_count);
        mZhuboZanNum = mZhubo.findViewById(R.id.sound_zhubo_zan_count);
        mZhuboSoundNum = mZhubo.findViewById(R.id.sound_zhubo_sound_count);
        mZhuboFollowNum = mZhubo.findViewById(R.id.sound_zhubo_follow_count);
        mZhuboNickname = mZhubo.findViewById(R.id.sound_zhubo_nickname);
        mZhuboRecycler = mZhubo.findViewById(R.id.sound_zhubo_recycler);

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
                if (position == 0) {
                    setCurrentType(TYPE_ZHUBO_ALBUM);
                    loadZhuboAlbum(mZhuboAlbumCount);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        updateIndicator(PLAYER);

        Glide.with(this).load(mAlbumCoverPath).into(mPlayCover);

        setCurrentType(TYPE_CURRENT_ALBUM);
        mItemPresenter.loadOneData(mAlbumOrder);

        mTrackAdapter = new SoundAlbumTrackAdapter(this, mAlbumRecycler);
        mAlbumRecycler.setAdapter(mTrackAdapter);
        mAlbumRecycler.setLayoutManager(new LinearLayoutManager(this));
        mAlbumRecycler.addItemDecoration(new RecyclerViewDecoration(this,
                RecyclerViewDecoration.VERTICAL_LIST));
        mTrackPresenter = new SoundTrackPresenter(this);

        mZhuboPresenter = new SoundZhuboPresenter(this);
        mZhuboAlbumAdapter = new SoundAlbumAdapter(new RecyclerAdapter.AdapterListener<SoundAlbumItemModel>() {
            @Override
            public void onItemClick(RecyclerAdapter.BaseViewHolder holder, SoundAlbumItemModel data) {

            }

            @Override
            public void onItemLongClick(RecyclerAdapter.BaseViewHolder holder, SoundAlbumItemModel data) {

            }
        });
        mZhuboRecycler.setAdapter(mZhuboAlbumAdapter);
        mZhuboRecycler.setLayoutManager(new GridLayoutManager(this, 3));
        mZhuboRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                        mZhuboAlbumLastVisiableItem == mZhuboAlbumAdapter.getItemCount() - 1) {
                    setCurrentType(TYPE_ZHUBO_ALBUM);
                    loadZhuboAlbum(mZhuboAlbumCount);
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mZhuboAlbumLastVisiableItem = ((LinearLayoutManager)recyclerView.getLayoutManager())
                        .findLastCompletelyVisibleItemPosition();
            }
        });
    }

    private void loadTrack(int count) {
        mAlbumProgressBar.setVisibility(View.VISIBLE);
        List<Integer> ids = mTrackIds.subList(mTrackIndex, mTrackIndex + count);
        mTrackPresenter.loadDataByIds(ids);
        mTrackIndex += count;
    }

    private void loadZhubo(int zhuboId) {
        mZhuboPresenter.loadOneData(zhuboId);
    }

    private void loadZhuboAlbum(int count) {
        mItemPresenter.loadAlbumByZhuboId(count, mCurrentAlbum.getZhuboId());
    }

    private void setCurrentType(int type) {
        mCurrentType = type;
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
    public void onLoadDone(final List<SoundItemModel> t) {
        if (t != null && t.size() > 0) {
            if (mCurrentType == TYPE_CURRENT_ALBUM) {
                mCurrentAlbum = t.get(0);
                updateAlbumUi(mCurrentAlbum);
                String[] ids = mCurrentAlbum.getSounds().split(",");
                for (String id : ids) {
                    mTrackIds.add(Integer.valueOf(id));
                }
                loadTrack(mTrackCount);
                loadZhubo(mCurrentAlbum.getZhuboId());
            } else if (mCurrentType == TYPE_ZHUBO_ALBUM) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (SoundItemModel soundItemModel : t) {
                            SoundAlbumItemModel model = new SoundAlbumItemModel(soundItemModel);
                            mZhuboAlbumAdapter.add(model);
                        }
                    }
                });

            }

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

    private void updatePlayerUi() {
        if (mCurrentZhubo != null)
            mPlayerNickname.setText(mCurrentZhubo.getNickname());
        if (mCurrentTrack != null) {
            mTrackTitle.setText(mCurrentTrack.getTitle());
            mSeekBar.setTotalDuration(mCurrentTrack.getDuration());
        } else {
            mSeekBar.setTotalDuration(0);
        }

    }

    private void updateZhuboUi(SoundZhuboModel model) {
        mZhuboNickname.setText(model.getNickname());
        mZhuboDesc.setText(model.getBrief());
        mZhuboFollowNum.setText(String.valueOf(model.getFollowCount()));
        mZhuboFansNum.setText(String.valueOf(model.getFansCount()));
        mZhuboZanNum.setText(String.valueOf(model.getZanCount()));
        mZhuboSoundNum.setText(String.valueOf(model.getSoundCount()));

        Glide.with(this)
                .load(model.getCover())
                .into(mZhuboCover);
    }

    @Override
    public void onLoadFailed(String reason) {

    }

    @Override
    public void onTrackBack(final List<SoundTrackModel> models) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAlbumProgressBar.setVisibility(View.GONE);
                if (models != null && models.size() > 0) {
                    Collections.sort(models);
                    mTrackAdapter.add(models);
                    if (mIsFirstTrackBack) {
                        mIsFirstTrackBack = false;
                        mCurrentTrack = models.get(0);
                        updatePlayerUi();
                    }
                } else {
                    int what = App.getInstance().isNetworkConnected()
                            ? Contant.MSG_SOUND_ALBUM_TRACK_NO_MORE : Contant.MSG_NETWORK_UNAVAILABLE;
                    mHandler.sendEmptyMessage(what);
                }
            }
        });
    }

    @Override
    public void onZhuboBack(final SoundZhuboModel model) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCurrentZhubo = model;
                updateZhuboUi(model);
                updatePlayerUi();
            }
        });
    }



    @Override
    public void onTrackClick(SoundTrackModel data) {
        Utils.showToast(this, "Track clicked");
    }

    @Override
    public void onLoadMore() {
        loadTrack(mTrackCount);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
