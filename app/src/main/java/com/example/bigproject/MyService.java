package com.example.bigproject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;

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
    HUDView hudView;
    Button mButton;

    WindowManager.LayoutParams params;
    WindowManager wm;

    private void buildThread(){
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        if(gallary.getImageLaster(date)!= null){
                            Message msg = new Message();
                            msg.obj = gallary.getImageLaster(date)+"|Uri";
                            handler.sendMessage(msg);
                            Thread.sleep(2000);
                            Message msg2=new Message();
                            msg2.obj="delete";
                            handler.sendMessage(msg2);
                        }else if (gallary.getVideoLaster(date)!= null){
                            Message msg = new Message();
                            msg.obj = gallary.getVideoLaster(date)+"|Uri";
                            handler.sendMessage(msg);
                            Thread.sleep(2000);
                            Message msg2=new Message();
                            msg2.obj="delete";
                            handler.sendMessage(msg2);
                        }
                        Thread.sleep(100);
                        date=System.currentTimeMillis()/1000;
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


    public void makeBtn(){
        wm.addView(hudView, params);
    }
    
    public void deleteBtn(){
        wm.removeView(hudView);

    }
    public MyService() {
        gallary = new Gallary(context);

        date = System.currentTimeMillis()/1000;
        buildThread();

        handler = new Handler(){
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            public void handleMessage(android.os.Message msg){
                if(!msg.obj.equals("delete")) makeBtn();
                else deleteBtn();
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

        //Работа с кнопкой

        hudView = new HUDView(context);
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        params = new WindowManager.LayoutParams(
                widthForButton,
                heightForButton,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSPARENT);
        params.gravity = Gravity.RIGHT | Gravity.TOP;
        params.horizontalMargin= (float) 0.05;
        params.verticalMargin= (float) 0.25;

        wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);


        this.startThread();

        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy(){
        this.stopThread();
        super.onDestroy();
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


