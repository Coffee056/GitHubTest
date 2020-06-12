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
 * Use the {@link DiagnosisFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiagnosisFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText et_diagnosis_name;
    private EditText et_diagnosis_IDnumber;
    private EditText et_diagnosis_tel;
    private EditText et_diagnosis_region;
    private EditText et_diagnosis_hospital;
    private EditText et_diagnosis_date;
    private EditText et_diagnosis_case;
    //private Button upload_pic_btn;
    private Button report_btn;

    private int mYear;
    private int mMonth;
    private int mDay;
    //private boolean isUploadPic = false;

    public DiagnosisFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DiagnosisFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DiagnosisFragment newInstance(String param1, String param2) {
        DiagnosisFragment fragment = new DiagnosisFragment();
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
                .url("http://39.97.163.234:8443/api/patientInfo/findOne")
                .post(body)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Log.d("PatientInfoFindOneTest", "onFailure: 访问服务器失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String s = response.body().string();
                Log.d("PatientInfoFindOneTest", "onResponse: "+s);
                parseJSONWithJSONObject(s);
            }
        });
    }

    //处理json格式数据
    private void parseJSONWithJSONObject(String jsonData){
        try{
            JSONObject jsonObject = new JSONObject(jsonData);
            if(!jsonObject.has("error")) {
                final String diagnosis_time = jsonObject.getString("diagnosis_time");
                final String diagnosis_location = jsonObject.getString("diagnosis_location");
                final String diagnosis_hospital = jsonObject.getString("diagnosis_hospital");
                final String diagnosis_case = jsonObject.getString("diagnosis_case");
                final String audit_status = jsonObject.getString("audit_status");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        et_diagnosis_date.setText(diagnosis_time);
                        et_diagnosis_date.setFocusable(false);
                        if(et_diagnosis_date.hasOnClickListeners()) {
                            et_diagnosis_date.setOnClickListener(null);
                        }
                        et_diagnosis_region.setText(diagnosis_location);
                        et_diagnosis_region.setFocusable(false);
                        et_diagnosis_hospital.setText(diagnosis_hospital);
                        et_diagnosis_hospital.setFocusable(false);
                        et_diagnosis_case.setText(diagnosis_case);
                        et_diagnosis_case.setFocusable(false);
                        report_btn.setClickable(false);
                        report_btn.setText(audit_status);
                    }
                });
            }
