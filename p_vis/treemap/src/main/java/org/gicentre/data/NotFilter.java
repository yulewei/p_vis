package org.gicentre.data;

import org.gicentre.data.Data.Record;

/** A filter to test if a record does NOT match another filter
 * 
 * @author Aidan Slingsby, giCentre
 *
 */
public class NotFilter implements DataFilter{

	private DataFilter dataFilter;
	
	/**Constuctor
	 * 
	 * @param dataFilter The datafilter to test non-matching
	 */
	public NotFilter(DataFilter dataFilter){
		this.dataFilter=dataFilter;
	}
	
	/**Test match
	 * @param record The record to test
	 * @return whether if does not match
	 */
	public boolean matches(Record record) {
		return !dataFilter.matches(record);
	}

}
