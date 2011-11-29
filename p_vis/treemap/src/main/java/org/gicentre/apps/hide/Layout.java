package org.gicentre.apps.hide;

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
		if (this.equals(HZ)) {
			return "HZ";
		} else if (this.equals(VT)) {
			return "VT";
		} else if (this.equals(OS)) {
			return "OS";
		} else if (this.equals(ST)) {
			return "ST";
		} else if (this.equals(SP)) {
			return "SP";
		} else if (this.equals(SA)) {
			return "SA";
		} else
			return null;
	}
}