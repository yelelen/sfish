package com.yelelen.sfish.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by yelelen on 17-10-10.
 */

public class VoiceWave extends SurfaceView implements SurfaceHolder.Callback {
    private static int SLEEP_TIME = 100;
    private final Object mSurfaceLock = new Object();
    private DrawThread mThread;
    private int mWidth = 600;
    private int mHeight = 300;
    private int mStep = 5;
    private int mPointCount;
    private float mZStep;

    public VoiceWave(Context context) {
        super(context);
        setZOrderOnTop(true);
        init();
    }

    public VoiceWave(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VoiceWave(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        getHolder().addCallback(this);
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
        setMeasuredDimension(mWidth, mHeight);
        Log.e("sfsdsd", "onMeasure ====> " + mWidth + " " + mHeight);
        mPointCount = mWidth / mStep;
        mZStep = 6.0f / mPointCount;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mThread = new DrawThread(holder);
        mThread.setRun(true);
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        synchronized (mSurfaceLock) {  //这里需要加锁，否则doDraw中有可能会crash
            mThread.setRun(false);
        }
    }


    private class DrawThread extends Thread {
        private SurfaceHolder mHolder;
        private boolean mIsRun = false;
        private Paint mPaint;
        private Path mPathFirst1;
        private Path mPathFirst2;
        private Path mPathSecond1;
        private Path mPathSecond2;
        private Path mPath3;
        private float x = 0f;
        private float y = 0f;
        private float z = -3f;

        private final float param1 = (float) (0.75 * Math.PI);
        private float param2 = (float) (0.5 * Math.PI);

        public DrawThread(SurfaceHolder holder) {
            super("VoiceWave DrawThread");
            mHolder = holder;
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(Color.GREEN);
            mPaint.setStrokeWidth(6);
            mPaint.setAlpha(64);

            mPathFirst1 = new Path();
            mPathFirst2 = new Path();
            mPathSecond1 = new Path();
            mPathSecond2 = new Path();
            mPath3 = new Path();
        }

        @Override
        public void run() {
            Log.e("ssss", Thread.currentThread().getName());

            while (true) {
                synchronized (mSurfaceLock) {
                    if (!mIsRun)
                        return;

                    Canvas canvas = mHolder.lockCanvas();
                    if (canvas != null) {
                        genPath();
                        doDraw(canvas);
                        mHolder.unlockCanvasAndPost(canvas);
                    }
                }

                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void doDraw(Canvas canvas) {
            canvas.translate(0, mHeight / 2);
            // 清屏
            Paint paint = new Paint();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawPaint(paint);
//            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
//
            float p = 425 / 4;
            LinearGradient gradient = new LinearGradient(mWidth / 4, -p, mWidth / 4, p,
                    Color.BLUE, Color.CYAN, Shader.TileMode.CLAMP);
            mPaint.setShader(gradient);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawPath(mPathFirst1, mPaint);
            canvas.drawPath(mPathFirst2, mPaint);
            mPathFirst1.reset();
            mPathFirst2.reset();

//            gradient = new LinearGradient(mWidth / 4, -p / 2, mWidth / 4, p / 2,
//                    Color.CYAN, Color.BLUE, Shader.TileMode.REPEAT);
//            mPaint.setShader(gradient);
//            mPaint.setStyle(Paint.Style.FILL);
//            canvas.drawRect(0, - p / 2, mWidth / 3, p / 2, mPaint);
//
//            gradient = new LinearGradient(mWidth * 3 / 4, -p / 2, mWidth * 3 / 4, p / 2,
//                    Color.CYAN, Color.BLUE, Shader.TileMode.REPEAT);
//            mPaint.setShader(gradient);
//            mPaint.setStyle(Paint.Style.FILL);
//            canvas.drawRect(mWidth * 2 / 3, - p / 2, mWidth, p / 2, mPaint);
//            canvas.save();
//            canvas.drawPath(mPathSecond1, mPaint);
//            canvas.drawPath(mPathSecond2, mPaint);
//            canvas.drawPath(mPath3, mPaint);

        }

        private void genPath() {
            x = 0;
            z = -3;
//            float m = (float) Math.sin(y += mStep) * mStep;
            while (z <= 3) {
                x += mStep;
                z += mZStep;
                Double a = (425 / (4 + Math.pow(z, 4)));
                float s1 = (float) Math.sin(param1 * z - param2);
                Double s2 = Math.sin(param1 * z - param2 * 0.4);
                float y1 = (float) ((float) (s1 * a) * Math.random() * 2);
                float y2 = (float) (a * s2);
                float y3 = (float) (y1 * 0.3);
                mPathFirst1.lineTo(x, y1);
                mPathFirst2.lineTo(x, -y1);
                mPathSecond1.lineTo(x, y2);
                mPathSecond2.lineTo(x, -y2);
                mPath3.lineTo(x, y3);
            }
//            setRun(false);
        }

        void setRun(boolean isRun) {
            this.mIsRun = isRun;
        }

    }

}
