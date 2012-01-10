package edu.zjut.map;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.swing.JFrame;

import org.gicentre.utils.colour.ColourTable;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.json.JSONException;

import au.com.bytecode.opencsv.CSVReader;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import edu.zjut.common.data.geo.EsriFeatureObj;
import edu.zjut.common.io.EsriJSONParser;
import edu.zjut.map.overlay.DefaultMapMarker;
import edu.zjut.map.overlay.EsriLayer;
import edu.zjut.map.overlay.IconMarker;
import edu.zjut.map.overlay.MapMarker;
import edu.zjut.map.overlay.MapPolygon;
import edu.zjut.map.overlay.MapPolyline;
import edu.zjut.map.overlay.Overlay;

public class Test {
	static Color layerBorderColor = new Color(10, 10, 10);
	static Color layerFillColor = new Color(10, 10, 10, 100);
	static Color layerHighlightBorderColor = Color.WHITE;
	static Color layerHighlightColor = new Color(10, 10, 10, 100);

	static Color borderColor = new Color(255, 0, 0);
	static Color highlightBorderColor = new Color(0, 0, 255);
	static Color fillColor = new Color(255, 0, 0, 100);
	static Color highlightColor = new Color(0, 0, 255, 100);

	public static void main(String[] args) throws Exception {
		JMapPanel viewer = new JMapPanel();
		viewer.loadMapConfig("map_config.xml");

		// loadTestMarker(viewer);
		// loadTestPolygon(viewer);
		// loadTestPolyline(viewer);

//		loadHangzhouRegion(viewer);

		// loadHangzhouLayer2(viewer);
		 loadHangzhouLayer(viewer);
		 
		// loadHouseData(viewer);

		JFrame jframe = new JFrame();
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		jframe.add(viewer, BorderLayout.CENTER);
		jframe.setLocation(100, 100);
		jframe.pack();
		jframe.setVisible(true);
	}

	public static void loadHangzhouRegion(JMapPanel viewer) throws Exception {
		CSVReader reader = new CSVReader(new FileReader("城区_规划单元.csv"));
		List<String[]> fileContent = reader.readAll();
		HashMap<String, HashSet<String>> regions = new HashMap<>();

		for (String[] line : fileContent) {
			String region = line[0];
			String unit = line[1];

			HashSet<String> set = regions.get(region);
			if (set == null) {
				set = new HashSet<String>();
				set.add(unit);
				regions.put(region, set);
			} else {
				set.add(unit);
			}
		}

		String unitFileName = "map/0.json";
		EsriJSONParser parser = new EsriJSONParser();
		EsriFeatureObj[] unitFeatures = parser.read(unitFileName);

		HashMap<String, Geometry> units = new HashMap<String, Geometry>();
		for (EsriFeatureObj unit : unitFeatures) {
			units.put(unit.name, unit.geometry);
		}

		int count = 0;
		EsriFeatureObj[] regionFeatures = new EsriFeatureObj[regions.size()];
		for (String r : regions.keySet()) {
			HashSet<String> set = regions.get(r);

			Geometry geo = null;
			for (String u : set) {
				if (geo == null)
					geo = units.get(u);
				else
					geo = geo.union(units.get(u));
			}

			regionFeatures[count] = new EsriFeatureObj(count, r, geo);
			count++;
		}

		parser.write(regionFeatures, "test.txt");
		
		EsriLayer layer = new EsriLayer(regionFeatures);
		layer.setHighlightBorderWidth(3.0f);

		Overlay[] overlays = layer.getOverlays();
		ColourTable cTable = ColourTable.getPresetColourTable(ColourTable.REDS,
				0, overlays.length);
		for (int i = 0; i < overlays.length; i++) {
			Overlay overlay = overlays[i];

			if (i % 5 != 0) {
				Color c = new Color(cTable.findColour(i));
				overlay.setFillColor(new Color(c.getRed(), c.getGreen(), c
						.getBlue(), 200));
			}
		}

		viewer.addLayer(layer);
	}

	public static void loadHangzhouLayer(JMapPanel viewer) throws Exception {

		String fileName = "test.txt";
		EsriJSONParser parser = new EsriJSONParser();
		parser.read(fileName);
		EsriFeatureObj[] features = parser.getFeatures();

		EsriLayer layer = new EsriLayer(features);
		layer.setHighlightBorderWidth(3.0f);

		Overlay[] overlays = layer.getOverlays();
		ColourTable cTable = ColourTable.getPresetColourTable(ColourTable.REDS,
				0, overlays.length);
		for (int i = 0; i < overlays.length; i++) {
			Overlay overlay = overlays[i];

			if (i % 5 != 0) {
				Color c = new Color(cTable.findColour(i));
				overlay.setFillColor(new Color(c.getRed(), c.getGreen(), c
						.getBlue(), 200));
			}
		}

		viewer.addLayer(layer);
	}

