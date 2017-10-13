package com.yelelen.sfish.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yelelen.sfish.Model.SoundAlbumItemModel;
import com.yelelen.sfish.Model.SoundItemModel;
import com.yelelen.sfish.Model.SoundRecyclerModel;
import com.yelelen.sfish.R;
import com.yelelen.sfish.contract.LoadContent;
import com.yelelen.sfish.presenter.SoundItemPresenter;
import com.yelelen.sfish.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yelelen on 17-9-5.
 */

public class SoundAdapter extends RecyclerAdapter<SoundRecyclerModel> {

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_sound;
    }

    @Override
    protected SoundViewHolder onCreateViewHolderImpl(View root, int viewType) {
        return new SoundViewHolder(root);
    }

    class SoundViewHolder extends BaseViewHolder<SoundRecyclerModel>
            implements AdapterListener<SoundAlbumItemModel>, View.OnClickListener,
            LoadContent<SoundItemModel>{
        private TextView mCategory1;
        private TextView mCategory2;
        private TextView mCategory3;
        private TextView mCategory;
        private ImageView mRefresh;
        private RecyclerView mRecyclerView;
        private SoundAlbumAdapter mSoundAlbumAdapter;
        private Context mContext;
        private SoundItemPresenter mPresenter;

        public SoundViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mCategory = itemView.findViewById(R.id.label_sound_category);
            mCategory1 = itemView.findViewById(R.id.label_sound_category_1);
            mCategory2 = itemView.findViewById(R.id.label_sound_category_2);
            mCategory3 = itemView.findViewById(R.id.label_sound_category_3);
            mRefresh = itemView.findViewById(R.id.im_sound_refresh);
            mRecyclerView = itemView.findViewById(R.id.sound_item_recycler);
            mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
            mSoundAlbumAdapter = new SoundAlbumAdapter(this);
            mRecyclerView.setAdapter(mSoundAlbumAdapter);

            mRefresh.setOnClickListener(this);
            mCategory1.setOnClickListener(this);
            mCategory2.setOnClickListener(this);
            mRefresh.setOnClickListener(this);

            mPresenter = new SoundItemPresenter(this);
        }

        @Override
        protected void onBind(SoundRecyclerModel data) {
            mCategory.setText(data.getCategory());
            mCategory1.setText(data.getCategory1());
            mCategory2.setText(data.getCategory2());
            mCategory3.setText(data.getCategory3());
        }

        @Override
        public void onItemClick(BaseViewHolder holder, SoundAlbumItemModel data) {

        }

        @Override
        public void onItemLongClick(BaseViewHolder holder, SoundAlbumItemModel data) {

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.im_sound_refresh:

                    break;
                case R.id.label_sound_category_1:
                    break;
                case R.id.label_sound_category_2:
                    break;
                case R.id.label_sound_category_3:
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onLoadDone(List<SoundItemModel> t) {
            if (t != null && t.size() > 0) {
                List<SoundAlbumItemModel> models = new ArrayList<>();
                for (SoundItemModel soundItemModel : t) {
                    SoundAlbumItemModel model = new SoundAlbumItemModel(soundItemModel);
                    models.add(model);
                }
                mSoundAlbumAdapter.replace(models);
            }
        }

        @Override
        public void onLoadFailed(String reason) {
            Utils.showToast(mContext, reason);
        }
    }


}
