package org.gicentre.apps.hide;

// Layouts
public enum Layout {
	ONE_DIM_LEFT_RIGHT, ONE_DIM_TOP_BOTTOM, ONE_DIM_ORDERED_SQUARIFIED, ONE_DIM_STRIP, TWO_DIMENSIONAL, ABS_POSITION;

	public String toString() {
		if (this.equals(ONE_DIM_LEFT_RIGHT)) {
			return "LeftToRight";
		} else if (this.equals(ONE_DIM_TOP_BOTTOM)) {
			return "TopToBottom";
		} else if (this.equals(ONE_DIM_ORDERED_SQUARIFIED)) {
			return "OrderedSq";
		} else if (this.equals(ONE_DIM_STRIP)) {
			return "RowByRow";
		} else if (this.equals(TWO_DIMENSIONAL)) {
			return "2dFill";
		} else if (this.equals(ABS_POSITION)) {
			return "2dPlot";
		} else
			return null;
	}
}