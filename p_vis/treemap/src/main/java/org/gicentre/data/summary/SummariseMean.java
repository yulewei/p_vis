package org.gicentre.data.summary;

import java.util.List;

import edu.zjut.common.data.attr.FieldType;
import edu.zjut.common.data.attr.MeasureField;

public class SummariseMean extends SummariseField {

	public SummariseMean(MeasureField dataField) {
		super(dataField);
	}

	public Object compute(List<Object> values) {
		if (dataField.getFieldType() == FieldType.STRING) {
			return null;
		}
		double sum = 0;
		int n = 0;
		for (Object v : values) {
			if (v != null && v instanceof Number) {
				sum += ((Number) v).doubleValue();
				n++;
			}
		}
		if (n == 0) {
			return null;
		} else {
			double avg = (double) sum / n;
			return correctObjectType(avg);
		}
	}

}