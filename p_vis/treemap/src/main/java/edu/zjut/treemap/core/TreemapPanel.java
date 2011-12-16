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
import edu.zjut.treemap.data.AndFilter;
import edu.zjut.treemap.data.DataUtil;
import edu.zjut.treemap.data.EqualsFilter;
import edu.zjut.treemap.data.NotFilter;
import edu.zjut.treemap.data.SummariseNode;
import edu.zjut.treemap.summary.SummariseField;
import edu.zjut.treemap.summary.SummariseNull;

/**
 * Draws a treemap according to a TreemapState object
 * 
 * @author Aidan Slingsby, giCentre
 * 
 */
public class TreemapPanel {

	TreemapSummaryNode activeNode;

	public void setActiveNode(TreemapSummaryNode activeNode) {
		this.activeNode = activeNode;
	}

	PApplet applet; // reference to the main Processing sketch
	Rectangle bounds; // bounds of this panel
	PFont font;

	TreemapSummaryNode treemap; // treemap
	HashMap<Integer, TreemapSummaryNode> oldTreemapNodes = new HashMap<Integer, TreemapSummaryNode>();

	private boolean localColourMin = false;
	boolean showLabels = true;
	private SummariseNode summaryNode;
	TreeMapProperties treeMapProperties;
	TreemapBuilder treemapBuilder;

	private PImage oldBuffer = null;
	private PGraphics buffer = null;
	private List<PGraphics> listAnimatedBuffers = new ArrayList<PGraphics>();
	boolean flagToDoStructuralRebuild = false;
	boolean flagToDoNonStructuralRebuild = false;
	boolean flagToRedraw = false;

	List<Object[]> records;
	List<Object[]> columnValues;
	// private Data data;

	List<SummariseField> summariseFields;

	float lerp = 1; // current step in the morphing/fading process (between 0
					// (start) to 1 (finished)
	boolean transitionMorph = true; // whether transition is a morph or a fade
	final float LERP_STEP_MORPH = 0.1f; // tranistion step (value between 0 and
										// 1)
	final float LERP_STEP_FADE = 0.08f; // tranistion step (value between 0 and
										// 1)
	boolean morphTransitionsOn = true; // if true, transitions (morph/fade)
										// turned on

