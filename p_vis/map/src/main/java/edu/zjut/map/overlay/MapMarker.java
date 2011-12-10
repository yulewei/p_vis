package edu.zjut.map.overlay;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import edu.zjut.map.GeoUtils;

/**
 * ±ê¼Ç, "Ô²È¦+²æ²æ"µÄÏÔÊ¾Ð§¹û
 * 
 * @author yulewei
 */
public class MapMarker extends Overlay {

	protected int radius = 10;

	protected Point point;

	public MapMarker(GeoPosition coord) {
		this(coord, "");
	}

	public MapMarker(GeoPosition coord, String title) {
		this.point = new GeometryFactory().createPoint(new Coordinate(coord
				.getLatitude(), coord.getLongitude()));
		this.title = title;
	}

	public MapMarker(Point point, String title) {
		this.point = point;
		this.title = title;
	}

	@Override
	public void paintOverlay(Graphics2D g, JXMapViewer map) {
		if (isHighlighted)
			return;

		Point2D pt = GeoUtils.getScreenCoord(map, point);
		g.translate(pt.getX(), pt.getY());

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

		Point2D pt = GeoUtils.getScreenCoord(map, point);
		g.translate(pt.getX(), pt.getY());

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

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	@Override
	public Geometry getGeometry() {
		return point;
	}

	@Override
	public boolean contains(JXMapViewer map, int x, int y) {
		Point2D pt = GeoUtils.getScreenCoord(map, point);

		return (pt.getX() - x) * (pt.getX() - x) + (pt.getY() - y)
				* (pt.getY() - y) < radius * radius;
	}
}
