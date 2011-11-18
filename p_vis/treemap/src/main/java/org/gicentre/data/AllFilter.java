package org.gicentre.data;

import java.util.HashSet;
import java.util.Iterator;

import org.gicentre.data.Data.Record;
/**All
 * 
 * @author Aidan Slingsby, giCentre
 *
 */
public class AllFilter implements DataFilter{

	/**Constructor
	 * 
	 */
	public AllFilter(){
	}

	
	/** Test if ALL the filters match a particular record
	 * 
	 * @param record Whether the record matches
	 */
	public boolean matches(Record record) {
		return true;
	}
	
	public String toString(){
		return "All";
	}
	
}
