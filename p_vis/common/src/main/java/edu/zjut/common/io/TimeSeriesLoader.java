package edu.zjut.common.io;

import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import edu.zjut.common.data.attr.SummaryType;
import edu.zjut.common.data.time.TimePeriod;
import edu.zjut.common.data.time.TimeSeriesCollection;
import edu.zjut.common.data.time.TimeSeriesData;
import edu.zjut.common.data.time.TimeType;

public class TimeSeriesLoader {

	static SimpleDateFormat format = new SimpleDateFormat("yyyy-M-d");

	static class Record implements Comparable<Record> {
		public Date date;
		public String group;
		public double value;

		public Record(Date date, String group, double value) {
			this.date = date;
			this.group = group;
			this.value = value;
		}

		@Override
		public int compareTo(Record o) {
			if (!group.equals(o.group)) {
				return group.compareTo(o.group);
			}

			if (!date.equals(o.date)) {
				return date.compareTo(o.date);
			}

			return (int) (value - o.value);
		}
	}

	/**
	 * 目前只支持两种summary类型
	 */
	private static double calcSummary(List<Record> records,
			SummaryType summaryType) {
		if (summaryType == SummaryType.SUM || summaryType == SummaryType.MEAN) {
			double sum = 0;
			int count = records.size();
			for (Record r : records) {
				sum += r.value;
			}

			if (summaryType == SummaryType.SUM)
				return sum;
			if (summaryType == SummaryType.MEAN)
				return sum / count;
		}

		return records.size();
	}

	/**
	 * 构建时间序列数据, 时间最小粒度为日期
	 * 
	 * @param records
	 * @return
	 */
	private static TimeSeriesData buildTimeSeries(List<Record> records,
			String curGroup, SummaryType summaryType) {
		TimeSeriesData timeSeries = new TimeSeriesData("", curGroup);

		// 汇总同一天的数据
		List<Record> recordsGroup = null;
		TimePeriod curDate = null;
		for (int i = 0; i < records.size(); i++) {
			Record record = records.get(i);
			TimePeriod yearDate = new TimePeriod(record.date);
			if (curDate == null) {
				curDate = yearDate;
				recordsGroup = new ArrayList<>();
				recordsGroup.add(record);
			} else if (curDate.compareTo(yearDate) == 0) {
				recordsGroup.add(record);
			} else {
				float value = (float) calcSummary(recordsGroup, summaryType);
				timeSeries.add(curDate, value);
				curDate = yearDate;
				recordsGroup = new ArrayList<>();
				recordsGroup.add(record);
			}
		}

		return timeSeries;
	}

	public static TimeSeriesCollection loadDataSet(String infile, String name,
			int dateCol, int groupCol, int valueCol, SummaryType summaryType)
			throws IOException, ParseException {

		CSVReader reader = new CSVReader(new FileReader(infile));
		List<String[]> list = reader.readAll();

		List<Record> records = new ArrayList<>();

		for (int i = 0; i < list.size(); i++) {
			String datestr = list.get(i)[dateCol];
			String group = list.get(i)[groupCol];
			String valuestr = list.get(i)[valueCol];
			Date date = format.parse(datestr);
			double value = Double.parseDouble(valuestr);
			records.add(new Record(date, group, value));
		}

		Collections.sort(records);

		List<TimeSeriesData> timeSeriesList = new ArrayList<>();
		List<Record> recordsGroup = null;
		String curGroup = null;
		for (int i = 0; i < records.size(); i++) {
			Record record = records.get(i);
			if (curGroup == null) {
				curGroup = record.group;
				recordsGroup = new ArrayList<>();
				recordsGroup.add(record);
			} else if (curGroup.equals(record.group)) {
				recordsGroup.add(record);
			} else {
				timeSeriesList.add(buildTimeSeries(recordsGroup, curGroup,
						summaryType));
				curGroup = record.group;
				recordsGroup = new ArrayList<>();
				recordsGroup.add(record);
			}
		}

		TimeSeriesCollection dataset = new TimeSeriesCollection(name,
				summaryType);
		for (TimeSeriesData timeSeries : timeSeriesList) {
			dataset.addSeries(timeSeries);
		}
		return dataset;
	}
}
