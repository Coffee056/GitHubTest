package com.example.githubtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.githubtest.SQL.BTConnection;
import com.example.githubtest.SQL.BroadcastKey;
import com.example.githubtest.adapter.MyAdapter;
import com.example.githubtest.model.Report;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UploadRecordActivity extends AppCompatActivity {

    private ImageView btn_back;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private String mobileNumber = null;

    private List<Report> reports = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_record);

        btn_back = (ImageView) findViewById(R.id.back_btn);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        SharedPreferences preferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        mobileNumber = preferences.getString("tel",null);
        //http请求数据库
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("tel",mobileNumber)
                .build();
        Request request = new Request.Builder()
                .url("http://39.97.163.234:8443/api/patientInfo/findOne")
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
                parseJSONWithJSONObject(s);
                Log.d("LoginTest", "onResponse: "+s);
            }
        });

        OkHttpClient client2 = new OkHttpClient();
        FormBody body2 = new FormBody.Builder()
                .add("tel",mobileNumber)
                .build();
        Request request2 = new Request.Builder()
                .url("http://39.97.163.234:8443/api/recoveryInfo/findOne")
                .post(body2)
                .build();

        Call call2 = client2.newCall(request2);
        call2.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Log.d("LoginTest", "onFailure: 访问服务器失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String s = response.body().string();
                parseJSONWithJSONObject2(s);
                Log.d("LoginTest", "onResponse recoveryInfo: "+s);
            }
        });

//        Report r = new Report();
//        r.setName("张三");
//        r.setIDnumber("123");
//        r.setTel("12345678");
//        r.setRegion("BeiJing");
//        r.setHospital("RenMingHospital");
//        r.setDate("2020-06-03");
//        r.setType("确诊上报");
//        r.setStatus("已审核");
//        Report r1 = new Report();
//        r1.setDate("2020-06-04");
//        r1.setType("康复上报");
//        r1.setStatus("待审核");
//        reports.add(r);
//        reports.add(r1);
        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(reports);
        recyclerView.setAdapter(mAdapter);

    }

    //处理json格式数据，并增改SharedPreferences
    private void parseJSONWithJSONObject(String jsonData){
        try{
            JSONObject jsonObject = new JSONObject(jsonData);

            String time = jsonObject.getString("diagnosis_time");
            String status = jsonObject.getString("audit_status");
            Report r = new Report();
            r.setDate(time);
            r.setType("确诊上报");
            r.setStatus(status);
            reports.add(r);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void parseJSONWithJSONObject2(String jsonData){
        try{
            JSONObject jsonObject = new JSONObject(jsonData);

            String time = jsonObject.getString("recovery_time");
            String status = jsonObject.getString("audit_status");
            Report r = new Report();
            r.setDate(time);
            r.setType("康复上报");
            r.setStatus(status);
            reports.add(r);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
