package org.gicentre.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gicentre.data.summary.SummariseField;
import org.gicentre.data.summary.SummariseNode;

/**
 * Class to store a data file Note that NODATA values are stored and returned as
 * NULLs, so code needs to be able to deal with this
 * 
 * @author Aidan Slingsby, giCentre
 * @author yulewei
 */
public class Data {

	private HashMap<String, DataField> variables = new HashMap<String, DataField>();
	private ArrayList<Record> lines = new ArrayList<Record>();
	private int[] currentColumnSort = null;
	private ValueComparator valueComparator = new ValueComparator();

	private HashMap<String, Integer> valuesTreatedAsNoData = new HashMap<String, Integer>();

	/**
	 * 将读取数据和数据本身分离, 添加 by yulewei
	 * 
	 * @author yulewei
	 * 
	 * @param dataFields
	 * @param recordList
	 */
	public Data(Collection<DataField> dataFields, List<String[]> recordList) {
		Iterator<DataField> it = dataFields.iterator();
		while (it.hasNext()) {
			DataField dataField = it.next();
			this.variables.put(dataField.name, dataField);
		}

		HashMap<DataField, Integer> dataField2OriginalColIdx = new LinkedHashMap<DataField, Integer>();

		int i = 0;
		Iterator<DataField> it1 = dataFields.iterator();
		while (it1.hasNext()) {
			DataField dataField = it1.next();
			dataField2OriginalColIdx.put(dataField, dataField.colIdx);
			dataField.colIdx = i;// set the datafield's idx to the new one
			i++;
		}

		for (int k = 0; k < recordList.size(); k++) {
			String[] line = recordList.get(k);
			lines.add(new Record(Arrays.asList(line), dataField2OriginalColIdx));
		}
	}

	/**
	 * Sorts the records according to the values in the column indexes in
	 * currentColumnSort
	 * 
	 * @param csvVariables
	 */
	private void sort(DataField[] csvVariables) {
		this.currentColumnSort = new int[csvVariables.length];
		for (int i = 0; i < csvVariables.length; i++) {
			currentColumnSort[i] = csvVariables[i].getColIdx();
		}
		Collections.sort(lines, valueComparator);
	}

	/**
	 * Return a DataField object from its name
	 * 
	 * @param name
	 * @return datafield object
	 */
	public DataField getDataField(String name) {
		DataField dataField = this.variables.get(name);
		if (dataField == null) {
			System.err.println("Variable \"" + name + "\" not found");
		}
		return dataField;
	}

	/**
	 * Returns the records that satisfy a filter/query
	 * 
	 * @param dataFilter
	 * @return a collection of records
	 */
	public Collection<Record> getRecords(DataFilter dataFilter) {
		ArrayList<Record> records = new ArrayList<Record>();
		Iterator<Record> it = this.lines.iterator();
		while (it.hasNext()) {
			Record record = it.next();
			if (dataFilter.matches(record)) {
				records.add(record);
			}
		}
		return records;
	}

	/**
	 * Returns all the records
	 * 
	 * @param dataFilter
	 * @return a collection of records
	 */
	public Collection<Record> getRecords() {
		return this.lines;
	}

	/**
	 * Returns the number of records
	 * 
	 * @return
	 */
	public int size() {
		return this.lines.size();
	}

	public void display() {
		Iterator<Record> it = lines.iterator();
		while (it.hasNext()) {
			Record record = it.next();
			for (Object tok : record.values) {
				System.out.print(tok + ",");
			}
			System.out.println();
		}
	}

	/**
	 * Summarises the contents as a tree of nodes, conditioning by the array of
	 * datafields supplied
	 * 
	 * @param hierarchyFields
	 *            Array of conditioning datafields
	 * @param summariseFields
	 *            Collection of summaryfields
	 * @param dataFilter
	 *            DataFilter
	 * @return The root node of a tree that summarises the data
	 */
	public SummariseNode getSummary(DataField[] hierarchyFields,
			Collection<SummariseField> summariseFields) {
		return this.getSummary(hierarchyFields, summariseFields, null);
	}

	/**
	 * Override to transform a string to one in a set format (e.g. removing all
	 * spaces)
	 * 
	 * @return
	 */
	public String transformToValidString(String value) {
		return value;
	}

