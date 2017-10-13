package com.yelelen.sfish.Model;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by yelelen on 17-9-14.
 */

@Database(name = DB.DB_NAME, version = DB.VERSION)
public class DB {
    public static final String DB_NAME = "SFish";
    public static final int  VERSION = 1;
}
