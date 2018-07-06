package com.sky_wf.chinachat.chat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.DateUtils;
import com.sky_wf.chinachat.R;
import com.sky_wf.chinachat.chat.listener.OnItemClickListener;
import com.sky_wf.chinachat.chat.utils.ChatConstants;
import com.sky_wf.chinachat.chat.utils.SmileUtils;
import com.sky_wf.chinachat.utils.Debugger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;

/**
 * @Date : 2018/6/15
 * @Author : WF
 * @Description :Chat Message Adapter
 */
public class MessageAdapter extends RecyclerView.Adapter
{
    private EMConversation conversation;
    private Context mContext;
    private OnItemClickListener itemClickListener;
    private final String TAG = "MessageAdapter";
    private List<EMMessage> msgList;
    private int msgCount = 0;

    public MessageAdapter(EMConversation conversation, Context mContext)
    {
        this.conversation = conversation;
        this.mContext = mContext;
        this.msgList = new ArrayList<EMMessage>();
        this.msgCount = conversation.getAllMsgCount();

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener)
    {
        itemClickListener = onItemClickListener;
    }

    public void addNewMessage(List<EMMessage> msgList)
    {
        if (msgList != null && msgList.size() > 0)
        {
            for (EMMessage emMessage : msgList)
            {
                msgList.add(emMessage);
                conversation.markMessageAsRead(emMessage.getMsgId());
            }
        }
    }

    public void addSingleMessage(EMMessage emMessage)
    {
        msgList.add(0,emMessage);
    }

    public void refresh()
    {

        this.msgList = conversation.getAllMessages();
        Collections.reverse(msgList);
        this.msgCount = conversation.getAllMsgCount();
        notifyDataSetChanged();
        Debugger.d(TAG, "refresh>>getAllMessages===内存中消息数" + msgList.size());
        Debugger.d(TAG, "refresh>>getAllMsgCount()===本地消息总数" + conversation.getAllMsgCount());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        return createItemViewByMessage(viewGroup, i);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i)
    {
        EMMessage emMessage = msgList.get(i);
        EMMessage.ChatType type = emMessage.getChatType();
        if (null != viewHolder)
        {

            // 暂时不做回执处理

            switch (emMessage.getType())
            {
                case TXT:
                    if (emMessage.direct() == EMMessage.Direct.RECEIVE)
                    {
                        handleReceiverTxtMessage(emMessage, viewHolder, i);
                    } else
                    {
                        handleSendTxtMessage(emMessage, viewHolder, i);
                    }
                    break;
                case IMAGE:
                    if (emMessage.direct() == EMMessage.Direct.RECEIVE)
                    {
                        handleReceiverPicMessage(emMessage, viewHolder, i);
                    }
                    break;
                case VIDEO:
                    break;
                case LOCATION:
                    break;
                case VOICE:
                    break;
                case FILE:
                    break;
                default:

            }

        } else
        {
            // 不符合任意一种消息类型或者没有会话消息
        }

    }

