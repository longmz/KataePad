package com.katae.pad.utils.baseadapter.interfaces;

import com.katae.pad.utils.baseadapter.ViewHolder;

/**
 * Created by longmingzan on 2018/1/23.
 */
public interface OnMultiItemClickListeners<T> {
    void onItemClick(ViewHolder viewHolder, T data, int position, int viewType);
}
