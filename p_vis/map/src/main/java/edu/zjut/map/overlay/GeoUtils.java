package edu.zjut.map.overlay;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;

public class GeoUtils {

	/**
	 * 经纬坐标转屏幕坐标
	 * 
	 * @param map
	 * @return
	 */
	public static Point getScreenCoord(JXMapViewer map, GeoPosition myLatlng) {
		Point2D gp_pt = map.getTileFactory()
				.geoToPixel(myLatlng, map.getZoom());
		Rectangle rect = map.getViewportBounds();
		Point pt = new Point((int) gp_pt.getX() - rect.x, (int) gp_pt.getY()
				- rect.y);
		return pt;
	}

	public static GeoPosition getGeoCoord(JXMapViewer map, int x, int y) {
		Rectangle rect = map.getViewportBounds();
		Point pt = new Point(x + rect.x, y + rect.y);

		GeoPosition gp = map.getTileFactory().pixelToGeo(pt, map.getZoom());

		return gp;
	}	
}
