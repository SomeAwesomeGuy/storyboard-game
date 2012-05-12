package utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateStringUtil {
	private static final SimpleDateFormat s_dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat s_createFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public static String getDatetime(final Date date) {
		return s_dbFormat.format(date);
	}
	
	public static String getDate(final Date date) {
		return s_createFormat.format(date);
	}
	
	public static String getTimeSince(final Date date) {
		final long diff = System.currentTimeMillis() - date.getTime();
		final long secs = diff / 1000;
		if(secs < 60) {
			return secs + "s";
		}
		final long min = secs / 60;
		if(min < 60) {
			return min + "m";
		}
		final long hrs = min / 60;
		if(hrs < 24) {
			final long rmin = min % 60;
			return hrs + "h " + rmin + "m";
		}
		final long days = hrs / 24;
		if(days < 7) {
			final long rhrs = hrs % 24;
			return days + "d " + rhrs + "h";
		}
		return days + "d";
	}
}
