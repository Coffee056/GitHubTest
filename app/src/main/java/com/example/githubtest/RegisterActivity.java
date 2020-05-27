package com.example.githubtest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private ImageView btn_back;
    private EditText et_mobile_number;
    private EditText et_password;
    private EditText et_second_pwd;
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
        if(TextUtils.isEmpty(et_mobile_number.getText())){
            Toast.makeText(this,"手机号不能为空！",Toast.LENGTH_SHORT).show();
            et_mobile_number.requestFocus();
        }else if(TextUtils.isEmpty(et_password.getText())){
            Toast.makeText(this,"设置密码不能为空！",Toast.LENGTH_SHORT).show();
            et_password.requestFocus();
        }else if(TextUtils.isEmpty(et_second_pwd.getText())) {
            Toast.makeText(this, "请填写确认密码！", Toast.LENGTH_SHORT).show();
            et_second_pwd.requestFocus();
        }else if(!et_password.getText().toString().equals(et_second_pwd.getText().toString())){
            Toast.makeText(this, "两次密码不同，请重新填写！", Toast.LENGTH_SHORT).show();
        }else{
            //Toast.makeText(getActivity(), "上报成功，请等待审核", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.custom_dialog);
            builder.setTitle("注册确认");
            builder.setMessage("是否确认注册？");
            builder.setNegativeButton("取消", null);
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    successRegister();
                }
            });
            builder.show();
        }
    }

    private void successRegister(){
        // TO-Do 传给服务器注册信息

        Toast.makeText(this, "注册成功!", Toast.LENGTH_SHORT).show();
        this.finish();
    }

}
