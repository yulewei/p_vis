package edu.zjut.treemap.core;

// Layouts
public enum Layout {
	/**
	 * sliceAndDice, horizontal
	 */
	HZ,

	/**
	 * sliceAndDice, vertical
	 */
	VT,

	/**
	 * orderedSquarified
	 */
	OS,

	/**
	 * strip
	 */
	ST,

	/**
	 * spatial
	 */
	SP,

	/**
	 * 
	 */
	SA;

	public String toString() {
		switch (this) {
		case HZ:
			return "HZ";
		case VT:
			return "VT";
		case OS:
			return "OS";
		case ST:
			return "ST";
		case SP:
			return "SP";
		case SA:
			return "SA";
		}
		return null;
	}
}