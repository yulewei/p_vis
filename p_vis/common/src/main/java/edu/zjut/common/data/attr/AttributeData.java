package edu.zjut.common.data.attr;

public class AttributeData {

	private DataField[] feilds;
	private DimensionField[] dimensionFeilds;
	private MeasureField[] measureFeilds;

	private String[] dimensionNames;
	private String[] measureNames;

	private Object[] dimensions;
	private Object[] measures;

	private DimensionField observationFeild;
	private String[] observationNames;

	/**
	 * 类型解析后的文件内容, 数据部分, 按列保存
	 */
	private Object[][] columnArrays;

	public AttributeData(DataField[] feilds, Object[][] columnArrays) {
		this.feilds = feilds;
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
		for (int i = 0; i < feilds.length; i++) {
			if (feilds[i] instanceof DimensionField)
				len++;
		}

		dimensionFeilds = new DimensionField[len];
		for (int i = 0, k = 0; i < feilds.length; i++) {
			if (feilds[i] instanceof DimensionField)
				dimensionFeilds[k++] = (DimensionField) feilds[i];
		}

		dimensionNames = new String[len];
		dimensions = new Object[len];
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
		for (int i = 0; i < feilds.length; i++) {
			if (feilds[i] instanceof MeasureField)
				len++;
		}
		measureFeilds = new MeasureField[len];
		for (int i = 0, k = 0; i < feilds.length; i++) {
			if (feilds[i] instanceof MeasureField)
				measureFeilds[k++] = (MeasureField) feilds[i];
		}

		measureNames = new String[len];
		for (int i = 0; i < measureNames.length; i++) {
			measureNames[i] = measureFeilds[i].getName();
		}

		measures = new Object[len];
		for (int i = 0; i < len; i++) {
			int index = measureFeilds[i].getColIdx();
			measures[i] = columnArrays[index];
		}
	}

	public DataField[] getFeilds() {
		return feilds;
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

	public Object[] getDimensions() {
		return dimensions;
	}

	public Object[] getMeasures() {
		return measures;
	}

	public Object[] getColumnAsArray(int col) {
		return columnArrays[col];
	}

	public double[] getMeasureColumnAsDouble(int col) {
		Object measure = columnArrays[measureFeilds[col].getColIdx()];
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
		Object measure = columnArrays[measureFeilds[col].getColIdx()];
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
