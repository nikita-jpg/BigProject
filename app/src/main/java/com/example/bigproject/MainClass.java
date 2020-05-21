package com.example.bigproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
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

    //FAB
    private FloatingActionButton fab;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private CoordinatorLayout rootLayout;


    //Save the FAB's active status
    //false -> fab = close
    //true -> fab = open
    private boolean FAB_Status = false;

    //Animations
    private Animation show_fab_1;
    private Animation hide_fab_1;
    private Animation show_fab_2;
    private Animation hide_fab_2;
    private Animation show_fab_3;
    private Animation hide_fab_3;

    /*Тут происходит запуск основных структур приложения */
    private void inicialization()
    {
        rootLayout = (CoordinatorLayout) findViewById(R.id.coordinator_lay);

        //Floating Action Buttons
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab_exit);
        fab2 = (FloatingActionButton) findViewById(R.id.create_zam);
        fab3 = (FloatingActionButton) findViewById(R.id.fab_3);

        //Animations
        show_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_show);
        hide_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_hide);
        show_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_show);
        hide_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_hide);
        show_fab_3 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab3_show);
        hide_fab_3 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab3_hide);


        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        layoutParams.rightMargin += (int) (fab1.getWidth() * 1.7);
        layoutParams.bottomMargin += (int) (fab1.getHeight() * 0.25);
        fab1.setLayoutParams(layoutParams);
        fab1.startAnimation(show_fab_1);
        fab1.setClickable(true);

        //Floating Action Button 2
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        layoutParams2.rightMargin += (int) (fab2.getWidth() * 1.5);
        layoutParams2.bottomMargin += (int) (fab2.getHeight() * 1.5);
        fab2.setLayoutParams(layoutParams2);
        fab2.startAnimation(show_fab_2);
        fab2.setClickable(true);

        //Floating Action Button 3
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
        layoutParams3.rightMargin += (int) (fab3.getWidth() * 0.25);
        layoutParams3.bottomMargin += (int) (fab3.getHeight() * 1.7);
        fab3.setLayoutParams(layoutParams3);
        fab3.startAnimation(show_fab_3);
        fab3.setClickable(true);
    }

    //Скрыть меню FAB
    private void hideFAB()
    {

        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        layoutParams.rightMargin -= (int) (fab1.getWidth() * 1.7);
        layoutParams.bottomMargin -= (int) (fab1.getHeight() * 0.25);
        fab1.setLayoutParams(layoutParams);
        fab1.startAnimation(hide_fab_1);
        fab1.setClickable(false);

        //Floating Action Button 2
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        layoutParams2.rightMargin -= (int) (fab2.getWidth() * 1.5);
        layoutParams2.bottomMargin -= (int) (fab2.getHeight() * 1.5);
        fab2.setLayoutParams(layoutParams2);
        fab2.startAnimation(hide_fab_2);
        fab2.setClickable(false);

        //Floating Action Button 3
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
        layoutParams3.rightMargin -= (int) (fab3.getWidth() * 0.25);
        layoutParams3.bottomMargin -= (int) (fab3.getHeight() * 1.7);
        fab3.setLayoutParams(layoutParams3);
        fab3.startAnimation(hide_fab_3);
        fab3.setClickable(false);
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
                Toast.makeText(getApplication(), "Floating Action Button 1", Toast.LENGTH_SHORT).show();
                break;
            case R.id.create_zam:
                makeZam();
                Toast.makeText(getApplication(), "Floating Action Button 2", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fab_3:
                Toast.makeText(getApplication(), "Floating Action Button 3", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    private void makeZam()
    {
        Zametka zametka = ZametkaWork.makeZametka();
        AddDialogFragment addDialogFragment = new AddDialogFragment(zametka);
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
