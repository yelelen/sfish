package com.yelelen.sfish.helper;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yelelen.sfish.Model.MmItemModel;
import com.yelelen.sfish.Model.MmItemModel_Table;

import java.util.List;

/**
 * Created by yelelen on 17-9-14.
 */

public class MmDbHelper extends BaseDbHelper<MmItemModel> {
    public static final MmDbHelper instance;

    static {
        instance = new MmDbHelper();
    }

    public static MmDbHelper getInstance() {
        return instance;
    }

    public List<MmItemModel> getFromLocal(int count, boolean order) {
        List<MmItemModel> models = SQLite.select()
                .from(MmItemModel.class)
                .where(MmItemModel_Table._id.lessThan(LAST_INDEX))
                .limit(count)
                .orderBy(MmItemModel_Table._id, order)
                .queryList();
        if (models.size() > 0)
            LAST_INDEX = models.get(models.size() - 1).getOrder();

        notifyDone(MmItemModel.class, models);
        return models;
    }


    public int getMaxOrder() {
        MmItemModel model = SQLite.select()
                .from(MmItemModel.class)
                .orderBy(MmItemModel_Table._id, false)
                .querySingle();
        return model == null ? 0 : model.getOrder();
    }


    public List<MmItemModel> getOrderByFavNum(int count, boolean order) {
        List<MmItemModel> models = SQLite.select()
                .from(MmItemModel.class)
                .limit(count)
                .orderBy(MmItemModel_Table.fav_num, order)
                .queryList();
        return models;
    }

    public List<MmItemModel> getOrderBySeenNum(int count, boolean order) {
        List<MmItemModel> models = SQLite.select()
                .from(MmItemModel.class)
                .limit(count)
                .orderBy(MmItemModel_Table.seen_num, order)
                .queryList();
        return models;
    }

    public List<MmItemModel> getOrderByTotalNum(int count, boolean order) {
        List<MmItemModel> models = SQLite.select()
                .from(MmItemModel.class)
                .limit(count)
                .orderBy(MmItemModel_Table.total_num, order)
                .queryList();
        return models;
    }

}
