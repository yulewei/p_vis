package org.gicentre.data.summary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.gicentre.data.DataField;
import org.gicentre.data.FieldType;
import org.gicentre.data.Record;

public class SummarisePercentile extends SummariseField {

	private int percentile;

	public SummarisePercentile(String name, DataField dataField, int percentile) {
		this(name, dataField, percentile, dataField.getFieldType());
	}

	public SummarisePercentile(String name, DataField dataField,
			int percentile, FieldType fieldType) {
		this.name = name;
		this.dataField = dataField;
		this.fieldType = fieldType;
		this.percentile = percentile;
	}

	public Object compute(List<Record> records) {
		Object value = null;
		HashSet<Object> valuesUsed = new HashSet<Object>();
		List<Comparable> sortedList = new ArrayList<Comparable>(records.size());
		for (Record record : records) {
			if (useRecord(record, valuesUsed)) {
				Comparable<Object> comparable = (Comparable<Object>) record
						.getValue(dataField);
				if (comparable != null) {
					sortedList.add(comparable);
				}
			}
		}
		Collections.sort(sortedList);

		if (sortedList.isEmpty()) {
			return null;
		} else {
			value = sortedList.get(
					(int) (sortedList.size() * (percentile / (float) 100)))
					.toString();
			return correctObjectType(value);
		}
	}
}