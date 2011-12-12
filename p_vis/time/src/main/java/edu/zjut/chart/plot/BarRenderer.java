package edu.zjut.chart.plot;

import processing.core.PApplet;
import processing.core.PConstants;
import edu.zjut.common.data.time.TimePeriod;
import edu.zjut.common.data.time.TimeSeriesCollection;
import edu.zjut.common.data.time.TimeSeriesData;

public class BarRenderer extends TimeSeriesPlot {

	float barWidth = 10;

	public BarRenderer(PApplet p, TimeSeriesCollection tsList) {
		super(p, tsList);
	}

	@Override
	protected void drawChart() {
		p.noStroke();
		// stroke(0);
		p.rectMode(PConstants.CORNERS);

		int size = series.seriesSize();
		for (int i = 0; i < size; i++) {
			p.fill(colorArr[i]);

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

					x = x + (i - size / 2) * (barWidth + 2);
					p.rect(x - barWidth / 2, y, x + barWidth / 2, plotY2);
				}
			}
		}
	}

}