//            if(IDnumber.equals("null")){
//                Log.d("FindOneTest","IDnumber is null");
//                IDnumber = null;
//
//            }
//            if(name.equals("null")){
//                Log.d("FindOneTest","name is null");
//                name = null;
//            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diagnosis, container, false);
        et_diagnosis_name = (EditText) view.findViewById(R.id.et_diagnosis_name);
        et_diagnosis_IDnumber = (EditText) view.findViewById(R.id.et_diagnosis_IDnumber);
        et_diagnosis_tel = (EditText) view.findViewById(R.id.et_diagnosis_tel);
        et_diagnosis_region = (EditText) view.findViewById(R.id.et_diagnosis_region);
        et_diagnosis_hospital = (EditText) view.findViewById(R.id.et_diagnosis_hospital);
        et_diagnosis_date = (EditText) view.findViewById(R.id.et_diagnosis_date);
        et_diagnosis_case = (EditText) view.findViewById(R.id.et_diagnosis_case);

        report_btn = (Button) view.findViewById(R.id.report_btn);


        et_diagnosis_date.setOnClickListener(new View.OnClickListener() {
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
        et_diagnosis_name.setText(name);
        et_diagnosis_IDnumber.setText(IDnumber);
        et_diagnosis_tel.setText(tel);

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
                        et_diagnosis_date.setText(yyyy+"-"+mm+"-"+dd);
                        //Toast.makeText(getActivity(),mYear+"   "+(mMonth+1)+"   "+mDay,Toast.LENGTH_SHORT).show();
                    }
                },
                mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    //上报信息
    private void report(){
        if(TextUtils.isEmpty(et_diagnosis_region.getText())){
            Toast.makeText(getActivity(),"请填写确诊所在省市",Toast.LENGTH_SHORT).show();
            et_diagnosis_region.requestFocus();
        }else if(TextUtils.isEmpty(et_diagnosis_hospital.getText())){
            Toast.makeText(getActivity(),"请填写确诊医院",Toast.LENGTH_SHORT).show();
            et_diagnosis_hospital.requestFocus();
        }else if(TextUtils.isEmpty(et_diagnosis_date.getText())) {
            Toast.makeText(getActivity(), "请选择确诊时间", Toast.LENGTH_SHORT).show();
            //et_diagnosis_date.requestFocus();
        }else if(TextUtils.isEmpty(et_diagnosis_case.getText())){
            Toast.makeText(getActivity(), "请填写病史", Toast.LENGTH_SHORT).show();
            et_diagnosis_case.requestFocus();
        }else{
            //Toast.makeText(getActivity(), "上报成功，请等待审核", Toast.LENGTH_SHORT).show();
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
                .add("diagnosis_time",et_diagnosis_date.getText().toString())
                .add("diagnosis_location",et_diagnosis_region.getText().toString())
                .add("diagnosis_hospital",et_diagnosis_hospital.getText().toString())
                .add("diagnosis_case",et_diagnosis_case.getText().toString())
                .build();
        Request request = new Request.Builder()
                .url("http://39.97.163.234:8443/api/patientInfo/insertOne")
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
                                Toast.makeText(getActivity(),"确诊上报成功!",Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.d("insertOneTest", "onSuccess: 确诊上报成功!");
                        getActivity().finish();
                    }else if(Integer.parseInt(s) == 1){
                        Log.d("insertOneTest", "onFailure: 确诊上报失败，已经上报过");
                    }
            }
        });

    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if(requestCode == 1){
//            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                choosePhoto();
//            }else{
//                Toast.makeText(getActivity(),"You denied the permission",Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    //选择图片功能
//    private  void choosePhoto(){
//        // 添加图片的主要代码
//        Intent intent = new Intent();
//        // 设定类型为image
//        intent.setType("image/*");
//        // 设置action
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        //Log.d("AddActivityTest", "MenuClickEvent: choose pics button");
//        // 选中相片后返回本Activity
//        startActivityForResult(intent, 10);
//    }

//    //数据回调方法
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == getActivity().RESULT_OK) {
//            Toast.makeText(getActivity(),"success",Toast.LENGTH_SHORT).show();
//            // 如果是选择照片
//            if (requestCode == 10) {
//                String path = handleImageOnKitKat(data);
//                isUploadPic = true;
//                Bitmap bitmap = BitmapFactory.decodeFile(path);
//                upload_pic_btn.setBackground(new BitmapDrawable(getContext().getResources(),bitmap));
//                Toast.makeText(getActivity(),path,Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

//    //取相册照片功能用的函数-获取文件路径   content开头URI --> 文件绝对路径
//    @TargetApi(Build.VERSION_CODES.KITKAT)
//    private String handleImageOnKitKat(Intent data){
//        String imagePath = null;
//        Uri uri = data.getData();
//        if(DocumentsContract.isDocumentUri(getActivity(),uri)){
//            // 如果是document类型的Uri，则通过document id处理
//            String docId = DocumentsContract.getDocumentId(uri);
//            if("com.android.providers.media.documents".equals(uri.getAuthority())){
//                String id = docId.split(":")[1];
//                String selection = MediaStore.Images.Media._ID + "=" + id;
//                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
//
//            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
//                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
//                imagePath = getImagePath(contentUri,null);
//            }
//        } else if("content".equalsIgnoreCase(uri.getScheme())){
//            //如果是content类型的Uri，则使用普通方式处理
//            imagePath = getImagePath(uri,null);
//
//        } else if("file".equalsIgnoreCase(uri.getScheme())){
//            //如果是file类型的Uri，直接获取图片路径即可
//            imagePath = uri.getPath();
//        }
//        //Log.d("AddActivityTest", "handleImageOnKitKat  imagePath="+imagePath);
//        return  imagePath;
//    }
//    public String getImagePath(Uri uri, String selection){
//        String path = null;
//        Cursor cursor = getActivity().getContentResolver().query(uri,null,selection,null,null);
//        if(cursor != null){
//            if(cursor.moveToFirst()){
//                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//            }
//            cursor.close();
//        }
//        return path;
//    }


}
