package edu.zjut.common.data.time;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class TimeSeriesData {
	private String timeName;
	private String valueName;

	private SortedMap<TimePeriod, Float> data;

	/**
	 * 由最小时间和最大时间填充而成
	 */
	private List<TimePeriod> times;

	private List<Float> values;

	public TimeSeriesData(String timeName, String valueName) {
		this.timeName = timeName;
		this.valueName = valueName;

		data = new TreeMap<TimePeriod, Float>();
	}

	public void add(TimePeriod time, float value) {
		data.put(time, value);
	}

	/**
	 * 在最小时间和最大时间之间填充缺失数据
	 */
	public void fillTimeRange(TimePeriod timeMin, TimePeriod timeMax,
			TimeType type) {

		times = new ArrayList<TimePeriod>();
		values = new ArrayList<Float>();
		for (TimePeriod time = timeMin; time.compareTo(timeMax) <= 0; time = time
				.rollDate(type, 1)) {
			times.add(time);

			Float value = data.get(time);
			if (value == null) {
				data.put(time, null);
				values.add(0.0f); // TODO 用0.0填充缺省值???
			} else {
				values.add(value);
			}
		}
	}

	public int size() {
		return data.size();
	}

	public String getTimeName() {
		return timeName;
	}

	public String getValueName() {
		return valueName;
	}

	public TimePeriod getTimeMin() {
		return data.firstKey();
	}

	public TimePeriod getTimeMax() {
		return data.lastKey();
	}

	public TimePeriod getTime(int index) {
		return times.get(index);
	}

	public float getValue(int index) {
		return values.get(index);
	}

	public List<TimePeriod> getTimes() {
		return times;
	}

	public void setTimes(List<TimePeriod> times) {
		this.times = times;
	}

	public List<Float> getValues() {
		return values;
	}

	public void setValues(List<Float> values) {
		this.values = values;
	}
}
