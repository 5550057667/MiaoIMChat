package com.sky_wf.chinachat.views.entity;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * @Date : 2018/7/6
 * @Author : WF
 * @Description :弹窗的内部子类项
 */
public class ActionItem
{
    public Drawable mDrawable;
    public CharSequence mTitle;

    public ActionItem(Drawable mDrawable, CharSequence mTitle)
    {
        this.mDrawable = mDrawable;
        this.mTitle = mTitle;
    }

    public ActionItem(Context context, int drawableId, int titleId)
    {
        this.mTitle = context.getResources().getString(titleId);
        this.mDrawable = context.getResources().getDrawable(drawableId);
    }
}
