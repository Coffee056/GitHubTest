package com.example.githubtest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AuthenticationActivity extends AppCompatActivity {

    private ImageView btn_back;
    private EditText et_name;
    private EditText et_IDnumber;
    private Button authentication_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        btn_back = (ImageView) findViewById(R.id.back_btn);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        boolean isAuthentication = getIntent().getBooleanExtra("isAuthentication",false);
        et_name = (EditText) findViewById(R.id.et_name);
        et_IDnumber = (EditText) findViewById(R.id.et_IDnumber);
        authentication_btn = (Button) findViewById(R.id.authentication_btn);

        if(!isAuthentication) {
            authentication_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    authenticate();
                }
            });
        }else{
            String name = getIntent().getStringExtra("name");
            String IDnumber = getIntent().getStringExtra("IDnumber");
            et_name.setText(name);
            et_name.setEnabled(false);
            et_IDnumber.setText(IDnumber);
            et_IDnumber.setEnabled(false);
            authentication_btn.setText("已认证");
            authentication_btn.setClickable(false);
        }
    }

    private void authenticate(){
        if(TextUtils.isEmpty(et_name.getText())){
            Toast.makeText(this,"请填写姓名",Toast.LENGTH_SHORT).show();
            et_name.requestFocus();
        }else if(TextUtils.isEmpty(et_IDnumber.getText())){
            Toast.makeText(this,"请填写身份证号",Toast.LENGTH_SHORT).show();
            et_IDnumber.requestFocus();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.custom_dialog);
            builder.setTitle("认证确认");
            builder.setMessage("是否确认认证？");
            builder.setNegativeButton("取消", null);
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    successAuthenticate();
                }
            });
            builder.show();
        }
    }

    private void successAuthenticate(){
        SharedPreferences preferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String mobileNumber = preferences.getString("tel",null);
        String health = preferences.getString("health",null);
        double risk = preferences.getFloat("risk",0.0f);


        //http请求数据库
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("tel",mobileNumber)
                .add("name",et_name.getText().toString())
                .add("idnumber",et_IDnumber.getText().toString())
                .add("health",health)
                .add("risk",String.valueOf(risk))
                .build();
        Request request = new Request.Builder()
                .url("http://39.97.163.234:8443/api/userAccount/updateOne")
                .post(body)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Log.d("updateOneTest", "onFailure: 访问服务器失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String s = response.body().string();
                Log.d("updateOneTest", "onResponse: "+s);
                //成功上传身份认证
                if(Integer.parseInt(s) == 0){
                    SharedPreferences.Editor editor = getSharedPreferences("UserInfo",MODE_PRIVATE).edit();
                    editor.putString("name",et_name.getText().toString());
                    editor.putString("IDnumber",et_IDnumber.getText().toString());
                    editor.apply();
                    Intent intent = new Intent();
                    intent.putExtra("name",et_name.getText().toString());
                    intent.putExtra("IDnumber",et_IDnumber.getText().toString());

                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        });


    }


}
