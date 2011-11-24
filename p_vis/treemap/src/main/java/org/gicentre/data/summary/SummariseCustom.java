package org.gicentre.data.summary;

import java.util.List;

import edu.zjut.common.data.attr.FieldType;

/**
 * Stores custom value
 * 
 * @author sbbb717
 * 
 */
public class SummariseCustom extends SummariseField {
	Object value;

	public SummariseCustom(String name, FieldType fieldType) {
		this.name = name;
		this.fieldType = fieldType;
	}

	public Object compute(List<Object> records) {
		return value; // Just returns last set value
	}

	/**
	 * Set value Automatically converted to the correct type for the field
	 * 
	 * @param dataField
	 * @param value
	 */
	public void setValue(Object value) {
		this.value = correctObjectType(value);
	}

	/**
	 * Set value Automatically converted to the correct type for the field
	 * 
	 * @param dataField
	 * @param value
	 */
	public void setValue(String value) {
		if (value == null) {
			this.value = null;
		} else if (fieldType == FieldType.INT) {
			this.value = Integer.parseInt(value);
		} else if (fieldType == FieldType.DOUBLE) {
			this.value = Double.parseDouble(value);
		} else if (fieldType == FieldType.STRING) {
			this.value = value;
		}
	}

	/**
	 * Set value to NODATA Automatically converted to the correct type for the
	 * field
	 * 
	 * @param dataField
	 * @param value
	 */
	public void setValueNoData() {
		this.value = null;
	}

	/**
	 * Returns the value of a datafield as an Object
	 * 
	 * @param dataField
	 * @return
	 */

	Object getValue() {
		return value;
	}

	/**
	 * Returns the value of a datafield as an integer. Will convert if it has
	 * to, if compatible.
	 * 
	 * @param dataField
	 * @return
	 */
	public Integer getValueAsInt() {
		Object v = value;
		if (v instanceof Integer) {
			return (Integer) v;
		}
		try {
			return ((Number) v).intValue();
		} catch (NumberFormatException e) {
			return null;
		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * Returns the value as a float Will convert if it has to, if compatible.
	 * 
	 * @param dataField
	 * @return
	 */
	public Float getValueAsFloat() {
		Object v = value;
		if (v instanceof Float) {
			return (Float) v;
		}
		try {
			return ((Number) v).floatValue();
		} catch (NumberFormatException e) {
			return null;
		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * Returns the value as a double Will convert if it has to, if compatible.
	 * 
	 * @param dataField
	 * @return
	 */
	public Double getValueAsDouble() {
		Object v = value;
		if (v instanceof Double) {
			return (Double) v;
		}
		try {
			return ((Number) v).doubleValue();
		} catch (NumberFormatException e) {
			return null;
		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * Returns the value as a long Will convert if it has to, if compatible.
	 * 
	 * @param dataField
	 * @return
	 */
	public Long getValueAsLong() {
		Object v = value;
		if (v instanceof Long) {
			return (Long) v;
		}
		try {
			return ((Number) v).longValue();
		} catch (NumberFormatException e) {
			return null;
		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * Returns the value as a string Will convert if it has to, if compatible.
	 * 
	 * @param dataField
	 * @return
	 */
	public String getValueAsString() {
		Object v = value;
		try {
			return v.toString();
		} catch (NullPointerException e) {
			return null;
		}
	}

}