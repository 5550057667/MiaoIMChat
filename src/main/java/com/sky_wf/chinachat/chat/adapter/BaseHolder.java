package com.sky_wf.chinachat.chat.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sky_wf.chinachat.chat.listener.OnItemClickListener;

/**
 * @Date : 2018/6/15
 * @Author : WF
 * @Description :
 */
public abstract class BaseHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener, View.OnLongClickListener
{
    protected OnItemClickListener itemClickListener;
    public BaseHolder(View itemView)
    {
        super(itemView);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);

    }

}
