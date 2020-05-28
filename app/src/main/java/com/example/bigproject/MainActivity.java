package com.example.bigproject;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Intent intent;

    private ClipboardManager clipboard;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_act);
        Fragment autorization = new Autorization();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.test,autorization).commit();

    }

}

