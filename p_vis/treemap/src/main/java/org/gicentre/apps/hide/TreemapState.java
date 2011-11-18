package org.gicentre.apps.hide;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gicentre.data.DataField;
import org.gicentre.data.FieldType;
import org.gicentre.data.summary.SummariseField;
import org.gicentre.data.summary.SummariseNull;
import org.gicentre.hive.Expression;
import org.gicentre.hive.ExpressionNotSupportedException;
import org.gicentre.hive.Hive;
import org.gicentre.hive.Path;
import org.gicentre.hive.Preset;
import org.gicentre.hive.Type;
import org.gicentre.hive.Variable;

/**
 * Stores the state of a treemap
 * 
 * Implements HiVE, enabling it to read and write state - those states that it
 * supports
 * 
 * @author Aidan Slingsby, giCentre
 * 
 */
public class TreemapState implements Hive {

	protected DataField[] hierarchyFields = new DataField[0];
	protected SummariseField[][] sizeFields;
	protected SummariseField[][] orderFields;
	protected SummariseField[][] colourFields;
	protected Layout[] layouts;
	protected Object[] filterValues;

	protected List<DataField> allowedHierarchyFields;
	protected List<SummariseField> allowedSizeFields;
	protected List<SummariseField> allowedOrderFields;
	protected List<SummariseField> allowedColourFields;
	protected List<Layout> allowedLayouts;

	protected Map<String, DataField> hierarchyFieldsLookup;
	protected Map<String, SummariseField> sizeFieldsLookup;
	protected Map<String, SummariseField> orderFieldsLookup;
	protected Map<String, SummariseField> colourFieldsLookup;

	// These are used by other classes to assess whether the treemap has changed
	// state
	protected boolean hierHasChanged = false;
	protected boolean orderHasChanged = false;
	protected boolean sizeHasChanged = false;
	protected boolean colourHasChanged = false;
	protected boolean layoutHasChanged = false;
	protected boolean appearanceHasChanged = false;

	// Marker variable that means use default
	//
	protected SummariseNull summariseNull = null;

	// Hashmap of the appearance states for each appearance type (see below)
	protected HashMap<AppearanceType, Integer>[] appearanceValues;

	// Appearance aspects
	static public enum AppearanceType {
		SHOW_LABELS, LABEL_SIZE, AUTOSIZE_LABELS, LABEL_OPACITY, ALLOW_VERTICAL_LABELS, PADDING, BORDER_WIDTH, INCLUDE_NULLS;

		public String toString() {
			switch (this) {
			case PADDING:
				return "Padding";
			case LABEL_SIZE:
				return "Label size";
			case SHOW_LABELS:
				return "Show labels";
			case ALLOW_VERTICAL_LABELS:
				return "Allow vertical labels";
			case INCLUDE_NULLS:
				return "Include nodata";
			case LABEL_OPACITY:
				return "Label opacity";
			case AUTOSIZE_LABELS:
				return "Autosize text";
			case BORDER_WIDTH:
				return "Outlines";
			}
			return null;
		}

		/**
		 * Returns true if min/max is 0 and 1 (thus should be displayed as a
		 * checkbox)
		 * 
		 * @return
		 */
		public boolean checkBox() {
			return (this.minValue() == 0 && this.maxValue() == 1);
		}

		/**
		 * Default values
		 * 
		 * @return
		 */
		public int defaultValue() {
			switch (this) {
			case PADDING:
				return 1;
			case ALLOW_VERTICAL_LABELS:
				return 0;
			case LABEL_SIZE:
				return 20;
			case SHOW_LABELS:
				return 1;
			case INCLUDE_NULLS:
				return 1;
			case LABEL_OPACITY:
				return 180;
			case AUTOSIZE_LABELS:
				return 1;
			case BORDER_WIDTH:
				return 1;
			}
			return 0;
		}

		/**
		 * Min values
		 * 
		 * @return
		 */
		public int minValue() {
			switch (this) {
			case PADDING:
				return 0;
			case LABEL_SIZE:
				return 6;
			case SHOW_LABELS:
				return 0;
			case ALLOW_VERTICAL_LABELS:
				return 0;
			case INCLUDE_NULLS:
				return 0;
			case LABEL_OPACITY:
				return 20;
			case AUTOSIZE_LABELS:
				return 0;
			case BORDER_WIDTH:
				return 0;
			}
			return 0;
		}

		/**
		 * Max values
		 * 
		 * @return
		 */
		public int maxValue() {
			switch (this) {
			case PADDING:
				return 9999;
			case LABEL_SIZE:
				return 9999;
			case SHOW_LABELS:
				return 1;
			case ALLOW_VERTICAL_LABELS:
				return 1;
			case INCLUDE_NULLS:
				return 1;
			case LABEL_OPACITY:
				return 255;
			case AUTOSIZE_LABELS:
				return 1;
			case BORDER_WIDTH:
				return 20;
			}
			return 0;
		}

		public int incrementSize() {
			switch (this) {
			case PADDING:
				return 1;
			case LABEL_SIZE:
				return 1;
			case SHOW_LABELS:
				return 1;
			case ALLOW_VERTICAL_LABELS:
				return 1;
			case INCLUDE_NULLS:
				return 1;
			case LABEL_OPACITY:
				return 10;
			case AUTOSIZE_LABELS:
				return 1;
			case BORDER_WIDTH:
				return 1;
			}
			return 0;
		}

	}

	// Layouts
	static public enum Layout {
		ONE_DIM_LEFT_RIGHT, ONE_DIM_TOP_BOTTOM, ONE_DIM_ORDERED_SQUARIFIED, ONE_DIM_STRIP, TWO_DIMENSIONAL, ABS_POSITION;

