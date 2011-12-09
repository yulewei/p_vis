package edu.zjut.map.overlay;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;

import org.jdesktop.swingx.JXMapViewer;

import com.vividsolutions.jts.awt.ShapeWriter;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class MapPolygon extends Overlay {

	/**
	 * JTS几何, 原始数据, 经纬坐标
	 */
	private MultiPolygon polygon;

	public MapPolygon(Polygon polygon) {
		this(polygon, "");
	}

	public MapPolygon(MultiPolygon polygon) {
		this(polygon, "");
	}

	public MapPolygon(Polygon polygon, String title) {
		this.polygon = new GeometryFactory()
				.createMultiPolygon(new Polygon[] { polygon });
		this.title = title;
	}

	public MapPolygon(MultiPolygon polygon, String title) {
		this.polygon = polygon;
		this.title = title;
	}

	@Override
	public void paintOverlay(Graphics2D g, JXMapViewer map) {
		if (isHighlighted)
			return;

		MultiPolygon polygon2 = (MultiPolygon) polygon.clone();
		GeoToPixelFilter filter = new GeoToPixelFilter(map);
		polygon2.apply(filter);
		ShapeWriter shapeWriter = new ShapeWriter();
		Shape shape = shapeWriter.toShape(polygon2);

		if (isFill) {
			g.setColor(fillColor);
			g.fill(shape);
		}

		if (isBorder) {
			g.setStroke(new BasicStroke(borderWidth));
			g.setColor(borderColor);
			g.draw(shape);
		}
	}

	@Override
	public void paintHighlightOverlay(Graphics2D g, JXMapViewer map) {
		if (!isHighlighted)
			return;

		MultiPolygon polygon2 = (MultiPolygon) polygon.clone();
		GeoToPixelFilter filter = new GeoToPixelFilter(map);
		polygon2.apply(filter);
		ShapeWriter shapeWriter = new ShapeWriter();
		Shape shape = shapeWriter.toShape(polygon2);

		if (isFill) {
			g.setColor(highlightColor);
			g.fill(shape);
		}

		if (isBorder) {
			g.setStroke(new BasicStroke(highlightBorderWidth));
			g.setColor(highlightBorderColor);
			g.draw(shape);
		}
	}

	/**
	 * 包围矩形, 经纬坐标
	 * 
	 * @return
	 */
	public Envelope getBoundingBox() {
		return polygon.getEnvelopeInternal();
	}

	public Point getCentroid() {
		return polygon.getCentroid();
	}

	@Override
	public Geometry getGeometry() {
		return polygon;
	}

	@Override
	public boolean contains(JXMapViewer map, int x, int y) {
		Point point = GeoUtils.getGeoCoord(map, x, y);
		return polygon.contains(point);
	}
}
