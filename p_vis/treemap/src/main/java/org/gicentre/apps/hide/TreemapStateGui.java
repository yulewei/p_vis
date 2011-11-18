package org.gicentre.apps.hide;

import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gicentre.data.DataField;
import org.gicentre.data.summary.SummariseField;
import org.gicentre.data.summary.SummariseNull;
import org.gicentre.hive.Expression;
import org.gicentre.hive.ExpressionNotSupportedException;
import org.gicentre.utils.gui.Tooltip;
import org.gicentre.utils.spatial.Direction;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;

/**
 * The GUI Extends TreemapState, so also serves as this.
 * 
 * This does no event handling itself. To use it, a Processing sketch must call
 * draw() and mouseClicked() whenever these events are fired
 * 
 * @author Aidan Slingsby, giCentre
 * 
 */
public class TreemapStateGui extends TreemapState {

	private int x, y;
	private PFont f;
	private PApplet applet;
	private int textHeight, plusMinusW, swapArrowW;
	Rectangle bounds;

	Tooltip clickHereToStartTooltip;

	protected Layout defaultLayout = Layout.ONE_DIM_STRIP;

	// Rectangles for all the clickable words, so mouse detection is easier
	// These are laid out by layout()
	private Rectangle[] hierarchyRectangles; // 0 is the label... so 1 longer
												// than the hierarchyFields
	private Rectangle[][] sizeRectangles;
	private Rectangle[][] orderRectangles;
	private Rectangle[][] colourRectangles;
	private Rectangle[] layoutRectangles;
	private Rectangle[] plusRectangles;
	private Rectangle[] minusRectangles;
	private Rectangle[] appearanceRectangles;
	private Rectangle[] swapRectangles;
	private Rectangle[] filterRectangles;

	private AppearanceType[] appearanceTypesEditing;

	private boolean flagToRelayoutGui = true;// flag to relayout GUI

	private int guiHeight;

