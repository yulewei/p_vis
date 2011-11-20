package org.gicentre.data.summary;

import java.util.List;

import org.gicentre.data.DataField;
import org.gicentre.data.FieldType;
import org.gicentre.data.Record;

public class SummariseOrder extends SummariseField {

	List<Object> orderValues;
	DataField orderField;

	public SummariseOrder(String name, DataField dataField,
			DataField orderField, List<Object> orderValues) {
		this(name, dataField, orderField, orderValues, dataField.getFieldType());
	}

	public SummariseOrder(String name, DataField dataField,
			DataField orderField, List<Object> orderValues, FieldType fieldType) {
		this.name = name;
		this.dataField = dataField;
		this.fieldType = fieldType;
		this.orderValues = orderValues;
	}

	public Object compute(List<Record> records) {
		Object varValue = (records.get(0).getValue(orderField));
		Object value = new Integer(orderValues.indexOf(varValue));
		return correctObjectType(value);
	}

}