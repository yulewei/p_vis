package org.gicentre.data.summary;

import java.util.List;

import edu.zjut.common.data.attr.DataField;
import edu.zjut.common.data.attr.FieldType;

public class SummariseCoefficientOfVariation extends SummariseField {

	List<Object> orderValues;
	DataField orderField;

	public SummariseCoefficientOfVariation(String name, DataField dataField) {
		this(name, dataField, dataField.getFieldType());
	}

	public SummariseCoefficientOfVariation(String name, DataField dataField,
			FieldType fieldType) {
		this.name = name;
		this.dataField = dataField;
		this.fieldType = fieldType;
	}

	public Object compute(List<Object> records) {
		if (dataField.getFieldType() == FieldType.STRING) {
			return null;
		}

		double std = ((Number) new SummariseStandardDeviation(null, dataField)
				.compute(records)).doubleValue();
		double mean = ((Number) new SummariseMean(null, dataField)
				.compute(records)).doubleValue();
		return correctObjectType(std / mean);
	}

}