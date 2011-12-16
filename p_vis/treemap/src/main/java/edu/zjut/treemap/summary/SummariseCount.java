package edu.zjut.treemap.summary;

import java.util.List;

import edu.zjut.common.data.attr.MeasureField;

public class SummariseCount extends SummariseField {

	public SummariseCount(MeasureField dataField) {
		super(dataField);
	}

	public Object compute(List<Object> values) {
		return values.size();
	}
}