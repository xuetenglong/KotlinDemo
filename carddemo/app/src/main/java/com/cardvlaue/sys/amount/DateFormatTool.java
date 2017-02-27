package com.cardvlaue.sys.amount;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cardvalue on 2016/5/12.
 */
public class DateFormatTool {

    public static int decDate(String start, String end) {
        //当前日期
        SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd   hh:mm:ss");
        String   date3   =   sDateFormat.format(new   java.util.Date());
        System.out.println("<--------"+date3+"日期"+"------sDateFormat-->"+sDateFormat);


        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Date date = new Date();
        Date date1 = new Date();
        Date date2 = new Date();
        try {
            date = df.parse(start);
            date1 = df1.parse(end);
            date2=df2.parse(date3);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        long s = date.getTime();
        long e = date1.getTime();
        long e2 = date2.getTime();
        //当前时间大于结束时间，说明已经过期
      /*  if(e2<e){
            return (int) ((e - s) / 1000 / 60 / 60 / 24);
        }else{
            int i=(int) ((e - s) / 1000 / 60 / 60 / 24);
            if(i>15){
                return 0;
            }else{
                return (int) ((e - s) / 1000 / 60 / 60 / 24);
            }
        }*/

        return (int) ((e - s) / 1000 / 60 / 60 / 24);
    }
}
