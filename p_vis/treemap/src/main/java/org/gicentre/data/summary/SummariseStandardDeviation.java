package org.gicentre.data.summary;

import java.util.List;

import edu.zjut.common.data.attr.FieldType;
import edu.zjut.common.data.attr.MeasureField;

public class SummariseStandardDeviation extends SummariseField {

	public SummariseStandardDeviation(MeasureField dataField) {
		super(dataField);
	}

	public Object compute(List<Object> values) {
		if (dataField.getFieldType() == FieldType.STRING) {
			return null;
		}
		double mean = ((Number) new SummariseMean(dataField).compute(values))
				.doubleValue();

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