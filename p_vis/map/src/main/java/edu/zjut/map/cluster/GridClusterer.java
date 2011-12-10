package edu.zjut.map.cluster;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.jdesktop.swingx.mapviewer.GeoPosition;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import edu.zjut.map.GeoUtils;
import edu.zjut.map.JMapPanel;
import edu.zjut.map.overlay.Overlay;

/**
 * 基于网格marker聚类, grid-based clustering.
 * 
 * <p>
 * 参考实现: <a href=
 * "http://google-maps-utility-library-v3.googlecode.com/svn/trunk/markerclusterer/"
 * >MarkerClusterer</a>; 源自Google Maps <a href=
 * http://code.google.com/p/google-maps-utility-library-v3/wiki/Libraries
 * >google-maps-utility-library-v3</a>
 * </p>
 * 
 * <p>
 * <ul>
 * <li>文章1: <a href=
 * "http://code.google.com/intl/en/apis/maps/articles/toomanymarkers.html">Too
 * Many Markers!</a>, 见其中<a href=
 * "http://code.google.com/intl/zh-CN/apis/maps/articles/toomanymarkers.html#markerclusterer"
 * >MarkerClusterer</a>部分</li>
 * <li>文章2: <a href=
 * "http://googlegeodevelopers.blogspot.com/2009/04/markerclusterer-solution-to-too-many.html"
 * >MarkerClusterer: A Solution to the Too Many Markers Problem</a></li>
 * </ul>
 * </p>
 * 
 * 
 * @author yulewei
 * 
 */
public class GridClusterer extends MarkerClusterer {

	private int gridSize = 60;

	class Cluster {
		Point center = null;
		Rectangle2D bounds = null;
		List<Overlay> markers = new ArrayList<Overlay>();

		void addMarker(Overlay marker) {
			if (markers.indexOf(marker) != -1)
				return;

			if (center == null) {
				center = (Point) marker.getGeometry();
			} else {
				Point pos = (Point) marker.getGeometry();

				int l = markers.size() + 1;
				double x = (this.center.getX() * (l - 1) + pos.getX()) / l;
				double y = (this.center.getY() * (l - 1) + pos.getY()) / l;
				center = new GeometryFactory()
						.createPoint(new Coordinate(x, y));
			}

			markers.add(marker);

			GeoPosition coord = new GeoPosition(center.getX(), center.getY());
			Point2D gp = mapPanel.getTileFactory().geoToPixel(coord,
					mapPanel.getZoom());

			bounds = new Rectangle2D.Double(gp.getX() - gridSize / 2, gp.getY()
					- gridSize / 2, gridSize, gridSize);
		}

		boolean isMarkerInClusterBounds(Overlay marker) {
			Point pos = (Point) marker.getGeometry();
			GeoPosition coord = new GeoPosition(pos.getX(), pos.getY());
			Point2D gp = mapPanel.getTileFactory().geoToPixel(coord,
					mapPanel.getZoom());

			return bounds.contains(gp);
		}
	}

	public GridClusterer(JMapPanel mapPanel) {
		super(mapPanel);
	}

	public GridClusterer(JMapPanel mapPanel, int gridSize) {
		super(mapPanel);

		this.gridSize = gridSize;
	}

	@Override
	protected List<Overlay> calculateClusters(List<Overlay> markers) {

		List<Cluster> clusters = new ArrayList<Cluster>();
		for (Overlay marker : markers) {
			Point pos = (Point) marker.getGeometry();

			double distance = Double.MAX_VALUE;
			Cluster clusterToAddTo = null;
			for (int i = 0; i < clusters.size(); i++) {
				Cluster cluster = clusters.get(i);
				Point center = cluster.center;
				double d = GeoUtils.distance(center, pos);
				if (d < distance) {
					distance = d;
					clusterToAddTo = cluster;
				}
			}

			if (clusterToAddTo != null
					&& clusterToAddTo.isMarkerInClusterBounds(marker)) {
				clusterToAddTo.addMarker(marker);
			} else {
				Cluster cluster = new Cluster();
				cluster.addMarker(marker);

				clusters.add(cluster);
			}
		}

		List<Overlay> clusteredMarkers = new ArrayList<>();
		for (Cluster cluster : clusters) {
			List<Overlay> clusterMarkers = cluster.markers;
			Overlay marker = null;
			if (clusterMarkers.size() == 1) {
				marker = clusterMarkers.get(0);
			} else {
				marker = new ClusterIcon(cluster.center, clusterMarkers.size());
			}
			clusteredMarkers.add(marker);
		}

		return clusteredMarkers;
	}
}
