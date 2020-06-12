package com.example.githubtest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.githubtest.SQL.BTConnection;
import com.example.githubtest.SQL.DBAdapter;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageButton btn_upload;
    private TextView tv_upload_record;
    private LinearLayout upload_Bluetooth;

    DBAdapter dbAdapter;

    public UploadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadFragment newInstance(String param1, String param2) {
        UploadFragment fragment = new UploadFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload, container, false);

        dbAdapter = new DBAdapter(getActivity());
        dbAdapter.open();//启动数据库

        tv_upload_record = (TextView) view.findViewById(R.id.tv_upload_record);
        tv_upload_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UploadRecordActivity.class);
                startActivity(intent);
            }
        });

        upload_Bluetooth = (LinearLayout) view.findViewById(R.id.bluetooth_upload);
        upload_Bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getBaseContext(),"开始上传蓝牙连接", Toast.LENGTH_SHORT).show();
                Upload_Bluetooth();
            }
        });;

        btn_upload = (ImageButton) view.findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                String name = preferences.getString("name",null);
                if(name != null) {
                    Intent intent = new Intent(getActivity(),ReportActivity.class);
                    startActivity(intent);
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.custom_dialog);
                    builder.setTitle("上报提示");
                    builder.setMessage("请先在'我的'页面中完成身份认证！");
                    builder.setPositiveButton("确认", null);
                    builder.show();
                }
            }
        });
        return view;
    }

    public String getLocalMacAddress() {
        WifiManager wifi = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    public void Upload_Bluetooth() {
        SharedPreferences preferences = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        int userid = preferences.getInt("userid", 0);
        SharedPreferences preferences2 = getActivity().getSharedPreferences("MAC", Context.MODE_PRIVATE);
        String my_mac = getLocalMacAddress();
        my_mac = preferences2.getString("MAC", "02:00:00:00:00:00");
        BTConnection[] bt = dbAdapter.queryUnsentBTConnection();

        OkHttpClient client = new OkHttpClient();

        if(bt!=null)
        for(final BTConnection newbt:bt) {
            FormBody body = new FormBody.Builder()
                    .add("userid", String.valueOf(userid))
                    .add("connect_date",BTConnection.DateToString(newbt.datetime))
                    .add("connect_time",String.valueOf(newbt.duration))
                    .add("self_mac",my_mac)
                    .add("connect_mac",newbt.MAC_address)
                    .build();
            Request request = new Request.Builder()
                    .url("http://39.97.163.234:8443/api/bluetoothInfo/insertOne")
                    .post(body)
                    .build();

            Log.d("send",  userid+"\n"+my_mac+"\n"+
                    BTConnection.DateToString(newbt.datetime)+"\n"+String.valueOf(newbt.duration)
                    +"\n"+newbt.MAC_address);

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
                    Log.d("LoginTest", "onResponse: " + s);
                    newbt.isSent=1;
                    dbAdapter.updateBTConnection(newbt.ID,newbt);
                    //Toast.makeText(getActivity().getBaseContext(),"上传蓝牙连接ID:"+newbt.ID, Toast.LENGTH_SHORT).show();
                }
            });

        }

        if(bt==null)
        {Toast.makeText(getActivity().getBaseContext(),"无蓝牙连接需要上传", Toast.LENGTH_SHORT).show();}
        else Toast.makeText(getActivity().getBaseContext(),"上传结束", Toast.LENGTH_SHORT).show();
    }
}
