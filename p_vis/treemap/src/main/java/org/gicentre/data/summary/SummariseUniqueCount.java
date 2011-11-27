package org.gicentre.data.summary;

import java.util.HashSet;
import java.util.List;

import edu.zjut.common.data.attr.MeasureField;

public class SummariseUniqueCount extends SummariseField {

	public SummariseUniqueCount(MeasureField dataField) {
		super(dataField);
	}

	public Object compute(List<Object> values) {
		HashSet<Object> uniqueValues = new HashSet<Object>();
		for (Object v : values) {
			uniqueValues.add(v);
		}
		uniqueValues.remove(null);
		return correctObjectType(uniqueValues.size());
	}
}