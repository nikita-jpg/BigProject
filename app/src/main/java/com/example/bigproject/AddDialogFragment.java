package com.example.bigproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

public class AddDialogFragment extends DialogFragment {

    private Button button;
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

    // Inflate the layout to use as dialog or embedded fragment
        View v = inflater.inflate(R.layout.dialog_lay, container, false);

        button= v.findViewById(R.id.button);
        zametkaName = v.findViewById(R.id.zametka_name);
        zametkaValue = v.findViewById(R.id.zametka_value);

        zametkaName.setText(zametka.getName());
        zametkaValue.setText(zametka.getValue());

        button.setOnClickListener(new View.OnClickListener(){
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
                v.setBackgroundColor(getContext().getResources().getColor(R.color.very_black));
            }
        });
        return v;
    }
}
