package com.example.bigproject;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import static com.example.bigproject.MyService.context;

public class LocalBase {

    public static synchronized Zametka deSerializationZametka(String str) throws IOException,ClassNotFoundException{
        byte[] data = Base64.decode(str,0);
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data));

        Zametka zametka = (Zametka) objectInputStream.readObject();
        objectInputStream.close();
        return zametka;
    }

    private static synchronized String serializationZametke(Serializable serializable) throws IOException,ClassNotFoundException{
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

        objectOutputStream.writeObject(serializable);
        objectOutputStream.close();

        return Base64.encodeToString(byteArrayOutputStream.toByteArray(),0);
    }

    //true-всё хорошо. false - всё плохо
    public static synchronized boolean save(Zametka zametka){

        //Получаем путь к папке приложения
        String root = String.valueOf(context.getFilesDir());
        //Имя файла = дата создания заметки
        File file1 = new File(root+"/folder/"+zametka.getData()+".txt");
        try {
            String zamStr = serializationZametke(zametka);
            FileOutputStream fileOutputStream = new FileOutputStream(file1);
            fileOutputStream.write(zamStr.getBytes());
            fileOutputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
