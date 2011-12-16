package edu.zjut.treemap.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import javax.swing.tree.DefaultMutableTreeNode;

import edu.zjut.common.data.attr.DataField;
import edu.zjut.treemap.summary.SummariseField;

public class SummariseNode extends DefaultMutableTreeNode implements
		Comparable<SummariseNode> {

	private HashMap<SummariseField, Object> summariseField2Value;
	private DataField conditioningField;
	protected Object conditioningValue;
	private List<Object[]> sortedRecords;
	private int startRowIdx;
	private int endRowIdx;
	private int order = 0;

	/**
	 * 
	 * @param dataField
	 * @param groupByValue
	 * @param order
	 * @param indivDataValues
	 * @param startIdx
	 * @param endIdx
	 * @param summaryField2Value
	 */
	public SummariseNode(DataField dataField, Object groupByValue, int order,
			List<Object[]> sortedRecords, int startIdx, int endIdx,
			HashMap<SummariseField, Object> summaryField2Value) {
		this.conditioningField = dataField;
		this.conditioningValue = groupByValue;
		this.sortedRecords = sortedRecords;
		this.startRowIdx = startIdx;
		this.endRowIdx = endIdx;
		this.summariseField2Value = summaryField2Value;
		this.order = order;
	}

	// sorted order
	public List<SummariseNode> getChildren() {
		List<SummariseNode> list = new ArrayList<SummariseNode>();
		Enumeration<DefaultMutableTreeNode> e = this.children();
		while (e.hasMoreElements()) {
			list.add((SummariseNode) e.nextElement());
		}
		Collections.sort(list);
		return list;
	}

	/**
	 * Gets the path id A string that's the variable name, colon, value for each
	 * hierarchical level separated by /. Root is /
	 * 
	 * @return
	 */
	public String getPathId() {
		if (this.isRoot()) {
			return "/";
		} else {
			String path = "";
			SummariseNode node = this;
			while (!node.isRoot()) {
				path = node.conditioningField.getName() + ":"
						+ node.conditioningValue + "/" + path;
				node = (SummariseNode) node.parent;
			}
			path = "/" + path;
			return path;
		}
	}

	/**
	 * Returns the node that matches pathId in this subtree
	 * 
	 * @param pathId
	 * @return
	 */
	public SummariseNode getNode(String pathId) {
		Iterator<SummariseNode> it = this.iterator();
		while (it.hasNext()) {
			SummariseNode node = it.next();
			if (node.getPathId().equals(pathId)) {
				return node;
			}
		}
		return null;
	}

	/**
	 * Get's the node's hierarchical level -1 for the root
	 * 
	 * @return level
	 */
	public int getLevel() {
		int level = -1;
		SummariseNode node = this;
		SummariseNode parent = (SummariseNode) this.parent;
		while (node.parent != null) {
			node = (SummariseNode) parent;
			parent = (SummariseNode) node.parent;
			level++;
		}
		return level;
	}

	/**
	 * Returns the natural order
	 * 
	 * @return
	 */
	public int getNaturalOrder() {
		return this.order;
	}

	/**
	 * Returns the conditioning field
	 * 
	 * @return
	 */
	public DataField getConditioningField() {
		return this.conditioningField;
	}

	/**
	 * Returns the conditioning value as a String
	 * 
	 * @return (note: null for no data)
	 */
	public String getConditioningValueAsString() {
		try {
			return conditioningValue.toString();
		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * Returns the conditioning value as Integer
	 * 
	 * @return (note: null for no data)
	 */
	public Integer getConditioningValueAsInt() {
		try {
			return ((Number) conditioningValue).intValue();
		} catch (NumberFormatException e) {
			return null;
		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * Returns the conditioning value as Long
	 * 
	 * @return (note: null for no data)
	 */
	public Long getConditioningValueAsLong() {
		try {
			return ((Number) conditioningValue).longValue();
		} catch (NumberFormatException e) {
			return null;
		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * Returns the conditioning value as a Float
	 * 
	 * @return (note: null for no data)
	 */
	public Float getConditioningValueAsFloat() {
		try {
			if (conditioningValue instanceof Number) {
				return ((Number) conditioningValue).floatValue();
			} else {
				return null;
			}
		} catch (NumberFormatException e) {
			return null;
		} catch (NullPointerException e) {
			// no data
			return null;
		}
	}

	/**
	 * Returns the conditioning value as a Double
	 * 
	 * @return (note: null for no data)
	 */
	public Double getConditioningValueDouble() {
		try {
			return ((Number) conditioningValue).doubleValue();
		} catch (NumberFormatException e) {
			return null;
		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * Returns the conditioning value
	 * 
	 * @return Will be null, String, Float, Double, Long or Integer (according
	 *         to the datatype)
	 */
	public Object getConditioningValue() {
		return conditioningValue;
	}

	/**
	 * Get the value of the specified summary field as a string
	 * 
	 * @param summaryField
	 * @return
	 */
	public String getSummaryAsString(SummariseField summaryField) {
		try {
			return summariseField2Value.get(summaryField).toString();
		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * Get the value of the specified summary field as an Integer
	 * 
	 * @param summaryField
	 * @return
	 */
	public Integer getSummaryAsInt(SummariseField summaryField) {
		try {
			return ((Number) summariseField2Value.get(summaryField)).intValue();
		} catch (NumberFormatException e) {
			System.out.println(e.getStackTrace());
			return 0;
		} catch (NullPointerException e) {
			// NO DATA
			return null;
		}
	}

	/**
	 * Get the value of the specified summary field as a Long
	 * 
	 * @param summaryField
	 * @return
	 */
	public Long getSummaryAsLong(SummariseField summaryField) {
		try {
			return ((Number) summariseField2Value.get(summaryField))
					.longValue();
		} catch (NumberFormatException e) {
			System.out.println(e.getStackTrace());
			return 0l;
		} catch (NullPointerException e) {
			// NO DATA
			return null;
		}
	}

	/**
	 * Get the value of the specified summary field as a Float
	 * 
	 * @param summaryField
	 * @return
	 */
	public Float getSummaryAsFloat(SummariseField summaryField) {
		try {
			return ((Number) summariseField2Value.get(summaryField))
					.floatValue();
		} catch (NumberFormatException e) {
			System.out.println(e.getStackTrace());
			return 0f;
		} catch (NullPointerException e) {
			// NO DATA
			return null;
		}
	}

	/**
	 * Get the value of the specified summary field as a Double
	 * 
	 * @param summaryField
	 * @return
	 */
	public Double getSummaryAsDouble(SummariseField summaryField) {
		try {
			return ((Number) summariseField2Value.get(summaryField))
					.doubleValue();
		} catch (NumberFormatException e) {
			System.out.println(e.getStackTrace());
			return 0d;
		} catch (NullPointerException e) {
			// NO DATA
			return null;
		}
	}

	/**
	 * Get the value of the specified summary field as a string (slightly less
	 * efficient then passing the SummariseField directly)
	 * 
	 * @param summaryField
	 * @return
	 */
	public String getSummaryAsString(String summaryField) {
		SummariseField summaryField1 = this.getSummaryField(summaryField);
		try {
			return summariseField2Value.get(summaryField1).toString();
		} catch (NullPointerException e) {
			// NO DATA
			return null;
		}
	}

	/**
	 * Get the value of the specified summary field as an Integer (slightly less
	 * efficient then passing the SummariseField directly)
	 * 
	 * @param summaryField
	 * @return
	 */
	public Integer getSummaryAsInt(String summaryField) {
		SummariseField summaryField1 = this.getSummaryField(summaryField);
		try {
			return ((Number) summariseField2Value.get(summaryField1))
					.intValue();
		} catch (NumberFormatException e) {
			System.out.println(e.getStackTrace());
			return 0;
		} catch (NullPointerException e) {
			// NO DATA
			return null;
		}
	}

	/**
	 * Get the value of the specified summary field as an Integer (slightly less
	 * efficient then passing the SummariseField directly)
	 * 
	 * @param summaryField
	 * @return
	 */
	public Long getSummaryAsLong(String summaryField) {
		SummariseField summaryField1 = this.getSummaryField(summaryField);
		try {
			return ((Number) summariseField2Value.get(summaryField1))
					.longValue();
		} catch (NumberFormatException e) {
			System.out.println(e.getStackTrace());
			return 0l;
		} catch (NullPointerException e) {
			// NO DATA
			return null;
		}
	}

	/**
	 * Get the value of the specified summary field as a Float (slightly less
	 * efficient then passing the SummariseField directly)
	 * 
	 * @param summaryField
	 * @return
	 */
	public Float getSummaryAsFloat(String summaryField) {
		SummariseField summaryField1 = this.getSummaryField(summaryField);
		try {
			return ((Number) summariseField2Value.get(summaryField1))
					.floatValue();
		} catch (NumberFormatException e) {
			System.out.println(e.getStackTrace());
			return 0f;
		} catch (NullPointerException e) {
			// NO DATA
			return null;
		}
	}

	/**
	 * Get the value of the specified summary field as a Double (slightly less
	 * efficient then passing the SummariseField directly)
	 * 
	 * @param summaryField
	 * @return
	 */
	public Double getSummaryAsDouble(String summaryField) {
		SummariseField summaryField1 = this.getSummaryField(summaryField);
		try {
			return ((Number) summariseField2Value.get(summaryField1))
					.doubleValue();
		} catch (NumberFormatException e) {
			System.out.println(e.getStackTrace());
			return 0d;
		} catch (NullPointerException e) {
			// NO DATA
			return null;
		}
	}

	/**
	 * Return the individual records
	 * 
	 */
	public List<Object[]> getRecords() {
		return sortedRecords.subList(startRowIdx, endRowIdx + 1);
	}

	/**
	 * Returns a breadth-first iterator, including this node
	 * 
	 * @param dataField
	 * @return
	 */
	public Iterator<SummariseNode> iterator() {
		return new BreadthFirstIterator(this);
	}

	/**
	 * Returns a breadth-first iterator, including this node
	 * 
	 * @param dataField
	 * @return
	 */
	public Iterator<SummariseNode> breathFirstIterator() {
		return new BreadthFirstIterator(this);
	}

	/**
	 * Returns a depth-first iterator, including this node
	 * 
	 * @param dataField
	 * @return
	 */
	public Iterator<SummariseNode> depthFirstIterator() {
		return new DepthFirstIterator(this);
	}

	private SummariseField getSummaryField(String summaryFieldName) {
		Iterator<SummariseField> it = summariseField2Value.keySet().iterator();
		while (it.hasNext()) {
			SummariseField summariseField = it.next();
			if (summariseField.getName().equals(summaryFieldName)) {
				return summariseField;
			}
		}
		System.err.println(summaryFieldName + " field doesn't exist");
		return null;
	}

	public String toString() {
		int level = this.getLevel();
		String s;
		if (level == -1) {
			s = "ROOT ";
		} else {
			s = "|";

			for (int i = 0; i < level; i++) {
				s += "-";
			}
			s += conditioningField + ":" + conditioningValue + " ";
		}
		Iterator<Entry<SummariseField, Object>> it = summariseField2Value
				.entrySet().iterator();
		while (it.hasNext()) {
			Entry<SummariseField, Object> entry = it.next();
			s += entry.getKey() + "=" + entry.getValue() + " ";
		}
		return s;
	}

	class BreadthFirstIterator implements Iterator<SummariseNode> {

		LinkedList<SummariseNode> queue;

		/**
		 * Initialises the iterator with the tree map node at the start of the
		 * traversal. The first item to be returned by this iterator will be the
		 * first child of the node provided to this constructor (assuming
		 * <code>hasNext</code> is true).
		 * 
		 * @param startNode
		 *            Parent node from which to iterate over the tree.
		 */
		public BreadthFirstIterator(SummariseNode startNode) {
			queue = new LinkedList<SummariseNode>();
			queue.add(startNode);
		}

		/**
		 * Reports whether or not there are any more elements in the tree that
		 * have yet to be iterated over.
		 * 
		 * @return True if there are more elements to iterate over.
		 */
		public boolean hasNext() {
			if (queue.size() > 0) {
				return true;
			}
			return false;
		}

		/**
		 * Provides the next tree map node in the breadth-first traversal of the
		 * hierarchy or throws a <code>NoSuchElementException</code> if there
		 * are no more nodes to iterate over.
		 * 
		 * @return Provides the next tree map node in the breadth-first
		 *         traversal.
		 */
		public SummariseNode next() {
			if (!queue.isEmpty()) {
				SummariseNode node = queue.removeFirst();

				Enumeration<SummariseNode> e = node.children();
				while (e.hasMoreElements()) {
					queue.add(e.nextElement());
				}
				return node;
			}
			throw new NoSuchElementException(
					"No more nodes to iterate in the treemap hierarchy");
		}

		/**
		 * Would remove the last returned node from the hierarchy, but does
		 * nothing in this case.
		 */
		public void remove() {
			// TODO Add node removal code here.
		}
	}

	class DepthFirstIterator implements Iterator<SummariseNode> {

		LinkedList<SummariseNode> queue;

		/**
		 * Initialises the iterator with the tree map node at the start of the
		 * traversal. The first item to be returned by this iterator will be the
		 * first child of the node provided to this constructor (assuming
		 * <code>hasNext</code> is true).
		 * 
		 * @param startNode
		 *            Parent node from which to iterate over the tree.
		 */
		public DepthFirstIterator(SummariseNode startNode) {
			queue = new LinkedList<SummariseNode>();
			queue.add(startNode);
		}

		/**
		 * Reports whether or not there are any more elements in the tree that
		 * have yet to be iterated over.
		 * 
		 * @return True if there are more elements to iterate over.
		 */
		public boolean hasNext() {
			if (queue.size() > 0) {
				return true;
			}
			return false;
		}

		/**
		 * Provides the next tree map node in the breadth-first traversal of the
		 * hierarchy or throws a <code>NoSuchElementException</code> if there
		 * are no more nodes to iterate over.
		 * 
		 * @return Provides the next tree map node in the breadth-first
		 *         traversal.
		 */
		public SummariseNode next() {
			if (!queue.isEmpty()) {
				SummariseNode node = queue.removeFirst();

				Enumeration<SummariseNode> e = node.children();
				while (e.hasMoreElements()) {
					queue.addFirst(e.nextElement());
				}
				return node;
			}
			throw new NoSuchElementException(
					"No more nodes to iterate in the treemap hierarchy");
		}

		/**
		 * Would remove the last returned node from the hierarchy, but does
		 * nothing in this case.
		 */
		public void remove() {
			// TODO Add node removal code here.
		}
	}

	public int compareTo(SummariseNode o) {
		if (this.order < o.order) {
			return -1;
		} else {
			return 1;
		}
	}

}
