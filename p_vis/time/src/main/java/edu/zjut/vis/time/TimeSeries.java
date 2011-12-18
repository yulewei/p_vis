package edu.zjut.vis.time;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.gicentre.utils.colour.ColourTable;

import edu.zjut.chart.plot.ChartType;
import edu.zjut.chart.plot.PlotFactory;
import edu.zjut.chart.plot.TimeSeriesPlot;
import edu.zjut.common.color.ColorSchemeListener;
import edu.zjut.common.color.ColorSchemePicker;
import edu.zjut.common.color.Legend;
import edu.zjut.common.color.LegendActionListener;
import edu.zjut.common.data.DataSetForApps;
import edu.zjut.common.data.attr.SummaryType;
import edu.zjut.common.data.time.TimeData;
import edu.zjut.common.data.time.TimeSeriesCollection;
import edu.zjut.common.data.time.TimeType;
import edu.zjut.common.event.DataSetEvent;
import edu.zjut.common.event.DataSetListener;

public class TimeSeries extends JPanel implements DataSetListener {

	private DataSetForApps dataSet;
	private TimeData timeData;

	TimeSeriesCollection overviewSeries;
	List<TimeSeriesCollection> detailSeriesList;

	private JPanel plotPanel;
	private PTimeSeries pTimeSeries;
	private ColorSchemePicker schemePicker;
	private Legend legend;

	private JToolBar jToolBar;
	private JToggleButton sideBarTglbtn;

	private JToggleButton datebtn;
	private JToggleButton monthbtn;
	private JToggleButton yearbtn;
	private JToggleButton allbtn;
	private JToggleButton gridTgbtn;

	private JSplitPane jSplitPane;
	private TimeCtrlPanel ctrlPanel;

	public TimeSeries() {
		detailSeriesList = new ArrayList<TimeSeriesCollection>();

		this.setLayout(new BorderLayout());

		initToolbar();
		this.add(jToolBar, BorderLayout.NORTH);

		jSplitPane = new JSplitPane();
		jSplitPane.setOneTouchExpandable(true);
		this.add(jSplitPane, BorderLayout.CENTER);

		plotPanel = new JPanel();
		plotPanel.setLayout(new BorderLayout());

		jSplitPane.add(plotPanel, JSplitPane.RIGHT);
		
		pTimeSeries = new PTimeSeries();
		pTimeSeries.setLayout(new BorderLayout());
		pTimeSeries.setPreferredSize(new Dimension(800, 600));
		pTimeSeries.init();
		plotPanel.add(pTimeSeries, BorderLayout.CENTER);

		schemePicker = new ColorSchemePicker();
		schemePicker.addPickerListener(new ColorSchemeListener() {
			public void colorChosen(ColourTable cTable) {
				legend.setColorTable(cTable);
				repaint();
			}
		});

//		pTimeSeries.add(schemePicker, BorderLayout.CENTER);

		legend = new Legend();
		legend.addLegendActionListener(new LegendActionListener() {
			@Override
			public void actionPerformed(boolean isActive) {
				schemePicker.setRefColorTable(legend.getColorTable());
				schemePicker.setLocationRelativeTo(TimeSeries.this);
				schemePicker.setVisible(isActive);
//				schemePicker.requestFocus();
			}
		});
		plotPanel.add(legend, BorderLayout.SOUTH);

		ctrlPanel = new TimeCtrlPanel(this, pTimeSeries);
		jSplitPane.add(ctrlPanel, JSplitPane.LEFT);

		jSplitPane.setDividerLocation(180);

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
					jSplitPane.setDividerLocation(180);
					jSplitPane.setDividerSize(dividerSize);
					ctrlPanel.setVisible(true);
				}
			}
		});

		jToolBar.add(sideBarTglbtn);

		JSeparator separator1 = new JSeparator(SwingConstants.VERTICAL);
		jToolBar.add(separator1);

		datebtn = new JToggleButton("Date");
		datebtn.setSelected(true);
		datebtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (datebtn.isSelected()) {
					reBuildTimeSeries(TimeType.DATE);
					pTimeSeries.redraw();
				}
			}
		});

		monthbtn = new JToggleButton("Month");
		monthbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (monthbtn.isSelected()) {
					reBuildTimeSeries(TimeType.MONTH);
					pTimeSeries.redraw();
				}
			}
		});

		yearbtn = new JToggleButton("Year");
		yearbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (yearbtn.isSelected()) {
					reBuildTimeSeries(TimeType.YEAR);
					pTimeSeries.redraw();
				}
			}
		});

		allbtn = new JToggleButton("All");
		allbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (allbtn.isSelected()) {
					pTimeSeries.setOverviewRange(0,
							overviewSeries.getTimeRange() - 1);
					pTimeSeries.redraw();
				}
			}
		});

		jToolBar.add(datebtn);
		jToolBar.add(monthbtn);
		jToolBar.add(yearbtn);
		jToolBar.add(allbtn);

		ButtonGroup btnGrupIcon = new ButtonGroup();
		btnGrupIcon.add(datebtn);
		btnGrupIcon.add(monthbtn);
		btnGrupIcon.add(yearbtn);
		btnGrupIcon.add(allbtn);

		JSeparator separator2 = new JSeparator(SwingConstants.VERTICAL);
		jToolBar.add(separator2);

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
		List<ChartType> types = new ArrayList<>();
		for (TimeSeriesCollection timeSeries : series) {
			timeSeries.buildTimeSeries();
			SummaryType summaryType = timeSeries.getSummaryType();
			if (summaryType == SummaryType.SUM)
				types.add(ChartType.AREA_STACKED);
			else
				types.add(ChartType.LINE);
		}

		overviewSeries = series.get(0);
		detailSeriesList = series.subList(1, series.size());

		setPlot(series, types);
		ctrlPanel.setState(series, types);
	}

	public void reBuildTimeSeries(TimeType timeType) {
		TimeSeriesPlot oldOPlot = pTimeSeries.getOverviewPlot();
		TimeSeriesCollection oseries = oldOPlot.getSeries();
		oseries.setTimeType(timeType);
		oseries.buildTimeSeries();

		ChartType otype = PlotFactory.parseType(oldOPlot);
		TimeSeriesPlot newOPlot = PlotFactory.create(otype, pTimeSeries,
				oseries);
		pTimeSeries.setOverviewPlot(newOPlot);

		List<TimeSeriesPlot> oldDPlots = pTimeSeries.getDetailPlots();
		List<TimeSeriesPlot> newDPlots = new ArrayList<TimeSeriesPlot>();
		for (TimeSeriesPlot plot : oldDPlots) {
			TimeSeriesCollection series = plot.getSeries();
			series.setTimeType(timeType);
			series.buildTimeSeries();

			ChartType type = PlotFactory.parseType(plot);
			TimeSeriesPlot newDPlot = PlotFactory.create(type, pTimeSeries,
					series);

			newDPlots.add(newDPlot);
		}

		pTimeSeries.setDetailPlots(newDPlots);
	}

	public void setPlot(List<TimeSeriesCollection> series, List<ChartType> types) {
		List<TimeSeriesPlot> plots = new ArrayList<TimeSeriesPlot>();
		for (int i = 0; i < series.size(); i++) {
			TimeSeriesPlot plot = PlotFactory.create(types.get(i), pTimeSeries,
					series.get(i));
			plots.add(plot);
		}

		pTimeSeries.setSeries(plots);

		ColourTable cTable = ColourTable
				.getPresetColourTable(ColourTable.SET3_8);
		legend.setData(series.get(0).getNames(), cTable);
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
