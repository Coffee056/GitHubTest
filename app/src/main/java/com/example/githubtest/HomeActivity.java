package com.example.githubtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.githubtest.SQL.BTConnection;
import com.example.githubtest.SQL.BroadcastKey;
import com.example.githubtest.SQL.DBAdapter;
import com.example.githubtest.SQL.SafetyReminder;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private RadioGroup mTabRadioGroup;
    private RadioButton rb_home;
    private RadioButton rb_forecast;
    private RadioButton rb_upload;
    private RadioButton rb_i;

    private MyFragmentPagerAdapter mAdapter;
    DBAdapter dbAdapter;

    public static Handler handler = new Handler();

    //几个代表页面的常量
    public static final int PAGE_HOME = 0;
    public static final int PAGE_FORECAST = 1;
    public static final int PAGE_UPLOAD = 2;
    public static final int PAGE_I = 3;

    private String mobileNumber = null;
    public String time="";
    Date nowdate;
    Date lastdate;

    public SafetyReminder[] SR;

    private Runnable DownloadBroadcastKey = new Runnable() {
        public void run() {
            this.update();
            handler.postDelayed(this, 1000*60*20);// 间隔20分钟
        }

        void update() {
            getBroadcastKey();
        }
    };

    private Runnable CompareLocal = new Runnable() {
        @Override
        public void run() {
            processCompare();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences preferences = this.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        mobileNumber = preferences.getString("tel", "");

        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        bindViews();
        rb_home.setChecked(true);
        dbAdapter = new DBAdapter(this);
        dbAdapter.open();//启动数据库

        //updateUserInfo();

        handler.post(DownloadBroadcastKey);
    }

    public void getBroadcastKey()
    {
            SR=dbAdapter.queryAllSafetyReminder();
            nowdate = new Date();
            Calendar no = Calendar.getInstance();
            no.setTime(nowdate);
            no.set(Calendar.DATE, no.get(Calendar.DATE) - 28);
            lastdate = no.getTime();
            time = BTConnection.DateToString(lastdate);

            dbAdapter.deleteAllBroadcastKsy();
        Log.v("更新时间",time);
        //http请求数据库
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("date",time)
                .build();
        Request request = new Request.Builder()
                .url("http://39.97.163.234:8443/api/bluetoothInfo/getBroadcastKeys")
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
                Log.d("LoginTest", "onResponse: "+s);
                processBroadcastKey(s);
            }
        });
    }

    public void processBroadcastKey(String s)
    {
       try{
           JSONArray jsonArray = new JSONArray(s);
           for(int i=0;i <jsonArray.length();i++)
           {
               JSONObject jsonObject = jsonArray.getJSONObject(i);
               String connect_time = jsonObject.getString("connect_time");
               String connect_mac = jsonObject.getString("connect_mac");
               String connect_date = jsonObject.getString("connect_date");
               String self_mac = jsonObject.getString("self_mac");
               BroadcastKey broadcastKey = new BroadcastKey(
                       BTConnection.strToDate(connect_date),Integer.parseInt(connect_time),
                       connect_mac,self_mac
               );
               long k=dbAdapter.insertBroadcastKey(broadcastKey);
               Log.d("插入情况", String.valueOf(k));
               Log.d("连接时间",connect_date);
               Log.d("连接时长",connect_time);
               Log.d("连接Mac",connect_mac);
               Log.d("确诊Mac",self_mac);
           }
           handler.post(CompareLocal);
       }catch (Exception e){
           e.printStackTrace();
       }
    }


    public String getLocalMacAddress() {
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }


    public void processCompare()
    {
            int count =0;
        SharedPreferences preferences2 = this.getSharedPreferences("Mac", Context.MODE_PRIVATE);

        String myMac = preferences2.getString("MAC", "02:00:00:00:00:00");
        myMac="098y8";
        List<String> adresslist=new ArrayList<>();
            //dbAdapter.insertBTConnection(new BTConnection(nowdate,"llll"));
            BTConnection[] bt=dbAdapter.queryBTConnectionByDate(lastdate,nowdate);
            BroadcastKey[] bk=dbAdapter.queryAllBroadcastKey();

            if(bk!=null)
            for(BroadcastKey b:bk)
            {
                if(adresslist.indexOf(b.self_mac)==-1) adresslist.add(b.self_mac);
                if(b.connect_mac.equals(myMac))
                    CreateNewSafetyReminder(new SafetyReminder(b.connect_date,b.connect_time));
            }

            if(bt!=null)
            for(BTConnection connection:bt)
            {
                if(adresslist.indexOf(connection.MAC_address)!=-1)
                    CreateNewSafetyReminder(new SafetyReminder(connection.datetime,connection.duration));;
            }

            HomeFragment.UpdateSafetyRemind();

    }

    public void CreateNewSafetyReminder(SafetyReminder s)
    {
        if(SR!=null)
        for(SafetyReminder sr:SR)
        {
            Log.d("safety",s.connect_date.toString()+"\n"+sr.connect_date.toString()
             +"\n"+s.connect_time+":"+sr.connect_time);
            if(s.connect_date.toString().equals(sr.connect_date.toString())
                    && s.connect_time== sr.connect_time)
                return;
        }
        dbAdapter.insertSafetyReminder(s);
    }

    private void bindViews(){
        mTabRadioGroup = (RadioGroup) findViewById(R.id.bottom_menu);
        rb_home = (RadioButton) findViewById(R.id.home_tab);
        rb_forecast = (RadioButton) findViewById(R.id.forecast_tab);
        rb_upload = (RadioButton) findViewById(R.id.upload_tab);
        rb_i = (RadioButton) findViewById(R.id.i_tab);

        mViewPager = (ViewPager) findViewById(R.id.fragment_viewPager);
        mViewPager.setAdapter(mAdapter);
        // register listener
        mViewPager.addOnPageChangeListener(mPageChangeListener);
        mTabRadioGroup.setOnCheckedChangeListener(mOnCheckedChangeListener);
    }

