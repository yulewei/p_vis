package edu.zjut.vis.treemap;

import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.tree.TreeNode;

import processing.core.PApplet;
import processing.core.PFont;
import edu.zjut.common.data.attr.FieldType;
import edu.zjut.treemap.core.Layout;
import edu.zjut.treemap.core.TreemapPanel;
import edu.zjut.treemap.core.TreemapState;
import edu.zjut.treemap.core.TreemapSummaryNode;
import edu.zjut.treemap.data.SummariseNode;
import edu.zjut.treemap.summary.SummariseField;
import edu.zjut.treemap.summary.SummariseNull;

/**
 * 
 * @author yulewei
 * 
 */
public class PTreemap extends PApplet {
	PFont font;
	DecimalFormat df = new DecimalFormat("0.00");

	TreemapState treemapState;
	TreemapPanel treemapPanel;

	private List<Layout> layouts;
	List<SummariseField> summariseFields;
	List<Object[]> records;

	int oldW, oldH;

	String tooltipLabel = null;
	String tooltipData = null;

	public PTreemap() {
		layouts = new ArrayList<Layout>();
		layouts.add(Layout.ST);
		layouts.add(Layout.HZ);
		layouts.add(Layout.VT);
		layouts.add(Layout.SP);
		layouts.add(Layout.SA);

		font = createFont("FFScala", 12);
		frameRate(30);
	}

	public void setData(TreemapState treemapState,
			List<SummariseField> summariseFields, List<Object[]> records) {
		this.treemapState = treemapState;
		this.summariseFields = summariseFields;
		this.records = records;

		treemapPanel = new TreemapPanel(this,
				new Rectangle(0, 0, width, height), records, summariseFields);
	}

	public void setup() {
		size(800, 800);
	}

	public void draw() {
		if (treemapPanel == null)
			return;

		if ((oldW != width || oldH != height)) {
			oldW = width;
			oldH = height;
			treemapPanel.setBounds(new Rectangle(0, 0, width, height));
		}

		treemapPanel.setTextFont(font);
		treemapPanel.draw(treemapState);

		if (tooltipLabel != null)
			drawTooltip();
	}

	private void drawTooltip() {
		textFont(font);
		textSize(12);
		float w = textWidth(tooltipLabel) + 2;
		float h = 14;
		if (tooltipData != null) {
			String[] toks = tooltipData.split("\\\n");
			h += toks.length * 11;

			textSize(10);
			for (String tok : toks) {
				float localW = textWidth(tok);
				if (localW > w)
					w = localW;
			}
		}

		float offsetX = 3;
		float offsetY = 3;
		stroke(80, 20);
		textAlign(LEFT, TOP);
		float x = mouseX + 10;
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
		text(tooltipData == null ? "" : tooltipData, x + offsetX, mouseY
				+ offsetY + 14);
	}

	public void mouseMoved() {
		// redraw everytime the mouse is moved
		loop();

		tooltipLabel = null;
		tooltipData = null;

		// identify the node (rectangle) that the mouse is over
		TreemapSummaryNode node = treemapPanel.getNodeFromMouse(mouseX, mouseY);
		treemapPanel.setHighlightNode(node);

		if (node == null)
			return;

		SummariseNode summariseNode = node.getSummariseNode();

		tooltipLabel = "";
		TreeNode[] treenodes = summariseNode.getPath();
		for (int i = 1; i < treenodes.length; i++) {
			String label = ((SummariseNode) treenodes[i])
					.getConditioningValueAsString();
			if (label == null) {
				label = "<no value>";
			} else {
				label = label.replaceAll("\\_", " ");
			}

			tooltipLabel += label;
			if (i < treenodes.length - 1) {
				tooltipLabel += " > ";
			}
		}

		tooltipData = "";

		HashSet<SummariseField> summaryFields = new HashSet<SummariseField>();

		// sizes
		SummariseField[][] sizeFields = treemapState.getSizeFields();
		for (int i = 0; i < sizeFields.length; i++) {
			summaryFields.add(sizeFields[i][sizeFields[i].length - 1]);
		}

		// orders
		SummariseField[][] orderFields = treemapState.getOrderFields();
		for (int i = 0; i < orderFields.length; i++) {
			summaryFields.add(orderFields[i][orderFields[i].length - 1]);
		}

		// colours
		SummariseField[][] colourFields = treemapState.getColourFields();
		for (int i = 0; i < colourFields.length; i++) {
			SummariseField summariseField = colourFields[i][colourFields[i].length - 1];
			if (summariseField != null
					&& summariseField instanceof SummariseNull == false) {
				summaryFields.add(summariseField);
			}
		}
		summaryFields.remove(null);

		// find the values of each
		for (SummariseField field : summaryFields) {
			if (field instanceof SummariseNull)
				continue;

			String value = "";
			if (field.getFieldType() == FieldType.STRING)
				value = summariseNode.getSummaryAsString(field);

			if (field.getFieldType() == FieldType.INT)
				value = summariseNode.getSummaryAsLong(field) + "";
			else
				value = df.format(summariseNode.getSummaryAsDouble(field));

			tooltipData += field.getName() + "=" + value;
			tooltipData += "\n";
		}

		int n = summariseNode.getRecords().size();
		if (n == 1) {
			tooltipData += n + " record";
		} else {
			tooltipData += n + " records";
		}
	}
}
