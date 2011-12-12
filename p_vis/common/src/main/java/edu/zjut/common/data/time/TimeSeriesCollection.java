package edu.zjut.common.data.time;

import java.util.ArrayList;
import java.util.List;

public class TimeSeriesCollection {

	private TimeType type;
	private List<TimeSeriesData> seriesList;

	private TimePeriod timeMin;
	private TimePeriod timeMax;
	private float valueMin;
	private float valueMax;

	public TimeSeriesCollection(TimeType type) {
		this.type = type;
		seriesList = new ArrayList<TimeSeriesData>();
	}

	public void addSeries(TimeSeriesData series) {
		seriesList.add(series);
	}

	public void removeSeries(TimeSeriesData series) {
		seriesList.remove(series);
	}

	/**
	 * 多个时间序列数据整合. 由于时间分布并不完全一致, 最小时间, 最大时间, 缺失值
	 */
	public void buildTimeSeries() {

		// 时间范围
		timeMin = null;
		timeMax = null;
		// 纵坐标范围
		valueMin = Float.MAX_VALUE;
		valueMax = Float.MIN_VALUE;
		for (TimeSeriesData series : seriesList) {
			series.build();

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
		return timeMax.subtract(timeMin) + 1;
	}

	public TimeType getTimeType() {
		return type;
	}

	public String[] getNames() {
		String[] names = new String[seriesList.size()];
		for (int i = 0; i < seriesList.size(); i++) {
			names[i] = seriesList.get(i).getValueName();
		}

		return names;
	}
}
