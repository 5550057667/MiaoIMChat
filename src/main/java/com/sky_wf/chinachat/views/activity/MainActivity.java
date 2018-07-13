package com.sky_wf.chinachat.views.activity;

import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.sky_wf.chinachat.App;
import com.sky_wf.chinachat.R;
import com.sky_wf.chinachat.chat.listener.OnItemClickListener;
import com.sky_wf.chinachat.utils.Utils;
import com.sky_wf.chinachat.views.activity.base.BaseFragmentActivity;
import com.sky_wf.chinachat.views.activity.fragment.Fragment_Discover;
import com.sky_wf.chinachat.views.activity.fragment.Fragment_Friends;
import com.sky_wf.chinachat.views.activity.fragment.Fragment_Msg;
import com.sky_wf.chinachat.views.activity.fragment.Fragment_Porfile;
import com.sky_wf.chinachat.utils.Constansts;
import com.sky_wf.chinachat.utils.Debugger;
import com.sky_wf.chinachat.views.entity.ActionItem;
import com.sky_wf.chinachat.views.widgets.TitlePopWindow;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends BaseFragmentActivity implements OnItemClickListener
{
    private Fragment[] fragments;
    private Fragment_Msg msg_fragment;
    private Fragment_Friends friend_fragment;
    private Fragment_Discover discover_fragment;
    private Fragment_Porfile profile_fragment;
    private TextView unreadMsgLable;
    private TextView unreadAdressLable;
    private TextView unreadFindLable;
    private TextView unreadProfileLable;
    private ImageView[] imageBottom;// 底部img
    private TextView[] txtBottom;// 底部txt
    private TextView txt_title;
    private ImageView img_right;
    private TitlePopWindow titlePopWindow;// 弹出菜单

    private int currentTabIndex = 0;// 当前Fragment的index
    private int index = 0;

    private EMMessageListener emMessageListener;
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        Debugger.d(TAG, ">>onCreate<<");
        Utils.setStatusColor(this, true, false, getResources().getColor(R.color.bar_color));// 设置透明状态栏
        findViewById();
        initViews();
        initMessageReceiverListener();// 消息监听
        initTabView();// 底部tab
        initTitlePopWindow();// 弹出框
        EMClient.getInstance().chatManager().addMessageListener(emMessageListener);
    }

    private void initMessageReceiverListener()
    {
        emMessageListener = new EMMessageListener()
        {
            @Override
            public void onMessageReceived(List<EMMessage> list)
            {
                Debugger.d(TAG, "MainActivity 已经收到最新消息" + "消息数" + list.size());
                Observable.create(new Observable.OnSubscribe<Object>()
                {

                    @Override
                    public void call(Subscriber<? super Object> subscriber)
                    {
                        subscriber.onNext(null);

                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Object>()
                {

                    @Override
                    public void onCompleted()
                    {

                    }

                    @Override
                    public void onError(Throwable throwable)
                    {

                    }

                    @Override
                    public void onNext(Object o)
                    {
                        Debugger.d(TAG, "收到新消息开始刷新" + isMainThread());
                        refresh();
                    }
                });
                Debugger.d(TAG, "当前线程是否为主线程：" + isMainThread());

            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> list)
            {

            }

            @Override
            public void onMessageRead(List<EMMessage> list)
            {

            }

            @Override
            public void onMessageDelivered(List<EMMessage> list)
            {

            }

            @Override
            public void onMessageRecalled(List<EMMessage> list)
            {

            }

            @Override
            public void onMessageChanged(EMMessage emMessage, Object o)
            {

            }
        };
    }

    public static boolean isMainThread()
    {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        refresh();
    }

    private void refresh()
    {
        updateUnreadLabel();
        msg_fragment.refresh();
    }

    private void initViews()
    {
        img_right.setVisibility(View.VISIBLE);
        img_right.setImageResource(R.drawable.icon_add);
        txt_title.setVisibility(View.VISIBLE);

    }

    private void initTitlePopWindow()
    {
        titlePopWindow = new TitlePopWindow(this);
        titlePopWindow.setOnItemClickListener(this);
        titlePopWindow.addAcionItem(
                new ActionItem(this, R.drawable.icon_menu_group, R.string.pop_group_chat));
        titlePopWindow
                .addAcionItem(new ActionItem(this, R.drawable.icon_add, R.string.pop_add_friend));
        titlePopWindow
                .addAcionItem(new ActionItem(this, R.drawable.icon_menu_sao, R.string.pop_qrcode));
        titlePopWindow.addAcionItem(new ActionItem(this, R.drawable.abv, R.string.pop_pay));
        titlePopWindow.refresh();

    }

    private void findViewById()
    {
        img_right = (ImageView) findViewById(R.id.img_right);
        txt_title = (TextView) findViewById(R.id.txt_left_title);

    }

    private void initTabView()
    {
        unreadMsgLable = (TextView) findViewById(R.id.unread_msg_number);
        unreadAdressLable = (TextView) findViewById(R.id.unread_friend_number);
        unreadFindLable = (TextView) findViewById(R.id.unread_discover_number);
        unreadProfileLable = (TextView) findViewById(R.id.unread_profile_number);
        msg_fragment = new Fragment_Msg();
        friend_fragment = new Fragment_Friends();
        discover_fragment = new Fragment_Discover();
        profile_fragment = new Fragment_Porfile();
        fragments = new Fragment[] { msg_fragment, friend_fragment, discover_fragment,
                profile_fragment };
        imageBottom = new ImageView[4];
        imageBottom[0] = (ImageView) findViewById(R.id.img_chinachat);
        imageBottom[1] = (ImageView) findViewById(R.id.img_friend);
        imageBottom[2] = (ImageView) findViewById(R.id.img_discover);
        imageBottom[3] = (ImageView) findViewById(R.id.img_profile);
        imageBottom[0].setSelected(true);

        txtBottom = new TextView[4];
        txtBottom[0] = (TextView) findViewById(R.id.txt_chinachat);
        txtBottom[1] = (TextView) findViewById(R.id.txt_friend);
        txtBottom[2] = (TextView) findViewById(R.id.txt_discover);
        txtBottom[3] = (TextView) findViewById(R.id.txt_profile);
        txtBottom[0].setTextColor(0xFF45C01A);

        getSupportFragmentManager().beginTransaction().add(R.id.frame_container, msg_fragment)
                .add(R.id.frame_container, friend_fragment)
                .add(R.id.frame_container, discover_fragment)
                .add(R.id.frame_container, profile_fragment).hide(friend_fragment)
                .hide(profile_fragment).hide(discover_fragment).show(msg_fragment).commit();

    }

    /**
     * 根据点击，呈现对应的Fragment
     * 
     * @param view
     */
    public void OnTabClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.bottom_chinachat:
                index = Constansts.index_Msg;
                txt_title.setText("喵信");
                break;
            case R.id.bottom_friend:
                index = Constansts.index_Friend;
                break;
            case R.id.bottom_discover:
                index = Constansts.index_Discover;
                break;
            case R.id.bottom_profile:
                index = Constansts.index_Profile;
                break;
        }
        if (currentTabIndex != index)
        {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded())
            {
                transaction.add(R.id.frame_container, fragments[index]);
            }
            transaction.show(fragments[index]);
            transaction.commit();
        }
        imageBottom[currentTabIndex].setSelected(false);
        imageBottom[index].setSelected(true);
        txtBottom[currentTabIndex].setTextColor(0xFF999999);
        txtBottom[index].setTextColor(0xFF45C01A);
        currentTabIndex = index;

    }

    public void onPopTitleClick(View view)
    {
        titlePopWindow.show(findViewById(R.id.layout_bar));
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Debugger.d(TAG, ">>onStop<<");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Debugger.d(TAG, ">>onDestroy<<");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (event.getKeyCode())
        {
            case KeyEvent.KEYCODE_BACK:
                App.exitActivity();

        }
        return super.onKeyDown(keyCode, event);
    }

    public void updateUnreadLabel()
    {
        int count;
        count = EMClient.getInstance().chatManager().getUnreadMessageCount();
        Debugger.d(TAG, "消息列表未读消息总数：>>>>" + count);
        if (count > 0)
        {
            unreadMsgLable.setText(String.valueOf(count));
            unreadMsgLable.setVisibility(View.VISIBLE);
        } else
        {
            unreadMsgLable.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View view, int position)
    {

    }

    @Override
    public void onLongClick(View view, int position)
    {

    }
}
