package com.example.githubtest.adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.githubtest.R;
import com.example.githubtest.SQL.BTConnection;
import com.example.githubtest.SQL.SafetyReminder;
import com.example.githubtest.model.Report;

import java.util.List;

public class SafetyRemindAdapter extends RecyclerView.Adapter<SafetyRemindAdapter.MyViewHolder> {

    private List<SafetyReminder> mSafetyReminder;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_safety_reminder_text;

        public MyViewHolder(View view) {
            super(view);
            tv_safety_reminder_text = view.findViewById(R.id.tv_safety_reminder_text);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SafetyRemindAdapter(List<SafetyReminder> SafetyReminder) {
        mSafetyReminder = SafetyReminder;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SafetyRemindAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.safetyreminder_item,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        SafetyReminder safetyReminder = mSafetyReminder.get(position);
        String s="记录"+(position+1)+"  接触时间:"+
                BTConnection.DateToString(safetyReminder.connect_date)+
                " ; 持续时间:"+safetyReminder.connect_time+"毫秒";
        Log.v("time",safetyReminder.toString());
        if(safetyReminder.isConfirm==1) s+="  已确认";
        holder.tv_safety_reminder_text.setText(s);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mSafetyReminder.size();
    }
}

