package com.example.bigproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


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
        int permissionstatusReadGalary = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permissionstatusReadGalary == PackageManager.GET_RECEIVERS);
        else ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_OF_PERMISSION);
        //Разрешение на отображение поверх экрана
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_OF_PERMISSION);
            }
        }
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

    TextView textView;
    TextView contentText;
    TextView typeText;
    ImageView imageView;
    Button btn;
    DBWork dbWork;


    private ClipboardManager clipboard;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Получаем разрешение на чтение из галереи и отображение поверх экрана
        Intent intent = new Intent(this,MyService.class);

        //Готовим БД
        dbWork = new DBWork(this);

        //intent.putExtra("context", (Parcelable) this);
        MyService.context=getApplicationContext();//Плохо,но не знаю как по-другому
        startService(intent);

        imageView = findViewById(R.id.imageView);
        contentText=findViewById(R.id.content);
        typeText=findViewById(R.id.type);
        btn = findViewById(R.id.button);
        btn.setOnClickListener(this);
        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);


        SQLiteDatabase db = dbWork.getReadableDatabase();
        Cursor curs = db.query("mytable",null, null, null, null, null, null);

        // определяем номера столбцов по имени в выборке
        int idColIndex = curs.getColumnIndex("id");
        int content = curs.getColumnIndex("content");
        int type = curs.getColumnIndex("type");

        curs.moveToLast();
        contentText.setText(curs.getString(content));
        typeText.setText(curs.getString(type));


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_OF_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                } else {
                    this.finish();
                }
                return;
        }
    }

    @Override
    public void onClick(View v) {

    }
}
