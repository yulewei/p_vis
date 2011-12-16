package edu.zjut.treemap.summary;

import java.util.List;

import edu.zjut.common.data.attr.MeasureField;

public class SummariseMin extends SummariseField {

	public SummariseMin(MeasureField dataField) {
		super(dataField);
	}

	public Object compute(List<Object> values) {
		Object value = null;
		for (Object v : values) {
			if (v != null
					&& (value == null || ((Comparable) v)
							.compareTo((Comparable) value) == 1)) {
				value = v;
			}
		}
		return correctObjectType(value);
	}
}