		public String toString() {
			if (this.equals(ONE_DIM_LEFT_RIGHT)) {
				return "LeftToRight";
			} else if (this.equals(ONE_DIM_TOP_BOTTOM)) {
				return "TopToBottom";
			} else if (this.equals(ONE_DIM_ORDERED_SQUARIFIED)) {
				return "OrderedSq";
			} else if (this.equals(ONE_DIM_STRIP)) {
				return "RowByRow";
			} else if (this.equals(TWO_DIMENSIONAL)) {
				return "2dFill";
			} else if (this.equals(ABS_POSITION)) {
				return "2dPlot";
			} else
				return null;
		}
	}

	/**
	 * Constructor
	 * 
	 * 
	 * @param numOrderStates
	 *            Number of order dimensions (e.g. 1 for only 1D orders, 2 for
	 *            2D orders,... - if supported by other classes)
	 * @param numSizeStates
	 *            Number of size dimensions (e.g. 2 for 2D sizes - if supported
	 *            by other classes)
	 * @param numColourStates
	 *            Number of colour dimensions (e.g. 2 for lightness and
	 *            saturation - if supported by other classes)
	 * @param allowedHierarchyFields
	 *            All the data variables offered by the GUI for the hierarchy
	 * @param allowedOrderFields
	 *            All the summary variables offered by the GUI for orders
	 * @param allowedSizeFields
	 *            All the summary variables offered by the GUI for sizes
	 * @param allowedColourFields
	 *            All the summary variables offered by the GUI for colours
	 * @param allowedLayouts
	 *            All the layouts that are offered in the GUI
	 */
	public TreemapState(int numOrderStates, int numSizeStates,
			int numColourStates, List<DataField> allowedHierarchyFields,
			List<SummariseField> allowedOrderFields,
			List<SummariseField> allowedSizeFields,
			List<SummariseField> allowedColourFields,
			List<Layout> allowedLayouts) {
		this.orderFields = new SummariseField[numOrderStates][0];
		this.sizeFields = new SummariseField[numSizeStates][0];
		this.colourFields = new SummariseField[numColourStates][0];
		this.layouts = new Layout[0];
		this.filterValues = new Object[0];
		this.appearanceValues = new HashMap[0];

		this.summariseNull = new SummariseNull("summariseNull");

		this.allowedHierarchyFields = allowedHierarchyFields;

		this.allowedOrderFields = allowedOrderFields;
		this.allowedOrderFields.remove(null);// cannot have any nulls
		this.allowedOrderFields.add(0, summariseNull);// This adds NATURAL

		this.allowedSizeFields = allowedSizeFields;
		this.allowedSizeFields.remove(null);// cannot have any nulls
		this.allowedSizeFields.add(0, summariseNull);// This adds FIXED

		this.allowedColourFields = allowedColourFields;
		this.allowedColourFields.add(0, summariseNull);// This adds HIER
		if (!allowedColourFields.contains(null)) {
			allowedColourFields.add(null);// no colour
		}

		this.allowedLayouts = allowedLayouts;

		hierarchyFieldsLookup = new HashMap<String, DataField>();
		for (DataField dataField : allowedHierarchyFields) {
			if (dataField != null) {
				hierarchyFieldsLookup.put(dataField.getName(), dataField);
			}
		}
		orderFieldsLookup = new HashMap<String, SummariseField>();
		for (SummariseField summariseField : allowedOrderFields) {
			if (summariseField != null) {
				orderFieldsLookup.put(summariseField.getName(), summariseField);
			}
		}
		sizeFieldsLookup = new HashMap<String, SummariseField>();
		for (SummariseField summariseField : allowedSizeFields) {
			if (summariseField != null) {
				sizeFieldsLookup.put(summariseField.getName(), summariseField);
			}
		}
		colourFieldsLookup = new HashMap<String, SummariseField>();
		for (SummariseField summariseField : allowedColourFields) {
			if (summariseField != null) {
				colourFieldsLookup
						.put(summariseField.getName(), summariseField);
			}
		}
	}

	/**
	 * Get the number of levels
	 * 
	 * @return Number of levels
	 */
	public int getNumLevels() {
		return hierarchyFields.length;
	}

	/**
	 * Gets the size variables
	 * 
	 * @return 2D array of the sizes (number of size dimension x number in
	 *         hierarchy)
	 */
	public SummariseField[][] getSizeFields() {
		return sizeFields;
	}

	/**
	 * Sets the size variables
	 * 
	 * @param sizeFields
	 *            2D array of the sizes (number of size dimension x number in
	 *            hierarchy)
	 */
	public void setSizeFields(SummariseField[][] sizeFields) {
		this.sizeFields = sizeFields;
	}

	/**
	 * Gets the appearance values
	 * 
	 * @return array of appearance values (for each level)
	 */
	public int getAppearanceValue(AppearanceType appearanceType, int level) {
		return (Integer) appearanceValues[level].get(appearanceType);
	}

	/**
	 * Gets the variables that are allowed to be in the hierarchy
	 * 
	 * @return
	 */
	public List<DataField> getAllowedHierarchyFields() {
		return allowedHierarchyFields;
	}

	/**
	 * Gets the variables that are allowed to be used for size
	 * 
	 * @return
	 */
	public List<SummariseField> getAllowedSizeFields() {
		return allowedSizeFields;
	}

	/**
	 * Gets the variables that are allowed to be used for order
	 * 
	 * @return
	 */
	public List<SummariseField> getAllowedOrderFields() {
		return allowedOrderFields;
	}

	/**
	 * Gets the variables that are allowed to be used for colour
	 * 
	 * @return
	 */
	public List<SummariseField> getAllowedColourFields() {
		return allowedColourFields;
	}

	/**
	 * Gets the layouts that are offered
	 * 
	 * @return
	 */
	public List<Layout> getAllowedLayouts() {
		return allowedLayouts;
	}

	/**
	 * Get the variables in the hierarchy
	 * 
	 * @return Array of data variables, for each level
	 */
	public DataField[] getHierarchyFields() {
		return hierarchyFields;
	}

