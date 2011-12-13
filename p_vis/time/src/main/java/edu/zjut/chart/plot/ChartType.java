package edu.zjut.chart.plot;

public enum ChartType {
	POINT, LINE, CURVE, BAR, AREA, AREA_STACKED;

	public String toString() {
		switch (this) {
		case POINT:
			return "Point";
		case LINE:
			return "Line";
		case CURVE:
			return "Curve";
		case BAR:
			return "Bar";
		case AREA:
			return "Area";
		case AREA_STACKED:
			return "Area Stacked";
		}
		return null;
	}
}
