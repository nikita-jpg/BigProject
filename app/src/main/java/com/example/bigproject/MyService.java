package com.example.bigproject;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

import java.util.Calendar;

public class MyService extends Service {

    Handler handler;//принимает сообщение из потока галереи
    Gallary gallary;
    Thread thread;//поток для сканирования галереи
    long date;//время последнего обновления галереи


    public static void makeBtn(Uri uri){};
    private void buildThread(){
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        if(gallary.getImageLaster(date)!= null){
                            MyService.makeBtn(gallary.getImageLaster(date));
                            date=System.currentTimeMillis()/1000;
                        }else if (gallary.getVideoLaster(date)!= null){
                            MyService.makeBtn(gallary.getVideoLaster(date));
                            date=System.currentTimeMillis()/1000;
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        });
    }
    
    public MyService() {
        gallary = new Gallary(getApplicationContext());
        date = System.currentTimeMillis()/1000;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate(){
        super.onCreate();
    }

    public int onStartCommand(Intent intent,int flags,int startId){




        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy(){
        super.onDestroy();
    }
}
