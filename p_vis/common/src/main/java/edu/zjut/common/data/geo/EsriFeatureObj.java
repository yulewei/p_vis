package edu.zjut.common.data.geo;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Feature Object:
 * http://help.arcgis.com/en/arcgisserver/10.0/apis/rest/feature.html
 * 
 * "Feature Object"由"attributes"和"geometry"两部分组成.
 * "attributes"暂时只保留"OBJECTID"和"NAME"这两个属性, "geometry"由JTS几何库重新封装.
 * 
 * @author yulewei
 * 
 */
public class EsriFeatureObj
{
	public int objectId;

	/**
	 * displayFieldName
	 */
	public String name;

	/**
	 * 由JTS几何库重新封装
	 */
	public Geometry geometry;

	public EsriFeatureObj(int objectId, String name, Geometry geometry)
	{
		this.objectId = objectId;
		this.name = name;
		this.geometry = geometry;
	}	
}
