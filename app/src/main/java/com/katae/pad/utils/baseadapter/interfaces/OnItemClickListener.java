package com.katae.pad.utils.baseadapter.interfaces;

import com.katae.pad.utils.baseadapter.ViewHolder;

/**
 * Created by longmingzan on 2018/1/23.
 */
public interface OnItemClickListener<T> {
    void onItemClick(ViewHolder viewHolder, T data, int position);
}
