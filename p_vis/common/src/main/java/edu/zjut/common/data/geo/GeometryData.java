package edu.zjut.common.data.geo;

import java.util.HashMap;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;

public class GeometryData {

	// 地名, 几何
	private HashMap<String, Geometry> nameGeometrys;

	private List<EsriFeatureObj[]> layers;

	public GeometryData(HashMap<String, Geometry> nameGeometrys,
			List<EsriFeatureObj[]> layers) {
		this.nameGeometrys = nameGeometrys;
		this.layers = layers;
	}

	public HashMap<String, Geometry> getNameGeometrys() {
		return nameGeometrys;
	}

	public List<EsriFeatureObj[]> getLayers() {
		return layers;
	}
}
