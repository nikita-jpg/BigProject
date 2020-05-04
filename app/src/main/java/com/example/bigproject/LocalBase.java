package com.example.bigproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

import static com.example.bigproject.MyService.context;



public class LocalBase {
    private final String publicKey = "mQmD3HLarC";
    private final String private7Key = "mQmD3HLarC";
    private static String root = String.valueOf(context.getFilesDir());


    public static synchronized String encode(String key,String value)
    {
        return "";
    }

    public static synchronized Zametka deSerializationZametka(String str) throws IOException,ClassNotFoundException
    {
        byte[] data = Base64.decode(str,0);
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data));

        Zametka zametka = (Zametka) objectInputStream.readObject();
        objectInputStream.close();
        return zametka;
    }

    private static synchronized String serializationZametke(Serializable serializable) throws IOException,ClassNotFoundException
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

        objectOutputStream.writeObject(serializable);
        objectOutputStream.close();

        return Base64.encodeToString(byteArrayOutputStream.toByteArray(),0);
    }

    private static synchronized boolean saveStrBitmap(String name, String strBtm)
    {
        File file1 = new File(root+"/images/"+name+".txt");

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file1);
            fileOutputStream.write(strBtm.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static synchronized Bitmap getBitmap(String name)
    {
        File file1 = new File(root+"/images/"+name+".txt");
        Bitmap bitmap = null;
        StringBuilder stringBuilder = new StringBuilder();
        int tekChar = -1;

        FileInputStream fileInputStream;
        try
        {
            fileInputStream = new FileInputStream(file1);
            while ((tekChar=fileInputStream.read())!=-1)
                stringBuilder.append((char)tekChar);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return bitmap;
        }

        bitmap = ZametkaWork.deSerializationBitmap(String.valueOf(stringBuilder));
        return bitmap;
    }

    //true-всё хорошо. false - всё плохо
    public static synchronized boolean save(Zametka zametka)
    {

        if(saveStrBitmap(zametka.getData(),zametka.getBitmap())) {

            zametka.setBitmap("");

            //Имя файла = дата создания заметки
            File file1 = new File(root + "/folder/" + zametka.getData() + ".txt");
            try {
                String zamStr = serializationZametke(zametka);
                FileOutputStream fileOutputStream = new FileOutputStream(file1);
                fileOutputStream.write(zamStr.getBytes());
                fileOutputStream.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        }else return false;

        return true;
    }

    public static synchronized Vector<Zametka> getZamLocal() throws FileNotFoundException
    {
        Vector<Zametka> vector = new Vector<>();

        File file1 = new File(root+"/folder");

        String[] arr = file1.list();

        File file;
        StringBuilder stringBuilder = new StringBuilder();
        int tekChar = -1;
        for(int i=0;i<arr.length;i++){
            if(i == 1){
                String agf="4564";
            }
            file = new File(root+"/folder/"+arr[i]);

            FileInputStream fileInputStream = new FileInputStream(file);

            try {
                stringBuilder.setLength(0);
                while ((tekChar=fileInputStream.read())!=-1)
                    stringBuilder.append((char)tekChar);
                vector.add(deSerializationZametka(String.valueOf(stringBuilder)));

            }catch (IOException | ClassNotFoundException e){
                e.printStackTrace();
            }
        }

        return vector;
    }
}
