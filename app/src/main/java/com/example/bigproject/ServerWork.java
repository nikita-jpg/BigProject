package com.example.bigproject;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

public class ServerWork {

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
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 1;
    }
    public int registerServer(String login, String password){
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
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