	/**
	 * Constructor
	 * 
	 * @param applet
	 *            Main processing sketch
	 * @param x
	 * @param y
	 * @param font
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
	public TreemapStateGui(PApplet applet, int x, int y, PFont font,
			int numOrderStates, int numSizeStates, int numColourStates,
			List<DataField> allowedHierarchyFields,
			List<SummariseField> allowedOrderFields,
			List<SummariseField> allowedSizeFields,
			List<SummariseField> allowedColourFields,
			List<Layout> allowedLayouts) {
		super(numOrderStates, numSizeStates, numColourStates,
				allowedHierarchyFields, allowedOrderFields, allowedSizeFields,
				allowedColourFields, allowedLayouts);
		this.x = x;
		this.y = y;
		this.f = font;
		this.applet = applet;

		// mkae sure there are no duplicates
		// If empty, add a null value...
		// ...but the hierarchy field values need at least one value

		// set allowed hierarchy fields
		this.allowedHierarchyFields = new ArrayList<DataField>();
		if (allowedHierarchyFields != null) {
			Iterator<DataField> it = allowedHierarchyFields.iterator();
			while (it.hasNext()) {
				DataField dataField = it.next();
				if (dataField != null
						&& !this.allowedHierarchyFields.contains(dataField)) {
					this.allowedHierarchyFields.add(dataField);
				}
			}
		}
		if (this.allowedHierarchyFields.isEmpty()) {
			System.err
					.println("There must be at least one hierarchy field allowed. Nulls are not allowed");
			return;
		}

		// set allowed size fields
		this.allowedSizeFields = new ArrayList<SummariseField>();
		if (allowedSizeFields != null) {
			Iterator<SummariseField> it = allowedSizeFields.iterator();
			while (it.hasNext()) {
				SummariseField dataField = it.next();
				if (!this.allowedSizeFields.contains(dataField))
					this.allowedSizeFields.add(dataField);
			}
		}
		if (this.allowedSizeFields.isEmpty())
			this.allowedSizeFields.add(null);// make sure there's at least one
												// element, even if it's null

		// set allowed order fields
		this.allowedOrderFields = new ArrayList<SummariseField>();
		if (allowedOrderFields != null) {
			Iterator<SummariseField> it = allowedOrderFields.iterator();
			while (it.hasNext()) {
				SummariseField dataField = it.next();
				if (!this.allowedOrderFields.contains(dataField)) {
					this.allowedOrderFields.add(dataField);
				}
			}
		}
		if (this.allowedOrderFields.isEmpty())
			this.allowedOrderFields.add(null);// make sure there's at least one
												// element, even if it's null

		// set allowed colour fields
		this.allowedColourFields = new ArrayList<SummariseField>();
		if (allowedColourFields != null) {
			this.allowedColourFields = allowedColourFields;
		}
		if (this.allowedColourFields.isEmpty())
			this.allowedColourFields.add(null);// make sure there's at least one
												// element, even if it's null

		// set allowed layouts
		this.allowedLayouts = new ArrayList<Layout>();
		if (allowedLayouts != null) {
			this.allowedLayouts = allowedLayouts;
		}

		applet.textFont(f);
		textHeight = (int) (applet.textAscent() + applet.textDescent());
		plusMinusW = (int) applet.textWidth("+");
		swapArrowW = (int) applet.textWidth("\u25C7");
		// calculate bounds
		int w = (int) applet.textWidth("Hierarchy:");// the widest label

		// work out gui height

		guiHeight = (textHeight)
				* (2 + sizeFields.length + orderFields.length
						+ colourFields.length + 1 + 1 + 1) + 4 + 4;

		bounds = new Rectangle(x, y, w + plusMinusW, guiHeight);

		flagToRelayoutGui = true;

		clickHereToStartTooltip = new Tooltip(applet, f, 12, 150);
		clickHereToStartTooltip.setAnchor(Direction.NORTH_WEST);
		clickHereToStartTooltip.setPointerSize(20);
		clickHereToStartTooltip.showPointer(true);
		clickHereToStartTooltip.setText("Click '+' to start adding data");

	}

	/**
	 * Sets the position for the top left corner
	 * 
	 * @param x
	 * @param y
	 */
	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
		flagToRelayoutGui = true;
	}

	/**
	 * Lays out whether each bit of text goes, for the current state. Called
	 * when the state changes. Lays text out so that the correct number of
	 * columns are used and they are the correct widths to fit the text in,
	 */
	private void layoutRectangles() {
		hierarchyRectangles = new Rectangle[this.hierarchyFields.length + 1];
		sizeRectangles = new Rectangle[sizeFields.length][this.hierarchyFields.length + 1];
		orderRectangles = new Rectangle[orderFields.length][this.hierarchyFields.length + 1];
		colourRectangles = new Rectangle[colourFields.length][this.hierarchyFields.length + 1];
		layoutRectangles = new Rectangle[this.hierarchyFields.length + 1];
		appearanceRectangles = new Rectangle[this.hierarchyFields.length + 1];
		filterRectangles = new Rectangle[this.hierarchyFields.length + 1];

		plusRectangles = new Rectangle[this.hierarchyFields.length + 1];
		minusRectangles = new Rectangle[this.hierarchyFields.length + 1];

		swapRectangles = new Rectangle[this.hierarchyFields.length + 1];

		// Find maximum lengths for each column
		int[] colWidths = new int[this.hierarchyFields.length + 1];
		applet.textFont(f);
		// do labels
		colWidths[0] = (int) applet.textWidth("Appearance:");// the widest label
		// then do the others
		for (int col = 0; col < this.hierarchyFields.length; col++) {
			colWidths[col + 1] = 0;
			int w = (int) applet.textWidth(hierarchyFields[col].getName());
			if (w > colWidths[col + 1]) {
				colWidths[col + 1] = w;
			}
			for (int j = 0; j < sizeFields.length; j++) {
				SummariseField summariseField = sizeFields[j][col];
				if (summariseField == null
						|| summariseField instanceof SummariseNull) {
					w = (int) applet.textWidth("FIXED");
				} else {
					w = (int) applet.textWidth(summariseField.getName());
				}
				if (w > colWidths[col + 1]) {
					colWidths[col + 1] = w;
				}
			}
			for (int j = 0; j < orderFields.length; j++) {
				SummariseField summariseField = orderFields[j][col];
				if (summariseField == null
						|| summariseField instanceof SummariseNull) {
					w = (int) applet.textWidth("HIERARCHY");
				} else {
					w = (int) applet.textWidth(summariseField.getName());
				}
				if (w > colWidths[col + 1]) {
					colWidths[col + 1] = w;
				}
			}
			for (int j = 0; j < colourFields.length; j++) {
				SummariseField summariseField = colourFields[j][col];
				if (summariseField == null
						|| summariseField instanceof SummariseNull) {
					w = (int) applet.textWidth("<no value>");
				} else {
					w = (int) applet.textWidth(summariseField.getName());
				}
				if (w > colWidths[col + 1]) {
					colWidths[col + 1] = w;
				}
			}
			w = (int) applet.textWidth(layouts[col].toString());
			if (w > colWidths[col + 1]) {
				colWidths[col + 1] = w;
			}
			w = (int) applet.textWidth(appearanceTypesEditing[col].toString()
					+ textHeight + 2);
			if (w > colWidths[col + 1]) {
				colWidths[col + 1] = w;
			}
			if (filterValues[col] != null) { // this is the case for <all> (no
												// filter at this level)
				w = (int) (applet.textWidth(filterValues[col].toString()) + applet
						.textWidth(" x"));
			} else {
				w = (int) (applet.textWidth("<all>"));
			}
			if (w > colWidths[col + 1]) {
				colWidths[col + 1] = w;
			}

		}
		// create rectangles
		int rectHeight = textHeight + 1;
		int cumX = 5;
		for (int col = 0; col < hierarchyFields.length + 1; col++) {
			hierarchyRectangles[col] = new Rectangle(cumX, textHeight,
					colWidths[col], rectHeight);
			for (int j = 0; j < sizeFields.length; j++) {
				sizeRectangles[j][col] = new Rectangle(cumX, (textHeight)
						* (2 + j), colWidths[col], rectHeight);
			}
			for (int j = 0; j < orderFields.length; j++) {
				orderRectangles[j][col] = new Rectangle(cumX, (textHeight)
						* (2 + sizeFields.length + j), colWidths[col],
						rectHeight);
			}
			for (int j = 0; j < colourFields.length; j++) {
				colourRectangles[j][col] = new Rectangle(cumX, (textHeight)
						* (2 + sizeFields.length + orderFields.length + j),
						colWidths[col], rectHeight);
			}
			layoutRectangles[col] = new Rectangle(
					cumX,
					(textHeight)
							* (2 + sizeFields.length + orderFields.length + colourFields.length),
					colWidths[col], rectHeight);

			minusRectangles[col] = new Rectangle(cumX + colWidths[col] / 2
					- plusMinusW / 2, 0, plusMinusW, rectHeight);
			plusRectangles[col] = new Rectangle(cumX + colWidths[col], 0,
					plusMinusW, rectHeight);
			if (col < hierarchyFields.length) {
				swapRectangles[col] = new Rectangle(cumX + colWidths[col] + 1,
						textHeight, swapArrowW, rectHeight);
			}

			appearanceRectangles[col] = new Rectangle(cumX, (textHeight)
					* (2 + sizeFields.length + orderFields.length
							+ colourFields.length + 1) + 4, colWidths[col],
					rectHeight);

			filterRectangles[col] = new Rectangle(cumX, (textHeight)
					* (2 + sizeFields.length + orderFields.length
							+ colourFields.length + 1 + 1) + 4, colWidths[col],
					rectHeight);

			cumX += colWidths[col] + 10;
		}
		bounds = new Rectangle(x, y, cumX + plusMinusW, guiHeight);
	}

	/**
	 * Draws GUI
	 * 
	 */
	public void draw() {
		if (flagToRelayoutGui) {
			flagToRelayoutGui = false;
			layoutRectangles();
		}
		int mouseX = applet.mouseX - x;
		int mouseY = applet.mouseY - y;

		applet.noStroke();
		applet.fill(240, 220);
		applet.rect(this.bounds.x, this.bounds.y,
				(int) hierarchyRectangles[0].getMaxX(), this.bounds.height);
		applet.fill(255, 220);
		applet.rect(this.bounds.x + (int) hierarchyRectangles[0].getMaxX(),
				this.bounds.y,
				this.bounds.width - (int) hierarchyRectangles[0].getMaxX(),
				this.bounds.height);

		applet.textFont(f);
		applet.fill(40);

		for (int col = 0; col < hierarchyFields.length + 1; col++) {
			if (col == 0) {
				applet.textAlign(PApplet.RIGHT, PApplet.TOP);
				applet.text("Hierarchy:",
						x + (int) hierarchyRectangles[col].getMaxX(), y
								+ hierarchyRectangles[col].y);
				for (int j = 0; j < sizeFields.length; j++) {
					String label = "Size";
					if (sizeFields.length > 1) {
						label += (j + 1);
					}
					applet.text(label + ":",
							x + (int) sizeRectangles[j][col].getMaxX(), y
									+ sizeRectangles[j][col].y);
				}
				for (int j = 0; j < orderFields.length; j++) {
					String label = "Order";
					if (orderFields.length > 1) {
						label += (j + 1);
					}
					applet.text(label + ":",
							x + (int) orderRectangles[j][col].getMaxX(), y
									+ orderRectangles[j][col].y);
				}
				for (int j = 0; j < colourFields.length; j++) {
					String label = "Colour";
					if (colourFields.length > 1) {
						label += (j + 1);
					}
					applet.text(label + ":",
							x + (int) colourRectangles[j][col].getMaxX(), y
									+ colourRectangles[j][col].y);
				}
				applet.text("Layout:",
						x + (int) layoutRectangles[col].getMaxX(), y
								+ layoutRectangles[col].y);
				applet.text("Appearance:",
						x + (int) appearanceRectangles[col].getMaxX(), y
								+ appearanceRectangles[col].y);
				applet.text("Filter:",
						x + (int) filterRectangles[col].getMaxX(), y
								+ filterRectangles[col].y);

				// and do the first plus sign
				applet.textAlign(PApplet.CENTER, PApplet.TOP);
				Rectangle r = plusRectangles[0];
				if (r.contains(mouseX, mouseY)) {
					applet.fill(150, 0, 0);
				} else {
					applet.fill(40);
				}
				applet.text("+", x + (int) r.getCenterX(), y + r.y);
				if (hierarchyFields.length == 0) {
					clickHereToStartTooltip.draw((float) r.getMaxX(),
							(float) r.getMaxY());
				}
			} else {
				applet.textAlign(PApplet.LEFT, PApplet.TOP);

				// minus signs
				Rectangle r = minusRectangles[col];
				if (r.contains(mouseX, mouseY)) {
					applet.fill(150, 0, 0);
				} else {
					applet.fill(40);
				}
				applet.text("\u2013", x + (int) r.getX(), y + r.y);

				// plus signs
				r = plusRectangles[col];
				if (r.contains(mouseX, mouseY)) {
					applet.fill(150, 0, 0);
				} else {
					applet.fill(40);
				}
				applet.text("+", x + (int) r.getX(), y + r.y);

				// swap arrows
				r = swapRectangles[col];
				if (r != null) {
					if (r.contains(mouseX, mouseY)) {
						applet.fill(150, 0, 0);
					} else {
						applet.fill(40);
					}
					applet.text("\u25C7", x + (int) r.getX(), y + r.y);
				}

				r = hierarchyRectangles[col];
				if (r.contains(mouseX, mouseY)) {
					applet.fill(150, 0, 0);
				} else {
					applet.fill(80);
				}
				applet.text(this.hierarchyFields[col - 1].getName(), x
						+ (int) r.getX(), y + r.y);

				for (int j = 0; j < sizeFields.length; j++) {
					r = sizeRectangles[j][col];
					if (r.contains(mouseX, mouseY)) {
						applet.fill(150, 0, 0);
					} else {
						applet.fill(80);
					}
					SummariseField summariseField = this.sizeFields[j][col - 1];
					String label = null;
					if (summariseField instanceof SummariseNull) {
						label = "FIXED";
					} else {
						label = summariseField.getName();
					}
					applet.text(label, x + (int) r.getX(), y + r.y);
				}

				for (int j = 0; j < orderFields.length; j++) {
					r = orderRectangles[j][col];
					if (r.contains(mouseX, mouseY)) {
						applet.fill(150, 0, 0);
					} else if (!layouts[col - 1].equals(Layout.TWO_DIMENSIONAL)
							&& !layouts[col - 1].equals(Layout.ABS_POSITION)
							&& j > 0) {
						applet.fill(180);// faint if one one layout used
					} else {
						applet.fill(80);
					}
					SummariseField summariseField = this.orderFields[j][col - 1];
					String label;
					if (summariseField instanceof SummariseNull == false) {
						label = summariseField.getName();
					} else {
						label = "NATURAL";
					}
					applet.text(label, x + (int) r.getX(), y + r.y);
				}

				for (int j = 0; j < colourFields.length; j++) {
					r = colourRectangles[j][col];
					if (r.contains(mouseX, mouseY)) {
						applet.fill(150, 0, 0);
					} else {
						applet.fill(80);
					}
					SummariseField summariseField = this.colourFields[j][col - 1];
					String label;
					if (summariseField instanceof SummariseNull) {
						label = "<none>";
					} else {
						label = summariseField.getName();
					}
					applet.text(label, x + (int) r.getX(), y + r.y);
				}

				r = layoutRectangles[col];
				if (r.contains(mouseX, mouseY)) {
					applet.fill(150, 0, 0);
				} else {
					applet.fill(80);
				}
				applet.text(this.layouts[col - 1].toString(),
						x + (int) r.getX(), y + r.y);

				r = appearanceRectangles[col];
				if (r.contains(mouseX, mouseY)
						&& mouseX < r.getMaxX() - textHeight) {
					applet.fill(150, 0, 0);
				} else {
					applet.fill(80);
				}
				applet.text(appearanceTypesEditing[col - 1].toString(), x
						+ (int) r.getX(), y + r.y);

				if (appearanceTypesEditing[col - 1].checkBox()) {
					applet.pushStyle();
					applet.strokeWeight(1.8f);
					if (r.contains(mouseX, mouseY)
							&& mouseX > r.getMaxX() - textHeight - 2) {
						applet.stroke(150, 0, 0);
					} else {
						applet.stroke(80);
					}
					if (appearanceValues[col - 1]
							.get(appearanceTypesEditing[col - 1]) == 0) {
						applet.noFill();
					} else {
						applet.fill(150);
					}
					applet.rect((int) (x + r.getMaxX() - textHeight + 2),
							(int) (y + r.getCenterY() - 1 - textHeight / 3f),
							textHeight * (2 / 3f), textHeight * (2 / 3f));
					applet.popStyle();
				} else {
					// draw up triangle
					if (r.contains(mouseX, mouseY)
							&& mouseX > r.getMaxX() - textHeight
							&& mouseY < r.getCenterY()) {
						applet.fill(150, 0, 0);
					} else {
						applet.fill(80);
					}
					applet.triangle((int) (x + r.getMaxX() - textHeight + 2),
							(int) (y + r.getCenterY() - 2),
							(int) (x + r.getMaxX() - (textHeight / 2)),
							(int) (y + r.y), (int) (x + r.getMaxX()), (int) (y
									+ r.getCenterY() - 2));

					// draw down triangle
					if (r.contains(mouseX, mouseY)
							&& mouseX > r.getMaxX() - textHeight
							&& mouseY > r.getCenterY()) {
						applet.fill(150, 0, 0);
					} else {
						applet.fill(80);
					}
					applet.triangle((int) (x + r.getMaxX() - textHeight + 2),
							(int) (y + r.getCenterY() + 2),
							(int) (x + r.getMaxX() - (textHeight / 2)),
							(int) (y + r.getMaxY()), (int) (x + r.getMaxX()),
							(int) (y + r.getCenterY() + 2));
				}

				r = filterRectangles[col];
				if (r.contains(mouseX, mouseY) && mouseX < r.getMaxX() - 20) {
					applet.fill(150, 0, 0);
				} else {
					applet.fill(80);
				}
				Object value = filterValues[col - 1];
				if (value == null) {
					value = "<all>";
				}
				applet.text(value.toString(), x + (int) r.getX(), y + r.y);
				if (filterValues[col - 1] != null) {
					if (r.contains(mouseX, mouseY) && mouseX > r.getMaxX() - 10) {
						applet.fill(150, 0, 0);
					} else {
						applet.fill(80);
					}
					applet.textAlign(PConstants.RIGHT, PConstants.TOP);
					applet.text("x", x + (int) r.getMaxX(), y + r.y);
				}
			}
		}

	}

	/**
	 * Return the bounds
	 * 
	 * @return
	 */
	public Rectangle getBounds() {
		return bounds;
	}

	/**
	 * Find whether a coordinate is inside the GUI area
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean contains(float x, float y) {
		return bounds.contains(x, y);
	}

	/**
	 * Should call this when the mouse is clicked
	 * 
	 */
	synchronized public void mouseClicked() {

		// offset mouse coordinate with GUI position - take mouse coords from
		// the main Processing sketch
		int mouseX = applet.mouseX - x;
		int mouseY = applet.mouseY - y;

		// Only do something if mouse is in area
		if (bounds.contains(applet.mouseX, applet.mouseY)) {

			// Change the value of any hierarchy variables clicked (forward/back
			// with left/right mouse button)
			for (int i = 1; i < hierarchyFields.length + 1; i++) {
				if (hierarchyRectangles[i].contains(mouseX, mouseY)) {
					int idx = allowedHierarchyFields
							.indexOf(hierarchyFields[i - 1]);
					if (idx == -1) {
						idx = 0;
					}
					if (applet.mouseEvent.getButton() == MouseEvent.BUTTON1) {
						idx++;
						if (idx > allowedHierarchyFields.size() - 1) {
							idx = 0;
						}
					} else if (applet.mouseEvent.getButton() == MouseEvent.BUTTON3) {
						idx--;
						if (idx < 0) {
							idx = allowedHierarchyFields.size() - 1;
						}
					}
					hierarchyFields[i - 1] = allowedHierarchyFields.get(idx);

					// check filter value is valid for this variable - otherwise
					// set to null (<all>)
					if (filterValues[i - 1] != null
							&& !hierarchyFields[i - 1].getOrderValues()
									.contains(filterValues[i - 1])) {
						filterValues[i - 1] = null;
					}

					hierHasChanged = true;
					flagToRelayoutGui = true;
				}
			}

			// Change the value of any order variables clicked (forward/back
			// with left/right mouse button)
			for (int j = 0; j < orderFields.length; j++) {
				for (int i = 1; i < hierarchyFields.length + 1; i++) { // use
																		// hierarchyFields.length,
																		// because
																		// it's
																		// per
																		// level
					if (orderRectangles[j][i].contains(mouseX, mouseY)) {
						int idx = allowedOrderFields
								.indexOf(orderFields[j][i - 1]);
						if (idx == -1) {
							idx = 0;
						}
						if (applet.mouseEvent.getButton() == MouseEvent.BUTTON1) {
							idx++;
							if (idx > allowedOrderFields.size() - 1) {
								idx = 0;
							}
						} else if (applet.mouseEvent.getButton() == MouseEvent.BUTTON3) {
							idx--;
							if (idx < 0) {
								idx = allowedOrderFields.size() - 1;
							}
						}
						orderFields[j][i - 1] = allowedOrderFields.get(idx);
						orderHasChanged = true;
						flagToRelayoutGui = true;
					}
				}
			}

			// Change the value of any size variables clicked (forward/back with
			// left/right mouse button)
			for (int j = 0; j < sizeFields.length; j++) {
				for (int i = 1; i < hierarchyFields.length + 1; i++) { // use
																		// hierarchyFields.length,
																		// because
																		// it's
																		// per
																		// level
					if (sizeRectangles[j][i].contains(mouseX, mouseY)) {
						int idx = allowedSizeFields
								.indexOf(sizeFields[j][i - 1]);
						if (idx == -1) {
							idx = 0;
						}
						if (applet.mouseEvent.getButton() == MouseEvent.BUTTON1) {
							idx++;
							if (idx > allowedSizeFields.size() - 1) {
								idx = 0;
							}
						} else if (applet.mouseEvent.getButton() == MouseEvent.BUTTON3) {
							idx--;
							if (idx < 0) {
								idx = allowedSizeFields.size() - 1;
							}
						}
						sizeFields[j][i - 1] = allowedSizeFields.get(idx);
						sizeHasChanged = true;
						flagToRelayoutGui = true;
					}
				}
			}

			// Change the value of any colour variables clicked (forward/back
			// with left/right mouse button)
			for (int j = 0; j < colourFields.length; j++) {
				for (int i = 1; i < hierarchyFields.length + 1; i++) { // use
																		// hierarchyFields.length,
																		// because
																		// it's
																		// per
																		// level
					if (colourRectangles[j][i].contains(mouseX, mouseY)) {
						int idx = allowedColourFields
								.indexOf(colourFields[j][i - 1]);
						if (idx == -1) {
							idx = 0;
						}
						if (applet.mouseEvent.getButton() == MouseEvent.BUTTON1) {
							idx++;
							if (idx > allowedColourFields.size() - 1) {
								idx = 0;
							}
						} else if (applet.mouseEvent.getButton() == MouseEvent.BUTTON3) {
							idx--;
							if (idx < 0) {
								idx = allowedColourFields.size() - 1;
							}
						}
						colourFields[j][i - 1] = allowedColourFields.get(idx);
						colourHasChanged = true;
						flagToRelayoutGui = true;
					}
				}
			}

			// Change the value of any layouts clicked (forward/back with
			// left/right mouse button)
			for (int i = 1; i < hierarchyFields.length + 1; i++) {
				if (layoutRectangles[i].contains(mouseX, mouseY)) {
					int idx = allowedLayouts.indexOf(layouts[i - 1]);
					if (idx == -1) {
						idx = 0;
					}
					if (applet.mouseEvent.getButton() == MouseEvent.BUTTON1) {
						idx++;
						if (idx > allowedLayouts.size() - 1) {
							idx = 0;
						}
					} else if (applet.mouseEvent.getButton() == MouseEvent.BUTTON3) {
						idx--;
						if (idx < 0) {
							idx = allowedLayouts.size() - 1;
						}
					}
					layouts[i - 1] = allowedLayouts.get(idx);
					layoutHasChanged = true;
					flagToRelayoutGui = true;
				}
			}

			// Change any appearance type clicked (forward/back with left/right
			// mouse button)
			// change the actual value if the arrows are clicked
			for (int i = 1; i < appearanceValues.length + 1; i++) {
				// change appearance type being edited
				if (appearanceRectangles[i].contains(mouseX, mouseY)
						&& mouseX < appearanceRectangles[i].getMaxX()
								- textHeight) {
					AppearanceType[] appearanceValues = AppearanceType.values();
					int idx = appearanceTypesEditing[i - 1].ordinal();
					if (idx == -1) {
						idx = 0;
					}
					if (applet.mouseEvent.getButton() == MouseEvent.BUTTON1) {
						idx++;
						if (idx >= appearanceValues.length) {
							idx = 0;
						}
					} else if (applet.mouseEvent.getButton() == MouseEvent.BUTTON3) {
						idx--;
						if (idx < 0) {
							idx = appearanceValues.length - 1;
						}
					}
					appearanceTypesEditing[i - 1] = appearanceValues[idx];
					flagToRelayoutGui = true;
				}
				if (appearanceRectangles[i].contains(mouseX, mouseY)) {
					AppearanceType appearanceType = appearanceTypesEditing[i - 1];
					// if a checkbox
					if (appearanceType.checkBox()) {
						if (mouseX > appearanceRectangles[i].getMaxX()
								- textHeight) {
							if (appearanceValues[i - 1].get(appearanceType) == 0) {
								appearanceValues[i - 1].put(appearanceType, 1);
							} else {
								appearanceValues[i - 1].put(appearanceType, 0);
							}
							// flag to build if user allows/disallows nulls
							if (appearanceType == AppearanceType.INCLUDE_NULLS) {
								hierHasChanged = true;
							}
							// show labels if user changes autosize text
							if (appearanceType == AppearanceType.AUTOSIZE_LABELS) {
								appearanceValues[i - 1].put(
										AppearanceType.SHOW_LABELS, 1);
							}
							appearanceHasChanged = true;
						}
					} else {
						// if clicked on up button
						if (appearanceRectangles[i].contains(mouseX, mouseY)
								&& mouseX > appearanceRectangles[i].getMaxX()
										- textHeight
								&& mouseY < appearanceRectangles[i]
										.getCenterY()) {

							int v = appearanceValues[i - 1].get(appearanceType);
							v += appearanceType.incrementSize();
							if (v > appearanceType.maxValue()) {
								v = appearanceType.maxValue();
							}
							appearanceValues[i - 1].put(appearanceType, v);
							// show labels if text size/opacity is changed
							if (appearanceType == AppearanceType.LABEL_OPACITY) {
								appearanceValues[i - 1].put(
										AppearanceType.SHOW_LABELS, 1);
							}
							if (appearanceType == AppearanceType.LABEL_SIZE) {
								appearanceValues[i - 1].put(
										AppearanceType.SHOW_LABELS, 1);
								appearanceValues[i - 1].put(
										AppearanceType.AUTOSIZE_LABELS, 0);
							}
							// rebuild if padding is changed
							if (appearanceType == AppearanceType.PADDING) {
								hierHasChanged = true;
							}
							// rebuild if border width is changed
							// (unfortunately, this is only to force a treemap
							// redraw rather then just the labels
							if (appearanceType == AppearanceType.BORDER_WIDTH) {
								hierHasChanged = true;
							}
							appearanceHasChanged = true;
						}
						// if clicked on down button
						if (appearanceRectangles[i].contains(mouseX, mouseY)
								&& mouseX > appearanceRectangles[i].getMaxX()
										- textHeight
								&& mouseY > appearanceRectangles[i]
										.getCenterY()) {
							if (appearanceTypesEditing[i - 1]
									.equals(appearanceType)) {
								int v = appearanceValues[i - 1]
										.get(appearanceType);
								v -= appearanceType.incrementSize();
								if (v < appearanceType.minValue()) {
									v = appearanceType.minValue();
								}
								appearanceValues[i - 1].put(appearanceType, v);
								// force a rebuild if user wants to
								// include/exclude nulls
								// show labels if text size/opacity is changed
								if (appearanceType == AppearanceType.LABEL_OPACITY) {
									appearanceValues[i - 1].put(
											AppearanceType.SHOW_LABELS, 1);
								}
								if (appearanceType == AppearanceType.LABEL_SIZE) {
									appearanceValues[i - 1].put(
											AppearanceType.SHOW_LABELS, 1);
									appearanceValues[i - 1].put(
											AppearanceType.AUTOSIZE_LABELS, 0);
								}
								// rebuild if padding is changed
								if (appearanceType == AppearanceType.PADDING) {
									hierHasChanged = true;
								}
								// rebuild if border width is changed
								// (unfortunately, this is only to force a
								// treemap redraw rather then just the labels
								if (appearanceType == AppearanceType.BORDER_WIDTH) {
									hierHasChanged = true;
								}
							}
							appearanceHasChanged = true;
						}
					}
				}
			}

			// Change the value of any filters clicked (forward/back with
			// left/right mouse button)
			for (int i = 1; i < hierarchyFields.length + 1; i++) {
				if (filterRectangles[i].contains(mouseX, mouseY)) {

					if (mouseX < filterRectangles[i].getMaxX() - 15) {
						// clicked on the word, not the x

						Integer idx = hierarchyFields[i - 1].getOrderValues()
								.indexOf(filterValues[i - 1]);

						if ((applet.mouseEvent.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
							if (idx == -1) {
								idx = 0;
							} else {
								idx++;
							}
							if (idx > hierarchyFields[i - 1].getOrderValues()
									.size() - 1) {
								idx = null;
							}
						} else if ((applet.mouseEvent.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {
							if (idx == -1) {
								idx = hierarchyFields[i - 1].getOrderValues()
										.size() - 1;
							} else {
								idx--;
							}
							if (idx < 0) {
								idx = null;
							}
						}
						if (idx == null) {
							filterValues[i - 1] = null;
						} else {
							filterValues[i - 1] = hierarchyFields[i - 1]
									.getOrderValues().get(idx);
						}
					} else {
						// clicked on the x
						filterValues[i - 1] = null;
					}
					hierHasChanged = true;
					flagToRelayoutGui = true;
				}
			}

			// minus signs
			if (minusRectangles.length > 0 && allowedSizeFields != null) {
				for (int i = 1; i < minusRectangles.length; i++) {
					if (minusRectangles[i].contains(mouseX, mouseY)) {
						cut(i - 1);
						hierHasChanged = true;
						flagToRelayoutGui = true;
					}
				}
			}

			// plus sign
			if (plusRectangles.length > 0 && allowedSizeFields != null) {
				for (int i = 0; i < plusRectangles.length; i++) {// start at 0
																	// for the
																	// plus sign
					if (plusRectangles[i].contains(mouseX, mouseY)) {
						DataField defaultHierField = null;
						SummariseField[] defaultOrderField = new SummariseField[orderFields.length];
						SummariseField[] defaultSizeField = new SummariseField[sizeFields.length];
						SummariseField[] defaultColourField = new SummariseField[colourFields.length];
						Layout defaultLayout = null;
						// if the first, use the top of the allowed lists
						if (i == 0) {
							if (!allowedHierarchyFields.isEmpty()) {
								defaultHierField = allowedHierarchyFields
										.get(0);
							}
							if (!allowedOrderFields.isEmpty()) {
								for (int j = 0; j < orderFields.length; j++) {
									defaultOrderField[j] = allowedOrderFields
											.get(0);
								}
							}
							if (!allowedSizeFields.isEmpty()) {
								for (int j = 0; j < sizeFields.length; j++) {
									defaultSizeField[j] = allowedSizeFields
											.get(0);
								}
							}
							if (!allowedColourFields.isEmpty()) {
								for (int j = 0; j < colourFields.length; j++) {
									defaultColourField[j] = allowedColourFields
											.get(0);
								}
							}
							if (!allowedLayouts.isEmpty()) {
								defaultLayout = allowedLayouts.get(0);
							}
						} else {
							// use the value to the left
							defaultHierField = hierarchyFields[i - 1];
							for (int j = 0; j < orderFields.length; j++) {
								defaultOrderField[j] = orderFields[j][i - 1];
							}
							for (int j = 0; j < sizeFields.length; j++) {
								defaultSizeField[j] = sizeFields[j][i - 1];
							}
							for (int j = 0; j < colourFields.length; j++) {
								defaultColourField[j] = colourFields[j][i - 1];
							}
							defaultLayout = layouts[i - 1];
						}
						insert(i, defaultHierField, defaultOrderField,
								defaultSizeField, defaultColourField,
								defaultLayout);
						hierHasChanged = true;
						flagToRelayoutGui = true;
					}
				}
			}

			// swap signs
			if (swapRectangles.length > 0 && allowedSizeFields != null) {
				for (int i = 0; i < swapRectangles.length - 1; i++) {
					if (swapRectangles[i].contains(mouseX, mouseY)) {
						swap(i, i + 1);
						hierHasChanged = true;
						flagToRelayoutGui = true;
					}
				}
			}

		}
		applet.loop();
	}

	/**
	 * Apply operator
	 * 
	 */
	public boolean applyOperator(Expression expression)
			throws ExpressionNotSupportedException {
		boolean result = super.applyOperator(expression);
		// relayout rectangles
		flagToRelayoutGui = true;
		return result;
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
		super.insert(level, hierarchyField, orderField, sizeField, colourField,
				layout);
		flagToRelayoutGui = true;

		// Shift the information about which appearance state is being edited
		// one level after where the new level has been inserted
		AppearanceType[] appearanceTypeEditing = new AppearanceType[this
				.getNumLevels()];
		for (int i = 0; i < appearanceTypeEditing.length; i++) {
			if (i < level) {
				appearanceTypeEditing[i] = this.appearanceTypesEditing[i];
			} else if (i == level) {
				if (level != 0) {
					appearanceTypeEditing[i] = this.appearanceTypesEditing[i - 1];
				} else {
					appearanceTypeEditing[i] = AppearanceType.SHOW_LABELS;
				}
			} else {
				appearanceTypeEditing[i] = this.appearanceTypesEditing[i - 1];
			}
		}
		this.appearanceTypesEditing = appearanceTypeEditing;
	}

	/**
	 * Cut a level
	 * 
	 * @param level
	 *            The level that needs to be cut
	 */
	public void cut(int level) {
		super.cut(level);
		flagToRelayoutGui = true;

		// Remove the information about which appearance state is being edited
		AppearanceType[] appearanceTypeEditing = new AppearanceType[this
				.getNumLevels()];
		for (int i = 0; i < appearanceTypeEditing.length; i++) {
			if (i < level) {
				appearanceTypeEditing[i] = this.appearanceTypesEditing[i];
			} else {
				appearanceTypeEditing[i] = this.appearanceTypesEditing[i + 1];
			}
		}
		this.appearanceTypesEditing = appearanceTypeEditing;
	}

	public void clear() {
		super.clear();
		flagToRelayoutGui = true;
	}

}