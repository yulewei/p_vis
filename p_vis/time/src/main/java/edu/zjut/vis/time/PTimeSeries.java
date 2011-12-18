package edu.zjut.vis.time;

import java.util.ArrayList;
import java.util.List;

import org.gicentre.utils.colour.ColourTable;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import edu.zjut.chart.plot.TimeSeriesPlot;
import edu.zjut.common.data.time.TimePeriod;
import edu.zjut.common.data.time.TimeType;

public class PTimeSeries extends PApplet {

	String title;
	int[] colorArr;
	ColourTable cTable;

	int titleHeight = 50;
	private boolean isShowTitle = false;

	DraggableRect cover;
	TimeSeriesPlot overviewPlot;
	List<TimeSeriesPlot> detailPlotList;
	List<Integer> weightList;
	float overviewWeight = 0.6f;

	public PTimeSeries() {
		this.detailPlotList = new ArrayList<TimeSeriesPlot>();
		this.weightList = new ArrayList<Integer>();

		cTable = ColourTable.getPresetColourTable(ColourTable.SET3_8);
	}

	public void setSeries(List<TimeSeriesPlot> plots) {
		setOverviewPlot(plots.get(0));

		detailPlotList.clear();
		weightList.clear();

		setDetailPlots(plots.subList(1, plots.size()));
	}

	public void setOverviewPlot(TimeSeriesPlot subplot) {
		this.overviewPlot = subplot;
		initColor(cTable);

		int range = overviewPlot.getSeries().getTimeRange();
		this.cover = new DraggableRect(this, 0, range - 1);
	}

	public TimeSeriesPlot getOverviewPlot() {
		return overviewPlot;
	}

	public void setDetailPlots(List<TimeSeriesPlot> detailPlotList) {
		this.detailPlotList = detailPlotList;
		weightList.clear();
		for (int i = 0; i < detailPlotList.size(); i++) {
			weightList.add(1);
		}

		for (TimeSeriesPlot plot : detailPlotList)
			plot.setColors(colorArr);
	}

	public List<TimeSeriesPlot> getDetailPlots() {
		return detailPlotList;
	}

	public void setDetailPlot(int index, TimeSeriesPlot detailPlot) {
		detailPlot.setColors(colorArr);
		detailPlotList.set(index, detailPlot);
	}

	public void setOverviewRange(int left, int right) {
		cover.setLeftIndex(left);
		cover.setRightIndex(right);
	}

	public int[] getOverviewRange() {
		return new int[] { cover.getLeftIndex(), cover.getRightIndex() };
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

	public void removeDetailPlot(int index) {
		detailPlotList.remove(index);
		weightList.remove(index);
	}

	/**
	 * TODO legend如何改进, 颜色如何控制
	 * 
	 * @param series
	 */
	protected void initColor(ColourTable cTable) {
		this.cTable = cTable;
		int size = overviewPlot.getSeries().seriesSize();
		colorArr = new int[size];
		for (int i = 0; i < size; i++) {
			colorArr[i] = cTable.findColour(i + 1);
		}
		
		setColors(colorArr);
	}

	/**
	 * 颜色控制
	 */
	public void setColors(int[] colorArr) {
		overviewPlot.setColors(colorArr);
		for (TimeSeriesPlot plot : detailPlotList)
			plot.setColors(colorArr);
	}

	public void setDrawGrid(boolean isDrawGrid) {
		overviewPlot.setDrawGrid(isDrawGrid);
		for (TimeSeriesPlot plot : detailPlotList)
			plot.setDrawGrid(isDrawGrid);

	}

	public void setup() {
		PFont font = createFont("FFScala", 12);
		textFont(font);
		smooth();
		size(800, 600);

		frameRate(10);

		// noLoop();
	}

	public void draw() {
		if (overviewPlot == null)
			return;

		background(255);

		int titleHeight = isShowTitle ? this.titleHeight : 0;
		int gap = 5;
		int plotHeight = height - titleHeight - 2 * gap;

		// title
		if (isShowTitle) {
			fill(100);
			textAlign(PConstants.CENTER, PConstants.CENTER);
			textSize(18);
			text(title, width / 2, titleHeight / 2);
		}

		textSize(12);

//		setColors(colorArr);
		drawPlot(gap, titleHeight + gap, width - 2 * gap, plotHeight);
	}

	public void drawPlot(int x, int y, int width, int height) {
		float sum = overviewWeight;
		for (int w : weightList) {
			sum += w;
		}

		int oh = (int) (height * (overviewWeight / sum));

		int totalHeight = 0;
		List<Integer> heights = new ArrayList<>();
		for (int i = 0; i < weightList.size(); i++) {
			int dh = (int) (height * (weightList.get(i) / sum));
			totalHeight += dh;
			heights.add(dh);
		}

		overviewPlot.size(x, y + height - oh, width, oh);
		overviewPlot.draw();

		float[] rect = overviewPlot.getPlotRect();
		cover.rangeSize(rect[0], rect[2], rect[1], rect[3]);
		cover.draw();

		// 绘制多个detail plot
		TimePeriod baseTime = overviewPlot.getSeries().getTimeMin();
		TimeType type = overviewPlot.getSeries().getTimeType();
		TimePeriod minTime = baseTime.rollDate(type, cover.getLeftIndex());
		TimePeriod maxTime = baseTime.rollDate(type, cover.getRightIndex());

		int dy = y;
		dy = y + height - totalHeight - oh;
		for (int i = 0; i < detailPlotList.size(); i++) {
			TimeSeriesPlot detailPlot = detailPlotList.get(i);

			int dh = heights.get(i);
			detailPlot.size(x, dy, width, dh);
			dy += dh;

			detailPlot.setVisualRange(minTime, maxTime);
			detailPlot.draw();
		}
	}
}