	public static void loadHangzhouLayer2(JMapPanel viewer) throws IOException,
			JSONException {
		String fileName = "map/44.json";
		EsriJSONParser parser = new EsriJSONParser();
		EsriFeatureObj[] features = parser.read(fileName);

		List<EsriFeatureObj> featuresList = new ArrayList<EsriFeatureObj>();
		for (EsriFeatureObj feature : features) {
			if (!feature.name.equals("余杭区") && !feature.name.equals("萧山区"))
				featuresList.add(feature);
		}

		int len = featuresList.size();
		System.out.println(len);

		EsriFeatureObj[] featuresArr = new EsriFeatureObj[len];
		for (int i = 0; i < len; i++) {
			featuresArr[i] = featuresList.get(i);
		}

		EsriLayer layer = new EsriLayer(featuresArr);
		layer.setFill(false);
		layer.setBorderWidth(3.0f);
		layer.setBorderColor(new Color(50, 50, 50, 200));

		// Overlay[] overlays = layer.getOverlays();
		// ColourTable cTable =
		// ColourTable.getPresetColourTable(ColourTable.REDS,
		// 0, overlays.length);
		// for (int i = 0; i < overlays.length; i++) {
		// Overlay overlay = overlays[i];
		//
		// Color c = new Color(cTable.findColour(i));
		// overlay.setFillColor(new Color(c.getRed(), c.getGreen(), c
		// .getBlue(), 200));
		// }

		viewer.addLayer(layer);
	}

	public static void loadHouseData(JMapPanel viewer) throws IOException {
		String infile = "hz_data/smid_geo.csv";
		int namecol = 1;
		int xcol = 4;
		int ycol = 3;

		CSVReader reader = new CSVReader(new FileReader(infile));
		List<String[]> list = reader.readAll();

		ColourTable cTable = ColourTable.getPresetColourTable(ColourTable.REDS,
				0, list.size());

		for (int i = 0; i < list.size(); i++) {
			String name = list.get(i)[namecol];
			double x = Double.parseDouble(list.get(i)[xcol]);
			double y = Double.parseDouble(list.get(i)[ycol]);

			// viewer.addOverlay(new IconMarker(new GeoPosition(x, y), name));

			// viewer.addOverlay(new DefaultMapMarker(new GeoPosition(x, y),
			// name));

			MapMarker marker = new MapMarker(new GeoPosition(x, y), name);
			marker.setRadius(5);

			marker.setBorder(false);
			marker.setFillColor(new Color(cTable.findColour(i)));
			marker.setHighlightColor(Color.BLACK);

			viewer.addOverlay(marker);
		}
	}

	public static void loadHangzhouPolygon(JMapPanel viewer)
			throws IOException, JSONException {
		String fileName = "map/0.json";
		EsriJSONParser parser = new EsriJSONParser();
		EsriFeatureObj[] features = parser.read(fileName);
		for (int i = 0; i < features.length; i++) {
			MapPolygon marker = new MapPolygon(
					(MultiPolygon) features[i].geometry, features[i].name);

			marker.setFillColor(new Color(10, 10, 10, 100));
			marker.setBorderColor(Color.BLACK);

			viewer.addOverlay(marker);
		}
	}

	public static void loadHouseData2(JMapPanel viewer) throws IOException {
		String infile = "data/house_data.csv";
		int namecol = 1;
		int xcol = 5;
		int ycol = 4;

		CSVReader reader = new CSVReader(new FileReader(infile));
		List<String[]> list = reader.readAll();

		for (int i = 0; i < list.size(); i++) {
			String name = list.get(i)[namecol];
			double x = Double.parseDouble(list.get(i)[xcol]);
			double y = Double.parseDouble(list.get(i)[ycol]);

			viewer.addOverlay(new IconMarker(new GeoPosition(x, y), name));
		}
	}

	public static void loadTestPolygon(JMapPanel viewer) {
		GeometryFactory geometryFactory = new GeometryFactory();
		Polygon polyon = geometryFactory.createPolygon(
				geometryFactory.createLinearRing(new Coordinate[] {
						new Coordinate(30.30190008, 120.1212215),
						new Coordinate(30.283, 120.35),
						new Coordinate(30.323, 120.166),
						new Coordinate(30.30190008, 120.1212215) }), null);
		MapPolygon marker3 = new MapPolygon(polyon);
		viewer.addOverlay(marker3);
	}

	public static void loadTestMarker(JMapPanel viewer) {
		MapMarker marker = new MapMarker(new GeoPosition(30.30190008,
				120.1212215), "你好");

		IconMarker marker2 = new IconMarker(new GeoPosition(30.40190008,
				120.1212215), "世界你好");

		viewer.addOverlay(marker);
		viewer.addOverlay(marker2);
	}

	public static void loadTestPolyline(JMapPanel viwer) throws IOException,
			JSONException {
		String fileName = "map/14.json";
		EsriJSONParser parser = new EsriJSONParser();
		EsriFeatureObj[] features = parser.read(fileName);
		for (int i = 0; i < features.length; i++) {
			// MapPolygon marker4 = new MapPolygon(
			// (MultiPolygon) features[i].geometry, features[i].name);

			MapPolyline marker4 = new MapPolyline(
					(MultiLineString) features[i].geometry, features[i].name);

			viwer.addOverlay(marker4);
		}
	}
}
