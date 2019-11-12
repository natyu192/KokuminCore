package me.nucha.core.utils;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.time.DurationFormatUtils;

public class TimeUtil {

	public static long diffTime(Date dateTo) {
		return dateTo.getTime() - System.currentTimeMillis();
	}

	public static long diffTime(Date dateFrom, Date dateTo) {
		return dateTo.getTime() - dateFrom.getTime();
	}

	public static String getDateString(long time, String date_format) {
		return getDateString(new Timestamp(time), date_format);
	}

	public static String getDateString(Date date, String date_format) {
		if (date.getTime() <= System.currentTimeMillis()) {
			return null;
		}
		String dateString = DurationFormatUtils.formatPeriod(System.currentTimeMillis(), date.getTime(), date_format);
		return dateString;
	}

}
