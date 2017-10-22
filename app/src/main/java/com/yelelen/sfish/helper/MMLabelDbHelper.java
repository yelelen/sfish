package com.yelelen.sfish.helper;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yelelen.sfish.Model.MmLabelModel;
import com.yelelen.sfish.Model.MmLabelModel_Table;

import java.util.List;

/**
 * Created by yelelen on 17-10-6.
 */

public class MMLabelDbHelper extends BaseDbHelper<MmLabelModel> {
    public static final MMLabelDbHelper instance;

    static {
        instance = new MMLabelDbHelper();
    }

    public static MMLabelDbHelper getInstance() {
        return instance;
    }

    @Override
    public List<MmLabelModel> getFromLocal(int count, boolean order) {
        List<MmLabelModel> models = SQLite.select()
                .from(MmLabelModel.class)
                .where(MmLabelModel_Table._id.lessThan(LAST_INDEX))
                .limit(count)
                .orderBy(MmLabelModel_Table._id, order)
                .queryList();
        if (models.size() > 0)
            LAST_INDEX = models.get(models.size() - 1).getOrder();

        notifyDone(MmLabelModel.class, models);
        return models;
    }

    @Override
    public int getMaxOrder() {
        MmLabelModel model = SQLite.select()
                .from(MmLabelModel.class)
                .orderBy(MmLabelModel_Table._id, false)
                .querySingle();
        return model == null ? 0 : model.getOrder();
    }

    public List<MmLabelModel> getDataFromLocal() {
        List<MmLabelModel> models = SQLite.select()
                .from(MmLabelModel.class)
                .orderBy(MmLabelModel_Table._id, false )
                .queryList();
        return models;
    }
}
