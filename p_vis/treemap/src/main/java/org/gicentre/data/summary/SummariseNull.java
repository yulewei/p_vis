package org.gicentre.data.summary;

import java.util.List;

import edu.zjut.common.data.attr.MeasureField;

public class SummariseNull extends SummariseField {

	public SummariseNull(String name) {
		super(new MeasureField(0, name, null, null, null, null));
	}

	public Object compute(List<Object> values) {
		return null;
	}
}