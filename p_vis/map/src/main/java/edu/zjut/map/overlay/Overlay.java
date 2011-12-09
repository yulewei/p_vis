package edu.zjut.map.overlay;

import java.awt.Color;
import java.awt.Graphics2D;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.painter.Painter;

import com.vividsolutions.jts.geom.Geometry;

public abstract class Overlay {

	protected String title;

	protected float borderWidth = 1.0f;
	protected Color borderColor = new Color(10, 10, 10);
	protected Color fillColor = new Color(10, 10, 10, 100);

	protected boolean isBorder = true;
	protected boolean isFill = true;

	public boolean isBorder() {
		return isBorder;
	}

	public void setBorder(boolean isBorder) {
		this.isBorder = isBorder;
	}

	public boolean isFill() {
		return isFill;
	}

	public void setFill(boolean isFill) {
		this.isFill = isFill;
	}

	public boolean isHighlighted = false;

	public boolean isHighlighted() {
		return isHighlighted;
	}

	public void setHighlighted(boolean isHighlighted) {
		this.isHighlighted = isHighlighted;
	}

	protected float highlightBorderWidth = 2.0f;
	protected Color highlightBorderColor = new Color(0, 0, 255);
	protected Color highlightColor = new Color(0, 0, 255, 100);

	public float getHighlightBorderWidth() {
		return highlightBorderWidth;
	}

	public void setHighlightBorderWidth(float highlightBorderWidth) {
		this.highlightBorderWidth = highlightBorderWidth;
	}

	public Color getHighlightBorderColor() {
		return highlightBorderColor;
	}

	public void setHighlightBorderColor(Color highlightBorderColor) {
		this.highlightBorderColor = highlightBorderColor;
	}

	public Color getHighlightColor() {
		return highlightColor;
	}

	public void setHighlightColor(Color highlightColor) {
		this.highlightColor = highlightColor;
	}

	public float getBorderWidth() {
		return borderWidth;
	}

	public void setBorderWidth(float borderWidth) {
		this.borderWidth = borderWidth;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	public Color getFillColor() {
		return fillColor;
	}

	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Painter<JXMapViewer> getOverlayPainter() {
		Painter<JXMapViewer> overlayPainter = new Painter<JXMapViewer>() {
			public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
				paintOverlay(g, map);
			}
		};

		return overlayPainter;
	}

	public Painter<JXMapViewer> getHighlightOverlayPainter() {
		Painter<JXMapViewer> overlayPainter = new Painter<JXMapViewer>() {
			public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
				paintHighlightOverlay(g, map);
			}
		};

		return overlayPainter;
	}

	/**
	 * 由JXMapViewer的Painter调用, Graphics2D来自JXMapViewer
	 * 
	 * @param g
	 * @param map
	 */
	public abstract void paintOverlay(Graphics2D g, JXMapViewer map);

	public abstract void paintHighlightOverlay(Graphics2D g, JXMapViewer map);

	public abstract Geometry getGeometry();

	public abstract boolean contains(JXMapViewer map, int x, int y);
}
