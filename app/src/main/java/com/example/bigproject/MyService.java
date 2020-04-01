package com.example.bigproject;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MyService extends Service {

    static Context context;//Возможная ошибка
    private static DBWork dbWork;
    Handler handler;//принимает сообщение из потока галереи
    Gallary gallary;
    Thread thread;//поток для сканирования галереи
    long date;//время последнего обновления галереи

    private void buildThread(){
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        if(gallary.getImageLaster(date)!= null){
                            Message msg = new Message();
                            msg.obj = gallary.getImageLaster(date)+"|Uri";
                            handler.handleMessage(msg);
                            date=System.currentTimeMillis()/1000;
                        }else if (gallary.getVideoLaster(date)!= null){
                            Message msg = new Message();
                            msg.obj = gallary.getVideoLaster(date)+"|Uri";
                            handler.handleMessage(msg);
                            date=System.currentTimeMillis()/1000;
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        });
    }
    private void startThread(){
        thread.start();
    }
    private void stopThread(){
        thread.interrupt();
    }
    
    public static void makeBtn(String str){
        MyService.save(str);
    };

    public MyService() {
        gallary = new Gallary(context);
        date = System.currentTimeMillis()/1000;
        buildThread();
        handler = new Handler(){
            public void handleMessage(android.os.Message msg){
                MyService.makeBtn(String.valueOf(msg.obj));
            }
        };
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate(){
        super.onCreate();
    }

    public int onStartCommand(Intent intent,int flags,int startId){
        this.startThread();
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy(){
        this.stopThread();
        super.onDestroy();
    }

    public static void save(String str){
        dbWork = new DBWork(context);
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
