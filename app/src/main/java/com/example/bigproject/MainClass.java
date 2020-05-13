package com.example.bigproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MainClass extends AppCompatActivity {
    private StaggeredGridLayoutManager gaggeredGridLayoutManager;
    private List<Zametka> zametkaList = null;
    private List<Zametka> zametkaListTwo = null;
    private SolventRecyclerViewAdapter rcAdapter;
    private RecyclerView recyclerView;
    protected boolean mainClassIsWork;

    private void startService()
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
    protected void updateUI()
    {
        try {
            zametkaListTwo = LocalBase.getZamLocal();
            rcAdapter.setItemList(zametkaListTwo);
            rcAdapter.notifyDataSetChangedMyMethod();
            rcAdapter.notifyDataSetChanged();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.os.Handler handler = new Handler()
        {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                updateUI();
            }
        };

        LocalBase.initialization(getApplicationContext(),handler);
        startService();
        try {
            MakeRecycleViewAndAdapter();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void onStart() {
        super.onStart();
        mainClassIsWork = true;
    }
    protected void onStop() {
        super.onStop();
        mainClassIsWork = false;
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        updateUI();
    }
}
