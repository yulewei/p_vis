package org.gicentre.data.summary;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.zjut.common.data.attr.DataField;
import edu.zjut.common.data.attr.FieldType;

public class SummariseDiversity extends SummariseField {

	public SummariseDiversity(String name, DataField dataField) {
		this(name, dataField, dataField.getFieldType());
	}

	public SummariseDiversity(String name, DataField dataField,
			FieldType fieldType) {
		this.name = name;
		this.dataField = dataField;
		this.fieldType = fieldType;
	}

	public Object compute(List<Object> values) {
		HashMap<Object, Integer> counts = new HashMap<Object, Integer>();

		// count each value of datafield
		HashSet<Object> valuesUsed = new HashSet<Object>();
		for (Object v : values) {
			Integer count = counts.get(v);
			if (count == null) {
				count = 0;
			}
			count++;
			counts.put(v, count);
		}

		// sum them
		int sumCounts = 0;
		for (Integer count : counts.values()) {
			sumCounts += count;
		}

		// find cumulative differences for all counts against every other count
		int cumSumDiffs = 0;
		for (Object value : counts.keySet()) {
			int refCount = counts.get(value);
			for (Integer count : counts.values()) {
				cumSumDiffs += Math.abs(refCount - count);
			}
		}
		return correctObjectType(new Double((double) cumSumDiffs / sumCounts));
	}

}