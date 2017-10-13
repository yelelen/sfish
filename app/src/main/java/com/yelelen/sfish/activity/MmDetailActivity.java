package com.yelelen.sfish.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yelelen.sfish.App;
import com.yelelen.sfish.Model.MmItemModel;
import com.yelelen.sfish.R;
import com.yelelen.sfish.adapter.MmDetailSmallAdapter;
import com.yelelen.sfish.adapter.MmDetailAdapter;
import com.yelelen.sfish.adapter.RecyclerAdapter;
import com.yelelen.sfish.contract.DownloadImage;
import com.yelelen.sfish.contract.LoadContent;
import com.yelelen.sfish.contract.MatrixImageViewListener;
import com.yelelen.sfish.contract.NetworkListener;
import com.yelelen.sfish.helper.Contant;
import com.yelelen.sfish.presenter.MmDetailPresenter;
import com.yelelen.sfish.utils.Utils;
import com.yelelen.sfish.view.MmViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MmDetailActivity extends BaseActivity implements LoadContent<String>, View.OnClickListener,
        RecyclerAdapter.AdapterListener<String>, MatrixImageViewListener.MatrixImageViewCallback,
        DownloadImage, NetworkListener {

    private LinearLayout mTop;
    private LinearLayout mBottom;
    private ImageView mBack;
    private ImageView mDownload;
    private ImageView mSlideshow;
    private ImageView mList;
    private ImageView mRotate;
    private TextView mTitle;
    private TextView mTotalNum;
    private TextView mCurrentIndex;
    private RecyclerView mRecycler;
    private ViewPager mPager;
    private FrameLayout mFrameLayout;

    private MmDetailSmallAdapter mAdapter;
    private MmDetailPresenter mPresenter;
    private MmDetailAdapter mMmDetailAdapter;

    private List<String> mPaths;
    private MmItemModel mModel;

    private boolean isSlideshow = false;

    private Map<Integer, RotateImageListener> mRotateImageListenerMap;
    private static float mRotateDegree = 0;
    private static int mLastImageIndex = -1;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case Contant.MSG_SLIDESHOW:
                    slideShow((int) msg.obj);
                    break;
                default:
                    break;
            }

        }
    };

    private static MmDetailActivity mInstance;

    public Handler getHandler() {
        return mHandler;
    }

    @Override
    public void refresh() {
        if (App.getInstance().isNetworkConnected()) {
            mPresenter.startLoadForPager(getUrls(), App.mMmImageBasePath + Utils.getMD5(mModel.getUrl()));
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstance = this;
        mRotateImageListenerMap = new HashMap<>();
    }

    public static MmDetailActivity getInstance() {
        return mInstance;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_mm_detail;
    }

    @Override
    protected void initView() {
        super.initView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition exit = TransitionInflater.from(this)
                    .inflateTransition(R.transition.slide_start);
            setTransitions(exit, null, null);
        }

        mTop = (LinearLayout) findViewById(R.id.ll_top);
        mBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        mBack = (ImageView) findViewById(R.id.im_back);
        mDownload = (ImageView) findViewById(R.id.im_download);
        mSlideshow = (ImageView) findViewById(R.id.im_slideshow);
        mList = (ImageView) findViewById(R.id.im_list);
        mRotate = (ImageView) findViewById(R.id.im_rotate);
        mTitle = (TextView) findViewById(R.id.txt_title);
        mCurrentIndex = (TextView) findViewById(R.id.txt_index);

        mTotalNum = (TextView) findViewById(R.id.txt_total_num);
        mFrameLayout = (FrameLayout) findViewById(R.id.lay_container);

        mRecycler = new RecyclerView(this);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mPager = new MmViewPager(this);
//        mPager.setRotation(90); // 可以实现垂直滑动，需要跟里面的View配合
        mFrameLayout.addView(mPager);

    }

    @Override
    protected void initData() {
        super.initData();
        mPaths = new ArrayList<>();

        mPresenter = new MmDetailPresenter(this);

        mAdapter = new MmDetailSmallAdapter(this);
        mRecycler.setAdapter(mAdapter);
        mMmDetailAdapter = new MmDetailAdapter();
        mPager.setAdapter(mMmDetailAdapter);

        mPresenter.setListener(this);
        mPresenter.startLoadForPager(getUrls(), App.mMmImageBasePath + Utils.getMD5(mModel.getUrl()));
        mCurrentIndex.setText(String.valueOf(mPager.getCurrentItem() + 1));
        mTotalNum.setText(" / " + String.valueOf(mModel.getTotalNum()));

        mTitle.setText(mModel.getTitle());

    }

    @Override
    protected boolean initArgs() {
        mModel = getIntent().getBundleExtra("MM").getParcelable("MM");
        if (mModel == null)
            return false;
        return true;
    }

    @Override
    protected void initListener() {
        super.initListener();

        mBack.setOnClickListener(this);
        mDownload.setOnClickListener(this);
        mSlideshow.setOnClickListener(this);
        mList.setOnClickListener(this);
        mRotate.setOnClickListener(this);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentIndex.setText(String.valueOf(position + 1));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }



    @NonNull
    private List<String> getUrls() {
        List<String> urls = new ArrayList<>();
        String url = mModel.getUrl();
        String urlPrefix = url.substring(0, url.lastIndexOf("/"));
        for (int i = 0; i < mModel.getTotalNum(); i++) {
            urls.add(urlPrefix + File.separator + (i + 1) + ".jpg");
        }
        return urls;
    }

    @Override
    public void onDownloadDone(final String path) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mMmDetailAdapter == null)
                    return;
                mMmDetailAdapter.getImagePaths().add(path);
                mMmDetailAdapter.notifyDataSetChanged();
                mPaths.add(path);
            }

        });
    }

    @Override
    public void onDownloadFailed(final String reason) {
        if (mHandler == null)
            return;
        Message msg = Message.obtain();
        msg.what = Contant.MSG_MM_DOWNLOAD_IMAGE_FAILED;
        msg.obj = reason;
        mHandler.sendMessage(msg);
    }


    @Override
    public void onLoadDone(List<String> paths) {
        if (paths != null && paths.size() > 0) {
            for (String path : paths) {
                mMmDetailAdapter.getImagePaths().add(path);
                mMmDetailAdapter.notifyDataSetChanged();
                mPaths.add(path);
            }
        }
    }

    @Override
    public void onLoadFailed(String reason) {
        Utils.showToast(this, reason);
    }

    @Override
    public void onItemClick(RecyclerAdapter.BaseViewHolder holder, String path) {
        mFrameLayout.removeView(mRecycler);
        mFrameLayout.addView(mPager);
        int pos = holder.getAdapterPosition();
        mPager.setCurrentItem(pos);
        hideUiController();
    }

    @Override
    public void onItemLongClick(RecyclerAdapter.BaseViewHolder holder, String path) {
    }


    @Override
    public void onClick() {
        mTop.setVisibility(mTop.isShown() ? View.GONE : View.VISIBLE);
        mBottom.setVisibility(mBottom.isShown() ? View.GONE : View.VISIBLE);
        isSlideshow = false;
    }

    @Override
    public void onLongClick(int position) {
        mFrameLayout.removeView(mPager);
        mFrameLayout.addView(mRecycler);
        mAdapter.replace(mPaths);
        mRecycler.scrollToPosition(position);
        hideUiController();
    }

    private void hideUiController() {
        mTop.setVisibility(View.GONE);
        mBottom.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.im_back:
                finish();
                break;
            case R.id.im_download:
                File saveDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                if (saveDir != null) {
                    String imgPath = mMmDetailAdapter.getImagePaths().get(mPager.getCurrentItem());
                    String fileName = Utils.getMD5(imgPath);
                    fileName += ".jpg";
                    Message message = Message.obtain();
                    if (Utils.copyFile(imgPath, saveDir.getAbsolutePath() + File.separator + fileName)) {
                        message.what = Contant.MSG_MM_SAVE_IMAGE;
                        message.obj = "图片保存在： " + saveDir.getAbsolutePath();
                    } else {
                        message.what = Contant.MSG_MM_SAVE_IMAGE_FAILED;
                        message.obj = getString(R.string.label_mm_save_image_failed);
                    }
                    super.mHandler.sendMessage(message);
                }
                break;
            case R.id.im_slideshow:
                if (!isSlideshow) {
                    isSlideshow = true;
                    Message msg = Message.obtain();
                    msg.what = Contant.MSG_SLIDESHOW;
                    msg.obj = mPager.getCurrentItem();
                    mHandler.sendMessage(msg);
                } else {
                    isSlideshow = false;
                }
                break;
            case R.id.im_list:
                onLongClick(mPager.getCurrentItem());
                break;
            case R.id.im_rotate:
                if (mLastImageIndex != mPager.getCurrentItem()) {
                    mLastImageIndex = mPager.getCurrentItem();
                    mRotateDegree = 0;
                }
                if (mRotateDegree >= 4)
                    mRotateDegree = 0;
                mRotateImageListenerMap.get(mPager.getCurrentItem()).onRotate((++mRotateDegree % 4) * 90f);
                break;
            default:
                break;
        }
    }

    private void slideShow(int pos) {
        if (isSlideshow) {
            hideUiController();
            if (pos > mMmDetailAdapter.getImagePaths().size() - 1)
                pos = 0;
            mPager.setCurrentItem(pos);
            Message msg = Message.obtain();
            msg.what = Contant.MSG_SLIDESHOW;
            msg.obj = pos + 1;
            mHandler.sendMessageDelayed(msg, 5000);
        }
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        mMmDetailAdapter = null;
        mAdapter = null;
        super.onDestroy();
    }

    public void addRotateImageListener(int pos, RotateImageListener listener) {
        if (pos >=0 && listener != null) {
            if (mRotateImageListenerMap.get(pos) == null) {
                mRotateImageListenerMap.put(pos, listener);
            }
        }
    }

    public void removeRotateImageListener(int pos) {
        if (pos >=0) {
            if (mRotateImageListenerMap.get(pos) != null) {
                mRotateImageListenerMap.remove(pos);
            }
        }
    }

    public interface RotateImageListener{
        void onRotate(float degress);
    }
}
