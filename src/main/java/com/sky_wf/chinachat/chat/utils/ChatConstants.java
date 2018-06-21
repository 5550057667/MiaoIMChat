package com.sky_wf.chinachat.chat.utils;

/**
 * @Date : 2018/6/11
 * @Author : WF
 * @Description :
 */
public class ChatConstants
{

    public static final int CHAT_SINGLE = 1;// 判断单聊
    public static final int CHAT_GROUP = 2;// 判断群聊

    //接收消息类型
    public static final int MESSAGE_TYPE_RECEIVER_TXT = 3;
    public static final int MESSAGE_TYPE_RECEIVER_PIC = 4;
    public static final int MESSAGE_TYPE_RECEIVER_VOICE = 5;
    public static final int MESSAGE_TYPE_RECEIVER_VIDEO = 6;
    public static final int MESSAGE_TYPE_RECEIVER_LOC = 7;
    public static final int MESSAGE_TYPE_RECEIVER_FILE = 8;
    public static final int MESSAGE_TYPE_RECEIVER_VOICE_CALL = 9;
    public static final int MESSAGE_TYPE_RECEIVER_VIDEO_CALL = 10;

    //发送消息类型
    public static final int MESSAGE_TYPE_SEND_TXT = 11;
    public static final int MESSAGE_TYPE_SEND_PIC = 12;
    public static final int MESSAGE_TYPE_SEND_VOICE = 13;
    public static final int MESSAGE_TYPE_SEND_VIDEO = 14;
    public static final int MESSAGE_TYPE_SEND_LOC = 15;
    public static final int MESSAGE_TYPE_SEND_FILE = 16;
    public static final int MESSAGE_TYPE_SEND_VOICE_CALL = 17;
    public static final int MESSAGE_TYPE_SEND_VIDEO_CALL = 18;

    public static final int REQUEST_CODE_COPY_AND_PASTE = 19;// 聊天输入框复制粘贴

    public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
    public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";

}