    @Override
    public int getItemCount()
    {
        Debugger.d(TAG, "getItemCount" + conversation.getAllMsgCount());
        return msgList.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        if (msgCount < msgList.size())
        {
            // 内存中的消息数大于本地数据库中消息数
        }else if(msgCount > msgList.size())
        {
            //内存中的消息数小于本地数据库中消息数
        }
        if (msgList.size() > 0)
        {
            EMMessage emMessage = msgList.get(0);
            List<EMMessage> list = conversation.loadMoreMsgFromDB(emMessage.getMsgId(), 10);
            Debugger.d(TAG, "loadFromDb>>>" + list.size() + emMessage.getMsgId());
        }
        if (msgList.size() > 0 && position < msgList.size())
        {
            EMMessage emMessage = msgList.get(position);
            Debugger.d(TAG, "getType==" + emMessage.getType());
            switch (emMessage.getType())
            {

                case TXT:
                    if (emMessage.getBooleanAttribute(ChatConstants.MESSAGE_ATTR_IS_VOICE_CALL,
                            false))
                    {
                        return emMessage.direct() == EMMessage.Direct.RECEIVE
                                ? ChatConstants.MESSAGE_TYPE_RECEIVER_VOICE_CALL
                                : ChatConstants.MESSAGE_TYPE_SEND_VOICE_CALL;
                    } else if (emMessage
                            .getBooleanAttribute(ChatConstants.MESSAGE_ATTR_IS_VIDEO_CALL, false))
                    {
                        return emMessage.direct() == EMMessage.Direct.RECEIVE
                                ? ChatConstants.MESSAGE_TYPE_RECEIVER_VIDEO_CALL
                                : ChatConstants.MESSAGE_TYPE_SEND_VIDEO_CALL;
                    }
                    return emMessage.direct() == EMMessage.Direct.RECEIVE
                            ? ChatConstants.MESSAGE_TYPE_RECEIVER_TXT
                            : ChatConstants.MESSAGE_TYPE_SEND_TXT;
                case IMAGE:
                    return emMessage.direct() == EMMessage.Direct.RECEIVE
                            ? ChatConstants.MESSAGE_TYPE_RECEIVER_PIC
                            : ChatConstants.MESSAGE_TYPE_SEND_PIC;
                case VOICE:
                    return emMessage.direct() == EMMessage.Direct.RECEIVE
                            ? ChatConstants.MESSAGE_TYPE_RECEIVER_VOICE
                            : ChatConstants.MESSAGE_TYPE_SEND_VOICE;
                case VIDEO:
                    return emMessage.direct() == EMMessage.Direct.RECEIVE
                            ? ChatConstants.MESSAGE_TYPE_RECEIVER_VIDEO
                            : ChatConstants.MESSAGE_TYPE_SEND_VIDEO;
                case LOCATION:
                    return emMessage.direct() == EMMessage.Direct.RECEIVE
                            ? ChatConstants.MESSAGE_TYPE_RECEIVER_LOC
                            : ChatConstants.MESSAGE_TYPE_SEND_LOC;
                case FILE:
                    return emMessage.direct() == EMMessage.Direct.RECEIVE
                            ? ChatConstants.MESSAGE_TYPE_RECEIVER_FILE
                            : ChatConstants.MESSAGE_TYPE_SEND_FILE;
            }
        }
        return super.getItemViewType(position);
    }

