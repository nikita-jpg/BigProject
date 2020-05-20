package com.example.bigproject;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class Autorization extends Activity implements View.OnClickListener {

    private final int REQUEST_OF_PERMISSION = 1;
    private final String APP_PREFERENCES = "mysettings";
    private final String AUTORIZATION = "Autorisation";
    private SharedPreferences mSittings;

    private Button enterBtn;
    private Button regBtn;
    private EditText loginText;
    private EditText passwordText;
    private TextView requestTextView;


    //Запускаем главное окно приложения
    private void startMainClass()
    {
        SharedPreferences.Editor editor = mSittings.edit();
        editor.putString(AUTORIZATION,"true");
        editor.apply();
        Intent intent = new Intent(Autorization.this,MainClass.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    //Запрашиваем разрешения
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

    //Проверяем,выполнен ли уже вход
    private boolean checkAutorization()
    {

        mSittings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if(mSittings.contains(AUTORIZATION) && mSittings.getString(AUTORIZATION,"true").equals("true") )
            return true;
        else
            return false;
    }

    //Выводим сообщение для пользователя
    private void requestToUser(String request,int color)
    {
        requestTextView.setTextColor(color);
        requestTextView.setText(request);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Получаем разрешение на чтение из галереи и отображение поверх экрана
        SetPermission();
        LocalBase.initialization(getApplicationContext());

        //Проверяем,был ли человек уже авторизован
        if(checkAutorization())
        {
            startMainClass();
        }
        else
        {
            setContentView(R.layout.activity_autorization);
            loginText = findViewById(R.id.loginText);
            passwordText = findViewById(R.id.passwordText);
            requestTextView = findViewById(R.id.requestTextView);
            enterBtn = findViewById(R.id.enterBtn);
            regBtn = findViewById(R.id.regBtn);

            enterBtn.setOnClickListener(this);
            regBtn.setOnClickListener(this);
        }


    }

    @Override
    public void onClick(View v) {

        final ServerWork serverWork = new ServerWork(getApplicationContext());
        final String login = loginText.getText().toString();
        final String password = passwordText.getText().toString();

        //-1:Неверный логин или пароль
        //0:Сервер не доступен
        //1:Успех
        //2:Логин уже занят
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                enterBtn.setClickable(true);
                regBtn.setClickable(true);
                switch (msg.arg1)
                {
                    case -1:
                        requestToUser(getString(R.string.request_minus_1),R.color.red);
                        break;
                    case 0:
                        requestToUser(getString(R.string.request_0),R.color.red);
                        break;
                    case 1:
                        startMainClass();
                        break;
                    case 2:
                        requestToUser(getString(R.string.request_2),R.color.red);
                        break;
                    case 3:
                        requestToUser(getString(R.string.request_3),R.color.red);
                        break;
                }
            }
        };

        Runnable runnable = null;
        if(login.length()<8 || password.length()<8) requestToUser(getString(R.string.small_login_or_password),R.color.red);
        else
        {
            enterBtn.setClickable(false);
            regBtn.setClickable(false);
            requestToUser(getString(R.string.waiting_srver_request),R.color.red);
            final int regOrAut;

            if(v.getId() == R.id.regBtn)
                regOrAut = 1;
            else
                regOrAut = 2;
            runnable = new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {
                    Message message = new Message();
                    message.arg1 = serverWork.regAutServer(login,password,regOrAut);
                    handler.sendMessage(message);
                }
            };

            Thread thread = new Thread(runnable);
            thread.start();
        }

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

