package edu.zjut.map;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;

import org.gicentre.utils.colour.ColourTable;
import org.jdesktop.swingx.mapviewer.GeoPosition;

import au.com.bytecode.opencsv.CSVReader;

import com.vividsolutions.jts.geom.Coordinate;
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

	public static void main(String[] args) throws IOException {
		JMapPanel viewer = new JMapPanel();
		viewer.loadMapConfig("map_config.xml");

		loadTestMarker(viewer);
		// loadTestPolygon(viewer);
		// loadTestPolyline(viewer);

		// loadHangzhouLayer2(viewer);
//		loadHangzhouLayer(viewer);
//		loadHouseData(viewer);

		JFrame jframe = new JFrame();
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		jframe.add(viewer, BorderLayout.CENTER);
		jframe.setLocation(100, 100);
		jframe.pack();
		jframe.setVisible(true);
	}

	public static void loadHangzhouLayer(JMapPanel viewer) {
		String fileName = "map/0.json";
		EsriJSONParser parser = new EsriJSONParser(fileName);
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

	public static void loadHangzhouLayer2(JMapPanel viewer) {
		String fileName = "map/44.json";
		EsriJSONParser parser = new EsriJSONParser(fileName);
		EsriFeatureObj[] features = parser.getFeatures();
		EsriLayer layer = new EsriLayer(features);
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

	public static void loadHangzhouPolygon(JMapPanel viewer) {
		String fileName = "map/0.json";
		EsriJSONParser parser = new EsriJSONParser(fileName);
		EsriFeatureObj[] features = parser.getFeatures();
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

		IconMarker marker2 = new IconMarker(new GeoPosition(
				30.40190008, 120.1212215), "世界你好");

		viewer.addOverlay(marker);
		viewer.addOverlay(marker2);
	}

	public static void loadTestPolyline(JMapPanel viwer) {
		String fileName = "map/14.json";
		EsriJSONParser parser = new EsriJSONParser(fileName);
		EsriFeatureObj[] features = parser.getFeatures();
		for (int i = 0; i < features.length; i++) {
			// MapPolygon marker4 = new MapPolygon(
			// (MultiPolygon) features[i].geometry, features[i].name);

			MapPolyline marker4 = new MapPolyline(
					(MultiLineString) features[i].geometry, features[i].name);

			viwer.addOverlay(marker4);
		}
	}
}
