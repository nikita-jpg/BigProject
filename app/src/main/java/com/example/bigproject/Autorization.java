package com.example.bigproject;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


public class Autorization extends Fragment implements View.OnClickListener {

    private final int REQUEST_OF_PERMISSION = 1;
    private final String APP_PREFERENCES = "mysettings";
    private final String AUTORIZATION = "Autorisation";
    private SharedPreferences mSittings;

    private Button enterBtn;
    private Button regBtn;
    private EditText loginText;
    private EditText passwordText;
    private TextView requestTextView;
    private String regStatus="";
    private Context context;
    private View rootView;

    public Autorization(){
    }


    //Запускаем главное окно приложения
    private void startMainClass()
    {
        if(!regStatus.equals("") && !regStatus.equals("reg"))
        {
            final Toast toast = Toast.makeText(context, "Началась загрузка данных", Toast.LENGTH_SHORT);
            toast.show();
            new CountDownTimer(10000, 1000) {
                public void onTick(long millisUntilFinished) {
                    toast.show();
                }

                public void onFinish() {
                    toast.cancel();
                }
            }.start();
        }
        mSittings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSittings.edit();
        editor.putString(AUTORIZATION,"true");
        editor.apply();
        ((MainClass)getActivity()).stopAuth(this);
    }



    //Выводим сообщение для пользователя
    private void requestToUser(String request)
    {
        Toast toast = Toast.makeText(context,
                request, Toast.LENGTH_SHORT);
        toast.show();

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //Получаем разрешение на чтение из галереи и отображение поверх экрана
        this.context = getActivity().getApplication();
        View rootView =
                inflater.inflate(R.layout.activity_autorization, container, false);

            loginText = rootView.findViewById(R.id.loginText);
            passwordText = rootView.findViewById(R.id.passwordText);
            enterBtn = rootView.findViewById(R.id.enterBtn);
            regBtn = rootView.findViewById(R.id.regBtn);

            enterBtn.setOnClickListener(this);
            regBtn.setOnClickListener(this);

        return rootView;
    }



    @Override
    public void onClick(View v) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = getActivity().getCurrentFocus();
        if (focusedView != null) {
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

        final ServerWork serverWork = new ServerWork(context);
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
                        requestToUser(getString(R.string.request_minus_1));
                        break;
                    case 0:
                        requestToUser(getString(R.string.request_0));
                        break;
                    case 1:
                        startMainClass();
                        break;
                    case 2:
                        requestToUser(getString(R.string.request_2));
                        break;
                    case 3:
                        requestToUser(getString(R.string.request_3));
                        break;
                }
            }
        };

        Runnable runnable = null;
        if(login.length()<8 || password.length()<8) requestToUser(getString(R.string.small_login_or_password));
        else
        {
            enterBtn.setClickable(false);
            regBtn.setClickable(false);
            requestToUser(getString(R.string.waiting_srver_request));
            final String regOrAut;

            if(v.getId() == R.id.regBtn)
                regOrAut = "reg";
            else
                regOrAut = "aut";
            regStatus = regOrAut;
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_OF_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                    String arr = "daa";
                    // permission granted
                } else {
                    System.exit(0);
                }
                return;
        }
    }
}

