package org.gicentre.data;

/**
 * All
 * 
 * @author Aidan Slingsby, giCentre
 * 
 */
public class AllFilter implements DataFilter {

	/**
	 * Constructor
	 * 
	 */
	public AllFilter() {
	}

	/**
	 * Test if ALL the filters match a particular record
	 * 
	 * @param record
	 *            Whether the record matches
	 */
	public boolean matches(Object[] record) {
		return true;
	}

	public String toString() {
		return "All";
	}

}
