package com.example.bigproject;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
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

public class MyService extends Service implements View.OnTouchListener {

    static Context context;//Возможная ошибка
    Handler handler;//принимает сообщение из потока галереи
    Gallary gallary;
    Thread thread;//поток для сканирования галереи
    long date;//время последнего обновления галереи
    String message;
    int widthForButton = 0;
    int heightForButton = 0;
    Button mButton;

    WindowManager.LayoutParams params;
    WindowManager wm;

    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
    private void buildThread() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String tekStr="";
                while (true) {
                    try {
                        if (gallary.getImageLaster(date) != null) {
                            Message msg = new Message();
                            msg.obj = gallary.getImageLaster(date) + "|Uri";
                            handler.sendMessage(msg);
                            Thread.sleep(2000);
                            Message msg2 = new Message();
                            msg2.obj = "delete";
                            handler.sendMessage(msg2);
                        } else if (gallary.getVideoLaster(date) != null) {
                            Message msg = new Message();
                            msg.obj = gallary.getVideoLaster(date) + "|Uri";
                            handler.sendMessage(msg);
                            Thread.sleep(2000);
                            Message msg2 = new Message();
                            msg2.obj = "delete";
                            handler.sendMessage(msg2);
                        } else if(!tekStr.equals(MyService.GetTekStr(clipboard))){
                            tekStr=MyService.GetTekStr(clipboard);
                            Message msg = new Message();
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


    public static String GetTekStr(ClipboardManager clipboard){

        ClipData.Item clipData = clipboard.getPrimaryClip().getItemAt(0);
        return ""+clipData.getText();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public MyService() {
        gallary = new Gallary(context);

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
        this.stopThread();
        super.onDestroy();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mButton.setClickable(false);
        mButton.setTextColor(context.getResources().getColor(R.color.button_txt_pressed));
        DBWork dbWork = new DBWork(context);
        dbWork.save(message);
        return false;
    }
}


