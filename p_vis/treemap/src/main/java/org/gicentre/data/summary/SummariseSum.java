package org.gicentre.data.summary;

import java.util.List;

import edu.zjut.common.data.attr.FieldType;
import edu.zjut.common.data.attr.MeasureField;

public class SummariseSum extends SummariseField {

	public SummariseSum(MeasureField dataField) {
		super(dataField);
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