package com.example.bigproject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.RequiresApi;

import java.io.IOException;

public class MyService extends Service implements View.OnTouchListener {

    static Context context;//Возможная ошибка
    private Handler handler;//принимает сообщение из потока галереи
    private GallaryAndBuffer gallaryAndBuffer;
    private Thread thread;//поток для сканирования галереи
    private long date;//время последнего обновления галереи
    private String message;//Из потока-сканнера в hendler приходит объект Message,это его свойство obj
    private String tekStr="";//текущая строка из буфера
    private int widthForButton = 0;
    private int heightForButton = 0;
    private Button mButton;
    private boolean cycle = true;

    private WindowManager.LayoutParams params;
    private WindowManager wm;


    private void buildThread()
    {
        tekStr= gallaryAndBuffer.GetTekStr();//Чтобы при первом запуске не появлялось кнопки
        thread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                while (cycle) {
                    try
                    {
                        if (gallaryAndBuffer.getImageLaster(date) != null) {
                            Message msg = new Message();
                            msg.obj = gallaryAndBuffer.getImageLaster(date) + "|Uri";
                            handler.sendMessage(msg);
                            Thread.sleep(2000);
                            Message msg2 = new Message();
                            msg2.obj = "delete";
                            handler.sendMessage(msg2);
                        } else if (gallaryAndBuffer.getVideoLaster(date) != null) {
                            Message msg = new Message();
                            msg.obj = gallaryAndBuffer.getVideoLaster(date) + "|Uri";
                            handler.sendMessage(msg);
                            Thread.sleep(2000);
                            Message msg2 = new Message();
                            msg2.obj = "delete";
                            handler.sendMessage(msg2);
                        }
                        else if( !"".equals(gallaryAndBuffer.GetTekStr()) && !tekStr.equals(gallaryAndBuffer.GetTekStr())) {
                            Message msg = new Message();
                            tekStr= gallaryAndBuffer.GetTekStr();
                            msg.obj = tekStr + "|txt";
                            handler.sendMessage(msg);
                            Thread.sleep(2000);
                            Message msg2 = new Message();
                            msg2.obj = "delete";
                            handler.sendMessage(msg2);
                        }
                        date = System.currentTimeMillis() / 1000;
                        Thread.sleep(250);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        });
    }

    private void startThread() {
        date = System.currentTimeMillis() / 1000;
        thread.start();
    }

    private void stopThread() {
        thread.interrupt();
    }


    private void makeBtn() {
        mButton.setTextColor(context.getResources().getColor(R.color.button_txt_norm));
        mButton.setClickable(true);
        wm.addView(mButton, params);
    }

    private void deleteBtn() {
        wm.removeView(mButton);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public MyService() {
        gallaryAndBuffer = new GallaryAndBuffer(context);

        //Описываем работу нового потока-сканнера
        buildThread();

        //Оформляем кнопку
        mButton = new Button(context);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(context.getResources().getColor(R.color.button));
        drawable.setCornerRadius(15);
        mButton.setBackground(drawable);
        mButton.setText("C");
        mButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 45);//надо фиксить
        mButton.setPadding(0,-17,0,0);
        mButton.setOnTouchListener(this);

        handler = new Handler() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            public void handleMessage(android.os.Message msg) {
                if (!msg.obj.equals("delete")) {
                    message = String.valueOf(msg.obj);
                    makeBtn();
                } else deleteBtn();
            }
        };

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        widthForButton = intent.getIntExtra("widthForButton", 0);
        heightForButton = intent.getIntExtra("heightForButton", 0);

        //Оформляем окно
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
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSPARENT);
        params.gravity = Gravity.RIGHT | Gravity.TOP;
        params.horizontalMargin = (float) 0.05;
        params.verticalMargin = (float) 0.25;
        wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);

        //Запускаем поток-сканнер
        this.startThread();

        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        cycle = false;
        this.stopThread();
        super.onDestroy();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        mButton.setClickable(false);
        mButton.setTextColor(context.getResources().getColor(R.color.button_txt_pressed));
        ZametkaWork zametka = new ZametkaWork(context);
        //Если заметка содержит фото,то в строке есть Uri
        if(message.substring(message.indexOf("|")+1,message.length()).equals("Uri"))
            zametka.MakeAndSaveImageZam(Uri.parse(message.substring(0,message.indexOf("|"))));
        else zametka.MakeAndSaveTextZam(message);
        return false;
    }

}


