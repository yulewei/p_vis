package edu.zjut.chart.plot;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import processing.core.PApplet;
import processing.core.PConstants;
import edu.zjut.chart.axis.Axis.Orientation;
import edu.zjut.chart.axis.DateAxis;
import edu.zjut.chart.axis.NumberAxis;
import edu.zjut.common.data.TimePeriod;
import edu.zjut.common.data.TimeSeriesCollection;
import edu.zjut.common.data.TimeSeriesData;
import edu.zjut.common.data.TimeType;

public abstract class TimeSeriesPlot extends Plot {

	/**
	 * 每个时间序列赋予一个颜色
	 */
	protected int[] colorArr;

	protected TimeSeriesCollection series;

	float plotX1, plotY1;
	float plotX2, plotY2;
	float labelX, labelY;

	public final static DateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");
	public final static DateFormat MOUTH_FORMAT = new SimpleDateFormat(
			"yyyy.MM");

	protected DateFormat dateFormat;
	protected NumberFormat numberFormat = new DecimalFormat("0");

	/**
	 * 数值轴
	 */
	protected NumberAxis valueAxis;

	/**
	 * 时间轴
	 */
	protected DateAxis timeAxis;

	/**
	 * 时间范围
	 */
	TimePeriod timeMin, timeMax;

	/**
	 * 数值范围
	 */
	float valueMin, valueMax;

	/**
	 * 绘制可见时间范围
	 */
	TimePeriod visualMin, visualMax;

	/**
	 * 绘制可见数值范围
	 */
	float axisValueMin, axisValueMax;

	private boolean isIncludeZero = false;
	private boolean isShowLabel = false;
	private boolean isShowHighlight = true;

	public TimeSeriesPlot(PApplet p, TimeSeriesCollection series) {
		super(p);

		this.series = series;

		if (series.getTimeType() == TimeType.YEAR)
			dateFormat = YEAR_FORMAT;
		if (series.getTimeType() == TimeType.MONTH)
			dateFormat = MOUTH_FORMAT;

		calcRange();

		initAxis();
	}

	protected void calcRange() {
		// 横坐标范围
		timeMin = series.getTimeMin();
		timeMax = series.getTimeMax();

		// 纵坐标范围
		valueMin = series.getValueMin();
		valueMax = series.getValueMax();
	}

	protected void initAxis() {

		// 计算基数
		int r = (int) Math.log10(valueMax) - 1;
		int interval = (int) Math.pow(10, r);
		int intervalMinor = interval / 2;
		if (intervalMinor == 0)
			intervalMinor = 1;
		if (valueMax / interval > 20) {
			interval *= 5;
			intervalMinor = interval;
		}

		// TODO 坐标轴包含0		
		axisValueMin = 0;
		axisValueMax = PApplet.ceil(valueMax / interval) * interval;
		
		valueAxis = new NumberAxis(p);
		valueAxis.axisData(axisValueMin, axisValueMax);
		valueAxis.interval(interval);
		valueAxis.intervalMinor(intervalMinor);
		valueAxis.setOrientation(Orientation.VERTICAL);
		valueAxis.setNumberFormat(numberFormat);

		// 时间轴
		visualMin = timeMin;
		visualMax = timeMax;
		timeAxis = new DateAxis(p);
		timeAxis.axisData(visualMin, visualMax);
		timeAxis.interval(1);
		timeAxis.intervalMinor(1);
		timeAxis.setTimeType(series.getTimeType());
		timeAxis.setOrientation(Orientation.HORIZONTAL);
		timeAxis.setDateFormat(dateFormat);
	}

	public void size(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		float axisMin = p.textWidth(numberFormat.format(axisValueMin));
		float axisMax = p.textWidth(numberFormat.format(axisValueMax));
		float axisWidth = axisMax > axisMin ? axisMax : axisMin;
		float axisHeight = p.textAscent();

		if (isShowLabel) {
			axisWidth += 20;
			axisHeight += 20;
		}

		plotX1 = x + axisWidth + 8;
		plotY1 = y + 20;
		plotX2 = x + width - 20;
		plotY2 = y + height - axisHeight - 8;

		labelX = x + 20;
		labelY = y + height - 20;

		valueAxis.plotSize(plotX1, plotY1, plotX2, plotY2);
		timeAxis.plotSize(plotX1, plotY1, plotX2, plotY2);
	}

