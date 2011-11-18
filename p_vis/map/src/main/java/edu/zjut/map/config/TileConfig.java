package edu.zjut.map.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import com.thoughtworks.xstream.XStream;

public class TileConfig {

	public static String CONFIG_FILE = "tile_servers.xml";

	public ArrayList<TileServer> tileServerList;
	private XStream xstream;

	public TileConfig() {
		xstream = new XStream();
		xstream.alias("TileServer", TileServer.class);
	}

	@SuppressWarnings("unchecked")
	public void loadTileServers() {
		// xstream.aliasField("name", TileServer.class, "serverName");
		// xstream.aliasField("url", TileServer.class, "templateUrl");

		try {
			InputStreamReader in = new InputStreamReader(new FileInputStream(
					CONFIG_FILE), "utf-8");
			tileServerList = (ArrayList<TileServer>) xstream.fromXML(in);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 0; i < tileServerList.size(); i++) {
			TileServer s = tileServerList.get(i);
			System.out.println(s.serverName);
			System.out.println(s.templateUrl);
		}
	}

	public void saveTileServers() {
		String xml = xstream.toXML(tileServerList);
		System.out.println(xml);

		try {
			OutputStreamWriter out = new OutputStreamWriter(
					new FileOutputStream(CONFIG_FILE), "utf-8");
			xstream.toXML(tileServerList, out);
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		TileConfig config = new TileConfig();
		config.loadTileServers();

		config.saveTileServers();
	}
}
