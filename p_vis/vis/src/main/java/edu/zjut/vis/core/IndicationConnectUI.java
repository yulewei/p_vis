/**
 * Copyright (c) 2009, GeoVISTA. 
 * Original author: Frank Hardisty
 * 
 * All rights reserved, except as below.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials provided
 *     with the distribution.
 *   * Neither the name of the JXLayer project nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package edu.zjut.vis.core;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;

/**
 * SpotLightUI paints connection lines between all indicated components and the
 * mouse location
 */
public class IndicationConnectUI<V extends JComponent> extends
		AbstractLayerUI<V> {
	private final List<Shape> shapeList = new ArrayList<Shape>();
	private final List<Component> srcCompList = new ArrayList<Component>();

	private Point mousePoint;

	private GeneralPath indicationGlyph;

	private float penWidth = 3f;

	public void setPenWidth(float penWidth) {
		this.penWidth = penWidth;
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		this.mousePoint = new Point();
		this.indicationGlyph = new GeneralPath();
	}

	public boolean paintIndication;

	// override paintLayer(), not paint()
	@Override
	protected void paintLayer(Graphics2D g2, JXLayer<? extends V> l) {
		// this paints layer as is
		super.paintLayer(g2, l);
		if (this.paintIndication == false) {
			return;
		}
		// custom painting is here
		this.indicationGlyph = new GeneralPath();
		g2.setColor(new Color(255, 0, 0, 125));

		BasicStroke capStroke = new BasicStroke(this.penWidth,
				BasicStroke.CAP_ROUND, 0);
		g2.setStroke(capStroke);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		for (int i = 0; i < this.shapeList.size(); i++) {
			Component comp = this.srcCompList.get(i);
			Shape shp = this.shapeList.get(i);

			Point aPoint = this.findPoint(shp, comp, l);
			this.indicationGlyph.moveTo(this.mousePoint.x, this.mousePoint.y);
			this.indicationGlyph.lineTo(aPoint.x, aPoint.y);
			// g2.draw(shp);

		}
		g2.draw(this.indicationGlyph);
	}

	/*
	 * Will return null if empty list is handed in.
	 */
	@SuppressWarnings("unused")
	private Point findClosestPoint(Point firstPoint, List<Point> otherPoints) {
		Point closestPoint = null;
		double minDist = Double.MAX_VALUE;
		for (Point pt : otherPoints) {
			double currDist = this.manhattanDistance(firstPoint, pt);
			if (currDist < minDist) {
				minDist = currDist;
				closestPoint = pt;
			}
		}

		return closestPoint;
	}

	private Point findPoint(Shape shp, Component comp, JXLayer l) {
		// current strategy: use nearest corner of bbox
		/*
		 * Rectangle bbox = shp.getBounds(); ArrayList<Point> bboxPoints = new
		 * ArrayList<Point>(); bboxPoints.add(new Point(bbox.x, bbox.y));
		 * bboxPoints.add(new Point(bbox.x + bbox.width, bbox.y));
		 * bboxPoints.add(new Point(bbox.x, bbox.y + bbox.height));
		 * bboxPoints.add(new Point(bbox.x + bbox.width, bbox.y + bbox.height));
		 * ArrayList<Point> convertPoints = new ArrayList<Point>(); for (Point
		 * pt : bboxPoints) { pt = SwingUtilities.convertPoint(comp, pt, l);
		 * convertPoints.add(pt); } return
		 * this.findClosestPoint(this.mousePoint, convertPoints);
		 */
		Rectangle bbox = shp.getBounds();
		Point pt = new Point((int) bbox.getCenterX(), (int) bbox.getCenterY());
		pt = SwingUtilities.convertPoint(comp, pt, l);

		return pt;

		// return

	}

	private double manhattanDistance(int x1, int y1, int x2, int y2) {
		double xDiff = x1 - x2;
		double yDiff = y1 - y2;

		xDiff = Math.abs(xDiff);
		yDiff = Math.abs(yDiff);

		return xDiff + yDiff;
	}

	private double manhattanDistance(Point firstPoint, Point secondPoint) {
		return this.manhattanDistance(firstPoint.x, firstPoint.y,
				secondPoint.x, secondPoint.y);
	}

	// catch drag events
	@Override
	protected void processMouseMotionEvent(MouseEvent e, JXLayer<? extends V> l) {
		super.processMouseMotionEvent(e, l);
		if (this.paintIndication == false) {
			return;
		}
		Point pt = e.getPoint();
		pt = SwingUtilities.convertPoint((Component) e.getSource(), e
				.getPoint(), l);

		this.mousePoint = pt;

		setDirty(true);

	}

	// catch MouseEvent.MOUSE_RELEASED
	@Override
	protected void processMouseEvent(MouseEvent e, JXLayer<? extends V> l) {
		super.processMouseEvent(e, l);

	}

	// clear overlay painting
	public void clear() {
		shapeList.clear();
		this.srcCompList.clear();

		// mark the ui as dirty and needed to be repainted
		// setDirty(true);
	}

	public void addShape(Shape shp, Component src) {
		this.shapeList.add(shp);
		this.srcCompList.add(src);
	}
}