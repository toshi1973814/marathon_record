package jp.walden.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalendarUtil {
	public static String getCurrentCalendarString(String format) {
  		Calendar currentDate = Calendar.getInstance();
  		SimpleDateFormat formatter= 
  				new SimpleDateFormat(format);
  		String dateNow = formatter.format(currentDate.getTime());
		return dateNow;
	}

}
