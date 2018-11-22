package com.example.gouwuche.utils;

import android.os.Handler;
import android.os.Looper;

import com.example.gouwuche.LoggingInterceptor;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Httputils {
    private final Handler mHandler;
    private final OkHttpClient mOkHttpClient;

    //////////////////////////////////////单例模式///////////////////////////////////////
    private static Httputils sHttputils;

    //构造方法不私有
    private Httputils(){
        //如果创建Handler的对象,是在一个普通的类里创建,那么一定要加上Looper.getMainLooper()这个参数
        mHandler = new Handler(Looper.getMainLooper());

        //添加拦截器
        LoggingInterceptor loggingInterceptor = new LoggingInterceptor();

        mOkHttpClient = new OkHttpClient.Builder()
                //调用.add方法
                .addInterceptor(loggingInterceptor)
                .readTimeout(2000,TimeUnit.SECONDS)
                .writeTimeout(2000,TimeUnit.SECONDS)
                .connectTimeout(2000,TimeUnit.SECONDS)
                .build();
    }

    //单例暴露一个普通的方法,给对方,双重锁模式
    public static Httputils getInstace(){
        if(sHttputils == null){
            synchronized (Httputils.class){
                if(sHttputils == null){
                    return sHttputils = new Httputils();
                }
            }
        }
        return sHttputils;
    }

    //接口回调
    public interface Onhttputils{

        void response(String str);

        void failual(Exception e);
    }

    //封装doget方法  用get方法进行解析
    public void doGet(String url,final Onhttputils onhttputils){
        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = mOkHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if(onhttputils != null){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onhttputils.failual(e);
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String str = response.body().string();
                if(onhttputils != null){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onhttputils.response(str);
                        }
                    });
                }
            }
        });
    }

    //用post方法解析
    public void doPost(String url, Map<String,String> map, final Onhttputils onhttputils){
        FormBody.Builder builder = new FormBody.Builder();

        if(map != null){
            for (String key : map.keySet()) {
                builder.add(key,map.get(key));
            }
        }

        FormBody formBody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        Call call = mOkHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if(onhttputils != null){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onhttputils.failual(e);
                        }
                    });
                }
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String str = response.body().string();
                if(onhttputils != null){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onhttputils.response(str);
                        }
                    });
                }
            }
        });
    }
}
