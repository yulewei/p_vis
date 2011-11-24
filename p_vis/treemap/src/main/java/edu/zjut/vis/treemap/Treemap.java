package edu.zjut.vis.treemap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JPanel;

import org.gicentre.apps.hide.ColourScaling;
import org.gicentre.data.summary.SummariseCount;
import org.gicentre.data.summary.SummariseField;
import org.gicentre.data.summary.SummariseMax;
import org.gicentre.data.summary.SummariseMean;
import org.gicentre.data.summary.SummariseMin;
import org.gicentre.data.summary.SummariseSum;
import org.gicentre.data.summary.SummariseUniqueCount;

import edu.zjut.common.ctrl.FieldList;
import edu.zjut.common.ctrl.FieldTransferHandler;
import edu.zjut.common.data.DataSetForApps;
import edu.zjut.common.data.attr.AttributeData;
import edu.zjut.common.data.attr.DataField;
import edu.zjut.common.data.attr.DimensionField;
import edu.zjut.common.data.attr.FieldType;
import edu.zjut.common.data.attr.MeasureField;
import edu.zjut.common.data.attr.SummaryType;
import edu.zjut.common.event.DataSetEvent;
import edu.zjut.common.event.DataSetListener;

public class Treemap extends JPanel implements DataSetListener {

	private DataSetForApps dataSet;
	private AttributeData attrData;

	private FieldList<String> filedList;
	private PTreemap pTreemap;

	private String defaultHive = null;
	private List<DataField> hierFields;
	private List<SummariseField> summariseFields;
	private List<Object[]> records;
	private List<Object[]> columnValues;

	public Treemap() {
		this.setLayout(new BorderLayout());
		filedList = new FieldList();
		filedList.setDropMode(DropMode.ON_OR_INSERT);
		filedList.setDragEnabled(true);
		filedList.setTransferHandler(new FieldTransferHandler());
		DefaultListModel<String> listModel = new DefaultListModel<String>();
		filedList.setModel(listModel);

		this.add(filedList, BorderLayout.NORTH);

		pTreemap = new PTreemap();
		this.add(pTreemap, BorderLayout.CENTER);
		pTreemap.init();

		this.setPreferredSize(new Dimension(500, 500));

		defaultHive = "sHier(/,$城区,$楼盘); sOrder(/,HIER,HIER); sSize(/,FX,FX); sColor(/,HIER,$累积成交毛坯均价); sLayout(/,SF,SF);";

	}

	@Override
	public void dataSetChanged(DataSetEvent e) {
		dataSet = e.getDataSetForApps();
		attrData = dataSet.getAttrData();

		buildTreemapData();

		pTreemap.setData(hierFields, summariseFields, records, columnValues,
				defaultHive);

		repaint();
	}

	private void buildTreemapData() {
		hierFields = new ArrayList<DataField>();
		summariseFields = new ArrayList<SummariseField>();
		records = new ArrayList<Object[]>();

		List<DataField> datafields = new ArrayList<DataField>();

		DataField[] fields = attrData.getFeilds();
		DimensionField[] dimensionFeilds = attrData.getDimensionFeilds();
		MeasureField[] measureFeilds = attrData.getMeasureFeilds();

		Object[][] columnArrays = attrData.getColumnArrays();
		int colSize = fields.length;
		int rowSize = attrData.getNumObservations();
		for (int col = 0; col < colSize; col++) {
			DataField field = fields[col];
			datafields.add(field);

			if (field instanceof DimensionField) {
				hierFields.add(field);
			} else if (field instanceof MeasureField) {
				MeasureField measureFeild = (MeasureField) field;
				SummariseField summariseField = parseSummaryType(
						measureFeild.getName(), measureFeild.getSummaryType(),
						field);
				summariseField.setColourTable(measureFeild.getColorTable());

				// 暂时全部用LIN
				summariseField.setColourScaling(ColourScaling.LIN);

				summariseFields.add(summariseField);
			}
		}

		for (int row = 0; row < rowSize; row++) {
			Object[] values = new Object[colSize];
			for (int col = 0; col < colSize; col++) {
				switch (fields[col].getFieldType()) {
				case ID:
					values[col] = ((Integer[]) columnArrays[col])[row];
					break;
				case STRING:
					values[col] = ((String[]) columnArrays[col])[row];
					break;
				case INT:
					values[col] = ((Integer[]) columnArrays[col])[row];
					break;
				case DOUBLE:
					values[col] = ((Double[]) columnArrays[col])[row];
					break;
				}
			}

			records.add(values);
		}
	}

	private FieldType TypeTansform(FieldType type) {
		switch (type) {
		case STRING:
			return FieldType.STRING;
		case INT:
			return FieldType.INT;
		case DOUBLE:
			return FieldType.DOUBLE;
		}
		return null;
	}

	private SummariseField parseSummaryType(String name,
			SummaryType summaryType, DataField refDataField) {
		if (summaryType == null)
			return new SummariseSum(name, refDataField);

		switch (summaryType) {
		case SUM:
			return new SummariseSum(name, refDataField);
		case MEAN:
			return new SummariseMean(name, refDataField);
		case COUNT:
			return new SummariseCount(name);
		case UNI_COUNT:
			return new SummariseUniqueCount(name, refDataField);
		case MAX:
			return new SummariseMax(name, refDataField);
		case MIN:
			return new SummariseMin(name, refDataField);
		}
		return new SummariseSum(name, refDataField);
	}
}
