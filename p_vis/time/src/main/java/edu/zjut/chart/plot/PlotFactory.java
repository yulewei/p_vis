package edu.zjut.chart.plot;

import edu.zjut.common.data.time.TimeSeriesCollection;
import processing.core.PApplet;

public class PlotFactory {

	public static TimeSeriesPlot create(ChartType type, PApplet p,
			TimeSeriesCollection timeSeries) {
		switch (type) {
		case POINT:
			return new PointRenderer(p, timeSeries);
		case LINE:
			return new LineRenderer(p, timeSeries);
		case BAR:
			return new BarRenderer(p, timeSeries);
		case AREA:
			return new AreaRenderer(p, timeSeries);
		case AREA_STACKED:
			return new AreaStackedRenderer(p, timeSeries);
		}

		return null;
	}
}
