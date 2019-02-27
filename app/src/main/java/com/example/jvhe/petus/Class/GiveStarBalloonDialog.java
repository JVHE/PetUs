package com.example.jvhe.petus.Class;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jvhe.petus.Activity.StarBalloonPaymentActivity;
import com.example.jvhe.petus.R;

public class GiveStarBalloonDialog extends Dialog implements View.OnClickListener {

    String streamer_name;
    TextView tv_streamer_name, tv_having_star_balloons;
    EditText et_count, et_chat;
    int number_star_balloons;
    GiveStarBalloonDialogListener giveStarBalloonDialogListener;

    public void setNumber_star_balloons(int number_star_balloons) {
        this.number_star_balloons = number_star_balloons;
    }

    public void setDialogListener(GiveStarBalloonDialogListener dialogListener) {
        this.giveStarBalloonDialogListener = dialogListener;
    }

    public GiveStarBalloonDialog(@NonNull Context context, String streamer_name, int number_star_balloons) {
        super(context);
        setTitle("별풍선 선물하기");
        setContentView(R.layout.dialog_give_star_balloon);
        this.streamer_name = streamer_name;
        this.number_star_balloons = number_star_balloons;
        tv_streamer_name = findViewById(R.id.tv_streamer_name);
        tv_streamer_name.setText(streamer_name);
        tv_having_star_balloons = findViewById(R.id.tv_having_star_balloons);
        String string = "보유 별풍선: " + number_star_balloons + "개";
        tv_having_star_balloons.setText(string);
        findViewById(R.id.btn_charge_star_balloon).setOnClickListener(this);
        findViewById(R.id.btn_present_star_balloons).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        et_count = findViewById(R.id.et_count);
        et_chat = findViewById(R.id.et_chat);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_charge_star_balloon:
                // 별풍선 액티비티로 이동
                Intent intent = new Intent(getContext(), StarBalloonPaymentActivity.class);
                getContext().startActivity(intent);
                // 돌아오면 대화창 다시 열려있게 만들까? 어떻게??
                dismiss();
                break;
            case R.id.btn_present_star_balloons:
                // 적은 별풍선 보내기
                // 메세지랑 별풍선을 받아와서 처리한다.
                // 예외처리는 1~자기가 가진 갯수
                // 메세지 처리는 어떡하지? dismiss 리스너를 만들까?

                // 예외처리 필요!!
                // 숫자 입력칸에 숫자만.
                if (et_count.toString().equals("") || Integer.parseInt(et_count.getText().toString())<1 || Integer.parseInt(et_count.getText().toString())>number_star_balloons) {
                    Toast.makeText(getContext(), "선물할 별풍선의 갯수는 1이상 "+number_star_balloons+"이하여야 합니다.",Toast.LENGTH_SHORT).show();
                    return;
                }
                giveStarBalloonDialogListener.onPositiveClicked(et_chat.getText().toString(), Integer.parseInt(et_count.getText().toString()));
                dismiss();
                break;
            case R.id.btn_cancel:
                cancel();
                break;
        }
    }

    public interface GiveStarBalloonDialogListener {

        public void onPositiveClicked(String msg, int number_star_balloons);

        public void onNegativeClicked();
    }

}
