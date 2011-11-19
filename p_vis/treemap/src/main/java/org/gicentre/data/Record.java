package org.gicentre.data;

import java.util.Arrays;

/**
 * Stores a record as an inner class NO DATA is stored as nulls, so need to
 * check this when using Data are stored as specified by each datafield. Can be
 * retrieved in any format (as long as it can be converted)
 * 
 * @author Aidan Slingsby
 * 
 */
public class Record {
	public Object[] values;

	public Record(Object[] values) {
		this.values = values;
	}

	public Object getValue(DataField dataField) {
		return values[dataField.getColIdx()];
	}

	public String toString() {
		return Arrays.toString(values);
	}
}