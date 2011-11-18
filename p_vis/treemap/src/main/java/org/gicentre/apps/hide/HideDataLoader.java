package org.gicentre.apps.hide;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.gicentre.apps.hide.TreemapState.Layout;
import org.gicentre.data.Data;
import org.gicentre.data.DataField;
import org.gicentre.data.FieldType;
import org.gicentre.data.Data.FileType;
import org.gicentre.data.Data.Record;
import org.gicentre.data.summary.SummariseCount;
import org.gicentre.data.summary.SummariseField;
import org.gicentre.data.summary.SummariseMax;
import org.gicentre.data.summary.SummariseMean;
import org.gicentre.data.summary.SummariseMin;
import org.gicentre.data.summary.SummariseNormalisedMean;
import org.gicentre.data.summary.SummariseNormalisedSum;
import org.gicentre.data.summary.SummariseSum;
import org.gicentre.data.summary.SummariseUniqueCount;
import org.gicentre.data.summary.SummariseWeightedMean;
import org.gicentre.utils.colour.ColourTable;
import org.gicentre.utils.io.DOMProcessor;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Handles loading the input data and stores the data that is loads in
 * 
 * @author Aidan Slingsby, giCentre
 * 
 */
public class HideDataLoader {

	// these store all the information extracted from the datafile that can be
	// accessed
	// using the getter methods
	private HashMap<Object, ColourTable> colours = new HashMap<Object, ColourTable>();
	private HashMap<Object, ColourScaling> colourScalings = new HashMap<Object, ColourScaling>();
	private Data data;
	private HashSet<SummariseField> summariseFields;
	private ArrayList<DataField> allowedHierVars;
	private ArrayList<SummariseField> allowedOrderVars;
	private ArrayList<SummariseField> allowedSizeVars;
	private ArrayList<SummariseField> allowedColourVars;
	private ArrayList<Layout> layouts;
	private String datasetName;

	// set by the constructor and accessed by run()
	InputStream xmlInputStream;
	BufferedReader dataReader;
	FileType dataFileType;
	boolean ignoreFirstLineOfDataInput;

	/**
	 * Loads a dataset assumes - the datafile has a .tab extension - the data
	 * files are tab delimited and the first line is ignored - the XML
	 * descriptor file has the same base filename with the ".xml" extension See
	 * http://gicentre.org/hide/fileformat.php for details
	 * 
	 * @param filename
	 *            with a .tab extension
	 */
	public static HideDataLoader loadDataset(String filename)
			throws IOException {
		File file = new File(filename);
		if (!file.exists() && file.isFile()) {
			throw new IOException("File " + filename + " does not exist.");
		}
		FileType fileType;
		if (filename.endsWith(".tab")) {
			fileType = FileType.TAB_DELIMITED;
		} else if (filename.endsWith(".csv")) {
			fileType = FileType.CSV;
		}
		// if (!filename.endsWith(".tab"))
		else {
			throw new IOException("File " + filename
					+ " must have a '.tab' extension.");
		}
		String xmlFilename = filename.substring(0, filename.length() - 4)
				+ ".xml";

		BufferedReader dataReader = new BufferedReader(new FileReader(filename));
		// HideDataLoader does the work of parsing the input files
		// dataLoader = new HideDataLoader(createInput(xmlFilename),
		// createReader(filename), fileType, true);
		FileInputStream xmlInput = new FileInputStream(xmlFilename);

		HideDataLoader dataLoader = new HideDataLoader(xmlInput, dataReader,
				fileType, true);

		return dataLoader;
	}

