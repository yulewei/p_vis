package edu.zjut.chart.plot;

import processing.core.PApplet;
import edu.zjut.common.data.time.TimePeriod;
import edu.zjut.common.data.time.TimeSeriesCollection;
import edu.zjut.common.data.time.TimeSeriesData;

public class LineRenderer extends TimeSeriesPlot {

	public LineRenderer(PApplet p, TimeSeriesCollection tsList) {
		super(p, tsList);
	}

	@Override
	protected void drawChart() {
		p.noFill();
		p.strokeWeight(2);

		for (int i = 0; i < series.seriesSize(); i++) {
			p.stroke(colorArr[i]);
			TimeSeriesData ts = series.get(i);
			plot(ts);
		}
	}

	protected void plot(TimeSeriesData ts) {
		int size = ts.size();

		int minIndex = 0;
		int maxIndex = size - 1;
		for (int row = 0; row < ts.size(); row++) {
			TimePeriod time = ts.getTime(row);
			if (time.compareTo(visualMin) >= 0) {
				minIndex = row;
				break;
			}
		}
		
		for (int row = ts.size() - 1; row >= 0; row--) {
			TimePeriod time = ts.getTime(row);
			if (time.compareTo(visualMax) <= 0) {
				maxIndex = row;
				break;
			}
		}

		float[] plotX = new float[size];
		float[] plotY = new float[size];
		for (int row = 0; row < ts.size(); row++) {
			TimePeriod time = ts.getTime(row);
			float value = ts.getValue(row);
			float x = PApplet.map(time.getSerialIndex(),
					visualMin.getSerialIndex(), visualMax.getSerialIndex(),
					plotX1, plotX2);
			float y = PApplet.map(value, axisValueMin, axisValueMax, plotY2,
					plotY1);
			plotX[row] = x;
			plotY[row] = y;
		}

		p.beginShape();
		for (int row = minIndex; row <= maxIndex; row++) {
			p.vertex(plotX[row], plotY[row]);
		}
		p.endShape();
	}
}
