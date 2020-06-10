package com.example.githubtest;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.githubtest.SQL.DBAdapter;
import com.example.githubtest.SQL.SafetyReminder;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "首页-生命周期";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //private boolean isOnBlueTooth = false;

    private  static Context context;

    private static ImageButton btn_bluetooth;
    private static TextView tv_bluetooth_btn_text;
    private TextView tv_safety_reminder_record;
    private static LinearLayout ll_safety_reminder;
    private static TextView tv_safety_reminder;


   public static Handler handler = new Handler();
    public static Runnable serviceStop = new Runnable() {
        public void run() {
            btn_bluetooth.setSelected(false);
            tv_bluetooth_btn_text.setText("扫描未开启");
            tv_bluetooth_btn_text.setTextColor(Color.parseColor("#9D9D9D"));
        }
    };

    public static Runnable SafetyRemind = new Runnable() {
        public void run() {
            DBAdapter dbAdapter = new DBAdapter(context);
            dbAdapter.open();//启动数据库
            SafetyReminder[] safetyReminder=dbAdapter.queryUncofirmSafetyReminder();
            if(safetyReminder==null)
            {
                ll_safety_reminder.setBackgroundColor(Color.parseColor("#00CC00"));
                tv_safety_reminder.setText("未检测到安全风险");
            }
            else
            {
                ll_safety_reminder.setBackgroundColor(Color.parseColor("#EE2C2C"));
                tv_safety_reminder.setText("检测到"+safetyReminder.length+"个风险,详情见安全提醒记录");
            }
        }
    };

    public static void UpdateGUI() {
        handler.post(serviceStop);
    }
    public static void UpdateSafetyRemind() {
        handler.post(SafetyRemind);
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        SharedPreferences preferences = getActivity().getSharedPreferences("data",Context.MODE_PRIVATE);
        //isOnBlueTooth = preferences.getBoolean("isOnBlueTooth",false);
        //Log.d(TAG, "onCreate: " + "SharedPreferences-get-isOnBlueTooth = " + isOnBlueTooth);
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        btn_bluetooth = (ImageButton) view.findViewById(R.id.btn_bluetooth);
        tv_bluetooth_btn_text = (TextView) view.findViewById(R.id.tv_bluetooth_btn_text);
        tv_safety_reminder_record = (TextView) view.findViewById(R.id.tv_safety_reminder_record);
        ll_safety_reminder = (LinearLayout) view.findViewById(R.id.ll_safety_reminder);
        tv_safety_reminder= (TextView) view.findViewById(R.id.tv_safety_reminder);

        context=this.getActivity();
        isServiceRun();
        final Intent serviceIntent = new Intent(getActivity(), BlueToothService.class);
        btn_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!btn_bluetooth.isSelected()){
                    getActivity().startService(serviceIntent);
                    onBlueTooth();
                }else{
                    getActivity().stopService(serviceIntent);
                    offBlueTooth();
                }
            }
        });

        tv_safety_reminder_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),SafetyReminderRecordActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    public void isServiceRun()
    {
        boolean serviceRunning = isServiceRunning(getActivity(),
                "com.example.githubtest.BlueToothService");

        if(serviceRunning)
        {
            onBlueTooth();
        }else{
            offBlueTooth();
        }

    }

    private void onBlueTooth(){
        //isOnBlueTooth = true;
        btn_bluetooth.setSelected(true);
        tv_bluetooth_btn_text.setText("扫描已开启");
        tv_bluetooth_btn_text.setTextColor(getResources().getColor(R.color.white));
    }

    private void offBlueTooth(){
        //isOnBlueTooth = false;
        btn_bluetooth.setSelected(false);
        tv_bluetooth_btn_text.setText("扫描未开启");
        tv_bluetooth_btn_text.setTextColor(getResources().getColor(R.color.text_grey));
    }

    public static boolean isServiceRunning(Context context,String serviceName){
        // 校验服务是否还存在
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = am.getRunningServices(100);

        for (ActivityManager.RunningServiceInfo info : services) {
            // 得到所有正在运行的服务的名称
            String name = info.service.getClassName();
            if (serviceName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onResume(){
        super.onResume();
        HomeFragment.UpdateSafetyRemind();
        Log.d(TAG, "onResume: ");
    }




    //数据持久化，保存是否开启蓝牙
    @Override
    public void onPause(){
        super.onPause();
//        SharedPreferences.Editor editor = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE).edit();
//        editor.putBoolean("isOnBlueTooth",isOnBlueTooth);
//        editor.apply();
//        Log.d(TAG, "onPause: SharedPreferences-save-isOnBlueTooth="+isOnBlueTooth);

    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
    }





    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onDetach(){
        super.onDetach();
        Log.d(TAG, "onDetach: ");
    }


}
