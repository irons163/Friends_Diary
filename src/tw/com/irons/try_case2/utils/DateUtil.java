package tw.com.irons.try_case2.utils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	public static Calendar calendar = Calendar.getInstance();

	/* 傳入日期如20090101及週期、格式字串如yyyyMMdd回傳下次日期 */
	public static String getNextDate(String date, int period, String format) {
		int mYear = Integer.parseInt(date.substring(0, 4));
		int mMonth = Integer.parseInt(date.substring(4, 6));
		int mDay = Integer.parseInt(date.substring(6, 8));
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);
		String nextDate = "";
		calendar = Calendar.getInstance();
		calendar.set(mYear, mMonth - 1, mDay);
		calendar.add(Calendar.DAY_OF_YEAR, period);
		nextDate = sdf.format(calendar.getTime());
		return nextDate;
	}

	/* 計算兩個日期所差的天數 */
	public static int computerDiffDate(long date1, long date2) {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTimeInMillis(date1);

		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTimeInMillis(date2);
		/* 先判斷是否同年 */
		int y1 = calendar1.get(Calendar.YEAR);
		int y2 = calendar2.get(Calendar.YEAR);

		int d1 = calendar1.get(Calendar.DAY_OF_YEAR);
		int d2 = calendar2.get(Calendar.DAY_OF_YEAR);
		int maxDays = 0;
		int day = 0;
		if (y1 - y2 > 0) {
			maxDays = calendar2.getActualMaximum(Calendar.DAY_OF_YEAR);
			day = d1 - d2 + maxDays;
		} else if (y1 - y2 < 0) {
			maxDays = calendar1.getActualMaximum(Calendar.DAY_OF_YEAR);
			day = d1 - d2 - maxDays;
		} else {
			day = d1 - d2;
		}
		return day;
	}

	/* 傳入日期如20090101回傳日期的long值 */
	public static long getDateTime(String strDate) {
		return getDateByFormat(strDate, "yyyyMMdd").getTime();
	}

	/* 傳入日期的long值及格式字串如yyyyMMdd */
	public static String getDateTime(String format, long millis) {
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);
		return (sdf.format(calendar.getTime()));
	}

	public static Date getDateByFormat(String strDate, String format) {
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);
		try {
			return (sdf.parse(strDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
