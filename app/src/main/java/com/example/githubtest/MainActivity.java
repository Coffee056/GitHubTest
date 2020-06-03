package com.example.githubtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

    //用户尝试登录
    private void userLogin(final String mobileNums,final String password){
        if(TextUtils.isEmpty(mobileNums)){
            Toast.makeText(this,"手机号不能为空",Toast.LENGTH_SHORT).show();
            et_account.requestFocus();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"密码不能为空",Toast.LENGTH_SHORT).show();
            et_password.requestFocus();
        }
//        else if(! isMobileNO(mobileNums)){
//            Toast.makeText(this,"手机号输入不正确",Toast.LENGTH_SHORT).show();
//            et_account.requestFocus();
//        }
        else{
            //http请求数据库
            OkHttpClient client = new OkHttpClient();
            FormBody body = new FormBody.Builder()
                    .add("tel",mobileNums)
                    .add("password",password)
                    .build();
            Request request = new Request.Builder()
                    .url("http://39.97.163.234:8443/api/userAccount/loginOne")
                    .post(body)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                    Log.d("LoginTest", "onFailure: 访问服务器失败");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String s = response.body().string();
                    Log.d("LoginTest", "onResponse: "+s);
                    processLogin(Integer.parseInt(s),mobileNums);
                }
            });
        }
    }

    //判断手机号格式是否正确
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

    //处理从服务器获取的登录返回信息
    private void processLogin(final int code, String mobileNums){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(code == 0){
                    //Log.d("LoginTest", "onResponse: "+"登录成功");
                    Toast.makeText(MainActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                }else if(code == 1){
                    //Log.d("LoginTest", "onResponse: "+"未注册");
                    Toast.makeText(MainActivity.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
                    et_password.setText("");
                }else if(code == 2){
                    //Log.d("LoginTest", "onResponse: "+"已在线");
                    Toast.makeText(MainActivity.this,"已在其它设备登录",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,"无法识别的code:"+code,Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(code == 0){
            //Toast.makeText(MainActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
            intent.putExtra("mobileNumber",mobileNums);
            startActivity(intent);
            finish();
        }
    }


}
