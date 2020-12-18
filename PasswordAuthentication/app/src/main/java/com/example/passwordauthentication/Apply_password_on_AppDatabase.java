package com.example.passwordauthentication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Apply_password_on_AppDatabase extends SQLiteOpenHelper {
    //this is database class, database is sqLite
    //embedded in Android Studio

    //Database name
    public static final String DATABASE_NAME = "logg.db";
    //table name
    public static final String TABLE_NAME = "apps";

    //column
    public static final String col1 = "package_name";




    //constructor
    public Apply_password_on_AppDatabase(Context context) {
        super(context,DATABASE_NAME,null,1);
    }

    //create table
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE apps( package_name TEXT PRIMARY KEY , password TEXT )  ");
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS apps");
    }

    // insert data into table
    public boolean insertData(String str) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col1, str);
        long insert = writableDatabase.insert(TABLE_NAME, null, contentValues);
        String dbstring = "";
        String Query = "SELECT * FROM " + TABLE_NAME + " WHERE 1";
        Cursor c = writableDatabase.rawQuery(Query,null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex(col1))!= null){
                dbstring += c.getString(c.getColumnIndex(col1));
                dbstring += "\n";
            }
            c.moveToNext();
        }
        System.out.println("Here is Database outputs " + dbstring);
        writableDatabase.close();
        return insert != -1;
    }
    //read data from table
    public Cursor getAllData() {
        return getWritableDatabase().rawQuery("Select * from apps", null);
    }

    //delete data from table
    public Integer deleteData(String str) {
        return Integer.valueOf(getWritableDatabase().delete(TABLE_NAME, "package_name =?", new String[]{str}));
    }
}
