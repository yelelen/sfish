package com.yelelen.sfish.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.yelelen.sfish.R;
import com.yelelen.sfish.utils.Utils;


/**
 * Created by yelelen on 17-10-15.
 */

public class CircleSeekBar extends View {
    private int mRadius;

    private int mProgressColor;
    private int mSecondProgressColor;
    private int mBackgroundProgressColor;
    private int mTextDurationColor;

    private int mProgressAlpha;
    private int mSecondProgressAlpha;
    private int mBackgroundProgressAlpha;

    private int mProgressWidth;
    private int mSecondProgressWith;
    private int mBackgroundProgressWith;

    private int mIndicatorImage;
    private int mIndicatorImagePress;
    private int mIndicatorAlpha;

    private Paint mProgressPaint;
    private Paint mSecondProgressPaint;
    private Paint mBackgroundProgressPaint;
    private Paint mIndicatorPaint;
    private Paint mTextPaint;

    private int mWidth;
    private int mHeight;
    private RectF mRectF;
    // 以3点钟方向为0度
    private float mStartAngle;
    private PointF mStartPointXY;
    private float mProgressSweepAngle;
    private float mSecondProgressSweepAngle;
    private float mTotalDegree;
    private int mTotalDuration;
    private int mHalfWidth;

    private int maxPaddingWidth;
    private Bitmap mIndicator;
    private Bitmap mIndicatorPress;
    private Bitmap mIndicatorNormal;
    private OnStateChangeListener mListener;

    private boolean mIsStart = false;
    private static final int MSG_START = 10;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MSG_START) {
                mProgressSweepAngle += mTotalDegree / mTotalDuration;
                invalidate();
                if (mIsStart)
                    mHandler.sendEmptyMessageDelayed(MSG_START, 1000);
            }
            return true;
        }
    });

    public CircleSeekBar(Context context) {
        this(context, null);
    }

    public CircleSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleSeekBar);

//      设置的颜色不能带透明度，否则会卡死UI，可以设置进度条的透明度达到效果,原因暂时不明
        mProgressColor = getResources().getColor(R.color.colorPrimary);
//        mSecondProgressColor = Color.CYAN;
        mSecondProgressColor = mProgressColor;
        mBackgroundProgressColor = Color.parseColor("#e6e6e6");
