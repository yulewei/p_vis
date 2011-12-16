package edu.zjut.treemap.core;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.gicentre.treemappa.TreeMapNode;
import org.gicentre.treemappa.TreeMapProperties;
import org.gicentre.utils.colour.ColourTable;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import edu.zjut.common.color.ColorScaling;
import edu.zjut.common.data.attr.DataField;
import edu.zjut.common.data.attr.FieldType;
import edu.zjut.treemap.data.DataUtil;
import edu.zjut.treemap.data.SummariseNode;
import edu.zjut.treemap.filter.AndFilter;
import edu.zjut.treemap.filter.EqualsFilter;
import edu.zjut.treemap.filter.NotFilter;
import edu.zjut.treemap.summary.SummariseField;
import edu.zjut.treemap.summary.SummariseNull;

/**
 * Draws a treemap according to a TreemapState object
 * 
 * @author Aidan Slingsby, giCentre
 * 
 */
public class TreemapPanel {

	TreemapSummaryNode highlightNode;

	PApplet p;
	Rectangle bounds;
	PFont font;

	List<Object[]> records;
	List<Object[]> columnValues;
	List<SummariseField> summariseFields;

	TreemapSummaryNode treemap;
	HashMap<Integer, TreemapSummaryNode> oldTreemapNodes = new HashMap<>();
	private SummariseNode summaryNode;
	TreeMapProperties treeMapProperties;
	TreemapBuilder treemapBuilder;

	private PImage oldBuffer = null;
	private PGraphics buffer = null;
	private List<PGraphics> listAnimatedBuffers = new ArrayList<PGraphics>();

	private boolean localColourMin = false;
	boolean showLabels = true;

	boolean flagToDoStructuralRebuild = false;
	boolean flagToDoNonStructuralRebuild = false;
	boolean flagToRedraw = false;

	float lerp = 1;
	boolean transitionMorph = true;
	final float LERP_STEP_MORPH = 0.1f;
	final float LERP_STEP_FADE = 0.08f;
	boolean morphTransitionsOn = true;

	public TreemapPanel(PApplet applet, Rectangle bounds,
			List<Object[]> records, List<SummariseField> summariseFields) {
		this.p = applet;
		this.bounds = bounds;

		this.records = records;
		this.summariseFields = summariseFields;

		// setup treemap properties
		treeMapProperties = new TreeMapProperties();
		treeMapProperties.setParameter("width", bounds.width + "");
		treeMapProperties.setParameter("height", bounds.height + "");
		treeMapProperties.setParameter("border", "1");
		treemapBuilder = new TreemapBuilder(treeMapProperties);

		buffer = applet.createGraphics(bounds.width, bounds.height,
				PApplet.JAVA2D);
		buffer.beginDraw();
		buffer.smooth();
		buffer.endDraw();
		oldBuffer = applet.createImage(bounds.width, bounds.height,
				PApplet.ARGB);
	}

