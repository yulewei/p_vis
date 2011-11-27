package edu.zjut.common.data.attr;

public class AttributeData {

	private DataField[] fields;
	private DimensionField[] dimensionFeilds;
	private MeasureField[] measureFeilds;

	private String[] dimensionNames;
	private String[] measureNames;

	private Object[][] dimensions;
	private Object[][] measures;

	private DimensionField observationFeild;
	private String[] observationNames;

	/**
	 * 类型解析后的文件内容, 数据部分, 按列保存
	 */
	private Object[][] columnArrays;

	public AttributeData(DataField[] feilds, Object[][] columnArrays) {
		this.fields = feilds;
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

		dimensionFeilds = new DimensionField[len];
		for (int i = 0, k = 0; i < fields.length; i++) {
			if (fields[i] instanceof DimensionField)
				dimensionFeilds[k++] = (DimensionField) fields[i];
		}

		dimensionNames = new String[len];
		dimensions = new Object[len][];
		for (int i = 0; i < len; i++) {
			dimensionNames[i] = dimensionFeilds[i].getName();

			int index = dimensionFeilds[i].getColIdx();
			dimensions[i] = columnArrays[index];

			if (dimensionFeilds[i].isObservation())
				observationFeild = dimensionFeilds[i];

		}

		observationNames = (String[]) columnArrays[observationFeild.getColIdx()];
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
		measureFeilds = new MeasureField[len];
		for (int i = 0, k = 0; i < fields.length; i++) {
			if (fields[i] instanceof MeasureField)
				measureFeilds[k++] = (MeasureField) fields[i];
		}

		measureNames = new String[len];
		for (int i = 0; i < measureNames.length; i++) {
			measureNames[i] = measureFeilds[i].getName();
		}

		measures = new Object[len][];
		for (int i = 0; i < len; i++) {
			int index = measureFeilds[i].getColIdx();
			measures[i] = columnArrays[index];
		}
	}

	public DataField[] getFeilds() {
		return fields;
	}

	public Object[][] getColumnArrays() {
		return columnArrays;
	}

	public DimensionField getObservationFeild() {
		return observationFeild;
	}

	public String[] getObservationNames() {
		return observationNames;
	}

	public int getNumObservations() {
		return observationNames.length;
	}

	public DimensionField[] getDimensionFeilds() {
		return dimensionFeilds;
	}

	public MeasureField[] getMeasureFeilds() {
		return measureFeilds;
	}

	public int getNumMeasures() {
		return measureFeilds.length;
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
		return transformRowArrays(measureFeilds, measures);
	}

	public Object[][] getDimensionRowArrays() {
		return transformRowArrays(dimensionFeilds, dimensions);
	}

	private Object[][] transformRowArrays(DataField[] fields,
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
		Object[][] measureRows = transformRowArrays(measureFeilds, measures);

		double[][] doubleRows = new double[measureRows.length][];
		for (int i = 0; i < measureRows.length; i++) {
			double[] row = new double[measureRows[i].length];
			for (int j = 0; j < measureRows[i].length; j++) {
				switch (measureFeilds[j].getFieldType()) {
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
		switch (measureFeilds[col].getFieldType()) {
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
