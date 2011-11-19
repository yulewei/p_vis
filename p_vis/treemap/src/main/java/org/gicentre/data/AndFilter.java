package org.gicentre.data;

import java.util.HashSet;
import java.util.Iterator;

/**
 * And logic for Filters
 * 
 * @author Aidan Slingsby, giCentre
 * 
 */
public class AndFilter implements DataFilter {
	// List of Filters
	HashSet<DataFilter> filters;

	/**
	 * Constructor
	 * 
	 * @param filters
	 *            The filters to test
	 */
	public AndFilter(DataFilter... filters) {
		this.filters = new HashSet<DataFilter>();
		for (DataFilter csvFilter : filters) {
			this.filters.add(csvFilter);
		}
	}

	/**
	 * Constructor
	 * 
	 * @param filters
	 *            The filters to test
	 */
	public AndFilter(HashSet<DataFilter> filters) {
		this.filters = filters;
	}

	/**
	 * Constructor
	 * 
	 * Creates an empty And filter
	 */
	public AndFilter() {
		this.filters = new HashSet<DataFilter>();
	}

	/**
	 * Add a filter
	 * 
	 * @param filter
	 */
	public void add(DataFilter filter) {
		this.filters.add(filter);
	}

	/**
	 * Test if ALL the filters match a particular record
	 * 
	 * @param record
	 *            Whether the record matches
	 */
	public boolean matches(Record record) {
		Iterator<DataFilter> it = filters.iterator();
		while (it.hasNext()) {
			if (!it.next().matches(record)) {
				return false;
			}
		}
		return true;
	}

}
