package com.yelelen.sfish.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.yelelen.sfish.R;
import com.yelelen.sfish.utils.Utils;

/**
 * Created by yelelen on 17-10-3.
 */

public class CircleButton extends View {
    private int mRadius;
    private int mBgColor;
    private int mRingColor;
    private int mImagePadding;
    private int mRingWidth;
    private Drawable mImage;
    private int mWidth;
    private int mBgAlpha;
    private Paint mBgPaint;
    private Paint mRingPaint;
    private Paint mImagePaint;
    private Bitmap mBitmap;

    public CircleButton(Context context) {
        this(context, null);
    }

    public CircleButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mRadius = Utils.dp2px(context, 22);
        mBgColor = Color.WHITE;
        mRingColor = Color.WHITE;
        mImagePadding = Utils.dp2px(context, 2);
        mRingWidth = Utils.dp2px(context, 2);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleButton);
        mRadius = (int) a.getDimension(R.styleable.CircleButton_radius, mRadius);
        mRingWidth = (int) a.getDimension(R.styleable.CircleButton_ringWidth, mRingWidth);
        mWidth = mRadius * 2;
        mImagePadding = (int) a.getDimension(R.styleable.CircleButton_imagePadding, mImagePadding);
        mBgColor = a.getColor(R.styleable.CircleButton_bgColor, mBgColor);
        mRingColor = a.getColor(R.styleable.CircleButton_ringColor, mRingColor);
        mImage = a.getDrawable(R.styleable.CircleButton_imageSrc);
        mBgAlpha = a.getInt(R.styleable.CircleButton_bgAlpha, 128);
        a.recycle();

        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPaint.setColor(mBgColor);
        mBgPaint.setAlpha(mBgAlpha);

        mRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setStrokeWidth(mRingWidth);
        mRingPaint.setColor(mRingColor);

        if (mImage == null)
            mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_logo);
        else
            mBitmap = ((BitmapDrawable)mImage).getBitmap();
//
//        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
//        mImagePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
//        mImagePaint.setShader(bitmapShader);

        setClickable(true);
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
        setMeasuredDimension(mWidth, mWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(mWidth / 2, mWidth / 2, mRadius - mRingWidth, mRingPaint);
        canvas.drawCircle(mWidth / 2, mWidth / 2, mRadius - mRingWidth * 2 + 2, mBgPaint);
//        canvas.drawCircle(mWidth / 2, mWidth / 2, mRadius - mImagePadding - mRingWidth, mImagePaint);
        canvas.drawBitmap(mBitmap, mImagePadding, mImagePadding, null);
    }
}