//        mBackgroundProgressColor = Color.parseColor("#7fe6e6e6");
        mTextDurationColor = getResources().getColor(R.color.textSecond);

        mProgressAlpha = 255;
        mSecondProgressAlpha = 96;
        mBackgroundProgressAlpha = 96;

        mProgressWidth = Utils.dp2px(context, 1);
        mSecondProgressWith = Utils.dp2px(context, 1);
        mBackgroundProgressWith = Utils.dp2px(context, 1);

        mIndicatorImage = R.mipmap.snail;
        mIndicatorImagePress = R.mipmap.snail_press;
        mIndicatorAlpha = 255;
        mTotalDuration = 120;

        mTextDurationColor = a.getColor(R.styleable.CircleSeekBar_cs_durationColor, mTextDurationColor);
        mProgressColor = a.getColor(R.styleable.CircleSeekBar_cs_progressColor, mProgressColor);
        mSecondProgressColor = a.getColor(R.styleable.CircleSeekBar_cs_secondProgressColor, mSecondProgressColor);
        mBackgroundProgressColor = a.getColor(R.styleable.CircleSeekBar_cs_backgroundProgressColor, mBackgroundProgressColor);

        mProgressWidth = (int) a.getDimension(R.styleable.CircleSeekBar_cs_progressBarWidth, mProgressWidth);
        mSecondProgressWith = (int) a.getDimension(R.styleable.CircleSeekBar_cs_secondProgressBarWidth, mSecondProgressWith);
        mBackgroundProgressWith = (int) a.getDimension(R.styleable.CircleSeekBar_cs_backgroundProgressBarWidth,
                mBackgroundProgressWith);

        mProgressAlpha = a.getInteger(R.styleable.CircleSeekBar_cs_progressAlpha, mProgressAlpha);
        mSecondProgressAlpha = a.getInteger(R.styleable.CircleSeekBar_cs_secondProgressAlpha, mSecondProgressAlpha);
        mBackgroundProgressAlpha = a.getInteger(R.styleable.CircleSeekBar_cs_backgroundProgressAlpha,
                mBackgroundProgressAlpha);

        mIndicatorImage = a.getInteger(R.styleable.CircleSeekBar_cs_indicatorImageSrc, mIndicatorImage);
        mIndicatorImagePress = a.getInteger(R.styleable.CircleSeekBar_cs_indicatorImagePressSrc, mIndicatorImagePress);
        mIndicatorAlpha = a.getInteger(R.styleable.CircleSeekBar_cs_indicatorAlpha, mIndicatorAlpha);
        mTotalDegree = a.getFloat(R.styleable.CircleSeekBar_cs_totalDegree, mTotalDegree);
        mTotalDuration = a.getInt(R.styleable.CircleSeekBar_cs_totalDuration, mTotalDuration);
        mStartAngle = a.getFloat(R.styleable.CircleSeekBar_cs_startAngle, mStartAngle);

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mProgressPaint.setColor(mProgressColor);
        mProgressPaint.setAlpha(mProgressAlpha);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(mProgressWidth);

        mSecondProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mSecondProgressPaint.setColor(mSecondProgressColor);
        mSecondProgressPaint.setAlpha(mSecondProgressAlpha);
        mSecondProgressPaint.setStyle(Paint.Style.STROKE);
        mSecondProgressPaint.setStrokeWidth(mSecondProgressWith);

        mBackgroundProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mBackgroundProgressPaint.setColor(mBackgroundProgressColor);
        mBackgroundProgressPaint.setAlpha(mBackgroundProgressAlpha);
        mBackgroundProgressPaint.setStyle(Paint.Style.STROKE);
        mBackgroundProgressPaint.setStrokeWidth(mBackgroundProgressWith);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mTextPaint.setColor(mTextDurationColor);
        mTextPaint.setTextSize(Utils.sp2px(getContext(), 10));

        mIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

        mIndicatorNormal = BitmapFactory.decodeResource(getResources(), mIndicatorImage);
        mIndicatorPress = BitmapFactory.decodeResource(getResources(), mIndicatorImagePress);
        mIndicator = mIndicatorNormal;

        mStartAngle = 135;
        mProgressSweepAngle = 0;
        mSecondProgressSweepAngle = 0;
        mTotalDegree = 270;

        mWidth = Utils.dp2px(context, 200);
        mHeight = Utils.dp2px(context, 200);
        mRectF = new RectF();

        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY)
            mWidth = width;
        if (heightMode == MeasureSpec.EXACTLY)
            mHeight = height;

        mWidth = Math.min(mWidth, mHeight);
        mHalfWidth = mWidth >> 1;
        int maxIndicatorWidth = Math.max(mIndicatorNormal.getWidth(), mIndicatorPress.getWidth());
        int maxProgressWidth = Math.max(mProgressWidth, Math.max(mSecondProgressWith, mBackgroundProgressWith));
        maxPaddingWidth = Math.max(maxIndicatorWidth, maxProgressWidth);
        mRadius = mHalfWidth - maxPaddingWidth;
        mRectF.set(-mRadius, -mRadius, mRadius, mRadius);
        mStartPointXY = getPointXY(mStartAngle);
        setMeasuredDimension(mWidth, mWidth);
    }

    private void getFinalWidthAndRadius() {
        mWidth = Math.min(mWidth, mHeight);
        mHalfWidth = mWidth >> 1;
        int maxIndicatorWidth = Math.max(mIndicatorNormal.getWidth(), mIndicatorPress.getWidth());
        int maxProgressWidth = Math.max(mProgressWidth, Math.max(mSecondProgressWith, mBackgroundProgressWith));
        maxPaddingWidth = Math.max(maxIndicatorWidth, maxProgressWidth);
        mRadius = mHalfWidth - maxPaddingWidth;
        mRectF.set(-mRadius, -mRadius, mRadius, mRadius);
        mStartPointXY = getPointXY(mStartAngle);
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mSecondProgressSweepAngle > mTotalDegree)
            mSecondProgressSweepAngle = mTotalDegree;
        if (mProgressSweepAngle > mTotalDegree) {
            mProgressSweepAngle = mTotalDegree;
            mIsStart = false;
            if (mListener != null)
                mListener.onEnd();
        }

        canvas.translate(mHalfWidth, mHalfWidth);
        canvas.drawArc(mRectF, mStartAngle, mTotalDegree, false, mBackgroundProgressPaint);
        canvas.drawArc(mRectF, mStartAngle, mSecondProgressSweepAngle, false, mSecondProgressPaint);
        canvas.drawArc(mRectF, mStartAngle, mProgressSweepAngle, false, mProgressPaint);
        int[] xy = getIndicatorXY(mStartAngle, mProgressSweepAngle);
        float x = xy[0] - (mIndicator.getWidth() >> 1);
        float y = xy[1] - (mIndicator.getHeight() >> 1);
        canvas.drawBitmap(mIndicator, x, y, mIndicatorPaint);

        int progress = Math.round((mProgressSweepAngle / mTotalDegree) * mTotalDuration);
        String start = getDurationText(progress);
        String end = getDurationText(mTotalDuration);
        float startX = (mStartPointXY.x - mTextPaint.measureText(start) / 2) - mHalfWidth;
        float startY = mStartPointXY.y + Utils.dp2px(getContext(), 16) - mHalfWidth;
        float endX = ( -startX - mTextPaint.measureText(end));
        float endY = (mStartPointXY.y + Utils.dp2px(getContext(), 16)) - mHalfWidth;
        canvas.drawText(start, startX, startY, mTextPaint);
        canvas.drawText(end, endX, endY, mTextPaint);

