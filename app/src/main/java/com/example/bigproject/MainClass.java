package com.example.bigproject;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
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
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.util.List;

public class MainClass extends AppCompatActivity implements View.OnClickListener{
    private StaggeredGridLayoutManager gaggeredGridLayoutManager;
    private List<Zametka> zametkaList = null;//Текущий список заметок
    private SolventRecyclerViewAdapter rcAdapter;//Адаптер для заметок
    private RecyclerView recyclerView;
    protected boolean mainClassIsWork;//Показывает открыт ли сейчас главный экран приложения
    private static Intent thisService;//Хранит текущий сервис
    private final String APP_PREFERENCES = "mysettings";//Имя SharedPreference с настройками
    private final String AUTORIZATION = "Autorisation";
    private final int REQUEST_OF_PERMISSION = 1;
    private SharedPreferences mSittings;
    //FAB
    private FloatingActionButton fab;
    private FloatingActionButton fabExit;
    private FloatingActionButton fabCreateZam;
    private FloatingActionButton fab3;


    //Save the FAB's active status
    //false -> fab = close
    //true -> fab = open
    private boolean FAB_Status = false;

    //Animations
    private Animation show_fab_1;
    private Animation hide_fab_1;
    private Animation show_fab_2;
    private Animation hide_fab_2;

    private int fab_exit_width;


    //Запрашиваем разрешения
    private void setPermission()
    {
        //Разрешение на чтение галереи
        int permissionStatusReadGalary = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionStatusAlertWindow = ContextCompat.checkSelfPermission(this,Manifest.permission.SYSTEM_ALERT_WINDOW);

        //Разрешение на отображение поверх экрана
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissionStatusAlertWindow!= PackageManager.PERMISSION_GRANTED) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + this.getPackageName()));
                startActivityForResult(intent, REQUEST_OF_PERMISSION);
            }
        }

        if(permissionStatusReadGalary == PackageManager.PERMISSION_GRANTED);
        else ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_OF_PERMISSION);

    }
    //Проверяем,выполнен ли уже вход
    private boolean checkAutorization()
    {

        mSittings = this.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if(mSittings.contains(AUTORIZATION) && mSittings.getString(AUTORIZATION,"true").equals("true") )
            return true;
        else
            return false;
    }

    /*Тут происходит запуск основных структур приложения */
    private void inicialization()
    {

        //Floating Action Buttons
        fab =  findViewById(R.id.fab);
        fabExit = findViewById(R.id.fab_exit);
        fabCreateZam = findViewById(R.id.create_zam);

        //Animations
        show_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_show);
        hide_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_hide);
        show_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_show);
        hide_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_hide);


        fab.setOnClickListener(this);
        fabExit.setOnClickListener(this);
        fabCreateZam.setOnClickListener(this);
    }

    private void startService()
    {
        // узнаем размеры экрана из класса Display
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);

        int widthForButton = (int) (0.13*metricsB.widthPixels);
        int heightForButton = (int)(0.08*metricsB.heightPixels);
        MyService.context=getApplicationContext();//Плохо,но не знаю как по-другому
        thisService = new Intent(this,MyService.class);
        thisService.putExtra("widthForButton",widthForButton);
        thisService.putExtra("heightForButton",heightForButton);
        startService(thisService);
    }

    private void MakeRecycleViewAndAdapter() throws FileNotFoundException
    {
        zametkaList = LocalBase.getZamLocal();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        recyclerView.setLayoutManager(gaggeredGridLayoutManager);

        rcAdapter = new SolventRecyclerViewAdapter(MainClass.this, zametkaList,recyclerView );
        recyclerView.setAdapter(rcAdapter);
    }

    protected synchronized void stopAuth(android.app.Fragment fragment){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.remove(fragment).commit();
        startMainClass();
    }

    protected synchronized void startMainClass()
    {
        setContentView(R.layout.activity_main);
        inicialization();
        android.os.Handler handler = new Handler()
        {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                updateUI();
            }
        };

        LocalBase.setHandler(handler);//Даём базе хендлер для общения с UI
        startService();
        try {
            MakeRecycleViewAndAdapter();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_act);

        setPermission();
        LocalBase.initialization(this);
        if(!checkAutorization())
        {
            //setContentView(R.layout.first_act);
            Fragment autorization = new Autorization();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.test,autorization).commit();
        }
        else startMainClass();

    }

    /*Работа с UI*/
    //Если нужно перерисовать UI
    protected synchronized void updateUI()
    {
        try {
            zametkaList = LocalBase.getZamLocal();
            rcAdapter.setItemList(zametkaList);
            rcAdapter.notifyDataSetChangedMyMethod();
            rcAdapter.notifyDataSetChanged();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //Показать меню FAB
    private void expandFAB()
    {

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fabCreateZam.getLayoutParams();
        layoutParams.rightMargin += (int) (fabCreateZam.getWidth() * 0.1);
        layoutParams.bottomMargin += (int) (fabCreateZam.getHeight() * 2.5);
        fabCreateZam.setLayoutParams(layoutParams);
        fabCreateZam.startAnimation(show_fab_1);
        fabCreateZam.setClickable(true);



        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fabExit.getLayoutParams();
        layoutParams2.rightMargin += (int) (fabExit.getWidth() * 0.1);
        layoutParams2.bottomMargin += (int) (fabExit.getHeight() * 1.3);
        fabExit.setLayoutParams(layoutParams2);
        fabExit.startAnimation(show_fab_2);
        fabExit.setClickable(true);

    }

    //Скрыть меню FAB
    private void hideFAB()
    {

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fabCreateZam.getLayoutParams();
        layoutParams.rightMargin -= (int) (fabCreateZam.getWidth() * 0.1);
        layoutParams.bottomMargin -= (int) (fabCreateZam.getHeight() * 2.5);
        fabCreateZam.setLayoutParams(layoutParams);
        fabCreateZam.startAnimation(hide_fab_1);
        fabCreateZam.setClickable(false);


        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fabExit.getLayoutParams();
        layoutParams2.rightMargin -= (int) (fabExit.getWidth() * 0.1);
        layoutParams2.bottomMargin -= (int) (fabExit.getHeight() * 1.3);
        fabExit.setLayoutParams(layoutParams2);
        fabExit.startAnimation(hide_fab_2);
        fabExit.setClickable(false);

    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.fab:
                if (FAB_Status == false) {
                    //Display FAB menu
                    expandFAB();
                    FAB_Status = true;
                } else {
                    //Close FAB menu
                    hideFAB();
                    FAB_Status = false;
                }
                break;
            case R.id.fab_exit:
                exit();
                break;
            case R.id.create_zam:
                makeZam();
                break;
        }
    }

    private void makeZam()
    {
        Zametka zametka = ZametkaWork.makeZametka();
        AddDialogFragment addDialogFragment = new AddDialogFragment(zametka,getApplicationContext());
        addDialogFragment.show(( this).getSupportFragmentManager(), "addDialog");
    }

    //Выходим из аккаунта
    private void exit()
    {

        Intent intent = new Intent(this,MyService.class);
        stopService(intent);
        LocalBase.deleteBase();

        SharedPreferences sharedPreferences = this.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        this.finish();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mainClassIsWork = true;
    }
    @Override
    protected void onStop() {
        super.onStop();
        mainClassIsWork = false;
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        updateUI();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
