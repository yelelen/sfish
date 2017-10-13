package com.yelelen.sfish.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.yelelen.sfish.R;
import com.yelelen.sfish.utils.Utils;

/**
 * Created by yelelen on 17-10-11.
 */

public class VoiceButtton extends View {
    private int mRadius;
    private int mBgColor;
    private int mRingColor;
    private int mImagePadding;
    private int mRingWidth;
    private int mWidth;
    private int mBgAlpha;
    private int mRingAlpha;
    private Paint mBgPaint;
    private Paint mRingPaint;
    private int mHalfWidth;
    private int mLevel = 0;
    private Listener mListener;
    private GestureDetector mGestureDetector;
    private int[] mBitmapIds = {R.mipmap.voice0, R.mipmap.voice1, R.mipmap.voice2, R.mipmap.voice3,
            R.mipmap.voice4, R.mipmap.voice};
    private Bitmap[] mBitmaps;
    private boolean mIsStart = false;

    public VoiceButtton(Context context) {
        this(context, null);
    }

    public VoiceButtton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoiceButtton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {

        mBgColor = Color.TRANSPARENT;
        mRingColor = getResources().getColor(R.color.colorPrimary);
        mImagePadding = Utils.dp2px(context, 8);
        mRingWidth = Utils.dp2px(context, 2);
        mWidth = Utils.dp2px(context, 192);
        mRingAlpha = 255;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VoiceButtton);
        mRingWidth = (int) a.getDimension(R.styleable.VoiceButtton_vb_ringWidth, mRingWidth);
        mImagePadding = (int) a.getDimension(R.styleable.VoiceButtton_vb_imagePadding, mImagePadding);
        mBgColor = a.getColor(R.styleable.VoiceButtton_vb_bgColor, mBgColor);
        mRingColor = a.getColor(R.styleable.VoiceButtton_vb_ringColor, mRingColor);
        mBgAlpha = a.getInt(R.styleable.VoiceButtton_vb_bgAlpha, 0);
        a.recycle();

        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPaint.setColor(mBgColor);
        mBgPaint.setAlpha(mBgAlpha);

        mRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setStrokeWidth(mRingWidth);
        mRingPaint.setColor(mRingColor);
        mRingPaint.setAlpha(mRingAlpha);

        setClickable(true);

        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (mListener != null && isRightClick(e))
                    mListener.onSingleClick();
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (mListener != null && isRightClick(e))
                    mListener.onDoubleClick();
                return true;
            }
        });

        mBitmaps = new Bitmap[mBitmapIds.length];
        for (int i = 0; i < mBitmapIds.length; i++)
            mBitmaps[i] = BitmapFactory.decodeResource(getResources(), mBitmapIds[i]);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int size = Math.min(width, height);
        if (widthMode == MeasureSpec.EXACTLY || heightMode == MeasureSpec.EXACTLY)
            mWidth = size;
        mRadius = mWidth / 6;
        mHalfWidth = mWidth / 2;
        setMeasuredDimension(mWidth, mWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(mHalfWidth, mHalfWidth, mRadius - mRingWidth * 2, mBgPaint);

        if (!mIsStart) {
            mRingPaint.setAlpha(255);
            canvas.drawCircle(mHalfWidth, mHalfWidth, mRadius - mRingWidth * 2, mRingPaint);
            canvas.drawBitmap(mBitmaps[5], mRadius * 2 + mImagePadding,
                    mRadius * 2 + mImagePadding, null);
        }

        else {
            canvas.drawBitmap(mBitmaps[mLevel], mRadius * 2 + mImagePadding,
                    mRadius * 2 + mImagePadding, null);
            for (int i = 1; i < mLevel; i++) {
                mRingPaint.setAlpha((int) (mRingAlpha / (i * 0.6 + 1)));
                canvas.drawCircle(mHalfWidth, mHalfWidth,
                        (float) (mRadius + i * i / Math.pow((mLevel - 1), 2) * 2 * mRadius), mRingPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    private boolean isRightClick(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        if (x > 2 * mRadius && x < 4 * mRadius)
            if (y > 2 * mRadius && y < 4 * mRadius)
                return true;
        return false;
    }

    public void setLevel(int level) {
        mLevel = level;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void start() {
        mIsStart = true;
        invalidate();
    }

    public void stop() {
        mIsStart = false;
        invalidate();
    }

    public interface Listener {
        void onSingleClick();

        void onDoubleClick();
    }
}
