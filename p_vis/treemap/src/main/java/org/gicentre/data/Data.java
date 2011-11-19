package org.gicentre.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.gicentre.data.summary.SummariseField;
import org.gicentre.data.summary.SummariseNode;

/**
 * 
 * @author yulewei
 */
public class Data {

	private List<Record> lines = new ArrayList<Record>();

	/**
	 * 将读取数据和数据本身分离, 添加 by yulewei
	 * 
	 * @author yulewei
	 * 
	 * @param dataFields
	 * @param recordList
	 */
	public Data(List<DataField> dataFields, List<String[]> recordList) {

		for (int k = 0; k < recordList.size(); k++) {
			String[] line = recordList.get(k);
			Object[] values = parseLine(line, dataFields);
			lines.add(new Record(values));
		}

		// 更新列索引位置
		for (int i = 0; i < dataFields.size(); i++) {
			DataField dataField = dataFields.get(i);
			dataField.colIdx = i;
		}
	}

	public Object[] parseLine(String[] line, List<DataField> dataFields) {
		Object[] values = new Object[dataFields.size()];
		for (int i = 0; i < dataFields.size(); i++) {
			DataField dataField = dataFields.get(i);
			String v = line[dataField.colIdx];
			if (v != null) {
				switch (dataField.fieldType) {
				case LONG:
					values[i] = Long.parseLong(v);
					break;
				case INT:
					values[i] = Integer.parseInt(v);
					break;
				case FLOAT:
					values[i] = Float.parseFloat(v);
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

	/**
	 * Returns the records that satisfy a filter/query
	 * 
	 * @param dataFilter
	 * @return a collection of records
	 */
	public Collection<Record> getRecords(DataFilter dataFilter) {
		List<Record> records = new ArrayList<Record>();
		for (Record record : lines) {
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
		return lines;
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
	 * Summarises the contents as a tree of nodes, conditioning by the array of
	 * datafields supplied
	 * 
	 * @param hierFields
	 *            Array of conditioning datafields
	 * @param sumFields
	 *            Collection of summaryfields
	 * @param dataFilter
	 *            DataFilter
	 * @return The root node of a tree that summarises the data
	 */
	public SummariseNode getSummary(final DataField[] hierFields,
			Collection<SummariseField> sumFields, DataFilter dataFilter) {

		int numLevels = hierFields.length;

		if (numLevels == 0) {
			return new SummariseNode(null, null, 0, null, -1, -1,
					new HashMap<SummariseField, Object>());
		}

		// 排序, 以便按照索引分组
		Collections.sort(lines, new Comparator<Record>() {
			@SuppressWarnings("unchecked")
			@Override
			public int compare(Record o1, Record o2) {
				for (DataField field : hierFields) {
					Comparable<Object> v1 = (Comparable<Object>) o1
							.getValue(field);
					Comparable<Object> v2 = (Comparable<Object>) o2
							.getValue(field);

					int compareValue = v1.compareTo(v2);
					if (compareValue != 0) {
						return compareValue;
					}
				}
				return 0;
			}
		});

		List<Record> sortedRecords = new ArrayList<Record>();
		if (dataFilter == null) {
			sortedRecords.addAll(lines);
		} else {
			sortedRecords.addAll(getRecords(dataFilter));
		}
		if (sortedRecords.isEmpty()) {
			return null;
		}

		// 排序后, 计算每个分组的值和分组索引
		ArrayList<Object>[] curHierValues = new ArrayList[numLevels];
		ArrayList<Integer>[] curHierRowIdxs = new ArrayList[numLevels];
		for (int i = 0; i < numLevels; i++) {
			curHierValues[i] = new ArrayList<Object>();
			curHierRowIdxs[i] = new ArrayList<Integer>();
		}

		int rowIdx = 0;
		Object[] prevGroupByValues = new Object[numLevels];
		for (Record record : sortedRecords) {
			Object[] curGroupByValues = new Object[numLevels];
			boolean rollAllLevel = false;
			for (int i = 0; i < numLevels; i++) {
				curGroupByValues[i] = record.getValue(hierFields[i]);
				if (rowIdx == 0
						|| rollAllLevel
						|| (prevGroupByValues[i] != null && !prevGroupByValues[i]
								.equals(curGroupByValues[i]))) {
					rollAllLevel = true;
					curHierValues[i].add(curGroupByValues[i]);
					curHierRowIdxs[i].add(rowIdx);
				}
			}
			rowIdx++;
			prevGroupByValues = curGroupByValues;
		}
		for (int i = 0; i < curHierRowIdxs.length; i++) {
			curHierRowIdxs[i].add(rowIdx);
		}

		// 以分组的方式计算全部SummariseNode, 构建节点树
		int[] rollUpIdxs = new int[numLevels];
		int curDepth = numLevels - 1;
		HashSet<SummariseNode>[] nodesToAdd = new HashSet[hierFields.length];
		for (int i = 0; i < hierFields.length; i++) {
			nodesToAdd[i] = new HashSet<SummariseNode>();
		}

		while (true) {
			if (curDepth > 0) {
				int index1 = curHierRowIdxs[curDepth - 1]
						.get(rollUpIdxs[curDepth - 1] + 1);
				int index2 = curHierRowIdxs[curDepth].get(rollUpIdxs[curDepth]);
				if (index1 == index2) {
					curDepth--;
				} else {
					curDepth = numLevels - 1;
				}
			} else {
				curDepth = numLevels - 1;
			}

			// 计算分组的Summarise结果
			int startRowIdx = curHierRowIdxs[curDepth]
					.get(rollUpIdxs[curDepth]);
			int endRowIdx = curHierRowIdxs[curDepth]
					.get(rollUpIdxs[curDepth] + 1) - 1;

			HashMap<SummariseField, Object> summaryValues = new HashMap<SummariseField, Object>();

			for (SummariseField summaryField : sumFields) {
				Object value = summaryField.compute(sortedRecords.subList(
						startRowIdx, endRowIdx + 1));
				summaryValues.put(summaryField, value);
			}

			// 计算order
			int order = startRowIdx;
			Object groupByValue = curHierValues[curDepth]
					.get(rollUpIdxs[curDepth]);
			List<Object> orderedValues = hierFields[curDepth].getOrderValues();
			if (orderedValues != null) {
				order = orderedValues.indexOf(groupByValue);
			}

			SummariseNode summaryNode = new SummariseNode(hierFields[curDepth],
					groupByValue, order, sortedRecords, startRowIdx, endRowIdx,
					summaryValues);

			if (curDepth < numLevels - 1) {
				for (SummariseNode node : nodesToAdd[curDepth + 1]) {
					summaryNode.add(node);
				}
				nodesToAdd[curDepth + 1].clear();
			}

			nodesToAdd[curDepth].add(summaryNode);

			rollUpIdxs[curDepth]++;

			if (curDepth == 0
					&& rollUpIdxs[curDepth] >= curHierRowIdxs[curDepth].size() - 1)
				break;
		}

		// 计算根节点
		HashMap<SummariseField, Object> summaryValues = new HashMap<SummariseField, Object>();
		for (SummariseField field : sumFields) {
			Object value = field.compute(sortedRecords);
			summaryValues.put(field, value);
		}

		SummariseNode root = new SummariseNode(null, null, 0, sortedRecords, 0,
				lines.size() - 1, summaryValues);
		for (SummariseNode node : nodesToAdd[0]) {
			root.add(node);
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
	public class Record {
		public Object[] values;

		public Record(Object[] values) {
			this.values = values;
		}

		public Object getValue(DataField dataField) {
			return values[dataField.getColIdx()];
		}

		public String toString() {
			return Arrays.toString(values);
		}
	}
}
