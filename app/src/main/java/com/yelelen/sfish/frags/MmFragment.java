package com.yelelen.sfish.frags;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.yelelen.sfish.App;
import com.yelelen.sfish.Model.MmItemModel;
import com.yelelen.sfish.R;
import com.yelelen.sfish.activity.MainActivity;
import com.yelelen.sfish.activity.MeActivity;
import com.yelelen.sfish.activity.MmDetailActivity;
import com.yelelen.sfish.adapter.MmAdapter;
import com.yelelen.sfish.adapter.MmPopupAdapter;
import com.yelelen.sfish.adapter.RecyclerAdapter;
import com.yelelen.sfish.contract.LoadContent;
import com.yelelen.sfish.contract.NetworkListener;
import com.yelelen.sfish.helper.Contant;
import com.yelelen.sfish.helper.KeyboardChangeHelper;
import com.yelelen.sfish.parser.VoiceParser;
import com.yelelen.sfish.presenter.MmPresenter;
import com.yelelen.sfish.utils.Utils;
import com.yelelen.sfish.view.CircleButton;
import com.yelelen.sfish.view.SearchBar;
import com.yelelen.sfish.view.VoiceButtton;

import java.util.List;


public class MmFragment extends BaseFragment implements LoadContent<MmItemModel>,
        RecyclerAdapter.AdapterListener<MmItemModel>, NetworkListener, View.OnClickListener,
        KeyboardChangeHelper.KeyBoardListener {
    private RecyclerView mRecyclerView;
    private MmAdapter mAdapter;
    private static MmPresenter mPresenter;
    private int mCount = 10;
    private int lastVisibleItem;
    private GridLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CircleButton mHome;
    private CircleButton mSearch;
    private CircleButton mLabel;
    private CircleButton mLeg;
    private CircleButton mBack;
    private CircleButton mForward;
    private CircleButton mVoice;
    private SearchBar mSearchBar;
    private EditText mSearchEditText;
    private boolean isMenuSee = false;
    private boolean isSearchBarSee = false;
    private static boolean isFirst = true;
    private static String mLabelText;
    private String mSuggestText;
    private KeyboardChangeHelper mKeyboardChangeHelper;
    private FrameLayout mLayout;
    private int mOriginBottomMargin;
    private PopupWindow mPopupWindow;
    private ImageView mPopupWait;
    private MmPopupAdapter mMmPopupAdapter;
    private View mPopupAnchor;

    private FrameLayout mVoiceLayout;
    private VoiceButtton mVoiceButtton;
    private TextView mVoiceText;

    private com.iflytek.cloud.SpeechRecognizer mSpeechRecognizer;
    private RecognizerListener mRecognizerListener;
    private SpeechSynthesizer mSpeechSynthesizer;
    private SynthesizerListener mSynthesizerListener;

    private static final int REFRESH = 100;
    private static final int LABEL = 101;
    private static final int SUGGEST = 102;
    private static int mCurDataType = REFRESH;
    private static int mLastDataType = REFRESH;

    private volatile static MmFragment mInstance;
    private boolean isVoiceLayoutSee = false;

    private MediaPlayer mMediaPlayer;

    public static MmFragment getInstance() {
        if (mInstance == null) {
            synchronized (MmFragment.class) {
                if (mInstance == null) {
                    mInstance = new MmFragment();
                }
            }
        }
        return mInstance;
    }

    @Override
    public void refresh() {
        if (App.getInstance().isNetworkConnected()) {
            if (mCurDataType == REFRESH) {
                mSwipeRefreshLayout.setRefreshing(true);
                mPresenter.loadLatestData(mCount);
            }
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            mHandler.sendEmptyMessage(Contant.MSG_NETWORK_UNAVAILABLE);
        }
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_mm;
    }

    @Override
    protected void initView(View root) {
        super.initView(root);
        mLayout = root.findViewById(R.id.lay_container);
        mRecyclerView = root.findViewById(R.id.mm_recyclerview);
        mLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mHome = root.findViewById(R.id.btn_home);

        mVoiceLayout = root.findViewById(R.id.voice_layout);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mVoiceLayout.getLayoutParams();
        params.width = (int) getResources().getDimension(R.dimen.search_bar_width);
        mVoiceLayout.setLayoutParams(params);
        mVoiceButtton = root.findViewById(R.id.voice_button);
        mVoiceText = root.findViewById(R.id.voice_text);
        mVoiceButtton.setListener(new VoiceButtton.Listener() {
            @Override
            public void onSingleClick() {
                if (!mSpeechRecognizer.isListening()) {
                    mVoiceText.setText(getResources().getString(R.string.label_voice_recording));
                    mSpeechRecognizer.startListening(mRecognizerListener);
                }

            }

            @Override
            public void onDoubleClick() {
                if (mSpeechRecognizer.isListening()) {
                    mSpeechRecognizer.stopListening();
                }
                if (isVoiceLayoutSee) {
                    isVoiceLayoutSee = false;
                }
                showOrHide(mHome, mVoiceLayout, 200, 0, null);
                recoveryLastDataType();
            }
        });


        mSwipeRefreshLayout = root.findViewById(R.id.swipe_fresh_layout);
        mSwipeRefreshLayout.setColorScheme(R.color.colorAccent, R.color.colorPrimary,
                R.color.textAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mCurDataType == REFRESH) {
                    mSwipeRefreshLayout.setRefreshing(true);
                    mPresenter.loadLatestData(mCount);
                } else {
                    mSwipeRefreshLayout.setRefreshing(false);
                }

            }
        });

        // 这句话是为了，第一次进入页面的时候显示加载进度条
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, Utils.dp2px(getActivity(), 20));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (lastVisibleItem == mAdapter.getItemCount() - 1) {
                        if (mCurDataType == REFRESH) {
                            mPresenter.loadMoreData(mCount);
                        }

                        if (mCurDataType == LABEL) {
                            mPresenter.loadLabelData(mCount, mLabelText);
                        }
                    }
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition();

                if (isMenuSee) {
                    stopAnimation();
                }

                if (isSearchBarSee) {
                    isSearchBarSee = false;
                    mSearchBar.collapse(40, 100);
                }

                if (isVoiceLayoutSee) {
                    isVoiceLayoutSee = false;
                    showOrHide(mHome, mVoiceLayout, 200, 0, null);
                    if (mSpeechRecognizer.isListening()) {
                        mSpeechRecognizer.stopListening();
                    }
                    recoveryLastDataType();
                }
            }
        });

        initMenu(root);
    }

    @Override
    protected void initData() {
        App.registerNetworkListener(this);
        mPresenter = new MmPresenter(this);
        mAdapter = new MmAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        setDataType(REFRESH);
        mPresenter.loadMoreData(mCount);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mLayout.getLayoutParams();
        mOriginBottomMargin = params.bottomMargin;

        initVoiceParamters();
    }


    private void initVoiceParamters() {
        mSpeechRecognizer = SpeechRecognizer.createRecognizer(getContext(), null);
        mSpeechRecognizer.setParameter(SpeechConstant.PARAMS, null);
////短信和日常用语：iat (默认)  视频：video  地图：poi  音乐：music
        mSpeechRecognizer.setParameter(SpeechConstant.DOMAIN, "iat");
//// 简体中文:"zh_cn", 美式英文:"en_us"
        mSpeechRecognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
////普通话：mandarin(默认)
////粤 语：cantonese
////四川话：lmz
////河南话：henanese<span style="font-family: Menlo;">     </span>
        mSpeechRecognizer.setParameter(SpeechConstant.ACCENT, "mandarin ");
//// 设置听写引擎 "cloud", "local","mixed"  在线  本地  混合
////本地的需要本地功能集成
        mSpeechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, "cloud");
