package com.example.gouwuche.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gouwuche.MyAddSubView;
import com.example.gouwuche.R;
import com.example.gouwuche.bean.Gouwu;

import java.util.List;

public class MyAdapter extends BaseExpandableListAdapter {

    List<Gouwu.DataBean> list;

    public MyAdapter(List<Gouwu.DataBean> list) {
        this.list = list;
    }

    @Override
    public int getGroupCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return list.get(groupPosition).getList() == null ? 0 : list.get(groupPosition).getList().size();
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        Gouwu.DataBean dataBean = list.get(groupPosition);
        ParntViewHolder mParntViewHolder;
        if(convertView == null){
            convertView = View.inflate(parent.getContext(), R.layout.parnt,null);
            mParntViewHolder = new ParntViewHolder(convertView);
            convertView.setTag(mParntViewHolder);
        }else{
           mParntViewHolder = (ParntViewHolder) convertView.getTag();
        }
        mParntViewHolder.seller_cb.setText(dataBean.getSellerName());
        //根据当前商家的所有商品,确定该商家得checkbox是否被选中
        boolean currentSellerAllProductSelected = ischeckselect(groupPosition);
        //更新UI
        mParntViewHolder.seller_cb.setChecked(currentSellerAllProductSelected);

       mParntViewHolder.seller_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击商家得checkBox
                if (onCartListChangeListener != null) {
                    onCartListChangeListener.onSellerCheckedChange(groupPosition);
                }
            }
        });
        return convertView;
    }

    private class ParntViewHolder {
        public CheckBox seller_cb;
        public TextView seller_name_tv;

        public ParntViewHolder(View rootView) {
            this.seller_cb = (CheckBox) rootView.findViewById(R.id.seller_cb);
            this.seller_name_tv = (TextView) rootView.findViewById(R.id.seller_name_tv);
        }

    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        List<Gouwu.DataBean.ListBean> listBean = this.list.get(groupPosition).getList();
        Gouwu.DataBean.ListBean bean = listBean.get(childPosition);
        final ChildViewHolder mChildViewHolder;
        if(convertView == null){
            convertView = View.inflate(parent.getContext(),R.layout.child,null);
            mChildViewHolder = new ChildViewHolder(convertView);
            convertView.setTag(mChildViewHolder);
        }else{
            mChildViewHolder = (ChildViewHolder) convertView.getTag();
        }


        //获取到图片
        String images = listBean.get(childPosition).getImages();

        //截取图片数据
        String[] split = images.split("!");
        //获取到第一张图片
        Glide.with(parent.getContext()).load(split[0]).into(mChildViewHolder.product_icon_iv);

        //设置商品名字
        mChildViewHolder.product_title_name_tv.setText(bean.getTitle());
        //设置商品单价
        mChildViewHolder.product_price_tv.setText(bean.getPrice()+"");
        //设置复选框是否选中
        mChildViewHolder.child_cb.setChecked(bean.getSelected() == 1);
        //设置组合式自定义控件内部的数量
        mChildViewHolder.add_remove_view.setNumber(bean.getNum());

        mChildViewHolder.child_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onCartListChangeListener != null){
                    onCartListChangeListener.onProductCheckedChange(groupPosition,childPosition);
                }
            }
        });

        mChildViewHolder.add_remove_view.setOnNumberChangeListener(new MyAddSubView.OnNumberChangeListener() {
            @Override
            public void onNumberChange(int num) {
                if (onCartListChangeListener != null) {
                    onCartListChangeListener.onProductNumberChange(groupPosition, childPosition, num);
                }
            }
        });
        return convertView;
    }

    //当前商家所有商品是否被选中
    public boolean ischeckselect(int groupPosition){
        Gouwu.DataBean dataBean = list.get(groupPosition);
        List<Gouwu.DataBean.ListBean> data = dataBean.getList();
        for (Gouwu.DataBean.ListBean listdata : data) {
            if(listdata.getSelected() == 0){
                return false;
            }
        }
        return true;
    }


    //所有商品是否被选中,双重for循环,里面的集合套集合取数据
    public boolean isAllProductsSelected() {
        for (int i = 0; i < list.size(); i++) {
            Gouwu.DataBean dataBean = list.get(i);
            List<Gouwu.DataBean.ListBean> list1 = dataBean.getList();
            for (int j = 0; j <  list1.size(); j++) {
                if (list1.get(j).getSelected() == 0) {
                    return false;
                }
            }
        }
        return true;
    }




    //计算总的数量
    public int allNumber(){
        int number = 0;
        for (int i = 0; i < list.size(); i++) {
            Gouwu.DataBean dataBean = list.get(i);
            List<Gouwu.DataBean.ListBean> data = dataBean.getList();
            for (int j = 0; j < data.size(); j++) {
                if(data.get(j).getSelected() == 1){
                     int num = data.get(j).getNum();
                     number += num;
                }
            }
        }
        return number;
    }

    //计算总价
    public float allPrice(){
        float price = 0;
        for (int i = 0; i < list.size(); i++) {
            Gouwu.DataBean dataBean = list.get(i);
            List<Gouwu.DataBean.ListBean> data = dataBean.getList();
            for (int j = 0; j < data.size(); j++) {
                if(data.get(j).getSelected() == 1){
                    int num = data.get(j).getNum();
                    float mprice = data.get(j).getPrice();
                    price += num*mprice;
                }
            }
        }
        return price;
    }

    //当商家得checkbox被点击得时候调用，设置当前商家得所有商品得状态--------商家组其所有的商品
    public void  setcheckzhuangtai(int grouposition, boolean isSelected){
        Gouwu.DataBean dataBean = list.get(grouposition);
        List<Gouwu.DataBean.ListBean> data = dataBean.getList();
        for (int i = 0; i <data.size() ; i++) {
            Gouwu.DataBean.ListBean listBean = data.get(i);
            listBean.setSelected(isSelected ? 1 : 0);
        }
    }

    //当商品得checkbox被点击得时候调用，改变当前商品状态---------商品子条目
    public void setshangpin(int groupPosition, int childPosition){
        Gouwu.DataBean dataBean = list.get(groupPosition);
        List<Gouwu.DataBean.ListBean> data = dataBean.getList();
        Gouwu.DataBean.ListBean listBean = data.get(childPosition);
        listBean.setSelected(listBean.getSelected() == 0 ?1 : 0 );
    }

    //设置所有商品得状态
    public void  allshangpin(boolean selected){
        for (int i = 0; i < list.size(); i++) {
            Gouwu.DataBean dataBean = list.get(i);
            List<Gouwu.DataBean.ListBean> list1 = dataBean.getList();
            for (int j = 0; j < list1.size(); j++) {
                list1.get(j).setSelected(selected?1:0);
            }
        }
    }

    //当加减器被点击得时候调用，改变当前商品得数量
    public void changeCurrentProductNumber(int groupPosition, int childPosition, int number) {
        Gouwu.DataBean dataBean = list.get(groupPosition);
        List<Gouwu.DataBean.ListBean> listBeans = dataBean.getList();
        Gouwu.DataBean.ListBean listBean = listBeans.get(childPosition);
        listBean.setNum(number);
    }


    private class ChildViewHolder {
        public CheckBox child_cb;
        public ImageView product_icon_iv;
        public TextView product_title_name_tv;
        public TextView product_price_tv;
        public MyAddSubView add_remove_view;

        public ChildViewHolder(View rootView) {
            this.child_cb = (CheckBox) rootView.findViewById(R.id.child_cb);
            this.product_icon_iv = (ImageView) rootView.findViewById(R.id.product_icon_iv);
            this.product_title_name_tv = (TextView) rootView.findViewById(R.id.product_title_name_tv);
            this.product_price_tv = (TextView) rootView.findViewById(R.id.product_price_tv);
            this.add_remove_view = (MyAddSubView) rootView.findViewById(R.id.add_remove_view);
        }
    }


    onCartListChangeListener onCartListChangeListener;

    public void setOnCartListChangeListener(MyAdapter.onCartListChangeListener onCartListChangeListener) {
        this.onCartListChangeListener = onCartListChangeListener;
    }

    public interface onCartListChangeListener {

        void onSellerCheckedChange(int groupPosition);

        void onProductCheckedChange(int groupPosition, int childPosition);

        void onProductNumberChange(int groupPosition, int childPosition, int number);
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


}
