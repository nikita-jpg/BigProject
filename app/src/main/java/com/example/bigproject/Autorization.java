package com.example.bigproject;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.FileNotFoundException;
import java.util.Vector;


public class Autorization extends Activity {

    private final int REQUEST_OF_PERMISSION = 1;
    private final String APP_PREFERENCES = "mysettings";
    private final String AUTORIZATION = "Autorisation";
    private SharedPreferences mSittings;

    private void SetPermission()
    {
        //Разрешение на чтение галереи
        int permissionStatusReadGalary = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionStatusAlertWindow = ContextCompat.checkSelfPermission(this,Manifest.permission.SYSTEM_ALERT_WINDOW);

        //Разрешение на отображение поверх экрана
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissionStatusAlertWindow!= PackageManager.PERMISSION_GRANTED) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_OF_PERMISSION);
            }
        }

        if(permissionStatusReadGalary == PackageManager.PERMISSION_GRANTED);
        else ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_OF_PERMISSION);

    }

    private void stertService()
    {
        // узнаем размеры экрана из класса Display
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);

        int widthForButton = (int) (0.13*metricsB.widthPixels);
        int heightForButton = (int)(0.08*metricsB.heightPixels);
        MyService.context=getApplicationContext();//Плохо,но не знаю как по-другому
        Intent intent = new Intent(this,MyService.class);
        intent.putExtra("widthForButton",widthForButton);
        intent.putExtra("heightForButton",heightForButton);
        startService(intent);
    }

    private void checkAutorization() {
        LocalBase.initialization(this.getApplicationContext());
        mSittings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if(mSittings.contains(AUTORIZATION))
        {
            if(mSittings.getString(AUTORIZATION,"false").equals("false")) {
                LocalBase.firstSetting();
                SharedPreferences.Editor editor = mSittings.edit();
                editor.putString(AUTORIZATION,"true");
                editor.apply();
            }

            else
            {
                //Не открываем окно авторизации, а сразу окно приложения
            }
        }
        else
        {
            LocalBase.firstSetting();
            SharedPreferences.Editor editor = mSittings.edit();
            editor.putString(AUTORIZATION,"true");
            editor.apply();
        }
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autorization);

        //Получаем разрешение на чтение из галереи и отображение поверх экрана
        SetPermission();

        //Проверяем,авторизован ли человек
        checkAutorization();

        //Запускаем сервис
        stertService();

        LocalBase.firstSetting();

        /*
        try {
            Bitmap bitmap = LocalBase.getBitmap("09:26:29.txt");
            ImageView imageView = findViewById(R.id.imageView2);
            imageView.setImageBitmap(bitmap);
            String asd ="+5";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

         */


    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_OF_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                    String arr = "daa";
                    // permission granted
                } else {
                    this.finish();
                }
                return;
        }
    }
}
