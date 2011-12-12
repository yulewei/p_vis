package edu.zjut.vis.time;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import edu.zjut.chart.plot.ChartType;
import edu.zjut.chart.plot.PlotFactory;
import edu.zjut.chart.plot.TimeSeriesPlot;
import edu.zjut.common.data.DataSetForApps;
import edu.zjut.common.data.time.TimeData;
import edu.zjut.common.data.time.TimeSeriesCollection;
import edu.zjut.common.event.DataSetEvent;
import edu.zjut.common.event.DataSetListener;

public class TimeSeries extends JPanel implements DataSetListener {

	private DataSetForApps dataSet;
	private TimeData timeData;

	TimeSeriesCollection overviewSeries;
	List<TimeSeriesCollection> detailSeriesList;

	private JToolBar jToolBar;
	private PTimeSeries pTimeSeries;

	public TimeSeries() {
		detailSeriesList = new ArrayList<TimeSeriesCollection>();

		BorderLayout layout = new BorderLayout();
		this.setLayout(layout);

		initToolbar();
		this.add(jToolBar, BorderLayout.NORTH);

		pTimeSeries = new PTimeSeries();
		pTimeSeries.setPreferredSize(new Dimension(800, 600));

		pTimeSeries.init();
		this.add(pTimeSeries, BorderLayout.CENTER);

		this.setPreferredSize(new Dimension(800, 600));
	}

	private void initToolbar() {
		jToolBar = new JToolBar();
		JToggleButton sideBarTglbtn = new JToggleButton();
		sideBarTglbtn.setIcon(new ImageIcon(getClass().getResource(
				"sidebar.png")));

		jToolBar.add(sideBarTglbtn);
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

		TimeSeriesPlot overviewPlot = PlotFactory.create(ChartType.LINE,
				pTimeSeries, timeSeries1);
		TimeSeriesPlot detailPlot = PlotFactory.create(ChartType.BAR,
				pTimeSeries, timeSeries2);
		pTimeSeries.setSeries(overviewPlot, detailPlot);
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
