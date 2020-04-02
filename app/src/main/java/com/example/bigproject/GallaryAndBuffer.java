package com.example.bigproject;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;

import java.net.URI;

public class GallaryAndBuffer extends AppCompatActivity {
    private Context context;
    private Cursor cursor;
    private ClipboardManager clipboard;


    GallaryAndBuffer(Context context){
        this.context = context;
        clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
    }

    public Uri getImageLaster(long date){
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_ADDED
        };

        String selection = MediaStore.Images.Media.DATE_ADDED+" >= ?";
        String[] selectionArgs = new String[]{
                String.valueOf(date)
        };
        String sortOrder = MediaStore.Images.Media.DATE_ADDED;

        cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder);
        cursor.moveToLast();
        if(cursor.getCount()==0) return null;
        else {
            int id = cursor.getInt(0);
            Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            return uri;
        }
    };
    public Uri getVideoLaster(long date){
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_ADDED
        };

        String selection = MediaStore.Video.Media.DATE_ADDED+" >= ?";
        String[] selectionArgs = new String[]{
                String.valueOf(date)
        };
        String sortOrder = MediaStore.Video.Media.DATE_ADDED;

        cursor =context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder);
        cursor.moveToLast();
        if(cursor.getCount()==0) return null;
        else {
            int id = cursor.getInt(0);
            Uri uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
            return uri;
        }
    };
    public String GetTekStr(){
        ClipData.Item clipData = clipboard.getPrimaryClip().getItemAt(0);
        return ""+clipData.getText();
    }
}
