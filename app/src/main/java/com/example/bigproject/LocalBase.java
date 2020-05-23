package com.example.bigproject;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import com.scottyab.aescrypt.AESCrypt;
import com.yakivmospan.scytale.Crypto;
import com.yakivmospan.scytale.KeyProps;
import com.yakivmospan.scytale.Options;
import com.yakivmospan.scytale.Store;


import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Handler;

import javax.crypto.SecretKey;
import javax.security.auth.x500.X500Principal;

import okhttp3.ResponseBody;

import static android.content.ContentValues.TAG;

public class LocalBase {

    private static Context context;
    private static String root = "";
    private static String folderForZametka = "/zametka";
    private static String folderForImages = "";
    private static String storeName = "";
    private static String storePassword = "";
    private static String alias = "";
    private static String keyPairPassword = "";
    private static String keySecterPassword = "";
    private static android.os.Handler handler = null;


    /* Инициализация важных переменных из config.propirties */
    public static void setHandler(android.os.Handler handlerArg){
        handler = handlerArg;
    }
    public static void initialization(Context contextArg)
    {
        context = contextArg;
        root = String.valueOf(context.getFilesDir());
        Properties properties = new Properties();
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open("config.properties");
            properties.load(inputStream);
            folderForZametka = properties.getProperty("folderForZametka");
            folderForImages = properties.getProperty("folderForImages");

            storeName = properties.getProperty("storeName");
            alias= properties.getProperty("alias");
            storePassword = properties.getProperty("storePassword");
            keyPairPassword = properties.getProperty("keyPairPassword");
            keySecterPassword = properties.getProperty("keySecterPassword");

            firstSetting();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Когда заходит неавторизованный пользователь */
    private static void makeKeys()
    {

        final Calendar start = Calendar.getInstance();
        final Calendar end = Calendar.getInstance();
        end.add(Calendar.YEAR, 100);

        KeyProps keyProps = new KeyProps.Builder()
                .setAlias(alias)
                .setPassword(keyPairPassword.toCharArray())
                .setKeySize(512)
                .setKeyType("RSA")
                .setSerialNumber(BigInteger.ONE)
                .setSubject(new X500Principal("CN=" + alias + " CA Certificate"))
                .setStartDate(start.getTime())
                .setEndDate(end.getTime())
                .setBlockModes("ECB")
                .setEncryptionPaddings("PKCS1Padding")
                .setSignatureAlgorithm("SHA256WithRSAEncryption")
                .build();

        Store store = new Store(context,storeName,storePassword.toCharArray());
        store.generateAsymmetricKey(keyProps);
        store.generateSymmetricKey(alias,keySecterPassword.toCharArray());
    }
    public static void firstSetting()
    {
        //Создаём папку для хранения заметок
        File file1 = new File(root + folderForZametka);
        if (!file1.exists()) {
            file1.mkdir();
        }
        //Создаём папку для хранения изображений
        File file2 = new File(root + folderForImages);
        if (!file2.exists()) {
            file2.mkdir();
        }
        //Создаём ключи для шифрования
        makeKeys();
    }

    /* Шифрование */
    public static synchronized String encode(String value)
    {

        Store store = new Store(context,storeName,storePassword.toCharArray());
        String encryptedData = null;
        try {
            encryptedData = AESCrypt.encrypt(store.getSymmetricKey(alias,keySecterPassword.toCharArray()).toString(),value);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return encryptedData;
    }
    public static synchronized String deCode(String value)
    {

        Store store = new Store(context,storeName,storePassword.toCharArray());
        String decryptedData = null;
        try {
            decryptedData = AESCrypt.decrypt(store.getSymmetricKey(alias,keySecterPassword.toCharArray()).toString(),value);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return decryptedData;
    }


    /* Сереализация */
    private static synchronized String serializationZametke(Serializable serializable) throws IOException
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(serializable);
        objectOutputStream.close();

        return Base64.encodeToString(byteArrayOutputStream.toByteArray(),0);
    }
    public static synchronized Zametka deSerializationZametka(String str) throws IOException,ClassNotFoundException
    {
        byte[] data = Base64.decode(str,0);
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data));

        Zametka zametka = (Zametka) objectInputStream.readObject();
        objectInputStream.close();
        return zametka;
    }