	public void draw(TreemapState treemapState) {
		boolean pregenerateMorphs = false;

		boolean hierHasChanged = treemapState.isHierChanged();
		boolean orderHasChanged = treemapState.orderChanged();
		boolean sizeHasChanged = treemapState.isSizeChanged();
		boolean colourHasChanged = treemapState.isColorChanged();
		boolean layoutHasChanged = treemapState.layoutChanged();
		boolean appearanceHasChanged = treemapState.isAppearanceChanged();

		Object[] filterValues = treemapState.filterValues;
		DataField[] hierFields = treemapState.getHierFields();
		HashMap<AppearanceType, Integer>[] appearanceValues = treemapState.appearanceValues;

		if (hierHasChanged) {
			flagToDoStructuralRebuild = true;
			flagToDoNonStructuralRebuild = true;
			flagToRedraw = true;
		}

		if (sizeHasChanged || orderHasChanged || layoutHasChanged) {
			flagToDoNonStructuralRebuild = true;
			flagToRedraw = true;
		}

		if (colourHasChanged || appearanceHasChanged) {
			flagToRedraw = true;
		}

		if (flagToDoStructuralRebuild) {
			AndFilter filter = new AndFilter();
			for (int i = 0; i < hierFields.length; i++) {
				if (filterValues[i] != null) {
					filter.add(new EqualsFilter(hierFields[i], filterValues[i]));
				}
				if (appearanceValues[i].get(AppearanceType.INCLUDE_NULLS) == 0) {
					filter.add(new NotFilter(new EqualsFilter(hierFields[i],
							null)));
				}
			}

			this.summaryNode = DataUtil.getSummary(records, hierFields,
					summariseFields, filter);
		}

		if (flagToDoNonStructuralRebuild || flagToRedraw) {
			// start off a transition
			lerp = 0;
			// capture old image for the next time
			oldBuffer.copy(buffer, bounds.x, bounds.y, bounds.width,
					bounds.height, 0, 0, bounds.width, bounds.height);

			if ((sizeHasChanged || orderHasChanged || layoutHasChanged)
					&& morphTransitionsOn && !oldTreemapNodes.isEmpty()) {
				// make the transition a morp unless a structural change..
				transitionMorph = true;
			} else {
				transitionMorph = false;
			}
		}

		// Build treemap
		if (flagToDoNonStructuralRebuild && summaryNode != null) {
			for (int i = 0; i < hierFields.length; i++) {
				treeMapProperties.setParameter("border" + i,
						appearanceValues[i].get(AppearanceType.PADDING) + "");
			}
			buildTreemap(treemapState);
		}

		if (this.summaryNode == null) {
			this.treemap = null;
		}

		// pregenerate morph frames
		if (pregenerateMorphs && lerp == 0 && transitionMorph) {
			listAnimatedBuffers.clear();
			float localLerp = 1 - lerp + getLerpStep();

			PGraphics oldG = p.g;
			while (localLerp >= 0) {
				PGraphics pGraphics = p.createGraphics(bounds.width,
						bounds.height, PApplet.JAVA2D);
				p.g = pGraphics;
				pGraphics.beginDraw();
				p.background(255);
				drawTreemapShapes(treemapState, localLerp);
				drawTreemapLabels(treemapState, localLerp);
				pGraphics.endDraw();
				listAnimatedBuffers.add(0, pGraphics);

				localLerp -= getLerpStep();
			}
			p.g = oldG;
		}

		if (buffer.width != bounds.width || buffer.height != bounds.height) {
			buffer = p.createGraphics(bounds.width, bounds.height,
					PApplet.JAVA2D);
			buffer.beginDraw();
			buffer.smooth();
			buffer.endDraw();
			PImage oldBuffer1 = p.createImage(bounds.width, bounds.height,
					PApplet.ARGB);
			oldBuffer1.copy(oldBuffer, 0, 0, oldBuffer.width, oldBuffer.height,
					0, 0, oldBuffer.width, oldBuffer.height);
			oldBuffer = oldBuffer1;
		}

		if (flagToRedraw
				|| (isInTransition() && transitionMorph && !pregenerateMorphs)) {

			// save reference to Processings' canvas
			PGraphics oldG = p.g;

			p.g = buffer;
			buffer.beginDraw();

			// draw treemap
			drawTreemapShapes(treemapState, lerp);
			drawTreemapLabels(treemapState, lerp);

			buffer.endDraw();
			p.g = oldG;
		}

		if (pregenerateMorphs && isInTransition() && transitionMorph
				&& listAnimatedBuffers.size() > 0) {
			PApplet.println((int) (lerp * (1 / getLerpStep())));
			buffer = listAnimatedBuffers
					.get((int) (lerp * (1 / getLerpStep())));
		}

		p.image(buffer, bounds.x, bounds.y);
		if (this.treemap == null) {
			p.textAlign(PConstants.CENTER, PConstants.CENTER);
		}

		// if in transition and not morphing...
		if (lerp < 1 && oldBuffer != null && !transitionMorph) {
			// if transition is fade, draw an image of the previous state on
			// top,
			// faded according to 'lerp'
			p.tint(255, 255, 255, PApplet.lerp(255, 0, lerp));
			p.image(oldBuffer, bounds.x, bounds.y);
			p.noTint();
		}
		// Then increment 'lerp' (to next frame in transition
		// do this beyond 1, so that it's in transition() for a bit longer than
		// necessary
		if (lerp <= 1 + getLerpStep()) {
			lerp += getLerpStep();
		}

		// 绘制高亮节点
		if (highlightNode != null) {
			Rectangle2D r = highlightNode.getRectangle();

			p.noFill();
			p.strokeWeight(2.0f);
			p.stroke(p.color(255, 255, 0));
			p.rect((float) r.getX(), (float) r.getY(), (float) r.getWidth(),
					(float) r.getHeight());
		}

		// reset changestateflags
		treemapState.resetChangeFlags();
		flagToDoNonStructuralRebuild = false;
		flagToDoStructuralRebuild = false;
		flagToRedraw = false;
	}

