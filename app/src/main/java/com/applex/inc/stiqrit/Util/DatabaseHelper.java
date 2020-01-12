package com.applex.inc.stiqrit.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME ="stiqrit_onDevice.db";
    private static final String TABLE_NAME = "stiQRsList";
    private static final String col1 = "Serial_No";
    private static final String col2 = "stiQR_ID";
    private static final String col3 = "Title";
    private static final String col4 = "Description";
    private static final String col5 = "Date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + col2 + " TEXT, "+ col3 + " TEXT, "+ col4 + " TEXT, "+ col5 +" TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }



    public boolean addData(String stiQR_ID,String title,String description,String date)    //////////for swipe undo insertion at fixed location
    {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col2,stiQR_ID);
        contentValues.put(col3,title);
        contentValues.put(col4,description);
        contentValues.put(col5,date);



        long result = db.insert(TABLE_NAME,null,contentValues);

        if(result == -1){
            return false ;
        }
        else {
            return true;
        }
    }

    public Cursor getListContents(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " +TABLE_NAME,null);
        return data;
    }

    public Cursor getItemId(String stiQR_ID){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + col1 + " FROM " + TABLE_NAME + " WHERE "+ col2 + "= '" + stiQR_ID +"'";
        Cursor data = db.rawQuery(query,null);
        return data;
    }

//    public void updateItem(String newName,String date,int id){
//        SQLiteDatabase db = this.getWritableDatabase();
////        String query = "UPDATE "+ TABLE_NAME + " SET "+ col2 + "= '" + newName +"' AND "+ col3 + "= '" + date+"'" + " WHERE "+ col1 +" ='"+id +"'"+ " AND "
////                + col2 +" ='" + oldName + "'";
//        String query = "UPDATE "+ TABLE_NAME + " SET "+ col2 + "= '" + newName +"' AND "+ col3 + "= '" + date+"'" + " WHERE "+ col1 +" ='"+id +"'";
//  //      String query = "UPDATE "+ TABLE_NAME + " SET "+ col2 + "= '" + newName + "' WHERE "+ col1 +" ='"+id +"'";
//        db.execSQL(query);
//    }

    public void deleteItem(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM "+ TABLE_NAME + " WHERE "+ col1 +" ='"+ id +"'";
        db.execSQL(query);
    }
//    public void deleteAll(){
//        SQLiteDatabase db = this.getWritableDatabase();
//        String query = "DELETE FROM "+ TABLE_NAME ;
//        db.execSQL(query);
//    }
}
