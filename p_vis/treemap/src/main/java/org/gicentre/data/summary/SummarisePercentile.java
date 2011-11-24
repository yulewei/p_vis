package org.gicentre.data.summary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import edu.zjut.common.data.attr.DataField;
import edu.zjut.common.data.attr.FieldType;

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

	public Object compute(List<Object> values) {
		Object value = null;
		HashSet<Object> valuesUsed = new HashSet<Object>();
		List<Comparable> sortedList = new ArrayList<Comparable>(values.size());
		for (Object v : values) {
			Comparable<Object> comparable = (Comparable<Object>) v;
			if (comparable != null) {
				sortedList.add(comparable);
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