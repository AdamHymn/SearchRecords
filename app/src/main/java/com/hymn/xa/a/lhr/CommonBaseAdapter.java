package com.hymn.xa.a.lhr;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * 公共适配器  用于ListView\GridView
 */
public abstract class CommonBaseAdapter<T> extends BaseAdapter {
    /**
     * 上下文对象
     **/
    protected Context mContext;
    /**
     * 数据源
     **/
    protected List<T> mDatas;

    /**
     * 布局ID
     **/
    private int layoutId;

    /**
     * 是否显示两条
     **/
    private boolean isShowTwo = false;

    /**
     * 子类构造方法中调用
     *
     * @param mContext
     * @param mDatas
     * @param layoutId
     */
    public CommonBaseAdapter(Context mContext, List<T> mDatas, int layoutId) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        this.layoutId = layoutId;
    }

    public CommonBaseAdapter(Context mContext, List<T> mDatas, int layoutId, boolean isShowTwo) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        this.layoutId = layoutId;
        this.isShowTwo = isShowTwo;
    }

    @Override
    public int getCount() {
        if (isShowTwo) {

            if (mDatas.size() > 2) {
                return 2;
            } else {
                return mDatas.size();
            }

        } else {
            return mDatas.size();
        }
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //初始化ViewHolder
        CommonViewHolder holder = CommonViewHolder.get(mContext, convertView, parent, layoutId, position);

        convert(holder, position, getItem(position));

        return holder.getConvertView();
    }

    //将convert方法公布出去
    public abstract void convert(CommonViewHolder holder, int position, T t);

    public void refreshData(List<T> mDatas) {
        this.mDatas = mDatas;
        notifyDataSetChanged();
    }
}
