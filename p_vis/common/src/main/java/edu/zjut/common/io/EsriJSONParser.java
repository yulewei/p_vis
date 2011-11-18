package edu.zjut.common.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

import edu.zjut.common.data.EsriFeatureObj;

/**
 * ArcGIS Server REST API的JSON返回结果参考:
 * http://help.arcgis.com/en/arcgisserver/10.0/apis/rest/index.html?query.html
 * JSON Response Syntax.
 * 
 * @author yulewei
 * 
 */
public class EsriJSONParser
{
	public static final String GEOMETRY_POINT = "esriGeometryPoint";

	public static final String GEOMETRY_POLYLINE = "esriGeometryPolyline";

	public static final String GEOMETRY_POLYGON = "esriGeometryPolygon";

	private String geometryType;

	
	/**
	 * 原始返回结果为"wkid: 2431"(EPSG:2431), 已经全部投影转换为EPSG:4326 (WGS84 经纬度)
	 */
	private int spatialReference = 2431;

	private String displayFieldName;

	private EsriFeatureObj[] features;

	public EsriJSONParser(String fileName)
	{
		try
		{
			parse(fileName);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

	private void parse(String fileName) throws IOException, JSONException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(fileName), "UTF-8"));

		JSONObject jsonobject = new JSONObject(reader.readLine());

		displayFieldName = jsonobject.get("displayFieldName").toString();
		geometryType = jsonobject.get("geometryType").toString();

		JSONArray jsonfeatures = (JSONArray) jsonobject.get("features");

		int len = jsonfeatures.length();
		features = new EsriFeatureObj[len];
		for (int i = 0; i < len; i++)
		{
			JSONObject object = (JSONObject) jsonfeatures.get(i);
			JSONObject jsonattributes = (JSONObject) object.get("attributes");

			int objectId = (Integer) jsonattributes.get("OBJECTID");

			String name = (String) jsonattributes.get(displayFieldName
					.toUpperCase());

			JSONObject jsongeometry = (JSONObject) object.get("geometry");

			Geometry geometry = parseGeometry(jsongeometry);
			features[i] = new EsriFeatureObj(objectId, name, geometry);
		}
	}

	private Geometry parseGeometry(JSONObject jsongeometry)
			throws JSONException
	{
		GeometryFactory geometryFactory = new GeometryFactory();

		Geometry geometry = null;

		if (geometryType.equals(GEOMETRY_POINT))
		{
			double x = (Double) jsongeometry.get("x");
			double y = (Double) jsongeometry.get("y");

			Coordinate coord = new Coordinate(y, x);

			geometry = geometryFactory.createPoint(coord);
		}
		else if (geometryType.equals(GEOMETRY_POLYLINE))
		{
			JSONArray jsonpaths = (JSONArray) jsongeometry.get("paths");
			LineString[] lineStrings = new LineString[jsonpaths.length()];
			for (int i = 0; i < jsonpaths.length(); i++)
			{
				JSONArray jsonpath = (JSONArray) jsonpaths.get(i);
				Coordinate[] path = new Coordinate[jsonpath.length()];
				for (int j = 0; j < jsonpath.length(); j++)
				{
					JSONArray jsonpoint = (JSONArray) jsonpath.get(j);
					double x = (Double) jsonpoint.get(0);
					double y = (Double) jsonpoint.get(1);
					path[j] = new Coordinate(y, x);
				}
				lineStrings[i] = geometryFactory.createLineString(path);
			}
			geometry = geometryFactory.createMultiLineString(lineStrings);
		}
		else if (geometryType.equals(GEOMETRY_POLYGON))
		{
			JSONArray jsonrings = (JSONArray) jsongeometry.get("rings");
			Polygon[] polygons = new Polygon[jsonrings.length()];
			for (int i = 0; i < jsonrings.length(); i++)
			{
				JSONArray jsonring = (JSONArray) jsonrings.get(i);
				Coordinate[] ring = new Coordinate[jsonring.length()];
				for (int j = 0; j < jsonring.length(); j++)
				{
					JSONArray jsonpoint = (JSONArray) jsonring.get(j);
					double x = (Double) jsonpoint.get(0);
					double y = (Double) jsonpoint.get(1);
					ring[j] = new Coordinate(y, x);
				}
				polygons[i] = geometryFactory.createPolygon(
						geometryFactory.createLinearRing(ring), null);
			}
			geometry = geometryFactory.createMultiPolygon(polygons);
		}

		return geometry;
	}

	public EsriFeatureObj[] getFeatures()
	{
		return features;
	}

	public String getGeometryType()
	{
		return geometryType;
	}

	/**
	 * @param args
	 * @throws JSONException
	 * @throws IOException
	 */
	public static void main(String[] args) throws JSONException, IOException
	{
		String fileName = "map/44.json";
		EsriJSONParser parser = new EsriJSONParser(fileName);
		EsriFeatureObj[] features = parser.getFeatures();

		System.out.println(features[0].geometry);
	}
}
