package com.example.passwordauthentication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Password_Database extends SQLiteOpenHelper {
    //this is database class, database is sqLite
    //embedded in Android Studio

    //database name
    public static final String DATABASE_NAME = "pass_data.db";
    //table name
    public static final String TABLE_NAME = "password_table";

    //columns
    public static final String col1 = "password";

    //constructor
    public Password_Database(Context context){
        super(context,DATABASE_NAME,null,1);
    }
    //create table

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE password_table( password TEXT  )  ");
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS password_table");
    }
    // insert data into table
    public boolean insertData(String str) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("password", str);
        long insert = writableDatabase.insert(TABLE_NAME, null, contentValues);
        writableDatabase.close();
        return insert != -1;
    }
    //delete data from table
    public void delete(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }
    //read data from table
    public Cursor getAllData(){
        return getWritableDatabase().rawQuery("Select * from password_table", null);
    }

}