    /* Сохранение данныых на устройстве */
    //Сохраняем картинку в папку
    public static synchronized boolean saveStrBitmap(String name, String strBtm)
    {
        if(strBtm.length() == 0) return false;

        try {

            String strBtmEncode = encode(strBtm);
            char[] arrS = deCode(strBtmEncode).toCharArray();
            String arr = deCode(strBtmEncode);
            Bitmap bitmap = ZametkaWork.deSerializationBitmap(String.valueOf(arrS));

            File file1 = new File(root + folderForImages + "/" + name + ".txt");
            FileOutputStream fileOutputStream = new FileOutputStream(file1);

            fileOutputStream.write(strBtmEncode.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    //Сохраняем заметку в папку
    public static synchronized boolean saveZamNotBtm(Zametka zametka)
    {
        zametka.setBitmap("");
        File file1 = new File(root + folderForZametka +"/"+ zametka.getData() + ".txt");
        try {
            String zamStr = serializationZametke(zametka);
            String zamStrEnc = encode(zamStr);

            FileOutputStream fileOutputStream = new FileOutputStream(file1);
            fileOutputStream.write(zamStrEnc.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static synchronized boolean save(Zametka zametka)
    {
        //Проверяем, содержит ли заметка uri
       // boolean haveUri = false;
        //if(!zametka.getUri().equals("")) haveUri = true;
        //zametka.setUri("");

        //Если заметка содержит картинку и нам удалось её сохранить, то идём сохранять текст
        if(!zametka.getBitmap().equals(""))
        {
            if (saveStrBitmap(zametka.getData(), zametka.getBitmap()))
                if(saveZamNotBtm(zametka))
                {
                    updateUI();
                    ServerWork serverWork = new ServerWork(context);
                    serverWork.upload(zametka.getData(),context);
                    return true;
                }
                else
                    return false;
            else
                return false;
        }else {
            if(saveZamNotBtm(zametka))
            {
                updateUI();
                ServerWork serverWork = new ServerWork(context);
                serverWork.upload(zametka.getData(),context);
                return true;
            }
            else
                return false;
        }
    }


    /* Удаление звсетки */
    public static synchronized void deleteZam(String data)
    {
        File fileZam = new File(root + folderForZametka + "/" + data + ".txt");
        fileZam.delete();
        File fileImage = new File(root + folderForImages + "/" + data + ".txt");
        fileImage.delete();
        updateUI();
    }

    //При выходе из аккаунта удаляем все данные
    public static void deleteBase()
    {
        File file1 = new File(root + folderForZametka+"/");
        if (file1.exists()) {
            try {
                FileUtils.deleteDirectory(file1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File file2 = new File(root + folderForImages);
        if (file2.exists()) {
            try {
                FileUtils.deleteDirectory(file2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    /* Получаем данные */
    //Получаем вектор,содержащий все заметки в памяти устройства. Название заметки и её картинки совпадают
    public static synchronized List<Zametka> getZamLocal() throws FileNotFoundException
    {
        List<Zametka> list =new ArrayList<Zametka>();

        File folder = new File(root+folderForZametka);


        String[] listOfFiles;
        if(folder.list() == null) return list;
        else {
            listOfFiles = folder.list();
        }

        File file;
        StringBuilder stringBuilder = new StringBuilder();
        String len = "";
        for(int i=0;i<listOfFiles.length;i++){

            file = new File(root+folderForZametka+"/"+listOfFiles[i]);

            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

            try {
                stringBuilder.setLength(0);
                while (( len=bufferedReader.readLine() ) != null)
                    stringBuilder.append(len);

                list.add(deSerializationZametka(deCode(String.valueOf(stringBuilder))));
                fileInputStream.close();

            }catch (IOException | ClassNotFoundException e){
                e.printStackTrace();
            }
        }
        return list;
    }
    //Получаем картинку по её имени
    public static synchronized Bitmap getBitmap(String name) throws FileNotFoundException {
        Bitmap bitmap = null;
        StringBuilder stringBuilder = new StringBuilder();
        String len = "";
        FileInputStream fileInputStream = new FileInputStream(root+folderForImages+"/"+name+".txt");
        BufferedReader bufferedReader;
        String text="";

        try
        {
            byte[] bytes = new byte[fileInputStream.available()];
            fileInputStream.read(bytes);
            text = new String(bytes);
            fileInputStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return bitmap;
        }

        bitmap = ZametkaWork.deSerializationBitmap(deCode(text));
        return bitmap;
    }
    //Получаем картинку по её имени
    public static synchronized boolean chheckBitmap(String name) {
        File file = new File(root+folderForImages+"/"+name+".txt");
        return file.exists();
    }

    /* Проверяет, открыто ли основное окно приложения. И если да,то перерисовывает интерфейс  */
    private static void updateUI()
    {
        Message message = new Message();
        handler.sendMessage(message);
    }

    public static synchronized boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
            String a = body.string();
            a = a.substring(5,17);
            String b = body.toString();
            File futureStudioIconFile = new File(root + "/" + folderForZametka +"/" + a);

            if(!futureStudioIconFile.exists()) return false;

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}

