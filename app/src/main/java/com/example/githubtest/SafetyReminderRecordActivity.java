package com.example.githubtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.githubtest.SQL.DBAdapter;
import com.example.githubtest.SQL.SafetyReminder;
import com.example.githubtest.adapter.MyAdapter;
import com.example.githubtest.adapter.SafetyRemindAdapter;
import com.example.githubtest.model.Report;

import java.util.ArrayList;
import java.util.List;

public class SafetyReminderRecordActivity extends AppCompatActivity {

    private ImageView btn_back;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private DBAdapter dbAdapter;

    private List<SafetyReminder> safetyReminders = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_reminder_record);

        btn_back = (ImageView) findViewById(R.id.back_btn);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        dbAdapter = new DBAdapter(this);
        dbAdapter.open();//启动数据库

        SafetyReminder[] srs = dbAdapter.queryUncofirmSafetyReminder();

        if(srs!=null)
        {
            for(SafetyReminder sr:srs)
                safetyReminders.add(sr);
            UpdateRecord();
        }



    }

    public void UpdateRecord()
    {
        // specify an adapter (see also next example)
        mAdapter = new SafetyRemindAdapter(safetyReminders);
        recyclerView.setAdapter(mAdapter);
    }
}
