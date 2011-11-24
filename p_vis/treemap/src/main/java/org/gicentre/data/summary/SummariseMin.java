package org.gicentre.data.summary;

import java.util.List;

import edu.zjut.common.data.attr.DataField;
import edu.zjut.common.data.attr.FieldType;

public class SummariseMin extends SummariseField {
	public SummariseMin(String name, DataField dataField) {
		this(name, dataField, dataField.getFieldType());
	}

	public SummariseMin(String name, DataField dataField, FieldType fieldType) {
		this.name = name;
		this.dataField = dataField;
		this.fieldType = fieldType;
	}

	public Object compute(List<Object> values) {
		Object value = null;
		for (Object v : values) {
			if (v != null
					&& (value == null || ((Comparable) v)
							.compareTo((Comparable) value) == 1)) {
				value = v;
			}
		}
		return correctObjectType(value);
	}
}