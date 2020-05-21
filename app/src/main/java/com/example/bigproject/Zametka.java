package com.example.bigproject;

import java.io.Serializable;

public class Zametka implements Serializable {
    private String data,name, value,uri;
    private String bitmap;

    Zametka(){
        data = "";
        name = "";
        value = "";
        bitmap = "";
        uri = "";
    }




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

    public void setUri(String uri){
        this.uri = uri;
    }

    public String getUri(){
        return uri;
    }


}
