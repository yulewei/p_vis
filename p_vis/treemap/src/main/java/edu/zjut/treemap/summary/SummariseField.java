package edu.zjut.treemap.summary;

import java.util.List;

import org.gicentre.utils.colour.ColourTable;

import edu.zjut.common.color.ColorScaling;
import edu.zjut.common.data.attr.FieldType;
import edu.zjut.common.data.attr.MeasureField;
import edu.zjut.common.data.attr.SummaryType;

public abstract class SummariseField {
	protected MeasureField dataField;

	public SummariseField(MeasureField dataField) {
		this.dataField = dataField;
	}

	public String getName() {
		return dataField.getName();
	}

	public int getColIdx() {
		return dataField.getColIdx();
	}

	public FieldType getFieldType() {
		return dataField.getFieldType();
	}

	public ColourTable getColorTable() {
		return dataField.getColorTable();
	}

	public void setColorTable(ColourTable colorTable) {
		dataField.setColorTable(colorTable);
	}

	public ColorScaling getColorScaling() {
		return dataField.getColorScaling();
	}

	public void setColorScaling(ColorScaling colorScaling) {
		dataField.setColorScaling(colorScaling);
	}

	public abstract Object compute(List<Object> records);

	protected Object correctObjectType(Object value) {
		if (value == null) {
			return null;
		}

		switch (dataField.getFieldType()) {
		case STRING:
			return value.toString();
		case INT:
			if (value instanceof Integer) {
				return value;
			}
			if (value instanceof String) {
				return Integer.parseInt((String) value);
			} else {
				return ((Number) value).intValue();
			}
		case DOUBLE:
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

	public static SummariseField createSummaryField(MeasureField field) {
		SummaryType summaryType = field.getSummaryType();
		if (summaryType == null)
			return new SummariseSum(field);
		switch (summaryType) {
		case SUM:
			return new SummariseSum(field);
		case MEAN:
			return new SummariseMean(field);
		case COUNT:
			return new SummariseCount(field);
		case UNI_COUNT:
			return new SummariseUniqueCount(field);
		case MAX:
			return new SummariseMax(field);
		case MIN:
			return new SummariseMin(field);
		}
		return new SummariseSum(field);
	}

	public String toString() {
		return dataField.getName();
	}
}