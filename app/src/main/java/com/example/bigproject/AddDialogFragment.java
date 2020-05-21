package com.example.bigproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;

public class AddDialogFragment extends DialogFragment {

    private Button buttonSave,buttonDelete,buttonSetPhoto;
    private EditText zametkaName;
    private EditText zametkaValue;
    private Zametka zametka;
    private final int Pick_image = 1;
    private Context context;

    AddDialogFragment(Zametka zametka,Context context)
    {
        this.zametka=zametka;
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    // Inflate the layout to use as dialog or embedded fragment
        View v = inflater.inflate(R.layout.dialog_lay, container, false);

        buttonSave= v.findViewById(R.id.button_save);
        buttonDelete= v.findViewById(R.id.button_delete);
        buttonSetPhoto = v.findViewById(R.id.button_set_image);

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
                        if(!zametka.getUri().equals(""))
                        {
                            ZametkaWork zametkaWork = new ZametkaWork(context);
                            zametkaWork.preparationAndSaveZam(zametka);
                        }
                    }
                });
                thread.start();
                Toast.makeText(v.getContext(), "Saved", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });


        buttonSetPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Вызываем стандартную галерею для выбора изображения с помощью Intent.ACTION_OPEN_DOCUMENT:
                Intent photoPickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                //Тип получаемых объектов - image:
                photoPickerIntent.setType("image/*");
                //Запускаем переход с ожиданием обратного результата в виде информации об изображении:
                startActivityForResult(photoPickerIntent, Pick_image);
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case Pick_image:
                if(resultCode == RESULT_OK){

                    //Получаем URI изображения, преобразуем его в Bitmap
                    //объект и отображаем в элементе ImageView нашего интерфейса:
                    final Uri imageUri = imageReturnedIntent.getData();zametka.setUri(imageUri.toString());
                    zametka.setUri(imageUri.toString());
                    //final InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
                    //final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    //imageView.setImageBitmap(selectedImage);

                }
        }}
}
