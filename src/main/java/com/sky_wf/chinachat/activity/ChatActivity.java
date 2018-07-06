package com.sky_wf.chinachat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.sky_wf.chinachat.App;
import com.sky_wf.chinachat.R;
import com.sky_wf.chinachat.activity.base.BaseActivity;
import com.sky_wf.chinachat.chat.adapter.MessageAdapter;
import com.sky_wf.chinachat.chat.adapter.MsgListAdapter;
import com.sky_wf.chinachat.chat.listener.OnItemClickListener;
import com.sky_wf.chinachat.chat.utils.ChatConstants;
import com.sky_wf.chinachat.chat.views.PaseteEditText;
import com.sky_wf.chinachat.utils.Constansts;
import com.sky_wf.chinachat.utils.Debugger;
import com.sky_wf.chinachat.utils.Utils;

import java.sql.Time;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * @Date : 2018/6/8
 * @Author : WF
 * @Description :聊天Activity
 */
public class ChatActivity extends BaseActivity implements OnItemClickListener
{

    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.img_right)
    ImageView imgRight;
    @BindView(R.id.btn_mode_voice)
    Button btnVoiceMode;
    @BindView(R.id.btn_keyboard_mode)
    Button btnKeyboardMode;
    @BindView(R.id.btn_press_to_speak)
    LinearLayout btnPressToSpeak;
    @BindView(R.id.et_message)
    PaseteEditText etMessage;
    @BindView(R.id.iv_emotions_normal)
    ImageView ivEmotionsNormal;
    @BindView(R.id.iv_emotions_enable)
    ImageView ivEmotionsEnable;
    @BindView(R.id.et_layout)
    RelativeLayout etLayout;
    @BindView(R.id.btn_more)
    Button btnMore;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.linear_chat_bottom)
    LinearLayout linearChatBottom;
    @BindView(R.id.chat_vPager)
    ViewPager chatVPager;
    @BindView(R.id.chat_face_container)
    LinearLayout chatFaceContainer;
    @BindView(R.id.view_photo)
    LinearLayout viewPhoto;
    @BindView(R.id.view_camera)
    LinearLayout viewCamera;
    @BindView(R.id.view_location)
    LinearLayout viewLocation;
    @BindView(R.id.view_file)
    LinearLayout viewFile;
    @BindView(R.id.view_audio)
    LinearLayout viewAudio;
    @BindView(R.id.view_video)
    LinearLayout viewVideo;
    @BindView(R.id.chat_tools_container)
    LinearLayout chatToolsContainer;
    @BindView(R.id.more)
    LinearLayout more;
    @BindView(R.id.chat_bottom_bar)
    LinearLayout chatBottomBar;
    @BindView(R.id.pg_load_more)
    ProgressBar pgLoadMore;
    @BindView(R.id.chat_content)
    RecyclerView chatContent;
    @BindView(R.id.img_chat_talk)
    ImageView imgChatTalk;
    @BindView(R.id.recording_voice)
    TextView recordingVoice;
    @BindView(R.id.view_talk)
    LinearLayout viewTalk;

    private final int pageSize = 20;
    private String username;
    private int chatType;
    private int group_number;
    private String chat_id;
    private Context context;
    private EMConversation conversation;
    private EMMessageListener msgListener;
    private MessageAdapter messageAdapter;
    private final String TAG = "ChatActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        getIntentData();
        super.onCreate(savedInstanceState);
        context = this;
        if (conversation != null)
        {
            messageAdapter = new MessageAdapter(conversation, context);
        } else
        {
            Debugger.d(TAG, "conversation is null!!!!");
        }
        messageAdapter.setOnItemClickListener(this);
        chatContent.setAdapter(messageAdapter);
        if (msgListener != null)
        {
            EMClient.getInstance().chatManager().addMessageListener(msgListener);
        }

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        App.acquireWakeLock();
        messageAdapter.refresh();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        App.releaseWakeLock();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (msgListener != null)
        {
            EMClient.getInstance().chatManager().removeMessageListener(msgListener);
        }
    }

    private void getIntentData()
    {
        Intent intent = getIntent();
        chat_id = intent.getStringExtra(Constansts.User_ID);
        username = intent.getStringExtra(Constansts.USERNAME);
        chatType = intent.getIntExtra(Constansts.TYPE, 0);

        if (chatType == ChatConstants.CHAT_GROUP)
        {
            group_number = intent.getIntExtra(Constansts.GROUP_NUMBER, 0);
        } else
        {
            if (null == username)
            {
                username = "好友";
            }
        }
        if (null != chat_id)
        {
            conversation = EMClient.getInstance().chatManager().getConversation(chat_id);
        }
        Debugger.d(TAG, "chat_id==" + chat_id + "username==" + username + "chatType==" + chatType
                + "groupnumber==" + group_number);
    }

    @Override
    protected void initTitle()
    {
        txtLeft.setVisibility(View.VISIBLE);
        if (chatType == ChatConstants.CHAT_GROUP)
        {
            txtLeft.setText(username + "(" + group_number + ")");
        } else
        {
            txtLeft.setText(username);
        }
        imgBack.setVisibility(View.VISIBLE);
        imgRight.setVisibility(View.VISIBLE);
        imgRight.setImageResource(R.drawable.more);

    }

    @Override
    protected void initView()
    {
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        // manager.setStackFromEnd(true);
        manager.setReverseLayout(true);
        chatContent.setLayoutManager(manager);

    }

    @Override
    protected void setListener()
    {
        etMessage.addTextChangedListener(new pasteEditTextWatcher());
        chatContent.addOnLayoutChangeListener(new View.OnLayoutChangeListener()
        {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                    int oldLeft, int oldTop, int oldRight, int oldBottom)
            {
                if (bottom < oldBottom)
                {
                    Observable.timer(50, TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Long>()
                            {
                                @Override
                                public void call(Long aLong)
                                {
                                    if (messageAdapter.getItemCount() > 0)
                                    {
                                        chatContent.smoothScrollToPosition(
                                                0);
                                    }
                                }
                            });

                }
            }
        });
        msgListener = new EMMessageListener()
        {
            @Override
            public void onMessageReceived(List<EMMessage> list)
            {
                Debugger.d("wftt",
                        "ChatActivity关键点收到新消息数目>>>>" + list.size() + MainActivity.isMain());
                messageAdapter.addSingleMessage(list.get(0));
                messageAdapter.notifyItemInserted(0);
                 updateMsgList();
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> list)
            {

            }

            @Override
            public void onMessageRead(List<EMMessage> list)
            {
                Debugger.d("wftt", "收到已读回执" + list.size());
            }

            @Override
            public void onMessageDelivered(List<EMMessage> list)
            {
                Debugger.d("wftt", "收到送达回执" + list.size());
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

    @OnClick({ R.id.img_back, R.id.img_right, R.id.btn_keyboard_mode, R.id.btn_press_to_speak,
            R.id.iv_emotions_normal, R.id.iv_emotions_enable, R.id.btn_more, R.id.btn_send,
            R.id.chat_face_container, R.id.view_photo, R.id.view_camera, R.id.view_location,
            R.id.view_file, R.id.view_audio, R.id.view_video, R.id.chat_tools_container, R.id.more,
            R.id.img_chat_talk, R.id.et_message, R.id.btn_mode_voice })
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.img_back:
                Utils.finish(this);
                break;
            case R.id.img_right:
                break;
            case R.id.btn_keyboard_mode:
                handleModeKeyboard();
                break;
            case R.id.btn_press_to_speak:
                break;
            case R.id.iv_emotions_normal:
                handleEmotionNormal();
                break;
            case R.id.iv_emotions_enable:
                handleEmotionEnable();
                break;
            case R.id.btn_more:
                handleBtnMore();
                break;
            case R.id.btn_send:
                sendText(etMessage.getText().toString());
                break;
            case R.id.chat_face_container:
                break;
            case R.id.view_photo:
                break;
            case R.id.view_camera:
                break;
            case R.id.view_location:
                break;
            case R.id.view_file:
                break;
            case R.id.view_audio:
                break;
            case R.id.view_video:
                break;
            case R.id.chat_tools_container:
                break;
            case R.id.more:
                break;
            case R.id.img_chat_talk:
                break;
            case R.id.et_message:
                handlePasteEdit();
                break;
            case R.id.btn_mode_voice:
                handleModeVoice();
                break;
        }
    }

    private void sendText(String content)
    {
        if (content.length() > 0)
        {
            EMMessage message = EMMessage.createTxtSendMessage(content, chat_id);
            // EMMessage message1 = EMMessage.createTxtSendMessage(content, "18829210302");
            if (chatType == ChatConstants.CHAT_GROUP)
            {
                message.setChatType(EMMessage.ChatType.GroupChat);
            }
            EMClient.getInstance().chatManager().sendMessage(message);
            // EMClient.getInstance().chatManager().sendMessage(message1);
            etMessage.setText("");
            messageAdapter.addSingleMessage(message);
            messageAdapter.notifyItemInserted(0);
             updateMsgList();
        }
    }

    private void updateMsgList()
    {
        Observable.timer(300, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>()
                {
                    @Override
                    public void call(Long aLong)
                    {
                        chatContent.smoothScrollToPosition(0);
                    }
                });
    }

    // 显示表情
    private void handleEmotionNormal()
    {
        more.setVisibility(View.VISIBLE);
        ivEmotionsNormal.setVisibility(View.GONE);
        ivEmotionsEnable.setVisibility(View.VISIBLE);
        chatToolsContainer.setVisibility(View.GONE);
        chatFaceContainer.setVisibility(View.VISIBLE);
        hideKeyBoard();
        etLayout.setVisibility(View.VISIBLE);
        btnPressToSpeak.setVisibility(View.GONE);
        etMessage.requestFocus();

    }

    // 隐藏表情
    private void handleEmotionEnable()
    {
        more.setVisibility(View.GONE);
        ivEmotionsNormal.setVisibility(View.VISIBLE);
        ivEmotionsEnable.setVisibility(View.GONE);
        chatToolsContainer.setVisibility(View.VISIBLE);
        chatFaceContainer.setVisibility(View.GONE);
    }

    // 点击加号
    private void handleBtnMore()
    {
        if (more.getVisibility() == View.VISIBLE)
        {
            if (chatFaceContainer.getVisibility() == View.VISIBLE)
            {
                chatFaceContainer.setVisibility(View.GONE);
                chatToolsContainer.setVisibility(View.VISIBLE);
                ivEmotionsNormal.setVisibility(View.VISIBLE);
                ivEmotionsEnable.setVisibility(View.GONE);
            } else
            {
                more.setVisibility(View.GONE);
            }
        } else
        {
            more.setVisibility(View.VISIBLE);
            hideKeyBoard();
            chatFaceContainer.setVisibility(View.GONE);
            chatToolsContainer.setVisibility(View.VISIBLE);
            btnPressToSpeak.setVisibility(View.GONE);
            etLayout.setVisibility(View.VISIBLE);
        }

    }

    // 点击输入框隐藏底部栏，显示输入法
    private void handlePasteEdit()
    {
        if (more.getVisibility() == View.VISIBLE)
        {
            more.setVisibility(View.GONE);
            ivEmotionsNormal.setVisibility(View.VISIBLE);
            ivEmotionsEnable.setVisibility(View.GONE);
        }
    }

    // 处理键盘按钮
    private void handleModeKeyboard()
    {
        etLayout.setVisibility(View.VISIBLE);
        more.setVisibility(View.GONE);
        btnKeyboardMode.setVisibility(View.GONE);
        btnVoiceMode.setVisibility(View.VISIBLE);
        etMessage.requestFocus();
        btnPressToSpeak.setVisibility(View.GONE);
        if (TextUtils.isEmpty(etMessage.getText().toString()))
        {
            btnMore.setVisibility(View.VISIBLE);
            btnSend.setVisibility(View.GONE);
        } else
        {
            btnMore.setVisibility(View.GONE);
            btnSend.setVisibility(View.VISIBLE);
        }

    }

    // 处理语音按钮
    private void handleModeVoice()
    {
        hideKeyBoard();
        more.setVisibility(View.GONE);
        etLayout.setVisibility(View.GONE);
        btnKeyboardMode.setVisibility(View.VISIBLE);
        btnVoiceMode.setVisibility(View.GONE);
        btnPressToSpeak.setVisibility(View.VISIBLE);
        btnSend.setVisibility(View.GONE);
        btnMore.setVisibility(View.VISIBLE);
        ivEmotionsNormal.setVisibility(View.VISIBLE);
        ivEmotionsEnable.setVisibility(View.GONE);
        chatFaceContainer.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View view, int position)
    {

    }

    @Override
    public void onLongClick(View view, int position)
    {

    }

    class pasteEditTextWatcher implements TextWatcher
    {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            if (!TextUtils.isEmpty(s))
            {
                btnMore.setVisibility(View.GONE);
                btnSend.setVisibility(View.VISIBLE);
            } else
            {
                btnSend.setVisibility(View.GONE);
                btnMore.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s)
        {

        }
    }
}