	/**
	 * Get the filter values
	 * 
	 * @return Array of data variables, for each level
	 */
	public Object[] getFilterValues() {
		return filterValues;
	}

	/**
	 * Get the order variables in the hierarchy
	 * 
	 * @return Array of variables (number of order dimensions X number in
	 *         hierarchy)
	 */
	public SummariseField[][] getOrderFields() {
		return orderFields;
	}

	/**
	 * Get the colour variables in the hierarchy
	 * 
	 * @return Array of variables (number of colour dimensions X number in
	 *         hierarchy)
	 */
	public SummariseField[][] getColourFields() {
		return colourFields;
	}

	/**
	 * Get the layouts in the hierarchy
	 * 
	 * @return Array of variables (number of order dimensions X number in
	 *         hierarchy)
	 */
	public Layout[] getLayouts() {
		return this.layouts;
	}

	/**
	 * Test is the hierarchy is empty
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return this.hierarchyFields.length == 0;
	}

	/**
	 * Swap two levels in the hierarchy
	 * 
	 * Swaps two levels along with the all the corresponding states
	 * 
	 * In line with HiVE, levels should be numbered from 1
	 * 
	 * @param level1
	 *            Level to swap with the other level
	 * @param level2
	 *            Level to swap with the other level
	 */
	public void swap(int level1, int level2) {
		level1--;
		level2--;
		if (level1 != level2 && level1 < this.hierarchyFields.length
				&& level2 < this.hierarchyFields.length) {

			// copy the state
			DataField[] hierarchyFields = new DataField[this.hierarchyFields.length];
			SummariseField[][] sizeFields = new SummariseField[this.sizeFields.length][this.hierarchyFields.length];
			SummariseField[][] orderFields = new SummariseField[this.orderFields.length][this.hierarchyFields.length];
			SummariseField[][] colourFields = new SummariseField[this.colourFields.length][this.hierarchyFields.length];
			Layout[] layouts = new Layout[this.hierarchyFields.length];
			HashMap<AppearanceType, Integer>[] appearanceValues = new HashMap[this.hierarchyFields.length];
			Object[] filterValues = new Object[this.hierarchyFields.length];
			for (int i = 0; i < this.hierarchyFields.length; i++) {
				hierarchyFields[i] = this.hierarchyFields[i];
				for (int j = 0; j < sizeFields.length; j++) {
					sizeFields[j][i] = this.sizeFields[j][i];
				}
				for (int j = 0; j < orderFields.length; j++) {
					orderFields[j][i] = this.orderFields[j][i];
				}
				for (int j = 0; j < colourFields.length; j++) {
					colourFields[j][i] = this.colourFields[j][i];
				}
				layouts[i] = this.layouts[i];
				appearanceValues[i] = this.appearanceValues[i];
				filterValues[i] = this.filterValues[i];
			}
			hierarchyFields[level1] = this.hierarchyFields[level2];
			for (int j = 0; j < sizeFields.length; j++) {
				sizeFields[j][level1] = this.sizeFields[j][level2];
			}
			for (int j = 0; j < orderFields.length; j++) {
				orderFields[j][level1] = this.orderFields[j][level2];
			}
			for (int j = 0; j < colourFields.length; j++) {
				colourFields[j][level1] = this.colourFields[j][level2];
			}
			layouts[level1] = this.layouts[level2];
			appearanceValues[level1] = this.appearanceValues[level2];
			filterValues[level1] = this.filterValues[level2];

			hierarchyFields[level2] = this.hierarchyFields[level1];
			for (int j = 0; j < sizeFields.length; j++) {
				sizeFields[j][level2] = this.sizeFields[j][level1];
			}
			for (int j = 0; j < orderFields.length; j++) {
				orderFields[j][level2] = this.orderFields[j][level1];
			}
			for (int j = 0; j < colourFields.length; j++) {
				colourFields[j][level2] = this.colourFields[j][level1];
			}
			layouts[level2] = this.layouts[level1];
			appearanceValues[level2] = this.appearanceValues[level1];
			filterValues[level2] = this.filterValues[level1];

			this.hierarchyFields = hierarchyFields;
			this.sizeFields = sizeFields;
			this.orderFields = orderFields;
			this.colourFields = colourFields;
			this.layouts = layouts;
			this.appearanceValues = appearanceValues;
			this.filterValues = filterValues;
		} else {
			System.err.println("Cannot cut swap levels " + level1 + " and "
					+ level2);
		}
	}

	/**
	 * Cut a level from the hierarchy
	 * 
	 * @param level
	 *            Level to cut
	 */
	public void cut(int level) {
		if (level < this.hierarchyFields.length) {
			DataField[] hierarchyFields = new DataField[this.hierarchyFields.length - 1];
			SummariseField[][] sizeFields = new SummariseField[this.sizeFields.length][this.hierarchyFields.length - 1];
			SummariseField[][] orderFields = new SummariseField[this.orderFields.length][this.hierarchyFields.length - 1];
			SummariseField[][] colourFields = new SummariseField[this.colourFields.length][this.hierarchyFields.length - 1];
			HashMap<AppearanceType, Integer>[] appearanceValues = new HashMap[this.hierarchyFields.length - 1];
			Layout[] layouts = new Layout[this.hierarchyFields.length - 1];
			Object[] filterValues = new Object[this.hierarchyFields.length - 1];

			if (this.hierarchyFields.length > 1) {
				for (int i = 0; i < this.hierarchyFields.length; i++) {
					if (i < level) {
						hierarchyFields[i] = this.hierarchyFields[i];
						for (int j = 0; j < sizeFields.length; j++) {
							sizeFields[j][i] = this.sizeFields[j][i];
						}
						for (int j = 0; j < orderFields.length; j++) {
							orderFields[j][i] = this.orderFields[j][i];
						}
						for (int j = 0; j < colourFields.length; j++) {
							colourFields[j][i] = this.colourFields[j][i];
						}
						layouts[i] = this.layouts[i];
						appearanceValues[i] = this.appearanceValues[i];
						filterValues[i] = this.filterValues[i];
					} else if (i > level) {
						hierarchyFields[i - 1] = this.hierarchyFields[i];
						for (int j = 0; j < sizeFields.length; j++) {
							sizeFields[j][i - 1] = this.sizeFields[j][i];
						}
						for (int j = 0; j < orderFields.length; j++) {
							orderFields[j][i - 1] = this.orderFields[j][i];
						}
						for (int j = 0; j < colourFields.length; j++) {
							colourFields[j][i - 1] = this.colourFields[j][i];
						}
						layouts[i - 1] = this.layouts[i];
						appearanceValues[i - 1] = this.appearanceValues[i];
						filterValues[i - 1] = this.filterValues[i];
					}
				}
			}
			this.hierarchyFields = hierarchyFields;
			this.sizeFields = sizeFields;
			this.orderFields = orderFields;
			this.colourFields = colourFields;
			this.layouts = layouts;
			this.filterValues = filterValues;
			this.appearanceValues = appearanceValues;
		} else {
			System.err.println("Cannot cut level " + level);
		}
	}

