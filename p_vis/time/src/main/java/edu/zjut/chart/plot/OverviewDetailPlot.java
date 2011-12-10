package edu.zjut.chart.plot;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import edu.zjut.common.data.time.TimePeriod;

public class OverviewDetailPlot extends Plot {

	DraggableRect cover;
	TimeSeriesPlot overviewPlot;
	List<TimeSeriesPlot> detailPlotList;
	List<Integer> weightList;

	float overviewWeight = 0.6f;

	public OverviewDetailPlot(PApplet p) {
		super(p);

		this.detailPlotList = new ArrayList<TimeSeriesPlot>();
		this.weightList = new ArrayList<Integer>();
	}

	public void setOverviewPlot(TimeSeriesPlot subplot) {
		this.overviewPlot = subplot;

		int range = overviewPlot.getSeries().getAllTimes().size();
		this.cover = new DraggableRect(p, 0, range - 1);
	}

	/**
	 * 添加多个detailPlot, 大小(高度)按权重计算
	 * 
	 * @param subplot
	 * @param weight
	 */
	public void addDetailPlot(TimeSeriesPlot subplot, int weight) {
		detailPlotList.add(subplot);
		weightList.add(weight);
	}

	public void clearDetailPlot() {
		detailPlotList.clear();
		weightList.clear();
	}

	/**
	 * 颜色控制
	 */
	public void setColors(int[] colorArr) {
		overviewPlot.setColors(colorArr);
		for (TimeSeriesPlot plot : detailPlotList)
			plot.setColors(colorArr);
	}

	public void draw() {
		p.pushStyle();

		float sum = overviewWeight;
		for (int w : weightList) {
			sum += w;
		}

		float oh = height * (overviewWeight / sum);

		overviewPlot.size(x, y + height - oh, width, oh);
		overviewPlot.draw();

		float[] rect = overviewPlot.getPlotRect();
		cover.rangeSize(rect[0], rect[2], rect[1], rect[3]);
		cover.draw();

		// 绘制多个detail plot
		TimePeriod minTime = overviewPlot.getSeries().getTime(
				cover.getLeftIndex());
		TimePeriod maxTime = overviewPlot.getSeries().getTime(
				cover.getRightIndex());

		float dy = y;
		for (int i = 0; i < detailPlotList.size(); i++) {
			TimeSeriesPlot detailPlot = detailPlotList.get(i);

			float dh = height * (weightList.get(i) / sum);
			detailPlot.size(x, dy, width, dh);
			dy += dh;

			detailPlot.setVisualRange(minTime, maxTime);
			detailPlot.draw();
		}

		p.popStyle();
	}
}
