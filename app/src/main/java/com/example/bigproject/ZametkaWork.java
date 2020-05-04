package com.example.bigproject;

import android.content.ContentResolver;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import androidx.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ZametkaWork {

    private Context context;
    private Bitmap bitmap = null;
    private Zametka zametka;
    private Uri uri = null; //Uri фото, которое нужно сохранить
    private String inf = ""; //Строка, которая приходит в методы save...
    private Thread thread; //Тут происходит вся работа

    ZametkaWork(Context context){
        this.context = context;
    }


    public String getAndEncodeBitmap(Uri uri){

        String str = "";

        ContentResolver resolver = context.getContentResolver();
        try {
            InputStream stream = resolver.openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        str = Base64.encodeToString(byteArrayOutputStream.toByteArray(),0);

        return str;
    }

    private Zametka makeZametka(){
        Zametka zametka = new Zametka();

        //Сохраняем дату
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);
        zametka.setData(dateText);

        //По дефолту имя заметки = дата
        zametka.setName(dateText);

        return zametka;
    }

    //Создаёт и сохраняет заметку на телефоне
    public void MakeAndSaveTextZam(String str){
        inf = str.substring(0,str.indexOf("|"));

        //Теперь мы сериализуем изображение в поток байтов,а его в строку. Это довольно тяжёлая задача, поэтому она в отдельном потоке
        thread =  new Thread(new Runnable() {
            @Override
            public void run() {

                //Создаём заметку
                zametka = makeZametka();

                //Добавляем к заметке наш текст
                zametka.setText(inf);

                //Фото нет
                zametka.setBitmap("");

                //Сохраняемя заметку в файл
                LocalBase.save(zametka);

                String v = "4";
                thread.interrupt();
            }
        });
        thread.start();
    }
    public void MakeAndSaveImageZam(Uri ArgUri) throws IOException, ClassNotFoundException
    {
        uri = ArgUri;
        //Теперь мы сериализуем изображение в поток байтов,а его в строку. Это довольно тяжёлая задача, поэтому она в отдельном потоке
        thread =  new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //Создаём заметку
                zametka = makeZametka();

                //Раз мы сохраняем картинку,текста нет
                zametka.setText("");

                //Сериализуем фото
                String bitmapStr = getAndEncodeBitmap(uri);

                //Добавляем фото к заметке(в виде строки)
                zametka.setBitmap(bitmapStr);

                //Сохраняемя заметку в файл
                LocalBase.save(zametka);

                String v = "4";
                thread.interrupt();
                }
            });
        thread.start();
    }


}
