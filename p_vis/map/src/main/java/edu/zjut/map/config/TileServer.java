package edu.zjut.map.config;

public class TileServer {

	// public static String DEFAULT_SERVER_NAME = "OSM Mapnik";
	// public static String DEFAULT_TEMPLATE_URL =
	// "http://tile.openstreetmap.org/{zoom}/{x}/{y}.png";

	public static String DEFAULT_SERVER_NAME = "Google Map";
	public static String DEFAULT_TEMPLATE_URL = "http://mt.google.com/vt/lyrs=m@161000000&hl=zh-CN&x={x}&y={y}&z={zoom}&s=Ga";

	public String serverName;
	public String templateUrl;
}