	/**
	 * Constructor
	 * 
	 * @param xmlInputStream
	 *            .xml file
	 * @param dataReader
	 *            data file
	 * @param dataFileType
	 *            data file type
	 * @param ignoreFirstLineOfDataInput
	 *            whether to ignore the first line
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public HideDataLoader(InputStream xmlInputStream,
			BufferedReader dataReader, FileType dataFileType,
			boolean ignoreFirstLineOfDataInput) {
		this.xmlInputStream = xmlInputStream;
		this.dataReader = dataReader;
		this.dataFileType = dataFileType;
		this.ignoreFirstLineOfDataInput = ignoreFirstLineOfDataInput;
		this.loadData();
	}

	public void loadData() {
		DOMProcessor xml = new DOMProcessor(xmlInputStream);
		Node root = xml.getElements("hideData")[0];

		Node datasetNameNode = xml.getNodeElement("datasetName", root);
		if (datasetNameNode != null) {
			datasetName = xml.getNodeText(datasetNameNode).replace(" ", "");
		}

		// load the data variables
		Collection<DataField> datafields = new HashSet<DataField>();
		List<ObjectIntNumber<DataField>> hierVars = new ArrayList<ObjectIntNumber<DataField>>();

		// temp list of datafields keyed to the name of another data field for
		// "appliesTo" Need to do this, because not all datafields will be
		// loaded yet
		Map<DataField, String> appliesTo = new HashMap<DataField, String>();

		for (Node node : xml.getNodeElements("variable", root)) {
			try {
				String name = null;
				Node childNode = xml.getNodeElement("name", node);
				if (childNode != null) {
					name = xml.getNodeText(childNode);
					if (name.matches("[^A-Za-z0-9]")) {
						System.err
								.print("ERROR: Variable name "
										+ name
										+ " is invalid (must not contain spaces or non-alphanumeric characters).\n");
						throw new NullPointerException();
					}
				} else {
					System.err.print("ERROR: missing <name> in "
							+ node.getNodeName() + "\n");
					throw new NullPointerException();
				}

				int colIdx = -1;
				childNode = xml.getNodeElement("colIdx", node);
				if (childNode != null) {
					colIdx = Integer.parseInt(xml.getNodeText(childNode));
				} else {
					System.err.print("ERROR: missing <colIdx> for variable \""
							+ name + "\"in " + node.getNodeName() + "\n");
					throw new NullPointerException();
				}

				FieldType fieldType = FieldType.valueOf(xml.getNodeText(
						xml.getNodeElement("dataType", node)).toUpperCase());
				if (fieldType == null) {
					System.err
							.print("ERROR: missing or invalid <dataType> for variable \""
									+ name
									+ "\"in "
									+ node.getNodeName()
									+ "\n");
					throw new NullPointerException();
				}

				DataField dataField = new DataField(name, colIdx, fieldType);
				datafields.add(dataField);

				// check if there order information
				Node valuesNode = xml.getNodeElement("values", node);
				if (valuesNode != null) {
					int prevOrder = 0;
					List<ObjectIntNumber<Object>> valuesWithOrder = new ArrayList<ObjectIntNumber<Object>>();
					Node[] items = xml.getNodeElements("item", valuesNode);
					for (Node itemNode : items) {
						String[] valueArray = xml.getAttributes("value",
								itemNode);
						String[] orderArray = xml.getAttributes("order",
								itemNode);
						String[] relabelArray = xml.getAttributes("relabel",
								itemNode);
						int order = prevOrder;
						if (orderArray.length > 0) {
							order = Integer.parseInt(orderArray[0]);
						}
						if (valueArray.length == 1) {
							Object value = null;
							Object relabel = null;
							if (fieldType == FieldType.DOUBLE) {
								value = Double.parseDouble(valueArray[0]);
								if (relabelArray.length > 0) {
									relabel = Double
											.parseDouble(relabelArray[0]);
								}
							} else if (fieldType == FieldType.INT) {
								value = Integer.parseInt(valueArray[0]);
								if (relabelArray.length > 0) {
									relabel = Integer.parseInt(relabelArray[0]);
								}
							} else if (fieldType == FieldType.FLOAT) {
								value = Float.parseFloat(valueArray[0]);
								if (relabelArray.length > 0) {
									relabel = Float.parseFloat(relabelArray[0]);
								}
							} else if (fieldType == FieldType.LONG) {
								value = Long.parseLong(valueArray[0]);
								if (relabelArray.length > 0) {
									relabel = Long.parseLong(relabelArray[0]);
								}
							} else {
								value = valueArray[0];
								if (relabelArray.length > 0) {
									relabel = relabelArray[0];
								}
							}
							if (relabel != null) {
								dataField.setReLabel(value, relabel);
								value = relabel;
							}
							ObjectIntNumber<Object> objectIntNumber = new ObjectIntNumber<Object>(
									value, order);
							valuesWithOrder.add(objectIntNumber);
							prevOrder = order;
						} else {
							System.err.print("ERROR: an <item> in variable \""
									+ name + "\" needs a value.\n");
							throw new NullPointerException();
						}
					}
					Collections.sort(valuesWithOrder);
					List<Object> values = new ArrayList<Object>();
					for (ObjectIntNumber<Object> objectIntNumber : valuesWithOrder) {
						values.add(objectIntNumber.getObject());
					}
					dataField.setOrderedValues(values);
				}

				// //check there are colour values
				Node colorMapNode = xml.getNodeElement("colorMap", node);
				if (colorMapNode == null) {
					colorMapNode = xml.getNodeElement("colourMap", node);
				}
				readColorMapInfo(dataField, colorMapNode, xml);

				// //check if it only applies to a specific hier field
				Node appliesToNode = xml.getNodeElement("appliesToHier", node);
				if (appliesToNode != null) {
					appliesTo.put(dataField, appliesToNode.getTextContent());
				}

				// find out if a hierarchy variable
				int prevOrder = 0;
				childNode = xml.getNodeElement("hier", node);
				if (childNode != null) {
					String[] orderArray = xml.getAttributes("order", node);
					String[] alwaysAllArray = xml.getAttributes("useAllValues",
							node);
					int order = prevOrder;
					if (orderArray.length > 0) {
						order = Integer.parseInt(orderArray[0]);
					}
					if (alwaysAllArray.length > 0) {
						if (alwaysAllArray[0].equalsIgnoreCase("true")) {
							dataField.setUseAllValues(true);
						} else if (alwaysAllArray[0].equalsIgnoreCase("false")) {
							dataField.setUseAllValues(false);
						} else {

							System.err
									.println("useAllValues must be \"true\" or \"false\".");
						}

					}
					ObjectIntNumber<DataField> objectIntNumber;
					objectIntNumber = new ObjectIntNumber<DataField>(dataField,
							order);
					hierVars.add(objectIntNumber);
					prevOrder = order;
				}
			} catch (NullPointerException e) {
				System.err.println(e);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		data = new DataHide(datafields, dataReader, dataFileType,
				ignoreFirstLineOfDataInput);

		// set the appliesToHierNode
		for (DataField dataField : appliesTo.keySet()) {
			dataField.setAppliesToHierField(data.getDataField(appliesTo
					.get(dataField)));
		}

		// add values to *datafields* if no values currently set, in natural
		// order
		for (ObjectIntNumber<DataField> objectIntNumber : hierVars) {
			DataField dataField = objectIntNumber.getObject();
			if (dataField.getOrderValues() == null) {
				// find all unique value
				List<Comparable<Object>> values = new ArrayList<Comparable<Object>>();
				for (Record record : data.getRecords()) {
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
		// variable
		// where colour has not been allocated
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
		for (Node node : xml.getNodeElements("summary", root)) {
			try {
				String name = null;
				Node childNode = xml.getNodeElement("name", node);
				if (childNode != null) {
					name = xml.getNodeText(childNode);
					if (name.matches("[^A-Za-z0-9]")) {
						System.out
								.print("Variable name "
										+ name
										+ " is invalid (must not contain spaces or non-alphanumeric characters).");
						throw new NullPointerException();
					}
				} else {
					System.out.println("missing <name> in "
							+ node.getNodeName());
					throw new NullPointerException();
				}

				FieldType fieldType = null;
				childNode = xml.getNodeElement("dataType", node);
				if (childNode != null) {
					fieldType = FieldType
							.valueOf(xml.getNodeText(
									xml.getNodeElement("dataType", node))
									.toUpperCase());
				}

				String units = null;
				childNode = xml.getNodeElement("units", node);
				if (childNode != null) {
					units = xml.getNodeText(xml.getNodeElement("units", node));
				}

				SummariseField summariseField = null;
				// get summary type
				childNode = xml.getNodeElement("summaryType", node);
				if (childNode != null) {
					String summaryType = xml.getNodeText(childNode);

					// if *sum*
					if (summaryType.equals("sum")) {
						childNode = xml.getNodeElement("refVariable", node);
						String refDataFieldName = xml.getNodeText(childNode);
						DataField refDataField = data
								.getDataField(refDataFieldName);
						if (fieldType == null) {
							summariseField = new SummariseSum(name,
									refDataField);
						} else {
							summariseField = new SummariseSum(name,
									refDataField, fieldType);
						}
					}

					// if *mean*
					else if (summaryType.equals("mean")) {
						childNode = xml.getNodeElement("refVariable", node);
						String refDataFieldName = xml.getNodeText(childNode);
						DataField refDataField = data
								.getDataField(refDataFieldName);
						if (fieldType == null) {
							summariseField = new SummariseMean(name,
									refDataField);
						} else {
							summariseField = new SummariseMean(name,
									refDataField, fieldType);
						}
					}

					// if *count*
					else if (summaryType.equals("count")) {
						summariseField = new SummariseCount(name);
					}

					// if *uniqueCount*
					else if (summaryType.equals("uniqueCount")) {
						childNode = xml.getNodeElement("refVariable", node);
						String refDataFieldName = xml.getNodeText(childNode);
						DataField refDataField = data
								.getDataField(refDataFieldName);
						summariseField = new SummariseUniqueCount(name,
								refDataField);
					}

					// if *max*
					else if (summaryType.equals("max")) {
						childNode = xml.getNodeElement("refVariable", node);
						String refDataFieldName = xml.getNodeText(childNode);
						DataField refDataField = data
								.getDataField(refDataFieldName);
						if (fieldType == null) {
							summariseField = new SummariseMax(name,
									refDataField);
						} else {
							summariseField = new SummariseMax(name,
									refDataField, fieldType);
						}
					}

					// if *min*
					else if (summaryType.equals("min")) {
						childNode = xml.getNodeElement("refVariable", node);
						String refDataFieldName = xml.getNodeText(childNode);
						DataField refDataField = data
								.getDataField(refDataFieldName);
						if (fieldType == null) {
							summariseField = new SummariseMin(name,
									refDataField);
						} else {
							summariseField = new SummariseMin(name,
									refDataField, fieldType);
						}
					}

					// if *weightedMean*
					else if (summaryType.equals("weightedMean")) {
						childNode = xml.getNodeElement("refVariable", node);
						String refDataFieldName = xml.getNodeText(childNode);
						DataField refDataField = data
								.getDataField(refDataFieldName);
						childNode = xml.getNodeElement("refVariable2", node);
						String refDataFieldName2 = xml.getNodeText(childNode);
						DataField refDataField2 = data
								.getDataField(refDataFieldName2);
						if (fieldType == null) {
							summariseField = new SummariseWeightedMean(name,
									refDataField, refDataField2);
						} else {
							summariseField = new SummariseWeightedMean(name,
									refDataField, refDataField2, fieldType);
						}
					}

					// if *normalisedSum*
					else if (summaryType.equals("normalisedSum")) {
						childNode = xml.getNodeElement("refVariable", node);
						String refDataFieldName = xml.getNodeText(childNode);
						DataField refDataField = data
								.getDataField(refDataFieldName);
						childNode = xml.getNodeElement("refVariable2", node);
						String refDataFieldName2 = xml.getNodeText(childNode);
						DataField refDataField2 = data
								.getDataField(refDataFieldName2);
						if (fieldType == null) {
							summariseField = new SummariseNormalisedSum(name,
									refDataField, refDataField2);
						} else {
							summariseField = new SummariseNormalisedSum(name,
									refDataField, refDataField2, fieldType);
						}
					}

					// if *normalisedAvg*
					else if (summaryType.equals("normalisedMean")) {
						childNode = xml.getNodeElement("refVariable", node);
						String refDataFieldName = xml.getNodeText(childNode);
						DataField refDataField = data
								.getDataField(refDataFieldName);
						childNode = xml.getNodeElement("refVariable2", node);
						String refDataFieldName2 = xml.getNodeText(childNode);
						DataField refDataField2 = data
								.getDataField(refDataFieldName2);
						if (fieldType == null) {
							summariseField = new SummariseNormalisedMean(name,
									refDataField, refDataField2);
						} else {
							summariseField = new SummariseNormalisedMean(name,
									refDataField, refDataField2, fieldType);
						}
					}

					if (summariseField != null) {
						summariseFields.add(summariseField);
						summariseField.setUnits(units);
					}

					// see if colour is set
					Node colourNode = xml.getNodeElement("colorMap", node);
					if (colourNode == null) {
						colourNode = xml.getNodeElement("colourMap", node);
					}
					readColorMapInfo(summariseField, colourNode, xml);
				} else {
					System.out.println("missing <summaryType> in "
							+ node.getNodeName());
					throw new NullPointerException();
				}

				// find out if a order, size or colour variables
				childNode = xml.getNodeElement("order", node);
				if (childNode != null) {
					String[] attribs = xml.getAttributes("order", node);
					ObjectIntNumber<SummariseField> objectIntNumber;
					if (attribs.length > 0) {
						objectIntNumber = new ObjectIntNumber<SummariseField>(
								summariseField, Integer.parseInt(attribs[0]));
					} else {
						objectIntNumber = new ObjectIntNumber<SummariseField>(
								summariseField, 0);
					}
					orderVars.add(objectIntNumber);
				}
				childNode = xml.getNodeElement("size", node);
				if (childNode != null) {
					String[] attribs = xml.getAttributes("order", node);
					ObjectIntNumber<SummariseField> objectIntNumber;
					if (attribs.length > 0) {
						objectIntNumber = new ObjectIntNumber<SummariseField>(
								summariseField, Integer.parseInt(attribs[0]));
					} else {
						objectIntNumber = new ObjectIntNumber<SummariseField>(
								summariseField, 0);
					}
					sizeVars.add(objectIntNumber);
				}
				childNode = xml.getNodeElement("colour", node);
				if (childNode == null) {
					childNode = xml.getNodeElement("color", node);
				}
				if (childNode != null) {
					String[] attribs = xml.getAttributes("order", node);
					ObjectIntNumber<SummariseField> objectIntNumber;
					if (attribs.length > 0) {
						objectIntNumber = new ObjectIntNumber<SummariseField>(
								summariseField, Integer.parseInt(attribs[0]));
					} else {
						objectIntNumber = new ObjectIntNumber<SummariseField>(
								summariseField, 0);
					}
					colourVars.add(objectIntNumber);					
				}

//				orderVars.add(new ObjectIntNumber<SummariseField>(
//						summariseField, 0));
//				sizeVars.add(new ObjectIntNumber<SummariseField>(
//						summariseField, 0));
//				colourVars.add(new ObjectIntNumber<SummariseField>(
//						summariseField, 0));

			} catch (NullPointerException e) {
				System.err.println(e);
			}
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

	private void readColorMapInfo(Object field, Node colorMapNode,
			DOMProcessor xml) {
		if (colorMapNode == null) {
			return;
		}

		ColourTable colourTable = null;
		// check if there's a preset
		String[] presetArray = xml.getAttributes("preset", colorMapNode);
		if (presetArray.length > 0) {
			// use reflection to get the palette type
			try {
				colourTable = ColourTable
						.getPresetColourTable(ColourTable.class.getField(
								presetArray[0]).getInt(ColourTable.class));
				ColourScaling colourScaling = ColourScaling.LIN;
				String[] scalingArray = xml.getAttributes("scaling",
						colorMapNode);
				if (scalingArray.length > 0
						&& scalingArray[0].equalsIgnoreCase("log")) {
					colourScaling = ColourScaling.LOG;
				}
				colourScalings.put(field, colourScaling);

			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// else create an empty colour map
			colourTable = new ColourTable();
		}
		Node[] items = xml.getNodeElements("rule", colorMapNode);
		for (Node ruleNode : items) {
			String[] numValueArray = xml.getAttributes("numValue", ruleNode);
			String[] colorArray = xml.getAttributes("color", ruleNode);
			String[] typeArray = xml.getAttributes("type", ruleNode);
			boolean useContinuousColourRule = false;
			if (typeArray.length > 0) {
				if (typeArray[0].equals("continuous")) {
					useContinuousColourRule = true;
				} else if (typeArray[0].equals("discrete")) {
					useContinuousColourRule = false;
				} else {
					System.err
							.println("Colour rule type must be \"continuous\" or \"discrete\". Assuming \"discrete\".");
				}

			}
			if (numValueArray.length == 1 && colorArray.length == 1) {
				int numValue = Integer.parseInt(numValueArray[0]);
				String color = colorArray[0];
				if (color.startsWith("#")
						&& (color.length() == 7 || color.length() == 9)) {
					int r = Integer.parseInt(color.substring(1, 3), 16);
					int g = Integer.parseInt(color.substring(3, 5), 16);
					int b = Integer.parseInt(color.substring(5, 7), 16);
					int a = 255;
					if (color.length() == 9) {
						a = Integer.parseInt(color.substring(7, 9), 16);
					}
					if (useContinuousColourRule) {
						colourTable.addContinuousColourRule(numValue,
								new Color(r, g, b, a).getRGB());
					} else {
						colourTable.addDiscreteColourRule(numValue, new Color(
								r, g, b, a).getRGB());
					}
				} else {
					System.out
							.println("Colour must start with a # and by 7 or 9 characters long");
				}
			}
		}
		colours.put(field, colourTable);
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
