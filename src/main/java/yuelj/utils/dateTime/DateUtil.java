package yuelj.utils.dateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import yuelj.utils.logs.SystemLog;

public class DateUtil {

	/**
	 * 获取当前时间
	 * <pre>YYYY-MM-dd HH:mm:ss
	 * @return
	 */
	public static String nowDatetime(){
		return getDateTimeString(new Date());
	}
	/**
	 * 6位随机数
	 * 
	 * @return
	 */
	public static String getRandomNum() {
		Calendar ca = Calendar.getInstance();
		String la = "" + ca.getTimeInMillis();
		return la.substring(0, 10);
	}

	/**
	 * 星期几
	 * 
	 * @return 0星期天，1星期一，2星期二 ... 6星期六
	 */
	public static int getWeekDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		// int hour=c.get(Calendar.DAY_OF_WEEK);
		// hour中存的就是星期几了，其范围 1~7
		// 1=星期日 7=星期六，其他类推
		return c.get(Calendar.DAY_OF_WEEK) - 1;
	}

	/**
	 * 得到小时
	 * 
	 * @return 24小时制
	 */
	public static int getDayHour() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		return c.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 获得当前日期前几天
	 * 
	 * @param day
	 *            提前的天数
	 * @return String yyyy-MM-dd HH:mm:ss
	 */
	public static String getTimeStr(int day) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String resultDay = df.format(getTimeDate(day));
		return resultDay;
	}

	/**
	 * 获得当前日期前几天
	 * 
	 * @param day
	 *            提前的天数
	 * @return Date
	 */
	public static Date getTimeDate(int day) {
		Date d = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.DATE, -day);
		return cal.getTime();
	}

	/**
	 * 获取当前时间的前几分钟
	 * 
	 * @param minute
	 *            前几分钟
	 * @return
	 */
	public static String getMinuteStr(int minute) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String resultDay = df.format(getMinuteDate(minute));
		return resultDay;
	}

	private static Date getMinuteDate(int minute) {
		Date d = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.MINUTE, -minute);
		return cal.getTime();
	}

	public static long getHowManyDay(String t) throws ParseException {
		DateFormat df = DateFormat.getDateInstance();
		Date date = df.parse(t);
		Calendar othercal = Calendar.getInstance();
		othercal.setTime(date);
		Calendar cal = Calendar.getInstance();
		long m = cal.getTimeInMillis() - othercal.getTimeInMillis();
		return ((m / 1000) / 3600) / 24;

	}

	public static long getHowManyMinute(String t) throws ParseException {
		if (t.contains(".")) {
			String[] b = t.split("\\.");
			t = b[0];
			SystemLog.printlog(t);
		}
		SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = formatDate.parse(t);
		Calendar othercal = Calendar.getInstance();
		othercal.setTime(date);
		Calendar cal = Calendar.getInstance();
		long m = cal.getTimeInMillis() - othercal.getTimeInMillis();
		return (m / 1000) / 60;

	}

	/**
	 * 以2015-02-09的形式获取今天的日期
	 * 
	 * @return
	 */
	public static String getTodayStr() {
		Calendar ca = Calendar.getInstance();
		String month = ca.get(ca.MONTH) + 1 + "";
		if (month.length() == 1) {
			month = "0" + month;
		}
		String date = ca.get(ca.DATE) + "";
		if (date.length() == 1) {
			date = "0" + date;
		}
		return ca.get(ca.YEAR) + "-" + month + "-" + date;
	}

	/**
	 * 以2015-02-09的形式获取今天以后day天，day正数表示+负数减
	 * 
	 * @return
	 */
	public static String getTodayPastStr(int day) {
		Calendar ca = Calendar.getInstance();
		ca.add(Calendar.DAY_OF_MONTH, day);
		String month = ca.get(ca.MONTH) + 1 + "";
		if (month.length() == 1) {
			month = "0" + month;
		}
		String date = ca.get(ca.DATE) + "";
		if (date.length() == 1) {
			date = "0" + date;
		}
		return ca.get(ca.YEAR) + "-" + month + "-" + date;
	}

	public static String getDatePastStr(Date newdate, int day) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(newdate);
		ca.add(Calendar.DAY_OF_MONTH, day);
		String month = ca.get(ca.MONTH) + 1 + "";
		if (month.length() == 1) {
			month = "0" + month;
		}
		String date = ca.get(ca.DATE) + "";
		if (date.length() == 1) {
			date = "0" + date;
		}
		return ca.get(ca.YEAR) + "-" + month + "-" + date;
	}

	public static String getDateTimePastStr(Date datetime, int day) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(datetime);
		ca.add(Calendar.DAY_OF_MONTH, day);
		String month = ca.get(ca.MONTH) + 1 + "";
		if (month.length() == 1) {
			month = "0" + month;
		}
		String date = ca.get(ca.DATE) + "";
		if (date.length() == 1) {
			date = "0" + date;
		}
		return ca.get(ca.YEAR) + "-" + month + "-" + date;
	}

	/**
	 * 返回min分钟后
	 * 
	 * @param datetime
	 * @param min
	 * @return
	 */
	public static Date getDateTimePastMin(Date datetime, int min) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(datetime);
		ca.add(Calendar.MINUTE, min);
		return ca.getTime();
	}

	public static Date getTodayPast(int day) {
		Calendar ca = Calendar.getInstance();
		ca.add(Calendar.DAY_OF_MONTH, day);
		return ca.getTime();
	}

	/**
	 * 将时间转换为“2014-10-29”的字符串形式
	 * 
	 * @param date
	 * @return
	 */
	public static String GetDateString(Date date) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		String month = ca.get(ca.MONTH) + 1 + "";
		if (month.length() == 1) {
			month = "0" + month;
		}
		String day = ca.get(ca.DATE) + "";
		if (day.length() == 1) {
			day = "0" + day;
		}
		return ca.get(ca.YEAR) + "-" + month + "-" + day;
	}

	/**
	 * 返回day1-day2的天数差
	 * 
	 * @param day1
	 *            形式 "2004-01-02 11:30:24"
	 * @param day2
	 * @return
	 */
	public static int getDayMinus(String day1, String day2) {
		long days = 0;
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date d1 = df.parse(day1);
			Date d2 = df.parse(day2);
			long diff = d1.getTime() - d2.getTime();
			days = diff / (1000 * 60 * 60 * 24);
		} catch (Exception e) {
		}
		return (int) Math.ceil(days);
	}

	/**
	 * 将时间转换为“2014-10-29 17:33:30”的字符串形式
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateTimeString(Date date) {
		String dateStr = "";
		DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			dateStr = sdf2.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateStr;
	}

	/**
	 * 将时间转换为“ 173330”的字符串形式
	 * 
	 * @param date
	 * @return
	 */
	public static String getTimeString(Date date) {
		String dateStr = "";
		DateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
		try {
			dateStr = sdf2.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateStr;
	}

	/**
	 * 根据字符串yyyy-MM-dd HH:mm:ss型时间转型为date
	 * 
	 * @param dateStr
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDateTimeStr(String dateStr) {
		Date d1 = null;
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			d1 = df.parse(dateStr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return d1;
	}

	/**
	 * 根据字符串型yyyy-MM-dd时间转型为date
	 * 
	 * @param dateStr
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDateStr(String dateStr) {
		Date d1 = null;
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			d1 = df.parse(dateStr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return d1;
	}

	/**
	 * 计算日期差
	 * 
	 * @param now
	 * @param returnDate
	 * @return
	 */
	public static int daysBetween(Date now, Date returnDate) {
		Calendar cReturnDate = Calendar.getInstance();
		Calendar cNow = Calendar.getInstance();
		cNow.setTime(returnDate);
		cReturnDate.setTime(now);
		setTimeToMidnight(cNow);
		setTimeToMidnight(cReturnDate);
		long todayMs = cNow.getTimeInMillis();
		long returnMs = cReturnDate.getTimeInMillis();
		long intervalMs = todayMs - returnMs;
		return millisecondsToDays(intervalMs) + 1;
	}

	/**
	 * 计算日期差
	 * 
	 * @param now
	 * @param returnDate
	 * @return
	 */
	public static int daysBetween(String nowStr, String returnDateStr) {
		Calendar cReturnDate = Calendar.getInstance();
		Calendar cNow = Calendar.getInstance();
		Date returnDate = DateUtil.parseDateStr(returnDateStr);
		Date now = DateUtil.parseDateStr(nowStr);
		cNow.setTime(returnDate);
		cReturnDate.setTime(now);
		setTimeToMidnight(cNow);
		setTimeToMidnight(cReturnDate);
		long todayMs = cNow.getTimeInMillis();
		long returnMs = cReturnDate.getTimeInMillis();
		long intervalMs = todayMs - returnMs;
		return millisecondsToDays(intervalMs) + 1;
	}

	public static void main(String[] args) {

		Date startdate = DateUtil.parseDateStr("2015-10-14");
		Date enddate = DateUtil.parseDateStr("2015-10-15");
		String a = daysBetween(startdate, enddate) + "";
		SystemLog.printlog(a);
	}

	private static int millisecondsToDays(long intervalMs) {
		return (int) (intervalMs / (1000 * 86400));
	}

	private static void setTimeToMidnight(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
	}

	/**
	 * 判断是否是周末
	 * 
	 * @return
	 */
	public static boolean isWeekEnd() {
		Calendar cal = Calendar.getInstance();
		if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			return true;
		}
		return false;
	}
}
