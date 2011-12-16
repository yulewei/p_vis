package edu.zjut.treemap.io;

import java.awt.Color;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.gicentre.utils.colour.ColourTable;

import au.com.bytecode.opencsv.CSVReader;
import edu.zjut.common.color.ColorScaling;
import edu.zjut.common.data.attr.DataField;
import edu.zjut.common.data.attr.DimensionField;
import edu.zjut.common.data.attr.FieldType;
import edu.zjut.common.data.attr.MeasureField;
import edu.zjut.treemap.core.Layout;
import edu.zjut.treemap.io.DataConfig.ColorMap;
import edu.zjut.treemap.io.DataConfig.Variable;
import edu.zjut.treemap.io.DataConfig.ColorMap.Rule;
import edu.zjut.treemap.summary.SummariseField;

/**
 * 
 * @author yulewei
 * 
 */
public class ConfigDataLoader {

	private String datasetName;
	private List<DataField> allowedHierVars;
	private List<SummariseField> allowedOrderVars;
	private List<SummariseField> allowedSizeVars;
	private List<SummariseField> allowedColourVars;
	private List<Layout> layouts;

	private List<SummariseField> summariseFields;
	private List<Object[]> records;

	public ConfigDataLoader(String configFilename) {
		DataConfig config = DataConfig.loadConfig(configFilename);
		datasetName = config.datasetName;
		List<String[]> list = readFile(config);
		loadVarData(config.varList, list);
	}

