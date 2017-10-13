package com.yelelen.sfish.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yelelen.sfish.R;
import com.yelelen.sfish.activity.MmDetailActivity;
import com.yelelen.sfish.view.MatrixImageView;
import com.yelelen.sfish.view.MmViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yelelen on 17-9-11.
 */

public class MmDetailAdapter extends PagerAdapter {
    private List<String> mImagePaths;
//    private MmImageCache mImageCache;

    public MmDetailAdapter(List<String> imagePaths) {
        mImagePaths = imagePaths;
//        mImageCache = new MmImageCache(MmImageCache.DEFAULT_SIZE);
    }

    public MmDetailAdapter() {
        this(new ArrayList<String>());
    }

    public List<String> getImagePaths() {
        return mImagePaths;
    }

    public void setImagePaths(List<String> paths) {
        mImagePaths = paths;
    }

    @Override
    public int getCount() {
        return mImagePaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View root = LayoutInflater.from(container.getContext())
                .inflate(R.layout.item_mm_detail, container, false);
        MatrixImageView imageView = (MatrixImageView) root.findViewById(R.id.im_item_mm_detail);

//        String path = mImagePaths.get(position);
//        Bitmap bitmap = mImageCache.getBitmapFromMemoryCache(path);
//        if (bitmap == null) {
//            bitmap = BitmapFactory.decodeFile(path);
//            mImageCache.addBitmapToMemoryCache(path, bitmap);
//        }
//        imageView.setImageBitmap(bitmap);
        imageView.setAdapterPosition(position);
//        imageView.setRotation(-90);
        Glide.with(root.getContext())
                .load(mImagePaths.get(position))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .fitCenter()
                .into(imageView);

        container.addView(root);

        imageView.setOnMovingListener((MmViewPager)container);
        imageView.setClickCallback(MmDetailActivity.getInstance());
        MmDetailActivity.getInstance().addRotateImageListener(position, imageView);

        return root;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        MmDetailActivity.getInstance().removeRotateImageListener(position);
//        mImageCache.removeBitmapFromMemoryCache(mImagePaths.get(position));
    }

}
