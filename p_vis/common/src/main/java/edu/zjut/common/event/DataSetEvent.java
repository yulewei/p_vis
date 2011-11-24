/* Licensed under LGPL v. 2.1 or any later version;
 see GNU LGPL for details.
 Original Author: Frank Hardisty */

package edu.zjut.common.event;

import java.util.EventObject;

import edu.zjut.common.data.DataSetForApps;
import edu.zjut.common.data.attr.AttributeData;


/**
 * An DataSetEvent signals that a new data set is available.
 * 
 */
public class DataSetEvent extends EventObject {

//	private final OriginalData dataSet;
	private transient DataSetForApps dataSetForApps;

	/**
	 * Note that the DataSetForApps is first. This allows us to pass in a null
	 * DataSetForApps if desired, without being confused with the previous cntr.
	 */
	public DataSetEvent(DataSetForApps dataSetForApps, Object source) {
		super(source);
		this.dataSetForApps = dataSetForApps;
//		dataSet = dataSetForApps.getDataObjectOriginal();
	}

	public DataSetForApps getDataSetForApps() {
		return dataSetForApps;
	}
}
