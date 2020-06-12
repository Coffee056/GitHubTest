package com.example.githubtest.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.githubtest.R;
import com.example.githubtest.model.Report;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<Report> mReports;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_report_text;
        TextView report_btn;

        public MyViewHolder(View view) {
            super(view);
            tv_report_text = view.findViewById(R.id.tv_report_text);
            report_btn = view.findViewById(R.id.report_btn);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<Report> reports) {
        mReports = reports;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.report_item,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Report report = mReports.get(position);
        holder.tv_report_text.setText(report.getType()+"      日期:"+report.getDate()+"         "+report.getStatus());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mReports.size();
    }
}

