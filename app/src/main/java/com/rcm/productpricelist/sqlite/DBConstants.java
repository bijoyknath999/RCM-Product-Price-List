package com.rcm.productpricelist.sqlite;

import android.provider.BaseColumns;

public class DBConstants implements BaseColumns {

    public static final String TABLE_NAME = "rcm_fav_product";

    public static final String COLUMN_ID = "_id";

    public static final String COLUMN_SLNO = "slno";

    public static final String TABLE_NAME_SEARCH = "rcm_search_product";

    public static final String COLUMN_ID_SEARCH = "search_id";

    public static final String COLUMN_SEARCH_TEXT = "search_text";

    public static final String SQL_CREATE_FAV_PRODUCT_ENTRIES =
            "CREATE TABLE " + TABLE_NAME +
                    " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_SLNO + " INTEGER" + ");";

    public static final String SQL_DELETE_FAV_PRODUCT_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static final String SQL_CREATE_SEARCH_PRODUCT_ENTRIES =
            "CREATE TABLE " + TABLE_NAME_SEARCH +
                    " (" + COLUMN_ID_SEARCH + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_SEARCH_TEXT + " TEXT" + ");";

    public static final String SQL_DELETE_SEARCH_PRODUCT_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME_SEARCH;
}
