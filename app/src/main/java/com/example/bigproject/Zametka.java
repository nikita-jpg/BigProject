package com.example.bigproject;

import java.io.Serializable;

public class Zametka implements Serializable {

    Zametka(){
        data = "";
        name = "";
        value = "";
        bitmap = "";
    }


    private String data,name, value;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getBitmap() {
        return bitmap;
    }

    public void setBitmap(String bitmap) {
        this.bitmap = bitmap;
    }


}
