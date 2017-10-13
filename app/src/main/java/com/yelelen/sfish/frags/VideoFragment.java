package com.yelelen.sfish.frags;


import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;

import com.yelelen.sfish.R;

import java.io.IOException;


public class VideoFragment extends BaseFragment implements TextureView.SurfaceTextureListener{
    private TextureView mTextureView;
    private Camera mCamera;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_video;
    }

    @Override
    protected void initView(final View root) {
        super.initView(root);
//        mTextureView = new TextureView(getContext());
//        mTextureView.setSurfaceTextureListener(this);
//        ((FrameLayout)root).addView(mTextureView);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mCamera = Camera.open();
        Camera.Size size = mCamera.getParameters().getPreviewSize();
        mTextureView.setLayoutParams(new FrameLayout.LayoutParams(size.width, size.height, Gravity.CENTER));

        try {
            mCamera.setPreviewTexture(surface);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCamera.startPreview();
        mTextureView.setAlpha(1.0f);
        mTextureView.setRotation(90);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mCamera.stopPreview();
        mCamera.release();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
