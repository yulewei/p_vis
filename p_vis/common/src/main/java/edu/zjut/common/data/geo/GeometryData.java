package edu.zjut.common.data.geo;

import java.util.List;

public class GeometryData {

	/**
	 * 地理名称关联图层
	 */
	private List<GeoLayer> geoNames;

	/**
	 * 辅助图层数据
	 */
	private List<GeoLayer> layers;

	public GeometryData(List<GeoLayer> geoNames, List<GeoLayer> layers) {
		this.geoNames = geoNames;
		this.layers = layers;
	}

	public List<GeoLayer> getGeoNames() {
		return geoNames;
	}

	public List<GeoLayer> getLayers() {
		return layers;
	}
}
