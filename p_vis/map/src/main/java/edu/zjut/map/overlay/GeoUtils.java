package edu.zjut.map.overlay;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class GeoUtils {

	/**
	 * Î³×ø±ê=>ÆÁÄ»×ø±ê
	 * 
	 * @param map
	 * @return
	 */
	public static Point2D getScreenCoord(JXMapViewer map, Point point) {
		GeoPosition coord = new GeoPosition(point.getX(), point.getY());

		Point2D gp_pt = map.getTileFactory().geoToPixel(coord, map.getZoom());
		Rectangle rect = map.getViewportBounds();
		Point2D pt = new Point2D.Double(gp_pt.getX() - rect.x, gp_pt.getY()
				- rect.y);
		return pt;
	}

	/**
	 * ÆÁÄ»×ø±ê=>¾­Î³×ø±ê
	 * 
	 * @param map
	 * @return
	 */
	public static Point getGeoCoord(JXMapViewer map, int x, int y) {
		Rectangle rect = map.getViewportBounds();
		Point2D pt = new Point2D.Double(x + rect.x, y + rect.y);

		GeoPosition gp = map.getTileFactory().pixelToGeo(pt, map.getZoom());

		double gx = gp.getLatitude();
		double gy = gp.getLongitude();
		Point point = new GeometryFactory().createPoint(new Coordinate(gx, gy));

		return point;
	}
}
