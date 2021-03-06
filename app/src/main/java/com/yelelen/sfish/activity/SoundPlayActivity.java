package com.yelelen.sfish.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialog;
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
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import com.yelelen.sfish.contract.MediaPlayerListener;
import com.yelelen.sfish.contract.SoundTrackItemListener;
import com.yelelen.sfish.contract.SoundTrackListener;
import com.yelelen.sfish.contract.SoundZhuboListener;
import com.yelelen.sfish.contract.UpdateMusicBarListener;
import com.yelelen.sfish.helper.Contant;
import com.yelelen.sfish.presenter.SoundItemPresenter;
import com.yelelen.sfish.presenter.SoundTrackPresenter;
import com.yelelen.sfish.presenter.SoundZhuboPresenter;
import com.yelelen.sfish.service.SoundService;
import com.yelelen.sfish.utils.BlurUtil;
import com.yelelen.sfish.utils.SnackbarUtil;
import com.yelelen.sfish.utils.Utils;
import com.yelelen.sfish.view.CircleSeekBar;
import com.yelelen.sfish.view.RecyclerViewDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SoundPlayActivity extends BaseActivity implements LoadContent<SoundItemModel>,
        CircleSeekBar.OnStateChangeListener, View.OnClickListener, SoundTrackListener,
        SoundTrackItemListener, SoundZhuboListener, MediaPlayerListener {
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
    private static final int MSG_UPDATE_REST_TIME = 11;
    private static final int TYPE_ALBUM_CURRENT = 100;
    private static final int TYPE_ALBUM_ZHUBO = 101;
    private int mCurrentAlbumType = TYPE_ALBUM_CURRENT;

    private static final int TYPE_ORDER_ORDER = 200;
    private static final int TYPE_ORDER_RANDOM = 201;
    private static final int TYPE_ORDER_REPEAT = 202;
    private int mCurrentOrderType = TYPE_ORDER_ORDER;

    private static final int TYPE_TIME_CLOSE = 300;
    private static final int TYPE_TIME_CURRENT = 301;
    private static final int TYPE_TIME_2 = 302;
    private static final int TYPE_TIME_3 = 303;
    private static final int TYPE_TIME_MINUTE_10 = 304;
    private static final int TYPE_TIME_MINUTE_20 = 305;
    private static final int TYPE_TIME_MINUTE_30 = 306;
    private static final int TYPE_TIME_MINUTE_60 = 307;
    private static final int TYPE_TIME_MINUTE_90 = 308;
    private int mCurrentTimeType = TYPE_TIME_CLOSE;
    private int mLastTimeType = mCurrentTimeType;
    private int mCurrentTimeCheckId = R.id.rb_sound_time_close;
    private int mRestTime = 0;

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
    private ProgressBar mPlayerProgressBar;
    private ImageView mPlayerOverEnd;
    private TextView mTimeText;

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
    private int mTrackCount = 15;
    private int mTrackIndex = 0;
    private int mZhuboAlbumCount = 9;

    private SoundItemModel mCurrentAlbum;
    private SoundTrackModel mCurrentTrack;
    private int mCurrentTrackIndex = 0;
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
    private int mZhuboAlbumLastVisibleItem;
    private ProgressBar mZhuboProgressBar;

    private SoundService.SoundBinder mSoundBinder;
    private boolean mIsInitPlay = true;

    private List<SoundTrackModel> mTrackModels;
    private static List<UpdateMusicBarListener> mMusicBarListeners;

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
                case MSG_UPDATE_REST_TIME:
                    mTimeText.setText(Utils.getDurationText(--mRestTime));
                    if (mRestTime > 0) {
                        if (mTimeText.isShown()) {
                            if (mCurrentTimeType >= TYPE_TIME_MINUTE_10)
                                mHandler.sendEmptyMessageDelayed(MSG_UPDATE_REST_TIME, 1000);
                            else {
                                if (mSoundBinder.isPlaying())
                                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_REST_TIME, 1000);
                            }
                        }
                    } else {
                        if (mCurrentTimeType != TYPE_TIME_CLOSE && mSoundBinder.isPlaying()) {
                            mTimeText.setVisibility(View.GONE);
                            mSoundBinder.pause();
                        }
                    }

                default:
                    break;
            }
            return true;
        }
    });

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mSoundBinder = (SoundService.SoundBinder) service;
            mSoundBinder.setListener(SoundPlayActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

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
        mPlayerProgressBar = mPlayer.findViewById(R.id.sound_play_progressBar);
        mPlayerOverEnd = mPlayer.findViewById(R.id.sound_play_progress_bar_completition);
        mTimeText = mPlayer.findViewById(R.id.sound_play_time_text);


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
        mZhuboProgressBar = mZhubo.findViewById(R.id.sound_zhubo_progressbar);

        mViews = new ArrayList<>();
        mViews.add(mZhubo);
        mViews.add(mPlayer);
        mViews.add(mAlbum);

        initClickListener();

        bindSoundService();

    }

    private void bindSoundService() {
        Intent intent = new Intent(this, SoundService.class);
        startService(intent);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    private void initClickListener() {
        mPlay.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mPrevious.setOnClickListener(this);
        mBack15.setOnClickListener(this);
        mForward15.setOnClickListener(this);
        mOrder.setOnClickListener(this);
        mTime.setOnClickListener(this);
    }

    public static void setMusicBarListener(UpdateMusicBarListener listener) {
        if (listener != null) {
            mMusicBarListeners.add(listener);
        }
    }

    private void updateMusicBar(boolean isPause) {
        if (mMusicBarListeners.size() > 0) {
            for (UpdateMusicBarListener mMusicBarListener : mMusicBarListeners) {
                mMusicBarListener.onUpdateMusicBar(isPause, mCurrentTrack);
            }
        }
    }

    @Override
    protected void initData() {
        super.initData();
        mTrackIds = new ArrayList<>();
        mTrackModels = new ArrayList<>();
        mMusicBarListeners = new ArrayList<>();

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
                    setCurrentAlbumType(TYPE_ALBUM_ZHUBO);
                    loadZhuboAlbum(mZhuboAlbumCount);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        updateIndicator(PLAYER);

        Glide.with(this).load(mAlbumCoverPath).into(mPlayCover);

        setCurrentAlbumType(TYPE_ALBUM_CURRENT);
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
                if (holder instanceof SoundAlbumAdapter.SoundAlbumViewHolder) {
                    Context context = ((SoundAlbumAdapter.SoundAlbumViewHolder) holder).itemView.getContext();
                    Intent intent = new Intent(context, SoundPlayActivity.class);
                    intent.putExtra("SoundAlbum", data.getOrder());
                    intent.putExtra("SoundAlbumCover", data.getCover());
                    context.startActivity(intent);
                    finish();
                }
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
                        mZhuboAlbumLastVisibleItem == mZhuboAlbumAdapter.getItemCount() - 1) {
                    mZhuboProgressBar.setVisibility(View.VISIBLE);
                    setCurrentAlbumType(TYPE_ALBUM_ZHUBO);
                    loadZhuboAlbum(mZhuboAlbumCount);
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mZhuboAlbumLastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager())
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
        if (mCurrentAlbum != null)
            mItemPresenter.loadAlbumByZhuboId(count, mCurrentAlbum.getZhuboId());
    }

    private void setCurrentAlbumType(int type) {
        mCurrentAlbumType = type;
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

    private void setCurrentOrderType(int order) {
        mCurrentOrderType = order;
        updateOrderImage(mCurrentOrderType);
    }

    private void updateOrderImage(int currentOrder) {
        mOrder.setImageResource(currentOrder == TYPE_ORDER_ORDER ? R.drawable.ic_sound_order :
                (currentOrder == TYPE_ORDER_REPEAT) ? R.drawable.ic_sound_repeat : R.drawable.ic_sound_random);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sound_play_back:
                finish();
                break;
            case R.id.sound_play_play:
                playOrPause();
                break;
            case R.id.sound_play_next:
                playNext();
                break;
            case R.id.sound_play_previous:
                playPrevious();
                break;
            case R.id.sound_play_back_15:
                playBack15();
                break;
            case R.id.sound_play_forward_15:
                playForward15();
                break;
            case R.id.sound_play_order:
                switchOrder();
                break;
            case R.id.sound_play_time:
                showTimeDialog();
                break;
            default:
                break;
        }
    }

    private void switchOrder() {
        int order = (++mCurrentOrderType - TYPE_ORDER_ORDER) % 3 + TYPE_ORDER_ORDER;
        setCurrentOrderType(order);
    }

    private void setCurrentTimeType(int timeType) {
        mLastTimeType = mCurrentTimeType;
        mCurrentTimeType = timeType;
    }

    private void playForward15() {
        mSoundBinder.seekTo(mSoundBinder.getPlayedDuration() + 15);
        if (TYPE_TIME_CURRENT <= mCurrentTimeType && mCurrentTimeType <= TYPE_TIME_3)
            updateTimeText();
    }

    private void playBack15() {
        int play = mSoundBinder.getPlayedDuration();
        if (play >= 15)
            mSoundBinder.seekTo(mSoundBinder.getPlayedDuration() - 15);
        else
            mSoundBinder.seekTo(0);
        if (TYPE_TIME_CURRENT <= mCurrentTimeType && mCurrentTimeType <= TYPE_TIME_3)
            updateTimeText();
    }

    private void playNext() {
        if (mCurrentOrderType == TYPE_ORDER_ORDER)
            playOrderNext();
        else if (mCurrentOrderType == TYPE_ORDER_REPEAT) {
            if (mIsInitPlay) {
                initPlay();
            } else {
                mSoundBinder.seekTo(0);
            }
        } else {
            playRandom();
        }
        if (TYPE_TIME_CURRENT <= mCurrentTimeType && mCurrentTimeType <= TYPE_TIME_3)
            updateTimeText();
    }


    private void playPrevious() {
        if (mCurrentOrderType == TYPE_ORDER_ORDER)
            playOrderPrevious();
        else if (mCurrentOrderType == TYPE_ORDER_REPEAT) {
            if (mIsInitPlay) {
                initPlay();
            } else {
                mSoundBinder.seekTo(0);
            }
        } else {
            playRandom();
        }
        if (TYPE_TIME_CURRENT <= mCurrentTimeType && mCurrentTimeType <= TYPE_TIME_3)
            updateTimeText();
    }


    private void playOrPause() {
        if (mIsInitPlay) {
            initPlay();
        } else {
            if (!mSoundBinder.isPlaying())
                mSoundBinder.play();
            else
                mSoundBinder.pause();
        }

    }

    private void initPlay() {
        mIsInitPlay = false;
        mCurrentTrack = mTrackModels.get(mCurrentTrackIndex);
        String path = App.mSoundTrackBasePath + String.valueOf(mCurrentTrack.getAlbumId()) +
                File.separator + String.valueOf(mCurrentTrack.getOrder());
        if (!new File(path).exists()) {
            path = mCurrentTrack.getPaths().split(",")[0];
            mPlayerProgressBar.setVisibility(View.VISIBLE);
            mPlayerOverEnd.setVisibility(View.GONE);
        } else {
            mPlayerProgressBar.setVisibility(View.GONE);
            mPlayerOverEnd.setVisibility(View.VISIBLE);
        }
        mSoundBinder.init(path);
        updatePlayerUi();
    }

    private void disableImageView(ImageView v, boolean disable, int disableImageId, int normalId) {
        v.setEnabled(!disable);
        v.setImageResource(disable ? disableImageId : normalId);
    }

    @Override
    public void onSeek(float progress) {
        if (mIsInitPlay) {
            initPlay();
            mIsInitPlay = false;
        } else {
            mSoundBinder.seekTo(Math.round(progress * mCurrentTrack.getDuration()));
        }
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (t != null && t.size() > 0) {
                    if (mCurrentAlbumType == TYPE_ALBUM_CURRENT) {
                        mCurrentAlbum = t.get(0);
                        updateAlbumUi(mCurrentAlbum);
                        String[] ids = mCurrentAlbum.getSounds().split(",");
                        for (String id : ids) {
                            mTrackIds.add(Integer.valueOf(id));
                        }
                        loadTrack(mTrackCount);
                        loadZhubo(mCurrentAlbum.getZhuboId());
                    } else if (mCurrentAlbumType == TYPE_ALBUM_ZHUBO) {

                        mZhuboProgressBar.setVisibility(View.GONE);
                        for (SoundItemModel soundItemModel : t) {
                            SoundAlbumItemModel model = new SoundAlbumItemModel(soundItemModel);
                            mZhuboAlbumAdapter.add(model);
                        }
                    }

                } else {
                    if (mCurrentAlbumType == TYPE_ALBUM_ZHUBO) {
                        mZhuboProgressBar.setVisibility(View.GONE);
                    }
                }
            }
        });

    }


    private void updateAlbumUi(final SoundItemModel model) {
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
        disableImageView(mBack15, true, R.drawable.ic_sound_back_15_gray,
                R.drawable.sel_sound_back_15);
        disableImageView(mForward15, true, R.drawable.ic_sound_forward_15_gray,
                R.drawable.sel_sound_forward_15);
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
        mSeekBar.stop();
        mSeekBar.setProgressSweepAngle(0);
        mPlay.setImageResource(R.drawable.ic_sound_play);
        disableImageView(mBack15, true, R.drawable.ic_sound_back_15_gray,
                R.drawable.sel_sound_back_15);
        disableImageView(mForward15, true, R.drawable.ic_sound_forward_15_gray,
                R.drawable.sel_sound_forward_15);

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
                    mTrackModels.addAll(models);
                    disableImageView(mNext, false, R.drawable.ic_sound_next_gray,
                            R.drawable.sel_sound_next);
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
    public void onTrackClick(SoundTrackModel data, int position) {
        mCurrentTrackIndex = position;
        if (!mCurrentTrack.equals(data)) {
            mCurrentTrack = data;
            mIsInitPlay = true;
            if (mSoundBinder.isPlaying())
                mSoundBinder.pause();
            playOrPause();
        } else {
            playOrPause();
        }

    }

    @Override
    public void onLoadMore() {
        loadTrack(mTrackCount);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        mSeekBar.setSecondProgressSweepAngle((int) (mSeekBar.getTotalDegree() * percent / 100));
        if (percent == 100) {
            mPlayerProgressBar.setVisibility(View.GONE);
            mPlayerOverEnd.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPlayerPlay() {
        mSeekBar.start();
        mPlay.setImageResource(R.drawable.ic_sound_play_stop);
        disableImageView(mBack15, false, R.drawable.ic_sound_back_15_gray,
                R.drawable.sel_sound_back_15);
        disableImageView(mForward15, false, R.drawable.ic_sound_forward_15_gray,
                R.drawable.sel_sound_forward_15);
        updateMusicBar(false);

        if (TYPE_TIME_CURRENT <= mCurrentTimeType && mCurrentTimeType <= TYPE_TIME_3)
            updateRestTime();
    }

    @Override
    public void onPlayerPause() {
        mSeekBar.stop();
        mPlay.setImageResource(R.drawable.ic_sound_play);
        updateMusicBar(true);
    }

    @Override
    public void onSeekDone() {
        float degree = mSoundBinder.getPlayedDuration() * 1.0f / mSoundBinder.getDuration() * mSeekBar.getTotalDegree();
        mSeekBar.setProgressSweepAngle((int) degree);
        if (TYPE_TIME_CURRENT <= mCurrentTimeType && mCurrentTimeType <= TYPE_TIME_3)
            updateTimeText();
    }

    @Override
    public void onError(String error) {
        SnackbarUtil.showNetPrompt(this, error);
    }

    @Override
    public void onCompletion() {
        mSeekBar.stop();
        mPlay.setImageResource(R.drawable.ic_sound_play);
        if (mCurrentOrderType == TYPE_ORDER_ORDER) {
            playOrderNext();
        } else if (mCurrentOrderType == TYPE_ORDER_REPEAT) {
            if (mIsInitPlay) {
                initPlay();
            } else {
                mSoundBinder.seekTo(0);
            }
        } else {
            playRandom();
        }
    }

    private void playRandom() {
        mCurrentTrackIndex = (int) (Math.random() * mTrackModels.size());
        if (mCurrentTrackIndex == mTrackModels.size() - 1)
            loadTrack(mTrackCount);
        initPlay();
    }

    private void playOrderNext() {
        ++mCurrentTrackIndex;
        if (mCurrentTrackIndex == mTrackModels.size() - 1) {
            disableImageView(mNext, true, R.drawable.ic_sound_next_gray, R.drawable.sel_sound_next);
            loadTrack(mTrackCount);
        }
        initPlay();
        disableImageView(mPrevious, false, R.drawable.ic_sound_previous_gray,
                R.drawable.sel_sound_previous);

    }

    private void playOrderPrevious() {
        --mCurrentTrackIndex;
        if (mCurrentTrackIndex < 0) {
            mCurrentTrackIndex = 0;
            disableImageView(mPrevious, true, R.drawable.ic_sound_previous_gray,
                    R.drawable.sel_sound_previous);
            SnackbarUtil.showPrompt(this, getString(R.string.label_sound_play_first),
                    SnackbarUtil.TOP_TO_DOWN);
        } else {
            initPlay();
        }
    }

    private void showTimeDialog() {
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        RelativeLayout root = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.bottom_dialog_sound_time,
                null, false);
        RadioGroup group = root.findViewById(R.id.rg_sound_time);
        if (mCurrentOrderType == TYPE_ORDER_RANDOM) {
            group.removeViews(2, 2);
        }
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int type;
                if (checkedId == R.id.rb_sound_time_close)
                    type = TYPE_TIME_CLOSE;
                else if (checkedId == R.id.rb_sound_time_current)
                    type = TYPE_TIME_CURRENT;
                else if (checkedId == R.id.rb_sound_time_2)
                    type = TYPE_TIME_2;
                else if (checkedId == R.id.rb_sound_time_3)
                    type = TYPE_TIME_3;
                else if (checkedId == R.id.rb_sound_time_10)
                    type = TYPE_TIME_MINUTE_10;
                else if (checkedId == R.id.rb_sound_time_20)
                    type = TYPE_TIME_MINUTE_20;
                else if (checkedId == R.id.rb_sound_time_30)
                    type = TYPE_TIME_MINUTE_30;
                else if (checkedId == R.id.rb_sound_time_60)
                    type = TYPE_TIME_MINUTE_60;
                else
                    type = TYPE_TIME_MINUTE_90;
                mCurrentTimeCheckId = checkedId;
                setCurrentTimeType(type);
                dialog.dismiss();
            }
        });
        group.check(mCurrentTimeCheckId);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mCurrentTimeType != mLastTimeType) {
                    updateTimeText();
                }
            }
        });
        dialog.setContentView(root);
        dialog.show();
    }

    private void getRestTime() {
        switch (mCurrentTimeType) {
            case TYPE_TIME_CLOSE:
                mRestTime = 0;
                break;
            case TYPE_TIME_CURRENT:
                mRestTime = getRestTimeByTracks(0);
                break;
            case TYPE_TIME_2:
                mRestTime = getRestTimeByTracks(1);
                break;
            case TYPE_TIME_3:
                mRestTime = getRestTimeByTracks(2);
                break;
            case TYPE_TIME_MINUTE_10:
                mRestTime = 600;
                break;
            case TYPE_TIME_MINUTE_20:
                mRestTime = 1200;
                break;
            case TYPE_TIME_MINUTE_30:
                mRestTime = 1800;
                break;
            case TYPE_TIME_MINUTE_60:
                mRestTime = 3600;
                break;
            case TYPE_TIME_MINUTE_90:
                mRestTime = 5400;
                break;
            default:
                break;
        }
    }

    private int getRestTimeByTracks(int restCount) {
        int played = mIsInitPlay ? 0 : mSoundBinder.getPlayedDuration();
        int currentRestTime = mCurrentTrack.getDuration() - played;
        if (restCount == 0)
            return currentRestTime;
        else {
            if (mCurrentOrderType == TYPE_ORDER_REPEAT)
                return currentRestTime + mCurrentTrack.getDuration() * restCount;
            else {
                int time = 0;
                for (int i = 0; i < restCount; i++) {
                    if (mCurrentTrackIndex + i + 1 < mTrackModels.size()) {
                        time += mTrackModels.get(mCurrentTrackIndex + i + 1).getDuration();
                    }
                }
                return time + currentRestTime;
            }
        }
    }

    private void updateRestTime() {
        if (mCurrentTimeType != TYPE_TIME_CLOSE) {
            if (!mTimeText.isShown())
                mTimeText.setVisibility(View.VISIBLE);
            mHandler.removeMessages(MSG_UPDATE_REST_TIME);
            mHandler.sendEmptyMessage(MSG_UPDATE_REST_TIME);
        } else {
            if (mTimeText.isShown())
                mTimeText.setVisibility(View.GONE);
        }

    }

    private void updateTimeText() {
        getRestTime();
        updateRestTime();
    }


    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        unbindService(mConnection);
        super.onDestroy();
    }
}
