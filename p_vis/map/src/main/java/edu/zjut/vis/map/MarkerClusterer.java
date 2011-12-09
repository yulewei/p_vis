package edu.zjut.vis.map;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.jdesktop.swingx.mapviewer.GeoPosition;

import com.vividsolutions.jts.geom.Point;

import edu.zjut.map.JMapPanel;
import edu.zjut.map.ZoomLevelListener;
import edu.zjut.map.overlay.ClusterMarker;
import edu.zjut.map.overlay.Overlay;

public class MarkerClusterer implements ZoomLevelListener {

	private JMapPanel mapPanel;
	private List<Overlay> markerList;
	private int gridSize = 60;
	private int maxZoom = 14;

	private class GridCell implements Comparable<GridCell> {
		int x;
		int y;

		public GridCell(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public int hashCode() {
			return x << 16 + y;
		}

		@Override
		public int compareTo(GridCell o) {
			if (this.x != o.x)
				return this.x - o.x;
			return this.y - o.y;
		}

		public String toString() {
			return x + " " + y;
		}
	}

	public MarkerClusterer(JMapPanel mapPanel) {
		this.mapPanel = mapPanel;
		mapPanel.addZoomLevelListener(this);
	}

	public MarkerClusterer(JMapPanel mapPanel, int gridSize, int maxZoom) {
		this.mapPanel = mapPanel;
		this.gridSize = gridSize;
		this.maxZoom = maxZoom;
	}

	public void setMarkerList(List<Overlay> markerList) {
		this.markerList = markerList;
	}

	public List<Overlay> clustering() {
		if (mapPanel.getZoom() > maxZoom)
			return markerList;

		TreeMap<GridCell, List<Overlay>> gridCluster = new TreeMap<>();

		for (Overlay overlay : markerList) {
			Point point = (Point) overlay.getGeometry();

			GeoPosition coord = new GeoPosition(point.getX(), point.getY());
			Point2D gp_pt = mapPanel.getTileFactory().geoToPixel(coord,
					mapPanel.getZoom());

			int x = (int) (gp_pt.getX() / gridSize);
			int y = (int) (gp_pt.getY() / gridSize);

			GridCell cell = new GridCell(x, y);

			List<Overlay> markers = gridCluster.get(cell);
			if (markers == null) {
				markers = new ArrayList<Overlay>();
				markers.add(overlay);
				gridCluster.put(cell, markers);
			} else {
				markers.add(overlay);
			}
		}

		List<Overlay> clusteredMarkers = new ArrayList<>();

		for (GridCell cell : gridCluster.keySet()) {
			List<Overlay> markers = gridCluster.get(cell);

			double sumX = 0, sumY = 0;
			for (Overlay overlay : markers) {
				Point point = (Point) overlay.getGeometry();
				sumX += point.getX();
				sumY += point.getY();
			}
			sumX /= markers.size();
			sumY /= markers.size();

			Overlay marker = null;

			if (markers.size() > 1)
				marker = new ClusterMarker(new GeoPosition(sumX, sumY),
						markers.size() + "");
			else
				marker = markers.get(0);

			clusteredMarkers.add(marker);
		}

		return clusteredMarkers;
	}

	@Override
	public void zoomLevelChanged() {
		List<Overlay> clusteredMarkers = clustering();
		mapPanel.setMarkerList(clusteredMarkers);
		mapPanel.repaint();
	}
}
