package edu.zjut.vis.time;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.zjut.common.data.time.TimeSeriesCollection;
import edu.zjut.time.PTimeSeries;

public class TimeSeries extends JPanel {

	static String title = "hello";
	TimeSeriesCollection overviewSeries;
	TimeSeriesCollection detailSeries;
	PTimeSeries applet;

	public TimeSeries() {
		BorderLayout layout = new BorderLayout();
		this.setLayout(layout);

		applet = new PTimeSeries();
		applet.setPreferredSize(new Dimension(1000, 600));

		overviewSeries = DataLoader.loadHZDataset(4, true);
		detailSeries = DataLoader.loadHZDataset(4, true);

		overviewSeries.buildTimeSeries();
		detailSeries.buildTimeSeries();

		applet.setTitle(title);
		applet.setSeries(overviewSeries, detailSeries);

		applet.init();
		this.add(applet);
		this.setPreferredSize(new Dimension(1200, 600));
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