	/**
	 * Insert a level
	 * 
	 * @param level
	 *            The level at which to insert
	 * @param hierarchyField
	 *            The variable to insert
	 * @param orderField
	 *            The order
	 * @param sizeField
	 *            The size
	 * @param colourField
	 *            The colour
	 * @param layout
	 *            The layout
	 */
	public void insert(int level, DataField hierarchyField,
			SummariseField[] orderField, SummariseField[] sizeField,
			SummariseField[] colourField, Layout layout) {
		if (level <= this.hierarchyFields.length) {
			DataField[] hierarchyFields = new DataField[this.hierarchyFields.length + 1];
			SummariseField[][] sizeFields = new SummariseField[this.sizeFields.length][this.hierarchyFields.length + 1];
			SummariseField[][] orderFields = new SummariseField[this.orderFields.length][this.hierarchyFields.length + 1];
			SummariseField[][] colourFields = new SummariseField[this.colourFields.length][this.hierarchyFields.length + 1];
			Layout[] layouts = new Layout[this.hierarchyFields.length + 1];
			Object[] filterValues = new Object[this.hierarchyFields.length + 1];

			HashMap<AppearanceType, Integer>[] appearanceValues = new HashMap[this.hierarchyFields.length + 1];
			if (this.hierarchyFields.length == 0) {
				hierarchyFields[0] = hierarchyField;
				for (int j = 0; j < sizeFields.length; j++) {
					sizeFields[j][0] = sizeField[j];
				}
				for (int j = 0; j < orderFields.length; j++) {
					orderFields[j][0] = orderField[j];
				}
				for (int j = 0; j < colourFields.length; j++) {
					colourFields[j][0] = colourField[j];
				}
				layouts[0] = layout;
				appearanceValues[0] = new HashMap<AppearanceType, Integer>();
				for (AppearanceType appearanceType : AppearanceType.values()) {
					appearanceValues[0].put(appearanceType,
							appearanceType.defaultValue());
				}
				filterValues[0] = null;
			}
			for (int i = 0; i < hierarchyFields.length; i++) {
				if (i < level) {
					hierarchyFields[i] = this.hierarchyFields[i];
					for (int j = 0; j < sizeFields.length; j++) {
						sizeFields[j][i] = this.sizeFields[j][i];
					}
					for (int j = 0; j < orderFields.length; j++) {
						orderFields[j][i] = this.orderFields[j][i];
					}
					for (int j = 0; j < colourFields.length; j++) {
						colourFields[j][i] = this.colourFields[j][i];
					}
					layouts[i] = this.layouts[i];
					appearanceValues[i] = this.appearanceValues[i];
					filterValues[i] = this.filterValues[i];
				} else if (i == level) {
					hierarchyFields[i] = hierarchyField;
					for (int j = 0; j < sizeFields.length; j++) {
						sizeFields[j][i] = sizeField[j];
					}
					for (int j = 0; j < orderFields.length; j++) {
						orderFields[j][i] = orderField[j];
					}
					for (int j = 0; j < colourFields.length; j++) {
						colourFields[j][i] = colourField[j];
					}
					layouts[i] = layout;
					filterValues[i] = null;
					appearanceValues[i] = new HashMap<AppearanceType, Integer>();
					for (AppearanceType appearanceType : AppearanceType
							.values()) {
						appearanceValues[i].put(appearanceType,
								appearanceType.defaultValue());
					}

				} else {
					hierarchyFields[i] = this.hierarchyFields[i - 1];
					for (int j = 0; j < sizeFields.length; j++) {
						sizeFields[j][i] = this.sizeFields[j][i - 1];
					}
					for (int j = 0; j < orderFields.length; j++) {
						orderFields[j][i] = this.orderFields[j][i - 1];
					}
					for (int j = 0; j < colourFields.length; j++) {
						colourFields[j][i] = this.colourFields[j][i - 1];
					}
					layouts[i] = this.layouts[i - 1];
					appearanceValues[i] = this.appearanceValues[i - 1];
					filterValues[i] = this.filterValues[i - 1];
				}
			}
			this.hierarchyFields = hierarchyFields;
			this.sizeFields = sizeFields;
			this.orderFields = orderFields;
			this.colourFields = colourFields;
			this.layouts = layouts;
			this.appearanceValues = appearanceValues;
			this.filterValues = filterValues;
		} else {
			System.err.println("Cannot cut level " + level);
		}
	}

	/**
	 * Reset change flag. Call whether you've read these
	 * 
	 */
	public void resetChangeFlags() {
		hierHasChanged = false;
		orderHasChanged = false;
		sizeHasChanged = false;
		colourHasChanged = false;
		layoutHasChanged = false;
		appearanceHasChanged = false;
	}

