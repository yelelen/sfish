package com.yelelen.sfish.helper;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yelelen.sfish.Model.SoundTrackModel;
import com.yelelen.sfish.Model.SoundTrackModel_Table;

/**
 * Created by yelelen on 17-10-21.
 */

public class SoundTrackDbHelper extends BaseDbHelper<SoundTrackModel> {
    public static final SoundTrackDbHelper instance;

    static {
        instance = new SoundTrackDbHelper();
    }

    public static SoundTrackDbHelper getInstance() {
        return instance;
    }

    @Override
    public SoundTrackModel getByOrder(int order) {
        return SQLite.select()
                .from(SoundTrackModel.class)
                .where(SoundTrackModel_Table._id.eq(order))
                .querySingle();
    }
}