    private BaseHolder createItemViewByMessage(ViewGroup viewGroup, int type)
    {
        Debugger.d(TAG, "type==" + type);
        switch (type)
        {
            case ChatConstants.MESSAGE_TYPE_RECEIVER_TXT:
                return new ReceiverTxtHolder(itemClickListener, LayoutInflater.from(mContext)
                        .inflate(R.layout.chat_receive_txt, viewGroup, false));

            case ChatConstants.MESSAGE_TYPE_SEND_TXT:
                return new SendTxtHolder(itemClickListener, LayoutInflater.from(mContext)
                        .inflate(R.layout.chat_sent_message, viewGroup, false));

            case ChatConstants.MESSAGE_TYPE_RECEIVER_PIC:
                return new ReceiverPicHolder(itemClickListener, LayoutInflater.from(mContext)
                        .inflate(R.layout.chat_receive_pic, viewGroup, false));

            case ChatConstants.MESSAGE_TYPE_SEND_PIC:
                return new SendPicHolder(itemClickListener, LayoutInflater.from(mContext)
                        .inflate(R.layout.chat_sent_picture, viewGroup, false));

            case ChatConstants.MESSAGE_TYPE_RECEIVER_VOICE:
                return new ReceiverVoiceHolder(itemClickListener, LayoutInflater.from(mContext)
                        .inflate(R.layout.chat_receive_voice, viewGroup, false));

            case ChatConstants.MESSAGE_TYPE_SEND_VOICE:
                return new SendVoiceHolder(itemClickListener, LayoutInflater.from(mContext)
                        .inflate(R.layout.chat_sent_voice, viewGroup, false));

            case ChatConstants.MESSAGE_TYPE_RECEIVER_VIDEO:
                return new ReceiverVideoHolder(itemClickListener, LayoutInflater.from(mContext)
                        .inflate(R.layout.chat_receive_video, viewGroup, false));

            case ChatConstants.MESSAGE_TYPE_SEND_VIDEO:
                return new SendVideoHolder(itemClickListener, LayoutInflater.from(mContext)
                        .inflate(R.layout.chat_sent_video, viewGroup, false));

            case ChatConstants.MESSAGE_TYPE_RECEIVER_LOC:
                return new ReceiverLocHolder(itemClickListener, LayoutInflater.from(mContext)
                        .inflate(R.layout.chat_receive_location, viewGroup, false));

            case ChatConstants.MESSAGE_TYPE_SEND_LOC:
                return new SendLocHolder(itemClickListener, LayoutInflater.from(mContext)
                        .inflate(R.layout.chat_sent_location, viewGroup, false));

            case ChatConstants.MESSAGE_TYPE_RECEIVER_FILE:
                return new ReceiverFileHolder(itemClickListener, LayoutInflater.from(mContext)
                        .inflate(R.layout.chat_receive_file, viewGroup, false));

            case ChatConstants.MESSAGE_TYPE_SEND_FILE:
                return new SendFileHolder(itemClickListener, LayoutInflater.from(mContext)
                        .inflate(R.layout.chat_sent_file, viewGroup, false));

            case ChatConstants.MESSAGE_TYPE_RECEIVER_VOICE_CALL:
                return new ReceiverVoiceCallHolder(itemClickListener, LayoutInflater.from(mContext)
                        .inflate(R.layout.chat_received_voice_call, viewGroup, false));

            case ChatConstants.MESSAGE_TYPE_SEND_VOICE_CALL:
                return new SendVoiceCallHolder(itemClickListener, LayoutInflater.from(mContext)
                        .inflate(R.layout.chat_sent_voice_call, viewGroup, false));

            case ChatConstants.MESSAGE_TYPE_RECEIVER_VIDEO_CALL:
                return new ReceiverVideoCallHolder(itemClickListener, LayoutInflater.from(mContext)
                        .inflate(R.layout.chat_received_video_call, viewGroup, false));

            case ChatConstants.MESSAGE_TYPE_SEND_VIDEO_CALL:
                return new SendVideoCallHolder(itemClickListener, LayoutInflater.from(mContext)
                        .inflate(R.layout.chat_sent_video_call, viewGroup, false));
            default:
                return new ReceiverTxtHolder(itemClickListener, LayoutInflater.from(mContext)
                        .inflate(R.layout.chat_receive_txt, viewGroup, false));

        }
    }

