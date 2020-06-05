package com.example.githubtest.SQL;

import java.util.Date;

public class BroadcastKey {
    public long ID = -1;
    public Date connect_date;//日期时间
    public int connect_time;//连接持续时间
    public String connect_mac;
    public String self_mac;

    public  BroadcastKey()
    {}

    public BroadcastKey(Date connect_date,int connect_time,String connect_mac,String self_mac)
    {
        this.connect_date=connect_date;
        this.connect_time=connect_time;
        this.connect_mac=connect_mac;
        this.self_mac=self_mac;
    }


    @Override
    public String toString(){
        String result = "";
        result += "id为" + this.ID +"，";
        result += "时间为" + this.connect_date.toString() + "，";
        result += "确诊患者MAC地址为" + this.self_mac;
        result += "连接MAC地址为" + this.connect_mac;
        result += "持续时间为"+ this.connect_time +"毫秒.";
        return result;
    }
}
