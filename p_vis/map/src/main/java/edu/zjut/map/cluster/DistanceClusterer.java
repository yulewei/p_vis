package edu.zjut.map.cluster;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import org.jdesktop.swingx.mapviewer.GeoPosition;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import edu.zjut.map.JMapPanel;
import edu.zjut.map.overlay.Overlay;

/**
 * ª˘”⁄æ‡¿Îmarkeræ€¿‡, Distance-based Clustering
 * 
 * @author yulewei
 * 
 */
public class DistanceClusterer extends MarkerClusterer {

	/**
	 * æ‡¿Î«ß√◊, ƒ¨»œ2km
	 */
	// private double maxDistance = 2.0;

	// œÒÀÿ??
	private double maxDistance = 10.0;

	public DistanceClusterer(JMapPanel mapPanel) {
		super(mapPanel);
	}

	private Point calcCentroid(Collection<Integer> markerIndexes) {
		double x = 0.0;
		double y = 0.0;

		for (Integer index : markerIndexes) {
			Point point = (Point) markers.get(index).getGeometry();
			x += point.getX();
			y += point.getY();
		}

		Point center = new GeometryFactory().createPoint(new Coordinate(x
				/ markerIndexes.size(), y / markerIndexes.size()));

		return center;
	}

	private double calcDistance(Point p1, Point p2) {
		GeoPosition coord1 = new GeoPosition(p1.getX(), p1.getY());
		Point2D gp1 = mapPanel.getTileFactory().geoToPixel(coord1,
				mapPanel.getZoom());
		GeoPosition coord2 = new GeoPosition(p2.getX(), p2.getY());
		Point2D gp2 = mapPanel.getTileFactory().geoToPixel(coord2,
				mapPanel.getZoom());
		return (gp1.getX() - gp2.getX()) * (gp1.getX() - gp2.getX())
				+ (gp1.getY() - gp2.getY()) * (gp1.getY() - gp2.getY());
	}

	@Override
	protected List<Overlay> calculateClusters(List<Overlay> markers) {

		List<TreeSet<Integer>> cluster = new ArrayList<TreeSet<Integer>>();
		TreeSet<Integer> set = new TreeSet<Integer>();
		set.add(0);
		cluster.add(set);

		for (int i = 0; i < markers.size(); i++) {
			Overlay marker = markers.get(i);
			Point point = (Point) marker.getGeometry();

			boolean flag = false;
			for (TreeSet<Integer> markerIndexes : cluster) {
				if (!set.contains(i)) {
					Point centroid = calcCentroid(markerIndexes);
					double dis = calcDistance(centroid, point);
					if (dis < maxDistance * maxDistance) {
						markerIndexes.add(i);
						flag = true;
						break;
					}
				}
			}

			if (!flag) {
				TreeSet<Integer> markerIndexes = new TreeSet<Integer>();
				markerIndexes.add(i);
				cluster.add(markerIndexes);
			}
		}

		List<Overlay> clusteredMarkers = new ArrayList<>();
		for (TreeSet<Integer> markerIndexes : cluster) {
			if (markerIndexes.size() < minClusterSize) {
				for (Integer index : markerIndexes)
					clusteredMarkers.add(markers.get(index));

			} else {
				Point centroid = calcCentroid(markerIndexes);
				Overlay marker = new ClusterIcon(centroid, markerIndexes.size());
				clusteredMarkers.add(marker);
			}
		}

		return clusteredMarkers;
	}
}
