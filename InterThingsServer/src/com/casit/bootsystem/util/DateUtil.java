package com.casit.bootsystem.util;
import java.text.*;
import java.util.*;
public class DateUtil {
	// 把Calendar对象格式化为需要的日期格式
	public static String format(String dateFormat, Calendar calendar) {
		try {
			return new SimpleDateFormat(dateFormat).format(calendar.getTime());
		} catch (Exception e) {
			return null;
		}
	}
	// 把Date对象按照dateFormat指定的模式格式化
	public static String format(String dateFormat, Date date) {
		try {
			return new SimpleDateFormat(dateFormat).format(date);
		} catch (Exception e) {
			return null;
		}
	}
	// 把字符串dateString解析为日期对象
	public static Date parse(String dateFormat, String dateString) {
		try {
			return new SimpleDateFormat(dateFormat).parse(dateString);
		} catch (Exception ex) {
			return null;
		}
	}
	// 把日期对象转换为Calendar日历对象
	public static Calendar toCalendar(Date date) {
		if (date != null) {
			Calendar cl = Calendar.getInstance();
			cl.setTime(date);
			return cl;
		}
		return null;
	}
	// 把Calendar日历对象转换为日期对象
	public static Date toDate(Calendar calendar) {
		return (calendar == null) ? null : calendar.getTime();
	}
	// 返回当年
	public static int getCurrentYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}
	
	
	  public static Date getStartTime() {
			Calendar todayStart = Calendar.getInstance();
			todayStart.set(Calendar.HOUR_OF_DAY,0);
			todayStart.set(Calendar.MINUTE,0);
			todayStart.set(Calendar.SECOND,0);
			todayStart.set(Calendar.MILLISECOND,0);
			return todayStart.getTime();
		}
		 
	  public static Date getEndTime() {
			Calendar todayEnd = Calendar.getInstance();
			todayEnd.set(Calendar.HOUR_OF_DAY,23);
			todayEnd.set(Calendar.MINUTE,59);
			todayEnd.set(Calendar.SECOND,59);
			todayEnd.set(Calendar.MILLISECOND,999);
			return todayEnd.getTime();
		}
}
