package com.yelelen.sfish.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.yelelen.sfish.Model.SoundItemModel;
import com.yelelen.sfish.R;
import com.yelelen.sfish.adapter.SoundViewPagerAdapter;
import com.yelelen.sfish.contract.LoadContent;
import com.yelelen.sfish.presenter.SoundItemPresenter;
import com.yelelen.sfish.presenter.SoundTrackPresenter;
import com.yelelen.sfish.utils.BlurUtil;
import com.yelelen.sfish.view.CircleSeekBar;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SoundPlayActivity extends BaseActivity implements LoadContent<SoundItemModel>,
        CircleSeekBar.OnStateChangeListener, View.OnClickListener {
    private SoundItemModel mModel;
    private int mAlbumOrder;
    private String mAlbumCoverPath;
    private SoundTrackPresenter mTrackPresenter;
    private SoundItemPresenter mItemPresenter;
    private ViewPager mViewPager;
    private SoundViewPagerAdapter mViewPagerAdapter;
    private List<View> mViews;
    private FrameLayout mPlayer;
    private FrameLayout mZhubo;
    private FrameLayout mAlbum;

    private static final int ZHUBO = 0;
    private static final int PLAYER = 1;
    private static final int ALBUM = 2;
    private static final int MSG_COVER_ROTATE = 10;

    private CircleImageView mPlayCover;
    private CircleSeekBar mSeekBar;
    private LinearInterpolator mLinearInterpolator;

    private ImageView mBack;

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
        mBack = findViewById(R.id.sound_play_back);
        mBack.setOnClickListener(this);

        LayoutInflater inflater = getLayoutInflater();
        mZhubo = (FrameLayout) inflater.inflate(R.layout.vp_sound_zhubo, null, false);
        mPlayer = (FrameLayout) inflater.inflate(R.layout.vp_sound_play, null, false);
        mAlbum = (FrameLayout) inflater.inflate(R.layout.vp_sound_album, null, false);

        mPlayCover = mPlayer.findViewById(R.id.im_sound_play_cover);
        mSeekBar = mPlayer.findViewById(R.id.sound_play_seekbar);


        mSeekBar.setTotalDuration(450);
        mSeekBar.setListener(this);
        mSeekBar.start();
        mLinearInterpolator = new LinearInterpolator();

        mViews = new ArrayList<>();
        mViews.add(mZhubo);
        mViews.add(mPlayer);
        mViews.add(mAlbum);

    }

    @Override
    protected void initData() {
        super.initData();
        mItemPresenter = new SoundItemPresenter(this);
        mViewPagerAdapter = new SoundViewPagerAdapter(mViews);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setCurrentItem(PLAYER);

        mPlayCover.setImageBitmap(BitmapFactory.decodeFile(mAlbumCoverPath));
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

    }

    @Override
    public void onLoadFailed(String reason) {

    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