	private void drawTreemapShapes(TreemapState treemapState, float lerp) {
		// paint background white
		p.background(255);
		if (treemap == null)
			return;

		Float[] mins = new Float[treemapState.hierFields.length];
		Float[] maxs = new Float[treemapState.hierFields.length];
		Arrays.fill(mins, Float.MAX_VALUE);
		Arrays.fill(maxs, -Float.MAX_VALUE);

		// Work out which levels to colour at
		// Level closest to the end that colour=null
		// CURRENTLY ONLY WORKS WITH THE FIRST COLOUR FIELD SET

		Layout[] layouts = treemapState.layouts;
		SummariseField[] colourFields = treemapState.getColourFields()[0];

		int colourLevel = -1;
		if (colourFields.length > 0) {

			// find the colourField closest to the leaves
			for (int i = colourFields.length - 1; i >= 0; i--) {
				if (colourFields[i] != null) {
					colourLevel = i;
					break;
				}
			}

			// work out min/max
			Iterator<TreeMapNode> it = treemap.iterator();
			while (it.hasNext()) {
				TreemapSummaryNode node = (TreemapSummaryNode) it.next();
				int level = node.getLevel();

				// if a leaf node OR the node to colour OR one level before
				// abs position
				if (node.getChildren().isEmpty()
						|| level == colourLevel + 1
						|| (level - 1 + 1 < layouts.length && layouts[level - 1 + 1]
								.equals(Layout.SA))) {
					Float value = null;
					if (colourFields[level - 1] instanceof SummariseNull
							&& node.getSummariseNode().getConditioningField()
									.getFieldType() != FieldType.STRING) {
						value = node.getSummariseNode()
								.getConditioningValueAsFloat();
					} else {
						value = node.getSummariseNode().getSummaryAsFloat(
								colourFields[level - 1]);
					}
					if (value != null && !Float.isInfinite(value)
							&& !Float.isNaN(value)) {
						if (mins[level - 1] > value) {
							mins[level - 1] = value;
						}
						if (maxs[level - 1] < value) {
							maxs[level - 1] = value;
						}
					}
				}
			}
			if (!localColourMin) {
				for (int i = 0; i < mins.length; i++) {
					mins[i] = 0f;
				}
			}

			// make min/maxes symmetrical for diverging colour schemes
			for (int i = 0; i < mins.length; i++) {
				if (colourFields[i] != null
						&& colourFields[i].getColorTable() != null
						&& colourFields[i].getColorTable().getMinIndex() == -1) {
					// then it's a diverging scheme
					float max = Math.max(Math.abs(mins[i]), Math.abs(maxs[i]));
					mins[i] = max * -1;
					maxs[i] = max;
				}
			}

			// if min equals max, then add one and subtract one
			for (int i = 0; i < mins.length; i++) {
				if (mins[i] == maxs[i]) {
					mins[i] -= 1;
					maxs[i] += 1;
				}
			}
		}

		// draw them

		p.stroke(32, 32 + 32);
		p.strokeWeight(1.5f);

		Iterator<TreeMapNode> it = treemap.iterator();

		while (it.hasNext()) {
			TreemapSummaryNode node = (TreemapSummaryNode) it.next();
			int level = node.getLevel();

			int borderwidth = treemapState.getAppearance(level - 1).get(
					AppearanceType.BORDER_WIDTH);
			if (borderwidth == 0) {
				p.noStroke();
			} else {
				p.strokeWeight(borderwidth / 2f);
				p.stroke(150);
			}

			// if a leaf node OR the node to colour OR one level before abs
			// position
			if (node.getChildren().isEmpty()
					|| level == colourLevel + 1
					|| (level - 1 + 1 < layouts.length && layouts[level - 1 + 1]
							.equals(Layout.SA))) {

				// 计算颜色
				Integer colour = calcColor(node, colourFields[level - 1],
						mins[level - 1], maxs[level - 1]);

				if (colour != null) {
					p.fill(colour, 255);
				} else {
					p.noFill();
				}

				// 计算形状
				Rectangle2D.Float r = calcShape(node, lerp);

				// draw the shape
				if (r != null && r.width > 1 && r.height > 1) {
					p.rect(r.x, r.y, r.width, r.height);
				}
			}
		}
	}