	/**
	 * Summarises the contents as a tree of nodes, conditioning by the array of
	 * datafields supplied
	 * 
	 * @param hierarchyFields
	 *            Array of conditioning datafields
	 * @param summariseFields
	 *            Collection of summaryfields
	 * @param dataFilter
	 *            DataFilter
	 * @return The root node of a tree that summarises the data
	 */
	public SummariseNode getSummary(DataField[] hierarchyFields,
			Collection<SummariseField> summariseFields, DataFilter dataFilter) {

		int numLevels = hierarchyFields.length;

		if (numLevels == 0) {
			return new SummariseNode(null, null, 0, null, -1, -1,
					new HashMap<SummariseField, Object>());
		}

		ArrayList<Object>[] curHierarchyValues = new ArrayList[numLevels];
		ArrayList<Integer>[] curHierarchyValueRowIdxs = new ArrayList[numLevels];

		// HashMap<DataField,Object[]> field2ColValues;
		List<Record> sortedRecords;

		// Find all columns in use for the summarising
		{

			sort(hierarchyFields);

			curHierarchyValues = new ArrayList[numLevels];
			curHierarchyValueRowIdxs = new ArrayList[numLevels];
			for (int i = 0; i < numLevels; i++) {
				curHierarchyValues[i] = new ArrayList<Object>();
				curHierarchyValueRowIdxs[i] = new ArrayList<Integer>();
			}

			Object[] prevGroupByValues = new Object[numLevels];
			// field2ColValues = new HashMap<DataField, Object[]>();
			// Iterator<DataField> it2 = dependentDataFields.iterator();
			// while (it2.hasNext()){
			// field2ColValues.put(it2.next(),new Object[lines.size()]);
			// }
			sortedRecords = new ArrayList<Record>();
			if (dataFilter == null) {
				sortedRecords.addAll(lines);
			} else {
				sortedRecords.addAll(getRecords(dataFilter));
			}

			if (sortedRecords.isEmpty()) {
				return null;
			}

			int rowIdx = 0;
			Iterator<Record> it1 = sortedRecords.iterator();
			while (it1.hasNext()) {
				Record csvRecord = it1.next();
				Object[] curGroupByValues = new Object[numLevels];
				boolean rollAll = false;
				for (int i = 0; i < numLevels; i++) {
					curGroupByValues[i] = csvRecord
							.getValue(hierarchyFields[i]);

					if ((rollAll
							|| (curGroupByValues[i] != null && !curGroupByValues[i]
									.equals(prevGroupByValues[i])) || (prevGroupByValues[i] != null && !prevGroupByValues[i]
							.equals(curGroupByValues[i]))) || rowIdx == 0) {
						rollAll = true;
						curHierarchyValues[i].add(curGroupByValues[i]);
						curHierarchyValueRowIdxs[i].add(rowIdx);
					}
					// it2 = dependentDataFields.iterator();
					// while (it2.hasNext()){
					// DataField dataField=it2.next();
					// field2ColValues.get(dataField)[rowIdx]=csvRecord.getValue(dataField);
					// }
				}
				rowIdx++;
				prevGroupByValues = curGroupByValues;
			}
			for (int i = 0; i < curHierarchyValueRowIdxs.length; i++) {
				curHierarchyValueRowIdxs[i].add(rowIdx);
			}

		}

		int[] rollUpIdxs = new int[numLevels];
		int curRollupDepth = numLevels - 1;

		HashSet<SummariseNode> nodesToAdd[] = new HashSet[hierarchyFields.length];
		for (int i = 0; i < hierarchyFields.length; i++) {
			nodesToAdd[i] = new HashSet<SummariseNode>();
		}

		do {
			if (curRollupDepth > 0) {
				if (curHierarchyValueRowIdxs[curRollupDepth - 1].get(
						rollUpIdxs[curRollupDepth - 1] + 1).equals(
						curHierarchyValueRowIdxs[curRollupDepth]
								.get(rollUpIdxs[curRollupDepth]))) {
					curRollupDepth--;
				} else {
					curRollupDepth = numLevels - 1;
				}
			} else {
				curRollupDepth = numLevels - 1;
			}

			int startRowIdx = curHierarchyValueRowIdxs[curRollupDepth]
					.get(rollUpIdxs[curRollupDepth]);
			int endRowIdx = curHierarchyValueRowIdxs[curRollupDepth]
					.get(rollUpIdxs[curRollupDepth] + 1) - 1;

			HashMap<SummariseField, Object> summaryValues = new HashMap<SummariseField, Object>();
			Iterator<SummariseField> it = summariseFields.iterator();
			while (it.hasNext()) {
				SummariseField summaryField = it.next();
				Object value = summaryField.compute(sortedRecords.subList(
						startRowIdx, endRowIdx + 1));
				summaryValues.put(summaryField, value);
			}
			// Set the order
			int order = startRowIdx;
			if (hierarchyFields[curRollupDepth].getOrderValues() != null) {
				// then need to replace with the order in the datafield
				order = hierarchyFields[curRollupDepth].getOrderValues()
						.indexOf(
								curHierarchyValues[curRollupDepth]
										.get(rollUpIdxs[curRollupDepth]));
			}

			SummariseNode summaryNode = new SummariseNode(
					hierarchyFields[curRollupDepth],
					curHierarchyValues[curRollupDepth]
							.get(rollUpIdxs[curRollupDepth]), order,
					sortedRecords, startRowIdx, endRowIdx, summaryValues);

			if (curRollupDepth < numLevels - 1) {
				Iterator<SummariseNode> it1 = nodesToAdd[curRollupDepth + 1]
						.iterator();
				while (it1.hasNext()) {
					summaryNode.add(it1.next());
				}
				nodesToAdd[curRollupDepth + 1].clear();
			}

			nodesToAdd[curRollupDepth].add(summaryNode);

			rollUpIdxs[curRollupDepth]++;

		} while (!(rollUpIdxs[curRollupDepth] >= curHierarchyValueRowIdxs[curRollupDepth]
				.size() - 1 && curRollupDepth == 0));

		HashMap<SummariseField, Object> summaryValues = new HashMap<SummariseField, Object>();
		Iterator<SummariseField> it = summariseFields.iterator();
		while (it.hasNext()) {
			SummariseField summaryField = it.next();
			Object value = summaryField.compute(sortedRecords);
			summaryValues.put(summaryField, value);
		}
		SummariseNode root = new SummariseNode(null, null, 0, sortedRecords, 0,
				lines.size() - 1, summaryValues);

		Iterator<SummariseNode> it1 = nodesToAdd[0].iterator();
		while (it1.hasNext()) {
			root.add(it1.next());
		}

		return root;
	}

