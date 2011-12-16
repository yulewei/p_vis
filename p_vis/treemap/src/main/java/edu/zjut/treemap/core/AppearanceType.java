package edu.zjut.treemap.core;

// Appearance aspects
public enum AppearanceType {
	SHOW_LABELS, LABEL_SIZE, AUTOSIZE_LABELS, LABEL_OPACITY, ALLOW_VERTICAL_LABELS, PADDING, BORDER_WIDTH, INCLUDE_NULLS;

	public String toString() {
		switch (this) {
		case PADDING:
			return "Padding";
		case LABEL_SIZE:
			return "Label size";
		case SHOW_LABELS:
			return "Show labels";
		case ALLOW_VERTICAL_LABELS:
			return "Allow vertical labels";
		case INCLUDE_NULLS:
			return "Include nodata";
		case LABEL_OPACITY:
			return "Label opacity";
		case AUTOSIZE_LABELS:
			return "Autosize text";
		case BORDER_WIDTH:
			return "Border width";
		}
		return null;
	}

	/**
	 * Returns true if min/max is 0 and 1 (thus should be displayed as a
	 * checkbox)
	 * 
	 * @return
	 */
	public boolean checkBox() {
		return (this.minValue() == 0 && this.maxValue() == 1);
	}

	/**
	 * Default values
	 * 
	 * @return
	 */
	public int defaultValue() {
		switch (this) {
		case PADDING:
			return 1;
		case ALLOW_VERTICAL_LABELS:
			return 0;
		case LABEL_SIZE:
			return 20;
		case SHOW_LABELS:
			return 1;
		case INCLUDE_NULLS:
			return 1;
		case LABEL_OPACITY:
			return 180;
		case AUTOSIZE_LABELS:
			return 1;
		case BORDER_WIDTH:
			return 1;
		}
		return 0;
	}

	/**
	 * Min values
	 * 
	 * @return
	 */
	public int minValue() {
		switch (this) {
		case PADDING:
			return 0;
		case LABEL_SIZE:
			return 6;
		case SHOW_LABELS:
			return 0;
		case ALLOW_VERTICAL_LABELS:
			return 0;
		case INCLUDE_NULLS:
			return 0;
		case LABEL_OPACITY:
			return 20;
		case AUTOSIZE_LABELS:
			return 0;
		case BORDER_WIDTH:
			return 0;
		}
		return 0;
	}

	/**
	 * Max values
	 * 
	 * @return
	 */
	public int maxValue() {
		switch (this) {
		case PADDING:
			return 10;
		case LABEL_SIZE:
			return 10;
		case SHOW_LABELS:
			return 1;
		case ALLOW_VERTICAL_LABELS:
			return 1;
		case INCLUDE_NULLS:
			return 1;
		case LABEL_OPACITY:
			return 255;
		case AUTOSIZE_LABELS:
			return 1;
		case BORDER_WIDTH:
			return 20;
		}
		return 0;
	}

	public int incrementSize() {
		switch (this) {
		case PADDING:
			return 1;
		case LABEL_SIZE:
			return 1;
		case SHOW_LABELS:
			return 1;
		case ALLOW_VERTICAL_LABELS:
			return 1;
		case INCLUDE_NULLS:
			return 1;
		case LABEL_OPACITY:
			return 10;
		case AUTOSIZE_LABELS:
			return 1;
		case BORDER_WIDTH:
			return 1;
		}
		return 0;
	}
}