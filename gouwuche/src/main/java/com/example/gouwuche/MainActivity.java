package com.example.gouwuche;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.gouwuche.adapter.MyAdapter;
import com.example.gouwuche.bean.Gouwu;
import com.example.gouwuche.presenter.Presenter;
import com.example.gouwuche.utils.Httputils;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ExpandableListView able;
    private CheckBox all;
    private TextView price;
    private Button jiesuan;
    Presenter presenter = new Presenter();
    MyAdapter ma;
    String url = "http://www.zhaoapi.cn/product/getCarts";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        initData();

    }

    private void initData() {

        HashMap<String,String> map = new HashMap<>();
        map.put("uid","71");
        //创建presenter层 new 一个实例
        //调用里面设置的方法
        presenter.Hzjpresenter(url, map, new Presenter.HzjInterface() {
            @Override//成功方法
            public void success(List<Gouwu.DataBean> data) {

                    ma = new MyAdapter(data);

                    //调用adapter里的接口回调给的方法
                    ma.setOnCartListChangeListener(new MyAdapter.onCartListChangeListener() {
                        @Override//点击商家的CheckBox的回调
                        public void onSellerCheckedChange(int groupPosition) {
                            //商家被点击
                            boolean ischeckselect = ma.ischeckselect(groupPosition);
                            ma.setcheckzhuangtai(groupPosition,!ischeckselect);
                            ma.notifyDataSetChanged();
                            //B.刷新底部数据
                            refreshselect();
                        }
                        //点击商品的CheckBox的回调
                        @Override
                        public void onProductCheckedChange(int groupPosition, int childPosition) {
                            ma.setshangpin(groupPosition,childPosition);
                            ma.notifyDataSetChanged();
                            //B.刷新底部数据
                            refreshselect();
                        }
                        //点击加减按钮的回调
                        @Override
                        public void onProductNumberChange(int groupPosition, int childPosition, int number) {
                            ma.changeCurrentProductNumber(groupPosition,childPosition,number);
                            ma.notifyDataSetChanged();
                            //B.刷新底部数据
                            refreshselect();
                        }
                    });
                    //设置适配器
                    able.setAdapter(ma);
                    //去掉箭头
                    able.setGroupIndicator(null);
                    for(int x=0; x<data.size(); x++){
                        able.expandGroup(x);
                    }
                    //调用刷新方法
                    refreshselect();
            }

            @Override//失败方法
            public void failed() {

            }
        });


    }



    //刷新总价方法
    private void refreshselect() {
        //去判断是否所有得商品都被选中
        boolean allProductsSelected = ma.isAllProductsSelected();
        //设置给全选checkBox
        all.setChecked(allProductsSelected);
        //计算总价
        float v = ma.allPrice();
        price.setText("总价:"+v);
        //计算总数量
        int totalNumber = ma.allNumber();
        jiesuan.setText("去结算(" + totalNumber + ")");

    }

    @Override
    public void onClick(View v) {

    }

    private void initView() {
        able = (ExpandableListView) findViewById(R.id.able);
        all = (CheckBox) findViewById(R.id.all);
        price = (TextView) findViewById(R.id.price);
        jiesuan = (Button) findViewById(R.id.jiesuan);
        jiesuan.setOnClickListener(this);

        //全选按钮的点击事件
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean allProductsSelected = ma.isAllProductsSelected();
                ma.allshangpin(!allProductsSelected);
                ma.notifyDataSetChanged();
                //刷新底部数据
                refreshselect();
            }
        });
    }
    //避免内存泄漏
    @Override
    protected void onDestroy() {
        super.onDestroy();

         presenter = null;
    }
}