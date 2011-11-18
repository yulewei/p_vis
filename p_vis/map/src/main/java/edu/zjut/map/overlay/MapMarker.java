package edu.zjut.map.overlay;

import java.awt.Graphics2D;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;

import com.vividsolutions.jts.geom.Point;

/**
 * ±ê¼Ç, "Ô²È¦+²æ²æ"µÄÏÔÊ¾Ð§¹û
 * 
 * @author yulewei
 */
public class MapMarker extends Overlay {
	protected int radius = 10;

	protected GeoPosition position;

	public MapMarker(GeoPosition coord) {
		this(coord, "");
	}

	public MapMarker(GeoPosition coord, String title) {
		this.position = coord;
		this.title = title;
	}

	public MapMarker(Point coord, String title) {
		this.position = new GeoPosition(coord.getX(), coord.getY());
		this.title = title;
	}

	@Override
	public void paintOverlay(Graphics2D g, JXMapViewer map) {
		if (isHighlighted)
			return;

		java.awt.Point pt = GeoUtils.getScreenCoord(map, this.getPosition());
		g.translate(pt.x, pt.y);

		// Ìî³ä
		if (isFill) {
			g.setColor(fillColor);
			g.fillOval(-radius, -radius, radius * 2, radius * 2);
		}

		// ±ß¿ò
		if (isBorder) {
			g.setColor(borderColor);
			g.drawOval(-radius, -radius, radius * 2, radius * 2);
			int r = (int) (radius * 0.3) + 1;
			g.drawLine(-r, -r, +r, +r);
			g.drawLine(-r, +r, +r, -r);
		}
	}

	@Override
	public void paintHighlightOverlay(Graphics2D g, JXMapViewer map) {
		if (!isHighlighted)
			return;
		
		java.awt.Point pt = GeoUtils.getScreenCoord(map, this.getPosition());
		g.translate(pt.x, pt.y);

		// Ìî³ä
		if (isFill) {
			g.setColor(highlightColor);
			g.fillOval(-radius, -radius, radius * 2, radius * 2);
		}

		// ±ß¿ò
		if (isBorder) {
			g.setColor(highlightBorderColor);
			g.drawOval(-radius, -radius, radius * 2, radius * 2);
			int r = (int) (radius * 0.3) + 1;
			g.drawLine(-r, -r, +r, +r);
			g.drawLine(-r, +r, +r, -r);
		}
	}

	public GeoPosition getPosition() {
		return position;
	}

	public void setPosition(GeoPosition position) {
		this.position = position;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	@Override
	public boolean contains(JXMapViewer map, int x, int y) {
		java.awt.Point pt = GeoUtils.getScreenCoord(map, this.getPosition());

		return (pt.x - x) * (pt.x - x) + (pt.y - y) * (pt.y - y) < radius
				* radius;
	}
}
