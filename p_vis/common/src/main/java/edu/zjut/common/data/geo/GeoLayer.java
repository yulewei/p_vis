package edu.zjut.common.data.geo;

public class GeoLayer {

	private String layerName;
	private EsriFeatureObj[] features;

	public GeoLayer(String layerName, EsriFeatureObj[] features) {
		this.layerName = layerName;
		this.features = features;
	}

	public String getLayerName() {
		return layerName;
	}

	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	public EsriFeatureObj[] getFeatures() {
		return features;
	}

	public void setFeatures(EsriFeatureObj[] features) {
		this.features = features;
	}
}
