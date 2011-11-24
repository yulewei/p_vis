package org.gicentre.data.summary;

import java.util.List;

import edu.zjut.common.data.attr.DataField;
import edu.zjut.common.data.attr.FieldType;

public class SummariseStandardDeviation extends SummariseField {

	public SummariseStandardDeviation(String name, DataField dataField) {
		this(name, dataField, FieldType.DOUBLE);
	}

	public SummariseStandardDeviation(String name, DataField dataField,
			FieldType fieldType) {
		this.name = name;
		this.dataField = dataField;
		this.fieldType = fieldType;
	}

	public Object compute(List<Object> values) {
		if (dataField.getFieldType() == FieldType.STRING) {
			return null;
		}
		double mean = ((Number) new SummariseMean(null, dataField)
				.compute(values)).doubleValue();

		double temp = 0;
		int n = 0;
		for (Object v : values) {
			if (v != null) {
				temp += Math.pow(((Number) v).doubleValue() - mean, 2);
			}
			n++;
		}
		temp /= n - 1;
		temp = Math.sqrt(temp);
		return correctObjectType(temp);
	}

}