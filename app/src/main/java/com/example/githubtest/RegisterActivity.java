package com.example.githubtest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private ImageView btn_back;
    private EditText et_mobile_number;
    private EditText et_password;
    private EditText et_second_pwd;
    private EditText et_MAC;
    private Button register_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_mobile_number = (EditText) findViewById(R.id.et_mobile_number);
        et_password = (EditText) findViewById(R.id.et_password);
        et_second_pwd = (EditText) findViewById(R.id.et_second_pwd);

        register_btn = (Button) findViewById(R.id.register_btn);
        btn_back = (ImageView) findViewById(R.id.back_btn);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

    }

    private void register(){
        final String mobileNums = et_mobile_number.getText().toString();
        final String password = et_password.getText().toString();
        final String second_pwd = et_second_pwd.getText().toString();
        if(TextUtils.isEmpty(mobileNums)){
            Toast.makeText(this,"手机号不能为空！",Toast.LENGTH_SHORT).show();
            et_mobile_number.requestFocus();
        }else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"设置密码不能为空！",Toast.LENGTH_SHORT).show();
            et_password.requestFocus();
        }else if(TextUtils.isEmpty(second_pwd)) {
            Toast.makeText(this, "请填写确认密码！", Toast.LENGTH_SHORT).show();
            et_second_pwd.requestFocus();
        }else if(!password.equals(second_pwd)){
            Toast.makeText(this, "两次密码不同，请重新填写！", Toast.LENGTH_SHORT).show();
            et_second_pwd.setText("");
            et_second_pwd.requestFocus();
        }else{

            AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.custom_dialog);
            builder.setTitle("注册确认");
            builder.setMessage("是否确认注册？");
            builder.setNegativeButton("取消", null);
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //http请求数据库
                    OkHttpClient client = new OkHttpClient();
                    FormBody body = new FormBody.Builder()
                            .add("tel",mobileNums)
                            .add("password",password)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://39.97.163.234:8443/api/userAccount/registerOne")
                            .post(body)
                            .build();

                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            e.printStackTrace();
                            Log.d("RegisterTest", "onFailure: 访问服务器失败");
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String s = response.body().string();
                            Log.d("RegisterTest", "onResponse: "+s);
                            processRegister(Integer.parseInt(s));
                        }
                    });
                    //successRegister();
                }
            });
            builder.show();
        }
    }

    private void processRegister(final int code){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(code == 0){
                    Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                }else if(code == 1) {
                    Toast.makeText(RegisterActivity.this, "该手机号已注册", Toast.LENGTH_SHORT).show();
                    et_mobile_number.setText("");
                    et_password.setText("");
                    et_second_pwd.setText("");
                    et_mobile_number.requestFocus();
                }else{
                    Toast.makeText(RegisterActivity.this,"无法识别的code:"+code,Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(code == 0){
            this.finish();
        }
    }


}
