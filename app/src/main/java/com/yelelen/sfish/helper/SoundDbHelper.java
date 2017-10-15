package com.yelelen.sfish.helper;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yelelen.sfish.Model.SoundItemModel;
import com.yelelen.sfish.Model.SoundItemModel_Table;

import java.util.List;

/**
 * Created by yelelen on 17-10-13.
 */

public class SoundDbHelper extends BaseDbHelper<SoundItemModel> {
    public static final SoundDbHelper instance;

    static {
        instance = new SoundDbHelper();
    }

    public static SoundDbHelper getInstance() {
        return instance;
    }

    public List<SoundItemModel> getFromLocal(int count, boolean order) {
        List<SoundItemModel> models = SQLite.select()
                .from(SoundItemModel.class)
                .where(SoundItemModel_Table._id.lessThan(LAST_INDEX))
                .limit(count)
                .orderBy(SoundItemModel_Table._id, order)
                .queryList();
        if (models.size() > 0)
            LAST_INDEX = models.get(models.size() - 1).getOrder();

        notifyDone(SoundItemModel.class, models);
        return models;
    }

    public SoundItemModel getByOrder(int order) {
        return SQLite.select()
                .from(SoundItemModel.class)
                .where(SoundItemModel_Table._id.eq(order))
                .querySingle();
    }


    public int getMaxOrder() {
        SoundItemModel model = SQLite.select()
                .from(SoundItemModel.class)
                .orderBy(SoundItemModel_Table._id, false)
                .querySingle();
        return model == null ? 0 : model.getOrder();
    }
}