	/**
	 * Reports whether the state has changed since last reset
	 * 
	 * @return
	 */
	public boolean hasChanged() {
		return hierHasChanged || orderHasChanged || sizeHasChanged
				|| colourHasChanged || layoutHasChanged || appearanceHasChanged;
	}

	/**
	 * Reports whether the hierarchy has changed since last reset
	 * 
	 * @return
	 */
	public boolean hasHierChanged() {
		return hierHasChanged;
	}

	/**
	 * Reports whether the order has changed since last reset
	 * 
	 * @return
	 */
	public boolean orderHasChanged() {
		return orderHasChanged;
	}

	/**
	 * Reports whether the size has changed since last reset
	 * 
	 * @return
	 */
	public boolean sizeHasChanged() {
		return sizeHasChanged;
	}

	/**
	 * Reports whether the colour has changed since last reset
	 * 
	 * @return
	 */
	public boolean colourHasChanged() {
		return colourHasChanged;
	}

	/**
	 * Reports whether the layout has changed since last reset
	 * 
	 * @return
	 */
	public boolean layoutHasChanged() {
		return layoutHasChanged;
	}

	/**
	 * Reports whether the appearance has changed since last reset
	 * 
	 * @return
	 */
	public boolean appearanceHasChanged() {
		return appearanceHasChanged;
	}

	public boolean applyExpressions(Collection<Expression> expressions) {
		HashMap<AppearanceType, Integer>[] oldAppearance = appearanceValues;
		this.clear();

		boolean flag = true;

		// do hier first
		for (Expression expression : expressions) {
			if (expression.getType().equals(Type.S_HIER)
					|| expression.getType().equals(Type.O_HIER)) {
				try {
					flag = applyOperator(expression);
				} catch (ExpressionNotSupportedException e) {
					System.err.println(e);
				}
			}
		}
		// then do layouts
		for (Expression expression : expressions) {
			if (expression.getType().equals(Type.S_LAYOUT)
					|| expression.getType().equals(Type.O_LAYOUT)) {
				try {
					flag = applyOperator(expression);
				} catch (ExpressionNotSupportedException e) {
					System.err.println(e);
				}
			}
		}
		// then do everything else except focus
		for (Expression expression : expressions) {
			if (!expression.getType().equals(Type.S_HIER)
					&& !expression.getType().equals(Type.O_HIER)
					&& !expression.getType().equals(Type.S_FOCUS)
					&& !expression.getType().equals(Type.O_FOCUS)
					&& !expression.getType().equals(Type.S_LAYOUT)
					&& !expression.getType().equals(Type.O_LAYOUT)) {
				try {
					flag = applyOperator(expression);
				} catch (ExpressionNotSupportedException e) {
					System.err.println(e);
				}
			}
		}
		// finally, do focus
		for (Expression expression : expressions) {
			if (expression.getType().equals(Type.S_FOCUS)
					|| expression.getType().equals(Type.O_FOCUS)) {
				try {
					flag = applyOperator(expression);
				} catch (ExpressionNotSupportedException e) {
					System.err.println(e);
				}
			}
		}

		// then restore appearance
		// store appearance - to restore later
		int len = oldAppearance.length < appearanceValues.length ? oldAppearance.length
				: appearanceValues.length;
		if (oldAppearance.length > 0) {
			for (int i = 0; i < len; i++) {
				for (AppearanceType appearanceType : oldAppearance[i].keySet()) {
					appearanceValues[i].put(appearanceType,
							oldAppearance[i].get(appearanceType));
				}
			}
		}

		return flag;
	}

