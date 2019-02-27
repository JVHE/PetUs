package com.example.jvhe.petus.Class;

public class ReviewItem {

    String title = "";
    String desc = "";
    String url = "";

    public ReviewItem(String title, String desc) {
        this.title = title;
        this.desc = desc;
    }

    public ReviewItem(String title, String desc, String url) {
        this.title = title;
        this.desc = desc;
        this.url = url;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
