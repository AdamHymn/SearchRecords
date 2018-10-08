package com.hymn.xa.a.lhr;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CommonViewHolder extends RecyclerView.ViewHolder {

    //存放的view的map,这里我们使用SparseArray(key是固定的为int类型)
    private SparseArray<View> mViews;
    private View mConvertView;


    /**
     * RecyclerAdapter使用。
     * @param itemView
     */
    public CommonViewHolder(View itemView) {
        super(itemView);
        this.mViews = new SparseArray<View>();
        mConvertView=itemView;
    }


    /**
     * BaseAdapter调用
     */
    public CommonViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        super(LayoutInflater.from(context).inflate(layoutId, parent, false));
        this.mViews = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
    }
    /**
     * BaseAdapter调用，复用
     */
    public static CommonViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {
            return new CommonViewHolder(context, parent, layoutId, position);
        } else {
            CommonViewHolder holder = (CommonViewHolder) convertView.getTag();
            return holder;
        }
    }

    /**
     * 根据viewId获取view
     * @param viewId 通过viewId获取控件
     * @param <T>
     * @return 返回的是View(布局)的子类（控件）
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getConvertView() {
        return mConvertView;
    }
}