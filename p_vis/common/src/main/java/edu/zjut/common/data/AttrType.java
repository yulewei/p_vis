package edu.zjut.common.data;

/**
 * 
 * @author yulewei
 * 
 */
public enum AttrType {
	NONE, ID, STRING, INT, DOUBLE, BOOLEAN;

	public boolean isDimensionType() {
		// return this == TYPE_ID || this == TYPE_STRING || this ==
		// TYPE_BOOLEAN;
		return this == STRING;
	}

	public boolean isMeasureType() {
		return this == INT || this == DOUBLE;
	}
}
