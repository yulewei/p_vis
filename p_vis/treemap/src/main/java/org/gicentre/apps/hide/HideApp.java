package org.gicentre.apps.hide;

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.tree.TreeNode;

import org.gicentre.apps.hide.TreemapState.AppearanceType;
import org.gicentre.data.SummariseNode;
import org.gicentre.data.summary.SummariseField;
import org.gicentre.data.summary.SummariseNull;
import org.gicentre.hive.Expression;
import org.gicentre.hive.ExpressionNotSupportedException;
import org.gicentre.hive.Type;
import org.gicentre.treemaps.TreemapSummaryNode;

import edu.zjut.common.data.attr.FieldType;
import edu.zjut.vis.treemap.ConfigDataLoader;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

/**
 * HiDE Software application for exploring datasets through hierarchical layouts
 * 
 * @author Aidan Slingsby, giCentre
 * 
 */
public class HideApp extends PApplet {

	private static final long serialVersionUID = -5883429178827883301L;
	private static final String versionNumber = "2.1.1";
	PFont font; // font
	TreemapStateGui treemapStateGui; // dual role of graphic state and GUI
	TreemapPanel treemapPanel; // draws the graphic
	DecimalFormat df = new DecimalFormat("0.00");

	ConfigDataLoader dataLoader = null;

	// tooltip replacement
	String tooltipLabel = null;
	String tooltipData = null;

	private boolean showHive = false;

	int oldW, oldH; // old width and height

	boolean showGui = false;

	// Need to register your application with Twitter, and enter the
	// keys there supply below.
	String customerKey = "REPLACE_WITH_YOUR_CUSTOMER_KEY";
	String customerSecret = "REPLACE_WITH_YOUR_CUSTOMER_SECRET";

