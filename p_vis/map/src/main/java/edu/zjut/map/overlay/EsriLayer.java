package edu.zjut.map.overlay;

import java.awt.Color;
import java.awt.Graphics2D;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;

import edu.zjut.common.data.geo.EsriFeatureObj;

/**
 * Í¼²ã
 * 
 * @author yulewei
 * 
 */
public class EsriLayer extends Overlay {

	private Overlay[] overlays;

	private EsriFeatureObj[] features;

	public EsriLayer(EsriFeatureObj[] features) {
		this.features = features;
		initChildOverlay();
	}

	private void initChildOverlay() {
		overlays = new Overlay[features.length];
		for (int i = 0; i < features.length; i++) {
			EsriFeatureObj feature = features[i];
			if (feature.geometry instanceof Point) {
				Point p = (Point) feature.geometry;
				GeoPosition gp = new GeoPosition(p.getX(), p.getY());
				overlays[i] = new DefaultMapMarker(gp, feature.name);
			} else if (feature.geometry instanceof MultiLineString) {
				overlays[i] = new MapPolyline(
						(MultiLineString) feature.geometry, feature.name);
			} else if (feature.geometry instanceof MultiPolygon) {
				overlays[i] = new MapPolygon((MultiPolygon) feature.geometry,
						feature.name);
			}
		}
	}

	public Overlay[] getOverlays() {
		return overlays;
	}

	@Override
	public void paintOverlay(Graphics2D g, JXMapViewer map) {
		for (Overlay overlay : overlays)
			overlay.paintOverlay(g, map);
	}

	@Override
	public void paintHighlightOverlay(Graphics2D g, JXMapViewer map) {
		for (Overlay overlay : overlays)
			overlay.paintHighlightOverlay(g, map);
	}

	public void setBorder(boolean isBorder) {
		this.isBorder = isBorder;
		for (Overlay overlay : overlays)
			overlay.setBorder(isBorder);

	}
	
	public void setFill(boolean isFill) {
		this.isFill = isFill;
		for (Overlay overlay : overlays)
			overlay.setFill(isFill);

	}
	
	public void setHighlighted(boolean isHighlighted) {
		this.isHighlighted = isHighlighted;		
		for (Overlay overlay : overlays)
			overlay.setHighlighted(isHighlighted);
	}

	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
		for (Overlay overlay : overlays)
			overlay.setFillColor(fillColor);
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
		for (Overlay overlay : overlays)
			overlay.setBorderColor(borderColor);
	}

	public void setBorderWidth(float borderWidth) {
		this.borderWidth = borderWidth;
		for (Overlay overlay : overlays)
			overlay.setBorderWidth(borderWidth);
	}

	public void setHighlightBorderWidth(float highlightBorderWidth) {
		this.highlightBorderWidth = highlightBorderWidth;
		for (Overlay overlay : overlays)
			overlay.setHighlightBorderWidth(highlightBorderWidth);
	}

	public void setHighlightBorderColor(Color highlightBorderColor) {
		this.highlightBorderColor = highlightBorderColor;
		for (Overlay overlay : overlays)
			overlay.setHighlightBorderColor(highlightBorderColor);
	}

	public void setHighlightColor(Color highlightColor) {
		this.highlightColor = highlightColor;
		for (Overlay overlay : overlays)
			overlay.setHighlightColor(highlightColor);
	}

	public Overlay containOverlay(JXMapViewer map, int x, int y) {
		for (Overlay overlay : overlays) {
			if (overlay.contains(map, x, y))
				return overlay;
		}
		return null;
	}

	public GeoPosition[] getBoundingBox() {
		double minlat = Double.MAX_VALUE, minlon = Double.MAX_VALUE;
		double maxlat = Double.MIN_VALUE, maxlon = Double.MIN_VALUE;
		for (Overlay overlay : overlays) {
			MapPolygon polygon = (MapPolygon) overlay;
			GeoPosition[] box = polygon.getBoundingBox();
			if (box[0].getLatitude() < minlat)
				minlat = box[0].getLatitude();
			if (box[0].getLongitude() < minlon)
				minlon = box[0].getLongitude();
			if (box[1].getLatitude() > maxlat)
				maxlat = box[1].getLatitude();
			if (box[1].getLongitude() > maxlon)
				maxlon = box[1].getLongitude();
		}
		GeoPosition min = new GeoPosition(minlat, minlon);
		GeoPosition max = new GeoPosition(maxlat, maxlon);
		return new GeoPosition[] { min, max };
	}

	@Override
	public boolean contains(JXMapViewer map, int x, int y) {
		for (Overlay overlay : overlays) {
			if (overlay.contains(map, x, y))
				return true;
		}
		return false;
	}

}
