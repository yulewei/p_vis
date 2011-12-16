package edu.zjut.treemap.core;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;

import org.gicentre.treemappa.TreeMapNode;
import org.gicentre.treemappa.TreeMapProperties;
import org.gicentre.treemappa.TreeMappa;

import processing.core.PApplet;
import edu.zjut.treemap.data.SummariseNode;
import edu.zjut.treemap.summary.SummariseField;
import edu.zjut.treemap.summary.SummariseNull;

/**
 * For building treemaps
 * 
 * @author Aidan Slingsby, giCentre
 * 
 */
public class TreemapBuilder {

	TreeMappa treeMappa;
	TreeMapProperties treeMapProperties;

	public TreemapBuilder(TreeMapProperties treeMapProperties) {
		this.treeMappa = new TreeMappa(treeMapProperties);
		this.treeMapProperties = treeMapProperties;
	}

	/**
	 * Constructor
	 * 
	 * @param summaryNode
	 * @param orderVars
	 *            [state number][hierarchy number]
	 * @param sizeVars
	 *            [state number][hierarchy number]
	 * @return
	 */
	public TreemapSummaryNode computeTreemap(SummariseNode summaryNode,
			SummariseField[][] orderVars, SummariseField[] sizeVars,
			Layout[] layouts, int offX, int offY) {

		int startLevel = summaryNode.getLevel() + 1;

		HashMap<SummariseNode, TreeMapNode> nodeHashMap = new HashMap<>();

		Iterator<SummariseNode> it = summaryNode.iterator();
		it.next();
		nodeHashMap.put(summaryNode, new TreemapSummaryNode("", 0, null, null,
				summaryNode));

		while (it.hasNext()) {
			SummariseNode node = it.next();
			int relativeLevel = node.getLevel() - startLevel;

			String label = node.getConditioningValueAsString();
			double order = node.getNaturalOrder();
			double orderX = node.getNaturalOrder();
			double orderY = node.getNaturalOrder();

			Layout layout = layouts[relativeLevel];
			if (!layout.equals(Layout.SP) && !layout.equals(Layout.SA)) {
				SummariseField orderField = orderVars[0][relativeLevel];
				if (orderField != null
						&& !(orderField instanceof SummariseNull))
					order = node.getSummaryAsDouble(orderField);

				if (layout.equals(Layout.ST)) {
					treeMapProperties.setParameter("layout"
							+ (relativeLevel + startLevel), "strip");
					treeMapProperties.setParameter("align"
							+ (relativeLevel + startLevel), "free");
				} else if (layout.equals(Layout.OS)) {
					treeMapProperties
							.setParameter("layout"
									+ (relativeLevel + startLevel),
									"orderedSquarified");
					treeMapProperties.setParameter("align"
							+ (relativeLevel + startLevel), "free");
				} else if (layout.equals(Layout.HZ)) {
					treeMapProperties.setParameter("layout"
							+ (relativeLevel + startLevel), "sliceAndDice");
					treeMapProperties.setParameter("align"
							+ (relativeLevel + startLevel), "horizontal");
				} else if (layout.equals(Layout.VT)) {
					treeMapProperties.setParameter("layout"
							+ (relativeLevel + startLevel), "sliceAndDice");
					treeMapProperties.setParameter("align"
							+ (relativeLevel + startLevel), "vertical");
				}
			} else {

				SummariseField orderFieldX = orderVars[0][relativeLevel];
				SummariseField orderFieldY = orderVars[1][relativeLevel];

				orderX = node.getSummaryAsDouble(orderFieldX);
				orderY = node.getSummaryAsDouble(orderFieldY);

				treeMapProperties.setParameter("layout"
						+ (relativeLevel + startLevel), "spatial");
				treeMapProperties.setParameter("align"
						+ (relativeLevel + startLevel), "free");
			}

			Float size = 1f;
			if (sizeVars[relativeLevel] != null) {
				size = node.getSummaryAsFloat(sizeVars[relativeLevel]);
			}
			if (size == null || Float.isNaN(size) || Float.isInfinite(size)
					|| size == 0) {
				size = 0.0000001f; // if zero, Treemappa ignores it.
			}

			TreeMapNode treeMapNode = new TreemapSummaryNode(label, order,
					size, (float) node.getNaturalOrder(), new Point2D.Double(
							orderX, orderY), node);

			nodeHashMap.get(node.getParent()).add(treeMapNode);
			nodeHashMap.put(node, treeMapNode);
		}

		treeMappa.setRoot(nodeHashMap.get(summaryNode));
		treeMappa.getRoot().sortDescendants();
		treeMappa.buildTreeMap();

		// move all the rectangles to their offsets
		Iterator<TreeMapNode> it1 = treeMappa.getRoot().iterator();
		while (it1.hasNext()) {
			Rectangle2D rect = it1.next().getRectangle();
			if (rect != null) {
				rect.setFrame(rect.getX() + offX, rect.getY() + offY,
						rect.getWidth(), rect.getHeight());
			}
		}

		// ¾ø¶ÔÎ»ÖÃ
		cartLayout(summaryNode, orderVars, layouts);

		return (TreemapSummaryNode) treeMappa.getRoot();
	}

