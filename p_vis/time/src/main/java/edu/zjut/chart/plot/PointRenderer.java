package edu.zjut.chart.plot;

import processing.core.PApplet;
import edu.zjut.common.data.time.TimePeriod;
import edu.zjut.common.data.time.TimeSeriesCollection;
import edu.zjut.common.data.time.TimeSeriesData;

public class PointRenderer extends TimeSeriesPlot {

	public PointRenderer(PApplet p, TimeSeriesCollection tsList) {
		super(p, tsList);
	}

	@Override
	protected void drawChart() {
		p.strokeWeight(5);
		for (int i = 0; i < series.seriesSize(); i++) {
			p.stroke(colorArr[i]);
			TimeSeriesData ts = series.get(i);
			for (int row = 0; row < ts.size(); row++) {
				TimePeriod time = ts.getTime(row);
				if (time.compareTo(visualMin) >= 0
						&& time.compareTo(visualMax) <= 0) {
					float value = ts.getValue(row);
					float x = PApplet.map(time.getSerialIndex(),
							visualMin.getSerialIndex(),
							visualMax.getSerialIndex(), plotX1, plotX2);
					float y = PApplet.map(value, axisValueMin, axisValueMax, plotY2,
							plotY1);
					p.point(x, y);
				}
			}
		}
	}
}