	/**
	 * Apply operator
	 * 
	 * @param expression
	 *            HiVE expression to apply
	 */
	public boolean applyOperator(Expression expression)
			throws ExpressionNotSupportedException {

		// HIER
		if (expression.getType() == Type.S_HIER
				|| expression.getType() == Type.O_HIER) {

			int startLevel = expression.getLevel() - 1;

			if (startLevel > this.hierarchyFields.length) {
				System.err.println("Current hierarchy is not deep enough for "
						+ expression);
				return false;
			}

			// each each variable in o/sHier
			for (int i = 0; i < expression.getVarGroups().size(); i++) {
				if (expression.getVarGroups().get(i).length != 1) {
					System.err
							.println("Hierarchy variables must only be in groups of one");
					return false;
				}
				Variable var = expression.getVarGroups().get(i)[0];
				if (!var.isDataVariable()) {
					System.err
							.println("The hierarchy can only have variables in.");
					return false;
				}
				DataField dataField = hierarchyFieldsLookup.get(var.getName());
				if (dataField == null) {
					System.err.println(var.getName() + " not found for "
							+ expression);
					return false;
				}
				DataField hierarchyField = dataField;

				int level = startLevel + i;
				if (level >= this.getNumLevels()) {
					// then INSERT
					SummariseField[] orderField = new SummariseField[orderFields.length];
					SummariseField[] sizeField = new SummariseField[sizeFields.length];
					SummariseField[] colourField = new SummariseField[colourFields.length];
					Layout layout = null;
					if (level == 0) {
						for (int j = 0; j < orderFields.length; j++) {
							orderField[j] = summariseNull;
						}
						for (int j = 0; j < sizeFields.length; j++) {
							sizeField[j] = summariseNull;
						}
						for (int j = 0; j < colourFields.length; j++) {
							colourField[j] = summariseNull;
						}
						layout = allowedLayouts.get(0);
					} else {
						// otherwise fill with the previous leaf values
						for (int j = 0; j < orderFields.length; j++) {
							orderField[j] = orderFields[j][level - 1];
						}
						for (int j = 0; j < sizeFields.length; j++) {
							sizeField[j] = sizeFields[j][level - 1];
						}
						for (int j = 0; j < colourFields.length; j++) {
							colourField[j] = colourFields[j][level - 1];
						}
						layout = layouts[level - 1];
					}
					insert(level, hierarchyField, orderField, sizeField,
							colourField, layout);
				} else {
					this.hierarchyFields[level] = hierarchyField;
				}
			}
			hierHasChanged = true;
			return true;
		}

		// ORDER, SIZE and COLOR
		else if (expression.getType() == Type.S_ORDER
				|| expression.getType() == Type.O_ORDER
				|| expression.getType() == Type.S_SIZE
				|| expression.getType() == Type.O_SIZE
				|| expression.getType() == Type.S_COLOR
				|| expression.getType() == Type.O_COLOR) {
			int startLevel = expression.getLevel() - 1;

			SummariseField[][] oldFields = null;
			Map<String, SummariseField> fieldsLookups = null;
			if (expression.getType() == Type.S_ORDER
					|| expression.getType() == Type.O_ORDER) {
				oldFields = this.orderFields;
				fieldsLookups = orderFieldsLookup;
			} else if (expression.getType() == Type.S_SIZE
					|| expression.getType() == Type.O_SIZE) {
				oldFields = this.sizeFields;
				fieldsLookups = sizeFieldsLookup;
			} else if (expression.getType() == Type.S_COLOR
					|| expression.getType() == Type.O_COLOR) {
				oldFields = this.colourFields;
				fieldsLookups = colourFieldsLookup;
			}

			if (startLevel + expression.getVarGroups().size() > oldFields[0].length) {
				System.err.println("Hierarchy not deep enough");
				return false;
			}
			SummariseField[][] newFields = new SummariseField[oldFields.length][oldFields[0].length];
			for (int i = 0; i < oldFields[0].length; i++) {
				// if the i is at a level that's not to be changed by the
				// operator
				// then just copy the state
				if (i < startLevel
						|| i >= startLevel + expression.getVarGroups().size()) {
					for (int j = 0; j < oldFields.length; j++) {
						newFields[j][i] = oldFields[j][i];
					}
				} else {
					// otherwise, set according to the operator
					Variable[] varGroup = expression.getVarGroups().get(
							i - startLevel);

					// if we are changing the order values... (we need to change
					// the layouts too)
					if (expression.getType() == Type.S_ORDER
							|| expression.getType() == Type.O_ORDER) {
						if (varGroup.length == 1) {
							SummariseField summariseField = fieldsLookups
									.get(varGroup[0].getName());
							if (summariseField == null
									&& varGroup[0].isDataVariable()) {
								System.err.println(varGroup[0].getName()
										+ " not found for " + expression);
								return false;
							}
							if (summariseField == null) {
								summariseField = summariseNull;
							}
							newFields[0][i] = summariseField;
							newFields[1][i] = summariseNull;
							this.layouts[i] = Layout.ONE_DIM_STRIP;
						} else if (varGroup.length == 2
								&& varGroup[0].getName().equals(
										Preset.NULL.toString())
								&& !varGroup[1].getName().equals(
										Preset.NULL.toString())) {
							SummariseField summariseField = fieldsLookups
									.get(varGroup[1].getName());
							if (summariseField == null
									&& varGroup[1].isDataVariable()) {
								System.err.println(varGroup[1].getName()
										+ " not found for " + expression);
								return false;
							}
							if (summariseField == null) {
								summariseField = summariseNull;
							}
							newFields[0][i] = summariseField;
							newFields[1][i] = summariseNull;
							this.layouts[i] = Layout.ONE_DIM_TOP_BOTTOM;
						} else if (varGroup.length == 2
								&& !varGroup[0].getName().equals(
										Preset.NULL.toString())
								&& varGroup[1].getName().equals(
										Preset.NULL.toString())) {
							SummariseField summariseField = fieldsLookups
									.get(varGroup[0].getName());
							if (summariseField == null
									&& varGroup[0].isDataVariable()) {
								System.err.println(varGroup[0].getName()
										+ " not found for " + expression);
								return false;
							}
							if (summariseField == null) {
								summariseField = summariseNull;
							}
							newFields[0][i] = summariseField;
							newFields[1][i] = summariseNull;
							this.layouts[i] = Layout.ONE_DIM_LEFT_RIGHT;
						} else {
							SummariseField summariseField = fieldsLookups
									.get(varGroup[0].getName());
							if (summariseField == null
									&& varGroup[0].isDataVariable()) {
								System.err.println(varGroup[0].getName()
										+ " not found for " + expression);
								return false;
							}
							if (summariseField == null) {
								summariseField = summariseNull;
							}
							newFields[0][i] = summariseField;

							summariseField = fieldsLookups.get(varGroup[1]
									.getName());
							if (summariseField == null
									&& varGroup[1].isDataVariable()) {
								System.err.println(varGroup[0].getName()
										+ " not found for " + expression);
								return false;
							}
							if (summariseField == null) {
								summariseField = summariseNull;
							}
							newFields[1][i] = summariseField;

							// if layout is already set to ABS_POSITION, retain
							// this... otherwise use TWO_DIMENSIONAL
							if (!this.layouts[i].equals(Layout.ABS_POSITION)) {
								this.layouts[i] = Layout.TWO_DIMENSIONAL;
							}
						}

					} else {
						// if we are changing the size/colour values...
						for (int j = 0; j < newFields.length; j++) {
							if (j < varGroup.length) {

								if (varGroup[j].isDataVariable()) {
									// use the data variable
									SummariseField summariseField = fieldsLookups
											.get(varGroup[j].getName());
									newFields[j][i] = summariseField;
								}

								else if (expression.getType() == Type.S_SIZE
										|| expression.getType() == Type.O_SIZE) {
									// else use summariseNull if size
									newFields[j][i] = summariseNull;
								} else if (varGroup[j].getName().equals(
										Preset.HIER.toString())) {
									// else use summarisenull (must be colour)
									newFields[j][i] = summariseNull;
								} else {
									// must be no colour
									newFields[j][i] = null;
								}
							}
						}
					}
				}

			}

			if (expression.getType() == Type.S_ORDER
					|| expression.getType() == Type.O_ORDER) {
				this.orderFields = newFields;
				this.orderHasChanged = true;
			} else if (expression.getType() == Type.S_SIZE
					|| expression.getType() == Type.O_SIZE) {
				this.sizeFields = newFields;
				this.sizeHasChanged = true;
			} else if (expression.getType() == Type.S_COLOR
					|| expression.getType() == Type.O_COLOR) {
				this.colourFields = newFields;
				this.colourHasChanged = true;
			}

			return true;
		}

		// LAYOUTs
		else if (expression.getType() == Type.S_LAYOUT
				|| expression.getType() == Type.O_LAYOUT) {
			int startLevel = expression.getLevel() - 1;

			Layout[] oldLayouts = this.layouts;

			if (startLevel + expression.getVarGroups().size() > oldLayouts.length) {
				System.err.println("Hierarchy not deep enough");
				return false;
			}
			Layout[] newLayouts = new Layout[oldLayouts.length];
			for (int i = 0; i < oldLayouts.length; i++) {
				// if the i is at a level that's not to be changed by the
				// operator
				// then just copy the state
				if (i < startLevel
						|| i >= startLevel + expression.getVarGroups().size()) {
					newLayouts[i] = oldLayouts[i];
				} else {
					// otherwise, set according to the operator
					Variable[] varGroup = expression.getVarGroups().get(
							i - startLevel);

					// if we are changing the size/colour values...
					if (varGroup[0].getName().equals(
							Preset.CARTESIAN.toString())) {
						newLayouts[i] = Layout.ABS_POSITION;
					} else if (varGroup[0].getName().equals(
							Preset.SPACE_FILLING.toString())) {
						// unfortunately, this cannot distinguish between
						// ONE_DIM_TOP_BOTTOM, ONE_DIM_ORDERED_SQUARIFIED or
						// ONE_DIM_STRIP, so defaults to former
						// depends on order
						SummariseField[] orderFields = new SummariseField[this
								.getOrderFields().length];
						for (int j = 0; j < orderFields.length; j++) {
							orderFields[j] = this.getOrderFields()[j][i];
						}
						if (orderFields[0] == null && orderFields[1] != null) {
							newLayouts[i] = Layout.ONE_DIM_TOP_BOTTOM;
						} else if (orderFields[0] != null
								&& orderFields[1] == null) {
							newLayouts[i] = Layout.ONE_DIM_LEFT_RIGHT;
						} else {
							newLayouts[i] = Layout.TWO_DIMENSIONAL;
						}
					} else {
						System.err.println("Not a valid layout");
						return false;
					}
				}

			}
			this.layouts = newLayouts;

			this.layoutHasChanged = true;
			return true;
		}

		// FOCUS
		if (expression.getType() == Type.S_FOCUS
				|| expression.getType() == Type.O_FOCUS) {

			List<String> pathElements = expression.getPath().getValues();
			for (int i = 0; i < this.hierarchyFields.length; i++) {
				if (i >= pathElements.size() || pathElements.get(i).equals("*")) {
					filterValues[i] = null;
				} else {
					if (hierarchyFields[i].getFieldType().equals(
							FieldType.DOUBLE)) {
						filterValues[i] = Double.parseDouble(pathElements
								.get(i));
					} else if (hierarchyFields[i].getFieldType().equals(
							FieldType.FLOAT)) {
						filterValues[i] = Float.parseFloat(pathElements.get(i));
					} else if (hierarchyFields[i].getFieldType().equals(
							FieldType.INT)) {
						filterValues[i] = Integer.parseInt(pathElements.get(i));
					} else if (hierarchyFields[i].getFieldType().equals(
							FieldType.LONG)) {
						filterValues[i] = Long.parseLong(pathElements.get(i));
					} else {
						filterValues[i] = pathElements.get(i);
					}
				}
			}
			hierHasChanged = true;
			return true;
		}

		else {
			throw new ExpressionNotSupportedException();
		}
	}

