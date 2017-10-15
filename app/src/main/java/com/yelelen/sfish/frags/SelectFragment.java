package com.yelelen.sfish.frags;


import android.view.View;
import android.widget.TextView;

import com.yelelen.sfish.R;
import com.yelelen.sfish.view.CircleSeekBar;


public class SelectFragment extends BaseFragment {
    private CircleSeekBar mSeekBar;
    private TextView mTextView;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_select;
    }

    @Override
    protected void initView(View root) {
        super.initView(root);
        mSeekBar = root.findViewById(R.id.circleseekbar);
        mTextView = root.findViewById(R.id.label_select_text);
        mSeekBar.setListener(new CircleSeekBar.OnSeekListener() {
            @Override
            public void onSeek(float progress) {
                mTextView.setText(Math.round(progress) + " %");
            }
        });
    }
}
