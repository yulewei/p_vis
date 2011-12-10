package edu.zjut.map.cluster;

import java.util.List;

import edu.zjut.map.JMapPanel;
import edu.zjut.map.ZoomLevelListener;
import edu.zjut.map.overlay.Overlay;

/**
 * markeræ€¿‡
 * 
 * @author yulewei
 */
public abstract class MarkerClusterer implements ZoomLevelListener {

	protected JMapPanel mapPanel;
	protected List<Overlay> markers;
	protected List<Overlay> clusteredMarkers;

	protected int maxZoom = 14;
	protected int minClusterSize = 2;

	public MarkerClusterer(JMapPanel mapPanel) {
		this.mapPanel = mapPanel;
	}

	public MarkerClusterer(JMapPanel mapPanel, int maxZoom) {
		this.mapPanel = mapPanel;
		this.maxZoom = maxZoom;
	}

	public void setMarkers(List<Overlay> markers) {
		this.markers = markers;
	}

	public List<Overlay> clustering() {
		if (mapPanel.getZoom() > maxZoom)
			return markers;

		clusteredMarkers = calculateClusters(markers);

		return clusteredMarkers;
	}

	protected abstract List<Overlay> calculateClusters(List<Overlay> markers);

	@Override
	public void zoomLevelChanged() {
		List<Overlay> clusteredMarkers = clustering();
		mapPanel.setMarkers(clusteredMarkers);
		mapPanel.repaint();
	}

	public int getMaxZoom() {
		return maxZoom;
	}

	public void setMaxZoom(int maxZoom) {
		this.maxZoom = maxZoom;
	}

	public int getMinClusterSize() {
		return minClusterSize;
	}

	public void setMinClusterSize(int minClusterSize) {
		this.minClusterSize = minClusterSize;
	}
}