	/**
	 * Stores a record as an inner class NO DATA is stored as nulls, so need to
	 * check this when using Data are stored as specified by each datafield. Can
	 * be retrieved in any format (as long as it can be converted)
	 * 
	 * @author Aidan Slingsby
	 * 
	 */
	public class Record implements Comparable<Record> {
		private Object[] values;

		/**
		 * Constructor is private - only the Data class can create records
		 * 
		 * @param values
		 * @param datafield2OriginalPosition
		 *            HashMap gives the original column number for each column
		 */
		private Record(List<String> values,
				Map<DataField, Integer> datafield2OriginalPosition) {
			this.values = new Object[datafield2OriginalPosition.size()];
			Iterator<Entry<DataField, Integer>> it = datafield2OriginalPosition
					.entrySet().iterator();
			while (it.hasNext()) {
				Entry<DataField, Integer> entry = it.next();
				DataField dataField = entry.getKey();
				int originalPosition = entry.getValue();
				String v = null;
				if (values.size() > originalPosition) {
					v = values.get(originalPosition);
				}
				if (dataField.relabels.containsKey(v)) {
					v = dataField.relabels.get(v).toString();
				}

				if (v != null) {
					if (dataField != null) {
						if (dataField.fieldType == FieldType.LONG) {
							try {
								this.values[dataField.colIdx] = Long
										.parseLong(v);
							} catch (NumberFormatException e) {
								String vString = v.toString();
								Integer count = valuesTreatedAsNoData
										.get(vString);
								if (count == null) {
									count = 0;
								}
								count++;
								valuesTreatedAsNoData.put(vString, count);
							}
						} else if (dataField.fieldType == FieldType.INT) {
							try {
								this.values[dataField.colIdx] = Integer
										.parseInt(v);
							} catch (NumberFormatException e) {
								String vString = v.toString();
								Integer count = valuesTreatedAsNoData
										.get(vString);
								if (count == null) {
									count = 0;
								}
								count++;
								valuesTreatedAsNoData.put(vString, count);
							}
						} else if (dataField.fieldType == FieldType.FLOAT) {
							try {
								this.values[dataField.colIdx] = Float
										.parseFloat(v);
							} catch (NumberFormatException e) {
								String vString = v.toString();
								Integer count = valuesTreatedAsNoData
										.get(vString);
								if (count == null) {
									count = 0;
								}
								count++;
								valuesTreatedAsNoData.put(vString, count);
							}
						} else if (dataField.fieldType == FieldType.DOUBLE) {
							try {
								this.values[dataField.colIdx] = Double
										.parseDouble(v);
							} catch (NumberFormatException e) {
								String vString = v.toString();
								Integer count = valuesTreatedAsNoData
										.get(vString);
								if (count == null) {
									count = 0;
								}
								count++;
								valuesTreatedAsNoData.put(vString, count);
							}
						} else if (dataField.fieldType == FieldType.STRING) {
							v = transformToValidString(v);
							if (!v.equals("")) {
								this.values[dataField.colIdx] = v.toString()
										.intern();
							} else {
								String vString = v.toString();
								Integer count = valuesTreatedAsNoData
										.get(vString);
								if (count == null) {
									count = 0;
								}
								count++;
								valuesTreatedAsNoData.put(vString, count);
							}
						}
					}
				}
			}
		}

