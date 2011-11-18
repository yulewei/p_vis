package edu.zjut.map.overlay;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import org.jdesktop.swingx.mapviewer.DefaultTileFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import edu.zjut.map.JXMapViewerX;
import edu.zjut.map.config.TileServer;
import edu.zjut.map.tile.TileFactoryInfoX;

public class Test {

	public static void main(String[] args) {
		JXMapViewerX map = new JXMapViewerX();

		TileFactoryInfoX info = new TileFactoryInfoX(
				TileServer.DEFAULT_SERVER_NAME, TileServer.DEFAULT_TEMPLATE_URL, 10, 16);
		DefaultTileFactory tf = new DefaultTileFactory(info);

		map.setTileFactory(tf);
		map.setZoom(11);
		map.setAddressLocation(new GeoPosition(30.30190008, 120.1212215));

		testPolygon(map);

		JFrame jframe = new JFrame();
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setLocation(100, 100);
		jframe.setSize(800, 800);
		jframe.add(map, BorderLayout.CENTER);
		jframe.setVisible(true);
	}

	private static void testPolygon(JXMapViewerX map) {
		GeometryFactory geometryFactory = new GeometryFactory();
		Polygon poly = geometryFactory.createPolygon(
				geometryFactory.createLinearRing(new Coordinate[] {
						new Coordinate(30.266, 120.4),
						new Coordinate(30.283, 120.35),
						new Coordinate(30.323, 120.166),
						new Coordinate(30.216, 120.25),
						new Coordinate(30.266, 120.4) }), null);
		MultiPolygon mp = geometryFactory
				.createMultiPolygon(new Polygon[] { poly });
		MapPolygon marker = new MapPolygon(mp);
		map.setOverlayPainter(marker.getOverlayPainter());
	}

}
