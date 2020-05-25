package com.example.githubtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button login_btn;
    private EditText et_account;
    private EditText et_password;
    private TextView tv_register;
    private TextView tv_forgetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_account = (EditText) findViewById(R.id.account);
        et_password = (EditText) findViewById(R.id.password);
        tv_register = (TextView) findViewById(R.id.register);
        tv_forgetPassword = (TextView) findViewById(R.id.forgetPassword);

        login_btn = (Button) findViewById(R.id.login);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin(et_account.getText().toString(),et_password.getText().toString());
            }
        });
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        tv_forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,GetBackPwdActivity.class);
                startActivity(intent);
            }
        });
    }

    private void userLogin(String mobileNums,String password){
        if(TextUtils.isEmpty(mobileNums)){
            Toast.makeText(this,"手机号不能为空",Toast.LENGTH_SHORT).show();
            et_account.requestFocus();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"密码不能为空",Toast.LENGTH_SHORT).show();
            et_password.requestFocus();
        }
        else if(! isMobileNO(mobileNums)){
            Toast.makeText(this,"手机号输入不正确",Toast.LENGTH_SHORT).show();
            et_account.requestFocus();
        }
        else{
            Toast.makeText(this,"登录成功！",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
            intent.putExtra("mobileNumber",mobileNums);
            startActivity(intent);
            finish();
        }
    }

    private boolean isMobileNO(String mobileNums) {
        /**
         * 判断字符串是否符合手机号码格式
         * 移动号段: 134,135,136,137,138,139,147,150,151,152,157,158,159,170,178,182,183,184,187,188
         * 联通号段: 130,131,132,145,155,156,170,171,175,176,185,186
         * 电信号段: 133,149,153,170,173,177,180,181,189
         * @param str
         * @return 待检测的字符串
         */
        String telRegex = "^((13[0-9])|(14[5,7,9])|(15[^4])|(18[0-9])|(17[0,1,3,5,6,7,8]))\\d{8}$";// "[1]"代表下一位为数字可以是几，"[0-9]"代表可以为0-9中的一个，"[5,7,9]"表示可以是5,7,9中的任意一位,[^4]表示除4以外的任何一个,\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobileNums))
            return false;
        else
            return mobileNums.matches(telRegex);
    }
}
