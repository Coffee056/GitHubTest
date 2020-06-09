package com.example.githubtest.SQL;

import java.util.Date;

public class SafetyReminder {
    public long ID = -1;
    public Date connect_date;//日期时间
    public int connect_time =0;//连接持续时间
    public int isConfirm=0;//是否确认

    public SafetyReminder(Date connect_date, int connect_time){
        this.connect_date =connect_date;
        this.connect_time=connect_time;
    }

    @Override
    public String toString(){
        String result = "";
        result += "id为" + this.ID +"，";
        result += "时间为" + this.connect_date + "，";
        result += "持续时间为"+ this.connect_time +"毫秒.";
        result +=" 是否确认" + this.isConfirm;
        return result;
    }

    SafetyReminder(){}
}
