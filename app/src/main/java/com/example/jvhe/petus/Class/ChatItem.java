package com.example.jvhe.petus.Class;

public class ChatItem {
    int user_id;
    String name, link_profile, msg, msg_datetime;
    int cnt_unread, chat_type;
    public UserInfo userInfo = null;

    public int getIs_private_or_group() {
        return is_private_or_group;
    }

    public void setIs_private_or_group(int is_private_or_group) {
        this.is_private_or_group = is_private_or_group;
    }

    int is_private_or_group;


    public ChatItem() {
    }

    public ChatItem(int user_id, String name, String link_profile, int chat_type, String msg, String msg_datetime, int cnt_unread) {
        this.user_id = user_id;
        this.name = name;
        this.link_profile = link_profile;
        this.msg = msg;
        this.msg_datetime = msg_datetime;
        this.cnt_unread = cnt_unread;
        this.chat_type = chat_type;
    }

//    public ChatItem(int user_id, String name, String link_profile, String msg, String msg_datetime, int cnt_unread) {
//        this.user_id = user_id;
//        this.name = name;
//        this.link_profile = link_profile;
//        this.msg = msg;
//        this.msg_datetime = msg_datetime;
//        this.cnt_unread = cnt_unread;
//    }

    public int getChat_type() {
        return chat_type;
    }

    public void setChat_type(int chat_type) {
        this.chat_type = chat_type;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink_profile() {
        return link_profile;
    }

    public void setLink_profile(String link_profile) {
        this.link_profile = link_profile;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg_datetime() {
        return msg_datetime;
    }

    public void setMsg_datetime(String msg_datetime) {
        this.msg_datetime = msg_datetime;
    }

    public int getCnt_unread() {
        return cnt_unread;
    }

    public void setCnt_unread(int cnt_unread) {
        this.cnt_unread = cnt_unread;
    }
}
