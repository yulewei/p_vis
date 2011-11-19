package org.gicentre.data.summary;

import java.util.List;

import org.gicentre.data.DataField;
import org.gicentre.data.FieldType;
import org.gicentre.data.Record;

public class SummariseNormalisedMean extends SummariseField {

	protected DataField normaliseField;// this would usually be area

	/**
	 * Class that computes an average that's normalised (with sum)
	 * 
	 * (uses 'float' data type as default)
	 * 
	 * @param name
	 * @param dataField
	 * @param normaliseField
	 */
	public SummariseNormalisedMean(String name, DataField dataField,
			DataField normaliseField) {
		this(name, dataField, normaliseField, FieldType.FLOAT);
	}

	public SummariseNormalisedMean(String name, DataField dataField,
			DataField normaliseField, FieldType fieldType) {
		this.name = name;
		this.dataField = dataField;
		this.fieldType = fieldType;
		this.normaliseField = normaliseField;
	}

	public Object compute(List<Record> records) {
		if (dataField.getFieldType() == FieldType.STRING
				|| normaliseField.getFieldType() == FieldType.STRING) {
			return null;
		}
		double avg = 0;
		double sum = 0;
		try {
			avg = ((Number) new SummariseMean(null, dataField).compute(records))
					.doubleValue();
			sum = ((Number) new SummariseSum(null, normaliseField)
					.compute(records)).doubleValue();
		} catch (NullPointerException e) {
			// returns null (no data) if either of the stats (above) return null
			// (no data)
			return null;
		}
		double normalised = (avg / sum);
		return correctObjectType(normalised);
	}

}