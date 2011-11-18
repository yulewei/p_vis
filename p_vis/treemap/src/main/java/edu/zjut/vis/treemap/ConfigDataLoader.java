package edu.zjut.vis.treemap;

import java.awt.Color;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.gicentre.apps.hide.ColourScaling;
import org.gicentre.apps.hide.TreemapState.Layout;
import org.gicentre.data.Data;
import org.gicentre.data.Data.Record;
import org.gicentre.data.DataField;
import org.gicentre.data.FieldType;
import org.gicentre.data.summary.SummariseCount;
import org.gicentre.data.summary.SummariseField;
import org.gicentre.data.summary.SummariseMax;
import org.gicentre.data.summary.SummariseMean;
import org.gicentre.data.summary.SummariseMin;
import org.gicentre.data.summary.SummariseSum;
import org.gicentre.data.summary.SummariseUniqueCount;
import org.gicentre.utils.colour.ColourTable;

import edu.zjut.vis.treemap.DataConfig.ColorMap;
import edu.zjut.vis.treemap.DataConfig.ColorMap.Rule;
import edu.zjut.vis.treemap.DataConfig.Summary;
import edu.zjut.vis.treemap.DataConfig.Variable;

import au.com.bytecode.opencsv.CSVReader;

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

	private Data data;
	private HashSet<SummariseField> summariseFields;
	private HashMap<Object, ColourTable> colours = new HashMap<Object, ColourTable>();
	private HashMap<Object, ColourScaling> colourScalings = new HashMap<Object, ColourScaling>();

	public ConfigDataLoader(String configFilename) {
		try {
			DataConfig config = DataConfig.loadConfig(configFilename);
			loadData(config);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void loadData(DataConfig config) throws IOException {
		
		datasetName = config.datasetName;

		Collection<DataField> datafields = new HashSet<DataField>();
		List<ObjectIntNumber<DataField>> hierVars = new ArrayList<ObjectIntNumber<DataField>>();

		for (Variable var : config.varList) {

			DataField dataField = new DataField(var.name, var.colIdx,
					FieldType.valueOf(var.dataType.toUpperCase()));
			datafields.add(dataField);

			// values节点

			// colorMap节点

			// hier节点
			if (var.hier != null) {
				if (var.hier.useAllValues != null)
					dataField.setUseAllValues(var.hier.useAllValues);
				ObjectIntNumber<DataField> objectIntNumber = new ObjectIntNumber<DataField>(
						dataField, var.hier.order);
				hierVars.add(objectIntNumber);
			}
		}

		char delt = ',';
		if (config.fileType.equalsIgnoreCase("tab"))
			delt = '\t';
		int ignore = config.ignoreFirstLine ? 1 : 0;
		CSVReader reader = new CSVReader(new FileReader(config.fileName), delt,
				'\'', ignore);

		List<String[]> list = reader.readAll();

		data = new Data(datafields, list);

		// add values to *datafields* if no values currently set, in natural
		// order
		for (ObjectIntNumber<DataField> objectIntNumber : hierVars) {
			DataField dataField = objectIntNumber.getObject();
			if (dataField.getOrderValues() == null) {
				// find all unique value
				List<Comparable<Object>> values = new ArrayList<Comparable<Object>>();
				for (Record record : data.getRecords()) {
					@SuppressWarnings("unchecked")
					Comparable<Object> value = (Comparable<Object>) record
							.getValue(dataField);
					if (value != null && !values.contains(value)) {
						values.add(value);
					}
				}
				Collections.sort(values);
				List<Object> orderedList = new ArrayList<Object>();
				for (Comparable<Object> v : values) {
					orderedList.add(v);
				}
				dataField.setOrderedValues(orderedList);
			}
		}

		// allocate a default perceptually-uniform colour map to each hierarchy
		// variable where colour has not been allocated
		int[] perceptualColours = new int[7];
		perceptualColours[0] = -7417875;
		perceptualColours[1] = -8530257;
		perceptualColours[2] = -3554571;
		perceptualColours[3] = -2112128;
		perceptualColours[4] = -19805;
		perceptualColours[5] = -348950;
		perceptualColours[6] = -5123462;
		for (ObjectIntNumber<DataField> objectIntNumber : hierVars) {
			DataField dataField = objectIntNumber.getObject();
			if (!colours.containsKey(dataField)) {
				int numValues = dataField.getOrderValues().size();
				ColourTable colourTable = new ColourTable();
				colourTable.addContinuousColourRule(-1,
						new Color(220, 220, 220).getRGB());// for no data
				for (int i = 0; i < perceptualColours.length; i++) {
					colourTable.addContinuousColourRule(i
							* (numValues / ((float) perceptualColours.length)),
							perceptualColours[i]);
				}
				colours.put(dataField, colourTable);
			}
		}

		// summary variables
		summariseFields = new HashSet<SummariseField>();
		List<ObjectIntNumber<SummariseField>> orderVars = new ArrayList<ObjectIntNumber<SummariseField>>();
		List<ObjectIntNumber<SummariseField>> sizeVars = new ArrayList<ObjectIntNumber<SummariseField>>();
		List<ObjectIntNumber<SummariseField>> colourVars = new ArrayList<ObjectIntNumber<SummariseField>>();

		for (Summary sum : config.sumList) {

			SummariseField summariseField = parseSummaryType(sum.name,
					sum.summaryType, sum.refVariable, sum.refVariable2);

			if (sum.colorMap != null)
				parseColorMap(summariseField, sum.colorMap);

			summariseFields.add(summariseField);

			// find out if a order, size or colour variables
			if (sum.order != null)
				orderVars.add(new ObjectIntNumber<SummariseField>(
						summariseField, 0));

			if (sum.size != null)
				sizeVars.add(new ObjectIntNumber<SummariseField>(
						summariseField, 0));

			if (sum.colour != null)
				colourVars.add(new ObjectIntNumber<SummariseField>(
						summariseField, 0));
		}

		Collections.sort(hierVars);
		allowedHierVars = new ArrayList<DataField>();
		for (ObjectIntNumber<DataField> objectIntNumber : hierVars) {
			allowedHierVars.add(objectIntNumber.getObject());
		}
		Collections.sort(orderVars);
		allowedOrderVars = new ArrayList<SummariseField>();
		for (ObjectIntNumber<SummariseField> objectIntNumber : orderVars) {
			allowedOrderVars.add(objectIntNumber.getObject());
		}
		Collections.sort(sizeVars);
		allowedSizeVars = new ArrayList<SummariseField>();
		for (ObjectIntNumber<SummariseField> objectIntNumber : sizeVars) {
			allowedSizeVars.add(objectIntNumber.getObject());
		}
		Collections.sort(colourVars);
		allowedColourVars = new ArrayList<SummariseField>();
		for (ObjectIntNumber<SummariseField> objectIntNumber : colourVars) {
			allowedColourVars.add(objectIntNumber.getObject());
		}
		allowedColourVars.add(Math.max(0, allowedColourVars.size() - 1), null);

		layouts = new ArrayList<Layout>();
		layouts.add(Layout.ONE_DIM_STRIP);
		layouts.add(Layout.ONE_DIM_LEFT_RIGHT);
		layouts.add(Layout.ONE_DIM_TOP_BOTTOM);
		layouts.add(Layout.TWO_DIMENSIONAL);
		layouts.add(Layout.ABS_POSITION);
	}

	private SummariseField parseSummaryType(String name, String summaryType,
			String refVariable, String refVariable2) {
		SummariseField summariseField = null;

		// if *sum*
		if (summaryType.equals("sum")) {
			DataField refDataField = data.getDataField(refVariable);
			summariseField = new SummariseSum(name, refDataField);
		}

		// if *mean*
		else if (summaryType.equals("mean")) {
			DataField refDataField = data.getDataField(refVariable);
			summariseField = new SummariseMean(name, refDataField);
		}

		// if *count*
		else if (summaryType.equals("count")) {
			summariseField = new SummariseCount(name);
		}

		// if *uniqueCount*
		else if (summaryType.equals("uniqueCount")) {
			DataField refDataField = data.getDataField(refVariable);
			summariseField = new SummariseUniqueCount(name, refDataField);
		}

		// if *max*
		else if (summaryType.equals("max")) {
			DataField refDataField = data.getDataField(refVariable);
			summariseField = new SummariseMax(name, refDataField);

		}

		// if *min*
		else if (summaryType.equals("min")) {
			DataField refDataField = data.getDataField(refVariable);
			summariseField = new SummariseMin(name, refDataField);
		}

		// // if *weightedMean*
		// else if (summaryType.equals("weightedMean")) {
		// DataField refDataField = data.getDataField(refVariable);
		// DataField refDataField2 = data.getDataField(refVariable2);
		// summariseField = new SummariseWeightedMean(name, refDataField,
		// refDataField2);
		// }
		//
		// // if *normalisedSum*
		// else if (summaryType.equals("normalisedSum")) {
		// DataField refDataField = data.getDataField(refVariable);
		// DataField refDataField2 = data.getDataField(refVariable2);
		// summariseField = new SummariseNormalisedSum(name, refDataField,
		// refDataField2);
		// }
		//
		// // if *normalisedAvg*
		// else if (summaryType.equals("normalisedMean")) {
		// DataField refDataField = data.getDataField(refVariable);
		// DataField refDataField2 = data.getDataField(refVariable2);
		// summariseField = new SummariseNormalisedMean(name, refDataField,
		// refDataField2);
		// }

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

		colourScalings.put(summariseField, colourScaling);

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

		colours.put(summariseField, colourTable);
	}

	/**
	 * Gets the colourtables for each variable
	 * 
	 * @return colourtables keyed to the variable
	 */
	public HashMap<Object, ColourTable> getColours() {
		return colours;
	}

	/**
	 * Gets the colour scalings (log/linear) for each variable
	 * 
	 * @return colourscalings keyed to the variable
	 */
	public HashMap<Object, ColourScaling> getColourScalings() {
		return colourScalings;
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

	/**
	 * Class that stores an Object and a number and can sort itself wrt another
	 * on the number Used in collections to sort object based on an associated
	 * number
	 * 
	 * @author sbbb717
	 * 
	 * @param <E>
	 */
	class ObjectIntNumber<E> implements Comparable<ObjectIntNumber<E>> {
		E object;
		float number;

		public ObjectIntNumber(E object, float number) {
			this.object = object;
			this.number = number;
		}

		public E getObject() {
			return (E) object;
		}

		public int compareTo(ObjectIntNumber<E> objectIntNumber) {
			if (this.number < objectIntNumber.number) {
				return -1;
			} else if (this.number > objectIntNumber.number) {
				return 1;
			} else {
				return 0;
			}
		}

		public String toString() {
			return this.object.toString() + ":" + number;
		}
	}
}
