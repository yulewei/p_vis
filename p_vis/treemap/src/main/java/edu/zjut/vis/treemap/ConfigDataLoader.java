package edu.zjut.vis.treemap;

import java.awt.Color;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import org.gicentre.apps.hide.ColourScaling;
import org.gicentre.apps.hide.TreemapState.Layout;
import org.gicentre.data.Data;
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

import au.com.bytecode.opencsv.CSVReader;
import edu.zjut.vis.treemap.DataConfig.ColorMap;
import edu.zjut.vis.treemap.DataConfig.ColorMap.Rule;
import edu.zjut.vis.treemap.DataConfig.Variable;

/**
 * 
 * @author yulewei
 * 
 */
public class ConfigDataLoader {

	private String datasetName;
	private ArrayList<DataField> allowedHierVars;
	private ArrayList<SummariseField> allowedOrderVars;
	private ArrayList<SummariseField> allowedSizeVars;
	private ArrayList<SummariseField> allowedColourVars;
	private ArrayList<Layout> layouts;

	private HashSet<SummariseField> summariseFields;
	private Data data;

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

	public void loadVarData(ArrayList<Variable> varList, List<String[]> list) {
		List<DataField> datafields = new ArrayList<DataField>();

		TreeMap<Integer, DataField> hierVars = new TreeMap<Integer, DataField>();

		// summary variables
		summariseFields = new HashSet<SummariseField>();
		allowedOrderVars = new ArrayList<SummariseField>();
		allowedSizeVars = new ArrayList<SummariseField>();
		allowedColourVars = new ArrayList<SummariseField>();

		for (Variable var : varList) {
			DataField dataField = new DataField(var.name, var.colIdx,
					FieldType.valueOf(var.dataType.toUpperCase()));
			datafields.add(dataField);

			// values节点

			// colorMap节点

			// hier节点
			if (var.hier != null) {
				if (var.hier.useAllValues != null)
					dataField.setUseAllValues(var.hier.useAllValues);
				hierVars.put(var.hier.order, dataField);
			}

			// summary节点
			if (var.summary != null) {
				SummariseField summariseField = parseSummaryType(var.name,
						var.summary.summaryType, dataField);

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

		data = new Data(datafields, list);
		for (DataField dataField : allowedHierVars) {
			TreeSet<Object> values = new TreeSet<Object>();
			for (Record record : data.getRecords()) {
				Object value = record.getValue(dataField);
				values.add(value);
			}
			List<Object> orderedList = new ArrayList<Object>();
			orderedList.addAll(values);
			dataField.setOrderedValues(orderedList);
		}

		layouts = new ArrayList<Layout>();
		layouts.add(Layout.ONE_DIM_STRIP);
		layouts.add(Layout.ONE_DIM_LEFT_RIGHT);
		layouts.add(Layout.ONE_DIM_TOP_BOTTOM);
		layouts.add(Layout.TWO_DIMENSIONAL);
		layouts.add(Layout.ABS_POSITION);
	}

	private SummariseField parseSummaryType(String name, String summaryType,
			DataField refDataField) {
		SummariseField summariseField = null;

		if (summaryType.equals("sum"))
			summariseField = new SummariseSum(name, refDataField);

		else if (summaryType.equals("mean"))
			summariseField = new SummariseMean(name, refDataField);

		else if (summaryType.equals("count"))
			summariseField = new SummariseCount(name);

		else if (summaryType.equals("uniqueCount"))
			summariseField = new SummariseUniqueCount(name, refDataField);

		else if (summaryType.equals("max"))
			summariseField = new SummariseMax(name, refDataField);

		else if (summaryType.equals("min"))
			summariseField = new SummariseMin(name, refDataField);

		return summariseField;
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

		ColourScaling colourScaling = ColourScaling.LIN;
		if (colorMap.scaling != null
				&& colorMap.scaling.equalsIgnoreCase("log")) {
			colourScaling = ColourScaling.LOG;
		}

		summariseField.setColourScaling(colourScaling);

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

		summariseField.setColourTable(colourTable);
	}

	/**
	 * Gets the data (columns/rows)
	 * 
	 * @return the data
	 */
	public Data getData() {
		return data;
	}

	/**
	 * Gets all the summary variables
	 * 
	 * @return
	 */
	public HashSet<SummariseField> getSummariseFields() {
		return summariseFields;
	}

	/**
	 * gets the data variables that can appear in the hierarchy
	 * 
	 * @return
	 */
	public ArrayList<DataField> getAllowedHierVars() {
		return allowedHierVars;
	}

	/**
	 * Gets the summary variables that can be used for order
	 * 
	 * @return
	 */
	public ArrayList<SummariseField> getAllowedOrderVars() {
		return allowedOrderVars;
	}

	/**
	 * Gets the summary variables that can be used for size
	 * 
	 * @return
	 */
	public ArrayList<SummariseField> getAllowedSizeVars() {
		return allowedSizeVars;
	}

	/**
	 * Gets the summary variables that can be used for colour
	 * 
	 * @return
	 */

	public ArrayList<SummariseField> getAllowedColourVars() {
		return allowedColourVars;
	}

	/**
	 * Gets the summary variables that can be used for layout
	 * 
	 * @return
	 */
	public ArrayList<Layout> getLayouts() {
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
