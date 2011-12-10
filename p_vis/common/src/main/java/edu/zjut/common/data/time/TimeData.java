package edu.zjut.common.data.time;

import java.util.List;

public class TimeData {
	private List<TimeSeriesCollection> series;

	public TimeData(List<TimeSeriesCollection> series) {
		this.series = series;
	}

	public List<TimeSeriesCollection> getSeries() {
		return series;
	}
}
