package com.yelelen.sfish.helper;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yelelen.sfish.Model.SoundZhuboModel;
import com.yelelen.sfish.Model.SoundZhuboModel_Table;

/**
 * Created by yelelen on 17-10-21.
 */

public class SoundZhuboDbHelper extends BaseDbHelper<SoundZhuboModel> {
    public static final SoundZhuboDbHelper instance;

    static {
        instance = new SoundZhuboDbHelper();
    }

    public static SoundZhuboDbHelper getInstance() {
        return instance;
    }

    @Override
    public SoundZhuboModel getByOrder(int order) {
        return SQLite.select()
                .from(SoundZhuboModel.class)
                .where(SoundZhuboModel_Table._id.eq(order))
                .querySingle();
    }
}
