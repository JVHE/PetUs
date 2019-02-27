package com.example.jvhe.petus.Class;

import com.bumptech.glide.request.RequestOptions;
import com.example.jvhe.petus.R;

public class StaticData {
//    public static final String url = "http://13.125.252.108/";
//    public static final String url = "http://13.124.250.83/";
    public static final String url = "http://13.209.26.41/";
    public static double my_lat = 37;
    public static double my_lng = 125;
    public static final String vidyo_token = "";

    public static final String chat_url = "192.168.137.1";

    public static final RequestOptions requestOptions = new RequestOptions()
            .placeholder(R.drawable.image_loading)
            .error(R.drawable.ic_image_error);
    public static final RequestOptions requestOptions_rounded = new RequestOptions()
            .circleCrop()
            .placeholder(R.drawable.image_loading)
            .error(R.drawable.ic_image_error)
//                        .dontAnimate()
            .skipMemoryCache(true);


}
