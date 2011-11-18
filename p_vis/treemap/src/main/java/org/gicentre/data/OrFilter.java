package org.gicentre.data;

import java.util.HashSet;
import java.util.Iterator;

import org.gicentre.data.Data.Record;

/**Or filter
 * 
 * Filters on whether a record matches any from a list of filters
 * 
 * @author Aidan Slingsby, giCentre
 *
 */
public class OrFilter implements DataFilter{
	HashSet<DataFilter> filters;

	/**Constructor
	 * 
	 * @param filters Filters for consider for OR
	 */
	public OrFilter(DataFilter... filters){
		this.filters=new HashSet<DataFilter>();
		for (DataFilter csvFilter : filters) {
			this.filters.add(csvFilter);
		}
	}

	/**Constructor
	 * 
	 * @param filters Filters for consider for OR
	 */
	public OrFilter(HashSet<DataFilter> filters){
		this.filters=filters;
	}

	/**Constructor
	 * Create an empty OR filter 
	 */
	public OrFilter(){
		this.filters=new HashSet<DataFilter>();
	}
	
	/**Add a filter
	 * 
	 * @param filter
	 */
	public void add(DataFilter filter){
		this.filters.add(filter);
	}
	
	/** Test whether a record matches
	 * @param record The record to test
	 */
	public boolean matches(Record record) {
		Iterator<DataFilter> it = filters.iterator();
		while (it.hasNext()){
			if (it.next().matches(record)){
				return true;
			}
		}
		return false;
	}
	
}
