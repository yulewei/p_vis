package edu.zjut.chart.axis;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import processing.core.PApplet;
import processing.core.PConstants;
import edu.zjut.common.data.TimePeriod;
import edu.zjut.common.data.TimeType;

public class DateAxis extends Axis {

	private DateFormat yearFormat = new SimpleDateFormat("yyyy");
	private DateFormat mouthFormat = new SimpleDateFormat("yyyy.MM");
	private DateFormat dateFormat;

	TimePeriod timeMin, timeMax;

	private TimeType type = TimeType.YEAR;

	public DateAxis(PApplet p) {
		super(p);
	}

	public void setDateFormat(DateFormat formatter) {
		this.dateFormat = formatter;
	}

	public DateFormat getDateFormat() {
		return dateFormat;
	}

	public void axisData(TimePeriod timeMin, TimePeriod timeMax) {
		this.timeMin = timeMin;
		this.timeMax = timeMax;
	}

	public void setTimeType(TimeType type) {
		this.type = type;
	}

	@Override
	protected void adjustIntervalHor() {
		if (dateFormat == null) {
			if (type == TimeType.YEAR)
				dateFormat = yearFormat;
			if (type == TimeType.MONTH)
				dateFormat = mouthFormat;
		}

		// 最小间距10像素
		interMin = intervalMinor;
		while (true) {
			float w = PApplet.map(timeMin.rollDate(type, interMin)
					.getSerialIndex(), timeMin.getSerialIndex(), timeMax
					.getSerialIndex(), plotX1, plotX2)
					- plotX1;
			if (w < 10)
				interMin++;
			else
				break;
		}

		// 防止坐标文本相互覆盖
		inter = interval;
		float wmin = p.textWidth(dateFormat.format(timeMin.getTime()));
		float wmax = p.textWidth(dateFormat.format(timeMax.getTime()));
		wmax = wmax > wmin ? wmax : wmin;
		wmax += 3;
		for (int i = 1;; i++) {
			float w = PApplet.map(timeMin.rollDate(type, inter)
					.getSerialIndex(), timeMin.getSerialIndex(), timeMax
					.getSerialIndex(), plotX1, plotX2)
					- plotX1;
			if (w < wmax)
				inter = interMin * i;
			else
				break;
		}
	}

	/**
	 * 计算自适应坐标轴间距
	 */
	@Override
	protected void adjustIntervalVer() {
		if (dateFormat == null) {
			if (type == TimeType.YEAR)
				dateFormat = yearFormat;
			if (type == TimeType.MONTH)
				dateFormat = mouthFormat;
		}

		// 最小间距10像素
		interMin = intervalMinor;
		while (true) {
			float w = PApplet.map(timeMin.rollDate(type, interMin)
					.getSerialIndex(), timeMin.getSerialIndex(), timeMax
					.getSerialIndex(), plotX1, plotX2)
					- plotX1;
			if (w < 10)
				interMin++;
			else
				break;
		}

		// 防止坐标文本相互覆盖
		inter = interval;
		float wmin = p.textWidth(dateFormat.format(timeMin.getTime()));
		float wmax = p.textWidth(dateFormat.format(timeMax.getTime()));
		wmax = wmax > wmin ? wmax : wmin;
		wmax += 3;
		for (int i = 1;; i++) {
			float w = PApplet.map(timeMin.rollDate(type, inter)
					.getSerialIndex(), timeMin.getSerialIndex(), timeMax
					.getSerialIndex(), plotX1, plotX2)
					- plotX1;
			if (w < wmax)
				inter = interMin * i;
			else
				break;
		}
	}

	@Override
	protected void drawHor() {

		int count = 0;
		TimePeriod v = timeMin;
		while (v.compareTo(timeMax) <= 0) {
			float x = PApplet.map(v.getSerialIndex(), timeMin.getSerialIndex(),
					timeMax.getSerialIndex(), plotX1, plotX2);
			if (count % inter == 0) {
				// Draw major tick
				p.textAlign(PConstants.CENTER, PConstants.TOP);
				p.stroke(axisColor);
				p.text(dateFormat.format(v.getTime()), x, plotY2 + 6);
				p.line(x, plotY2 + 4, x, plotY2);

			} else {
				// Draw minor tick
				p.stroke(128);
				p.line(x, plotY2 + 2, x, plotY2);
			}

			v = v.rollDate(type, interMin);
			count += interMin;
		}
	}

	@Override
	protected void drawVer() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void drawGrid() {
		p.stroke(gridColor);
		TimePeriod v = timeMin.rollDate(type, interMin);
		while (v.compareTo(timeMax) <= 0) {
			if (orient == Orientation.HORIZONTAL) {
				float x = PApplet.map(v.getSerialIndex(),
						timeMin.getSerialIndex(), timeMax.getSerialIndex(),
						plotX1, plotX2);
				p.line(x, plotY1, x, plotY2);
			}
			if (orient == Orientation.VERTICAL) {
				float y = PApplet.map(v.getSerialIndex(),
						timeMin.getSerialIndex(), timeMax.getSerialIndex(),
						plotY2, plotY1);
				p.line(plotX1, y, plotX2, y);
			}
			v = v.rollDate(type, interMin);
		}
	}
}
