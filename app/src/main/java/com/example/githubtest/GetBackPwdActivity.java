package com.example.githubtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class GetBackPwdActivity extends AppCompatActivity {

    private ImageView btn_back;
    private Button getBackPwd_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_back_pwd);

        btn_back = (ImageView) findViewById(R.id.back_btn);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getBackPwd_btn = (Button) findViewById(R.id.getBackPwd_btn);
        getBackPwd_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GetBackPwdActivity.this,ResetPwdActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
