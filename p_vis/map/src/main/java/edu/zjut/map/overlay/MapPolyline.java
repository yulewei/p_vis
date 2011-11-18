package edu.zjut.map.overlay;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;

import com.vividsolutions.jts.awt.ShapeWriter;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;


public class MapPolyline extends Overlay {
	/**
	 * JTS几何, 原始数据, 经纬坐标
	 */
	private MultiLineString polyline;

	public MapPolyline(LineString polyline) {
		this(polyline, "");
	}

	public MapPolyline(MultiLineString polyline) {
		this(polyline, "");
	}

	public MapPolyline(LineString polyline, String title) {
		this.polyline = new GeometryFactory()
				.createMultiLineString(new LineString[] { polyline });
		this.title = title;

		this.borderWidth = 2.0f;
	}

	public MapPolyline(MultiLineString polyline, String title) {
		this.polyline = polyline;
		this.title = title;
		
		this.borderWidth = 2.0f;
	}

	@Override
	public void paintOverlay(Graphics2D g, JXMapViewer map) {
		if (isHighlighted)
			return;
		
		MultiLineString polyline2 = (MultiLineString) polyline.clone();
		GeoToPixelFilter filter = new GeoToPixelFilter(map);
		polyline2.apply(filter);

		ShapeWriter shapeWriter = new ShapeWriter();
		Shape shape = shapeWriter.toShape(polyline2);

		g.setStroke(new BasicStroke(borderWidth));
		g.setColor(borderColor);
		g.draw(shape);
	}

	@Override
	public void paintHighlightOverlay(Graphics2D g, JXMapViewer map) {
		if (!isHighlighted)
			return;
		
		MultiLineString polyline2 = (MultiLineString) polyline.clone();
		GeoToPixelFilter filter = new GeoToPixelFilter(map);
		polyline2.apply(filter);

		ShapeWriter shapeWriter = new ShapeWriter();
		Shape shape = shapeWriter.toShape(polyline2);

		g.setStroke(new BasicStroke(highlightBorderWidth));
		g.setColor(highlightBorderColor);
		g.draw(shape);		
	}

	@Override
	public boolean contains(JXMapViewer map, int x, int y) {
		GeoPosition gp = GeoUtils.getGeoCoord(map, x, y);
		double gx = gp.getLatitude();
		double gy = gp.getLongitude();

		return polyline.distance(new GeometryFactory()
				.createPoint(new Coordinate(gx, gy))) < 0.005;
	}

}
