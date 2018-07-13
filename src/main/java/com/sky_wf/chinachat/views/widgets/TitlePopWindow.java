package com.sky_wf.chinachat.views.widgets;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sky_wf.chinachat.R;
import com.sky_wf.chinachat.chat.adapter.BaseHolder;
import com.sky_wf.chinachat.chat.listener.OnItemClickListener;
import com.sky_wf.chinachat.utils.AppUtils;
import com.sky_wf.chinachat.utils.Utils;
import com.sky_wf.chinachat.views.entity.ActionItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Date : 2018/7/6
 * @Author : WF
 * @Description :弹窗
 */
public class TitlePopWindow extends PopupWindow
{

    private RecyclerView recyclerTitle;
    private TitlePopAdapter adapter;
    private Context mContext;
    private OnItemClickListener clickListener;
    private List<ActionItem> mActionItems = new ArrayList<ActionItem>();

    public TitlePopWindow(Context mContext)
    {
        this(mContext, FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
    }

    public TitlePopWindow(Context mContext, int width, int height)
    {
        this.mContext = mContext;
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);

        setWidth(width);
        setHeight(height);
        setBackgroundDrawable(new BitmapDrawable());
        setContentView(LayoutInflater.from(mContext).inflate(R.layout.layout_titlepop, null));
        setAnimationStyle(R.style.AnimTitlePop);
        initViews();
    }

    public void addAcionItem(ActionItem item)
    {
        if (mActionItems != null)
        {
            mActionItems.add(item);
        }
    }

    public void refresh()
    {
        adapter.notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener clickListener)
    {
        this.clickListener = clickListener;
    }

    private void initViews()
    {
        recyclerTitle = getContentView().findViewById(R.id.recycler_title);
        adapter = new TitlePopAdapter(mContext, mActionItems);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerTitle.setLayoutManager(manager);
        recyclerTitle.setAdapter(adapter);
        recyclerTitle.addItemDecoration(new TitlePopSpaceDecoraion(50));
        recyclerTitle.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL));
    }

    public void show(View view)
    {
        int[] locations = new int[2];
        view.getLocationOnScreen(locations);
        showAtLocation(view, Gravity.NO_GRAVITY,
                Utils.getScreenWidth(mContext)-getWidth()/2-20,
                locations[1] + view.getHeight());
    }

    class ActionHolder extends BaseHolder
    {
        @BindView(R.id.img_item)
        ImageView img_icon;
        @BindView(R.id.tv_item)
        TextView tv_acion_name;

        public ActionHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        @Override
        public void onClick(View v)
        {
            int pos = this.getLayoutPosition();
            clickListener.onClick(v, pos);
        }

        @Override
        public boolean onLongClick(View v)
        {
            int pos = this.getLayoutPosition();
            clickListener.onLongClick(v, pos);
            return false;
        }
    }

    class TitlePopAdapter extends RecyclerView.Adapter<ActionHolder>
    {
        private List<ActionItem> mActionItems;
        private LayoutInflater inflater;
        private Context mContext;

        public TitlePopAdapter(Context mContext, List<ActionItem> list)
        {
            this.mContext = mContext;
            this.mActionItems = list;
            inflater = LayoutInflater.from(mContext);
        }

        @NonNull
        @Override
        public ActionHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
        {
            View itemView = inflater.inflate(R.layout.item_pop_action, null);
            return new ActionHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ActionHolder actionHolder, int i)
        {
            ActionHolder holder = actionHolder;
            ActionItem item = mActionItems.get(i);
            holder.tv_acion_name.setText(item.mTitle.toString());
            holder.img_icon.setImageDrawable(item.mDrawable);
        }

        @Override
        public int getItemCount()
        {
            return mActionItems.size();
        }
    }
}