	private Integer calcColor(TreemapSummaryNode node,
			SummariseField colorField, Float min, Float max) {

		if (colorField == null || colorField.getColorTable() == null)
			return null;

		Float value = node.getSummariseNode().getSummaryAsFloat(colorField);
		if (value == null)
			return null;

		Integer colour = null;
		float transformedValue = ((Number) value).floatValue();
		float transformedMin = min;
		float transformedMax = max;
		if (colorField.getColorScaling().equals(ColorScaling.LOG)) {
			boolean isNegative = false;
			if (min != 0) {
				if (transformedMin < 0)
					transformedMin = -(float) Math.log(Math.abs(min));
				else
					transformedMin = (float) Math.log(Math.abs(min));
			}
			if (max != 0) {
				transformedMax = (float) Math.log(max);
			}
			if (value < 0) {
				isNegative = true;
			}
			if (value != 0) {
				transformedValue = (float) Math.log(Math.abs(value));
				if (isNegative) {
					transformedValue = -transformedValue;
				}
			}
		}

		ColourTable ct = colorField.getColorTable();
		colour = ct.findColour(PApplet.map(transformedValue, transformedMin,
				transformedMax, ct.getMinIndex(), ct.getMaxIndex()));
		return colour;
	}

	private void drawTreemapLabels(TreemapState treemapState, float lerp) {
		if (treemap == null)
			return;

		p.textFont(font);
		p.textAlign(PApplet.CENTER, PApplet.CENTER);

		Iterator<TreeMapNode> it = treemap.iterator();
		while (it.hasNext()) {
			TreemapSummaryNode node = (TreemapSummaryNode) it.next();

			HashMap<AppearanceType, Integer> appearances = treemapState
					.getAppearance(node.getLevel() - 1);

			if (appearances.get(AppearanceType.SHOW_LABELS) != 1)
				continue;

			String label = node.getLabel();
			if (label == null) {
				label = "<no value>";
			} else {
				label = label.replaceAll("\\_", " ");
			}

			Rectangle2D.Float r = calcShape(node, lerp);

			// draw the text
			if (r != null && r.width > 10 && r.height > 10) {
				int textSize;
				textSize = appearances.get(AppearanceType.LABEL_SIZE);

				p.textSize(textSize);
				p.fill(100);
				p.textAlign(PApplet.CENTER, PApplet.CENTER);

				// adjust the opacity so that labels are lighter towards
				// the 'root'
				int opacity = appearances.get(AppearanceType.LABEL_OPACITY);
				p.fill(80, opacity);

				p.pushMatrix();
				p.translate((float) r.getCenterX(), (float) r.getCenterY());

				// if it doesn't fit, rotate but only if it fits better
				boolean rotated = false;
				float textWidth = p.textWidth(label);
				if (appearances.get(AppearanceType.ALLOW_VERTICAL_LABELS) == 1) {
					if (r.width < textWidth
							&& r.height - textWidth > r.width - textWidth) {
						p.rotate(-PApplet.HALF_PI);
						rotated = true;
					}
				}

				// if autosize text, scale appropriately
				if (appearances.get(AppearanceType.AUTOSIZE_LABELS) == 1) {
					if (rotated) {
						p.scale(PApplet.min(r.height / (textWidth + 8), r.width
								/ (p.textAscent() + p.textDescent() + 8)));

					} else {
						p.scale(PApplet.min(r.width / (textWidth + 8), r.height
								/ (p.textAscent() + p.textDescent() + 8)));
					}
				}

				// draw label
				p.text(label, 0, 0);
				p.popMatrix();
			}
		}
	}

