package com.example.jvhe.petus.Class;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;

public class CafeItem implements ClusterItem, Serializable {
    String name = "";
    String place_id = "";
//    Bitmap bmImage = null;
    String image_url = "";
    String desc = "";
    String location = "서울특별시 동작구";
    double latitude = 0, longitude = 0;

    String title = "";
    String snippet = "";

    double distance = 0;

    public CafeItem() {
    }

    // 테스트용
    public CafeItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.title = name;
    }

//    public Bitmap getBmImage() {
//        return bmImage;
//    }
//
//    public void setBmImage(Bitmap bmImage) {
//        this.bmImage = bmImage;
//    }


    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
        this.snippet = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(latitude, longitude);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    //    @Override
    public String getTitle() {
        return name;
    }

//    @Override
    public String getSnippet() {
        return location;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