	/**
	 * Constructor
	 * 
	 * @param applet
	 *            the Processing sketch
	 * @param treemapState
	 *            the treemap state
	 * @param font
	 * @param bounds
	 * @param data
	 * @param summariseFields
	 *            //all the summarise variables
	 * @param colours
	 *            HashMap of colourtables keyed to variables
	 * @param colourScalings
	 *            hashmap of the colour scalings.
	 */
	public TreemapPanel(PApplet applet, PFont font, Rectangle bounds,
			List<Object[]> records, List<SummariseField> summariseFields) {
		this.applet = applet;
		this.bounds = bounds;
		this.font = font;

		this.records = records;

		// this.data = data;
		this.summariseFields = summariseFields;

		// setup treemap properties
		treeMapProperties = new TreeMapProperties();
		treeMapProperties.setParameter("width", this.getBounds().width + "");
		treeMapProperties.setParameter("height", this.getBounds().height + "");
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

	/**
	 * Gets the image
	 * 
	 */
	public PImage getImageToTweet() {
		PGraphics graphics = applet.createGraphics(bounds.width, bounds.height,
				PApplet.JAVA2D);
		graphics.image(buffer, 0, 0);
		PImage image = applet.createImage(bounds.width, bounds.height,
				PApplet.ARGB);
		image.copy(graphics, this.bounds.x, this.bounds.y, this.bounds.width,
				this.bounds.height, 0, 0, this.bounds.width, this.bounds.height);
		return image;
	}

	private float getLerpStep() {
		if (transitionMorph) {
			return LERP_STEP_MORPH;
		} else {
			return LERP_STEP_FADE;
		}
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
			if (this.summaryNode != null)
				addSpecifiedValues(this.summaryNode, treemapState);
		}

		if (flagToDoNonStructuralRebuild || flagToRedraw) {
			// start off a transition
			lerp = 0;
			// capture old image for the next time
			oldBuffer.copy(buffer, this.bounds.x, this.bounds.y,
					this.bounds.width, this.bounds.height, 0, 0,
					this.bounds.width, this.bounds.height);

			if ((sizeHasChanged || orderHasChanged || layoutHasChanged)
					&& morphTransitionsOn && !oldTreemapNodes.isEmpty()) {
				// make the transition a morp unless a structural change..
				transitionMorph = true;
			} else {
				transitionMorph = false;
			}
		}

		// Build treemap
		if (flagToDoNonStructuralRebuild && this.summaryNode != null) {
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

			PGraphics oldG = applet.g;
			while (localLerp >= 0) {
				PGraphics pGraphics = applet.createGraphics(bounds.width,
						bounds.height, PApplet.JAVA2D);
				applet.g = pGraphics;
				pGraphics.beginDraw();
				applet.background(255);
				drawTreemapShapes(treemapState, localLerp);
				drawTreemapLabels(treemapState, localLerp);
				pGraphics.endDraw();
				listAnimatedBuffers.add(0, pGraphics);

				localLerp -= getLerpStep();
			}
			applet.g = oldG;
		}

		if (buffer.width != bounds.width || buffer.height != bounds.height) {
			buffer = applet.createGraphics(bounds.width, bounds.height,
					PApplet.JAVA2D);
			buffer.beginDraw();
			buffer.smooth();
			buffer.endDraw();
			PImage oldBuffer1 = applet.createImage(bounds.width, bounds.height,
					PApplet.ARGB);
			oldBuffer1.copy(oldBuffer, 0, 0, oldBuffer.width, oldBuffer.height,
					0, 0, oldBuffer.width, oldBuffer.height);
			oldBuffer = oldBuffer1;
		}

		if (flagToRedraw
				|| (isInTransition() && transitionMorph && !pregenerateMorphs)) {

			// save reference to Processings' canvas
			PGraphics oldG = applet.g;

			applet.g = buffer;
			buffer.beginDraw();

			// draw treemap
			drawTreemapShapes(treemapState, lerp);
			drawTreemapLabels(treemapState, lerp);

			buffer.endDraw();
			applet.g = oldG;
		}

		if (pregenerateMorphs && isInTransition() && transitionMorph
				&& listAnimatedBuffers.size() > 0) {
			PApplet.println((int) (lerp * (1 / getLerpStep())));
			buffer = listAnimatedBuffers
					.get((int) (lerp * (1 / getLerpStep())));
		}

		applet.image(buffer, bounds.x, bounds.y);
		if (this.treemap == null) {
			applet.textAlign(PConstants.CENTER, PConstants.CENTER);

		}

		// if in transition and not morphing...
		if (lerp < 1 && oldBuffer != null && !transitionMorph) {
			// if transition is fade, draw an image of the previous state on
			// top,
			// faded according to 'lerp'
			applet.tint(255, 255, 255, PApplet.lerp(255, 0, lerp));
			applet.image(oldBuffer, bounds.x, bounds.y);
			applet.noTint();
		}
		// Then increment 'lerp' (to next frame in transition
		// do this beyond 1, so that it's in transition() for a bit longer than
		// necessary
		if (lerp <= 1 + getLerpStep()) {
			lerp += getLerpStep();
		}

		// 绘制高亮节点
		if (activeNode != null) {
			Rectangle2D r = activeNode.getRectangle();

			applet.noFill();
			applet.strokeWeight(2.0f);
			applet.stroke(applet.color(255, 255, 0));
			applet.rect((float) r.getX(), (float) r.getY(),
					(float) r.getWidth(), (float) r.getHeight());
		}

		// reset changestateflags
		treemapState.resetChangeFlags();
		flagToDoNonStructuralRebuild = false;
		flagToDoStructuralRebuild = false;
		flagToRedraw = false;
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

	private void drawTreemapLabels(TreemapState treemapState, float lerp) {
		if (treemap == null)
			return;

		Iterator<TreeMapNode> it = treemap.iterator();
		applet.textFont(font);
		applet.textAlign(PApplet.CENTER, PApplet.CENTER);
		while (it.hasNext()) {
			TreemapSummaryNode node = (TreemapSummaryNode) it.next();

			HashMap<AppearanceType, Integer> appearances = treemapState
					.getAppearance(node.getLevel() - 1);

			if (appearances.get(AppearanceType.SHOW_LABELS) != 1)
				continue;

			Rectangle2D r = null;
			// need to work out the shape again so we can place the text
			// in the centre of it
			if (lerp < 1 && transitionMorph) {
				Rectangle2D r2 = node.getRectangle();
				Rectangle2D r1 = null;
				if (oldTreemapNodes.containsKey(node.getPathId().hashCode())) {
					r1 = oldTreemapNodes.get(node.getPathId().hashCode())
							.getRectangle();
				}
				if (r1 == null && r2 != null) {
					r1 = new Rectangle2D.Float((float) r2.getCenterX(),
							(float) r2.getCenterY(), 0, 0);
				} else if (r2 == null && r1 != null) {
					r2 = new Rectangle2D.Float((float) r1.getCenterX(),
							(float) r1.getCenterY(), 0, 0);
				}
				if (r1 != null && r2 != null) {
					r = new Rectangle2D.Float(PApplet.lerp((float) r1.getX(),
							(float) r2.getX(), lerp), PApplet.lerp(
							(float) r1.getY(), (float) r2.getY(), lerp),
							PApplet.lerp((float) r1.getWidth(),
									(float) r2.getWidth(), lerp), PApplet.lerp(
									(float) r1.getHeight(),
									(float) r2.getHeight(), lerp));
				}
			} else {
				r = node.getRectangle();
			}
			String label = node.getLabel();
			if (label == null) {
				label = "<no value>";
			} else {
				label = label.replaceAll("\\_", " ");
			}

			// draw the text
			if (r != null && r.getWidth() > 10 && r.getHeight() > 10) {
				int textSize;
				textSize = appearances.get(AppearanceType.LABEL_SIZE);

				applet.textSize(textSize);
				applet.fill(100);
				applet.textAlign(PApplet.CENTER, PApplet.CENTER);

				// adjust the opacity so that labels are lighter towards
				// the 'root'
				int opacity = appearances.get(AppearanceType.LABEL_OPACITY);
				applet.fill(80, opacity);

				applet.pushMatrix();
				applet.translate((float) r.getCenterX(), (float) r.getCenterY());
				// if it doesn't fit, rotate but only if it fits better
				boolean rotated = false;
				int textWidth = (int) applet.textWidth(label);
				if (appearances.get(AppearanceType.ALLOW_VERTICAL_LABELS) == 1) {
					if (r.getWidth() < textWidth
							&& r.getHeight() - textWidth > r.getWidth()
									- textWidth) {
						applet.rotate(-PApplet.HALF_PI);
						rotated = true;
					}
				}
				// if autosize text, scale appropriately
				if (appearances.get(AppearanceType.AUTOSIZE_LABELS) == 1) {
					if (rotated) {
						applet.scale(PApplet.min(
								(float) r.getHeight() / (textWidth + 8),
								(float) r.getWidth()
										/ (applet.textAscent()
												+ applet.textDescent() + 8)));

					} else {
						applet.scale(PApplet.min(
								(float) r.getWidth() / (textWidth + 8),
								(float) r.getHeight()
										/ (applet.textAscent()
												+ applet.textDescent() + 8)));
					}
				}

				// draw label
				applet.text(label, 0, 0);
				applet.popMatrix();
			}
		}
	}

	private void drawTreemapShapes(TreemapState treemapState, float lerp) {
		// paint background white
		applet.background(255);
		if (treemap == null)
			return;

		Float[] mins = new Float[treemapState.hierFields.length];
		Float[] maxs = new Float[treemapState.hierFields.length];
		Arrays.fill(mins, Float.MAX_VALUE);
		Arrays.fill(maxs, -Float.MAX_VALUE);

		// Work out which levels to colour at
		// Level closest to the end that colour=null
		// CURRENTLY ONLY WORKS WITH THE FIRST COLOUR FIELD SET
		SummariseField[] colourFields = treemapState.getColourFields()[0];
		int colourLevel = -1;
		// find the colourField closest to the leaves
		if (colourFields.length > 0) {
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
						|| (level - 1 + 1 < treemapState.layouts.length && treemapState.layouts[level - 1 + 1]
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

		applet.stroke(32, 32 + 32);
		applet.strokeWeight(1.5f);

		Iterator<TreeMapNode> it = treemap.iterator();

		while (it.hasNext()) {
			TreemapSummaryNode node = (TreemapSummaryNode) it.next();
			int level = node.getLevel();
			int borderwidth = treemapState.getAppearance(level - 1).get(
					AppearanceType.BORDER_WIDTH);
			if (borderwidth == 0) {
				applet.noStroke();
			} else {
				applet.strokeWeight(borderwidth / 2f);
				applet.stroke(150);
			}

			// if a leaf node OR the node to colour OR one level before abs
			// position
			if (node.getChildren().isEmpty()
					|| level == colourLevel + 1
					|| (level - 1 + 1 < treemapState.layouts.length && treemapState.layouts[level - 1 + 1]
							.equals(Layout.SA))) {
				// work out the colour
				Integer colour = null;
				if (colourFields[level - 1] != null
						&& colourFields[level - 1].getColorTable() != null) {
					Float value = node.getSummariseNode().getSummaryAsFloat(
							colourFields[level - 1]);
					if (value != null) {
						float transformedValue = ((Number) value).floatValue();
						float transformedMin = mins[level - 1];
						float transformedMax = maxs[level - 1];
						if (colourFields[level - 1].getColorScaling().equals(
								ColorScaling.LOG)) {
							boolean isNegative = false;
							if (mins[level - 1] != 0) {
								if (transformedMin < 0)
									transformedMin = -(float) Math.log(Math
											.abs(mins[level - 1]));
								else
									transformedMin = (float) Math.log(Math
											.abs(mins[level - 1]));
							}
							if (maxs[level - 1] != 0) {
								transformedMax = (float) Math
										.log(maxs[level - 1]);
							}
							if (value < 0) {
								isNegative = true;
							}
							if (value != 0) {
								transformedValue = (float) Math.log(Math
										.abs(value));
								if (isNegative) {
									transformedValue = -transformedValue;
								}
							}
						}
						ColourTable ct = colourFields[level - 1]
								.getColorTable();
						colour = ct.findColour(PApplet.map(transformedValue,
								transformedMin, transformedMax,
								ct.getMinIndex(), ct.getMaxIndex()));
					} else {
						applet.noFill();
					}
				}
				if (colour != null) {
					applet.fill(colour, 255);
				} else {
					applet.noFill();
				}
				// work out the shape
				Rectangle2D r = null;
				// if morphing, interpolate between previous and current
				// shape
				if (lerp < 1 && transitionMorph) {
					Rectangle2D r2 = node.getRectangle();
					Rectangle2D r1 = null;
					if (oldTreemapNodes
							.containsKey(node.getPathId().hashCode())) {
						r1 = oldTreemapNodes.get(node.getPathId().hashCode())
								.getRectangle();
					}
					if (r1 == null && r2 != null) {
						r1 = new Rectangle2D.Float((float) r2.getCenterX(),
								(float) r2.getCenterY(), 0, 0);
					} else if (r2 == null && r1 != null) {
						r2 = new Rectangle2D.Float((float) r1.getCenterX(),
								(float) r1.getCenterY(), 0, 0);
					}
					if (r1 != null && r2 != null) {
						r = new Rectangle2D.Float(PApplet.lerp(
								(float) r1.getX(), (float) r2.getX(), lerp),
								PApplet.lerp((float) r1.getY(),
										(float) r2.getY(), lerp), PApplet.lerp(
										(float) r1.getWidth(),
										(float) r2.getWidth(), lerp),
								PApplet.lerp((float) r1.getHeight(),
										(float) r2.getHeight(), lerp));
					}
				} else {
					// otherwise use the current shape
					r = node.getRectangle();
				}
				// draw the shape
				if (r != null && r.getBounds().getWidth() > 1
						&& r.getBounds().getHeight() > 1) {
					applet.rect((float) r.getX(), (float) r.getY(),
							(float) r.getWidth(), (float) r.getHeight());
				}
			}
		}
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
				treemapState.getLayouts(), this.getBounds().x,
				this.getBounds().y);
	}

	/**
	 * Get the bounds of this panel
	 * 
	 * @return
	 */
	public Rectangle getBounds() {
		return this.bounds;
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

	/**
	 * Find the treemap node (at the leaf-level) that the mouse is in
	 * 
	 * @return
	 */
	public TreemapSummaryNode getNodeFromMouse() {
		TreemapSummaryNode foundNode = null;
		if (treemap != null) {
			Iterator<TreeMapNode> it = treemap.iterator();
			while (it.hasNext()) {
				TreemapSummaryNode node = (TreemapSummaryNode) it.next();
				if (node != null
						&& node.getRectangle() != null
						&& node.getRectangle().contains(applet.mouseX,
								applet.mouseY)) {
					foundNode = node;
				}
			}
		}
		return foundNode;
	}

	/**
	 * Ensures that each branch contains all the specified values for all the
	 * variables where the user specified this
	 * 
	 * But ONLY if the variable doesn't also appear further down the hierarchy
	 * 
	 * @param summariseNodeRoot
	 * @param hierarchyVars
	 */
	static private void addSpecifiedValues(SummariseNode summariseNodeRoot,
			TreemapState state) {
		// find out which ones NOT to add these values to, be checking if the
		// variable
		// exists further down the hierarchy
		boolean[] shouldAddSpecifiedValues = new boolean[state.getHierFields().length];
		Arrays.fill(shouldAddSpecifiedValues, true);
		for (int i = 1; i < state.getHierFields().length; i++) {
			for (int j = 0; j < i; j++) {
				if (state.getHierFields()[j].equals(state.getHierFields()[i])) {
					shouldAddSpecifiedValues[i] = false;
				}
			}
		}

		Iterator<SummariseNode> it = summariseNodeRoot.iterator();
		while (it.hasNext()) {
			SummariseNode node = it.next();

			// if it has children
			// if this flag has been set (above)
			// if no filters
			if (node.getLevel() + 1 < state.hierFields.length
					&& shouldAddSpecifiedValues[node.getLevel() + 1]
					&& state.getFilterValues()[node.getLevel() + 1] == null) {
				DataField dataFieldOfChildren = state.hierFields[node
						.getLevel() + 1];
			}
		}
	}
}
