package com.yelelen.sfish.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.yelelen.sfish.Model.SoundItemModel;
import com.yelelen.sfish.R;
import com.yelelen.sfish.contract.LoadContent;
import com.yelelen.sfish.presenter.SoundItemPresenter;
import com.yelelen.sfish.presenter.SoundTrackPresenter;
import com.yelelen.sfish.utils.BlurUtil;

import java.util.List;

public class SoundPlayActivity extends BaseActivity implements LoadContent<SoundItemModel> {
    private SoundItemModel mModel;
    private int mAlbumOrder;
    private String mAlbumCover;
    private SoundTrackPresenter mTrackPresenter;
    private SoundItemPresenter mItemPresenter;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_sound_play;
    }

    @Override
    protected boolean initArgs() {
        mAlbumOrder = getIntent().getIntExtra("SoundAlbum", 0);
        mAlbumCover = getIntent().getStringExtra("SoundAlbumCover");
        return true;
    }

    @Override
    protected void initView() {
        super.initView();
        mItemPresenter = new SoundItemPresenter(this);
        String path = mItemPresenter.getDbHelper().getByOrder(mAlbumOrder).getPath();
        ((ImageView)findViewById(R.id.sound_play_bg)).setImageBitmap(getBlurBitmap(path));

    }

    private Bitmap getBlurBitmap(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        int scaleRatio = 10;
        int blurRadius = 8;
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,
                bitmap.getWidth() / scaleRatio,
                bitmap.getHeight() / scaleRatio,
                false);
        Bitmap blurBitmap = BlurUtil.doBlur(scaledBitmap, blurRadius, true);
        return blurBitmap;
    }

    @Override
    public void onLoadDone(List<SoundItemModel> t) {

    }

    @Override
    public void onLoadFailed(String reason) {

    }
}
