package com.example.bigproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.async.http.body.MultipartFormDataBody;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PublicKey;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;
import static com.example.bigproject.MainActivity.APP_PREFERENCES;

public class ServerWork {
    private static String defaultHost = "https://38c7363d.ngrok.io/";
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
                saveServer();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    public void saveServer(){
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
                    if(arr[0].equals("0")) return;
                    String b = "4";

                    for(int i=0;i<arr.length;i++)
                    {
                        Gson gson1 = new GsonBuilder()
                                .setLenient()
                                .create();
                        Retrofit retrofit1 = new Retrofit.Builder()
                                .baseUrl(defaultHost)
                                .addConverterFactory(GsonConverterFactory.create(gson))
                                .build();
                        ServerApi registrationApi1 = retrofit.create(ServerApi.class);
                        Call<ResponseBody> request1 = registrationApi1.downloadFile(auth,arr[i]);

                        Response<ResponseBody> file = request1.execute();
                        LocalBase.writeResponseBodyToDisk(file.body());
                        String c ="4";

                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
    public static synchronized void upload(String name,Context con){

        AsyncHttpPost post = new AsyncHttpPost(defaultHost+"uploadFile");
        MultipartFormDataBody body = new MultipartFormDataBody();
        File file1 = new File(root+"/"+folderForZametka+"/"+name+".txt");
        if(!file1.exists()){
            return;
        }

        body.addFilePart("zam", file1);
        //body.addFilePart("img", file2);
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

    }

}
