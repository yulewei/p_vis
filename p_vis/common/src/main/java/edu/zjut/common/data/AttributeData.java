package edu.zjut.common.data;

public class AttributeData {

	public int keyCol = 0;
	public int nameCol = 1;

	public AttrType[] dataTypes;
	public String[] attributeNames;

	/**
	 * 类型解析后的文件内容, 数据部分, 按列保存
	 */
	public Object[] columnArrays;

	public String[] observationNames;
	public int numObservations;
	public Object[] dataSetNumeric;
	public String[] numericAttributeNames;
	public int numNumericAttributes;

	public AttributeData(AttrType[] dataTypes, String[] varNames,
			Object[] columnArrays, int nameCol) {
		this.dataTypes = dataTypes;
		this.attributeNames = varNames;
		this.columnArrays = columnArrays;

		this.nameCol = nameCol;
		
		// 属性数据, 提取数值部分
		initDataSetNumeric();
	}

	public AttributeData(String[] varNames, Object[] columnArrays, int nameCol) {

		this.attributeNames = varNames;
		this.columnArrays = columnArrays;

		this.nameCol = nameCol;

		initDataTypes();
		
		initDataSetNumeric();
	}

	private void initDataTypes() {
		dataTypes = new AttrType[columnArrays.length];
		for (int i = 0; i < columnArrays.length; i++) {
			if (columnArrays[i] instanceof String[])
				dataTypes[i] = AttrType.TYPE_STRING;
			else if (columnArrays[i] instanceof int[])
				dataTypes[i] = AttrType.TYPE_INT;
			else if (columnArrays[i] instanceof double[])
				dataTypes[i] = AttrType.TYPE_DOUBLE;
			else if (columnArrays[i] instanceof boolean[])
				dataTypes[i] = AttrType.TYPE_BOOLEAN;
			else
				dataTypes[i] = AttrType.TYPE_NONE;
		}
	}

	/**
	 * 属性数据, 提取数值部分
	 */
	public void initDataSetNumeric() {
		observationNames = (String[]) columnArrays[nameCol];
		if (observationNames != null)
			numObservations = observationNames.length;

		int len = attributeNames.length;
		numNumericAttributes = 0;
		for (int i = 0; i < len; i++) {
			if (dataTypes[i].isNumericType())
				numNumericAttributes++;
		}

		dataSetNumeric = new Object[numNumericAttributes];
		numericAttributeNames = new String[numNumericAttributes];
		int k = 0;
		for (int i = 0; i < len; i++) {
			if (dataTypes[i].isNumericType()) {
				dataSetNumeric[k] = columnArrays[i];
				numericAttributeNames[k] = attributeNames[i];
				k++;
			}
		}
	}

	/**
	 * Returns the attribute names for all input arrays.
	 */
	public String[] getAttributeNamesOriginal() {
		return attributeNames;
	}

	public Object[] getColumnArrays() {
		return columnArrays;
	}

	public String getColumnName(int i) {
		return attributeNames[i];
	}

	public Object[] getDataSetNumeric() {
		return dataSetNumeric;
	}

	public String[] getObservationNames() {
		return observationNames;
	}
	
	/**
	 * Returns the number of numerical variables (double[], int[], and String[])
	 */
	public int getNumberNumericAttributes() {
		return numNumericAttributes;
	}

	/**
	 * Returns the number of observations in the data set, for which there are
	 * attribute names.
	 */
	public int getNumObservations() {
		return numObservations;
	}

	/**
	 * This first index is zero, the next one, and so on, the last being
	 * getNumberNumericAttributes() -1
	 */
	public double[] getNumericDataAsDouble(int numericArrayIndex) {
		Object dataNumeric = dataSetNumeric[numericArrayIndex];
		// because it is a string array of variable names
		double[] doubleData = null;
		if (dataNumeric instanceof double[]) {
			doubleData = (double[]) dataNumeric;
		} else if (dataNumeric instanceof int[]) {
			int[] intData = (int[]) dataNumeric;
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
					"Unable to parse values in column " + numericArrayIndex
							+ " as a number");
		}
		return doubleData;
	}

	/**
	 * Returns a double where the numericArrayIndex is the nth numeric array in
	 * the data set, and obs is the nth observation in that array.
	 */
	public double getNumericValueAsDouble(int numericArrayIndex, int obs) {
		Object dataNumeric = dataSetNumeric[numericArrayIndex];
		double[] doubleData = null;
		double doubleVal = Double.NaN;
		if (dataNumeric instanceof double[]) {
			doubleData = (double[]) dataNumeric;
			doubleVal = doubleData[obs];
		} else if (dataNumeric instanceof int[]) {
			int[] intData = (int[]) dataNumeric;
			doubleVal = intData[obs];
		} else {
			throw new IllegalArgumentException(
					"Unable to parse values in column " + numericArrayIndex
							+ " as a number");
		}
		return doubleVal;
	}

	/**
	 * Returns the names of only the numerical variables (double[], int[], and
	 * boolean[]) from the attribute arrays.
	 */
	public String[] getNumericAttributeNames() {
		return numericAttributeNames;
	}

	/**
	 * Returns the name of the nth numeric array.
	 */

	public String getNumericArrayName(int arrayPlace) {
		return numericAttributeNames[arrayPlace];
	}
}