//// 设置返回结果格式 听写会话支持json和plain
        mSpeechRecognizer.setParameter(SpeechConstant.RESULT_TYPE, "json");
////设置是否带标点符号 0表示不带标点，1则表示带标点。
//        mIat.setParameter(SpeechConstant.ASR_PTT, "0");
////只有设置这个属性为1时,VAD_BOS  VAD_EOS才会生效,且RecognizerListener.onVolumeChanged才有音量返回默认：1
        mSpeechRecognizer.setParameter(SpeechConstant.VAD_ENABLE, "1");
//// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理1000~10000
        mSpeechRecognizer.setParameter(SpeechConstant.VAD_BOS, "5000");
//// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音0~10000
        mSpeechRecognizer.setParameter(SpeechConstant.VAD_EOS, "2000");
//// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
//// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
////设置识别会话被中断时（如当前会话未结束就开启了新会话等），
////是否通 过RecognizerListener.onError(com.iflytek.cloud.SpeechError)回调ErrorCode.ERROR_INTERRUPT错误。
////默认false    [null,true,false]
//        mIat.setParameter(SpeechConstant.ASR_INTERRUPT_ERROR,"false");
////音频采样率  8000~16000  默认:16000
//        mIat.setParameter(SpeechConstant.SAMPLE_RATE,"16000");
////默认:麦克风(1)(MediaRecorder.AudioSource.MIC)
////在写音频流方式(-1)下，应用层通过writeAudio函数送入音频；
////在传文件路径方式（-2）下，SDK通过应用层设置的ASR_SOURCE_PATH值， 直接读取音频文件。目前仅在SpeechRecognizer中支持。
//        mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
////保存音频文件的路径   仅支持pcm和wav
//        mIat.setParameter(SpeechConstant.ASR_SOURCE_PATH, Environment.getExternalStorageDirectory().getAbsolutePath() + "test.wav");

        mRecognizerListener = new RecognizerListener() {
            @Override
            public void onVolumeChanged(int i, byte[] bytes) {
                Log.e("xxxx", "volume --> " + String.valueOf(i));
                mVoiceButtton.setLevel(i / 6);
                mVoiceButtton.start();
            }

            @Override
            public void onBeginOfSpeech() {
            }

            @Override
            public void onEndOfSpeech() {
                mVoiceButtton.stop();
                mVoiceText.setText(getResources().getString(R.string.label_voice_again));
                if (mMediaPlayer == null) {
                    mMediaPlayer = MediaPlayer.create(getContext(), R.raw.voice_prompt);
                }
                if (!mMediaPlayer.isPlaying()) {
                    mMediaPlayer.start();
                }
            }

            @Override
            public void onResult(RecognizerResult recognizerResult, boolean b) {
                if (!b) {
                    String json = recognizerResult.getResultString();
                    String str = VoiceParser.parseIatResult(json);
                    if (!TextUtils.isEmpty(str)) {
                        setPopupAnchor(mVoiceLayout);
                        mVoiceText.setText(str);
                        mSuggestText = str;
                        mPresenter.loadSuggestData(mCount * 3, str);
                    }
                }
            }

            @Override
            public void onError(SpeechError speechError) {
                mVoiceText.setText(speechError.getErrorDescription());
                mSpeechSynthesizer.startSpeaking(speechError.getErrorDescription(), mSynthesizerListener);
            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {
            }
        };


        //1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
        mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(getContext(), null);

        /**
         2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
         *
         */

        // 清空参数
        mSpeechSynthesizer.setParameter(SpeechConstant.PARAMS, null);

        mSpeechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        mSpeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
        mSpeechSynthesizer.setParameter(SpeechConstant.SPEED, "50");//设置语速
        //设置合成音调
        mSpeechSynthesizer.setParameter(SpeechConstant.PITCH, "40");
        mSpeechSynthesizer.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
        mSpeechSynthesizer.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // 设置播放合成音频打断音乐播放，默认为true
        mSpeechSynthesizer.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//        boolean isSuccess = mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts2.wav");
//        Toast.makeText(MainActivity.this, "语音合成 保存音频到本地：\n" + isSuccess, Toast.LENGTH_LONG).show();

        mSynthesizerListener = new SynthesizerListener() {
            @Override
            public void onSpeakBegin() {

            }

            @Override
            public void onBufferProgress(int i, int i1, int i2, String s) {

            }

            @Override
            public void onSpeakPaused() {

            }

            @Override
            public void onSpeakResumed() {

            }

            @Override
            public void onSpeakProgress(int i, int i1, int i2) {

            }

            @Override
            public void onCompleted(SpeechError speechError) {
                if (speechError != null) {
                    mSearchEditText.setText(speechError.getErrorDescription());
                }

            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

            }
        };
    }

    private void initMenu(View root) {
        mSearch = root.findViewById(R.id.btn_search);
        mLabel = root.findViewById(R.id.btn_label);
        mLeg = root.findViewById(R.id.btn_leg);
        mBack = root.findViewById(R.id.btn_back);
        mForward = root.findViewById(R.id.btn_forward);
        mVoice = root.findViewById(R.id.btn_voice);
        mSearchBar = root.findViewById(R.id.searchbar);
        mSearchBar.setListener(new SearchBar.SearchBarListenerImpl() {
            @Override
            public void onExpandEnd() {
                super.onExpandEnd();
                mKeyboardChangeHelper = new KeyboardChangeHelper(getActivity());
                mKeyboardChangeHelper.setKeyBoardListener(MmFragment.this);
                isSearchBarSee = true;
                mSearchEditText.requestFocus();
                setDataType(SUGGEST);
            }


            @Override
            public void onCollapseStart() {
                super.onCollapseStart();
                isSearchBarSee = false;
                mKeyboardChangeHelper.destroy();
                mKeyboardChangeHelper = null;
                backOriginPos();
            }

            @Override
            public void onCollapseEnd() {
                super.onCollapseEnd();
                recoveryLastDataType();
                mSearchBar.setVisibility(View.INVISIBLE);
                if (mForward.isShown()) {
                    showOrHide(mHome, mForward, 200, 0, null);
                }
                if (mVoice.isShown() && !mVoiceLayout.isShown()) {
                    showOrHide(mHome, mVoice, 200, 0, null);
                }

            }
        });
        mSearchEditText = mSearchBar.getEditText();
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setPopupAnchor(mSearchBar);
                loadSuggestData(mCount * 3, s.toString());
                showVoiceOrForward();
            }
        });

        mSearch.setOnClickListener(this);
        mForward.setOnClickListener(this);
        mVoice.setOnClickListener(this);
        mLabel.setOnClickListener(this);
        mLeg.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mHome.setOnClickListener(this);
        mHome.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ((MainActivity) getActivity()).showActivity(MeActivity.class);
                return false;
            }
        });

    }


    @Override
    public void onLoadDone(final List<MmItemModel> models) {
        Log.e("ssss", Thread.currentThread().getName());

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int type = mCurDataType;
                if (models == null) {
                    if (type == REFRESH) {
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            mHandler.sendEmptyMessage(Contant.MSG_MM_REFRESH_NO_MORE);
                            return;
                        } else {
                            mHandler.sendEmptyMessage(Contant.MSG_MM_LOAD_NO_MORE);
                            return;
                        }

                    } else if (type == SUGGEST) {
                        showPopupWindow(true);
                        if (!App.getInstance().isNetworkConnected) {
                            mHandler.sendEmptyMessage(Contant.MSG_NETWORK_UNAVAILABLE);
                            return;
                        }
                        return;
                    } else if (type == LABEL) {
                        if (!App.getInstance().isNetworkConnected)
                            mHandler.sendEmptyMessage(Contant.MSG_NETWORK_UNAVAILABLE);
                        else
                            mHandler.sendEmptyMessage(Contant.MSG_MM_LOAD_NO_MORE);
                        return;
                    }

                }

                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    if (App.getInstance().isNetworkConnected()) {
                        for (int i = models.size() - 1; i >= 0; i--) {
                            mAdapter.addToFirst(models.get(i));
                        }
                        mRecyclerView.smoothScrollToPosition(0);
                    } else {
                        mHandler.sendEmptyMessage(Contant.MSG_NETWORK_UNAVAILABLE);
                        mPresenter.setLastIndex(mPresenter.getLastIndex() + models.size());
                    }
                    return;
                }

                if (type == SUGGEST) {
                    showPopupWindow(false);
                    handleItemTitle(models);
                    mMmPopupAdapter.replace(models);
                    return;
                }

                if (type == LABEL) {
                    mPresenter.saveLabelIndex(models, mLabelText);
                    if (isFirst)
                        mPresenter.setLabelStartIndex(models.size());
                }

                if (type == REFRESH) {
                    if (isFirst)
                        mPresenter.setLastIndex(mPresenter.getMaxOrder() - models.size() + 1);
                }

                if (isFirst) {
                    isFirst = false;
                    mAdapter.replace(models);
                    mRecyclerView.smoothScrollToPosition(0);
                } else {
                    mAdapter.add(models);
                }

            }
        });
    }

    @Override
    public void onLoadFailed(final String reason) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                mHandler.sendEmptyMessage(Contant.MSG_NETWORK_UNAVAILABLE);
            }
        });
    }


    private void handleItemTitle(List<MmItemModel> models) {
        if (models == null || models.size() <= 0)
            return;

        for (int j = 0; j < models.size(); j++) {
            MmItemModel model = models.get(j);
            String title = model.getTitle();
            String[] strs = title.split(mSuggestText);
            String html = "<font color='#d81e06'>" + mSuggestText + "</font>";
            StringBuilder builder = new StringBuilder();
            int i;
            for (i = 0; i < strs.length - 1; i++) {
                builder.append(strs[i]).append(html);
            }
            builder.append(strs[i]);
            if (strs.length == 1) {
                builder.append(html);
            }
            model.setTitle(builder.toString());
        }
    }

    private void setDataType(int type) {
        mLastDataType = mCurDataType;
        mCurDataType = type;
    }

    private void recoveryLastDataType() {
        setDataType(mLastDataType);
    }

    @Override
    public void onItemClick(RecyclerAdapter.BaseViewHolder holder, MmItemModel data) {
        data.setTitle(Html.fromHtml(data.getTitle()).toString());
        Bundle bundle = new Bundle();
        bundle.putParcelable("MM", data);
        ((MainActivity) getActivity()).showActivity(MmDetailActivity.class, "MM", bundle);
    }

    @Override
    public void onItemLongClick(RecyclerAdapter.BaseViewHolder holder, MmItemModel data) {
        mHome.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        mPresenter.unregisterDbObserver();
        App.unregisterNetworkListener(this);
        mSpeechRecognizer.destroy();
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying())
                mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_home:
                if (!isMenuSee) {
                    isMenuSee = true;
                    startMenuAnim();
                } else {
                    stopAnimation();
                }
                break;
            case R.id.btn_search:
                stopAnimation();
                startSearchAnim();
                break;
            case R.id.btn_label:
                showLabel();
                stopAnimation();
                break;
            case R.id.btn_voice:
                showVoiceSpeak();
                break;
            case R.id.btn_leg:
                loadDataByLabel(mCount, "美腿");
                stopAnimation();
                break;
            case R.id.btn_forward:
                if (isSearchBarSee) {
                    isSearchBarSee = false;
                    mSearchBar.collapse(40, 100);
                }
                showHome();
                break;
            case R.id.btn_back:
                loadHomeData(mCount);
                stopAnimation();
                break;
        }
    }

    private void showHome() {
        showOrHide(mHome, mForward, 200, 0, null);
    }

    private void showVoiceSpeak() {
        if (isSearchBarSee) {
            isSearchBarSee = false;
            mSearchBar.collapse(40, 100);
        }
        showOrHide(mVoiceLayout, mVoice, 200, 200, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startVoiceListening();
                isVoiceLayoutSee = true;
                mVoiceText.setText(getResources().getString(R.string.label_voice_recording));
                setDataType(SUGGEST);
            }
        });
    }

    private void startVoiceListening() {
        mVoiceText.setText(getResources().getString(R.string.label_voice_prompt));
        mSpeechRecognizer.startListening(mRecognizerListener);
    }

    private void showVoiceOrForward() {
        if (TextUtils.isEmpty(mSearchEditText.getText().toString())) {
            showOrHide(mVoice, mForward, 200, 0, null);
        } else {
            if (!mForward.isShown()) {
                mForward.setAlpha(1f);
                mForward.setVisibility(View.VISIBLE);
                mVoice.setVisibility(View.GONE);
            }
        }


    }


    private void showLabel() {
        MmLabelFragment.show(getChildFragmentManager());
    }

    private void startMenuAnim() {
        setChildMenuVisiable();
        int width = Utils.dp2px(getContext(), 40);
        int homeWidth = Utils.dp2px(getContext(), 44);
        int avgWidth = (mHome.getRight() - homeWidth - width * 4) / 5;
        int deltaX = width / 2 + homeWidth / 2;
        ObjectAnimator searchMove = ObjectAnimator.ofFloat(mSearch, "translationX",
                0, -(avgWidth + deltaX));
        ObjectAnimator labelMove = ObjectAnimator.ofFloat(mLabel, "translationX",
                0, -(avgWidth * 2 + width + deltaX));
        ObjectAnimator legMove = ObjectAnimator.ofFloat(mLeg, "translationX",
                0, -(avgWidth * 3 + 2 * width + deltaX));
        ObjectAnimator chestMove = ObjectAnimator.ofFloat(mBack, "translationX",
                0, -(avgWidth * 4 + width * 3 + deltaX));

        AnimatorSet set = new AnimatorSet();
        set.playTogether(searchMove, labelMove, legMove, chestMove);
        set.setDuration(300);
        set.start();

    }

    private void setChildMenuVisiable() {
        mSearch.setVisibility(View.VISIBLE);
        mBack.setVisibility(View.VISIBLE);
        mLeg.setVisibility(View.VISIBLE);
        mLabel.setVisibility(View.VISIBLE);
    }

    private void setChildMenuInvisiable() {
        mSearch.setVisibility(View.GONE);
        mBack.setVisibility(View.GONE);
        mLeg.setVisibility(View.GONE);
        mLabel.setVisibility(View.GONE);
    }

    private void stopAnimation() {
        int width = Utils.dp2px(getContext(), 40);
        int homeWidth = Utils.dp2px(getContext(), 44);
        int avgWidth = (mHome.getRight() - homeWidth - width * 4) / 5;
        int deltaX = width / 2 + homeWidth / 2;

        ObjectAnimator searchMove = ObjectAnimator.ofFloat(mSearch, "translationX",
                -(avgWidth + deltaX), 0);
        ObjectAnimator labelMove = ObjectAnimator.ofFloat(mLabel, "translationX",
                -(avgWidth * 2 + width + deltaX), 0);
        ObjectAnimator legMove = ObjectAnimator.ofFloat(mLeg, "translationX",
                -(avgWidth * 3 + 2 * width + deltaX), 0);
        ObjectAnimator chestMove = ObjectAnimator.ofFloat(mBack, "translationX",
                -(avgWidth * 4 + width * 3 + deltaX), 0);


        AnimatorSet set = new AnimatorSet();
        set.playTogether(searchMove, labelMove, legMove, chestMove);
        set.setDuration(300);
        set.start();

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setChildMenuInvisiable();
                isMenuSee = false;
            }
        });
    }

    public void startSearchAnim() {
        showOrHide(mVoice, mHome, 200, 300, new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mHome.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mSearchBar.setVisibility(View.VISIBLE);
                mSearchBar.expand(40, 100);
            }
        });

    }

    private void showOrHide(View show, final View hide, int duration, int startDelay,
                            AnimatorListenerAdapter listenerAdapter) {
        show.setVisibility(View.VISIBLE);
        show.setAlpha(0);
        ObjectAnimator invisible = ObjectAnimator.ofFloat(hide, "alpha", 1f, 0f);
        ObjectAnimator appear = ObjectAnimator.ofFloat(show, "alpha", 0f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(appear, invisible);
        set.setDuration(duration);

        if (listenerAdapter == null) {
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    hide.setVisibility(View.GONE);
                }
            });
        } else {
            set.addListener(listenerAdapter);
        }
        set.setStartDelay(startDelay);
        set.start();
    }

    public void loadDataByLabel(int count, String label) {
        mLabelText = label;
        isFirst = true;
        setDataType(LABEL);
        mPresenter.setLabelStartIndex(0);
        mPresenter.loadLabelData(count, label);
    }

    public void loadHomeData(int count) {
        isFirst = true;
        setDataType(REFRESH);
        mPresenter.setLastIndex(Contant.MAX_VALUE);
        mPresenter.loadMoreData(count);
    }

    public void loadSuggestData(int count, String s) {
        mSuggestText = s;
        mPresenter.loadSuggestData(count, s);
    }

    @Override
    public void onKeyboardChange(boolean isShow, int keyboardHeight) {

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mLayout.getLayoutParams();
        if (isShow) {
            params.bottomMargin = keyboardHeight;
        } else {
            params.bottomMargin = mOriginBottomMargin;
        }
        getActivity().onWindowFocusChanged(true);
        mLayout.setLayoutParams(params);
    }

    private void backOriginPos() {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mLayout.getLayoutParams();
        params.bottomMargin = mOriginBottomMargin;
        mLayout.setLayoutParams(params);
    }

    private void createPopupWindow() {
        mMmPopupAdapter = new MmPopupAdapter(this);
        View root = getLayoutInflater().inflate(R.layout.mm_popup, null);
        mPopupWait = root.findViewById(R.id.im_mm_popup_wait);
        RecyclerView recyclerView = root.findViewById(R.id.mm_popup_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mMmPopupAdapter);

        int width = mSearchBar.getWidth();
        int height = (int) getResources().getDimension(R.dimen.mm_popup_height);
        mPopupWindow = new PopupWindow(root, width, height);
        // 设置动画
        mPopupWindow.setAnimationStyle(R.style.popup_window_anim);
        // 设置背景颜色
//        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7f000000")));
        mPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_popup_bg));
        //  设置可以获取焦点
//        mPopupWindow.setFocusable(true);
        // 设置可以触摸弹出框以外的区域
        mPopupWindow.setOutsideTouchable(true);
        // 更新popupwindow的状态
        mPopupWindow.update();
        // TODO: 2016/5/17 以下拉的方式显示，并且可以设置显示的位置
//
    }


    private void setPopupAnchor(View anchor) {
        mPopupAnchor = anchor;
    }

    private void showPopupWindow(boolean showWait) {
        if (mPopupWindow == null) {
            createPopupWindow();
        }
        mPopupWait.setVisibility(showWait ? View.VISIBLE : View.GONE);
        mMmPopupAdapter.replace(null);
        if (mPopupAnchor != null) {
            if (mPopupAnchor instanceof FrameLayout) {
                mPopupWindow.setHeight((int) (getResources().getDimension(R.dimen.mm_popup_height) * 1.5f));
                mPopupWindow.showAsDropDown(mPopupAnchor, 0, Utils.dp2px(getContext(), 4));
                return;
            }

            if (mPopupAnchor instanceof SearchBar) {
                mPopupWindow.setHeight((int) getResources().getDimension(R.dimen.mm_popup_height));
            }
            mPopupWindow.showAsDropDown(mPopupAnchor);
        }

    }

}
