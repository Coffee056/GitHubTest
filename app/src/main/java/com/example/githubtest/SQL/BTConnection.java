package com.example.githubtest.SQL;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BTConnection {
    public long ID = -1;
    public Date datetime;//日期时间
    public int duration =0;//连接持续时间
    public int isSent =0;//是否上传
    public String MAC_address;

    public  BTConnection()
    {}

    public BTConnection(Date datetime, String address)
    {
        this.datetime = datetime;
        this.MAC_address = address;
    }


    @Override
    public String toString(){
        String result = "";
        result += "id为" + this.ID +"，";
        result += "时间为" + this.datetime.toString() + "，";
        result += "MAC地址为" + this.MAC_address;
        result += "持续时间为"+ this.duration +"秒.";
        result +=" 是否发送" + this.isSent;
        return result;
    }

    public static Date strToDate(String strDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.v("Test",strDate);
        Date d=null;
        try {
            d = format.parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return d;
    }

    public static String DateToString(Date date)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s=format.format(date);
        Log.v("Time",s);
        return s;
    }

}