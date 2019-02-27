package com.example.jvhe.petus.Activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.jvhe.petus.KaKaoPay.KakaoActivity;
import com.example.jvhe.petus.R;

public class StarBalloonPaymentActivity extends AppCompatActivity {

    Button btn_pay;
    EditText et_count;
    TextView tv_number_star_balloons, tv_price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star_balloon_payment);

        btn_pay = findViewById(R.id.btn_pay);
        et_count = findViewById(R.id.et_count);
        // 텍스트는 나중에
        et_count.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tv_number_star_balloons = findViewById(R.id.tv_number_star_balloons);
        tv_price = findViewById(R.id.tv_price);

        // 액션바 감추기
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), KakaoActivity.class);
                // 예외처리 필요
                intent.putExtra("num", Integer.parseInt(et_count.getText().toString()) * 10);
                startActivity(intent);
                finish();
            }
        });
    }
}
