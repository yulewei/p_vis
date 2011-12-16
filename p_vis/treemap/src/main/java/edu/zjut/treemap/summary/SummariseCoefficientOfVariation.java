package edu.zjut.treemap.summary;

import java.util.List;

import edu.zjut.common.data.attr.FieldType;
import edu.zjut.common.data.attr.MeasureField;

public class SummariseCoefficientOfVariation extends SummariseField {

	public SummariseCoefficientOfVariation(MeasureField dataField) {
		super(dataField);
	}

	public Object compute(List<Object> records) {
		if (dataField.getFieldType() == FieldType.STRING) {
			return null;
		}

		double std = ((Number) new SummariseStandardDeviation(dataField)
				.compute(records)).doubleValue();
		double mean = ((Number) new SummariseMean(dataField).compute(records))
				.doubleValue();
		return correctObjectType(std / mean);
	}
}