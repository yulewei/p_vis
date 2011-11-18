package edu.zjut.map.overlay;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;

/**
 * 经纬坐标转屏幕坐标
 * 
 * @author yulewei
 * 
 */
public class GeoToPixelFilter implements CoordinateSequenceFilter {

	private JXMapViewer map;

	public GeoToPixelFilter(JXMapViewer map) {
		this.map = map;
	}

	@Override
	public void filter(CoordinateSequence seq, int i) {
		Rectangle rect = map.getViewportBounds();

		double x = seq.getOrdinate(i, 0);
		double y = seq.getOrdinate(i, 1);
		Point2D gp_pt = map.getTileFactory().geoToPixel(new GeoPosition(x, y),
				map.getZoom());
		Point pt = new Point((int) gp_pt.getX() - rect.x, (int) gp_pt.getY()
				- rect.y);

		seq.setOrdinate(i, 0, pt.x);
		seq.setOrdinate(i, 1, pt.y);
	}

	@Override
	public boolean isDone() {
		return false;
	}

	@Override
	public boolean isGeometryChanged() {
		return true;
	}
}
