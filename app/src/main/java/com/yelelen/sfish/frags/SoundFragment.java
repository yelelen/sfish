package com.yelelen.sfish.frags;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yelelen.sfish.Model.SoundRecyclerModel;
import com.yelelen.sfish.R;
import com.yelelen.sfish.adapter.SoundAdapter;

import java.util.ArrayList;
import java.util.List;


public class SoundFragment extends BaseFragment  {
    private RecyclerView mRecyclerView;
    private SoundAdapter mSoundAdapter;
    private List<SoundRecyclerModel> mModels;
    private static SoundFragment mInstance;

    static {
        mInstance = new SoundFragment();
    }

    public static SoundFragment getInstance() {
        return mInstance;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_sound;
    }

    @Override
    protected void initView(final View root) {
        super.initView(root);

        mRecyclerView = root.findViewById(R.id.sound_recyclerview);
        mSoundAdapter = new SoundAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mSoundAdapter);
    }

    @Override
    protected void initData() {
        super.initData();
        mModels = new ArrayList<>();
//        mModels.add(new SoundRecyclerModel("猜你喜欢", "", "", ""));
        mModels.add(new SoundRecyclerModel("有声书", "悬疑", "言情", "文学"));
        mModels.add(new SoundRecyclerModel("脱口秀", "歪果仁说", "奇葩绽放", "干货铺子"));
        mModels.add(new SoundRecyclerModel("广播剧", "百合", "现代言情", "剧情歌"));
        mModels.add(new SoundRecyclerModel("相声评书", "名家评书", "郭德纲", "单田芳"));
        mModels.add(new SoundRecyclerModel("商业财经", "创业", "股评", "理财"));
        mModels.add(new SoundRecyclerModel("情感", "谈恋爱", "城市爱情故事", "心灵疗愈"));
        mModels.add(new SoundRecyclerModel("娱乐", "段子笑话", "热门综艺", "星座运势"));
        mModels.add(new SoundRecyclerModel("音乐", "歌单", "纯音乐", "欧美"));
        mModels.add(new SoundRecyclerModel("影视", "电影资讯", "影评地带", "原声记忆"));
        mModels.add(new SoundRecyclerModel("历史", "百家讲坛", "中国史", "名人传"));
        mModels.add(new SoundRecyclerModel("人文", "经典名著", "诗词歌赋", "纪实档案"));
        mModels.add(new SoundRecyclerModel("诗歌", "现代诗", "诗人电台", "启蒙"));
        mModels.add(new SoundRecyclerModel("儿童", "轻松家教", "热门儿歌", "营业启蒙"));
        mModels.add(new SoundRecyclerModel("IT科技", "互联网", "智能", "大数据"));
        mModels.add(new SoundRecyclerModel("动漫游戏", "ACG脱口秀", "美女主播", "实况解说"));
        mModels.add(new SoundRecyclerModel("3D体验馆", "奇妙听觉", "ASMR", "清新自然"));
        mModels.add(new SoundRecyclerModel("教育培训", "名企大咖", "心理调节", "人际沟通"));
        mSoundAdapter.add(mModels);
    }

    private SoundRecyclerModel buildModel(int category, int category1,
                                          int category2, int category3) {
        SoundRecyclerModel model = new SoundRecyclerModel();
        model.setCategory(getResources().getString(category));
        model.setCategory1(getResources().getString(category1));
        model.setCategory2(getResources().getString(category2));
        model.setCategory3(getResources().getString(category3));
        return model;
    }
}
