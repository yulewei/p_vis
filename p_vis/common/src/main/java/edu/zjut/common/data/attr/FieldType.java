package edu.zjut.common.data.attr;

/**
 * 
 * @author yulewei
 * 
 */
public enum FieldType {
	ID, STRING, INT, DOUBLE, BOOLEAN;

	public boolean isDimensionType() {
		// return this == TYPE_ID || this == TYPE_STRING || this ==
		// TYPE_BOOLEAN;
		return this == ID || this == STRING;
	}

	public boolean isMeasureType() {
		return this == INT || this == DOUBLE;
	}
}