    // 处理接收的文字消息
    private void handleReceiverTxtMessage(EMMessage emMessage, RecyclerView.ViewHolder viewHolder,
            int position)
    {
        ReceiverTxtHolder holder = (ReceiverTxtHolder) viewHolder;

        if (position == 0)
        {
            holder.tvTimeStamp
                    .setText(DateUtils.getTimestampString(new Date(emMessage.getMsgTime())));
            holder.tvTimeStamp.setVisibility(View.VISIBLE);
        } else
        {
            if (DateUtils.isCloseEnough(emMessage.getMsgTime(),
                    msgList.get(position - 1).getMsgTime()))
            {
                holder.tvTimeStamp.setVisibility(View.GONE);
            } else
            {
                holder.tvTimeStamp
                        .setText(DateUtils.getTimestampString(new Date(emMessage.getMsgTime())));
                holder.tvTimeStamp.setVisibility(View.VISIBLE);
            }
        }
        EMTextMessageBody txtbody = (EMTextMessageBody) emMessage.getBody();
        Spannable spannable = SmileUtils.getSmiledText(mContext, txtbody.getMessage());
        holder.tvChatContent.setText(spannable, TextView.BufferType.SPANNABLE);
        holder.tvChatContent.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // 处理长按消息
            }
        });
    }

    // 处理接收的文字消息
    private void handleReceiverPicMessage(EMMessage emMessage, RecyclerView.ViewHolder viewHolder,
            int position)
    {
        ReceiverPicHolder holder = (ReceiverPicHolder) viewHolder;

        if (position == 0)
        {
            holder.tvTimeStamp
                    .setText(DateUtils.getTimestampString(new Date(emMessage.getMsgTime())));
            holder.tvTimeStamp.setVisibility(View.VISIBLE);
        } else
        {
            if (DateUtils.isCloseEnough(emMessage.getMsgTime(),
                    msgList.get(position - 1).getMsgTime()))
            {
                holder.tvTimeStamp.setVisibility(View.GONE);
            } else
            {
                holder.tvTimeStamp
                        .setText(DateUtils.getTimestampString(new Date(emMessage.getMsgTime())));
                holder.tvTimeStamp.setVisibility(View.VISIBLE);
            }
        }
        holder.ivPic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // 处理长按消息
            }
        });

        if (emMessage.status() == EMMessage.Status.INPROGRESS)
        {
            holder.ivPic.setImageResource(R.drawable.default_image);
            showDownloadImageProgress(emMessage, viewHolder);
        } else
        {

        }
    }

    private void showDownloadImageProgress(EMMessage emMessage, RecyclerView.ViewHolder viewHolder)
    {
        ReceiverPicHolder holder = (ReceiverPicHolder) viewHolder;
        EMImageMessageBody imgBody = (EMImageMessageBody) emMessage.getBody();
        holder.progressBar.setVisibility(View.VISIBLE);
        holder.tvPicPercent.setVisibility(View.VISIBLE);
        emMessage.setMessageStatusCallback(new EMCallBack()
        {
            @Override
            public void onSuccess()
            {

            }

            @Override
            public void onError(int i, String s)
            {

            }

            @Override
            public void onProgress(int i, String s)
            {

            }
        });

    }

    // 处理发送的文字消息
    private void handleSendTxtMessage(EMMessage emMessage, RecyclerView.ViewHolder viewHolder,
            int position)
    {
        SendTxtHolder holder = (SendTxtHolder) viewHolder;

        if (position == 0)
        {
            holder.tvTimeStamp
                    .setText(DateUtils.getTimestampString(new Date(emMessage.getMsgTime())));
            holder.tvTimeStamp.setVisibility(View.VISIBLE);
        } else
        {
            if (DateUtils.isCloseEnough(emMessage.getMsgTime(),
                    msgList.get(position - 1).getMsgTime()))
            {
                holder.tvTimeStamp.setVisibility(View.GONE);
            } else
            {
                holder.tvTimeStamp
                        .setText(DateUtils.getTimestampString(new Date(emMessage.getMsgTime())));
                holder.tvTimeStamp.setVisibility(View.VISIBLE);
            }
        }
        EMTextMessageBody txtbody = (EMTextMessageBody) emMessage.getBody();
        Spannable spannable = SmileUtils.getSmiledText(mContext, txtbody.getMessage());
        holder.tvChatContent.setText(spannable, TextView.BufferType.SPANNABLE);
        holder.tvChatContent.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // 处理长按消息
            }
        });

        switch (emMessage.status())
        {
            case SUCCESS:
                holder.progressBar.setVisibility(View.GONE);
                holder.ivMsgStatus.setVisibility(View.GONE);
                break;
            case FAIL:
                holder.progressBar.setVisibility(View.GONE);
                holder.ivMsgStatus.setVisibility(View.VISIBLE);
                break;
            case INPROGRESS:
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.ivMsgStatus.setVisibility(View.GONE);
                break;
            default:
                sendMsgInBackground(emMessage, holder);
        }

        if (emMessage.isAcked())
        {
            holder.tvDelivered.setVisibility(View.INVISIBLE);
            holder.tvAck.setVisibility(View.VISIBLE);

        } else
        {
            holder.tvAck.setVisibility(View.INVISIBLE);
            holder.tvDelivered.setVisibility(View.VISIBLE);
        }
    }

    private void sendMsgInBackground(final EMMessage emMessage, RecyclerView.ViewHolder viewHolder)
    {
        SendTxtHolder sendTxtHolder = (SendTxtHolder) viewHolder;
        sendTxtHolder.ivMsgStatus.setVisibility(View.GONE);
        sendTxtHolder.progressBar.setVisibility(View.VISIBLE);
        Observable.create(new Observable.OnSubscribe<Object>()
        {
            @Override
            public void call(Subscriber<? super Object> subscriber)
            {
                EMClient.getInstance().chatManager().sendMessage(emMessage);

            }
        }).subscribe(new Subscriber<Object>()
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

            }
        });
    }

    public static class ReceiverFileHolder extends BaseHolder
    {
        @BindView(R.id.time_stamp)
        TextView tvTimeStamp;
        @BindView(R.id.iv_user_head)
        ImageView ivUserHead;
        @BindView(R.id.tv_chat_usename)
        TextView tvChatUserName;
        @BindView(R.id.linear_file_container)
        LinearLayout linear_file_container;
        @BindView(R.id.tv_rec_filename)
        TextView tvFileName;
        @BindView(R.id.tv_rec_filesize)
        TextView tvFileSize;
        @BindView(R.id.tv_rec_file_state)
        TextView tvFileState;
        @BindView(R.id.pb_sending)
        ProgressBar progressBar;

        public ReceiverFileHolder(OnItemClickListener listener, View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemClickListener = listener;
        }

        @Override
        public void onClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onClick(v, pos);
        }

        @Override
        public boolean onLongClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onLongClick(v, pos);
            return false;
        }
    }

    /**
     * @Date : 2018/6/15
     * @Author : WF
     * @Description :接收Location消息Holder
     */
    public static class ReceiverLocHolder extends BaseHolder
    {
        @BindView(R.id.time_stamp)
        TextView tvTimeStamp;
        @BindView(R.id.iv_user_head)
        ImageView ivUserHead;
        @BindView(R.id.tv_chat_usename)
        TextView tvChatUserName;
        @BindView(R.id.liner_chat_location)
        LinearLayout linearLocation;
        @BindView(R.id.tv_location)
        TextView tvLocation;
        @BindView(R.id.iv_chat_location)
        ImageView ivLocation;

        public ReceiverLocHolder(OnItemClickListener listener, View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemClickListener = listener;
        }

        @Override
        public void onClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onClick(v, pos);
        }

        @Override
        public boolean onLongClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onLongClick(v, pos);
            return false;
        }
    }

    /**
     * @Date : 2018/6/15
     * @Author : WF
     * @Description :接收Pic消息Holder
     */
    public static class ReceiverPicHolder extends BaseHolder
    {
        @BindView(R.id.time_stamp)
        TextView tvTimeStamp;
        @BindView(R.id.iv_user_head)
        ImageView ivUserHead;
        @BindView(R.id.tv_chat_usename)
        TextView tvChatUserName;
        @BindView(R.id.rl_rcv_layout)
        RelativeLayout rlRcvLayout;
        @BindView(R.id.iv_rcv_pic)
        ImageView ivPic;
        @BindView(R.id.loading_layout)
        LinearLayout linearLoading;
        @BindView(R.id.progress_pic)
        ProgressBar progressBar;
        @BindView(R.id.tv_chat_percent)
        TextView tvPicPercent;

        public ReceiverPicHolder(OnItemClickListener listener, View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemClickListener = listener;
        }

        @Override
        public void onClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onClick(v, pos);
        }

        @Override
        public boolean onLongClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onLongClick(v, pos);
            return false;
        }
    }

    /**
     * @Date : 2018/6/15
     * @Author : WF
     * @Description :接收文字消息Holder
     */
    public static class ReceiverTxtHolder extends BaseHolder
    {
        @BindView(R.id.time_stamp)
        TextView tvTimeStamp;
        @BindView(R.id.iv_user_head)
        ImageView ivUserHead;
        @BindView(R.id.tv_chat_usename)
        TextView tvChatUserName;
        @BindView(R.id.tv_chat_content)
        TextView tvChatContent;

        public ReceiverTxtHolder(OnItemClickListener listener, View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemClickListener = listener;
        }

        @Override
        public void onClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onClick(v, pos);
        }

        @Override
        public boolean onLongClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onLongClick(v, pos);
            return false;
        }
    }

    /**
     * @Date : 2018/6/15
     * @Author : WF
     * @Description :接收VideoCall消息Holder
     */
    public static class ReceiverVideoCallHolder extends BaseHolder
    {
        @BindView(R.id.time_stamp)
        TextView tvTimeStamp;
        @BindView(R.id.iv_user_head)
        ImageView ivUserHead;
        @BindView(R.id.tv_chat_usename)
        TextView tvChatUserName;
        @BindView(R.id.rl_rcv_layout)
        RelativeLayout rlLayout;
        @BindView(R.id.iv_chat_rcv_video)
        ImageView ivChatVideo;
        @BindView(R.id.tv_chat_content)
        TextView tvChatContent;

        public ReceiverVideoCallHolder(OnItemClickListener listener, View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemClickListener = listener;
        }

        @Override
        public void onClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onClick(v, pos);
        }

        @Override
        public boolean onLongClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onLongClick(v, pos);
            return false;
        }
    }

    /**
     * @Date : 2018/6/15
     * @Author : WF
     * @Description :接收Video消息Holder
     */
    public static class ReceiverVideoHolder extends BaseHolder
    {
        @BindView(R.id.time_stamp)
        TextView tvTimeStamp;
        @BindView(R.id.iv_user_head)
        ImageView ivUserHead;
        @BindView(R.id.tv_chat_usename)
        TextView tvChatUserName;
        @BindView(R.id.linear_chat_video)
        LinearLayout linearVideo;
        @BindView(R.id.frame_chat_video)
        FrameLayout frameCall;
        @BindView(R.id.iv_chat_video)
        ImageView ivChatVideo;
        @BindView(R.id.linear_video_data)
        LinearLayout linearVideoData;
        @BindView(R.id.tv_chat_video_length)
        TextView tvVideoLength;
        @BindView(R.id.tv_chat_video_size)
        TextView tvVideoSize;

        public ReceiverVideoHolder(OnItemClickListener listener, View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemClickListener = listener;
        }

        @Override
        public void onClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onClick(v, pos);
        }

        @Override
        public boolean onLongClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onLongClick(v, pos);
            return false;
        }
    }

    /**
     * @Date : 2018/6/15
     * @Author : WF
     * @Description :接收语音消息Holder
     */
    public static class ReceiverVoiceHolder extends BaseHolder
    {
        @BindView(R.id.time_stamp)
        TextView tvTimeStamp;
        @BindView(R.id.iv_user_head)
        ImageView ivUserHead;
        @BindView(R.id.tv_chat_usename)
        TextView tvChatUserName;
        @BindView(R.id.iv_voice)
        ImageView ivVoice;
        @BindView(R.id.tv_voice_length)
        TextView tvVoiceLength;
        @BindView(R.id.iv_unread_voice)
        ImageView ivUnreadVoice;
        @BindView(R.id.progress_pic)
        ProgressBar progressBar;

        public ReceiverVoiceHolder(OnItemClickListener listener, View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemClickListener = listener;
        }

        @Override
        public void onClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onClick(v, pos);
        }

        @Override
        public boolean onLongClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onLongClick(v, pos);
            return false;
        }
    }

    /**
     * @Date : 2018/6/15
     * @Author : WF
     * @Description :接收VoiceCall消息Holder
     */
    public static class ReceiverVoiceCallHolder extends BaseHolder
    {
        @BindView(R.id.time_stamp)
        TextView tvTimeStamp;
        @BindView(R.id.iv_user_head)
        ImageView ivUserHead;
        @BindView(R.id.tv_chat_usename)
        TextView tvChatUserName;
        @BindView(R.id.rl_rcv_layout)
        RelativeLayout rlLayout;
        @BindView(R.id.iv_chat_voice_call)
        ImageView ivVoiceCall;
        @BindView(R.id.tv_chat_content)
        TextView tvChatContent;

        public ReceiverVoiceCallHolder(OnItemClickListener listener, View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemClickListener = listener;
        }

        @Override
        public void onClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onClick(v, pos);
        }

        @Override
        public boolean onLongClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onLongClick(v, pos);
            return false;
        }
    }

    /**
     * @Date : 2018/6/15
     * @Author : WF
     * @Description :发送File消息Holder
     */
    public static class SendFileHolder extends BaseHolder
    {
        @BindView(R.id.time_stamp)
        TextView tvTimeStamp;
        @BindView(R.id.iv_user_head)
        ImageView ivUserHead;
        @BindView(R.id.linear_file_container)
        LinearLayout liearContainer;
        @BindView(R.id.tv_file_name)
        TextView tvFileName;
        @BindView(R.id.tv_file_size)
        TextView tvFileSize;
        @BindView(R.id.tv_file_state)
        TextView tvFileStatus;
        @BindView(R.id.msg_status)
        ImageView tvMsgStatus;
        @BindView(R.id.tv_ack)
        TextView tvAck;
        @BindView(R.id.tv_delivered)
        TextView tvDelivered;
        @BindView(R.id.ll_loading)
        LinearLayout lienarLoading;
        @BindView(R.id.pb_sending)
        ProgressBar progressBar;
        @BindView(R.id.percentage)
        TextView tvPercentage;

        public SendFileHolder(OnItemClickListener listener, View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemClickListener = listener;
        }

        @Override
        public void onClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onClick(v, pos);
        }

        @Override
        public boolean onLongClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onLongClick(v, pos);
            return false;
        }
    }

    /**
     * @Date : 2018/6/15
     * @Author : WF
     * @Description :发送Location消息Holder
     */
    public static class SendLocHolder extends BaseHolder
    {
        @BindView(R.id.time_stamp)
        TextView tvTimeStamp;
        @BindView(R.id.iv_user_head)
        ImageView ivUserHead;
        @BindView(R.id.ll_location)
        LinearLayout linearLocation;
        @BindView(R.id.pb_sending)
        ProgressBar progressBar;
        @BindView(R.id.tv_location)
        TextView tvLocation;
        @BindView(R.id.msg_status)
        ImageView tvMsgStatus;
        @BindView(R.id.tv_ack)
        TextView tvAck;
        @BindView(R.id.tv_delivered)
        TextView tvDelivered;

        public SendLocHolder(OnItemClickListener listener, View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemClickListener = listener;
        }

        @Override
        public void onClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onClick(v, pos);
        }

        @Override
        public boolean onLongClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onLongClick(v, pos);
            return false;
        }
    }

    /**
     * @Date : 2018/6/15
     * @Author : WF
     * @Description :发送Pic消息Holder
     */
    public static class SendPicHolder extends BaseHolder
    {
        @BindView(R.id.time_stamp)
        TextView tvTimeStamp;
        @BindView(R.id.iv_user_head)
        ImageView ivUserHead;
        @BindView(R.id.rl_picture)
        RelativeLayout rlLayout;
        @BindView(R.id.iv_sendPicture)
        ImageView ivSendPic;
        @BindView(R.id.ll_loading)
        LinearLayout liearLoading;
        @BindView(R.id.progressBar)
        ProgressBar progressBar;
        @BindView(R.id.percentage)
        TextView tvPercentage;
        @BindView(R.id.msg_status)
        ImageView tvMsgStatus;
        @BindView(R.id.tv_ack)
        TextView tvAck;
        @BindView(R.id.tv_delivered)
        TextView tvDelivered;

        public SendPicHolder(OnItemClickListener listener, View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemClickListener = listener;
        }

        @Override
        public void onClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onClick(v, pos);
        }

        @Override
        public boolean onLongClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onLongClick(v, pos);
            return false;
        }
    }

    /**
     * @Date : 2018/6/15
     * @Author : WF
     * @Description :发送文字消息Holder
     */
    public static class SendTxtHolder extends BaseHolder
    {
        @BindView(R.id.time_stamp)
        TextView tvTimeStamp;
        @BindView(R.id.iv_user_head)
        ImageView ivUserHead;
        @BindView(R.id.tv_chat_content)
        TextView tvChatContent;
        @BindView(R.id.msg_status)
        ImageView ivMsgStatus;
        @BindView(R.id.tv_ack)
        TextView tvAck;
        @BindView(R.id.tv_delivered)
        TextView tvDelivered;
        @BindView(R.id.pb_sending)
        ProgressBar progressBar;

        public SendTxtHolder(OnItemClickListener listener, View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemClickListener = listener;
        }

        @Override
        public void onClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onClick(v, pos);
        }

        @Override
        public boolean onLongClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onLongClick(v, pos);
            return false;
        }
    }

    /**
     * @Date : 2018/6/15
     * @Author : WF
     * @Description :发送videoCall消息Holder
     */
    public static class SendVideoCallHolder extends BaseHolder
    {
        @BindView(R.id.time_stamp)
        TextView tvTimeStamp;
        @BindView(R.id.iv_user_head)
        ImageView ivUserHead;
        @BindView(R.id.rl_picture)
        RelativeLayout linearVoiceContainer;
        @BindView(R.id.iv_call_icon)
        ImageView ivCallIcon;
        @BindView(R.id.tv_chatcontent)
        TextView tvChatContent;
        @BindView(R.id.msg_status)
        ImageView tvMsgStatus;
        @BindView(R.id.tv_ack)
        TextView tvAck;

        public SendVideoCallHolder(OnItemClickListener listener, View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemClickListener = listener;
        }

        @Override
        public void onClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onClick(v, pos);
        }

        @Override
        public boolean onLongClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onLongClick(v, pos);
            return false;
        }
    }

    /**
     * @Date : 2018/6/15
     * @Author : WF
     * @Description :发送Video消息Holder
     */
    public static class SendVideoHolder extends BaseHolder
    {
        @BindView(R.id.time_stamp)
        TextView tvTimeStamp;
        @BindView(R.id.iv_user_head)
        ImageView ivUserHead;
        @BindView(R.id.ll_click_area)
        LinearLayout linearClick;
        @BindView(R.id.chatting_click_area)
        FrameLayout frameClick;
        @BindView(R.id.chatting_content_iv)
        ImageView imgContent;
        @BindView(R.id.chatting_video_data_area)
        LinearLayout linearVideoData;
        @BindView(R.id.chatting_size_iv)
        TextView tvVideoSize;
        @BindView(R.id.chatting_length_iv)
        TextView tvVideoLength;
        @BindView(R.id.container_status_btn)
        LinearLayout linear_status;
        @BindView(R.id.chatting_status_btn)
        ImageView ivChattingStatus;
        @BindView(R.id.ll_loading)
        LinearLayout liearLoading;
        @BindView(R.id.progressBar)
        ProgressBar progressBar;
        @BindView(R.id.percentage)
        TextView tvPercentage;
        @BindView(R.id.msg_status)
        ImageView ivMsgStatus;
        @BindView(R.id.tv_ack)
        TextView tvAck;
        @BindView(R.id.tv_delivered)
        TextView tvDelivered;

        public SendVideoHolder(OnItemClickListener listener, View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemClickListener = listener;
        }

        @Override
        public void onClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onClick(v, pos);
        }

        @Override
        public boolean onLongClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onLongClick(v, pos);
            return false;
        }
    }

    /**
     * @Date : 2018/6/15
     * @Author : WF
     * @Description :发送VoiceCall消息Holder
     */
    public static class SendVoiceCallHolder extends BaseHolder
    {
        @BindView(R.id.time_stamp)
        TextView tvTimeStamp;
        @BindView(R.id.iv_user_head)
        ImageView ivUserHead;
        @BindView(R.id.rl_picture)
        RelativeLayout linearVoiceContainer;
        @BindView(R.id.iv_call_icon)
        ImageView ivCallIcon;
        @BindView(R.id.tv_chatcontent)
        TextView tvChatContent;
        @BindView(R.id.msg_status)
        ImageView tvMsgStatus;
        @BindView(R.id.tv_ack)
        TextView tvAck;

        public SendVoiceCallHolder(OnItemClickListener listener, View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemClickListener = listener;
        }

        @Override
        public void onClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onClick(v, pos);
        }

        @Override
        public boolean onLongClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onLongClick(v, pos);
            return false;
        }
    }

    /**
     * @Date : 2018/6/15
     * @Author : WF
     * @Description :发送Voice消息Holder
     */
    public static class SendVoiceHolder extends BaseHolder
    {
        @BindView(R.id.time_stamp)
        TextView tvTimeStamp;
        @BindView(R.id.iv_user_head)
        ImageView ivUserHead;
        @BindView(R.id.iv_voice)
        ImageView iv_voice;
        @BindView(R.id.tv_length)
        TextView tvVoiceLength;
        @BindView(R.id.msg_status)
        ImageView ivMsgStatus;
        @BindView(R.id.tv_ack)
        TextView tvAck;
        @BindView(R.id.tv_delivered)
        TextView tvDelivered;
        @BindView(R.id.pb_sending)
        ProgressBar progressBar;

        public SendVoiceHolder(OnItemClickListener listener, View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemClickListener = listener;
        }

        @Override
        public void onClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onClick(v, pos);
        }

        @Override
        public boolean onLongClick(View v)
        {
            int pos = this.getLayoutPosition();
            itemClickListener.onLongClick(v, pos);
            return false;
        }
    }

}
