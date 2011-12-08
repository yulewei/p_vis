package edu.zjut.vis.map;

import edu.zjut.common.data.geo.GeoLayer;

public class LayerCheckableItem {
	private boolean isVisible = true;
	private GeoLayer layer;

	public LayerCheckableItem(GeoLayer layer) {
		this.layer = layer;
	}

	public GeoLayer getLayer() {
		return layer;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public String toString() {
		return layer.getLayerName();
	}
}