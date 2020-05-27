package com.example.bigproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.async.http.body.MultipartFormDataBody;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.bigproject.MainActivity.APP_PREFERENCES;

public class ServerWork {
    private static String defaultHost = "https://920ddfa6bc10.ngrok.io/";
    private static SharedPreferences mSittings;
    private static final String AUTHTOHOST = "AuthToHost";
    private static final String folderForZametka="zametka";
    private static final String folderForImages="images";
    private static String root = "";
    private static Context context;


    ServerWork(Context context){
        this.context = context;
        root = String.valueOf(context.getFilesDir());

    }



    public int regAutServer(String login, String password, String regOrAut)
    {
        //Готовим запрос
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(defaultHost)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        ServerApi registrationApi = retrofit.create(ServerApi.class);
        Call<String> request;
        if(regOrAut.equals("reg"))
            request = registrationApi.regPerson(login,password);
        else
            request = registrationApi.autPerson(login,password);
        String req;

        //Делаем запрос
        try {
            req = request.execute().body();
            if(req == null) return 0;
            String[] requests = req.split(":");
            //Проверяем код ответа сервера
            if(!requests[0].equals("1"))
                return Integer.parseInt(requests[0]);

            //Шифруем файл для автоавторизации
            //req = LocalBase.encode(requests[1]);
            //Cохраняем файл для автоавторизации
            mSittings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSittings.edit();
            editor.putString(AUTHTOHOST,requests[1]);
            editor.commit();

            if(!regOrAut.equals("reg"))
                saveFromServer();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    public void saveFromServer(){
        android.os.Handler handler = new Handler()
        {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

            }
        };

        mSittings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        final String auth = mSittings.getString(AUTHTOHOST,"");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(defaultHost)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
                ServerApi registrationApi = retrofit.create(ServerApi.class);
                Call<String[]> request = registrationApi.getFileNameArr(auth);

                try {
                    String[] arr  = request.execute().body();
                    String[] arr2;
                    if(arr[0].equals("0")) return;
                    //Переделываем
                    for(int i=0;i<arr.length;i++)
                    {
                        ServerApi registrationApi1 = retrofit.create(ServerApi.class);
                        Call<String[]> request1 = registrationApi1.uploadText(auth,arr[i]);
                        arr2 = request1.execute().body();
                        if(arr2[0].equals("0")) return;

                        String name = arr2[1].substring(0,arr2[1].indexOf("|"));
                        name = name.replaceAll("_",":");
                        String data = arr2[1].substring(arr2[1].indexOf("|"));
                        LocalBase.writeResponseBodyToDisk(data,name);

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        });
        thread.start();
    }

    //Переделываем
    //true - загружаем ещё и картинку,false - только текст
    public static synchronized void upload(final String name, Context con, boolean imgBol) throws IOException {

        AsyncHttpPost post = new AsyncHttpPost(defaultHost+"uploadText");
        MultipartFormDataBody body = new MultipartFormDataBody();
        File file1 = new File(root+"/"+folderForZametka+"/"+name+".txt");
        if(!file1.exists()){
            return;
        }


        body.addFilePart("zam", file1);
        mSittings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        body.addStringPart("foo", mSittings.getString(AUTHTOHOST,""));
        body.addStringPart("fileName",name);
        post.setBody(body);
        AsyncHttpClient.getDefaultInstance().executeString(post, new AsyncHttpClient.StringCallback(){
            @Override
            public void onCompleted(Exception ex, AsyncHttpResponse source, String result) {
                if (ex != null) {
                    ex.printStackTrace();
                    return;
                }
                System.out.println("Server says: " + result);
            }
        });

        if(imgBol)
        {
            File file2 = new File(root+"/"+folderForImages+"/"+name+".txt");
            if(!file2.exists()){
                return;
            }
            final byte[] bytes = IOUtils.toByteArray(new FileInputStream(file2));
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    final String fileName = name;
                    Gson gson = new GsonBuilder()
                            .setLenient()
                            .create();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(defaultHost)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();
                    ServerApi registrationApi = retrofit.create(ServerApi.class);
                    Call<Integer> request = registrationApi.uploadImage( mSittings.getString(AUTHTOHOST,""),fileName,bytes);
                    try {
                        request.execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }




    }

}
