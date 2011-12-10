package edu.zjut.map;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class GeoUtils {

	/**
	 * 距离计算 (存在精度问题, 待改进)
	 * 
	 * @param p1
	 *            经纬度
	 * @param p2
	 *            经纬度
	 * @return 距离(千米km)
	 */
	public static double distance(Point p1, Point p2) {
		GeoPosition gp1 = new GeoPosition(p1.getX(), p1.getY());
		GeoPosition gp2 = new GeoPosition(p2.getX(), p2.getY());
		return distance(gp1, gp2);
	}

	/**
	 * 距离计算
	 * 
	 * @param p1
	 *            经纬度
	 * @param p2
	 *            经纬度
	 * @return 距离(千米km)
	 */
	public static double distance(GeoPosition p1, GeoPosition p2) {
		// Radius of the Earth in km
		double R = 6371;
		double dLat = (p2.getLatitude() - p1.getLatitude()) * Math.PI / 180;
		double dLon = (p2.getLongitude() - p1.getLongitude()) * Math.PI / 180;
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(p1.getLatitude() * Math.PI / 180)
				* Math.cos(p2.getLatitude() * Math.PI / 180)
				* Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = R * c;
		return d;
	}

	/**
	 * 纬坐标=>屏幕坐标
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
	 * 屏幕坐标=>经纬坐标
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

	public static void main(String[] args) {
		GeoPosition p1 = new GeoPosition(119.94, 30.13);
		GeoPosition p2 = new GeoPosition(120.27, 30.14);

		System.out.println(GeoUtils.distance(p1, p2));
	}
}
