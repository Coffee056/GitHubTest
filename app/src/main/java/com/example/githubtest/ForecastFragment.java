package com.example.githubtest;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.githubtest.SQL.BTConnection;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForecastFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForecastFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView tv_result;
    private Button btn_forecast;
    private String s;

    public ForecastFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ForecastFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ForecastFragment newInstance(String param1, String param2) {
        ForecastFragment fragment = new ForecastFragment();
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
        View view = inflater.inflate(R.layout.fragment_forecast, container, false);
        btn_forecast = (Button) view.findViewById(R.id.btn_forecast);
        tv_result = (TextView) view.findViewById(R.id.tv_result);

        btn_forecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userForecast();
            }
        });

        return view;
    }

    private void userForecast(){
        SharedPreferences preferences = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        int userid = preferences.getInt("userid", 0);
        String my_mac = getLocalMacAddress();

        OkHttpClient client = new OkHttpClient();
        Calendar no = Calendar.getInstance();
        no.set(Calendar.DATE, no.get(Calendar.DATE) - 28);
        String time = BTConnection.DateToString(no.getTime());
        Log.d("getForecastTest", "time: "+time);
        FormBody body = new FormBody.Builder()
                .add("userid", String.valueOf(userid))
                .add("selfmac","iiiii")   //getLocalMacAddress()
                .add("date",time)
                .build();
        Request request = new Request.Builder()
                .url("http://39.97.163.234:8443/api/bluetoothInfo/getForecast")
                .post(body)
                .build();


        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Log.d("getForecastTest", "onFailure: 访问服务器失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                s = response.body().string();
                Log.d("getForecastTest", "onResponse: " + s);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        double result = Double.parseDouble(s);
                        double r = new BigDecimal(result*100).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
                        tv_result.setText("经预测分析，您的感染概率为:"+String.valueOf(r)+"%");
                    }
                });
            }
        });

    }

    public String getLocalMacAddress() {
        WifiManager wifi = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }
}
