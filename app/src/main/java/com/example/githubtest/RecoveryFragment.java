package com.example.githubtest;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecoveryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecoveryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText et_recovery_name;
    private EditText et_recovery_IDnumber;
    private EditText et_recovery_tel;
    private EditText et_recovery_region;
    private EditText et_recovery_hospital;
    private EditText et_recovery_date;
    private EditText et_recovery_case;

    private Button report_btn;

    private int mYear;
    private int mMonth;
    private int mDay;
    //private boolean isUploadPic = false;

    public RecoveryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecoveryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecoveryFragment newInstance(String param1, String param2) {
        RecoveryFragment fragment = new RecoveryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);

        loadFromServer();
    }

    //从服务器获取是否上报过
    private void loadFromServer(){
        SharedPreferences preferences = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String tel = preferences.getString("tel",null);

        //http请求数据库
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("tel",tel)
                .build();
        Request request = new Request.Builder()
                .url("http://39.97.163.234:8443/api/recoveryInfo/findOne")
                .post(body)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Log.d("RecoveryInfoFindOneTest", "onFailure: 访问服务器失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String s = response.body().string();
                Log.d("RecoveryInfoFindOneTest", "onResponse: "+s);
                parseJSONWithJSONObject(s);
            }
        });
    }

    //处理json格式数据
    private void parseJSONWithJSONObject(String jsonData){
        try{
            JSONObject jsonObject = new JSONObject(jsonData);
            if(!jsonObject.has("error")) {
                final String recovery_time = jsonObject.getString("recovery_time");
                final String recovery_location = jsonObject.getString("recovery_location");
                final String recovery_hospital = jsonObject.getString("recovery_hospital");
                final String recovery_case = jsonObject.getString("recovery_case");
                final String audit_status = jsonObject.getString("audit_status");

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        et_recovery_date.setText(recovery_time);
                        et_recovery_date.setEnabled(false);
                        et_recovery_date.setClickable(false);
                        if(et_recovery_date.hasOnClickListeners()) {
                            et_recovery_date.setOnClickListener(null);
                        }
                        et_recovery_region.setText(recovery_location);
                        et_recovery_region.setEnabled(false);
                        et_recovery_hospital.setText(recovery_hospital);
                        et_recovery_hospital.setEnabled(false);
                        et_recovery_case.setText(recovery_case);
                        et_recovery_case.setEnabled(false);
                        report_btn.setClickable(false);
                        report_btn.setText("审核状态:     "+audit_status);
                    }
                });
            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recovery, container, false);
        et_recovery_name = (EditText) view.findViewById(R.id.et_recovery_name);
        et_recovery_IDnumber = (EditText) view.findViewById(R.id.et_recovery_IDnumber);
        et_recovery_tel = (EditText) view.findViewById(R.id.et_recovery_tel);
        et_recovery_region = (EditText) view.findViewById(R.id.et_recovery_region);
        et_recovery_hospital = (EditText) view.findViewById(R.id.et_recovery_hospital);
        et_recovery_date = (EditText) view.findViewById(R.id.et_recovery_date);
        et_recovery_case = (EditText) view.findViewById(R.id.et_recovery_case);

        report_btn = (Button) view.findViewById(R.id.report_btn);

        et_recovery_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        report_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                report();
            }
        });

        SharedPreferences preferences = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String tel = preferences.getString("tel",null);
        String name = preferences.getString("name",null);
        String IDnumber = preferences.getString("IDnumber",null);
        et_recovery_name.setText(name);
        et_recovery_IDnumber.setText(IDnumber);
        et_recovery_tel.setText(tel);

        return view;
    }

    //显示日期选择器
    private void showDatePicker(){

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mYear = year;
                        mMonth = month;
                        mDay = dayOfMonth;
                        int realMonth = mMonth+1;
                        String yyyy = ""+mYear;
                        String mm;
                        String dd;
                        if(realMonth < 10){
                            mm = "0"+realMonth;
                        }else{
                            mm = ""+realMonth;
                        }
                        if(mDay < 10){
                            dd = "0"+mDay;
                        }else{
                            dd = ""+mDay;
                        }
                        et_recovery_date.setText(yyyy+"-"+mm+"-"+dd);
                        //Toast.makeText(getActivity(),mYear+"   "+(mMonth+1)+"   "+mDay,Toast.LENGTH_SHORT).show();
                    }
                },
                mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    //上报信息
    private void report(){
        if(TextUtils.isEmpty(et_recovery_region.getText())){
            Toast.makeText(getActivity(),"请填写康复所在省市",Toast.LENGTH_SHORT).show();
            et_recovery_region.requestFocus();
        }else if(TextUtils.isEmpty(et_recovery_hospital.getText())){
            Toast.makeText(getActivity(),"请填写确诊医院",Toast.LENGTH_SHORT).show();
            et_recovery_hospital.requestFocus();
        }else if(TextUtils.isEmpty(et_recovery_date.getText())) {
            Toast.makeText(getActivity(), "请选择确诊时间", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(et_recovery_case.getText())){
            Toast.makeText(getActivity(), "请填写病史", Toast.LENGTH_SHORT).show();
            et_recovery_case.requestFocus();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.custom_dialog);
            builder.setTitle("上报确认");
            builder.setMessage("是否确认上报？");
            builder.setNegativeButton("取消", null);
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    successReport();
                }
            });
            builder.show();
        }
    }

    //上报成功逻辑
    private void successReport(){
        // TO-Do 传给服务器上报信息(包括蓝牙连接信息?)
        SharedPreferences preferences = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String tel = preferences.getString("tel",null);
        //http请求数据库
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("tel",tel)
                .add("recovery_time",et_recovery_date.getText().toString())
                .add("recovery_location",et_recovery_region.getText().toString())
                .add("recovery_hospital",et_recovery_hospital.getText().toString())
                .add("recovery_case",et_recovery_case.getText().toString())
                .build();
        Request request = new Request.Builder()
                .url("http://39.97.163.234:8443/api/recoveryInfo/insertOne")
                .post(body)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Log.d("insertOneTest", "onFailure: 访问服务器失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String s = response.body().string();
                Log.d("insertOneTest", "onResponse: "+s);
                if(Integer.parseInt(s) == 0){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),"康复上报成功!",Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.d("insertOneTest", "onSuccess: 康复上报成功!");
                    getActivity().finish();
                }else if(Integer.parseInt(s) == 1){
                    Log.d("insertOneTest", "onFailure: 康复上报失败，已经上报过");
                }
            }
        });

    }




}
