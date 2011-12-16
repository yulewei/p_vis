package edu.zjut.treemap.data;

import edu.zjut.common.data.attr.DataField;

/**
 * Enables filtering of records based on whether a record variable is equal to a
 * value
 * 
 * @author Aidan Slingsby, giCentre
 * 
 */
public class EqualsFilter implements DataFilter {

	DataField var;
	Object value;

	/**
	 * Constructor
	 * 
	 * @param var
	 *            The variables
	 * @param value
	 *            The value that the variable should match
	 */
	public EqualsFilter(DataField var, Object value) {
		this.var = var;
		this.value = value;
	}

	/**
	 * Test whether a record matches
	 * 
	 * @param record
	 *            The record to match
	 */
	public boolean matches(Object[] record) {
		// check if null
		if (record[var.getColIdx()] == null) {
			return value == null;
		}
		// check value if not
		else {
			return (record[var.getColIdx()].equals(value));
		}
	}

	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o instanceof EqualsFilter == false) {
			return false;
		} else {
			if (this.var.equals(((EqualsFilter) o).var)
					&& ((this.value == null && ((EqualsFilter) o).value == null) || (this.value != null
							&& ((EqualsFilter) o).value != null && this.value
								.equals(((EqualsFilter) o).value)))) {
				return true;
			} else {
				return false;
			}
		}
	}

	public String toString() {
		if (value == null) {
			return var.getName() + "=N/A";
		} else {
			return var.getName() + "=" + value;
		}
	}

}
