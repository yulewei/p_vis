package edu.zjut.common.data.time;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;


public class TimeSeriesData {
	private String timeName;
	private String valueName;

	private SortedMap<TimePeriod, Float> data;

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
	 * 
	 */
	void build() {
		times = new ArrayList<TimePeriod>();
		values = new ArrayList<Float>();
		for (TimePeriod time : data.keySet()) {
			times.add(time);
			values.add(data.get(time));
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