//    //从服务器更新用户信息
//    private void updateUserInfo(){
//        //http请求数据库
//        OkHttpClient client = new OkHttpClient();
//        FormBody body = new FormBody.Builder()
//                .add("tel",mobileNumber)
//                .build();
//        Request request = new Request.Builder()
//                .url("http://39.97.163.234:8443/api/userAccount/findOne")
//                .post(body)
//                .build();
//
//        Call call = client.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                e.printStackTrace();
//                Log.d("FindOneTest", "onFailure: 访问服务器失败");
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                String s = response.body().string();
//                Log.d("FindOneTest", "onResponse: "+s);
//                parseJSONWithJSONObject(s);
//            }
//        });
//    }
//
//    //处理json格式数据，并增改SharedPreferences
//    private void parseJSONWithJSONObject(String jsonData){
//        try{
//            JSONObject jsonObject = new JSONObject(jsonData);
//
//            int userid = jsonObject.getInt("userid");
//            String IDnumber = jsonObject.getString("idnumber");
//            String health = jsonObject.getString("health");
//            String name = jsonObject.getString("name");
//            double risk = jsonObject.getDouble("risk");
//
//            if(IDnumber.equals("null")){
//                Log.d("FindOneTest","IDnumber is null");
//                IDnumber = null;
//
//            }
//            if(name.equals("null")){
//                Log.d("FindOneTest","name is null");
//                name = null;
//            }
//
//            SharedPreferences.Editor editor = getSharedPreferences("UserInfo",MODE_PRIVATE).edit();
//            editor.putInt("userid",userid);
//            editor.putString("tel",mobileNumber);
//            editor.putString("name",name);
//            editor.putString("IDnumber",IDnumber);
//            editor.putString("health",health);
//            editor.putFloat("risk",(float)risk);
//            editor.apply();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//
//    }


    //下面三个成员(MyFragmentPagerAdapter,mPageChangeListener,mOnCheckedChangeListener) : Fragment+RadioGroup+RadioButton+ViewPager 实现滑动页面及底部栏
    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private final int PAGE_COUNT = 4;
        private HomeFragment myFragment1 = null;
        private ForecastFragment myFragment2 = null;
        private UploadFragment myFragment3 = null;
        private MeFragment myFragment4 = null;
        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            myFragment1 = HomeFragment.newInstance("","");
            myFragment2 = ForecastFragment.newInstance("","");
            myFragment3 = UploadFragment.newInstance("","");
            myFragment4 = MeFragment.newInstance("","");
        }
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case PAGE_HOME:
                    fragment = myFragment1;
                    break;
                case PAGE_FORECAST:
                    fragment = myFragment2;
                    break;
                case PAGE_UPLOAD:
                    fragment = myFragment3;
                    break;
                case PAGE_I:
                    fragment = myFragment4;
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }
        @Override
        public Object instantiateItem(ViewGroup vg, int position) {
            return super.instantiateItem(vg, position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //System.out.println("position Destory" + position);
            super.destroyItem(container, position, object);
        }
    }

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener(){

        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            RadioButton radioButton = (RadioButton) mTabRadioGroup.getChildAt(i);
            radioButton.setChecked(true);
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            for (int i = 0; i < group.getChildCount(); i++) {
                if (group.getChildAt(i).getId() == checkedId) {
                    mViewPager.setCurrentItem(i,false);
                    return;
                }
            }
        }
    };

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mViewPager.removeOnPageChangeListener(mPageChangeListener);
    }

    public String getMobileNumber(){
        return mobileNumber;
    }

}
