package com.katae.pad.utils;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by longm on 2016/12/4.
 */

public class RecyclerViewHolder extends RecyclerView.ViewHolder {
    // 所有控件集合
    private SparseArray<View> mViews;

    public final View mView;

    public RecyclerViewHolder(View view) {
        super(view);
        mView = view;
        mViews = new SparseArray<>();
    }

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 为文本设置text
     *
     * @param viewId view的Id
     * @param text   文本
     * @return 返回ViewHolder
     */
    public RecyclerViewHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    /**
     * 设置ImageView
     *
     * @param viewId view的Id
     * @param resId  资源Id
     * @return 返回ViewHolder
     */
    public RecyclerViewHolder setImage(int viewId, int resId) {
        ImageView iv = getView(viewId);
        iv.setImageResource(resId);
        return this;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
