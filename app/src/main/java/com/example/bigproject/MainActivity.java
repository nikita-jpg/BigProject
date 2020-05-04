package com.example.bigproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {


    private final int REQUEST_OF_PERMISSION = 1;
    private String GetClipType(ClipboardManager clipboard){

        ClipData.Item clipData = clipboard.getPrimaryClip().getItemAt(0);
        String str = (String) clipData.getText();
        if((clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) && (clipData.getText()!= null)) return "text";
        else if((clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_URILIST)) && (clipData.getText()!= null)) return "uri";
        else return "intent";
    }

    private void SetPermission(){
        //Разрешение на чтение галереи
        int permissionStatusReadGalary = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionStatusAlertWindow = ContextCompat.checkSelfPermission(this,Manifest.permission.SYSTEM_ALERT_WINDOW);

        //Разрешение на отображение поверх экрана
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissionStatusAlertWindow!=PackageManager.PERMISSION_GRANTED) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_OF_PERMISSION);
            }
        }

        if(permissionStatusReadGalary == PackageManager.PERMISSION_GRANTED);
        else ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_OF_PERMISSION);

    }

    private void stertService(){
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
    private Bitmap GetLastImage(){
        Cursor cursor;
        String[] projection = new String[] {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_ADDED
        };

        String selection = MediaStore.Images.Media.DATE_ADDED+" >= ?";
        String[] selectionArgs = new String[]{String.valueOf(System.currentTimeMillis()/1000-86400)};

        String sortOrder = MediaStore.Images.Media.DATE_ADDED;
        cursor = getApplicationContext().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );
        cursor.moveToLast();
        long id = cursor.getLong(0);//id колонки id всегда 0


        Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
        ContentResolver resolver = getApplicationContext().getContentResolver();
        Bitmap bitmap = Bitmap.createBitmap(100, 100,
                Bitmap.Config.ARGB_8888);;
        try {
            InputStream stream = resolver.openInputStream(contentUri);
            bitmap = BitmapFactory.decodeStream(stream);
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    public Zametka makeZametka(){
        Zametka zametka = new Zametka();

        //Сохраняем дату
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);
        zametka.setData(dateText);

        //По дефолту имя заметки = дата
        zametka.setName(dateText);

        return zametka;
    }

    TextView textView;
    TextView contentText;
    TextView typeText;
    ImageView imageView;
    Button btn;
    ZametkaWork dbWork;

    public void setImage(Bitmap image){
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(image);
    }


    private ClipboardManager clipboard;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Получаем разрешение на чтение из галереи и отображение поверх экрана
        SetPermission();

        //Запускаем сервис
        stertService();








        File file1 = new File(getFilesDir()+"/folder");
        if (!file1.exists()) {
            file1.mkdir();
        }
        File file2 = new File(getFilesDir()+"/images");
        if (!file2.exists()) {
            file2.mkdir();
        }



        /*
        String root = String.valueOf(getFilesDir());
        File file = new File(root+"/folder/15.txt");
        boolean a = true;
        if (!file.exists()) {
            try {
                a = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File file1 = new File(root+"/folder/14.txt");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file1);
            fileOutputStream.write(string.getBytes());
            fileOutputStream.close();

            File file11 = new File(root+"/folder/14.txt");
            FileInputStream fileInputStream = new FileInputStream(file11);

            int i=-1;
            while((i=fileInputStream.read())!=-1){
                stringBuilder.append((char)i);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        String b ="5";


         */
        /*Готовим БД
        dbWork = new ZametkaWork(this);
        SQLiteDatabase db = dbWork.getReadableDatabase();
        Cursor curs = db.query("mytable",null, null, null, null, null, null);
        */
        //clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);


    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_OF_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                    String arr = "daa";
                    // permission granted
                } else {
                    this.finish();
                }
                return;
        }
    }

}