		/**
		 * Returns the value of a datafield as an Object
		 * 
		 * @param dataField
		 * @return
		 */

		public Object getValue(DataField dataField) {
			return values[dataField.getColIdx()];
		}

		/**
		 * Returns the value of a datafield as an integer. Will convert if it
		 * has to, if compatible.
		 * 
		 * @param dataField
		 * @return
		 */
		public Integer getValueAsInt(DataField dataField) {
			Object v = values[dataField.getColIdx()];
			if (v instanceof Integer) {
				return (Integer) v;
			}
			try {
				return ((Number) v).intValue();
			} catch (NumberFormatException e) {
				return null;
			} catch (NullPointerException e) {
				return null;
			}
		}

		/**
		 * Returns the value of a datafield as a float Will convert if it has
		 * to, if compatible.
		 * 
		 * @param dataField
		 * @return
		 */
		public Float getValueAsFloat(DataField dataField) {
			Object v = values[dataField.getColIdx()];
			if (v instanceof Float) {
				return (Float) v;
			}
			try {
				return ((Number) v).floatValue();
			} catch (NumberFormatException e) {
				return null;
			} catch (NullPointerException e) {
				return null;
			}
		}

		/**
		 * Returns the value of a datafield as a double Will convert if it has
		 * to, if compatible.
		 * 
		 * @param dataField
		 * @return
		 */
		public Double getValueAsDouble(DataField dataField) {
			Object v = values[dataField.getColIdx()];
			if (v instanceof Double) {
				return (Double) v;
			}
			try {
				return ((Number) v).doubleValue();
			} catch (NumberFormatException e) {
				return null;
			} catch (NullPointerException e) {
				return null;
			}
		}

		/**
		 * Returns the value of a datafield as a long Will convert if it has to,
		 * if compatible.
		 * 
		 * @param dataField
		 * @return
		 */
		public Long getValueAsLong(DataField dataField) {
			Object v = values[dataField.getColIdx()];
			if (v instanceof Long) {
				return (Long) v;
			}
			try {
				return ((Number) v).longValue();
			} catch (NumberFormatException e) {
				return null;
			} catch (NullPointerException e) {
				return null;
			}
		}

		/**
		 * Returns the value of a datafield as a string Will convert if it has
		 * to, if compatible.
		 * 
		 * @param dataField
		 * @return
		 */
		public String getValueAsString(DataField dataField) {
			Object v = values[dataField.getColIdx()];
			try {
				return v.toString();
			} catch (NullPointerException e) {
				return null;
			}
		}

		/**
		 * Returns the value of a datafield as an integer. Will convert if it
		 * has to, if compatible.
		 * 
		 * @param dataField
		 * @return
		 */
		public Integer getValueAsInt(String dataField) {
			return getValueAsInt(getDataField(dataField));
		}

		/**
		 * Returns the value of a datafield as a float Will convert if it has
		 * to, if compatible.
		 * 
		 * @param dataField
		 * @return
		 */
		public Float getValueAsFloat(String dataField) {
			return getValueAsFloat(getDataField(dataField));
		}

