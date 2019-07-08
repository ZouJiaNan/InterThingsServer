package com.casit.bootsystem.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTools
{
  public static String dateToTime(String date, String dateStyle)
  {
    SimpleDateFormat format = new SimpleDateFormat(dateStyle);
    try
    {
      Date oldDate = format.parse(date);
      long time = oldDate.getTime();
      long nowTime = System.currentTimeMillis();
      long second = nowTime - time;
      second /= 1000L;
      long days = second / 86400L;
      second %= 86400L;
      long hours = second / 3600L;
      second %= 3600L;
      long minutes = second / 60L;
      second %= 60L;
      if (days > 0L) {
        return days + "天" + hours + "小时" + minutes + "分" + second + "秒";
      }
      return hours + "小时" + minutes + "分" + second + "秒";
    }
    catch (ParseException e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  public static String secondToTime(long mss)
  {
    long days = mss / 86400000L;
    long hours = mss % 86400000L / 3600000L;
    long minutes = mss % 3600000L / 60000L;
    long seconds = mss % 60000L / 1000L;
    return days + " days " + hours + " hours " + minutes + " minutes " + 
      seconds + " seconds ";
  }
  
  public static Date StrToDate(String str)
  {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date = null;
    try
    {
      date = format.parse(str);
    }
    catch (ParseException e)
    {
      e.printStackTrace();
    }
    return date;
  }
  
  public static String DateToStr(Date date)
  {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String str = format.format(date);
    return str;
  }
}
