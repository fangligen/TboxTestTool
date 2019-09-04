package com.gofun.tbox.tools.ble.transfer.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class DateUtil {
	public static final SimpleDateFormat shortsdf = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat longsdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat shortsdfUTC = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat longsdfUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat localsdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
	public static final String PUSH_MESSAGE_FORMAT = "MM月dd日 HH:mm";
	public static final String str[] = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };// 字符串数组
	// public static final int MINUTE_10 = 10;
	// public static final int MINUTE_20 = 20;

	/**
	 * 时间格式化（到日期）
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDate(Date date) {
		String newDate = "";
		if (date == null)
			return newDate;
		try {
			newDate = shortsdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return newDate;
	}

	/**
	 * 时间格式化（到秒）
	 * 
	 * @param date
	 * @return
	 */
	public static String formatTime(Date date) {
		String newDate = "";
		if (date == null)
			return newDate;
		try {
			newDate = longsdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return newDate;
	}

	public static String formatLoaclTime(Date date) {
		String newDate = "";
		if (date == null)
			return newDate;
		try {
			newDate = localsdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newDate;
	}

	/*
	 * 根据时间转换问候语 早上：5:00 —— 8:59 上午：9:00 ——10:59 中午：11:00——12:59 下午：13:00——18:59
	 * 晚上：19:00——23:59 凌晨：24:00—— 4:59
	 */
	public static String getGreetings() {
		String greetings = "早上好";
		String nowdate1 = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());// 获取当前时间
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
		try {
			Date d1 = sdf.parse("5:00:00");
			Date d2 = sdf.parse("8:59:59");

			Date d3 = sdf.parse("9:00:00");
			Date d4 = sdf.parse("10:59:59");

			Date d5 = sdf.parse("11:00:00");
			Date d6 = sdf.parse("12:59:59");

			Date d7 = sdf.parse("13:00:00");
			Date d8 = sdf.parse("18:59:59");

			Date d9 = sdf.parse("19:00:00");
			Date d10 = sdf.parse("23:59:59");

			Date d11 = sdf.parse("24:00:00");
			Date d12 = sdf.parse("4:59:59");
			Date nowdate = sdf.parse(nowdate1);
			if (nowdate.after(d12) && nowdate.before(d3))
				greetings = "早上好";
			else if (nowdate.after(d2) && nowdate.before(d5))
				greetings = "上午好";
			else if (nowdate.after(d4) && nowdate.before(d7))
				greetings = "中午好";
			else if (nowdate.after(d6) && nowdate.before(d9))
				greetings = "下午好";
			else if (nowdate.after(d8) && nowdate.before(d11))
				greetings = "晚上好";
			else if (nowdate.after(d10) && nowdate.before(d1))
				greetings = "凌晨好";
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return greetings;
	}

	/**
	 * 时间格式化（到秒）
	 * 
	 * @param ticks
	 *            时间刻度
	 * @return
	 */
	public static String formatTime(Long ticks) {
		String newDate = "";
		if (ticks == null)
			return newDate;
		try {
			Date date = new Date(ticks);
			newDate = longsdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return newDate;
	}

	public static String formatTimeMin(Date date) {
		String newDate = "";
		if (date == null)
			return newDate;
		try {
			newDate = shortsdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return newDate;
	}

	public static Date string2Date(String s, int type) {
		if (s == null) {
			return null;
		}
		Calendar cal = null;
		String a[] = s.split("-| |:");
		try {
			if (a.length >= 3) {
				cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, Integer.valueOf(a[0]));
				cal.set(Calendar.MONTH, Integer.valueOf(a[1]) - 1);
				cal.set(Calendar.DATE, Integer.valueOf(a[2]));
			}
			if (type == 0) {
				if (a.length >= 5) {
					cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(a[3]));
					cal.set(Calendar.MINUTE, Integer.valueOf(a[4]));
					if (a.length == 6) {
						cal.set(Calendar.SECOND, Integer.valueOf(a[5]));
					}
				} else {
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
				}
			} else if (type == 1) {
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
			} else if (type == 2) {
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.SECOND, 59);
			}
		} catch (Exception e) {

		}
		if (cal != null) {
			return cal.getTime();
		}
		return null;
	}

	public static Date stringDateMin(String s) {
		Date date = null;

		try {
			date = shortsdf.parse(s);
		} catch (ParseException e) {
		}
		return date;
	}

	public static Date stringDate(String s) {
		Date date = null;

		try {
			date = longsdf.parse(s);
		} catch (ParseException e) {
		}
		return date;
	}

	/**
	 * 以短格式格式化时间,实例：2010-09-19
	 * 
	 * @param time
	 *            时间刻度
	 * @return 格式化后的时间
	 * @author zhengrunjin @ 2010-09-19
	 */
	public static String stringDateShortFormat(Long time) {
		if (time != null) {
			return shortsdf.format(new Date(time));
		}
		return null;
	}

	public static String stringDate(Long l) {
		if (l != null) {
			return longsdf.format(new Date(l));
		}
		return null;
	}

	public static String stringDateShortFormatUTC(Long time) {
		if (time != null) {

			shortsdf.setTimeZone(TimeZone.getDefault());
			return shortsdf.format(new Date(time * 1000));
		}
		return null;
	}

	public static String stringDateUTC(Long l) {
		if (l != null) {
			longsdf.setTimeZone(TimeZone.getDefault());
			return longsdf.format(new Date(l * 1000));
		}
		return "";
	}

	public static String stringDateUTC(Long l, String format) {
		if (l != null) {
			SimpleDateFormat df = new SimpleDateFormat(format);
			df.setTimeZone(TimeZone.getDefault());
			return df.format(new Date(l * 1000));
		}
		return "";
	}

	/**
	 * 返回于指定日期间隔一定天数的日期
	 * 
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date getSpecDate(Date date, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - days);
		return calendar.getTime();
	}

	public static boolean after(Date date1, Date date2) {
		Calendar dc1 = Calendar.getInstance();
		dc1.setTime(date1);
		Calendar dc2 = Calendar.getInstance();
		dc2.setTime(date2);
		return dc1.after(dc2);
	}

	public static boolean before(Date date1, Date date2) {
		Calendar dc1 = Calendar.getInstance();
		dc1.setTime(date1);
		Calendar dc2 = Calendar.getInstance();
		dc2.setTime(date2);
		return dc1.before(dc2);
	}

	// 日期转换
	public static java.sql.Date getBeforeAfterDate(String datestr, int day) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		java.sql.Date olddate = null;
		try {
			df.setLenient(false);
			olddate = new java.sql.Date(df.parse(datestr).getTime());
		} catch (ParseException e) {
			throw new RuntimeException("日期转换错误");
		}
		Calendar cal = new GregorianCalendar();
		cal.setTime(olddate);

		int Year = cal.get(Calendar.YEAR);
		int Month = cal.get(Calendar.MONTH);
		int Day = cal.get(Calendar.DAY_OF_MONTH);

		int NewDay = Day + day;

		cal.set(Calendar.YEAR, Year);
		cal.set(Calendar.MONTH, Month);
		cal.set(Calendar.DAY_OF_MONTH, NewDay);

		return new java.sql.Date(cal.getTimeInMillis());
	}

	// 日期转换
	public static Date getBeforeAfterDate(Date date, int day) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);

		int Year = cal.get(Calendar.YEAR);
		int Month = cal.get(Calendar.MONTH);
		int Day = cal.get(Calendar.DAY_OF_MONTH);

		int NewDay = Day + day;

		cal.set(Calendar.YEAR, Year);
		cal.set(Calendar.MONTH, Month);
		cal.set(Calendar.DAY_OF_MONTH, NewDay);

		return new Date(cal.getTimeInMillis());
	}

	// 参数日期+小时数得到新日期
	// type：1=天数 ，2=小时数 3=月数
	public static Date getNewDate(Date d, int num, int type) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		switch (type) {
		case 1:
			calendar.add(Calendar.DAY_OF_YEAR, num);
			break;
		case 2:
			calendar.add(Calendar.HOUR_OF_DAY, num);
			break;
		case 3:
			calendar.add(Calendar.MONTH, num);
			break;
		}

		return calendar.getTime();
	}

	/**
	 * 两个日期相差的天数,只精确到天
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static Integer diffDays(Date date1, Date date2) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date1);
		long time1 = cal.getTimeInMillis();
		cal.setTime(date2);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);
		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * 两个日期时分秒
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static String getDistanceTime(Date date1, Date date2) {
		long day = 0;
		long hour = 0;
		long min = 0;
		long sec = 0;
		try {
			long time1 = date1.getTime();
			long time2 = date2.getTime();
			long diff;
			if (time1 < time2) {
				diff = time2 - time1;
			} else {
				diff = time1 - time2;
			}
			day = diff / (24 * 60 * 60 * 1000);
			hour = (diff / (60 * 60 * 1000) - day * 24);
			min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
			sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hour + ":" + min + ":" + sec;
	}

	/**
	 * 两个日期相差的天数,只精确到年
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static Integer diffYear(Date date1, Date date2) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date1);
		int time1 = cal.get(Calendar.YEAR);
		cal.setTime(date2);
		int time2 = cal.get(Calendar.YEAR);
		int between_year = time2 - time1;
		return between_year;
	}

	/**
	 * 两个日期相差的月数,只精确到月
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static Integer diffMonth(Date date1, Date date2) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date1);
		int time1 = cal.get(Calendar.MONTH);
		cal.setTime(date2);
		int time2 = cal.get(Calendar.MONTH);
		int between_year = time2 - time1;
		return between_year;
	}

	/**
	 * 返回一天的结束时间
	 * 
	 * @param d
	 * @return
	 */
	public static String getEndTimeOfDays(String d) {
		return d.trim() + " 23:59:59";
	}

	/**
	 * 去掉日期的时间部分
	 * 
	 * @param d
	 * @return
	 */
	public String formatDateStr(String d) {
		try {
			return shortsdf.format(longsdf.parse(d));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取过几分钟的时间
	 * 
	 * @param d
	 * @param minute
	 * @return
	 */
	public static Date addMinutes(Date d, int minute) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		calendar.add(Calendar.MINUTE, minute);
		return calendar.getTime();
	}

	/**
	 * 获得过几小时的时间
	 * 
	 * @param d
	 *            需要计算的时间类型
	 * @param hours
	 *            小时数
	 * @return
	 */
	public static Date addHours(Date d, int hours) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		calendar.add(Calendar.HOUR, hours);
		return calendar.getTime();
	}

	/**
	 * 获得过几天的时间
	 * 
	 * @param d
	 *            需要计算的时间类型
	 * @param days
	 *            天数
	 * @return
	 */
	public static Date addDays(Date d, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		calendar.add(Calendar.DATE, days);
		return calendar.getTime();
	}

	/**
	 * 获得前几小时的时间
	 * 
	 * @param d
	 *            需要计算的时间类型
	 * @param hours
	 *            小时数
	 * @return
	 */
	public static Date beforeHours(Date d, int hours) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		calendar.add(Calendar.HOUR, -hours);
		return calendar.getTime();
	}

	public static String formatDate(Date date, String format) {
		String newDate = "";
		if (date == null)
			return newDate;
		try {
			SimpleDateFormat df = new SimpleDateFormat(format);
			newDate = df.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return newDate;
	}

	public static Date formatDate(String strDate, String format) {
		Date date = null;
		if (strDate == null)
			return date;
		try {
			SimpleDateFormat df = new SimpleDateFormat(format);
			date = df.parse(strDate);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return date;
	}

	public static String getDate(String datetime) {
		if (datetime == null) {
			return null;
		}
		if (datetime.indexOf(" ") == -1) {
			return datetime;
		}
		return datetime.trim().split(" ")[0];
	}

	/**
	 * 获得HH:mm:ss格式的时间字符串
	 * 
	 * @param hours
	 * @param minutes
	 * @param seconds
	 * @return
	 */
	public static String getTimeStr(int hours, int minutes, int seconds) {
		StringBuffer timeStr = new StringBuffer();
		if (hours < 10) {
			timeStr.append("0" + hours);
		} else {
			timeStr.append(hours);
		}
		timeStr.append(":");
		if (minutes < 10) {
			timeStr.append("0" + minutes);
		} else {
			timeStr.append(minutes);
		}
		timeStr.append(":");
		if (seconds < 10) {
			timeStr.append("0" + seconds);
		} else {
			timeStr.append(seconds);
		}

		return timeStr.toString();
	}

	/**
	 * 获取时间为0点的某天的Date对象
	 * 
	 * @param offset
	 * @return
	 */
	public static Date getZeroOfDay(int offset) {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), (cal.get(Calendar.DAY_OF_MONTH) + offset), 0, 0, 0);
		return cal.getTime();
	}

	/**
	 * 获取时间为24点的某天的Date对象
	 * 
	 * @param offset
	 * @return
	 */
	public static Date get24OfDay(int offset) {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), (cal.get(Calendar.DAY_OF_MONTH) + offset), 23, 59, 59);
		return cal.getTime();
	}

	/**
	 * 供图片使用（缓存）
	 * 
	 * @return
	 */
	public static Long currentTimeMillis() {
		return System.currentTimeMillis();
	}

	public static Date formatDate(long time, String format) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		SimpleDateFormat df = new SimpleDateFormat(format);
		String newDate = df.format(calendar.getTime());
		Date date = null;
		try {
			date = df.parse(newDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 获取一天最早的时间点 00:00:00
	 * 
	 * @param date
	 * @return
	 */
	public static Date getEarliestOfDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		return c.getTime();
	}

	/**
	 * 获取一天最晚的时间点 23:59:59
	 * 
	 * @param date
	 * @return
	 */
	public static Date getLatestOfDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		return c.getTime();
	}

	/**
	 * 
	 * @param date
	 *            创建时间
	 * @param minute
	 *            倒数分钟
	 * @return
	 */
	public static long countDownTime(long date, int minute) {
		long countDownTime = 0;

		String dateUTC = DateUtil.stringDateUTC(date);
		Date dowmTime = DateUtil.addMinutes(stringDate(dateUTC), minute);
		Date now = new Date();
		if (dowmTime.after(now)) {
			countDownTime = (dowmTime.getTime() - now.getTime()) / 1000;
		}
		if (countDownTime < 0) {
			countDownTime = 0;
		}
		return countDownTime;
	}

	/**
	 * 相差秒数
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static long dateDiffSec(long time1, long time2) {
		long diff = time2 - time1;
		long diffSec = diff / 1000;
		return diffSec;
	}

	/**
	 * 获取订单提示时间
	 * 
	 * @author BennyTian
	 * @date 2015年3月25日 上午10:36:03
	 * @param startTime
	 * @return
	 */
	public static String getOrdersTimeStr(Date startTime) {
		int i = diffDays(formatDate(formatDate(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd"),
				formatDate(formatDate(startTime, "yyyy-MM-dd"), "yyyy-MM-dd"));
		StringBuffer sb = new StringBuffer();
		if (i == 0) {
			sb.append("今天(" + getDateOfWeek(startTime) + ")");
		} else if (i == 1) {
			sb.append("明天(" + getDateOfWeek(startTime) + ")");
		} else {
			sb.append(formatDate(startTime, "MM月dd日"));
		}
		sb.append(" " + formatDate(startTime, "HH:mm") + " 出发");
		return sb.toString();
	}

	/**
	 * 将日期转换成日常短语 <br/>
	 * 1:当天 : 今天 HH:mm <br/>
	 * 2:当年 : MM:dd <br/>
	 * 3:往年 : yyyy-MM-dd <br/>
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDateToPhrase(Date date) {
		Calendar now = Calendar.getInstance();
		Calendar target = Calendar.getInstance();
		target.setTime(date);
		if (target.get(Calendar.YEAR) != now.get(Calendar.YEAR)) {
			return formatDate(date, "yyyy-MM-dd");
		}
		if (target.get(Calendar.DAY_OF_YEAR) != now.get(Calendar.DAY_OF_YEAR)) {
			return formatDate(date, "MM-dd");
		}
		return formatDate(date, "今天 HH:mm");
	}

	/**
	 * 判断是否在两个时间点内
	 * 
	 * @author 朱厚飞
	 * @date 2015年3月13日 下午3:44:35
	 * @param time
	 *            小时 以","分隔,如23,6
	 * @return
	 */
	public static boolean between(String time) {
		boolean flag = false;
		Integer stime = null;
		Integer etime = null;
		if (time != null) {
			String[] times = time.split(",");
			if (times.length == 2 && isNumeric(times[0].trim()) && isNumeric(times[1].trim())) {
				stime = Integer.valueOf(times[0].trim());
				etime = Integer.valueOf(times[1].trim());
			}

		}
		if (stime != null && etime != null) {
			Calendar c = Calendar.getInstance();
			int hours = c.get(Calendar.HOUR_OF_DAY);
			if (stime >= etime) {
				if (etime >= 1) {
					if (stime <= hours && hours <= 23 || 0 <= hours && hours <= etime - 1) {
						flag = true;
					}
				} else {
					if (stime <= hours && hours <= 23 || 0 <= hours && hours <= 23) {
						flag = true;
					}
				}
			} else {
				if (etime >= 1) {
					if (stime <= hours && hours <= etime - 1) {
						flag = true;
					}
				} else {
					if (stime <= hours && hours <= 23) {
						flag = true;
					}
				}
			}

		}
		return flag;
	}

	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	public static Date format(Date date, String format) {
		return formatDate(date.getTime(), format);
	}

	/**
	 * 获取消息推送格式的日期 <b>03月01日 08:00（周日）</b>
	 * 
	 * @author BennyTian
	 * @date 2015年3月25日 上午10:26:32
	 * @param date
	 * @return
	 */
	public static String getPushMessageDate(Date date) {
		String shortDate = formatDate(date, PUSH_MESSAGE_FORMAT);
		return shortDate + "（" + str[getDayOfWeek(date) - 1] + "）";
	}

	/**
	 * 获取指定日期为星期几:1-7
	 * 
	 * @author BennyTian
	 * @date 2015年3月25日 上午10:26:53
	 * @param date
	 * @return
	 */
	public static Integer getDayOfWeek(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 获取星期几，中文的，周日~周六
	 * 
	 * @author BennyTian
	 * @date 2015年3月25日 上午10:32:57
	 * @param date
	 * @return
	 */
	public static String getDateOfWeek(Date date) {
		return str[getDayOfWeek(date) - 1];
	}

	/**
	 * "" + day1 + "天" + hour1 + "小时" + minute1 + "分钟";
	 * 
	 * @param minute
	 * @return
	 */
	public static String hourMinute(int minute) {
		minute = minute * 60;
		long day1 = minute / (24 * 3600);
		long hour1 = minute % (24 * 3600) / 3600;
		long minute1 = minute % 3600 / 60;
		// long second1 = minute % 60;
		StringBuffer sb = new StringBuffer("");
		if (day1 > 0) {
			sb.append(day1 + "天");
		}
		if (hour1 >= 0) {
			sb.append(hour1 + "小时");
		}
		if (minute1 >= 0) {
			sb.append(minute1 + "分钟");
		}

		return sb.toString();
	}

	public static Long getTime(String date, SimpleDateFormat sdf) {

		try {
			return sdf.parse(date).getTime() / 1000;
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * utc 转 Date
	 * 
	 * @param date
	 * @return
	 */
	public static Date utc2Date(Long date) {
		if (date == null) {
			return null;
		}
		return stringDate(DateUtil.stringDateUTC(date));
	}

	public static Date utc2Local(String utcTime, String utcTimePatten) {
		SimpleDateFormat utcFormater = new SimpleDateFormat(utcTimePatten);
		utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date gpsUTCDate = null;
		try {
			gpsUTCDate = utcFormater.parse(utcTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return gpsUTCDate;
	}

	public static void main(String[] args) {
		// String date = "2015-03-25";
		// System.out.println(getDateOfWeek(DateUtil.formatDate(date,
		// "yyyy-MM-dd")));
		//
		// System.out.println(hourMinute(4492*60));
		// System.out.println(getOrdersTimeStr(DateUtil.formatDate(date,
		// "yyyy-MM-dd")));
		// System.out.println(DateUtil.formatDate("20160325104302","yyyyMMddHHmmss"));

		// System.out.println(get24OfDay(0));

		// long timeout =
		// DateUtil.dateDiffSec(DateUtil.get24OfDay(-1).getTime(),
		// DateUtil.get24OfDay(0).getTime());

		// System.out.println(DateUtil.countDownTime(1460458245,30 ));
		// System.out.println(DateUtil.formatTime(DateUtil.string2Date("2016-04-19",
		// 1)));
		// System.out.println(utc2Date(GlobalTime.globalTimeUtc()));
		// Date beforeDate = DateUtil.beforeHours(new Date(), 72);
		// String startTime = DateUtil.formatDate(beforeDate, "yyyy-MM-dd")+"
		// 00:00:00";
		// String endTime = DateUtil.formatDate(beforeDate, "yyyy-MM-dd")+"
		// 23:59:59";
		//
		// String sql = "select UserID,SIM from `user` where channelId in
		// ('RJ010047','RJ010019','RJM10004','RJJ21008','RJ2016') and
		// VerifyLicense=1 and License_O is NULL and regdate BETWEEN
		// UNIX_TIMESTAMP('"+startTime+"') AND UNIX_TIMESTAMP('"+endTime+"')";

		// System.out.println(sql);
		String startTime = DateUtil.formatDate(new Date(), "yyyy-MM-dd") + " 00:00:00";
		String endTime = DateUtil.formatDate(DateUtil.addDays(new Date(), 2), "yyyy-MM-dd") + " 23:59:59";
		System.out.println(DateUtil.stringDate(startTime));
		System.out.println(DateUtil.stringDate(endTime));

		// System.out.println(DateUtil.formatTime(DateUtil.getZeroOfDay(-1)) );
		//
		// Date newDate = DateUtil.getBeforeAfterDate(new Date(), 15);
		//
		// System.out.println(DateUtil.formatTime(newDate));

	}

}
