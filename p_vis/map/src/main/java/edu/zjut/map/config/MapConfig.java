package edu.zjut.map.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.thoughtworks.xstream.XStream;

public class MapConfig {

	public static String CONFIG_FILE = "map_config.xml";

	public String serverName = TileServer.DEFAULT_SERVER_NAME;
	public String templateUrl = TileServer.DEFAULT_TEMPLATE_URL;
	public String cacheDir = "C:/tiles/gmap_hz";
	public int minzoom = 1;
	public int maxzoom = 16;
	public int zoom = 11;
	public double lat = 30.30190008;
	public double lon = 120.1812215;
	public boolean isShowStatusBar = true;
	public boolean isShowCenterCross = false;
	public boolean isShowScaleRule = false;
	public boolean isDrawTileBorders = true;
	public int width = 800;
	public int height = 800;

	public static MapConfig loadConfig(String filename) {
		XStream xstream = new XStream();
		xstream.alias("config", MapConfig.class);

		MapConfig config = null;

		try {
			InputStreamReader in = new InputStreamReader(new FileInputStream(
					filename), "utf-8");
			config = (MapConfig) xstream.fromXML(in);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return config;
	}

	public static void saveConfig(MapConfig config, String filename) {
		XStream xstream = new XStream();
		xstream.alias("config", MapConfig.class);

		String xml = xstream.toXML(config);
		System.out.println(xml);

		try {
			OutputStreamWriter out = new OutputStreamWriter(
					new FileOutputStream(filename), "utf-8");
			xstream.toXML(config, out);
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// MapConfig config = MapConfig.loadTileServers();
		MapConfig config = new MapConfig();
		MapConfig.saveConfig(config, "test.xml");
	}
}