	/**
	 * Returns the state
	 * 
	 * @return List of states
	 */
	public List<Expression> getState() {
		ArrayList<Expression> expressions = new ArrayList<Expression>();

		// Hierarchy
		Expression expression = new Expression(Type.S_HIER);
		for (int i = 0; i < hierarchyFields.length; i++) {
			expression.addVar("$" + hierarchyFields[i].getName());
		}
		expressions.add(expression);

		// Order
		expression = new Expression(Type.S_ORDER);

		for (int i = 0; i < orderFields[0].length; i++) {
			String[] varStrings = null;

			if (layouts[i] == Layout.ONE_DIM_STRIP
					|| layouts[i] == Layout.ONE_DIM_ORDERED_SQUARIFIED) {
				// One dimensional ordering
				if (orderFields[0][i] == null
						|| orderFields[0][i] instanceof SummariseNull) {
					varStrings = new String[] { Preset.HIER.toString() };
				} else {
					varStrings = new String[] { "$"
							+ orderFields[0][i].getName() };
				}
			} else if (layouts[i] == Layout.ONE_DIM_LEFT_RIGHT
					|| layouts[i] == Layout.ONE_DIM_TOP_BOTTOM) {
				// One dimensional ordering vertically or horizontally
				String varName;
				if (orderFields[0][i] == null
						|| orderFields[0][i] instanceof SummariseNull) {
					varName = Preset.HIER.toString();
				} else {
					varName = "$" + orderFields[0][i].getName();
				}
				if (layouts[i] == Layout.ONE_DIM_LEFT_RIGHT) {
					varStrings = new String[] { varName, Preset.NULL.toString() };
				} else {
					varStrings = new String[] { Preset.NULL.toString(), varName };
				}
			} else {
				// Two-dimensional ordering
				varStrings = new String[2];
				if (orderFields[0][i] == null
						|| orderFields[0][i] instanceof SummariseNull) {
					varStrings[0] = Preset.HIER.toString();
				} else {
					varStrings[0] = "$" + orderFields[0][i].getName();
				}
				if (orderFields.length > 1) {
					if (orderFields[1][i] == null
							|| orderFields[1][i] instanceof SummariseNull) {
						varStrings[1] = Preset.HIER.toString();
					} else {
						varStrings[1] = "$" + orderFields[1][i].getName();
					}
				} else {
					varStrings[1] = Preset.NULL.toString();
				}

			}
			expression.addVarGroup(varStrings);
		}
		expressions.add(expression);

		// Size
		expression = new Expression(Type.S_SIZE);

		if (sizeFields.length == 1) {
			for (int i = 0; i < sizeFields[0].length; i++) {
				if (sizeFields[0][i] == null
						|| sizeFields[0][i] instanceof SummariseNull) {
					expression.addVar(Preset.FIXED.toString());
				} else {
					expression.addVar("$" + sizeFields[0][i].getName());
				}
			}
		} else {
			for (int i = 0; i < sizeFields[0].length; i++) {
				String[] varGroupString = new String[sizeFields.length];
				for (int j = 0; j < sizeFields.length; j++) {
					if (sizeFields[j][i] == null
							|| sizeFields[j][i] instanceof SummariseNull) {
						varGroupString[j] = Preset.FIXED.toString();
					} else {
						varGroupString[j] = "$" + sizeFields[j][i].getName();
					}
				}
				expression.addVarGroup(varGroupString);
			}
		}
		expressions.add(expression);

		// Colour
		expression = new Expression(Type.S_COLOR);
		// assume that colourFields is a rectangular 2D array
		if (colourFields.length == 1) {
			for (int i = 0; i < colourFields[0].length; i++) {
				if (colourFields[0][i] == null) {
					expression.addVar(Preset.NULL.toString());
				} else if (colourFields[0][i] instanceof SummariseNull) {
					expression.addVar(Preset.HIER.toString());// if HIERARCHY,
																// use the
																// conditioning
																// variable
				} else {
					expression.addVar("$" + colourFields[0][i].getName());
				}
			}
		} else {
			for (int i = 0; i < colourFields[0].length; i++) {
				String[] varGroupString = new String[colourFields.length];
				for (int j = 0; j < colourFields.length; j++) {
					if (colourFields[j][i] == null) {
						varGroupString[j] = Preset.NULL.toString();
					} else if (colourFields[j][i] instanceof SummariseNull) {
						varGroupString[j] = Preset.HIER.toString();
					} else {
						varGroupString[j] = "$" + colourFields[j][i].getName();
					}
				}
				expression.addVarGroup(varGroupString);
			}
		}
		expressions.add(expression);

		// Layouts
		expression = new Expression(Type.S_LAYOUT);
		for (int i = 0; i < layouts.length; i++) {
			if (layouts[i].equals(Layout.ABS_POSITION)) {
				expression.addVar(Preset.CARTESIAN.toString());// currently, all
																// the layouts
																// are space
																// filling
			} else {
				expression.addVar(Preset.SPACE_FILLING.toString());
			}
		}
		expressions.add(expression);

		// focus/filter
		expression = new Expression(Type.S_FOCUS);
		Path path = new Path();
		boolean noFilters = true;
		for (int i = 0; i < hierarchyFields.length; i++) {
			if (this.filterValues[i] != null) {
				noFilters = false;
			}
		}
		// Only return oFocus if there are any filters (i.e. path is not "/")
		if (!noFilters) {
			for (int i = 0; i < hierarchyFields.length; i++) {
				if (this.filterValues[i] == null) {
					path.addWildcard();
				} else {
					path.addNode(this.filterValues[i].toString());
				}
			}
			expression.setPath(path);
			expression.addVar(Preset.SELECT.toString());
			expressions.add(expression);
		}

		return expressions;
	}