	private Rectangle2D.Float calcShape(TreemapSummaryNode node, float lerp) {

		Rectangle2D r = null;

		// 插值
		if (lerp < 1 && transitionMorph) {
			Rectangle2D r2 = node.getRectangle();
			Rectangle2D r1 = null;
			if (oldTreemapNodes.containsKey(node.getPathId().hashCode())) {
				r1 = oldTreemapNodes.get(node.getPathId().hashCode())
						.getRectangle();
			}
			if (r1 == null && r2 != null) {
				r1 = new Rectangle2D.Double(r2.getCenterX(), r2.getCenterY(),
						0, 0);
			} else if (r2 == null && r1 != null) {
				r2 = new Rectangle2D.Double(r1.getCenterX(), r1.getCenterY(),
						0, 0);
			}
			if (r1 != null && r2 != null) {
				float x = PApplet.lerp((float) r1.getX(), (float) r2.getX(),
						lerp);
				float y = PApplet.lerp((float) r1.getY(), (float) r2.getY(),
						lerp);
				float w = PApplet.lerp((float) r1.getWidth(),
						(float) r2.getWidth(), lerp);
				float h = PApplet.lerp((float) r1.getHeight(),
						(float) r2.getHeight(), lerp);
				r = new Rectangle2D.Float(x, y, w, h);
			}
		} else {
			// otherwise use the current shape
			r = node.getRectangle();
		}

		if (r == null)
			return null;

		Rectangle2D.Float rf = new Rectangle2D.Float();
		rf.setRect(r);
		return rf;
	}

	/**
	 * Builds the treemap geometry
	 * 
	 */
	private void buildTreemap(TreemapState treemapState) {
		// store old treemap nodes
		if (treemap != null) {
			Iterator<TreeMapNode> it = treemap.iterator();
			while (it.hasNext()) {
				TreemapSummaryNode node = (TreemapSummaryNode) it.next();
				oldTreemapNodes.put(node.getPathId().hashCode(), node);
			}
		}

		// just use the first of the sizes - treemaps don't support others
		treemap = treemapBuilder.computeTreemap(summaryNode,
				treemapState.getOrderFields(), treemapState.getSizeFields()[0],
				treemapState.getLayouts(), bounds.x, bounds.y);
	}

	public void setTextFont(PFont font) {
		this.font = font;
	}

	/**
	 * Gets the image
	 * 
	 */
	public PImage getImageToTweet() {
		PGraphics graphics = p.createGraphics(bounds.width, bounds.height,
				PApplet.JAVA2D);
		graphics.image(buffer, 0, 0);
		PImage image = p.createImage(bounds.width, bounds.height, PApplet.ARGB);
		image.copy(graphics, bounds.x, bounds.y, bounds.width, bounds.height,
				0, 0, bounds.width, bounds.height);
		return image;
	}

	private float getLerpStep() {
		if (transitionMorph) {
			return LERP_STEP_MORPH;
		} else {
			return LERP_STEP_FADE;
		}
	}

