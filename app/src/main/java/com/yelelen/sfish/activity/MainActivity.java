package com.yelelen.sfish.activity;

import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.transition.Transition;
import android.transition.TransitionInflater;

import com.yelelen.sfish.R;
import com.yelelen.sfish.adapter.MainPagerAdapter;

public class MainActivity extends BaseActivity {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }


    protected void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition explode = TransitionInflater.from(this)
                    .inflateTransition(R.transition.explode);
            setTransitions(explode, null, null);
        }

        mTabLayout = findViewById(R.id.tablayout);
        mViewPager = findViewById(R.id.pager);
    }

    protected void initData() {
        MainPagerAdapter pagerAdapter = new MainPagerAdapter();
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(4);

        initTabs();
        mTabLayout.getTabAt(4).select();
    }

    @Override
    protected void initListener() {
        super.initListener();

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = mTabLayout.getSelectedTabPosition();
                mViewPager.setCurrentItem(pos, true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mTabLayout.getTabAt(position).select();
            }
        });
    }

    private void initTabs() {
        TabLayout.Tab tabMM = mTabLayout.newTab();
        TabLayout.Tab tabBook = mTabLayout.newTab();
        TabLayout.Tab tabSelect = mTabLayout.newTab();
        TabLayout.Tab tabSound = mTabLayout.newTab();
        TabLayout.Tab tabVideo = mTabLayout.newTab();

        tabMM.setIcon(R.drawable.sel_tab_mm);
        tabSelect.setIcon(R.drawable.sel_tab_select);
        tabBook.setIcon(R.drawable.sel_tab_book);
        tabSound.setIcon(R.drawable.sel_tab_sound);
        tabVideo.setIcon(R.drawable.sel_tab_video);

        mTabLayout.addTab(tabSelect);
        mTabLayout.addTab(tabVideo);
        mTabLayout.addTab(tabSound);
        mTabLayout.addTab(tabBook);
        mTabLayout.addTab(tabMM);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