	public void setVisualRange(TimePeriod visualMin, TimePeriod visualMax) {
		this.visualMin = visualMin;
		this.visualMax = visualMax;
		timeAxis.axisData(visualMin, visualMax);
	}

	/**
	 * 每个时间序列赋予一个颜色
	 */
	public void setColors(int[] colorArr) {
		this.colorArr = colorArr;
	}

	public float[] getPlotRect() {
		return new float[] { plotX1, plotY1, plotX2, plotY2 };
	}

	public void draw() {
		p.pushStyle();

		// 灰色背景
		p.fill(224);
		p.rect(x, y, width, height);

		// 白色plot区域
		p.fill(255);
		p.rectMode(PConstants.CORNERS);
		p.noStroke();
		p.rect(plotX1, plotY1, plotX2, plotY2);

		if (isShowLabel)
			drawAxisLabels();

		timeAxis.draw();
		valueAxis.draw();

		drawChart();

		if (isShowHighlight)
			drawDataHighlight();

		p.popStyle();
	}

	protected abstract void drawChart();

	protected void drawAxisLabels() {
		p.fill(0);
		p.textSize(13);
		p.textLeading(15);

		p.textAlign(PConstants.CENTER, PConstants.CENTER);
		p.text(series.get(0).getValueName(), labelX, (plotY1 + plotY2) / 2);
		p.textAlign(PConstants.CENTER);
		p.text(series.get(0).getTimeName(), (plotX1 + plotX2) / 2, labelY);
	}

	/**
	 * 高亮点, 鼠标判断
	 */
	protected void drawDataHighlight() {

		boolean find = false;
		float mindis = 3;
		TimePeriod minyear = null;
		float minvalue = -1, minx = -1, miny = -1;

		for (int i = 0; i < series.size(); i++) {
			TimeSeriesData ts = series.get(i);
			List<TimePeriod> times = ts.getTimes();
			List<Float> values = ts.getValues();
			for (int row = 0; row < times.size(); row++) {
				TimePeriod time = times.get(row);
				if (time.compareTo(visualMin) >= 0
						&& time.compareTo(visualMax) <= 0) {
					float value = values.get(row);
					float x = PApplet.map(time.getSerialIndex(),
							visualMin.getSerialIndex(),
							visualMax.getSerialIndex(), plotX1, plotX2);
					float y = PApplet.map(value, axisValueMin, axisValueMax,
							plotY2, plotY1);

					float dis = PApplet.dist(p.mouseX, p.mouseY, x, y);
					if (dis < mindis) {
						find = true;
						mindis = dis;
						minyear = time;
						minvalue = value;
						minx = x;
						miny = y;
					}
				}
			}
		}

		if (find) {
			// 点
			p.ellipseMode(PConstants.CENTER);
			p.stroke(0);
			p.strokeWeight(1);
			p.fill(0xff5679C1);
			p.noFill();
			p.ellipse(minx, miny, 8, 8);

			// 绘制十字线
			p.stroke(128);
			p.strokeWeight(1);
			// p.line(x, plotY1, x, plotY2);
			// p.line(plotX1, y, plotX2, y);

			// 虚线
			for (float dy = plotY1; dy <= plotY2; dy += 4)
				p.line(minx, dy, minx, dy + 1);
			for (float dx = plotX1; dx <= plotX2; dx += 4)
				p.line(dx, miny, dx + 1, miny);

			// 文本
			p.fill(0);
			p.textSize(12);
			p.textAlign(PConstants.CENTER);
			p.text(numberFormat.format(minvalue) + " ("
					+ dateFormat.format(minyear.getTime()) + ")", minx,
					miny - 8);
			p.textAlign(PConstants.LEFT);
		}
	}

	public TimeSeriesCollection getSeries() {
		return series;
	}

	public void setSeries(TimeSeriesCollection series) {
		this.series = series;
	}

	public NumberAxis getValueAxis() {
		return valueAxis;
	}

	public void setValueAxis(NumberAxis dataAxis) {
		this.valueAxis = dataAxis;
	}

	public DateAxis getTimeAxis() {
		return timeAxis;
	}

	public void setTimeAxis(DateAxis timeAxis) {
		this.timeAxis = timeAxis;
	}
}
