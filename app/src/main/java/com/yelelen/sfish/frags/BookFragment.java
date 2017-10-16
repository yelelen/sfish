package com.yelelen.sfish.frags;


import android.view.View;

import com.yelelen.sfish.R;
import com.yelelen.sfish.view.MusicBar;
import com.yelelen.sfish.view.SearchBar;


public class BookFragment extends BaseFragment {
    private SearchBar searchBar;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_book;
    }

    @Override
    protected void initView(View root) {
        super.initView(root);
        MusicBar mb = root.findViewById(R.id.musicbar);
        mb.start();

    }
}