		/**
		 * Returns the value of a datafield as a double Will convert if it has
		 * to, if compatible.
		 * 
		 * @param dataField
		 * @return
		 */
		public Double getValueAsDouble(String dataField) {
			return getValueAsDouble(getDataField(dataField));
		}

		/**
		 * Returns the value of a datafield as a long Will convert if it has to,
		 * if compatible.
		 * 
		 * @param dataField
		 * @return
		 */
		public Long getValueAsLong(String dataField) {
			return getValueAsLong(getDataField(dataField));
		}

		/**
		 * Returns the value of a datafield as a string Will convert if it has
		 * to, if compatible.
		 * 
		 * @param dataField
		 * @return
		 */
		public String getValueAsString(String dataField) {
			return getValueAsString(getDataField(dataField));
		}

		public int compareTo(Record o) {
			if (currentColumnSort == null) {
				return 0;
			} else {
				for (int i = 0; i < currentColumnSort.length; i++) {
					int compareValue = valueComparator.compare(
							this.values[currentColumnSort[i]],
							o.values[currentColumnSort[i]]);
					if (compareValue != 0) {
						return compareValue;
					}
				}
			}
			return 0;
		}

		/**
		 * Set value Automatically converted to the correct type for the field
		 * 
		 * @param dataField
		 * @param value
		 */
		public void setValue(DataField dataField, Number value) {
			if (value == null) {
				this.values[dataField.colIdx] = null;
			} else if (dataField.getFieldType() == FieldType.INT) {
				this.values[dataField.colIdx] = value.intValue();
			} else if (dataField.getFieldType() == FieldType.FLOAT) {
				this.values[dataField.colIdx] = value.floatValue();
			} else if (dataField.getFieldType() == FieldType.DOUBLE) {
				this.values[dataField.colIdx] = value.doubleValue();
			} else if (dataField.getFieldType() == FieldType.LONG) {
				this.values[dataField.colIdx] = value.longValue();
			} else if (dataField.getFieldType() == FieldType.STRING) {
				this.values[dataField.colIdx] = value.toString();
			}
		}

		/**
		 * Set value Automatically converted to the correct type for the field
		 * 
		 * @param dataField
		 * @param value
		 */
		public void setValue(DataField dataField, String value) {
			if (value == null) {
				this.values[dataField.colIdx] = null;
			} else if (dataField.getFieldType() == FieldType.INT) {
				this.values[dataField.colIdx] = Integer.parseInt(value);
			} else if (dataField.getFieldType() == FieldType.FLOAT) {
				this.values[dataField.colIdx] = Float.parseFloat(value);
			} else if (dataField.getFieldType() == FieldType.DOUBLE) {
				this.values[dataField.colIdx] = Double.parseDouble(value);
			} else if (dataField.getFieldType() == FieldType.LONG) {
				this.values[dataField.colIdx] = Long.parseLong(value);
			} else if (dataField.getFieldType() == FieldType.STRING) {
				this.values[dataField.colIdx] = value;
			}
		}

		/**
		 * Set value to NODATA Automatically converted to the correct type for
		 * the field
		 * 
		 * @param dataField
		 * @param value
		 */
		public void setValueNoData(DataField dataField) {
			this.values[dataField.colIdx] = null;
		}

		/**
		 * Set value Automatically converted to the correct type for the field
		 * 
		 * @param dataField
		 * @param value
		 */
		public void setValue(String dataField, Number value) {
			setValue(getDataField(dataField), value);
		}

		/**
		 * Set value Automatically converted to the correct type for the field
		 * 
		 * @param dataField
		 * @param value
		 */
		public void setValue(String dataField, String value) {
			setValue(getDataField(dataField), value);
		}

		/**
		 * Set value to NODATA Automatically converted to the correct type for
		 * the field
		 * 
		 * @param dataField
		 * @param value
		 */
		public void setValueNoData(String dataField) {
			setValueNoData(getDataField(dataField));
		}

	}

	/**
	 * a comparator to compare all types of values
	 * 
	 * @author Aidan Slingsby
	 * 
	 */
	class ValueComparator implements Comparator<Object> {

		public int compare(Object o1, Object o2) {
			if (o1 == null && o2 == null) {
				return 0;
			} else if (o1 == null) {
				return 1;
			} else if (o2 == null) {
				return -1;
			} else {
				return ((Comparable<Object>) o1)
						.compareTo((Comparable<Object>) o2);
			}
		}

	}

}
