package com.yelelen.sfish.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.widget.ImageView;

import com.yelelen.sfish.R;
import com.yelelen.sfish.frags.PermissionFragment;

public class LaunchActivity extends BaseActivity {

    private ImageView mTextXiao;
    private ImageView mTextYu;
    private ImageView mTextHao;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_launch;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PermissionFragment.hasAllPerm(this, getSupportFragmentManager())) {
            startAnimations();
        }
    }

    private void startAnimations() {
        ObjectAnimator animatorXiao = ObjectAnimator.ofFloat(mTextXiao, "translationY", 0, -20, 0);
        ObjectAnimator animatorYu = ObjectAnimator.ofFloat(mTextYu, "translationY", 0, -20, 0);
        final ObjectAnimator animatorHao = ObjectAnimator.ofFloat(mTextHao, "translationY", 0, -20, 0);
        animatorXiao.setDuration(500);
        animatorYu.setDuration(500);
        animatorHao.setDuration(500);

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(animatorXiao, animatorYu, animatorHao);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                showMainActivity();

                super.onAnimationEnd(animation);
            }
        });
        if (!animatorSet.isStarted())
            animatorSet.start();
    }

    public void initView() {
        mTextXiao = (ImageView) findViewById(R.id.im_xiao);
        mTextYu = (ImageView) findViewById(R.id.im_yu);
        mTextHao = (ImageView) findViewById(R.id.im_hao);
    }


    private void showMainActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                MainActivity.show(LaunchActivity.this);
                showActivity(MainActivity.class);

            }
        });
        finish();
    }
}

