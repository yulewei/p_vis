package org.gicentre.data.summary;

import java.util.HashSet;
import java.util.List;

import edu.zjut.common.data.attr.FieldType;

public class SummariseCount extends SummariseField {

	public SummariseCount(String name) {
		this.name = name;
		this.fieldType = FieldType.INT;
	}

	public Object compute(List<Object> values) {
		if (dataField == null) {
			return values.size();
		} else {
			int c = 0;
			HashSet<Object> valuesUsed = new HashSet<Object>();
			for (Object value : values) {
				c++;
			}
			return c;
		}
	}
}