	/**
	 * move any cartographic layouts to their cartographic position
	 * 
	 * @param summaryNode
	 * @param orderVars
	 * @param layouts
	 */
	private void cartLayout(SummariseNode summaryNode,
			SummariseField[][] orderVars, Layout[] layouts) {

		// First find min/max x/ys
		int depth = summaryNode.getDepth();
		Float[] minXs = new Float[depth];
		Float[] maxXs = new Float[depth];
		Float[] minYs = new Float[depth];
		Float[] maxYs = new Float[depth];
		Float[] maxHalfWHs = new Float[depth];// of treemap nodes (in pixels)

		Iterator<TreeMapNode> it = treeMappa.getRoot().iterator();
		while (it.hasNext()) {
			TreemapSummaryNode node = (TreemapSummaryNode) it.next();
			SummariseNode summariseNode = node.getSummariseNode();

			int level = node.getLevel();
			if (!layouts[level - 1].equals(Layout.SA))
				continue;

			// find size for each node
			Rectangle2D rect = node.getRectangle();
			float halfWHs = (float) Math.sqrt((rect.getWidth() * rect
					.getHeight())) / 2;
			if (maxHalfWHs[level - 1] == null
					|| halfWHs > maxHalfWHs[level - 1])
				maxHalfWHs[level - 1] = halfWHs;

			// find x
			SummariseField orderXVar = orderVars[0][level - 1];

			float x = -99999;
			if (orderXVar == null || orderXVar instanceof SummariseNull) {
				x = summariseNode.getNaturalOrder();
			} else {
				x = summariseNode.getSummaryAsFloat(orderXVar);
			}
			if (x != -99999) {
				if (minXs[level - 1] == null || x < minXs[level - 1])
					minXs[level - 1] = x;
				if (maxXs[level - 1] == null || x > maxXs[level - 1])
					maxXs[level - 1] = x;
			}

			// find y
			SummariseField orderYVar = orderVars[1][level - 1];
			float y = -99999;
			if (orderYVar == null || orderYVar instanceof SummariseNull) {
				y = summariseNode.getNaturalOrder();
			} else {
				y = summariseNode.getSummaryAsFloat(orderYVar);
			}
			if (y != -99999) {
				if (minYs[level - 1] == null || y < minYs[level - 1])
					minYs[level - 1] = y;
				if (maxYs[level - 1] == null || y > maxYs[level - 1])
					maxYs[level - 1] = y;
			}
		}

		// then move them
		it = treeMappa.getRoot().iterator();
		while (it.hasNext()) {
			TreemapSummaryNode node = (TreemapSummaryNode) it.next();
			SummariseNode summariseNode = node.getSummariseNode();

			int level = node.getLevel();

			if (maxXs[level - 1] != null && maxYs[level - 1] != null
					&& layouts[level - 1].equals(Layout.SA)) {

				Rectangle2D parentR = ((TreemapSummaryNode) node.getParent())
						.getRectangle();
				Rectangle2D r = node.getRectangle();

				float x = -99999;
				SummariseField orderXVar = orderVars[0][level - 1];
				if (orderXVar == null || orderXVar instanceof SummariseNull) {
					x = summariseNode.getNaturalOrder();
				} else {
					x = summariseNode.getSummaryAsFloat(orderXVar);
				}

				float y = -99999;
				SummariseField orderYVar = orderVars[1][level - 1];
				if (orderYVar == null || orderYVar instanceof SummariseNull) {
					y = summariseNode.getNaturalOrder();
				} else {
					y = summariseNode.getSummaryAsFloat(orderYVar);
				}

				int wH = 0;
				if (r.getWidth() * r.getHeight() > 0) {
					wH = (int) Math.sqrt((r.getWidth() * r.getHeight()) / 2);
				}

				float screenX = PApplet.map(x, minXs[level - 1],
						maxXs[level - 1], (float) parentR.getMinX()
								+ maxHalfWHs[level - 1],
						(float) parentR.getMaxX() - maxHalfWHs[level - 1]);
				float screenY = PApplet.map(y, minYs[level - 1],
						maxYs[level - 1], (float) parentR.getMaxY()
								- maxHalfWHs[level - 1],
						(float) parentR.getMinY() + maxHalfWHs[level - 1]);

				Rectangle2D parentOldR = (Rectangle2D) r.clone();

				r.setFrame(screenX - wH / 2, screenY - wH / 2, wH, wH);
				Rectangle2D parentNewR = r;

				float minX = (float) parentOldR.getMinX();
				float maxX = (float) parentOldR.getMaxX();
				float minY = (float) parentOldR.getMinY();
				float maxY = (float) parentOldR.getMaxY();
				float width2 = (float) parentOldR.getWidth();
				float height2 = (float) parentOldR.getHeight();

				float minX2 = (float) parentNewR.getMinX();
				float maxX2 = (float) parentNewR.getMaxX();
				float minY2 = (float) parentNewR.getMinY();
				float maxY2 = (float) parentNewR.getMaxY();
				float width3 = (float) parentNewR.getWidth();
				float height3 = (float) parentNewR.getHeight();

				// then move everything within this node
				for (TreeMapNode node1 : node) {
					float x2 = (float) node1.getRectangle().getX();
					float y2 = (float) node1.getRectangle().getY();
					float width = (float) node1.getRectangle().getWidth();
					float height = (float) node1.getRectangle().getHeight();

					float newX = PApplet.map(x2, minX, maxX, minX2, maxX2);
					float newY = PApplet.map(y2, minY, maxY, minY2, maxY2);
					float newW = PApplet.map(width, 0, width2, 0, width3);
					float newH = PApplet.map(height, 0, height2, 0, height3);

					node1.getRectangle().setFrame(newX, newY, newW, newH);
				}
			}
		}
	}
}
