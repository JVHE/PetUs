package com.example.jvhe.petus.Class;

import java.io.Serializable;

public class StreamRoomItem implements Serializable{
    UserInfo streamer_info = null;
    String title = "";
    String desc = "";
    String room_name = "";
    String reaction = "";

    public StreamRoomItem() {
        streamer_info = new UserInfo();
    }

    public StreamRoomItem(UserInfo streamer_info, String title, String desc, String room_name) {
        this.streamer_info = streamer_info;
        this.title = title;
        this.desc = desc;
        this.room_name = room_name;
    }

    public StreamRoomItem(UserInfo streamer_info, String title, String desc, String room_name, String reaction) {
        this.streamer_info = streamer_info;
        this.title = title;
        this.desc = desc;
        this.room_name = room_name;
        this.reaction = reaction;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    public UserInfo getStreamer_info() {
        return streamer_info;
    }

    public void setStreamer_info(UserInfo streamer_info) {
        this.streamer_info = streamer_info;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }
}
