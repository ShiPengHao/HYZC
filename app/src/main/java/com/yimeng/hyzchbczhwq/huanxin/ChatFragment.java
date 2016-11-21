package com.yimeng.hyzchbczhwq.huanxin;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.ui.EaseChatFragment.EaseChatFragmentHelper;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.hyphenate.easeui.widget.emojicon.EaseEmojiconMenu;
import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.activity.AccountInfoActivity;
import com.yimeng.hyzchbczhwq.activity.AppointDetailActivity;
import com.yimeng.hyzchbczhwq.activity.BaseActivity;
import com.yimeng.hyzchbczhwq.bean.AppointmentBean;
import com.yimeng.hyzchbczhwq.bean.DoctorBean;
import com.yimeng.hyzchbczhwq.bean.UserBean;
import com.yimeng.hyzchbczhwq.utils.MyConstant;
import com.yimeng.hyzchbczhwq.utils.MyToast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 集成环信的聊天UI，只能做单聊
 */
public class ChatFragment extends EaseChatFragment implements EaseChatFragmentHelper {

    // constant start from 11 to avoid conflict with constant in base class
//    private static final int ITEM_VIDEO = 11;
//    private static final int ITEM_FILE = 12;
    private static final int ITEM_VOICE_CALL = 13;
    private static final int ITEM_VIDEO_CALL = 14;

    //    private static final int REQUEST_CODE_SELECT_VIDEO = 11;
//    private static final int REQUEST_CODE_SELECT_FILE = 12;
    private static final int REQUEST_CODE_GROUP_DETAIL = 13;
    private static final int REQUEST_CODE_CONTEXT_MENU = 14;
    private static final int REQUEST_CODE_SELECT_AT_USER = 15;


    private static final int MESSAGE_TYPE_SENT_VOICE_CALL = 1;
    private static final int MESSAGE_TYPE_RECV_VOICE_CALL = 2;
    private static final int MESSAGE_TYPE_SENT_VIDEO_CALL = 3;
    private static final int MESSAGE_TYPE_RECV_VIDEO_CALL = 4;

    //red packet code : 红包功能使用的常量
    private static final int MESSAGE_TYPE_RECV_RED_PACKET = 5;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET = 6;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_ACK = 7;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_ACK = 8;
    //    private static final int REQUEST_CODE_SEND_RED_PACKET = 16;
    private static final int ITEM_RED_PACKET = 16;
    //end of red packet code

    /**
     * if it is chatBot
     */
    private boolean isRobot;
    private MyReceiver myReceiver;
    private String nick;
    private DoctorBean doctorBean;
    private UserBean userBean;
    private String selfUserId;
    private HashMap<String, Object> params;
    private String selfType;

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (toChatUsername.equalsIgnoreCase(intent.getStringExtra("username"))) {//防止获得自己的信息
                nick = intent.getStringExtra("nick");
                titleBar.setTitle(nick);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void setUpView() {
        setChatFragmentListener(this);
        if (chatType == Constant.CHATTYPE_SINGLE) {
            Map<String, RobotUser> robotMap = HuanXinHelper.getInstance().getRobotList();
            if (robotMap != null && robotMap.containsKey(toChatUsername)) {
                isRobot = true;
            }
        }
        super.setUpView();
        // set click listener
        titleBar.setLeftLayoutClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//                if (EasyUtils.isSingleActivity(getActivity())) {
//                    Intent intent = new Intent(getActivity(), ChatActivity.class);
//                    startActivity(intent);
//                }
                onBackPressed();
            }
        });
        ((EaseEmojiconMenu) inputMenu.getEmojiconMenu()).addEmojiconGroup(EmojiconExampleGroupData.getData());
