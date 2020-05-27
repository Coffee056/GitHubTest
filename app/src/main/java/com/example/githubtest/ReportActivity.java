package com.example.githubtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class ReportActivity extends AppCompatActivity {
    private ImageView btn_back;
    private ViewPager mViewPager;
    private RadioGroup mTabRadioGroup;
    private RadioButton rb_diagnosis;
    private RadioButton rb_recure;

    private MyFragmentPagerAdapter mAdapter;



    //几个代表页面的常量
    public static final int PAGE_HOME = 0;
    public static final int PAGE_FORECAST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        bindViews();
        rb_diagnosis.setChecked(true);

    }

    private void bindViews(){
        btn_back = (ImageView) findViewById(R.id.back_btn);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTabRadioGroup = (RadioGroup) findViewById(R.id.bottom_menu);
        rb_diagnosis = (RadioButton) findViewById(R.id.diagnosis_tab);
        rb_recure = (RadioButton) findViewById(R.id.recure_tab);

        mViewPager = (ViewPager) findViewById(R.id.fragment_viewPager);
        mViewPager.setAdapter(mAdapter);
        // register listener
        mViewPager.addOnPageChangeListener(mPageChangeListener);
        mTabRadioGroup.setOnCheckedChangeListener(mOnCheckedChangeListener);
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private final int PAGE_COUNT = 2;
        private DiagnosisFragment myFragment1 = null;
        private Fragment myFragment2 = null;

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            myFragment1 = DiagnosisFragment.newInstance("","");
            myFragment2 = BlankFragment.newInstance("康复","");
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

}
