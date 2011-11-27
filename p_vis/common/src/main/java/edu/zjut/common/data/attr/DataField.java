package edu.zjut.common.data.attr;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * 数据维度(字段)封装
 * 
 * @author yulewei
 * 
 */
public abstract class DataField {

	protected int colIdx;
	protected String name;
	protected FieldType dataType;
	protected Object[] columnValues;

	public DataField(int colIdx, String name, FieldType dataType,
			Object[] columnValues) {
		this.colIdx = colIdx;
		this.name = name;
		this.dataType = dataType;
		this.columnValues = columnValues;
	}

	public int getColIdx() {
		return colIdx;
	}

	public void setColIdx(int colIdx) {
		this.colIdx = colIdx;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FieldType getFieldType() {
		return dataType;
	}

	public void setFieldType(FieldType dataType) {
		this.dataType = dataType;
	}

	public void setColumnValues(Object[] columnValues) {
		this.columnValues = columnValues;
	}

	public Object[] getColumnValues() {
		return columnValues;
	}
	
	public List<Object> getOrderValues() {
		List<Object> list = new ArrayList<Object>();
		TreeSet<Object> values = new TreeSet<Object>();
		for (Object o : columnValues) {
			values.add(o);
		}
		list.addAll(values);
		return list;
	}
}
