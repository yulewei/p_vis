package edu.zjut.common.data.time;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimePeriod implements Comparable<TimePeriod> {

	private GregorianCalendar calendar;
	private int date;
	private int month;
	private int year;

	public TimePeriod(int year) {
		this(year, 1, 1);
	}

	public TimePeriod(int year, int month) {
		this(year, month, 1);
	}

	public TimePeriod(int year, int month, int date) {
		this.year = year;
		this.month = month;
		this.date = date;

		calendar = new GregorianCalendar();
		calendar.set(this.year, this.month - 1, this.date, 0, 0, 0);
	}

	public TimePeriod(Date time) {
		calendar = new GregorianCalendar();
		calendar.setTime(time);

		this.month = calendar.get(Calendar.MONTH) + 1;
		this.year = calendar.get(Calendar.YEAR);
		this.date = calendar.get(Calendar.DATE);
	}

	public TimePeriod rollDate(TimeType type, int count) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(this.getTime());
		if (type == TimeType.YEAR)
			calendar.add(Calendar.YEAR, count);
		if (type == TimeType.MONTH)
			calendar.add(Calendar.MONTH, count);
		if (type == TimeType.DATE)
			calendar.add(Calendar.DATE, count);

		return new TimePeriod(calendar.getTime());
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public int getDate() {
		return date;
	}

	public Date getTime() {
		return calendar.getTime();
	}

	/**
	 * 天数
	 * 
	 * @return
	 */
	public long getSerialIndex() {
		// return this.year * 12 + this.month;
		long millis = calendar.getTime().getTime();
		return millis / (1000 * 60 * 60 * 24);
	}

	/**
	 * 
	 * @param type
	 * @param time
	 * @return
	 */
	public int subtract(TimeType type, TimePeriod time) {
		if (type == TimeType.YEAR)
			return this.year - time.year;
		if (type == TimeType.MONTH)
			return (this.year - time.year) * 12 + (this.month - time.month);

		// 默认日期之差
		return (int) (this.getSerialIndex() - time.getSerialIndex());
	}

	public String toString() {
		return this.year + " " + this.month + " " + this.date;
	}

	public int compareTo(TimePeriod time) {
		if (this.year != time.year)
			return this.year - time.year;
		if (this.month != time.month)
			return this.month - time.month;
		return this.date - time.date;

		// return calendar.getTime().compareTo(time.getTime());
	}

	public static void main(String[] args) {
		TimePeriod time1 = new TimePeriod(2002, 12, 1);
		TimePeriod time2 = new TimePeriod(2002, 11, 30);

		System.out.println(time1.subtract(TimeType.YEAR, time2));
		System.out.println(time1.subtract(TimeType.MONTH, time2));
		System.out.println(time1.subtract(TimeType.DATE, time2));

	}
}
