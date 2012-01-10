package edu.zjut.common.data.attr;

public class AttributeData {

	private DataField[] fields;
	private DimensionField[] dimensionFields;
	private MeasureField[] measureFields;

	private String[] dimensionNames;
	private String[] measureNames;

	private Object[][] dimensions;
	private Object[][] measures;

	private DimensionField observationField;
	private String[] observationNames;

	/**
	 * 类型解析后的文件内容, 数据部分, 按列保存
	 */
	private Object[][] columnArrays;

	public AttributeData(DataField[] fields, Object[][] columnArrays) {
		this.fields = fields;
		this.columnArrays = columnArrays;

		// 提取维度部分
		initDimensions();

		// 提取度量(数值)部分
		initMeasures();
	}

	/**
	 * 提取维度部分
	 */
	private void initDimensions() {

		int len = 0;
		for (int i = 0; i < fields.length; i++) {
			if (fields[i] instanceof DimensionField)
				len++;
		}

		dimensionFields = new DimensionField[len];
		for (int i = 0, k = 0; i < fields.length; i++) {
			if (fields[i] instanceof DimensionField)
				dimensionFields[k++] = (DimensionField) fields[i];
		}

		dimensionNames = new String[len];
		dimensions = new Object[len][];
		for (int i = 0; i < len; i++) {
			dimensionNames[i] = dimensionFields[i].getName();

			int index = dimensionFields[i].getColIdx();
			dimensions[i] = columnArrays[index];

			if (dimensionFields[i].isObservation())
				observationField = dimensionFields[i];

		}

		observationNames = (String[]) columnArrays[observationField.getColIdx()];
	}

	/**
	 * 提取度量(数值)部分
	 */
	private void initMeasures() {
		int len = 0;
		for (int i = 0; i < fields.length; i++) {
			if (fields[i] instanceof MeasureField)
				len++;
		}
		measureFields = new MeasureField[len];
		for (int i = 0, k = 0; i < fields.length; i++) {
			if (fields[i] instanceof MeasureField)
				measureFields[k++] = (MeasureField) fields[i];
		}

		measureNames = new String[len];
		for (int i = 0; i < measureNames.length; i++) {
			measureNames[i] = measureFields[i].getName();
		}

		measures = new Object[len][];
		for (int i = 0; i < len; i++) {
			int index = measureFields[i].getColIdx();
			measures[i] = columnArrays[index];
		}
	}

	public DataField[] getFields() {
		return fields;
	}

	public Object[][] getColumnArrays() {
		return columnArrays;
	}

	public DimensionField getObservationField() {
		return observationField;
	}

	public String[] getObservationNames() {
		return observationNames;
	}

	public int getNumObservations() {
		return observationNames.length;
	}

	public DimensionField[] getDimensionFields() {
		return dimensionFields;
	}

	public MeasureField[] getMeasureFields() {
		return measureFields;
	}

	public int getNumMeasures() {
		return measureFields.length;
	}

	public String[] getMeasureNames() {
		return measureNames;
	}

	public String[] getDimensionNames() {
		return dimensionNames;
	}

	public Object[][] getDimensions() {
		return dimensions;
	}

	public Object[][] getMeasures() {
		return measures;
	}

	public Object[] getColumnAsArray(int col) {
		return columnArrays[col];
	}

	public Object[][] getRowArrays() {
		return transformRowArrays(fields, columnArrays);
	}

	public Object[][] getMeasureRowArrays() {
		return transformRowArrays(measureFields, measures);
	}

	public Object[][] getDimensionRowArrays() {
		return transformRowArrays(dimensionFields, dimensions);
	}

	public Object[][] transformRowArrays(DataField[] fields,
			Object[][] columnArrays) {
		int colSize = columnArrays.length;
		int rowSize = columnArrays[0].length;

		Object[][] rowArrays = new Object[rowSize][];
		for (int row = 0; row < rowSize; row++) {
			Object[] values = new Object[colSize];
			for (int col = 0; col < colSize; col++) {
				switch (fields[col].getFieldType()) {
				case ID:
				case INT:
					values[col] = ((Integer[]) columnArrays[col])[row];
					break;
				case STRING:
					values[col] = ((String[]) columnArrays[col])[row];
					break;
				case DOUBLE:
					values[col] = ((Double[]) columnArrays[col])[row];
					break;
				}
			}
			rowArrays[row] = values;
		}

		return rowArrays;
	}

	public double[][] getMeasureRowsAsDouble() {
		Object[][] measureRows = transformRowArrays(measureFields, measures);

		double[][] doubleRows = new double[measureRows.length][];
		for (int i = 0; i < measureRows.length; i++) {
			double[] row = new double[measureRows[i].length];
			for (int j = 0; j < measureRows[i].length; j++) {
				switch (measureFields[j].getFieldType()) {
				case DOUBLE:
					row[j] = (double) measureRows[i][j];
					break;
				case INT:
					row[j] = (int) measureRows[i][j];
					break;
				}
			}
			doubleRows[i] = row;
		}

		return doubleRows;
	}

	public double[] getMeasureColumnAsDouble(int col) {
		Object[] measure = measures[col];

		Double[] doubleData = null;
		switch (measureFields[col].getFieldType()) {
		case DOUBLE:
			doubleData = (Double[]) measure;
			break;
		case INT:
			Integer[] intData = (Integer[]) measure;
			doubleData = new Double[intData.length];
			for (int i = 0; i < intData.length; i++) {
				if (intData[i] == Integer.MIN_VALUE) {
					doubleData[i] = Double.NaN;
				} else {
					doubleData[i] = Double.valueOf(intData[i]);
				}
			}
			break;
		}

		double[] data = new double[doubleData.length];
		for (int i = 0; i < doubleData.length; i++) {
			data[i] = doubleData[i];
		}
		return data;
	}
}
