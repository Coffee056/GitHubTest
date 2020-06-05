package com.example.githubtest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "我的-生命周期";
    private boolean isAuthentication = false;
    private String name = null;
    private String IDnumber = null;
    private String health = null;
    private double risk = 0.0;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView tv_authentication;
    private TextView tv_phone_number;
    private TextView tv_health;
    private TextView tv_risk;

    private String mobileNumber = null;

    public MeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MeFragment newInstance(String param1, String param2) {
        MeFragment fragment = new MeFragment();
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
        Log.d(TAG, "onCreate: ");

        //mobileNumber = ((HomeActivity) getActivity()).getMobileNumber();

        SharedPreferences preferences = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        mobileNumber = preferences.getString("tel",null);
        name = preferences.getString("name",null);
        IDnumber = preferences.getString("IDnumber",null);
        health = preferences.getString("health",null);
        risk = preferences.getFloat("risk",0.0f);
        risk = new BigDecimal(risk*100).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        tv_authentication = (TextView) view.findViewById(R.id.tv_authentication);
        tv_phone_number = (TextView) view.findViewById(R.id.tv_phone_number);
        tv_health = (TextView) view.findViewById(R.id.tv_health);
        tv_risk = (TextView) view.findViewById(R.id.tv_risk);

        if(name != null && IDnumber != null){
            isAuthentication = true;
            tv_authentication.setText(name+"(已实名)");
        }

        tv_health.setText(health);
        tv_risk.setText(String.valueOf(risk)+"%");

        tv_authentication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),AuthenticationActivity.class);
                intent.putExtra("isAuthentication",isAuthentication);
                intent.putExtra("name",name);
                intent.putExtra("IDnumber",IDnumber);
                startActivityForResult(intent,1);
            }
        });

        if(mobileNumber.length() == 11) {
            tv_phone_number.setText(mobileNumber.substring(0, 3) + "****" + mobileNumber.substring(7));
        }else{
            tv_phone_number.setText(mobileNumber);
        }
        tv_phone_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),SetPhoneNumberActivity.class);
                intent.putExtra("mobileNumber",mobileNumber);
                startActivity(intent);
            }
        });
        return view;
    }

    //数据回调方法
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 1:
                if(resultCode == getActivity().RESULT_OK){
                    name = data.getStringExtra("name");
                    IDnumber = data.getStringExtra("IDnumber");
                    if(name != null){
                        Toast.makeText(getActivity(), "认证成功!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onActivityResult: not null");
                        tv_authentication.setText(name+"(已实名)");
                        isAuthentication = true;
                    }else{
                        Log.d(TAG, "onActivityResult: name is null");
                    }
                }
        }
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

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "onPause: ");
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
}
