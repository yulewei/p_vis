package edu.zjut.common.data.attr;

/**
 * Î¬¶È.
 * 
 * @author yulewei
 */
public class DimensionField extends DataField {

	boolean isObservation = false;

	public DimensionField(int colIdx, String name, FieldType dataType,
			Object[] columnValues) {
		super(colIdx, name, dataType, columnValues);
		this.isObservation = false;
	}

	public DimensionField(int colIdx, String name, FieldType dataType,
			Object[] columnValues, boolean isObservation) {
		super(colIdx, name, dataType, columnValues);
		this.isObservation = isObservation;
	}

	public boolean isObservation() {
		return isObservation;
	}

	public void setObservation(boolean isObservation) {
		this.isObservation = isObservation;
	}
}
