package edu.zjut.time;

import java.util.ArrayList;
import java.util.List;

import org.gicentre.utils.colour.ColourTable;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import edu.zjut.chart.plot.OverviewDetailPlot;
import edu.zjut.chart.plot.TimeSeriesAreaStacked;
import edu.zjut.chart.plot.TimeSeriesLine;
import edu.zjut.chart.plot.TimeSeriesPlot;
import edu.zjut.color.Legend;
import edu.zjut.color.LegendListener;
import edu.zjut.common.data.TimeSeriesCollection;

public class PTimeSeries extends PApplet implements LegendListener {

	String title;

	TimeSeriesCollection overviewSeries;
	List<TimeSeriesCollection> detailSeriesList;

	OverviewDetailPlot plot;

	Legend legend;
	int[] colorArr;
	ColourTable cTable;

	float titleHeight = 50;
	float legendHeight = 50;
	private boolean isShowTitle = false;
	private boolean isShowLegend = true;

	public PTimeSeries() {
		plot = new OverviewDetailPlot(this);

		detailSeriesList = new ArrayList<TimeSeriesCollection>();
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setSeries(TimeSeriesCollection overviewSeries,
			TimeSeriesCollection detailSeries) {
		this.overviewSeries = overviewSeries;
		this.detailSeriesList.add(detailSeries);

		TimeSeriesPlot overviewPlot = new TimeSeriesLine(this, overviewSeries);
		TimeSeriesPlot detailPlot = new TimeSeriesAreaStacked(this,
				detailSeries);
		TimeSeriesPlot detailPlot2 = new TimeSeriesLine(this, detailSeries);

		plot.setOverviewPlot(overviewPlot);
		plot.addDetailPlot(detailPlot, 1);
		plot.addDetailPlot(detailPlot2, 1);

		initColor(overviewSeries);
	}

	public void setOverviewSeries(TimeSeriesCollection series) {
		this.overviewSeries = series;

		TimeSeriesPlot overviewPlot = new TimeSeriesLine(this, series);
		plot.setOverviewPlot(overviewPlot);

		initColor(series);
	}

	/**
	 * TODO legend如何改进, 颜色如何控制
	 * 
	 * @param series
	 */
	protected void initColor(TimeSeriesCollection series) {
		cTable = ColourTable.getPresetColourTable(ColourTable.SET3_8);
		legend = new Legend(this, series.getNames(), cTable);
		legend.addLegendListener(this);
		
		int size = overviewSeries.size();
		colorArr = new int[size];
		for (int i = 0; i < size; i++) {
			colorArr[i] = cTable.findColour(i + 1);
		}
	}

	public void setDetailSeries(TimeSeriesCollection series) {
		detailSeriesList.add(series);

		TimeSeriesPlot detailPlot = new TimeSeriesAreaStacked(this, series);

		plot.addDetailPlot(detailPlot, 1);
	}

	public void setup() {
		PFont font = createFont("FFScala", 12);
		textFont(font);
		smooth();
	}

	public void draw() {
		background(255);

		float titleHeight = isShowTitle ? this.titleHeight : 0;
		float legendHeight = isShowLegend ? this.legendHeight : 0;
		float gap = 5;
		float plotHeight = height - titleHeight - legendHeight - 2 * gap;
		plotHeight = isShowLegend ? plotHeight - gap : plotHeight;

		// title
		if (isShowTitle) {
			fill(100);
			textAlign(PConstants.CENTER, PConstants.CENTER);
			textSize(18);
			text(title, width / 2, titleHeight / 2);
		}

		textSize(12);

		plot.setColors(colorArr);
		plot.size(gap, titleHeight + gap, width - 2 * gap, plotHeight);
		plot.draw();

		// legend
		if (isShowLegend) {
			legend.size(width / 2 - 150, height - legendHeight - gap, 300,
					legendHeight);
			legend.draw();
		}
	}

	@Override
	public void colorChanged() {
		cTable = legend.getColorTable();
		int numColours = cTable.getColourRules().size() - 1;

		int size = overviewSeries.size();
		colorArr = new int[size];
		for (int i = 0; i < size; i++) {
			colorArr[i] = cTable.findColour(i + 1);
		}
	}
}