	protected List<String[]> readFile(DataConfig config) {
		List<String[]> list = null;
		try {
			char delt = ',';
			if (config.fileType.equalsIgnoreCase("tab"))
				delt = '\t';
			int ignore = config.ignoreFirstLine ? 1 : 0;
			CSVReader reader = new CSVReader(new FileReader(config.fileName),
					delt, '\'', ignore);

			list = reader.readAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	protected void parseDataFile(List<DataField> dataFields,
			List<String[]> recordList) {
		records = new ArrayList<Object[]>();

		for (int k = 0; k < recordList.size(); k++) {
			String[] line = recordList.get(k);
			Object[] values = parseLine(line, dataFields);
			records.add(values);
		}

		// 更新列索引位置
		for (int i = 0; i < dataFields.size(); i++) {
			DataField dataField = dataFields.get(i);
			dataField.setColIdx(i);
		}
	}

	public Object[] parseLine(String[] line, List<DataField> dataFields) {
		Object[] values = new Object[dataFields.size()];
		for (int i = 0; i < dataFields.size(); i++) {
			DataField dataField = dataFields.get(i);
			String v = line[dataField.getColIdx()];
			if (v != null) {
				switch (dataField.getFieldType()) {
				case INT:
					values[i] = Integer.parseInt(v);
					break;
				case DOUBLE:
					values[i] = Double.parseDouble(v);
					break;
				case STRING:
					values[i] = v.toString().intern();
					break;
				}
			}
		}
		return values;
	}

	public void loadVarData(ArrayList<Variable> varList, List<String[]> list) {
		List<DataField> datafields = new ArrayList<DataField>();

		TreeMap<Integer, DataField> hierVars = new TreeMap<Integer, DataField>();

		// summary variables
		summariseFields = new ArrayList<SummariseField>();
		allowedOrderVars = new ArrayList<SummariseField>();
		allowedSizeVars = new ArrayList<SummariseField>();
		allowedColourVars = new ArrayList<SummariseField>();

		for (Variable var : varList) {
			DataField dataField = null;
			FieldType fieldType = FieldType.valueOf(var.dataType.toUpperCase());
			if (var.summary == null) {
				dataField = new DimensionField(var.colIdx, var.name, fieldType,
						null);
				datafields.add(dataField);
			}

			// hier节点
			if (var.hier != null) {
				hierVars.put(var.hier.order, dataField);
			}

			// summary节点
			if (var.summary != null) {

				MeasureField field = new MeasureField(var.colIdx, var.name,
						fieldType, null, null, null);
				datafields.add(field);

				SummariseField summariseField = SummariseField
						.createSummaryField(field);

				if (var.summary.colorMap != null)
					parseColorMap(summariseField, var.summary.colorMap);

				summariseFields.add(summariseField);

				// find out if a order, size or colour variables
				if (var.summary.order != null)
					allowedOrderVars.add(summariseField);

				if (var.summary.size != null)
					allowedSizeVars.add(summariseField);

				if (var.summary.colour != null)
					allowedColourVars.add(summariseField);
			}
		}

		allowedHierVars = new ArrayList<DataField>();
		for (DataField dataField : hierVars.values()) {
			allowedHierVars.add(dataField);
		}

		allowedColourVars.add(Math.max(0, allowedColourVars.size() - 1), null);

		// 解析数据文件
		parseDataFile(datafields, list);

		for (DataField dataField : allowedHierVars) {
			Object[] values = new Object[records.size()];
			for (int i = 0; i < records.size(); i++) {
				Object[] record = records.get(i);
				values[i] = record[dataField.getColIdx()];
			}
			dataField.setColumnValues(values);
		}
	}

	private void parseColorMap(SummariseField summariseField, ColorMap colorMap) {

		ColourTable colourTable = null;

		if (colorMap.preset != null) {
			int type = 0;
			try {
				type = ColourTable.class.getField(colorMap.preset).getInt(
						ColourTable.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			colourTable = ColourTable.getPresetColourTable(type);
		} else {
			colourTable = new ColourTable();
		}

		ColorScaling colourScaling = ColorScaling.LIN;
		if (colorMap.scaling != null
				&& colorMap.scaling.equalsIgnoreCase("log")) {
			colourScaling = ColorScaling.LOG;
		}

		summariseField.setColorScaling(colourScaling);

		if (colorMap.rules != null) {
			for (Rule rule : colorMap.rules) {
				String color = rule.color;
				int numValue = rule.numValue;
				if (color.startsWith("#")
						&& (color.length() == 7 || color.length() == 9)) {
					int r = Integer.parseInt(color.substring(1, 3), 16);
					int g = Integer.parseInt(color.substring(3, 5), 16);
					int b = Integer.parseInt(color.substring(5, 7), 16);
					int a = 255;
					if (color.length() == 9) {
						a = Integer.parseInt(color.substring(7, 9), 16);
					}
					colourTable.addDiscreteColourRule(numValue, new Color(r, g,
							b, a).getRGB());
				}
			}
		}

		summariseField.setColorTable(colourTable);
	}

	public List<Object[]> getRecords() {
		return records;
	}


	/**
	 * Gets all the summary variables
	 * 
	 * @return
	 */
	public List<SummariseField> getSummariseFields() {
		return summariseFields;
	}

	/**
	 * gets the data variables that can appear in the hierarchy
	 * 
	 * @return
	 */
	public List<DataField> getAllowedHierVars() {
		return allowedHierVars;
	}

	/**
	 * Gets the summary variables that can be used for order
	 * 
	 * @return
	 */
	public List<SummariseField> getAllowedOrderVars() {
		return allowedOrderVars;
	}

	/**
	 * Gets the summary variables that can be used for size
	 * 
	 * @return
	 */
	public List<SummariseField> getAllowedSizeVars() {
		return allowedSizeVars;
	}

	/**
	 * Gets the summary variables that can be used for colour
	 * 
	 * @return
	 */

	public List<SummariseField> getAllowedColourVars() {
		return allowedColourVars;
	}

	/**
	 * Gets the summary variables that can be used for layout
	 * 
	 * @return
	 */
	public List<edu.zjut.treemap.core.Layout> getLayouts() {
		return layouts;
	}

	/**
	 * Gets the summary variables that can be used for size
	 * 
	 * @return
	 */
	public String getDatasetName() {
		return datasetName;
	}
}
