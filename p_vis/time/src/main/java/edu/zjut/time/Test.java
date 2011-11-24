package edu.zjut.time;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;

import au.com.bytecode.opencsv.CSVReader;
import edu.zjut.common.data.time.TimePeriod;
import edu.zjut.common.data.time.TimeSeriesCollection;
import edu.zjut.common.data.time.TimeSeriesData;
import edu.zjut.common.data.time.TimeType;

public class Test {

	static String title = "hello";
	static TimeSeriesCollection overviewSeries;
	static TimeSeriesCollection detailSeries;
	static PTimeSeries applet;

	public static TimeSeriesData loadData(String infile, int tcol, int dcol) {

		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy");

			CSVReader reader = new CSVReader(new FileReader(infile), '\t');
			List<String[]> list = reader.readAll();
			String timeName = list.get(0)[tcol];
			String valueName = list.get(0)[dcol];
			TimeSeriesData td = new TimeSeriesData(timeName, valueName);

			for (int i = 1; i < list.size(); i++) {
				Date date = format.parse(list.get(i)[tcol]);
				Calendar c = Calendar.getInstance();
				c.setTime(date);

				float value = Float.parseFloat(list.get(i)[dcol]);
				td.add(new TimePeriod(c.get(Calendar.YEAR), 1), value);
			}

			return td;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void loadData() {
		overviewSeries = new TimeSeriesCollection(TimeType.YEAR);
		TimeSeriesData td = loadData("data/milk-tea-coffee.tsv", 0, 1);
		overviewSeries.addSeries(td);

		detailSeries = new TimeSeriesCollection(TimeType.YEAR);
		for (int i = 1; i <= 3; i++) {
			td = loadData("data/milk-tea-coffee.tsv", 0, i);
			detailSeries.addSeries(td);
		}
	}

	public static void loadHZData() {
		overviewSeries = DataLoader.loadHZDataset(4, true);
		detailSeries = DataLoader.loadHZDataset(4, true);
	}

	public static void main(String[] args) {

		applet = new PTimeSeries();
		applet.setPreferredSize(new Dimension(1000, 600));

		// loadData();

		loadHZData();

		overviewSeries.buildTimeSeries();
		detailSeries.buildTimeSeries();

		applet.setTitle(title);
		applet.setSeries(overviewSeries, detailSeries);

		JFrame jframe = new JFrame();
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setLocation(60, 120);
		// jframe.setSize(1200, 600);

		jframe.setLayout(new BorderLayout());
		jframe.add(applet);
		jframe.pack();

		applet.init();
		jframe.setVisible(true);
	}
}
