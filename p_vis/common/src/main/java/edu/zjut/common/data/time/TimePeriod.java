package edu.zjut.common.data.time;

import java.util.Calendar;
import java.util.Date;

public class TimePeriod implements Comparable<TimePeriod> {

	private int month;
	private int year;

	public TimePeriod(int year) {
		this.year = year;
		this.month = 1;
	}

	public TimePeriod(int year, int month) {
		this.year = year;
		this.month = month;
	}

	public TimePeriod(Date time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(time);
		this.month = calendar.get(Calendar.MONTH) + 1;
		this.year = calendar.get(Calendar.YEAR);
	}

	public TimePeriod rollDate(TimeType type, int count) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(this.year, this.month - 1, 1, 0, 0, 0);

		if (type == TimeType.YEAR)
			calendar.add(Calendar.YEAR, count);
		if (type == TimeType.MONTH)
			calendar.add(Calendar.MONTH, count);

		return new TimePeriod(calendar.getTime());
	}

	public int getYear() {
		return this.year;
	}

	public int getMonth() {
		return this.month;
	}

	public Date getTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(this.year, this.month - 1, 1, 0, 0, 0);
		return calendar.getTime();
	}

	public long getSerialIndex() {
		return this.year * 12L + this.month;
	}

	public String toString() {
		return this.year + " " + this.month;
	}

	public int subtract(TimePeriod time) {
		return (this.year - time.year) * 12 + this.month - time.month;
	}

	public int compareTo(TimePeriod time) {
		if (this.year != time.year)
			return this.year - time.year;

		return this.month - time.month;
	}
}
