package org.gicentre.data.summary;

import java.util.List;

import org.gicentre.apps.hide.ColourScaling;
import org.gicentre.utils.colour.ColourTable;

import edu.zjut.common.data.attr.DataField;
import edu.zjut.common.data.attr.FieldType;

public abstract class SummariseField {
	protected String name;
	protected FieldType fieldType;
	protected DataField dataField;

	private ColourTable colourTable;
	private ColourScaling colourScaling;

	public ColourTable getColourTable() {
		return colourTable;
	}

	public void setColourTable(ColourTable colourTable) {
		this.colourTable = colourTable;
	}

	public ColourScaling getColourScaling() {
		return colourScaling;
	}

	public void setColourScaling(ColourScaling colourScaling) {
		this.colourScaling = colourScaling;
	}

	public String getName() {
		return name;
	}

	public DataField getDataField() {
		return dataField;
	}

	public int getColIdx() {
		return dataField.getColIdx();
	}

	public FieldType getFieldType() {
		return fieldType;
	}

	public abstract Object compute(List<Object> records);

	protected Object correctObjectType(Object value) {
		if (value == null) {
			return null;
		}
		if (fieldType == FieldType.STRING) {
			return value.toString();
		}
		if (fieldType == FieldType.INT) {
			if (value instanceof Integer) {
				return value;
			}
			if (value instanceof String) {
				return Integer.parseInt((String) value);
			} else {
				return ((Number) value).intValue();
			}
		}		
		if (fieldType == FieldType.DOUBLE) {
			if (value instanceof Double) {
				return value;
			}
			if (value instanceof String) {
				return Double.parseDouble((String) value);
			} else {
				return ((Number) value).doubleValue();
			}
		}
		return null;
	}

	public String toString() {
		return name;
	}

}