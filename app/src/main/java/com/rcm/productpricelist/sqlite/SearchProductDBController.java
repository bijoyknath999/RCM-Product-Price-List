package com.rcm.productpricelist.sqlite;

import static com.rcm.productpricelist.sqlite.DBConstants.COLUMN_ID_SEARCH;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class SearchProductDBController {

    private final SQLiteDatabase db;
    private DBHelper dbHelper;

    public SearchProductDBController(Context context) {
        dbHelper = DBHelper.getInstance(context);
        db = dbHelper.getWritableDatabase();
    }

    public boolean insertData(String search_text) {
        ContentValues cv = new ContentValues();

        cv.put(DBConstants.COLUMN_SEARCH_TEXT, search_text);
        long result = db.insert(DBConstants.TABLE_NAME_SEARCH,null, cv);
        return result != -1;
    }

    public ArrayList<SearchModels> getAllData() {

        Cursor cursor = db.rawQuery("SELECT * FROM " + DBConstants.TABLE_NAME_SEARCH, null);

        int totalCount = cursor.getCount();
        if (totalCount>3)
            deleteAllSearch(cursor,totalCount);

        String[] projection = {
                COLUMN_ID_SEARCH,
                DBConstants.COLUMN_SEARCH_TEXT,
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = COLUMN_ID_SEARCH + " DESC";

        Cursor c = db.query(
                DBConstants.TABLE_NAME_SEARCH,  // The table name to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        return fetchData(c);
    }


    private ArrayList<SearchModels> fetchData(Cursor c) {
        ArrayList<SearchModels> searchModelsArrayList = new ArrayList<>();

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    // get  the  data into array,or class variable
                    int itemId = c.getInt(c.getColumnIndexOrThrow(COLUMN_ID_SEARCH));
                    String search_text = c.getString(c.getColumnIndexOrThrow(DBConstants.COLUMN_SEARCH_TEXT));
                    searchModelsArrayList.add(new SearchModels(itemId, search_text));
                } while (c.moveToNext());
            }
            c.close();
        }
        return searchModelsArrayList;
    }

    public boolean deleteSearch(String itemId,Context context) {
        long result = db.delete(DBConstants.TABLE_NAME_SEARCH, "search_id=?", new String[]{itemId});
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public void deleteAllSearch(Cursor cursor, int totalCount) {

        int numToDelete = totalCount - 3;

        if (numToDelete > 0) {
            String deleteQuery = "DELETE FROM " + DBConstants.TABLE_NAME_SEARCH + " WHERE " + DBConstants.COLUMN_ID_SEARCH + " NOT IN " +
                    "(SELECT " + COLUMN_ID_SEARCH + " FROM " + DBConstants.TABLE_NAME_SEARCH + " ORDER BY " + COLUMN_ID_SEARCH + " DESC LIMIT 3)";
            db.execSQL(deleteQuery);
        }
    }
}