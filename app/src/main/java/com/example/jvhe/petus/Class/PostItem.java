package com.example.jvhe.petus.Class;

import android.graphics.Bitmap;

import java.io.Serializable;

public class PostItem implements Serializable {

    int post_id = -1, post_comment_count = -1, post_like = -1, post_dislike = -1;
    String title = "", content = "", post_datetime = "", path = "", name = "", link_profile = "";
//    Bitmap profile, thumbnail;

    public PostItem() {
    }

    public PostItem(int post_id, String title, String content, String post_datetime, int post_like, int post_dislike, String path, String name, String link_profile) {
        this.post_id = post_id;
        this.title = title;
        this.content = content;
        this.post_datetime = post_datetime;
        this.post_like = post_like;
        this.post_dislike = post_dislike;
        this.path = path;
        this.name = name;
        this.link_profile = link_profile;
    }

    public PostItem(int post_id, String title, String content, String post_datetime, int post_comment_count, int post_like, int post_dislike, String path, String name, String link_profile) {
        this.post_id = post_id;
        this.title = title;
        this.content = content;
        this.post_datetime = post_datetime;
        this.post_comment_count = post_comment_count;
        this.post_like = post_like;
        this.post_dislike = post_dislike;
        this.path = path;
        this.name = name;
        this.link_profile = link_profile;
    }

    public int getPost_comment_count() {
        return post_comment_count;
    }

    public void setPost_comment_count(int post_comment_count) {
        this.post_comment_count = post_comment_count;
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPost_datetime() {
        return post_datetime;
    }

    public void setPost_datetime(String post_datetime) {
        this.post_datetime = post_datetime;
    }

    public int getPost_like() {
        return post_like;
    }

    public void setPost_like(int post_like) {
        this.post_like = post_like;
    }

    public int getPost_dislike() {
        return post_dislike;
    }

    public void setPost_dislike(int post_dislike) {
        this.post_dislike = post_dislike;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
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

//    public Bitmap getProfile() {
//        return profile;
//    }
//
//    public void setProfile(Bitmap profile) {
//        this.profile = profile;
//    }
//
//    public Bitmap getThumbnail() {
//        return thumbnail;
//    }
//
//    public void setThumbnail(Bitmap thumbnail) {
//        this.thumbnail = thumbnail;
//    }
}
