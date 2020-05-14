package com.example.bigproject;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class SolventViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener, AddDialogListener {

    public TextView countryName;
    public ImageView countryPhoto;
    public Zametka zametka;
    public Context context;

    public SolventViewHolders(View itemView, Context context) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.context=context;
        countryName =  itemView.findViewById(R.id.country_name);
        countryPhoto =  itemView.findViewById(R.id.country_photo);
    }


    @Override
    public void onClick(View view) {
        Toast.makeText(view.getContext(), "Clicked Position = " + zametka.getData(), Toast.LENGTH_SHORT).show();
        DialogFragment newFragment = new AddDialogFragment();
        newFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "addDialog");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

    }
}
