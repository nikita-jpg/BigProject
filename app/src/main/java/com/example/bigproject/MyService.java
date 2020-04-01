package com.example.bigproject;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

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
    int widthForButton=0;
    int heightForButton=0;
    int widthForButtonMergen=0;
    int heightForButtonMergen=0;

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
    public  void makeBtn(String str){

        MyService.save(str);
    };


    public void btn(){

        HUDView hudView = new HUDView(context);
        // узнаем размеры экрана из класса Display
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                widthForButton,
                heightForButton,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                0,
//              WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                      | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSPARENT);
        params.gravity = Gravity.RIGHT | Gravity.TOP;
        params.horizontalMargin= (float) 0.05;
        params.verticalMargin= (float) 0.25;
        params.setTitle("Load Average");
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.addView(hudView, params);
    }





    public MyService() {
        gallary = new Gallary(context);
        date = System.currentTimeMillis()/1000;
        buildThread();
        handler = new Handler(){
            public void handleMessage(android.os.Message msg){
                super.handleMessage(msg);
                this.makeBtn(String.valueOf(msg.obj));
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
        widthForButton=intent.getIntExtra("widthForButton",0);
        heightForButton=intent.getIntExtra("heightForButton",0);
        widthForButtonMergen=intent.getIntExtra("widthForButtonMerge",0);
        heightForButtonMergen = intent.getIntExtra("heightForButtonMerge",0);
        this.startThread();
        btn();
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
class HUDView extends View {
    float width;
    float height;

    public HUDView(Context context) {
        super(context);
        Toast.makeText(getContext(),"HUDView", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.DKGRAY);
        paint.setStyle(Paint.Style.FILL);
        RectF rect = new RectF();
        Rect mTextBoundRect = new Rect();

        width= (float) (getWidth());
        height= (float) (getHeight());

        rect.set(0, 0, width,
                height);

        paint.setTextSize(100);
        // Подсчитаем размер текста

        paint.getTextBounds("C", 0, 1, mTextBoundRect);
        //mTextWidth = textBounds.width();
        // Используем measureText для измерения ширины
        float mTextWidth = paint.measureText("C");
        float mTextHeight = mTextBoundRect.height();

        canvas.drawRoundRect(rect, 20, 20, paint);
        paint.setColor(Color.BLUE);
        canvas.drawText("C",width/2-(mTextWidth / 2f),(float) (height)/2+(mTextHeight /2f),paint);

    }

    @Override
    protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //return super.onTouchEvent(event);
        Toast.makeText(getContext(),"onTouchEvent", Toast.LENGTH_LONG).show();
        return true;
    }
}


