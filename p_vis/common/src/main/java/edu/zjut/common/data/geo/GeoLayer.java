package edu.zjut.common.data.geo;

import com.vividsolutions.jts.geom.Geometry;

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

	public Geometry getGeometry(String geoName) {
		Geometry geometry = null;
		for (EsriFeatureObj feature : features) {
			if (geoName.equals(feature.name)) {
				geometry = feature.geometry;
				break;
			}
		}

		return geometry;
	}

	public String toString() {
		return layerName;
	}
}
