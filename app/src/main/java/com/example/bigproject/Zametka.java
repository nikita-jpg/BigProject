package com.example.bigproject;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Zametka implements Serializable {

    Zametka(){
        data = "";
        name = "";
        text = "";
        bitmap = "";
    }


    private String data,name,text;
    private String bitmap;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getBitmap() {
        return bitmap;
    }

    public void setBitmap(String bitmap) {
        this.bitmap = bitmap;
    }


}
