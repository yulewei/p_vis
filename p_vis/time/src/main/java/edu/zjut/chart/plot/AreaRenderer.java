package edu.zjut.chart.plot;

import processing.core.PApplet;
import processing.core.PConstants;
import edu.zjut.common.data.time.TimePeriod;
import edu.zjut.common.data.time.TimeSeriesCollection;
import edu.zjut.common.data.time.TimeSeriesData;

public class AreaRenderer extends TimeSeriesPlot {

	public AreaRenderer(PApplet p, TimeSeriesCollection tsList) {
		super(p, tsList);
	}

	@Override
	protected void drawChart() {
		p.noStroke();

		for (int i = 0; i < series.seriesSize(); i++) {
			p.fill(colorArr[i]);
			p.beginShape();

			TimeSeriesData ts = series.get(i);
			TimePeriod min = visualMax, max = visualMin;
			for (int row = 0; row < ts.size(); row++) {
				TimePeriod time = ts.getTime(row);
				if (time.compareTo(visualMin) >= 0
						&& time.compareTo(visualMax) <= 0) {
					if (time.compareTo(min) < 0)
						min = time;
					if (time.compareTo(max) > 0)
						max = time;

					float value = ts.getValue(row);
					float x = PApplet.map(time.getSerialIndex(),
							visualMin.getSerialIndex(),
							visualMax.getSerialIndex(), plotX1, plotX2);
					float y = PApplet.map(value, axisValueMin, axisValueMax,
							plotY2, plotY1);
					p.vertex(x, y);
				}
			}

			// draw the lower-right and lower-left corners
			float x1 = PApplet.map(min.getSerialIndex(),
					visualMin.getSerialIndex(), visualMax.getSerialIndex(),
					plotX1, plotX2);
			float x2 = PApplet.map(max.getSerialIndex(),
					visualMin.getSerialIndex(), visualMax.getSerialIndex(),
					plotX1, plotX2);

			p.vertex(x2, plotY2);
			p.vertex(x1, plotY2);
			p.endShape(PConstants.CLOSE);
		}
	}
}
