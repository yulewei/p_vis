package edu.zjut.common.data;

import java.util.HashMap;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;


public class GeometryData {

	// 地名, 几何
	public HashMap<String, Geometry> nameGeometrys;

	public List<EsriFeatureObj[]> layers;

	public GeometryData(HashMap<String, Geometry> nameGeometrys,
			List<EsriFeatureObj[]> layers) {
		this.nameGeometrys = nameGeometrys;
		this.layers = layers;
	}
}
