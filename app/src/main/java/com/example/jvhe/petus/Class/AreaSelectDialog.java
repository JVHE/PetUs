package com.example.jvhe.petus.Class;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.example.jvhe.petus.R;

public class AreaSelectDialog extends Dialog implements View.OnClickListener {

    String value = "";

    public AreaSelectDialog(@NonNull Context context) {
        super(context);
        setTitle("지역 선택");
        setContentView(R.layout.dialog_area_select);


        findViewById(R.id.btn_0).setOnClickListener(this);
        findViewById(R.id.btn_1).setOnClickListener(this);
        findViewById(R.id.btn_2).setOnClickListener(this);
        findViewById(R.id.btn_3).setOnClickListener(this);
        findViewById(R.id.btn_4).setOnClickListener(this);
        findViewById(R.id.btn_5).setOnClickListener(this);
        findViewById(R.id.btn_6).setOnClickListener(this);
        findViewById(R.id.btn_7).setOnClickListener(this);
        findViewById(R.id.btn_8).setOnClickListener(this);
        findViewById(R.id.btn_9).setOnClickListener(this);
        findViewById(R.id.btn_10).setOnClickListener(this);
        findViewById(R.id.btn_11).setOnClickListener(this);
        findViewById(R.id.btn_12).setOnClickListener(this);
        findViewById(R.id.btn_13).setOnClickListener(this);
        findViewById(R.id.btn_14).setOnClickListener(this);
        findViewById(R.id.btn_15).setOnClickListener(this);
        findViewById(R.id.btn_16).setOnClickListener(this);
        findViewById(R.id.btn_17).setOnClickListener(this);
        findViewById(R.id.btn_18).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.btn_0:
                value = "내 주변";
                break;
            case R.id.btn_1:
                value = "전체";
                break;
            case R.id.btn_2:
                value = "서울";
                break;
            case R.id.btn_3:
                value = "경기";
                break;
            case R.id.btn_4:
                value = "인천";
                break;
            case R.id.btn_5:
                value = "부산";
                break;
            case R.id.btn_6:
                value = "대구";
                break;
            case R.id.btn_7:
                value = "광주";
                break;
            case R.id.btn_8:
                value = "대전";
                break;
            case R.id.btn_9:
                value = "울산";
                break;
            case R.id.btn_10:
                value = "강원";
                break;
            case R.id.btn_11:
                value = "충남";
                break;
            case R.id.btn_12:
                value = "충북";
                break;
            case R.id.btn_13:
                value = "경남";
                break;
            case R.id.btn_14:
                value = "경북";
                break;
            case R.id.btn_15:
                value = "전남";
                break;
            case R.id.btn_16:
                value = "전북";
                break;
            case R.id.btn_17:
                value = "세종";
                break;
            case R.id.btn_18:
                value = "제주";
                break;

        }
        Toast.makeText(getContext(), "선택 아이템: " + value, Toast.LENGTH_SHORT).show();
        dismiss();
    }

    public String getValue() {
        return value;
    }
}
