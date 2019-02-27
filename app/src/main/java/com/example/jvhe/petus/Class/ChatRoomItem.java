package com.example.jvhe.petus.Class;

public class ChatRoomItem {

    int group_or_user_id;
    String name_or_title, link_profile, msg, msg_datetime;
    int cnt_unread;
    int is_private_or_group;
    public UserInfo userInfo = null;

    public int getIs_private_or_group() {
        return is_private_or_group;
    }

    public void setIs_private_or_group(int is_private_or_group) {
        this.is_private_or_group = is_private_or_group;
    }

    public int getGroup_or_user_id() {
        return group_or_user_id;
    }

    public void setGroup_or_user_id(int group_or_user_id) {
        this.group_or_user_id = group_or_user_id;
    }

    public String getName_or_title() {
        return name_or_title;
    }

    public void setName_or_title(String name_or_title) {
        this.name_or_title = name_or_title;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
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
