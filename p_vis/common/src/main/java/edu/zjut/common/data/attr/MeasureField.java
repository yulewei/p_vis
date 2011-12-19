package edu.zjut.common.data.attr;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.gicentre.utils.colour.ColourTable;

import edu.zjut.common.color.ColorScaling;

/**
 * 度量(数值).
 * 
 * @author yulewei
 */
public class MeasureField extends DataField {

	protected SummaryType summaryType;
	protected ColourTable colorTable;
	protected ColorScaling colorScaling;
	protected Number min;
	protected Number max;

	public MeasureField(int colIdx, String name, FieldType dataType,
			Object[] columnValues, SummaryType summaryType,
			ColourTable colorTable) {
		super(colIdx, name, dataType, columnValues);
		this.summaryType = summaryType;
		this.colorTable = colorTable;

		if (colorTable == null)
			this.colorTable = ColourTable
					.getPresetColourTable(ColourTable.OR_RD);
		buildData();
	}

	private void buildData() {
		if (dataType == null || columnValues == null)
			return;

		switch (dataType) {
		case INT:
			Integer[] intValues = (Integer[]) columnValues;
			List<Integer> intList = Arrays.asList(intValues);
			min = Collections.min(intList);
			max = Collections.max(intList);

			break;
		case DOUBLE:
			Double[] doubleValues = (Double[]) columnValues;
			List<Double> doubleList = Arrays.asList(doubleValues);
			min = Collections.min(doubleList);
			max = Collections.max(doubleList);
			break;
		}
	}

	public Number getMin() {
		return min;
	}

	public Number getMax() {
		return max;
	}

	/**
	 * 百分比
	 */
	public float findPercent(int index) {
		float f = 0.0f;
		if (dataType == FieldType.INT) {
			Integer v = (Integer) columnValues[index] - (Integer) min;
			f = 1.0f * v / ((Integer) max - (Integer) min);
		}
		if (dataType == FieldType.DOUBLE) {
			Double v = (Double) columnValues[index] - (Double) min;
			v = v / ((Double) max - (Double) min);
			f = v.floatValue();
		}

		return f;
	}

	public int findColor(int index) {
		return colorTable.findColour(findPercent(index));
	}

	public void setColumnValues(Object[] columnValues) {
		this.columnValues = columnValues;

		buildData();
	}

	public double[] getColumnAsDouble() {
		Double[] doubleData = null;
		switch (dataType) {
		case DOUBLE:
			doubleData = (Double[]) columnValues;
			break;
		case INT:
			Integer[] intData = (Integer[]) columnValues;
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

	public SummaryType getSummaryType() {
		return summaryType;
	}

	public void setSummaryType(SummaryType summaryType) {
		this.summaryType = summaryType;
	}

	public ColourTable getColorTable() {
		return colorTable;
	}

	public void setColorTable(ColourTable colorTable) {
		this.colorTable = colorTable;
	}

	public ColorScaling getColorScaling() {
		// return colorScaling;
		return ColorScaling.LIN;
	}

	public void setColorScaling(ColorScaling colorScaling) {
		this.colorScaling = colorScaling;
	}
}
