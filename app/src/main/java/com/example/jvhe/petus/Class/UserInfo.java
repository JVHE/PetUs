package com.example.jvhe.petus.Class;

import java.io.Serializable;

public class UserInfo implements Serializable {
    String email, name, link_profile;
    int user_id, is_friend;
    boolean is_following, is_selected;

    public UserInfo() {
        email="";
        name="";
        link_profile="";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getLink_profile() {
        return link_profile;
    }

    public void setLink_profile(String link_profile) {
        this.link_profile = link_profile;
    }

    public int getIs_friend() {
        return is_friend;
    }

    public void setIs_friend(int is_friend) {
        this.is_friend = is_friend;
    }

    public boolean isIs_following() {
        return is_following;
    }

    public void setIs_following(boolean is_following) {
        this.is_following = is_following;
    }

    public boolean isIs_selected() {
        return is_selected;
    }

    public void setIs_selected(boolean is_selected) {
        this.is_selected = is_selected;
    }
}
