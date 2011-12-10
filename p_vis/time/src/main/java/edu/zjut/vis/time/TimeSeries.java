package edu.zjut.vis.time;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.zjut.common.data.DataSetForApps;
import edu.zjut.common.data.time.TimeData;
import edu.zjut.common.data.time.TimeSeriesCollection;
import edu.zjut.common.event.DataSetEvent;
import edu.zjut.common.event.DataSetListener;
import edu.zjut.time.PTimeSeries;

public class TimeSeries extends JPanel implements DataSetListener {

	private DataSetForApps dataSet;
	private TimeData timeData;

	static String title = "hello";
	PTimeSeries pTimeSeries;

	public TimeSeries() {
		BorderLayout layout = new BorderLayout();
		this.setLayout(layout);

		pTimeSeries = new PTimeSeries();
		pTimeSeries.setPreferredSize(new Dimension(800, 600));

		TimeSeriesCollection overviewSeries = null;
		TimeSeriesCollection detailSeries = null;

		String infile = "hz_data/sale_data_by_date.csv";
		try {
			overviewSeries = DataLoader.loadDataSet(infile, 0, 1, 4, true);
			detailSeries = DataLoader.loadDataSet(infile, 0, 1, 4, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		overviewSeries.buildTimeSeries();
		detailSeries.buildTimeSeries();

//		pTimeSeries.setTitle(title);
//		pTimeSeries.setSeries(overviewSeries, detailSeries);

		pTimeSeries.init();
		this.add(pTimeSeries);
		this.setPreferredSize(new Dimension(800, 600));
	}

	@Override
	public void dataSetChanged(DataSetEvent e) {
		dataSet = e.getDataSetForApps();
		timeData = dataSet.getTimeData();

		List<TimeSeriesCollection> series = timeData.getSeries();

		TimeSeriesCollection timeSeries1 = series.get(0);
		TimeSeriesCollection timeSeries2 = series.get(1);
		
		timeSeries1.buildTimeSeries();
		timeSeries2.buildTimeSeries();

		pTimeSeries.setTitle(title);
		pTimeSeries.setSeries(timeSeries1, timeSeries2);
	}

	public static void main(String[] args) {
		TimeSeries time = new TimeSeries();
		JFrame jframe = new JFrame();
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		jframe.setLocation(20, 120);
		jframe.setSize(1200, 600);

		jframe.add(time);
		jframe.setVisible(true);
	}
}
