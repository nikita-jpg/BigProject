package com.example.bigproject;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

public class AddDialogFragment extends DialogFragment {

    private Button buttonSave,buttonDelete;
    private EditText zametkaName;
    private EditText zametkaValue;
    private Zametka zametka;

    AddDialogFragment(Zametka zametka)
    {
        this.zametka=zametka;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    // Inflate the layout to use as dialog or embedded fragment
        View v = inflater.inflate(R.layout.dialog_lay, container, false);

        buttonSave= v.findViewById(R.id.button_save);
        buttonDelete= v.findViewById(R.id.button_delete);

        zametkaName = v.findViewById(R.id.zametka_name);
        zametkaValue = v.findViewById(R.id.zametka_value);

        zametkaName.setText(zametka.getName());
        zametkaValue.setText(zametka.getValue());



        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LocalBase.deleteZam(zametka.getData());
                    }
                });
                thread.start();
                Toast.makeText(v.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });


        buttonSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                zametka.setName(zametkaName.getText().toString());
                zametka.setValue(zametkaValue.getText().toString());
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LocalBase.save(zametka);
                    }
                });
                thread.start();
                Toast.makeText(v.getContext(), "Saved", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        return v;
    }
}
