package edu.zjut.vis.time;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.TransferHandler;

import edu.zjut.chart.plot.ChartType;
import edu.zjut.chart.plot.PlotFactory;
import edu.zjut.chart.plot.TimeSeriesPlot;
import edu.zjut.common.ctrl.FieldComponent;
import edu.zjut.common.ctrl.FieldComponent.ColorEnum;
import edu.zjut.common.ctrl.FieldImporter;
import edu.zjut.common.data.time.TimeSeriesCollection;

public class TimeCtrlPanel extends JPanel {

	private TimeSeries timeSeries;
	private PTimeSeries pTimeSeries;

	private JPanel listPanel;
	private JPanel buttonPanel;

	private SpringLayout springLayout;
	private List<SeriesPanel> panelList;

	public TimeCtrlPanel(TimeSeries timeSeries, PTimeSeries pTimeSeries) {
		this.timeSeries = timeSeries;
		this.pTimeSeries = pTimeSeries;

		panelList = new ArrayList<>();
		this.setPreferredSize(new Dimension(300, 500));

		this.setLayout(new BorderLayout());
		listPanel = new JPanel();
		this.add(new JScrollPane(listPanel), BorderLayout.CENTER);
		buttonPanel = new JPanel();
		this.add(buttonPanel, BorderLayout.SOUTH);

		springLayout = new SpringLayout();
		listPanel.setLayout(springLayout);

		SeriesPanel panel1 = new SeriesPanel(0);
		springLayout.putConstraint(SpringLayout.NORTH, panel1, 10,
				SpringLayout.NORTH, listPanel);
		springLayout.putConstraint(SpringLayout.WEST, panel1, 3,
				SpringLayout.WEST, listPanel);
		springLayout.putConstraint(SpringLayout.EAST, panel1, -3,
				SpringLayout.EAST, listPanel);
		listPanel.add(panel1);

		panelList.add(panel1);

		JButton addButton = new JButton("+");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				addButtonActionPerformed(evt);
			}
		});

		JButton removeButton = new JButton("-");
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				removeButtonActionPerformed(evt);
			}
		});

		buttonPanel.add(removeButton);
		buttonPanel.add(addButton);
	}

	private void addButtonActionPerformed(ActionEvent evt) {
		SeriesPanel panel = addSeriesPanel();

		// 复制最后一个plot
		List<TimeSeriesPlot> plots = pTimeSeries.getDetailPlots();
		int size = plots.size();
		TimeSeriesPlot plot = plots.get(size - 1);
		pTimeSeries.addDetailPlot(plot, 1);

		panel.setTimeSeries(plot.getSeries());
		panel.setChartType(PlotFactory.parseType(plot));

		pTimeSeries.redraw();

		validate();
		repaint();
	}

	private void removeButtonActionPerformed(ActionEvent evt) {
		// 至少两个
		int len = panelList.size();
		if (len == 2)
			return;

		removeSeriesPanel();

		// 删除最后一个plot
		List<TimeSeriesPlot> plots = pTimeSeries.getDetailPlots();
		int size = plots.size();
		pTimeSeries.removeDetailPlot(size - 1);

		pTimeSeries.redraw();

		validate();
		repaint();
	}

	public void setState(List<TimeSeriesCollection> series,
			List<ChartType> types) {
		for (int i = 1; i < panelList.size(); i++) {
			listPanel.remove(panelList.get(i));
		}
		SeriesPanel panel = panelList.get(0);
		panel.setTimeSeries(series.get(0));
		panel.setChartType(types.get(0));

		panelList.clear();
		panelList.add(panel);

		for (int i = 1; i < series.size(); i++) {
			SeriesPanel seriesPanel = addSeriesPanel();
			seriesPanel.setTimeSeries(series.get(i));
			seriesPanel.setChartType(types.get(i));
		}
	}

	private SeriesPanel addSeriesPanel() {
		int size = panelList.size();
		SeriesPanel prePanel = panelList.get(size - 1);
		SeriesPanel seriesPanel = new SeriesPanel(size);
		springLayout.putConstraint(SpringLayout.NORTH, seriesPanel, 10,
				SpringLayout.SOUTH, prePanel);
		springLayout.putConstraint(SpringLayout.WEST, seriesPanel, 0,
				SpringLayout.WEST, prePanel);
		springLayout.putConstraint(SpringLayout.EAST, seriesPanel, 0,
				SpringLayout.EAST, prePanel);
		listPanel.add(seriesPanel);
		panelList.add(seriesPanel);

		return seriesPanel;
	}

	private SeriesPanel removeSeriesPanel() {
		int size = panelList.size();
		SeriesPanel seriesPanel = panelList.get(size - 1);
		listPanel.remove(seriesPanel);
		panelList.remove(size - 1);

		return seriesPanel;
	}

	public void chartTypeChanged(int index, ChartType type) {
		if (index == 0) {
			TimeSeriesPlot oldPlot = pTimeSeries.getOverviewPlot();
			TimeSeriesPlot plot = PlotFactory.create(type, pTimeSeries,
					oldPlot.getSeries());
			pTimeSeries.setOverviewPlot(plot);
		} else {
			index--;

			TimeSeriesPlot oldPlot = pTimeSeries.getDetailPlots().get(index);
			TimeSeriesPlot detailPlot = PlotFactory.create(type, pTimeSeries,
					oldPlot.getSeries());
			pTimeSeries.setDetailPlot(index, detailPlot);
		}

		pTimeSeries.redraw();
	}

	public void timeSeriesChanged(int index, TimeSeriesCollection series) {
		if (index == 0) {
			TimeSeriesPlot oldPlot = pTimeSeries.getOverviewPlot();
			ChartType type = PlotFactory.parseType(oldPlot);
			TimeSeriesPlot plot = PlotFactory.create(type, pTimeSeries, series);
			pTimeSeries.setOverviewPlot(plot);
		} else {
			index--;

			TimeSeriesPlot oldPlot = pTimeSeries.getDetailPlots().get(index);
			ChartType type = PlotFactory.parseType(oldPlot);

			TimeSeriesPlot detailPlot = PlotFactory.create(type, pTimeSeries,
					series);
			pTimeSeries.setDetailPlot(index, detailPlot);
		}

		repaint();
		pTimeSeries.redraw();
	}

	public class SeriesPanel extends JPanel {
		private int index;

		private SpringLayout springLayout;
		private JLabel seriesLbl;
		private JLabel typeLabel;
		private FieldComponent<TimeSeriesCollection> seriesComp;
		private JComboBox<ChartType> typeComboBox;
		private ComboBoxModel<ChartType> typeModel;

		public SeriesPanel(int index) {
			this.index = index;

			typeModel = new DefaultComboBoxModel<ChartType>(ChartType.values());

			this.setPreferredSize(new Dimension(200, 100));
			this.setBorder(BorderFactory.createTitledBorder(""));

			springLayout = new SpringLayout();
			setLayout(springLayout);

			seriesComp = new FieldComponent<TimeSeriesCollection>();
			seriesComp
					.setTransferHandler(new SeriesImporter(seriesComp, index));

			this.add(seriesComp);

			seriesLbl = new JLabel();
			this.add(seriesLbl);
			seriesLbl.setText("Series");

			typeComboBox = new JComboBox<ChartType>();
			this.add(typeComboBox);
			typeComboBox.setModel(typeModel);
			typeComboBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					ChartType type = (ChartType) typeComboBox.getSelectedItem();
					chartTypeChanged(SeriesPanel.this.index, type);
				}
			});

			typeLabel = new JLabel("Type");
			this.add(typeLabel);

			initLayout();
		}

		public void setTimeSeries(TimeSeriesCollection timeSeries) {
			seriesComp.setValue(timeSeries);
			seriesComp.setColor(ColorEnum.ORANGE);
		}

		public void setChartType(ChartType type) {
			typeComboBox.setSelectedItem(type);
		}

		private void initLayout() {
			springLayout.putConstraint(SpringLayout.NORTH, seriesComp, 2,
					SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, seriesComp, 40,
					SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.EAST, seriesComp, -2,
					SpringLayout.EAST, this);

			springLayout.putConstraint(SpringLayout.SOUTH, seriesLbl, -2,
					SpringLayout.SOUTH, seriesComp);
			springLayout.putConstraint(SpringLayout.WEST, seriesLbl, 2,
					SpringLayout.WEST, this);

			springLayout.putConstraint(SpringLayout.NORTH, typeComboBox, 8,
					SpringLayout.SOUTH, seriesComp);
			springLayout.putConstraint(SpringLayout.WEST, typeComboBox, 0,
					SpringLayout.WEST, seriesComp);
			springLayout.putConstraint(SpringLayout.EAST, typeComboBox, 0,
					SpringLayout.EAST, seriesComp);

			springLayout.putConstraint(SpringLayout.SOUTH, typeLabel, -2,
					SpringLayout.SOUTH, typeComboBox);
			springLayout.putConstraint(SpringLayout.WEST, typeLabel, 0,
					SpringLayout.WEST, seriesLbl);
		}
	}

	public class SeriesImporter extends FieldImporter<TimeSeriesCollection> {

		private int index;
		private FieldComponent<TimeSeriesCollection> seriesComp;

		public SeriesImporter(FieldComponent<TimeSeriesCollection> seriesComp,
				int index) {
			super(TimeSeriesCollection.class);
			this.seriesComp = seriesComp;
			this.index = index;
		}

		public boolean importData(TransferHandler.TransferSupport support) {
			getTransferData(support);

			List<TimeSeriesCollection> values = data.getValues();
			if (!values.isEmpty()) {
				TimeSeriesCollection series = values.get(0);

				seriesComp.setValue(series);
				seriesComp.setColor(ColorEnum.ORANGE);
				timeSeriesChanged(index, series);
			}

			return true;
		}
	}
}
