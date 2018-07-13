package com.sky_wf.chinachat.views.widgets;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @Date : 2018/7/10
 * @Author : WF
 * @Description :
 */
public class TitlePopSpaceDecoraion extends RecyclerView.ItemDecoration
{
    private int space;

    public TitlePopSpaceDecoraion(int space)
    {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
       outRect.left = space;
       outRect.bottom = space;
       outRect.right = space;
       if(parent.getChildAdapterPosition(view)==0)
       {
           outRect.top = space;
       }
    }
}
