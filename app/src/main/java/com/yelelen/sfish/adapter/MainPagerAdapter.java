package com.yelelen.sfish.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yelelen.sfish.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yelelen on 17-10-3.
 */

public class MainPagerAdapter extends PagerAdapter {
    private Boolean isFirst = true;
    private List<View> mViews;

    public MainPagerAdapter() {
        mViews = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());

        if (isFirst) {
            isFirst = false;
            makeViews(container, inflater);
        }

        container.addView(mViews.get(position));
        return mViews.get(position);
    }

    private void makeViews(ViewGroup container, LayoutInflater inflater) {
        mViews.add(inflater.inflate(R.layout.main_select, container, false));
        mViews.add(inflater.inflate(R.layout.main_video, container, false));
        mViews.add(inflater.inflate(R.layout.main_sound, container, false));
        mViews.add(inflater.inflate(R.layout.main_book, container, false));
        mViews.add(inflater.inflate(R.layout.main_mm, container, false));
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }
}
