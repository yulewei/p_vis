package edu.zjut.chart.plot;

import java.util.List;

import edu.zjut.common.data.TimePeriod;
import edu.zjut.common.data.TimeSeriesCollection;
import edu.zjut.common.data.TimeSeriesData;
import processing.core.PApplet;
import processing.core.PConstants;

public class TimeSeriesBars extends TimeSeriesPlot {

	float barWidth = 10;

	public TimeSeriesBars(PApplet p, TimeSeriesCollection tsList) {
		super(p, tsList);
	}

	@Override
	protected void drawChart() {
		p.noStroke();
		// stroke(0);
		p.rectMode(PConstants.CORNERS);

		int size = series.size();
		for (int i = 0; i < size; i++) {
			p.fill(colorArr[i]);

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
					float y = PApplet.map(value, axisValueMin, axisValueMax, plotY2,
							plotY1);

					x = x + (i - size / 2) * (barWidth + 2);
					p.rect(x - barWidth / 2, y, x + barWidth / 2, plotY2);
				}
			}
		}
	}

}
