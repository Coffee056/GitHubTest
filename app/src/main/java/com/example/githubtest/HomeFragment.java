package com.example.githubtest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


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
    private boolean isOnBlueTooth = false;

    private ImageButton btn_bluetooth;
    private TextView tv_bluetooth_btn_text;
    private TextView tv_safety_reminder_record;

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
        isOnBlueTooth = preferences.getBoolean("isOnBlueTooth",false);
        Log.d(TAG, "onCreate: " + "SharedPreferences-get-isOnBlueTooth = " + isOnBlueTooth);
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

        if(isOnBlueTooth){
            onBlueTooth();
        }else{
            offBlueTooth();
        }

        btn_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!btn_bluetooth.isSelected()){
                    onBlueTooth();
                }else{
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

    private void onBlueTooth(){
        isOnBlueTooth = true;
        btn_bluetooth.setSelected(true);
        tv_bluetooth_btn_text.setText("蓝牙已开启");
        tv_bluetooth_btn_text.setTextColor(getResources().getColor(R.color.white));
    }

    private void offBlueTooth(){
        isOnBlueTooth = false;
        btn_bluetooth.setSelected(false);
        tv_bluetooth_btn_text.setText("蓝牙未开启");
        tv_bluetooth_btn_text.setTextColor(getResources().getColor(R.color.text_grey));
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume: ");
    }




    //数据持久化，保存是否开启蓝牙
    @Override
    public void onPause(){
        super.onPause();
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE).edit();
        editor.putBoolean("isOnBlueTooth",isOnBlueTooth);
        editor.apply();
        Log.d(TAG, "onPause: SharedPreferences-save-isOnBlueTooth="+isOnBlueTooth);

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
