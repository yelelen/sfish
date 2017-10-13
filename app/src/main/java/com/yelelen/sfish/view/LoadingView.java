package com.yelelen.sfish.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.yelelen.sfish.R;

/**
 * Created by yelelen on 17-9-14.
 */

public class LoadingView extends View {
    private int mWidth = 320;
    private int mHeight = 320;
    private Paint mPathPaint;
    private Paint mLogoPaint;
    private Path mPath;
    private Canvas mBitmapCanvas;
    private Bitmap mBitmap;
    private Bitmap mLogo;
    private int mCount = 100;

    private static final int MAX_PROGRESS = 100;
    private int mCurrentProgress = 0;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mBitmapCanvas = new Canvas(mBitmap);

        mLogoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPathPaint = new Paint();
        mPathPaint.setAntiAlias(true);
        mPathPaint.setColor(getResources().getColor(R.color.colorPrimary));
        mPathPaint.setDither(true);
        mPathPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        mPath = new Path();
        mLogo = BitmapFactory.decodeResource(getResources(), R.mipmap.logo);

        startAnimation();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth, mHeight);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mBitmapCanvas.drawBitmap(mLogo, (mWidth - mLogo.getWidth()) / 2, (mHeight - mLogo.getHeight())/ 2, mLogoPaint);

        mPath.reset();
        float y =  (1 - (float)mCurrentProgress / MAX_PROGRESS) * mHeight;
        mPath.moveTo(mWidth, y);
        mPath.lineTo(mWidth, mHeight);
        mPath.lineTo(0, mHeight);
        mPath.lineTo(0, y);
        float dy =(float)mCount / 100 * 20;
        if (mCount % 2 == 0){
            for(int i = 0; i < 6; i++){
                mPath.rQuadTo(50 - dy, -dy, 100 - dy, 0);
                mPath.rQuadTo(50 - dy, dy, 100 - dy, 0);
            }
        } else {
            for(int i = 0; i < 6; i++){

                mPath.rQuadTo(50 + dy, dy, 100 + dy, 0);
                mPath.rQuadTo(50 + dy, -dy, 100 + dy, 0);
            }
        }
        mPath.close();
        mBitmapCanvas.drawPath(mPath, mPathPaint);
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    private void startAnimation() {
        mCurrentProgress = 0;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCurrentProgress++;
                mCount--;
                if (mCurrentProgress > MAX_PROGRESS) {
//                    mHandler.removeCallbacks(this);
                    mCurrentProgress = 0;
                    mCount = 0;
                    mHandler.postDelayed(this, 30);
                } else {
                    invalidate();
                    mHandler.postDelayed(this, 30);
                }
            }
        }, 30);

    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == GONE)
            mHandler.removeCallbacksAndMessages(this);
        Log.e("Loading", "mhandler remove");
    }
}
