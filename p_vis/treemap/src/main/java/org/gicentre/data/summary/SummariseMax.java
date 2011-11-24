package org.gicentre.data.summary;

import java.util.HashSet;
import java.util.List;

import edu.zjut.common.data.attr.DataField;
import edu.zjut.common.data.attr.FieldType;

public class SummariseMax extends SummariseField {

	public SummariseMax(String name, DataField dataField) {
		this(name, dataField, dataField.getFieldType());
	}

	public SummariseMax(String name, DataField dataField, FieldType fieldType) {
		this.name = name;
		this.dataField = dataField;
		this.fieldType = fieldType;
	}

	public Object compute(List<Object> values) {
		Object value = null;
		HashSet<Object> valuesUsed = new HashSet<Object>();
		for (Object v : values) {
			if (v != null
					&& (value == null || ((Comparable) v)
							.compareTo((Comparable) value) == -1)) {
				value = v;
			}
		}
		return correctObjectType(value);
	}

}