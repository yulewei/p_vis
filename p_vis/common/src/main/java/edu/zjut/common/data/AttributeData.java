package edu.zjut.common.data;

import org.gicentre.utils.colour.ColourTable;

public class AttributeData {

	private int keyCol = 0;
	private int nameCol = 1;

	private AttrType[] dataTypes;

	private String[] attributeNames;

	private SummaryType[] summaryTypes;

	private ColourTable[] colorTables;

	/**
	 * 类型解析后的文件内容, 数据部分, 按列保存
	 */
	private Object[] columnArrays;

	// 维度数据
	private int numObservations;
	private String[] observationNames;
	private int numDimensions;
	private String[] dimensionNames;
	private Object[] dimensions;

	// 度量(数值)数据
	public int numMeasures;
	public String[] measureNames;
	public Object[] measures;

	public AttributeData(int nameCol, AttrType[] dataTypes, String[] varNames,
			Object[] columnArrays, SummaryType[] summaryTypes,
			ColourTable[] colorTables) {
		this.nameCol = nameCol;

		this.dataTypes = dataTypes;
		this.attributeNames = varNames;
		this.columnArrays = columnArrays;

		this.summaryTypes = summaryTypes;
		this.colorTables = colorTables;

		// 提取维度部分
		initDimensions();

		// 提取度量(数值)部分
		initMeasures();
	}

	public AttributeData(int nameCol, String[] varNames, Object[] columnArrays) {
		this.nameCol = nameCol;

		this.attributeNames = varNames;
		this.columnArrays = columnArrays;

		initDataTypes();

		// 提取维度部分
		initDimensions();

		// 提取度量(数值)部分
		initMeasures();
	}

	private void initDataTypes() {
		dataTypes = new AttrType[columnArrays.length];
		for (int i = 0; i < columnArrays.length; i++) {
			if (columnArrays[i] instanceof String[])
				dataTypes[i] = AttrType.STRING;
			else if (columnArrays[i] instanceof boolean[])
				dataTypes[i] = AttrType.BOOLEAN;
			else if (columnArrays[i] instanceof int[])
				dataTypes[i] = AttrType.INT;
			else if (columnArrays[i] instanceof double[])
				dataTypes[i] = AttrType.DOUBLE;
			else
				dataTypes[i] = AttrType.NONE;
		}
	}

	/**
	 * 提取维度部分
	 */
	private void initDimensions() {
		observationNames = (String[]) columnArrays[nameCol];
		if (observationNames != null)
			numObservations = observationNames.length;

		int len = attributeNames.length;
		numDimensions = 0;
		for (int i = 0; i < len; i++) {
			if (dataTypes[i].isDimensionType())
				numDimensions++;
		}

		dimensions = new Object[numDimensions];
		dimensionNames = new String[numDimensions];
		int k = 0;
		for (int i = 0; i < len; i++) {
			if (dataTypes[i].isDimensionType()) {
				dimensions[k] = columnArrays[i];
				dimensionNames[k] = attributeNames[i];
				k++;
			}
		}
	}

	/**
	 * 提取度量(数值)部分
	 */
	private void initMeasures() {
		int len = attributeNames.length;
		numMeasures = 0;
		for (int i = 0; i < len; i++) {
			if (dataTypes[i].isMeasureType())
				numMeasures++;
		}

		measures = new Object[numMeasures];
		measureNames = new String[numMeasures];
		int k = 0;
		for (int i = 0; i < len; i++) {
			if (dataTypes[i].isMeasureType()) {
				measures[k] = columnArrays[i];
				measureNames[k] = attributeNames[i];
				k++;
			}
		}
	}

	public AttrType[] getDataTypes() {
		return dataTypes;
	}

	public Object[] getColumnArrays() {
		return columnArrays;
	}

	public String[] getAttributeNames() {
		return attributeNames;
	}

	public SummaryType[] getSummaryTypes() {
		return summaryTypes;
	}

	public ColourTable[] getColorTables() {
		return colorTables;
	}

	public int getNumObservations() {
		return numObservations;
	}

	public String[] getObservationNames() {
		return observationNames;
	}

	public Object[] getDimensions() {
		return dimensions;
	}

	public String[] getDimensionNames() {
		return dimensionNames;
	}

	public int getNumDimensions() {
		return numDimensions;
	}

	public Object[] getMeasures() {
		return measures;
	}

	public int getNumMeasures() {
		return numMeasures;
	}

	public String[] getMeasureNames() {
		return measureNames;
	}

	public double[] getMeasureColumnAsDouble(int col) {
		Object measure = measures[col];
		// because it is a string array of variable names
		double[] doubleData = null;
		if (measure instanceof double[]) {
			doubleData = (double[]) measure;
		} else if (measure instanceof int[]) {
			int[] intData = (int[]) measure;
			doubleData = new double[intData.length];
			for (int i = 0; i < intData.length; i++) {
				if (intData[i] == Integer.MIN_VALUE) {
					doubleData[i] = Double.NaN;
				} else {
					doubleData[i] = intData[i];
				}
			} // next i
		} else {
			throw new IllegalArgumentException(
					"Unable to parse values in column " + col + " as a number");
		}
		return doubleData;
	}

	public double getMeasureValueAsDouble(int col, int row) {
		Object measure = measures[col];
		double[] doubleData = null;
		double doubleVal = Double.NaN;
		if (measure instanceof double[]) {
			doubleData = (double[]) measure;
			doubleVal = doubleData[row];
		} else if (measure instanceof int[]) {
			int[] intData = (int[]) measure;
			doubleVal = intData[row];
		} else {
			throw new IllegalArgumentException(
					"Unable to parse values in column " + col + " as a number");
		}
		return doubleVal;
	}
}
