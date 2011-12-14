package edu.zjut.common.data.time;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import edu.zjut.common.data.attr.SummaryType;

public class TimeSeriesData {
	private String timeName;
	private String valueName;

	/**
	 * 原始数据, 时间粒度为日期
	 */
	private SortedMap<TimePeriod, Float> data;

	private List<TimePeriod> times;
	private List<Float> values;

	private float valueMin;
	private float valueMax;

	public TimeSeriesData(String timeName, String valueName) {
		this.timeName = timeName;
		this.valueName = valueName;

		data = new TreeMap<TimePeriod, Float>();
	}

	public void add(TimePeriod time, float value) {
		data.put(time, value);
	}

	/**
	 * 根据当前的时间粒度, 重新计算
	 */
	public void build(TimeType timeType, SummaryType summaryType) {

		List<TimePeriod> originalTimes = new ArrayList<TimePeriod>();
		List<Float> originalValues = new ArrayList<Float>();

		originalTimes.addAll(data.keySet());
		for (TimePeriod time : originalTimes) {
			originalValues.add(data.get(time));
		}

		if (timeType == TimeType.DATE) {
			times = originalTimes;
			values = originalValues;
		} else {
			times = new ArrayList<TimePeriod>();
			values = new ArrayList<Float>();

			List<Float> valuesGroup = null;
			TimePeriod curTime = null;
			for (int i = 0; i < originalTimes.size(); i++) {
				TimePeriod time = null;
				if (timeType == TimeType.MONTH) {
					TimePeriod tmp = originalTimes.get(i);
					time = new TimePeriod(tmp.getYear(), tmp.getMonth());
				} else {
					TimePeriod tmp = originalTimes.get(i);
					time = new TimePeriod(tmp.getYear());
				}

				Float value = originalValues.get(i);
				if (curTime == null) {
					curTime = time;
					valuesGroup = new ArrayList<>();
					valuesGroup.add(value);
				} else if (curTime.compareTo(time) == 0) {
					valuesGroup.add(value);
				} else {
					float sum = (float) calcSummary(valuesGroup, summaryType);
					times.add(curTime);
					values.add(sum);

					curTime = time;
					valuesGroup = new ArrayList<>();
					valuesGroup.add(value);
				}
			}
		}

		// 纵坐标范围
		valueMin = Float.MAX_VALUE;
		valueMax = Float.MIN_VALUE;
		for (float value : values) {
			if (value < valueMin)
				valueMin = value;
			if (value > valueMax)
				valueMax = value;
		}
	}

	private double calcSummary(List<Float> values, SummaryType summaryType) {
		if (summaryType == SummaryType.SUM || summaryType == SummaryType.MEAN) {
			double sum = 0;
			int count = values.size();
			for (Float v : values) {
				sum += v;
			}

			if (summaryType == SummaryType.SUM)
				return sum;
			if (summaryType == SummaryType.MEAN)
				return sum / count;
		}

		return values.size();
	}

	public int size() {
		return times.size();
	}

	public String getTimeName() {
		return timeName;
	}

	public String getValueName() {
		return valueName;
	}

	public TimePeriod getTimeMin() {
		return times.get(0);
	}

	public TimePeriod getTimeMax() {
		return times.get(times.size() - 1);
	}

	public float getValueMin() {
		return valueMin;
	}

	public float getValueMax() {
		return valueMax;
	}

	public TimePeriod getTime(int index) {
		return times.get(index);
	}

	public Float getValue(int index) {
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
