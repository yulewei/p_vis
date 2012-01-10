package edu.zjut.common.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import edu.zjut.common.data.geo.EsriFeatureObj;

/**
 * <p>
 * ArcGIS Server下对某个Layer进行Query操作, 解析JSON返回结果．
 * </p>
 * 
 * <p>
 * 参考: <a href=
 * "http://help.arcgis.com/en/arcgisserver/10.0/apis/rest/index.html?query.html"
 * > ArcGIS Server REST API: Query (Operation)</a>, 见JSON Response Syntax部分
 * </p>
 * 
 * @author yulewei
 * 
 */
public class EsriJSONParser {
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

	public EsriJSONParser() {
	}

	public EsriFeatureObj[] read(String fileName) throws IOException,
			JSONException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(fileName), "UTF-8"));

		JSONObject jsonobject = new JSONObject(reader.readLine());

		displayFieldName = jsonobject.get("displayFieldName").toString();
		geometryType = jsonobject.get("geometryType").toString();

		JSONArray jsonfeatures = (JSONArray) jsonobject.get("features");

		int len = jsonfeatures.length();
		features = new EsriFeatureObj[len];
		for (int i = 0; i < len; i++) {
			JSONObject object = (JSONObject) jsonfeatures.get(i);
			JSONObject jsonattributes = (JSONObject) object.get("attributes");

			int objectId = (Integer) jsonattributes.get("OBJECTID");

			String name = "";
			try {
				name = jsonattributes.get(displayFieldName.toUpperCase())
						.toString();
			} catch (JSONException e) {
				name = jsonattributes.get(displayFieldName).toString();
			}

			JSONObject jsongeometry = (JSONObject) object.get("geometry");
			Geometry geometry = readGeometry(jsongeometry);
			features[i] = new EsriFeatureObj(objectId, name, geometry);
		}

		return features;
	}

	private Geometry readGeometry(JSONObject jsongeometry) throws JSONException {
		GeometryFactory geometryFactory = new GeometryFactory();

		Geometry geometry = null;

		if (geometryType.equals(GEOMETRY_POINT)) {
			double x = (Double) jsongeometry.get("x");
			double y = (Double) jsongeometry.get("y");

			Coordinate coord = new Coordinate(y, x);

			geometry = geometryFactory.createPoint(coord);
		} else if (geometryType.equals(GEOMETRY_POLYLINE)) {
			JSONArray jsonpaths = (JSONArray) jsongeometry.get("paths");
			LineString[] lineStrings = new LineString[jsonpaths.length()];
			for (int i = 0; i < jsonpaths.length(); i++) {
				JSONArray jsonpath = (JSONArray) jsonpaths.get(i);
				Coordinate[] path = new Coordinate[jsonpath.length()];
				for (int j = 0; j < jsonpath.length(); j++) {
					JSONArray jsonpoint = (JSONArray) jsonpath.get(j);
					double x = (Double) jsonpoint.get(0);
					double y = (Double) jsonpoint.get(1);
					path[j] = new Coordinate(y, x);
				}
				lineStrings[i] = geometryFactory.createLineString(path);
			}
			geometry = geometryFactory.createMultiLineString(lineStrings);
		} else if (geometryType.equals(GEOMETRY_POLYGON)) {
			JSONArray jsonrings = (JSONArray) jsongeometry.get("rings");
			Polygon[] polygons = new Polygon[jsonrings.length()];
			for (int i = 0; i < jsonrings.length(); i++) {
				JSONArray jsonring = (JSONArray) jsonrings.get(i);
				Coordinate[] ring = new Coordinate[jsonring.length()];
				for (int j = 0; j < jsonring.length(); j++) {
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

	public EsriFeatureObj[] getFeatures() {
		return features;
	}

	public String getGeometryType() {
		return geometryType;
	}

	public void write(EsriFeatureObj[] features, String fileName)
			throws JSONException, IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(fileName), "UTF-8"));

		JSONObject jsonobject = new JSONObject();
		jsonobject.put("displayFieldName", "Name");

		Geometry geo = features[0].geometry;
		if (geo instanceof Point) {
			geometryType = GEOMETRY_POINT;
		} else if (geo instanceof LineString) {
			geometryType = GEOMETRY_POLYLINE;
		} else if (geo instanceof MultiLineString) {
			geometryType = GEOMETRY_POLYLINE;
		} else if (geo instanceof Polygon) {
			geometryType = GEOMETRY_POLYGON;
		} else if (geo instanceof MultiPolygon) {
			geometryType = GEOMETRY_POLYGON;
		}
		jsonobject.put("geometryType", geometryType);

		JSONArray arr = new JSONArray();
		for (int i = 0; i < features.length; i++) {
			EsriFeatureObj feature = features[i];
			JSONObject jsonattributes = new JSONObject();
			jsonattributes.put("OBJECTID", feature.objectId);
			jsonattributes.put("Name", feature.name);
			JSONObject jsongeometry = writeGeometry(feature.geometry);

			JSONObject object = new JSONObject();
			object.put("attributes", jsonattributes);
			object.put("geometry", jsongeometry);
			arr.put(object);
		}

		jsonobject.put("features", arr);

		writer.append(jsonobject.toString());
		writer.flush();
		writer.close();
	}

	private JSONObject writeGeometry(Geometry geometry) throws JSONException {
		JSONObject jsongeometry = new JSONObject();

		if (geometry instanceof Point) {
			Point point = (Point) geometry;
			jsongeometry.put("x", point.getY());
			jsongeometry.put("y", point.getX());
		} else if (geometry instanceof LineString) {
			LineString lineString = (LineString) geometry;
			JSONArray jsonpaths = new JSONArray();
			Coordinate[] path = lineString.getCoordinates();
			JSONArray jsonpath = new JSONArray();
			for (int j = 0; j < path.length; j++) {
				JSONArray jsonpoint = new JSONArray();
				jsonpoint.put(path[j].y);
				jsonpoint.put(path[j].x);
				jsonpath.put(jsonpoint);
			}
			jsonpaths.put(jsonpath);
			jsongeometry.put("paths", jsonpaths);
		} else if (geometry instanceof MultiLineString) {
			MultiLineString multiLineString = (MultiLineString) geometry;
			int num = multiLineString.getNumGeometries();
			JSONArray jsonpaths = new JSONArray();
			for (int i = 0; i < num; i++) {
				LineString lineString = (LineString) multiLineString
						.getGeometryN(i);
				Coordinate[] path = lineString.getCoordinates();
				JSONArray jsonpath = new JSONArray();
				for (int j = 0; j < path.length; j++) {
					JSONArray jsonpoint = new JSONArray();
					jsonpoint.put(path[j].y);
					jsonpoint.put(path[j].x);
					jsonpath.put(jsonpoint);
				}
				jsonpaths.put(jsonpath);
			}
			jsongeometry.put("paths", jsonpaths);

		} else if (geometry instanceof Polygon) {
			Polygon polygon = (Polygon) geometry;
			JSONArray jsonpaths = new JSONArray();
			Coordinate[] ring = polygon.getCoordinates();
			JSONArray jsonpath = new JSONArray();
			for (int j = 0; j < ring.length; j++) {
				JSONArray jsonpoint = new JSONArray();
				jsonpoint.put(ring[j].y);
				jsonpoint.put(ring[j].x);
				jsonpath.put(jsonpoint);
			}
			jsonpaths.put(jsonpath);
			jsongeometry.put("rings", jsonpaths);

		} else if (geometry instanceof MultiPolygon) {
			MultiPolygon multiPolygon = (MultiPolygon) geometry;
			int num = multiPolygon.getNumGeometries();
			JSONArray jsonpaths = new JSONArray();
			for (int i = 0; i < num; i++) {
				Polygon polygon = (Polygon) multiPolygon.getGeometryN(i);
				Coordinate[] ring = polygon.getExteriorRing().getCoordinates();
				JSONArray jsonpath = new JSONArray();
				for (int j = 0; j < ring.length; j++) {
					JSONArray jsonpoint = new JSONArray();
					jsonpoint.put(ring[j].y);
					jsonpoint.put(ring[j].x);
					jsonpath.put(jsonpoint);
				}
				jsonpaths.put(jsonpath);
			}
			jsongeometry.put("rings", jsonpaths);
		}

		return jsongeometry;
	}

	/**
	 * @param args
	 * @throws JSONException
	 * @throws IOException
	 */
	public static void main(String[] args) throws JSONException, IOException {
		String fileName = "map/44.json";
		// String fileName = "test.txt";
		EsriJSONParser parser = new EsriJSONParser();
		EsriFeatureObj[] features = parser.read(fileName);
		System.out.println(features[0].geometry.getNumPoints());

		parser.write(features, "test.txt");

		EsriFeatureObj[] features2 = parser.read("test.txt");
		System.out.println(features2[0].geometry.getNumPoints());
	}
}
