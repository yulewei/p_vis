package org.gicentre.data.summary;

import java.util.HashSet;
import java.util.List;

import org.gicentre.data.DataField;
import org.gicentre.data.FieldType;
import org.gicentre.data.Record;

public class SummariseUniqueCount extends SummariseField {

	public SummariseUniqueCount(String name, DataField dataField) {
		this.name = name;
		this.dataField = dataField;
		this.fieldType = FieldType.INT;
	}

	public Object compute(List<Record> records) {
		HashSet<Object> uniqueValues = new HashSet<Object>();
		for (Record record : records) {
			Object v = record.getValue(dataField);
			uniqueValues.add(v);
		}
		uniqueValues.remove(null);
		return correctObjectType(uniqueValues.size());
	}

}