	/**
	 * Clears the state
	 */
	public void clear() {
		hierarchyFields = new DataField[0];
		sizeFields = new SummariseField[sizeFields.length][0];
		orderFields = new SummariseField[orderFields.length][0];
		colourFields = new SummariseField[colourFields.length][0];
		layouts = new Layout[0];
	}

	/**
	 * Saves the state of the appearance values (not captured in HiVE) to file.
	 * This can be subsequently restored
	 * 
	 * @param filename
	 */
	public void outputAppearanceStateToFile(String filename) throws IOException {
		BufferedWriter bw = new BufferedWriter(new PrintWriter(filename));
		bw.write("//Appearance state for HiDE, written on "
				+ new SimpleDateFormat("yyyy MM dd hh:00")
						.format(GregorianCalendar.getInstance().getTime())
				+ " by " + System.getProperty("user.name") + "\n");
		for (int i = 0; i < hierarchyFields.length; i++) {
			for (AppearanceType appearanceType : AppearanceType.values()) {
				bw.write(i + "\t" + appearanceType.name() + "\t"
						+ appearanceValues[i].get(appearanceType) + "\n");
			}
		}
		bw.close();
		System.out.println("Saved appearance to " + filename);
	}

	/**
	 * Restores the appearance values
	 * 
	 * @param filename
	 */
	public void inputAppearanceStateFromFile(BufferedReader br)
			throws IOException, NumberFormatException {
		// make a copy
		HashMap<AppearanceType, Integer>[] appearanceValues = this.appearanceValues;
		while (br.ready()) {
			String line = br.readLine();
			if (!line.startsWith("//")) {
				String[] toks = line.split("\t");
				if (toks.length == 3) {
					int level = Integer.parseInt(toks[0]);
					AppearanceType appearanceType = AppearanceType
							.valueOf(toks[1]);
					int value = Integer.parseInt(toks[2]);
					// try to restore
					if (level < appearanceValues.length) {
						appearanceValues[level].put(appearanceType, value);
					}
				}
			}
		}
		br.close();
		this.appearanceValues = appearanceValues;
		this.hierHasChanged = true;
		this.appearanceHasChanged = true;
	}

}
