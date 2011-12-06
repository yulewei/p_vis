package edu.zjut.common.data.geo;

import java.util.HashMap;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class GeometryData {

	// 地名, 几何
	private HashMap<String, Point> nameGeometrys;

	private List<GeoLayer> layers;

	public GeometryData(HashMap<String, Point> nameGeometrys,
			List<GeoLayer> layers) {
		this.nameGeometrys = nameGeometrys;
		this.layers = layers;
	}

	public HashMap<String, Point> getNameGeometrys() {
		return nameGeometrys;
	}

	public List<GeoLayer> getLayers() {
		return layers;
	}
}
