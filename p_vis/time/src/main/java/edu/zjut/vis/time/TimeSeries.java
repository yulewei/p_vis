package edu.zjut.vis.time;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

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

	private PTimeSeries pTimeSeries;

	private JToolBar jToolBar;
	private JToggleButton sideBarTglbtn;
	private JToggleButton allbtn;
	private JToggleButton gridTgbtn;

	private JSplitPane jSplitPane;
	private TimeCtrlPanel ctrlPanel;

	public TimeSeries() {
		detailSeriesList = new ArrayList<TimeSeriesCollection>();

		BorderLayout layout = new BorderLayout();
		this.setLayout(layout);

		initToolbar();
		this.add(jToolBar, BorderLayout.NORTH);

		jSplitPane = new JSplitPane();
		jSplitPane.setOneTouchExpandable(true);
		this.add(jSplitPane, BorderLayout.CENTER);

		pTimeSeries = new PTimeSeries();
		pTimeSeries.setPreferredSize(new Dimension(800, 600));

		pTimeSeries.init();

		jSplitPane.add(pTimeSeries, JSplitPane.RIGHT);

		ctrlPanel = new TimeCtrlPanel(this, pTimeSeries);
		jSplitPane.add(ctrlPanel, JSplitPane.LEFT);
		
		jSplitPane.setDividerLocation(150);

		this.setPreferredSize(new Dimension(800, 600));
	}

	private void initToolbar() {
		jToolBar = new JToolBar();

		// 侧边栏
		sideBarTglbtn = new JToggleButton();
		sideBarTglbtn.setIcon(new ImageIcon(getClass().getResource(
				"sidebar.png")));
		sideBarTglbtn.setSelected(true);
		sideBarTglbtn.addActionListener(new ActionListener() {
			private int dividerSize = -1;

			public void actionPerformed(ActionEvent evt) {
				if (dividerSize == -1) {
					dividerSize = jSplitPane.getDividerSize();
				}
				if (!sideBarTglbtn.isSelected()) {
					jSplitPane.setDividerLocation(0);
					jSplitPane.setDividerSize(0);
					ctrlPanel.setVisible(false);
				} else {
					jSplitPane.setDividerLocation(150);
					jSplitPane.setDividerSize(dividerSize);
					ctrlPanel.setVisible(true);
				}
			}
		});

		jToolBar.add(sideBarTglbtn);

		JSeparator separator1 = new JSeparator(SwingConstants.VERTICAL);
		jToolBar.add(separator1);

		allbtn = new JToggleButton("All");
		allbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (allbtn.isSelected()) {
					pTimeSeries.setOverviewRange(0,
							overviewSeries.getTimeRange() - 1);
				}
			}
		});

		jToolBar.add(allbtn);

		// 辅助显示, 网格/中心点等
		gridTgbtn = new JToggleButton();
		gridTgbtn.setSelected(true);
		gridTgbtn.setIcon(new ImageIcon(getClass().getResource("grid.png")));
		gridTgbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				pTimeSeries.setDrawGrid(gridTgbtn.isSelected());
				pTimeSeries.redraw();
			}
		});

		jToolBar.add(gridTgbtn);
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

		overviewSeries = timeSeries1;
		detailSeriesList.clear();
		detailSeriesList.add(timeSeries2);

		TimeSeriesPlot overviewPlot = PlotFactory.create(ChartType.LINE,
				pTimeSeries, timeSeries1);
		TimeSeriesPlot detailPlot = PlotFactory.create(ChartType.AREA_STACKED,
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
