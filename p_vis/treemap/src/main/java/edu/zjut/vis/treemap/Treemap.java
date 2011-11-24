package edu.zjut.vis.treemap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.gicentre.apps.hide.ColourScaling;
import org.gicentre.data.DataField;
import org.gicentre.data.FieldType;
import org.gicentre.data.Record;
import org.gicentre.data.summary.SummariseCount;
import org.gicentre.data.summary.SummariseField;
import org.gicentre.data.summary.SummariseMax;
import org.gicentre.data.summary.SummariseMean;
import org.gicentre.data.summary.SummariseMin;
import org.gicentre.data.summary.SummariseSum;
import org.gicentre.data.summary.SummariseUniqueCount;
import org.gicentre.utils.colour.ColourTable;

import edu.zjut.common.data.AttrType;
import edu.zjut.common.data.AttributeData;
import edu.zjut.common.data.DataSetForApps;
import edu.zjut.common.data.SummaryType;
import edu.zjut.common.event.DataSetEvent;
import edu.zjut.common.event.DataSetListener;

public class Treemap extends JPanel implements DataSetListener {

	private DataSetForApps dataSet;
	private AttributeData attrData;

	private PTreemap pTreemap;

	private String defaultHive = null;
	private List<DataField> hierFields;
	private List<SummariseField> summariseFields;
	private List<Record> records;

	public Treemap() {
		BorderLayout layout = new BorderLayout();
		this.setLayout(layout);

		pTreemap = new PTreemap();
		this.add(pTreemap);
		pTreemap.init();

		this.setPreferredSize(new Dimension(500, 500));

		defaultHive = "sHier(/,$城区,$楼盘); sOrder(/,HIER,HIER); sSize(/,FX,FX); sColor(/,HIER,$累积成交毛坯均价); sLayout(/,SF,SF);";

	}

	@Override
	public void dataSetChanged(DataSetEvent e) {
		dataSet = e.getDataSetForApps();
		attrData = dataSet.getAttrData();

		buildTreemapData();

		pTreemap.setData(hierFields, summariseFields, records, defaultHive);

		repaint();
	}

	private void buildTreemapData() {
		hierFields = new ArrayList<DataField>();
		summariseFields = new ArrayList<SummariseField>();
		records = new ArrayList<Record>();

		List<DataField> datafields = new ArrayList<DataField>();

		Object[] columnArrays = attrData.getColumnArrays();
		AttrType[] dataTypes = attrData.getDataTypes();
		String[] attributeNames = attrData.getAttributeNames();
		SummaryType[] summaryTypes = attrData.getSummaryTypes();
		ColourTable[] colorTables = attrData.getColorTables();

		int colSize = dataTypes.length;
		int rowSize = attrData.getNumObservations();
		for (int col = 0; col < colSize; col++) {
			String name = attributeNames[col];
			DataField dataField = new DataField(name, col,
					TypeTansform(dataTypes[col]));
			datafields.add(dataField);

			if (dataTypes[col].isDimensionType()) {
				hierFields.add(dataField);
			} else if (dataTypes[col].isMeasureType()) {
				SummariseField summariseField = parseSummaryType(name,
						summaryTypes[col], dataField);
				summariseField.setColourTable(colorTables[col]);

				// 暂时全部用LIN
				summariseField.setColourScaling(ColourScaling.LIN);

				summariseFields.add(summariseField);
			}
		}

		for (int row = 0; row < rowSize; row++) {
			Object[] values = new Object[colSize];
			for (int col = 0; col < colSize; col++) {
				switch (dataTypes[col]) {
				case ID:
					values[col] = ((int[]) columnArrays[col])[row];
					break;
				case STRING:
					values[col] = ((String[]) columnArrays[col])[row];
					break;
				case INT:
					values[col] = ((int[]) columnArrays[col])[row];
					break;
				case DOUBLE:
					values[col] = ((double[]) columnArrays[col])[row];
					break;
				}
			}

			records.add(new Record(values));
		}
	}

	private FieldType TypeTansform(AttrType type) {
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
