package org.gicentre.data.summary;

import java.util.HashSet;
import java.util.List;

import org.gicentre.data.Data.Record;
import org.gicentre.data.DataField;
import org.gicentre.data.FieldType;

public class SummariseMin extends SummariseField {
	public SummariseMin(String name, DataField dataField) {
		this(name, dataField, dataField.getFieldType());
	}

	public SummariseMin(String name, DataField dataField, FieldType fieldType) {
		this.name = name;
		this.dataField = dataField;
		this.fieldType = fieldType;
	}

	public Object compute(List<Record> records) {
		Object value = null;
		HashSet<Object> valuesUsed = new HashSet<Object>();
		for (Record record : records) {
			if (useRecord(record, valuesUsed)) {
				Object v = record.getValue(dataField);
				if (v != null
						&& (value == null || ((Comparable) v)
								.compareTo((Comparable) value) == 1)) {
					value = v;
				}
			}
		}
		return correctObjectType(value);
	}
}