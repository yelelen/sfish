package com.yelelen.sfish.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.EditText;

import com.yelelen.sfish.R;
import com.yelelen.sfish.utils.Utils;


/**
 * Created by yelelen on 17-10-7.
 */

public class SearchBar extends ViewGroup {
    private int mRadius;
    private EditText mEditText;
    private int mWidth;
    private int mHeight;
    private int mTotalX;
    private int mDeltax = 0;
    private Paint mPaint;
    private static final int MSG_EXPAND = 0;
    private static final int MSG_COLLAPSE = 1;
    private int mExpandDelay;
    private int mCollapseDelay;
    private int mExpandStep;
    private int mCollapseStep;
    private String mPrompt = "";
    private boolean isFirst = true;
    private SearchBarListener mListener;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_EXPAND:
                    if (mDeltax > 0) {
                        mDeltax -= mExpandStep;
                        mHandler.sendEmptyMessageDelayed(MSG_EXPAND, mExpandDelay);
                        if (mListener != null)
                            mListener.onExpanding();
                    } else {
                        mDeltax = 0;
                        mEditText.setHint(mPrompt);
                        mEditText.setEnabled(true);
                        if (mListener != null)
                            mListener.onExpandEnd();
                    }
                    break;
                case MSG_COLLAPSE:
                    if (mDeltax < mTotalX) {
                        mDeltax += mCollapseStep;
                        mHandler.sendEmptyMessageDelayed(MSG_COLLAPSE, mCollapseDelay);
                        if (mListener != null)
                            mListener.onCollapsing();
                    } else {
                        mDeltax = mTotalX;
                        mEditText.setVisibility(GONE);
                        if (mListener != null)
                            mListener.onCollapseEnd();
                    }
                    break;
                default:
                    break;
            }
            requestLayout();
        }
    };

    public SearchBar(Context context) {
        this(context, null);
    }

    public SearchBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        int bgColor = Color.BLACK;
        int promptColor = context.getResources().getColor(R.color.textSecond);
        int textColor = context.getResources().getColor(R.color.searchbarTextColor);
        int bgAlpha = 128;
        mRadius = Utils.dp2px(context, 19);

        int textSize = Utils.sp2px(context, 6);
        mWidth = (int) getResources().getDimension(R.dimen.search_bar_width);
        mHeight = Utils.dp2px(context, 38);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SearchBar);
        bgColor = a.getColor(R.styleable.SearchBar_sb_bgColor, bgColor);
        textColor = a.getColor(R.styleable.SearchBar_sb_textColor, textColor);
        promptColor = a.getColor(R.styleable.SearchBar_sb_promptColor, promptColor);
        bgAlpha = a.getInt(R.styleable.SearchBar_sb_bgAlpha, bgAlpha);
        mRadius = (int) a.getDimension(R.styleable.SearchBar_sb_radius, mRadius);
        mPrompt = a.getString(R.styleable.SearchBar_sb_prompt);
        textSize = (int) a.getDimension(R.styleable.SearchBar_sb_textSize, textSize);

        mEditText = new EditText(context);
        mEditText.setHintTextColor(promptColor);
        mEditText.setMaxLines(1);
        mEditText.setTextColor(textColor);
        mEditText.setTextSize(textSize);
        addView(mEditText);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setColor(bgColor);
        mPaint.setAlpha(bgAlpha);

        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = width;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = height;
        }

//        Log.e("sss", "mTotalX -> " + mTotalX + "  mWidth -- > " + mWidth + "  mDeltaX --> " + mDeltax);

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(mWidth - mRadius * 2, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);

        measureChild(mEditText, widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        if (isFirst) {
            mTotalX = mWidth - mRadius * 2;
            mDeltax = mTotalX;
            isFirst = false;
        }
//        Log.e("onLayout --- > ", "mTotalX: " + mTotalX + "  mDeltaX: " + mDeltax);
        mEditText.layout(mRadius + mDeltax, 0, mWidth - mRadius * 2 -5, mHeight);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        RectF rectFLeft = new RectF(mDeltax, 0, mRadius * 2 + mDeltax, mHeight);
        canvas.drawArc(rectFLeft, 90, 180, true, mPaint);
        canvas.drawRect(mRadius + mDeltax, 0, mWidth - mRadius, mHeight, mPaint);
        RectF rectRight = new RectF(mWidth - mRadius  * 2, 0, mWidth, mHeight);
        canvas.drawArc(rectRight, 270, 180, true, mPaint);
        super.dispatchDraw(canvas);
    }

    public void expand(int step, int duration) {
        mExpandStep = step;
        mEditText.setEnabled(false);
        mEditText.setVisibility(VISIBLE);
        if (mListener != null)
            mListener.onExpandStart();
//        Log.e("ssss", "mTotalX ------->  " + mTotalX);
        mExpandDelay =duration / (mTotalX / step);
        mHandler.sendEmptyMessage(MSG_EXPAND);
    }

    public void collapse(int step, int duration) {
        mCollapseStep = step;
        mEditText.setEnabled(false);
        if (mListener != null)
            mListener.onCollapseStart();
        mCollapseDelay = duration / (mTotalX / step);
        mHandler.sendEmptyMessage(MSG_COLLAPSE);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isInArc(ev.getX());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    private boolean isInArc(float x) {
        if (x < mRadius || (x < mWidth && x > mWidth - mRadius))
            return true;
        return false;
    }

    public EditText getEditText() {
        return mEditText;
    }

    interface SearchBarListener{
        void onExpanding();
        void onCollapsing();
        void onExpandEnd();
        void onCollapseEnd();
        void onExpandStart();
        void onCollapseStart();
    }

    public static class SearchBarListenerImpl implements SearchBarListener{
        public void onExpanding() {}
        public void onCollapsing() {}
        public void onExpandEnd() {}
        public void onCollapseEnd() {}
        public void onExpandStart() {}
        public void onCollapseStart() {}
    }

    public void setListener(SearchBarListener listener) {
        mListener = listener;
    }
}