	/**
	 * Main method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		PApplet.main(new String[] { "org.gicentre.apps.hide.HideApp" });
	}

	/**
	 * Initialises the application
	 */
	public void setup() {
		println("HiDE: Hierarchical Data Explorer - v" + versionNumber);
		println("giCentre, City University London");
		println("http://gicentre.org/hide/");

		if (frame != null) {
			frame.setResizable(true);
		}

		size(800, 600);
		// smooth();
		oldW = width;
		oldH = height;

		// creates the font to use
		// font = createFont("Tahoma", 12);
		font = createFont("FFScala", 12);

		// String filename = this.getParameter("datasetName");
		String filename = "data/soufan_top100.xml";
		dataLoader = new ConfigDataLoader(filename);

		// create a new treemap state
		treemapStateGui = new TreemapStateGui(this, 0, 0, font, 2, 1, 1,
				dataLoader.getAllowedHierVars(),
				dataLoader.getAllowedOrderVars(),
				dataLoader.getAllowedSizeVars(),
				dataLoader.getAllowedColourVars(), dataLoader.getLayouts());

		// create new treemap panel
		treemapPanel = new TreemapPanel(this, treemapStateGui, font,
				new Rectangle(0, 0, width, height), dataLoader.getRecords(),
				dataLoader.getColumnValues(), dataLoader.getSummariseFields());

		String datasetName = dataLoader.getDatasetName();

		if (frame != null) {
			if (datasetName == null) {
				frame.setTitle("HiDE v" + versionNumber
						+ ": Hierarchical Data Explorer");
			} else {
				frame.setTitle("HiDE v" + versionNumber
						+ ": Hierarchical Data Explorer [" + datasetName + "]");
			}
		}

		frameRate(30);

		restoreState("sHier(/,$区县,$楼盘); sOrder(/,HIER,HIER); sSize(/,FX,FX); sColor(/,HIER,$均价); sLayout(/,SF,SF);");

		// ensure window redraws when a window is resized
		this.addComponentListener(new java.awt.event.ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				loop();
			}
		});
	}

	/**
	 * Draws the screen Overrides PApplets draw()
	 */
	@Override
	public void draw() {
		// if a treemapPanel exists (there isn't if no data is loaded) and the
		// app window has changed size, change its size
		if ((oldW != width || oldH != height)) {
			oldW = width;
			oldH = height;
			treemapPanel.setBounds(new Rectangle(0, 0, width, height));
		}

		// white background
		background(255);

		// otherwise draw the graphic
		treemapPanel.draw();

		if (treemapStateGui.getHierarchyFields().length > 0
				&& treemapPanel.treemap == null) {
			fill(150);
			textFont(font);
			textSize(20);
			textAlign(CENTER, CENTER);
			text("No data to display.\n\nTry removing some of the filters.",
					width / 2, height / 2);
		}

		if (showGui) {
			treemapStateGui.draw();
		}

		if (treemapStateGui.getHierarchyFields().length == 0) {
			fill(150);
			textFont(font);
			textSize(20);
			textAlign(CENTER, CENTER);
			text("Click the + symbol above to start showing data.\n\nPress 'h' for help.",
					width / 2, height / 2);
		}

		if (tooltipLabel != null) {
			textSize(12);
			// work out w and h
			int w = (int) textWidth(tooltipLabel) + 2;
			int h = 14;
			if (tooltipData != null) {
				String toks[] = tooltipData.split("\\\n");
				h += toks.length * 11;
				for (String tok : toks) {
					textSize(10);
					int localW = (int) textWidth(tok);
					if (localW > w) {
						w = localW;
					}
				}
			}

			int offsetX = 3;
			int offsetY = 3;
			stroke(80, 20);
			textAlign(LEFT, TOP);
			int x = mouseX + 10;
			if (x + w > width) {
				x = width - w - 2 * offsetX;
			}
			fill(255, 248, 147, 200);
			rect(x, mouseY, w + 2 * offsetX, h + 2 * offsetY);
			fill(80);
			textSize(12);
			text(tooltipLabel, x + offsetX, mouseY + offsetY);
			fill(100);
			textSize(10);
			textLeading(11);
			text(tooltipData, x + offsetX, mouseY + offsetY + 14);
		}

		if (showHive) {
			textAlign(CENTER, BOTTOM);
			textSize(20);
			fill(50);
			if (!treemapStateGui.isEmpty()) {
				text(Expression.formatExpression(treemapStateGui.getState()),
						0, 0, width, height);
			} else {
				text("", 0, 0, width, height);
			}
		}

		// only stop looping (drawing) if not in transition and not
		// capturing
		if (!treemapPanel.isInTransition()) {
			noLoop();
		}
	}

	private void restoreState(String hive) {
		Collection<Expression> expressions = Expression.parseExpressions(hive);
		treemapStateGui.applyExpressions(expressions);

		treemapPanel.flagToDoStructuralRebuild();
		System.out.println("Restored state to " + hive);
	}

	public void keyPressed() {

		if (key == 'e' || key == 'E') {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
			String filename = "screenshot_" + dateFormat.format(new Date())
					+ ".png";
			PImage image = treemapPanel.getImageToTweet();
			image.save(filename);
		}

		// restoring (pasting) a state from the clipboard
		// keyCode+32 because this is how you
		// find the key if CTRL is pressed
		if (treemapPanel != null
				&& (keyEvent.isControlDown() || keyEvent.isMetaDown())
				&& (key == 'v' || key == 'V' || keyCode + 32 == 'v' || keyCode + 32 == 'V')) {
			String state = null;
			try {
				Clipboard clipboard = Toolkit.getDefaultToolkit()
						.getSystemClipboard();
				state = clipboard.getContents(this)
						.getTransferData(DataFlavor.stringFlavor).toString();
			} catch (UnsupportedFlavorException e) {
				System.err.println(e);
			} catch (IOException e) {
				System.err.println(e);
			}
			if (state != null) {
				restoreState(state);
			}

		}

		// restore a state from a QR code in chart.png

		// if (key=='r'){
		// restoreGraphicFromQR(loadImage("/Users/sbbb717/Desktop/chart.png").getImage());
		// }

		// local colouring
		if (treemapPanel != null && key == 'l' || key == 'L') {
			treemapPanel.setUseLocalMin(!treemapPanel.getUseLocalMin());
			treemapPanel.flagToRedraw();
		}

		// show/hide GUI
		if (treemapPanel != null && key == 'g' || key == 'G') {
			showGui = !showGui;
			// oldH=oldH+1;//just make it look like the window has resized
		}

		// show/hide HiVE expression
		if (treemapPanel != null && !keyEvent.isControlDown()
				&& !keyEvent.isMetaDown() && key == 'v' || key == 'V') {
			showHive = !showHive;
		}

		if (treemapPanel != null && key == 't' || key == 'T') {
			treemapPanel.setTransitionsOn(!treemapPanel.isTransitionsOn());
		}

		loop();
	}

	/**
	 * Mousemoved event
	 */
	public void mouseMoved() {

		// redraw everytime the mouse is moved
		loop();
		tooltipLabel = null;
		tooltipData = null;
		if (!showGui || (showGui && !treemapStateGui.contains(mouseX, mouseY))) {
			// identify the node (rectangle) that the mouse is over
			TreemapSummaryNode node = treemapPanel.getNodeFromMouse();
			if (node != null) {
				tooltipLabel = "";
				TreeNode[] treenodes = node.getSummariseNode().getPath();

				// construct the path through the hierarchy to the path
				for (int i = 1; i < treenodes.length; i++) {
					String thisLabel = ((SummariseNode) treenodes[i])
							.getConditioningValueAsString();
					if (thisLabel == null) {
						thisLabel = "<no value>";
					} else {
						thisLabel = thisLabel.replaceAll("\\_", " ");
					}
					tooltipLabel += thisLabel;
					if (i < treenodes.length - 1) {
						tooltipLabel += " > ";
					}
				}
				tooltipData = "";
				// establish the summary variable values shown as part of the
				// state
				HashSet<SummariseField> summaryFieldsToDisplay = new HashSet<SummariseField>();
				// sizes
				for (int i = 0; i < treemapStateGui.getSizeFields().length; i++) {
					summaryFieldsToDisplay
							.add(treemapStateGui.getSizeFields()[i][treemapStateGui
									.getSizeFields()[i].length - 1]);
				}
				// orders
				for (int i = 0; i < treemapStateGui.getOrderFields().length; i++) {
					summaryFieldsToDisplay
							.add(treemapStateGui.getOrderFields()[i][treemapStateGui
									.getOrderFields()[i].length - 1]);
				}
				// colours
				for (int i = 0; i < treemapStateGui.getColourFields().length; i++) {
					SummariseField summariseField = treemapStateGui
							.getColourFields()[i][treemapStateGui
							.getColourFields()[i].length - 1];
					if (summariseField != null
							&& summariseField instanceof SummariseNull == false) {
						summaryFieldsToDisplay.add(summariseField);
						;
					}
				}
				summaryFieldsToDisplay.remove(null);

				// find the values of each
				for (SummariseField summariseField : summaryFieldsToDisplay) {
					String value = "";
					if (summariseField instanceof SummariseNull == false) {
						if (summariseField.getFieldType() == FieldType.STRING) {
							value = node.getSummariseNode().getSummaryAsString(
									summariseField);
						}
						if (summariseField.getFieldType() == FieldType.INT) {
							value = node.getSummariseNode().getSummaryAsLong(
									summariseField)
									+ "";
						} else {
							Double v = node.getSummariseNode()
									.getSummaryAsDouble(summariseField);
							if (v != null) {
								if (Math.abs(v) >= 1) {
									value = df.format(v);
								} else {
									MathContext mc = new MathContext(2,
											RoundingMode.HALF_UP);
									BigDecimal bigDecimal = new BigDecimal(v,
											mc);
									value = bigDecimal.toPlainString();
								}
							}
						}
						tooltipData += summariseField.getName() + "=" + value;
						tooltipData += "\n";
					}
				}
				int n = node.getSummariseNode().getRecords().size();
				if (n == 1) {
					tooltipData += n + " record";
				} else {
					tooltipData += n + " records";
				}
				// set the text on the tooltip and enable
			}
		}
	}

	/**
	 * Mouseclicked event
	 */
	public void mouseClicked() {
		// check that the mouse click is relevant to the GUI
		if (treemapStateGui != null && showGui) {
			treemapStateGui.mouseClicked();
		}
		// redraw
		loop();
	}
}
