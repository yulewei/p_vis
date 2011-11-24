package org.gicentre.data.summary;

import java.util.List;

import edu.zjut.common.data.attr.DataField;
import edu.zjut.common.data.attr.FieldType;

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

	public Object compute(List<Object> values) {
		Object varValue = values.get(0);
		Object value = new Integer(orderValues.indexOf(varValue));
		return correctObjectType(value);
	}

}