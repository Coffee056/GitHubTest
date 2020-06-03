package com.example.githubtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.githubtest.adapter.MyAdapter;
import com.example.githubtest.model.Report;

import java.util.ArrayList;
import java.util.List;

public class UploadRecordActivity extends AppCompatActivity {

    private ImageView btn_back;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<Report> reports = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_record);

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

        Report r = new Report();
        r.setName("张三");
        r.setIDnumber("123");
        r.setTel("12345678");
        r.setRegion("BeiJing");
        r.setHospital("RenMingHospital");
        r.setDate("2020-06-03");
        r.setType("确诊上报");
        r.setStatus("已审核");
        Report r1 = new Report();
        r1.setDate("2020-06-04");
        r1.setType("康复上报");
        r1.setStatus("待审核");
        reports.add(r);
        reports.add(r1);
        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(reports);
        recyclerView.setAdapter(mAdapter);

    }
}
