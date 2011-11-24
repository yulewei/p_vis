package edu.zjut.chart.plot;

import java.util.List;

import edu.zjut.common.data.time.TimePeriod;
import edu.zjut.common.data.time.TimeSeriesCollection;
import edu.zjut.common.data.time.TimeSeriesData;
import processing.core.PApplet;

public class TimeSeriesCurve extends TimeSeriesPlot {

	public TimeSeriesCurve(PApplet p, TimeSeriesCollection tsList) {
		super(p, tsList);
	}

	@Override
	protected void drawChart() {
		p.noFill();
		p.strokeWeight(2);
		for (int i = 0; i < series.size(); i++) {
			p.stroke(colorArr[i]);
			p.beginShape();
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
					p.curveVertex(x, y);
					// double the curve points for the start and stop
					if ((time.compareTo(visualMin) == 0)
							|| (time.compareTo(visualMax) == 0)) {
						p.curveVertex(x, y);
					}
				}
			}
			p.endShape();
		}
	}
}
