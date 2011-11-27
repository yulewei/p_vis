package edu.zjut.common.data.attr;

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

	public MeasureField(int colIdx, String name, FieldType dataType,
			Object[] columnValues, SummaryType summaryType,
			ColourTable colorTable) {
		super(colIdx, name, dataType, columnValues);
		this.summaryType = summaryType;
		this.colorTable = colorTable;
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
//		return colorScaling;
		return ColorScaling.LIN;
	}

	public void setColorScaling(ColorScaling colorScaling) {
		this.colorScaling = colorScaling;
	}
}
