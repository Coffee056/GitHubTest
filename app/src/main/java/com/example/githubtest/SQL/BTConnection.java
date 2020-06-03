package com.example.githubtest.SQL;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;

public class BTConnection {
    public int ID = -1;
    public Date date;
    public Time time;
    public int duration =0;
    public String MAC_address;

    public  BTConnection()
    {}

    public BTConnection(Date date, Time time,String address)
    {
        this.date = date;
        this.time = time;
        this.MAC_address = address;
    }


    @Override
    public String toString(){
        String result = "";
        result += "id为" + this.ID +"，";
        result += "日期为" + this.date.toString() + "，";
        result += "连接开始时间为" + this.time.toString() + "，";
        result += "MAC地址为" + this.MAC_address;
        result += "持续时间为"+ this.duration +"秒.";
        return result;
    }

    public static Date strToDate(String strDate) {
        String str = strDate;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date d = null;
        try {
            d = format.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Date date = new Date(d.getTime());
        return date;
    }

    public static Time strToTime(String strDate) {
        String str = strDate;
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        java.util.Date d = null;
        try {
            d = format.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Time time = new Time(d.getTime());
        return time;
    }
}