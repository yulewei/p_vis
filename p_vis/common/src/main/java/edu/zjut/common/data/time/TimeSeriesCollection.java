package edu.zjut.common.data.time;

import java.util.ArrayList;
import java.util.List;

import edu.zjut.common.data.attr.SummaryType;

public class TimeSeriesCollection {

	private TimeType timeType = TimeType.DATE;

	private String name;
	private SummaryType summaryType;
	private List<TimeSeriesData> seriesList;

	private TimePeriod timeMin;
	private TimePeriod timeMax;
	private float valueMin;
	private float valueMax;

	public TimeSeriesCollection(String name, SummaryType summaryType) {
		this.name = name;
		this.summaryType = summaryType;
		seriesList = new ArrayList<TimeSeriesData>();
	}

	public void addSeries(TimeSeriesData series) {
		seriesList.add(series);
	}

	public void removeSeries(TimeSeriesData series) {
		seriesList.remove(series);
	}

	/**
	 * 
	 */
	public void buildTimeSeries() {

		// 时间范围
		timeMin = null;
		timeMax = null;
		// 纵坐标范围
		valueMin = Float.MAX_VALUE;
		valueMax = Float.MIN_VALUE;
		for (TimeSeriesData series : seriesList) {
			
			// 根据当前的时间粒度, 重新计算
			series.build(timeType, summaryType);

			TimePeriod tmin = series.getTimeMin();
			TimePeriod tmax = series.getTimeMax();
			if (timeMin == null || timeMin.compareTo(tmin) > 0)
				timeMin = tmin;
			if (timeMax == null || timeMax.compareTo(tmax) < 0)
				timeMax = tmax;

			float vmin = series.getValueMin();
			float vmax = series.getValueMax();
			if (vmin < valueMin)
				valueMin = vmin;
			if (vmax > valueMax)
				valueMax = vmax;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SummaryType getSummaryType() {
		return summaryType;
	}

	public TimePeriod getTimeMin() {
		return timeMin;
	}

	public TimePeriod getTimeMax() {
		return timeMax;
	}

	public float getValueMin() {
		return valueMin;
	}

	public float getValueMax() {
		return valueMax;
	}

	public TimeSeriesData get(int i) {
		return seriesList.get(i);
	}

	public int seriesSize() {
		return seriesList.size();
	}

	public int getTimeRange() {
		return timeMax.subtract(timeType, timeMin) + 1;
	}

	public void setTimeType(TimeType timeType) {
		this.timeType = timeType;
	}

	public TimeType getTimeType() {
		return timeType;
	}

	public String[] getNames() {
		String[] names = new String[seriesList.size()];
		for (int i = 0; i < seriesList.size(); i++) {
			names[i] = seriesList.get(i).getValueName();
		}

		return names;
	}

	public String toString() {
		return name;
	}
}
