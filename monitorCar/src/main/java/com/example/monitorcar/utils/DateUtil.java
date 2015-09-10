package com.example.monitorcar.utils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
	public static long DateStringToLong(int year, int month, int day, int hour,
			int minute) {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.set(year, month-1, day, hour, minute, 0);
		long i = calendar.getTimeInMillis();
		return calendar.getTimeInMillis() / 1000;
	}
	
	
	public static Date getDateBefore(Date date, int n) {  
        Calendar now = Calendar.getInstance();    
        now.setTime(date);    
        now.set(Calendar.DATE, now.get(Calendar.DATE) - n);    
        return now.getTime();  
    }  
	
	public static String formatTime(long time) {
		String timeString = "";
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		timeString = sdf.format(time * 1000);
		return timeString;
	}
	
	public static String bigDecimalToString(long tmp) {

		return new BigDecimal(tmp).divide(new BigDecimal(1000000)).toString();
	}
}
