package edu.zjut.treemap.filter;

/**
 * Interface for DataFilters for querying/filtering records
 * 
 * @author Aidan Slingsby, giCentre
 * 
 */
public interface DataFilter {

	/**
	 * Returns whether a record matches a filer
	 * 
	 * @param record
	 * @return
	 */
	public boolean matches(Object[] record);

}