//        if (mIsStart) {
//            mProgressSweepAngle += mTotalDegree / mTotalDuration;
//            postInvalidateDelayed(1000);
//        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (isRightPos(x, y)) {
                    // 由于atan2返回的值为[-pi,pi],因此需要将弧度值转换一下，使得区间为[0,2*pi]
                    int degree;
                    double radian = Math.atan2(y - mHalfWidth, x - mHalfWidth);
                    if (radian < 0) {
                        radian = radian + 2 * Math.PI;
                        degree = (int) (Math.toDegrees(radian));
                        mProgressSweepAngle = degree - mStartAngle;
                    } else if (radian >= 0 && radian <= Math.toRadians(mStartAngle)) {
                        degree = (int) (Math.toDegrees(radian));
                        mProgressSweepAngle = 360 - mStartAngle + degree;
                    } else {
                        mProgressSweepAngle = (int) (Math.toDegrees(radian) - mStartAngle);
                    }

                    if (mProgressSweepAngle > mTotalDegree)
                        mProgressSweepAngle = 0;

                    if (mListener != null)
                        mListener.onSeek((int) (mProgressSweepAngle / mTotalDegree * 100));
                    mIndicator = mIndicatorPress;

                    if (!mIsStart) {
                        mIsStart = true;
                        mHandler.sendEmptyMessage(MSG_START);
                        if (mListener != null) {
                            mListener.onBegin();
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mIndicator = mIndicatorNormal;
                break;

        }

        return true;
    }

    private boolean isRightPos(float x, float y) {
        if (y > mStartPointXY.y) {
            return false;
        }
        float distance = Utils.twoPointDistance(new PointF(x, y), new PointF(mHalfWidth, mHalfWidth));
        return (mRadius - maxPaddingWidth / 2 <= distance && distance <= mRadius + maxPaddingWidth / 2);
    }

    private boolean isOnIndicator(float x, float y) {
        int[] indicator = getIndicatorXY(mStartAngle, mProgressSweepAngle);
        int ix = indicator[0] + mHalfWidth;
        int iy = indicator[1] + mHalfWidth;
        if (x >= (ix - maxPaddingWidth) && x <= (ix + maxPaddingWidth)) {
            if (y >= (iy - maxPaddingWidth) && y <= (iy + maxPaddingWidth))
                return true;
        }
        return false;
    }

    public OnStateChangeListener getListener() {
        return mListener;
    }

    public void setListener(OnStateChangeListener listener) {
        mListener = listener;
    }

    public interface OnStateChangeListener {
        void onSeek(int progress);
        void onBegin();
        void onEnd();
    }

    private PointF getPointXY(float startAngle) {
        int[] f = getIndicatorXY(0, mStartAngle);
        PointF pointF = new PointF(f[0] + mHalfWidth, f[1] + mHalfWidth);
        return pointF;
    }

    private int[] getIndicatorXY(float startAngle, float progressSweepAngle) {
        float totalAngle = startAngle + progressSweepAngle;
        int x, y;
        float degree;
        if (totalAngle >= 90 && totalAngle <= 180) {
            degree = 180 - totalAngle;
            x = (int) (-mRadius * Math.cos(Math.toRadians(degree)));
            y = (int) (mRadius * Math.sin(Math.toRadians(degree)));
        } else if (totalAngle > 180 && totalAngle <= 270) {
            degree = totalAngle - 180;
            x = (int) (-mRadius * Math.cos(Math.toRadians(degree)));
            y = (int) (-mRadius * Math.sin(Math.toRadians(degree)));
        } else if (totalAngle > 270 && totalAngle <= 360) {
            degree = 360 - totalAngle;
            x = (int) (mRadius * Math.cos(Math.toRadians(degree)));
            y = (int) (-mRadius * Math.sin(Math.toRadians(degree)));
        } else {
            degree = totalAngle;
            x = (int) (mRadius * Math.cos(Math.toRadians(degree)));
            y = (int) (mRadius * Math.sin(Math.toRadians(degree)));
        }

        int[] xy = new int[2];
        xy[0] = x;
        xy[1] = y;
        return xy;
    }

    private String getDurationText(int second) {
        int m = second / 60;
        int s = second % 60;
        String str = s < 10 ? ("0" + String.valueOf(s)) : String.valueOf(s);
        if (second < 0) {
            return "0:00";
        } else if (second >= 0 && second < 60) {
            return "0:" + str;
        } else {
            return String.valueOf(m) + ":" + str;
        }


    }

    public boolean isStart() {
        return mIsStart;
    }

    public void start() {
        mIsStart = true;
        mHandler.sendEmptyMessage(MSG_START);
        if (mListener != null) {
            mListener.onBegin();
        }
    }

    public void stop() {
        mIsStart = false;
    }

    public int getTextDurationColor() {
        return mTextDurationColor;
    }

    public void setTextDurationColor(int textDurationColor) {
        mTextDurationColor = textDurationColor;
        mTextPaint.setColor(mTextDurationColor);
    }

    public int getTotalDuration() {
        return mTotalDuration;
    }

    public void setTotalDuration(int totalDuration) {
        mTotalDuration = totalDuration;
        invalidate();
    }

    public int getProgressColor() {
        return mProgressColor;
    }

    public void setProgressColor(int progressColor) {
        mProgressColor = progressColor;
        mProgressPaint.setColor(mProgressColor);
    }

    public int getSecondProgressColor() {
        return mSecondProgressColor;
    }

    public void setSecondProgressColor(int secondProgressColor) {
        mSecondProgressColor = secondProgressColor;
        mSecondProgressPaint.setColor(mSecondProgressColor);
    }

    public int getBackgroundProgressColor() {
        return mBackgroundProgressColor;
    }

    public void setBackgroundProgressColor(int backgroundProgressColor) {
        mBackgroundProgressColor = backgroundProgressColor;
        mBackgroundProgressPaint.setColor(mBackgroundProgressColor);
    }

    public int getProgressAlpha() {
        return mProgressAlpha;
    }

    public void setProgressAlpha(int progressAlpha) {
        mProgressAlpha = progressAlpha;
        mProgressPaint.setAlpha(mProgressAlpha);
    }

    public int getSecondProgressAlpha() {
        return mSecondProgressAlpha;
    }

    public void setSecondProgressAlpha(int secondProgressAlpha) {
        mSecondProgressAlpha = secondProgressAlpha;
        mSecondProgressPaint.setAlpha(mSecondProgressAlpha);
    }

    public int getBackgroundProgressAlpha() {
        return mBackgroundProgressAlpha;
    }

    public void setBackgroundProgressAlpha(int backgroundProgressAlpha) {
        mBackgroundProgressAlpha = backgroundProgressAlpha;
        mBackgroundProgressPaint.setAlpha(mBackgroundProgressAlpha);
    }

    public int getProgressWidth() {
        return mProgressWidth;
    }

    public void setProgressWidth(int progressWidth) {
        mProgressWidth = progressWidth;
        mProgressPaint.setStrokeWidth(mProgressWidth);
        getFinalWidthAndRadius();

    }

    public int getSecondProgressWith() {
        return mSecondProgressWith;
    }

    public void setSecondProgressWith(int secondProgressWith) {
        mSecondProgressWith = secondProgressWith;
        mSecondProgressPaint.setStrokeWidth(mSecondProgressWith);
        getFinalWidthAndRadius();

    }

    public int getBackgroundProgressWith() {
        return mBackgroundProgressWith;
    }

    public void setBackgroundProgressWith(int backgroundProgressWith) {
        mBackgroundProgressWith = backgroundProgressWith;
        mBackgroundProgressPaint.setStrokeWidth(mBackgroundProgressWith);
        getFinalWidthAndRadius();

    }


    public int getIndicatorImage() {
        return mIndicatorImage;
    }

    public void setIndicatorImage(int indicatorImage) {
        mIndicatorImage = indicatorImage;
        mIndicatorNormal = BitmapFactory.decodeResource(getResources(), mIndicatorImage);
        getFinalWidthAndRadius();
    }

    public int getIndicatorImagePress() {
        return mIndicatorImagePress;
    }

    public void setIndicatorImagePress(int indicatorImagePress) {
        mIndicatorImagePress = indicatorImagePress;
        mIndicatorPress = BitmapFactory.decodeResource(getResources(), mIndicatorImagePress);
        getFinalWidthAndRadius();
    }

    public int getIndicatorAlpha() {
        return mIndicatorAlpha;
    }

    public void setIndicatorAlpha(int indicatorAlpha) {
        mIndicatorAlpha = indicatorAlpha;
        mIndicatorPaint.setAlpha(mIndicatorAlpha);
    }


    public void setWidth(int width) {
        mWidth = width;
        getFinalWidthAndRadius();
    }


    public void setHeight(int height) {
        mHeight = height;
        getFinalWidthAndRadius();
    }

    public float getStartAngle() {
        return mStartAngle;
    }

    public void setStartAngle(int startAngle) {
        mStartAngle = startAngle;
        if (mStartAngle >= 360)
            mStartAngle %= 360;
        mStartPointXY = getPointXY(mStartAngle);
    }

    public float getProgressSweepAngle() {
        return mProgressSweepAngle;
    }

    public void setProgressSweepAngle(int progressSweepAngle) {
        mProgressSweepAngle = progressSweepAngle;
        if (mProgressSweepAngle > mTotalDegree)
            mProgressSweepAngle = mTotalDegree;
    }

    public float getSecondProgressSweepAngle() {
        return mSecondProgressSweepAngle;
    }

    public void setSecondProgressSweepAngle(int secondProgressSweepAngle) {
        mSecondProgressSweepAngle = secondProgressSweepAngle;
        if (mSecondProgressSweepAngle > mTotalDegree)
            mSecondProgressSweepAngle = mTotalDegree;
    }

    public float getTotalDegree() {
        return mTotalDegree;
    }

    public void setTotalDegree(int totalDegree) {
        mTotalDegree = totalDegree;
        if (mTotalDegree > 360)
            mTotalDegree = 360;
    }
}
