package com.yelelen.sfish.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.yelelen.sfish.R;
import com.yelelen.sfish.utils.Utils;

/**
 * Created by yelelen on 17-10-16.
 */

public class MusicBar extends View {
    private int mBarColor;
    private int mBarDistance;
    private int mBarCount;
    private int mWidth;
    private int mHeight;
    private int mBarWidth;
    private int mRefreshDuration;
    private boolean mIsStart;
    private Paint mPaint;

    public MusicBar(Context context) {
        this(context, null);
    }

    public MusicBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mBarColor = getResources().getColor(R.color.colorPrimary);
        mBarDistance = Utils.dp2px(context, 2);
        mBarWidth = Utils.dp2px(context, 2);
        mBarCount = 4;
        mWidth = Utils.dp2px(context, 14);
        mHeight = Utils.dp2px(context, 24);
        mRefreshDuration = 150;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MusicBar);
        mBarColor = a.getColor(R.styleable.MusicBar_mb_barColor, mBarColor);
        mBarDistance = (int) a.getDimension(R.styleable.MusicBar_mb_barDistance, mBarDistance);
        mBarCount = a.getInteger(R.styleable.MusicBar_mb_barCount, mBarCount);
        mBarWidth = (int) a.getDimension(R.styleable.MusicBar_mb_barWidth, mBarWidth);
        mRefreshDuration = a.getInteger(R.styleable.MusicBar_mb_refresh_duration, mRefreshDuration);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setColor(mBarColor);

        mIsStart = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY)
            mWidth = width;
        if (heightMode == MeasureSpec.EXACTLY)
            mHeight = height;

        setMeasuredDimension(mWidth, mHeight);

        computeFinalBarWidth();
    }

    private void computeFinalBarWidth() {
        mBarWidth = (mWidth - getPaddingLeft() - getPaddingRight() - (mBarDistance * (mBarCount - 1))) / mBarCount;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < mBarCount; i++) {
            float left = i * (mBarWidth + mBarDistance);
            float top = makeRandomHeight();
            float right = left + mBarWidth;
            float bottom = mHeight;
            canvas.drawRect(left, top, right, bottom, mPaint);
        }
        if (mIsStart) {
            postInvalidateDelayed(mRefreshDuration);
        }
    }

    private float makeRandomHeight() {
        return (float) (Math.random() * mHeight);
    }

    public void start() {
        mIsStart = true;
        invalidate();
    }

    public void stop() {
        mIsStart = false;
    }

    public int getBarColor() {
        return mBarColor;
    }

    public void setBarColor(int barColor) {
        mBarColor = barColor;
        mPaint.setColor(mBarColor);
    }

    public int getBarDistance() {
        return mBarDistance;
    }

    public void setBarDistance(int barDistance) {
        mBarDistance = barDistance;
        computeFinalBarWidth();
    }

    public int getBarCount() {
        return mBarCount;
    }

    public void setBarCount(int barCount) {
        mBarCount = barCount;
        computeFinalBarWidth();
    }


    public void setWidth(int width) {
        mWidth = width;
        computeFinalBarWidth();
    }


    public void setHeight(int height) {
        mHeight = height;
    }

    public int getBarWidth() {
        return mBarWidth;
    }

    public void setBarWidth(int barWidth) {
        mBarWidth = barWidth;
        computeFinalBarWidth();
    }

    public int getRefreshDuration() {
        return mRefreshDuration;
    }

    public void setRefreshDuration(int refreshDuration) {
        mRefreshDuration = refreshDuration;
    }

    public boolean isStart() {
        return mIsStart;
    }

    public Paint getPaint() {
        return mPaint;
    }

    public void setPaint(Paint paint) {
        mPaint = paint;
    }
}