	/**
	 * get the root node as a SummariseNode
	 * 
	 * @return
	 */
	public SummariseNode getSummariseNode() {
		return this.summaryNode;
	}

	/**
	 * get the treemp root node
	 * 
	 * @return
	 */
	public TreemapSummaryNode getTreemapSummaryNode() {
		return this.treemap;
	}

	/**
	 * set whether the lightest colour is the local min
	 * 
	 * @param useLocalMin
	 *            colouring, true if use local min; false if zero
	 */
	public void setUseLocalMin(boolean useLocalMin) {
		localColourMin = useLocalMin;
		flagToDoNonStructuralRebuild();
	}

	/**
	 * get whether the lightest colour is local min
	 * 
	 * @return true if local min; false if zero
	 */
	public boolean getUseLocalMin() {
		return localColourMin;
	}

	/**
	 * Whether the shapes are in transition
	 * 
	 * @return
	 */
	public boolean isInTransition() {
		return lerp <= 1 + getLerpStep();
	}

	/**
	 * Check to see if transitions are on
	 * 
	 * @return onlyUseFadeTransitions
	 */
	public boolean isTransitionsOn() {
		return this.morphTransitionsOn;
	}

	/**
	 * Set transitions on/off (useful if morphing to too slow)
	 * 
	 * @param transitionsOn
	 */
	public void setTransitionsOn(boolean transitionsOn) {
		this.morphTransitionsOn = transitionsOn;
	}

	/**
	 * Flag to do a structural rebuild (change in hierarchy - needs to
	 * resummarise all the data)
	 * 
	 */
	public void flagToDoStructuralRebuild() {
		flagToDoStructuralRebuild = true;
		flagToDoNonStructuralRebuild = true;
		flagToRedraw = true;
	}

	/**
	 * Flag to do a non-structural rebuild (change in geometry only)
	 * 
	 */
	public void flagToDoNonStructuralRebuild() {
		flagToDoNonStructuralRebuild = true;
		flagToRedraw = true;
	}

	/**
	 * Flag to do redraw
	 * 
	 */
	public void flagToRedraw() {
		flagToRedraw = true;
	}

	/**
	 * Change the bounds and treemap to fit
	 * 
	 * @param bounds
	 */
	public void setBounds(Rectangle bounds) {
		this.bounds.setBounds(bounds);
		treeMapProperties.setParameter("width", bounds.width + "");
		treeMapProperties.setParameter("height", bounds.height + "");
		flagToDoNonStructuralRebuild();
	}

	/**
	 * Change the bounds, and the ratio of treemap
	 * 
	 * @param bounds
	 */
	public void setBounds(Rectangle bounds, float treemapWidthHeightRatio) {
		this.bounds.setBounds(bounds);
		// calculate as if w should be preserved
		int w = bounds.width;
		int h = (int) (w / treemapWidthHeightRatio);
		// if h is too big, preserve h
		if (h > bounds.height) {
			h = bounds.height;
			w = (int) (h * treemapWidthHeightRatio);
		}
		treeMapProperties.setParameter("width", w + "");
		treeMapProperties.setParameter("height", h + "");
		flagToDoNonStructuralRebuild();
	}

	public void setHighlightNode(TreemapSummaryNode activeNode) {
		this.highlightNode = activeNode;
	}

	/**
	 * 叶子节点
	 * 
	 * @param mouseX
	 * @param mouseY
	 * @return
	 */
	public TreemapSummaryNode getNodeFromMouse(int mouseX, int mouseY) {
		if (treemap == null)
			return null;

		TreemapSummaryNode find = null;
		Iterator<TreeMapNode> it = treemap.iterator();
		while (it.hasNext()) {
			TreemapSummaryNode node = (TreemapSummaryNode) it.next();
			if (node.getRectangle().contains(mouseX, mouseY)) {
				find = node;
			}
		}

		return find;
	}
}
