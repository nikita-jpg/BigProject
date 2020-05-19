package com.example.bigproject;

import android.os.Build;
import android.util.Base64;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerWork {
    private static String defaultHost = "http://192.168.1.7:8080/";
    private static KeyPair keyPairForHost = null;
    private static PublicKey pkFromHost = null;

    //Получаем от сервера публичный ключ
    private PublicKey getPublickKeyForAutorization()
    {
        return null;
    }

    public int checkAutorizationServer(String login, String password){
        /* Код для работы с сервером
        PublicKey publicKey = this.getPublickKeyForAutorization();
        if(publicKey == null) return false;
        Cipher cipher = null;
        String dataToServer = "";
        byte[] encodedBytes = null;
        try {
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE,publicKey);
            //encodedBytes = cipher.doFinal();
        } catch (NoSuchAlgorithmException |NoSuchPaddingException|InvalidKeyException e) {
            e.printStackTrace();
            return false;
        }
         */
        return 0;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public int registerServer(String login, String password) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        pkFromHost = getPublicKeyForAuth();

        /*
        //Test
        Cipher cipher = null;
        String test ="";


        byte[] bytes = null;
        byte[] prom = null;
        KeyPair keyPair = null;
        try {
            keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        try {
            cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            prom = login.getBytes();
            bytes = cipher.doFinal(prom);
            test = Arrays.toString(bytes);
        } catch (NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        String[] byteValues = test.substring(1, test.length() - 1).split(",");
        byte[] bytes1 = new byte[byteValues.length];

        for (int i=0, len=bytes1.length; i<len; i++)
        {
            bytes1[i] = Byte.parseByte(byteValues[i].trim());
        }

        try {
            Cipher cipher2 = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
            cipher2.init(Cipher.DECRYPT_MODE,keyPair.getPrivate());
            byte[] req = cipher2.doFinal(bytes1);

            test = new String(req, "UTF-8");
        } catch (NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //Test
         */

        login = encodeForHost(login);
        password = encodeForHost(password);
        keyPairForHost = KeyPairGenerator.getInstance("RSA").generateKeyPair();

        String normPkToHost = Arrays.toString(keyPairForHost.getPublic().getEncoded());
        String arr[] = new String[11];
        arr[0] = login;
        arr[1] = password;
        for(int i =2;i<9;i++)
            arr[i] = encodeForHost(normPkToHost.substring( (i-2)*(normPkToHost.length()/8),(i-1)*(normPkToHost.length()/8)));
        arr[9] = encodeForHost(normPkToHost.substring(7*normPkToHost.length()/8));

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        String[] reqs = regPerson(arr);

        String[] byteValues = reqs[0].substring(1, reqs[0].length() - 1).split(",");
        byte[] bytes = new byte[byteValues.length];

        for (int i=0; i<bytes.length; i++) {
            bytes[i] = Byte.parseByte(byteValues[i].trim());
        }
        pkFromHost = keyFactory.generatePublic(new X509EncodedKeySpec(bytes));
        return 1;
    }

    //Методы регистрации на сервере
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static String[] regPerson(String[] arr)
    {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(defaultHost)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        RegistrationApi registrationApi = retrofit.create(RegistrationApi.class);
        //Получаем строку вида Pk:Код индентификации


        Call<String[]> request = registrationApi.regPerson(arr);

        String[] requestArr = null;
        String reqStr="";
        try
        {
            requestArr = request.execute().body();
            for(int i=0;i<10;i++)
                reqStr+= decodeFrorHost(requestArr[i]);
            String[] reqs = reqStr.split(":");
            return reqs;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static PublicKey getPublicKeyForAuth()
    {
        //Подготовка к отправек запроса
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(defaultHost)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        RegistrationApi restApi = retrofit.create(RegistrationApi.class);

        Call<String> pkKey = restApi.getPublicKeyForReg();
        PublicKey publicKey =null;

        //Отправляем запрос
        try {
            KeyFactory factory = KeyFactory.getInstance("RSA");

            String test = pkKey.execute().body();

            String[] byteValues = test.substring(1, test.length() - 1).split(",");
            byte[] bytes = new byte[byteValues.length];

            for (int i=0, len=bytes.length; i<len; i++) {
                bytes[i] = java.lang.Byte.parseByte(byteValues[i].trim());
            }

            publicKey =  factory.generatePublic(new X509EncodedKeySpec(bytes));

        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return publicKey;
        }
        return publicKey;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static String encodeForHost(String data){

        try {
            javax.crypto.Cipher cipher =javax.crypto.Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, pkFromHost);
            byte[] bytes = cipher.doFinal(data.getBytes());
            String req = java.util.Arrays.toString(bytes);
            return req;
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return "";
    }
    private static String decodeFrorHost(String data){

        String string ="";
        try {
            String[] byteValues = data.substring(1, data.length() - 1).split(",");
            byte[] bytes = new byte[byteValues.length];

            for (int i=0, len=bytes.length; i<len; i++)
            {
                bytes[i] = Byte.parseByte(byteValues[i].trim());
            }
            Cipher cipher2 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher2.init(Cipher.DECRYPT_MODE,keyPairForHost.getPrivate());
            byte[] req = cipher2.doFinal(bytes);
            string = new String(req, "UTF-8");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return string;
    }
}
