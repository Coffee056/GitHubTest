package com.example.githubtest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SetPhoneNumberActivity extends AppCompatActivity {

    private ImageView btn_back;
    private TextView tv_set_phone_number;

    private String mobileNumber = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_phone_number);

        mobileNumber = getIntent().getStringExtra("mobileNumber");

        btn_back = (ImageView) findViewById(R.id.back_btn);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_set_phone_number = (TextView) findViewById(R.id.tv_set_phone_number);
        if(mobileNumber.length() == 11) {
            tv_set_phone_number.setText(mobileNumber.substring(0, 3) + "****" + mobileNumber.substring(7));
        }else{
            tv_set_phone_number.setText(mobileNumber);
        }
        //tv_set_phone_number.setText(mobileNumber.substring(0,3)+"****"+mobileNumber.substring(7));
    }
}
