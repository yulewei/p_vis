package edu.zjut.common.data;

/**
 * 
 * @author yulewei
 *
 */
public enum AttrType {
	TYPE_NONE, TYPE_ID, TYPE_STRING, TYPE_INT, TYPE_DOUBLE, TYPE_BOOLEAN;

	public boolean isNumericType() {
		return this == TYPE_INT || this == TYPE_DOUBLE
				|| this == TYPE_BOOLEAN;
	}
}
