package org.gicentre.data.summary;

import java.util.HashSet;
import java.util.List;

import org.gicentre.data.DataField;
import org.gicentre.data.FieldType;
import org.gicentre.data.Record;

public class SummariseWeightedMean extends SummariseField {

	protected DataField weightField;

	public SummariseWeightedMean(String name, DataField dataField,
			DataField weightField) {
		this(name, dataField, weightField, dataField.getFieldType());
	}

	public SummariseWeightedMean(String name, DataField dataField,
			DataField weightField, FieldType fieldType) {
		this.name = name;
		this.dataField = dataField;
		this.fieldType = fieldType;
		this.weightField = weightField;
	}

	public Object compute(List<Record> records) {
		if (dataField.getFieldType() == FieldType.STRING
				&& weightField.getFieldType() == FieldType.STRING) {
			return null;
		}

		double sum = 0;
		double wSum = 0;
		HashSet<Object> valuesUsed = new HashSet<Object>();
		for (Record record : records) {
			if (useRecord(record, valuesUsed)) {
				Object v1 = record.getValue(dataField);
				Object v2 = record.getValue(weightField);
				if (v1 != null && v2 != null && v1 instanceof Number
						&& v2 instanceof Number) {
					sum += ((Number) v1).doubleValue();
					wSum += ((Number) v2).doubleValue();
				}
			}
		}
		double avg = (sum / wSum);

		return correctObjectType(avg);
	}

}