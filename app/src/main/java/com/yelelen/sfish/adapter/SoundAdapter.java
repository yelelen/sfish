package com.yelelen.sfish.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.yelelen.sfish.Model.SoundAlbumItemModel;
import com.yelelen.sfish.Model.SoundItemModel;
import com.yelelen.sfish.Model.SoundRecyclerModel;
import com.yelelen.sfish.R;
import com.yelelen.sfish.activity.MainActivity;
import com.yelelen.sfish.activity.SoundPlayActivity;
import com.yelelen.sfish.contract.LoadContent;
import com.yelelen.sfish.presenter.SoundItemPresenter;
import com.yelelen.sfish.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yelelen on 17-9-5.
 */

public class SoundAdapter extends RecyclerAdapter<SoundRecyclerModel> {
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    public SoundAdapter(Context context) {
        super();
        mPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    protected int getItemLayoutId() {
        return R.layout.item_sound;
    }

    @Override
    protected SoundViewHolder onCreateViewHolderImpl(View root, int viewType) {
        return new SoundViewHolder(root);
    }


    @Override
    public void onViewDetachedFromWindow(BaseViewHolder<SoundRecyclerModel> holder) {
        super.onViewDetachedFromWindow(holder);
        String label = ((SoundViewHolder)holder).getCurrLabel();
        String key = ((SoundViewHolder)holder).getSharePreKey();
        mEditor.putString(key, label);
        mEditor.apply();
    }

    class SoundViewHolder extends BaseViewHolder<SoundRecyclerModel>
            implements AdapterListener<SoundAlbumItemModel>, View.OnClickListener,
            LoadContent<SoundItemModel> {
        private RadioButton mCategory1;
        private RadioButton mCategory2;
        private RadioButton mCategory3;
        private RadioGroup mRadioGroup;
        private TextView mCategory;
        private ImageView mRefresh;
        private ImageView mEmpty;
        private RecyclerView mRecyclerView;
        private SoundAlbumAdapter mSoundAlbumAdapter;
        private Context mContext;
        private SoundItemPresenter mPresenter;
        private int mCount = 6;
        private String mCurrLabel = "";
        private String mLastLabel = mCurrLabel;
        private String mSharePreKey;

        public SoundViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mCategory = itemView.findViewById(R.id.label_sound_category);
            mCategory1 = itemView.findViewById(R.id.rb_sound_category_1);
            mCategory2 = itemView.findViewById(R.id.rb_sound_category_2);
            mCategory3 = itemView.findViewById(R.id.rb_sound_category_3);
            mRadioGroup = itemView.findViewById(R.id.rg_category);

            mRefresh = itemView.findViewById(R.id.im_sound_refresh);
            mEmpty = itemView.findViewById(R.id.im_sound_empty);
            mRecyclerView = itemView.findViewById(R.id.sound_item_recycler);
            mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
            mSoundAlbumAdapter = new SoundAlbumAdapter(this);
            mRecyclerView.setAdapter(mSoundAlbumAdapter);

            mRefresh.setOnClickListener(this);
            mCategory.setOnClickListener(this);
            mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    setRadioButttonTextColor(checkedId);
                    setCurrentLabel(getCheckedButtonText(checkedId));
                    mPresenter.setLabelStartIndex(0);
                    mPresenter.loadLabelData(mCount, mCurrLabel);
                }
            });

            mPresenter = new SoundItemPresenter(this);
        }


        @Override
        protected void onBind(SoundRecyclerModel data) {
            String category1 = data.getCategory1();
            String category2 = data.getCategory2();
            String category3 = data.getCategory3();

            mCategory.setText(data.getCategory());
            if (TextUtils.isEmpty(category1))
                mCategory1.setVisibility(View.GONE);
            else
                mCategory1.setText(category1);

            if (TextUtils.isEmpty(category2))
                mCategory2.setVisibility(View.GONE);
            else
                mCategory2.setText(category2);

            if (TextUtils.isEmpty(category3))
                mCategory3.setVisibility(View.GONE);
            else
                mCategory3.setText(category3);

            mSharePreKey = data.getCategory();
            mCurrLabel = mPreferences.getString(mSharePreKey, mSharePreKey);
            setCurrentLabel(mCurrLabel);
            mPresenter.loadLabelData(mCount, mCurrLabel);

            int labelId;
            if (!mCurrLabel.equals(data.getCategory())) {
                if (mCurrLabel.equals(category1))
                    labelId = R.id.rb_sound_category_1;
                else if (mCurrLabel.equals(category2))
                    labelId = R.id.rb_sound_category_2;
                else
                    labelId = R.id.rb_sound_category_3;
                mRadioGroup.check(labelId);
                setRadioButttonTextColor(labelId);
            }

        }

        public String getCurrLabel() {
            return mCurrLabel;
        }

        public String getSharePreKey() {
            return mSharePreKey;
        }

        @Override
        public void onItemClick(BaseViewHolder holder, SoundAlbumItemModel data) {
            Intent intent = new Intent(mContext, SoundPlayActivity.class);
            intent.putExtra("SoundAlbum", data.getOrder());
            intent.putExtra("SoundAlbumCover", data.getCover());
            mContext.startActivity(intent);
        }

        @Override
        public void onItemLongClick(BaseViewHolder holder, SoundAlbumItemModel data) {

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.im_sound_refresh:
                    setCurrentLabel(mCurrLabel);
                    mPresenter.loadLabelData(mCount, mCurrLabel);
                    break;
                case R.id.label_sound_category:
                    setRadioButttonTextColor(-100);
                    setCurrentLabel(mCategory.getText().toString());
                    mPresenter.setLabelStartIndex(0);
                    mPresenter.loadLabelData(mCount, mCurrLabel);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onLoadDone(final List<SoundItemModel> t) {
            if (MainActivity.getInstance() != null) {
                MainActivity.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (t != null && t.size() > 0) {
                            mEmpty.setVisibility(View.GONE);
                            List<SoundAlbumItemModel> models = new ArrayList<>();
                            for (SoundItemModel soundItemModel : t) {
                                SoundAlbumItemModel model = new SoundAlbumItemModel(soundItemModel);
                                models.add(model);
                            }
                            mSoundAlbumAdapter.replace(models);
                        } else {
                            if (mLastLabel != mCurrLabel) {
                                mSoundAlbumAdapter.replace(null);
                                mEmpty.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });

            }

        }

        private void setCurrentLabel(String label) {
            mLastLabel = mCurrLabel;
            mCurrLabel = label;
        }

        @Override
        public void onLoadFailed(String reason) {
            Utils.showToast(mContext, reason);
        }

        private void setRadioButttonTextColor(int id) {
            int colorHight = mContext.getResources().getColor(R.color.colorPrimary);
            int colorGray = mContext.getResources().getColor(R.color.textSecond);

            // id == -100 表示清除所有的RadioButton的选中状态
            if (id == -100) {
                mRadioGroup.clearCheck();
                mCategory1.setTextColor(colorGray);
                mCategory2.setTextColor(colorGray);
                mCategory3.setTextColor(colorGray);
                return;
            }

            mCategory1.setTextColor(id == R.id.rb_sound_category_1 ? colorHight : colorGray);
            mCategory2.setTextColor(id == R.id.rb_sound_category_2 ? colorHight : colorGray);
            mCategory3.setTextColor(id == R.id.rb_sound_category_3 ? colorHight : colorGray);
        }

        private String getCheckedButtonText(int id) {
            return (id == R.id.rb_sound_category_1) ? mCategory1.getText().toString()
                    : (id == R.id.rb_sound_category_2) ? mCategory2.getText().toString()
                    : mCategory3.getText().toString();
        }
    }

}
