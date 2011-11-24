package org.gicentre.data.summary;

import java.util.List;

import edu.zjut.common.data.attr.DataField;
import edu.zjut.common.data.attr.FieldType;

public class SummariseSum extends SummariseField {

	public SummariseSum(String name, DataField dataField) {
		this(name, dataField, dataField.getFieldType());
	}

	public SummariseSum(String name, DataField dataField, FieldType fieldType) {
		this.name = name;
		this.dataField = dataField;
		this.fieldType = fieldType;
	}

	public Object compute(List<Object> values) {
		if (dataField.getFieldType() == FieldType.STRING) {
			return null;
		}
		double sum = 0;
		for (Object v : values) {
			if (v != null && v instanceof Number) {
				sum += ((Number) v).doubleValue();
			}
		}
		return correctObjectType(sum);
	}
}