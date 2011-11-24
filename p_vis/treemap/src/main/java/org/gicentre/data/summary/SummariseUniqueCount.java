package org.gicentre.data.summary;

import java.util.HashSet;
import java.util.List;

import edu.zjut.common.data.attr.DataField;
import edu.zjut.common.data.attr.FieldType;

public class SummariseUniqueCount extends SummariseField {

	public SummariseUniqueCount(String name, DataField dataField) {
		this.name = name;
		this.dataField = dataField;
		this.fieldType = FieldType.INT;
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