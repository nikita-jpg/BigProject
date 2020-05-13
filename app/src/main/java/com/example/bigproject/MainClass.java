package com.example.bigproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        recyclerView.setLayoutManager(gaggeredGridLayoutManager);

        SolventRecyclerViewAdapter rcAdapter = new SolventRecyclerViewAdapter(MainClass.this, zametkaList,recyclerView );
        recyclerView.setAdapter(rcAdapter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocalBase.initialization(getApplicationContext());
        startService();
        try {
            MakeRecycleViewAndAdapter();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


}
