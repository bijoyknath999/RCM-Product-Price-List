package com.rcm.productpricelist.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class FavProductDBController {

    private final SQLiteDatabase db;


    public FavProductDBController(Context context) {
        db = DBHelper.getInstance(context).getWritableDatabase();
    }

    public boolean insertData(int slno) {
        ContentValues cv = new ContentValues();

        cv.put(DBConstants.COLUMN_SLNO, slno);
        long result = db.insert(DBConstants.TABLE_NAME,null, cv);
        return result != -1;
    }

    public Cursor readAllData(){
        String query = "SELECT * FROM " + DBConstants.TABLE_NAME;

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public ArrayList<FavModels> getAllData() {


        String[] projection = {
                DBConstants.COLUMN_ID,
                DBConstants.COLUMN_SLNO,
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = DBConstants.COLUMN_ID + " DESC";

        Cursor c = db.query(
                DBConstants.TABLE_NAME,  // The table name to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        return fetchData(c);
    }


    private ArrayList<FavModels> fetchData(Cursor c) {
        ArrayList<FavModels> favModelsArrayList = new ArrayList<>();

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    // get  the  data into array,or class variable
                    int itemId = c.getInt(c.getColumnIndexOrThrow(DBConstants.COLUMN_ID));
                    int slono = c.getInt(c.getColumnIndexOrThrow(DBConstants.COLUMN_SLNO));
                    favModelsArrayList.add(new FavModels(itemId, slono));
                } while (c.moveToNext());
            }
            c.close();
        }
        return favModelsArrayList;
    }

    public boolean deleteFav(String itemId,Context context) {
        long result = db.delete(DBConstants.TABLE_NAME, "slno=?", new String[]{itemId});
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public void deleteAllFav() {
        db.execSQL("DELETE FROM " + DBConstants.TABLE_NAME);
    }

    public boolean checkFav(String slno) {
        boolean valid = false;
        Cursor res = db.rawQuery("SELECT * from "+DBConstants.TABLE_NAME+" where "+DBConstants.COLUMN_SLNO+"='" +slno + "'", null );
        if (res.getCount()==1)
        {
            valid = true;
        }
        return valid;
    }
}