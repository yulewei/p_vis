package edu.zjut.map.cluster;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.jdesktop.swingx.mapviewer.GeoPosition;

import com.vividsolutions.jts.geom.Point;

import edu.zjut.map.JMapPanel;
import edu.zjut.map.overlay.Overlay;

/**
 * 最原始的基于网格marker聚类.
 * 
 * @author yulewei
 * 
 */
public class GridClustererOriginal extends MarkerClusterer {

	private int gridSize = 60;

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
			return "(" + x + " " + y + ")";
		}
	}

	public GridClustererOriginal(JMapPanel mapPanel) {
		super(mapPanel);
	}

	public GridClustererOriginal(JMapPanel mapPanel, int gridSize) {
		super(mapPanel);

		this.gridSize = gridSize;
	}

	@Override
	protected List<Overlay> calculateClusters(List<Overlay> markers) {
		TreeMap<GridCell, List<Overlay>> gridCluster = new TreeMap<>();

		for (Overlay overlay : markers) {
			Point point = (Point) overlay.getGeometry();

			GeoPosition coord = new GeoPosition(point.getX(), point.getY());
			Point2D gp_pt = mapPanel.getTileFactory().geoToPixel(coord,
					mapPanel.getZoom());

			int x = (int) (gp_pt.getX() / gridSize);
			int y = (int) (gp_pt.getY() / gridSize);

			GridCell cell = new GridCell(x, y);

			List<Overlay> cellMarkers = gridCluster.get(cell);
			if (cellMarkers == null) {
				cellMarkers = new ArrayList<Overlay>();
				cellMarkers.add(overlay);
				gridCluster.put(cell, markers);
			} else {
				cellMarkers.add(overlay);
			}
		}

		List<Overlay> clusteredMarkers = new ArrayList<>();

		for (GridCell cell : gridCluster.keySet()) {
			List<Overlay> cellMarkers = gridCluster.get(cell);

			double sumX = 0, sumY = 0;
			for (Overlay overlay : markers) {
				Point point = (Point) overlay.getGeometry();
				sumX += point.getX();
				sumY += point.getY();
			}
			sumX /= cellMarkers.size();
			sumY /= cellMarkers.size();

			if (cellMarkers.size() < minClusterSize) {
				for (Overlay marker : cellMarkers)
					clusteredMarkers.add(marker);
			} else {
				Overlay marker = new ClusterIcon(new GeoPosition(sumX, sumY),
						cellMarkers.size());
				clusteredMarkers.add(marker);
			}
		}

		return clusteredMarkers;
	}
}
