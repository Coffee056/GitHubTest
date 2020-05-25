package com.example.githubtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private RadioGroup mTabRadioGroup;
    private RadioButton rb_home;
    private RadioButton rb_forecast;
    private RadioButton rb_upload;
    private RadioButton rb_i;

    private MyFragmentPagerAdapter mAdapter;

    //几个代表页面的常量
    public static final int PAGE_HOME = 0;
    public static final int PAGE_FORECAST = 1;
    public static final int PAGE_UPLOAD = 2;
    public static final int PAGE_I = 3;

    private String mobileNumber = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mobileNumber = getIntent().getStringExtra("mobileNumber");

        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        bindViews();
        rb_home.setChecked(true);

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

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private final int PAGE_COUNT = 4;
        private HomeFragment myFragment1 = null;
        private Fragment myFragment2 = null;
        private UploadFragment myFragment3 = null;
        private MeFragment myFragment4 = null;
        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            myFragment1 = HomeFragment.newInstance("","");
            myFragment2 = BlankFragment.newInstance("预测","");
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
