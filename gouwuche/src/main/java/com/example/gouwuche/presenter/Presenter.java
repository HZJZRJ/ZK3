package com.example.gouwuche.presenter;

import com.example.gouwuche.bean.Gouwu;
import com.example.gouwuche.utils.Httputils;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

public class Presenter {
                                                                   //把接口传进来
    public void Hzjpresenter(String url, Map<String,String> map,final HzjInterface hzjInterface){
        Httputils httputils = Httputils.getInstace();

        //获取封装okhttp中调用dopost方法
        httputils.doPost(url, map, new Httputils.Onhttputils() {
            @Override
            public void response(String str) {
                Gouwu gouwu = new Gson().fromJson(str, Gouwu.class);
                List<Gouwu.DataBean> data = gouwu.getData();
                //调用接口中成功方法
                hzjInterface.success(data);
            }
            @Override
            public void failual(Exception e) {
            }
        });
    }

    //实现接口,里面返回两个方法
    public interface HzjInterface{
        //成功方法
        void success(List<Gouwu.DataBean> data);
        //失败
        void failed();
    }
}