//        if(chatType == EaseConstant.CHATTYPE_GROUP){
//            inputMenu.getPrimaryMenu().getEditText().addTextChangedListener(new TextWatcher() {
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    if(count == 1 && "@".equals(String.valueOf(s.charAt(start)))){
//                        startActivityForResult(new Intent(getActivity(), PickAtUserActivity.class).
//                                putExtra("groupId", toChatUsername), REQUEST_CODE_SELECT_AT_USER);
//                    }
//                }
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//                @Override
//                public void afterTextChanged(Editable s) {
//
//                }
//            });
//        }
        setRefreshProfileReceiver();
        nick = PreferenceManager.getInstance().getUserNick(toChatUsername);
        if (selfUserId == null) {
            params = new HashMap<>();
            SharedPreferences sp = getActivity().getSharedPreferences(MyConstant.PREFS_ACCOUNT, Context.MODE_PRIVATE);
            selfUserId = sp.getString(MyConstant.KEY_ACCOUNT_LAST_ID, "");
            selfType = sp.getString(MyConstant.KEY_ACCOUNT_LAST_TYPE, "");
            boolean isDoctor = "doctor".equalsIgnoreCase(selfType);
            getDoctorInfo(isDoctor);
            getUserInfo(!isDoctor);
        }
    }

    /**
     * 获得用户信息，如果自己是用户，则根据自身id，否则根据聊天对象id
     *
     * @param selfIsUser 自己是用户
     */
    private void getUserInfo(boolean selfIsUser) {
        HashMap<String, Object> params = new HashMap<>();
        if (selfIsUser)
            params.put("user_id", selfUserId);
        else
            params.put("user_id", toChatUsername.substring(toChatUsername.indexOf("_") + 1));
        new BaseActivity.SoapAsyncTask() {
            @Override
            protected void onPostExecute(String s) {
                try {
                    userBean = new Gson().fromJson(new JSONObject(s).optJSONArray("data").optString(0), UserBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute("Get_User_Msg", params);
    }

    /**
     * 获得医生信息，如果自己是医生，则根据自身id，否则根据聊天对象id
     *
     * @param selfIsDoctor 自己是医生
     */
    private void getDoctorInfo(boolean selfIsDoctor) {
        HashMap<String, Object> params = new HashMap<>();
        if (selfIsDoctor)
            params.put("doctor_id", selfUserId);
        else
            params.put("doctor_id", toChatUsername.substring(toChatUsername.indexOf("_") + 1));
        new BaseActivity.SoapAsyncTask() {
            @Override
            protected void onPostExecute(String s) {
                try {
                    doctorBean = new Gson().fromJson(new JSONObject(s).optJSONArray("data").optString(0), DoctorBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute("Get_Doctor_Msg", params);
    }

    @Override
    protected void registerExtendMenuItem() {
        //use the menu in base class
        super.registerExtendMenuItem();
        //extend menu items
//        inputMenu.registerExtendMenuItem(R.string.attach_video, R.drawable.em_chat_video_selector, ITEM_VIDEO, extendMenuItemClickListener);
//        inputMenu.registerExtendMenuItem(R.string.attach_file, R.drawable.em_chat_file_selector, ITEM_FILE, extendMenuItemClickListener);
        if (chatType == Constant.CHATTYPE_SINGLE) {
            inputMenu.registerExtendMenuItem(R.string.attach_voice_call, R.drawable.em_chat_voice_call_selector, ITEM_VOICE_CALL, extendMenuItemClickListener);
            inputMenu.registerExtendMenuItem(R.string.attach_video_call, R.drawable.em_chat_video_call_selector, ITEM_VIDEO_CALL, extendMenuItemClickListener);
        }
        //聊天室暂时不支持红包功能
        //red packet code : 注册红包菜单选项
//        if (chatType != Constant.CHATTYPE_CHATROOM) {
//            inputMenu.registerExtendMenuItem(R.string.attach_red_packet, R.drawable.em_chat_red_packet_selector, ITEM_RED_PACKET, extendMenuItemClickListener);
//        }
        //end of red packet code
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
//            switch (resultCode) {
//            case ContextMenuActivity.RESULT_CODE_COPY: // copy
//                clipboard.setPrimaryClip(ClipData.newPlainText(null,
//                        ((EMTextMessageBody) contextMenuMessage.getBody()).getMessage()));
//                break;
//            case ContextMenuActivity.RESULT_CODE_DELETE: // delete
//                conversation.removeMessage(contextMenuMessage.getMsgId());
//                messageList.refresh();
//                break;
//
//            case ContextMenuActivity.RESULT_CODE_FORWARD: // forward
//                Intent intent = new Intent(getActivity(), ForwardMessageActivity.class);
//                intent.putExtra("forward_msg_id", contextMenuMessage.getMsgId());
//                startActivity(intent);
//
//                break;
//
//            default:
//                break;
//            }
//        }
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
//            case REQUEST_CODE_SELECT_VIDEO: //send the video
//                if (data != null) {
//                    int duration = data.getIntExtra("dur", 0);
//                    String videoPath = data.getStringExtra("path");
//                    File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
//                    try {
//                        FileOutputStream fos = new FileOutputStream(file);
//                        Bitmap ThumbBitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
//                        ThumbBitmap.compress(CompressFormat.JPEG, 100, fos);
//                        fos.close();
//                        sendVideoMessage(videoPath, file.getAbsolutePath(), duration);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                break;
//            case REQUEST_CODE_SELECT_FILE: //send the file
//                if (data != null) {
//                    Uri uri = data.getData();
//                    if (uri != null) {
//                        sendFileByUri(uri);
//                    }
//                }
//                break;
                case REQUEST_CODE_SELECT_AT_USER:
                    if (data != null) {
                        String username = data.getStringExtra("username");
                        inputAtUsername(username, false);
                    }
                    break;
                //red packet code : 发送红包消息到聊天界面
//            case REQUEST_CODE_SEND_RED_PACKET:
//                if (data != null){
//                    sendMessage(RedPacketUtil.createRPMessage(getActivity(), data, toChatUsername));
//                }
//                break;
                //end of red packet code
                default:
                    break;
            }
        }

    }

    /**
     * 设置头像昵称信息改变的监听
     */
    private void setRefreshProfileReceiver() {
        myReceiver = new MyReceiver();
        getActivity().registerReceiver(myReceiver, new IntentFilter(Constant.ACTION_MY_PROFILE_REFRESH));
    }


    @Override
    public void onDestroyView() {
        if (myReceiver != null)
            getActivity().unregisterReceiver(myReceiver);
        super.onDestroyView();
    }

    @Override
    public void onSetMessageAttributes(EMMessage message) {
        if (isRobot) {
            //set message extension
            message.setAttribute("em_robot_message", isRobot);
        }
    }

    @Override
    public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
        return new CustomChatRowProvider();
    }


    @Override
    public void onEnterToChatDetails() {
//        if (chatType == Constant.CHATTYPE_GROUP) {
//            EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
//            if (group == null) {
//                Toast.makeText(getActivity(), R.string.gorup_not_found, 0).show();
//                return;
//            }
//            startActivityForResult(
//                    (new Intent(getActivity(), GroupDetailsActivity.class).putExtra("groupId", toChatUsername)),
//                    REQUEST_CODE_GROUP_DETAIL);
//        }else if(chatType == Constant.CHATTYPE_CHATROOM){
//        	startActivityForResult(new Intent(getActivity(), ChatRoomDetailsActivity.class).putExtra("roomId", toChatUsername), REQUEST_CODE_GROUP_DETAIL);
//        }
    }

    @Override
    public void onAvatarClick(String username) {//TODO 聊天头像点击事件回调
        if (toChatUsername.equalsIgnoreCase(username)) {// 点击了对方
            getAppointmentList();
        } else {// 点击了自己
            startActivity(new Intent(getActivity(), AccountInfoActivity.class));
        }
        //handling when user click avatar
//        Intent intent = new Intent(getActivity(), UserProfileActivity.class);
//        intent.putExtra("username", username);
//        startActivity(intent);
    }

    /**
     * 获取对话者之间发生的预约数据
     */
    private void getAppointmentList() {
        if (selfUserId == null || doctorBean == null || userBean == null)
            return;
        params.clear();
        params.put("pagesize", 10);
        params.put("pageindex", 1);
        params.put("starttime", "");
        params.put("endtime", "");
        params.put("userid", userBean.user_id);
        params.put("doctorname", doctorBean.doctor_name);
        new BaseActivity.SoapAsyncTask() {
            @Override
            protected void onPostExecute(String s) {
                if (s == null)
                    return;
                try {
                    ArrayList<AppointmentBean> tempData = new Gson().fromJson(new JSONObject(s).optString("data")
                            , new TypeToken<ArrayList<AppointmentBean>>() {
                            }.getType());
                    if (tempData.size() == 0) {
                        MyToast.show("您没有与" + nick + "有过预约业务");
                        return;
                    }
                    Collections.sort(tempData);
                    int id = tempData.get(0).appointment_id;
                    if (selfType != null && id > 0)
                        startActivity(new Intent(getActivity(), AppointDetailActivity.class).putExtra("type", selfType).putExtra("id", id));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute("Patient_AppointmentList", params);
    }

    @Override
    public void onAvatarLongClick(String username) {
        inputAtUsername(username);
    }


    @Override
    public boolean onMessageBubbleClick(EMMessage message) {
        //消息框点击事件，demo这里不做覆盖，如需覆盖，return true
        //red packet code : 拆红包页面
//        if (message.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, false)){
//            RedPacketUtil.openRedPacket(getActivity(), chatType, message, toChatUsername, messageList);
//            return true;
//        }
        //end of red packet code
        return false;
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        //red packet code : 处理红包回执透传消息
//        for (EMMessage message : messages) {
//            EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
//            String action = cmdMsgBody.action();//获取自定义action
//            if (action.equals(RedPacketConstant.REFRESH_GROUP_RED_PACKET_ACTION)){
//                RedPacketUtil.receiveRedPacketAckMessage(message);
//                messageList.refresh();
//            }
//        }
        //end of red packet code
//        super.onCmdMessageReceived(messages);
    }

    @Override
    public void onMessageBubbleLongClick(EMMessage message) {
        // no message forward when in chat room
//        startActivityForResult((new Intent(getActivity(), ContextMenuActivity.class)).putExtra("message",message)
//                .putExtra("ischatroom", chatType == EaseConstant.CHATTYPE_CHATROOM),
//                REQUEST_CODE_CONTEXT_MENU);
    }

    @Override
    public boolean onExtendMenuItemClick(int itemId, View view) {
        switch (itemId) {
//        case ITEM_VIDEO:
//            Intent intent = new Intent(getActivity(), ImageGridActivity.class);
//            startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
//            break;
//        case ITEM_FILE: //file
//            selectFileFromLocal();
//            break;
            case ITEM_VOICE_CALL:
                startVoiceCall();
                break;
            case ITEM_VIDEO_CALL:
                startVideoCall();
                break;
            //red packet code : 进入发红包页面
//        case ITEM_RED_PACKET:
//            RedPacketUtil.startRedPacketActivityForResult(this, chatType, toChatUsername, REQUEST_CODE_SEND_RED_PACKET);
//            break;
            //end of red packet code
            default:
                break;
        }
        //keep exist extend menu
        return false;
    }

    /**
     * select file
     */
    protected void selectFileFromLocal() {
        Intent intent = null;
        if (Build.VERSION.SDK_INT < 19) { //api 19 and later, we can't use this way, demo just select from images
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
//        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
    }

    /**
     * make a voice call
     */
    protected void startVoiceCall() {
        if (!EMClient.getInstance().isConnected()) {
            Toast.makeText(getActivity(), R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(getActivity(), VoiceCallActivity.class)
                    .putExtra("username", toChatUsername)
                    .putExtra("isComingCall", false));
            // voiceCallBtn.setEnabled(false);
            inputMenu.hideExtendMenuContainer();
        }
    }

    /**
     * make a video call
     */
    protected void startVideoCall() {
        if (!EMClient.getInstance().isConnected())
            Toast.makeText(getActivity(), R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
        else {
            startActivity(new Intent(getActivity(), VideoCallActivity.class)
                    .putExtra("username", toChatUsername)
                    .putExtra("isComingCall", false));
            // videoCallBtn.setEnabled(false);
            inputMenu.hideExtendMenuContainer();
        }
    }

    /**
     * chat row provider
     */
    private final class CustomChatRowProvider implements EaseCustomChatRowProvider {
        @Override
        public int getCustomChatRowTypeCount() {
            //here the number is the message type in EMMessage::Type
            //which is used to count the number of different chat row
            return 8;
        }

        @Override
        public int getCustomChatRowType(EMMessage message) {
            if (message.getType() == EMMessage.Type.TXT) {
                //voice call
                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE_CALL : MESSAGE_TYPE_SENT_VOICE_CALL;
                } else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
                    //video call
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO_CALL : MESSAGE_TYPE_SENT_VIDEO_CALL;
                }
                //red packet code : 红包消息和红包回执消息的chat row type
//                else if (message.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, false)) {
//                    //发送红包消息
//                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_RED_PACKET : MESSAGE_TYPE_SEND_RED_PACKET;
//                } else if (message.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE, false)) {
//                    //领取红包消息
//                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_RED_PACKET_ACK : MESSAGE_TYPE_SEND_RED_PACKET_ACK;
//                }
                //end of red packet code
            }
            return 0;
        }

        @Override
        public EaseChatRow getCustomChatRow(EMMessage message, int position, BaseAdapter adapter) {
//            if (message.getType() == EMMessage.Type.TXT) {
            // voice call or video call
//                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false) ||
//                    message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)){
//                    return new ChatRowVoiceCall(getActivity(), message, position, adapter);
//                }
//                //red packet code : 红包消息和红包回执消息的chat row
//                else if (message.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, false)) {//发送红包消息
//                    return new ChatRowRedPacket(getActivity(), message, position, adapter);
//                } else if (message.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE, false)) {//open redpacket message
//                    return new ChatRowRedPacketAck(getActivity(), message, position, adapter);
//                }
            //end of red packet code
//            }
            return null;
        }

    }

}
