package com.example.bigproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBWork extends SQLiteOpenHelper {


    public DBWork(@Nullable Context context) {
        super(context, "myDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table mytable ("
                + "id integer primary key autoincrement,"
                + "content text,"
                + "type text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void save(String str,Context context){
        DBWork dbWork = new DBWork(context);
        ContentValues contentValues = new ContentValues();
        SQLiteDatabase db = dbWork.getWritableDatabase();

        String value = str.substring(0,str.indexOf("|"));
        if(str.substring(str.indexOf("|")+1,str.length()).equals("Uri")){
            contentValues.put("content",value);
            contentValues.put("type","Uri");
        }else{
            contentValues.put("content",value);
            contentValues.put("type","txt");
        }
        db.insert("mytable", null, contentValues);
        dbWork.close();
    }
}
