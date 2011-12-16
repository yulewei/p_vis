package edu.zjut.treemap.core;

import java.awt.geom.Point2D;
import java.util.Iterator;

import org.gicentre.treemappa.TreeMapNode;

import edu.zjut.treemap.data.SummariseNode;

public class TreemapSummaryNode extends TreeMapNode {

	private SummariseNode summaryNode;

	public TreemapSummaryNode(String label, double order, Float sizeValue,
			Float colourValue, SummariseNode summaryNode) {
		super(label, order, sizeValue, colourValue);
		this.summaryNode = summaryNode;
	}

	public TreemapSummaryNode(String label, double order, Float sizeValue,
			Float colourValue, Point2D location, SummariseNode summaryNode) {
		super(label, order, sizeValue, colourValue, location);
		this.summaryNode = summaryNode;
	}

	public String getPathId() {
		return this.summaryNode.getPathId();
	}

	public SummariseNode getSummariseNode() {
		return this.summaryNode;
	}

	public TreemapSummaryNode getNode(String pathId) {
		Iterator<TreeMapNode> it = this.iterator();
		while (it.hasNext()) {
			TreemapSummaryNode node = (TreemapSummaryNode) it.next();
			if (node.getPathId().equals(pathId)) {
				return node;
			}
		}
		return null;
	}
}
