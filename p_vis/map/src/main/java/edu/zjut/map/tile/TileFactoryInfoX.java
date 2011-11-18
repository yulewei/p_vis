package edu.zjut.map.tile;

import java.awt.geom.Point2D;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdesktop.swingx.mapviewer.TileFactoryInfo;

/**
 * 
 * @author yulewei
 */
public class TileFactoryInfoX extends TileFactoryInfo {
	private int minimumZoomLevel;
	private int maximumZoomLevel;
	private int totalMapZoom;
	// the size of each tile (assumes they are square)
	private int tileSize = 256;

	/**
	 * 必须以参数形式, {zoom}, {x}, {y}, 如:
	 * http://tile.openstreetmap.org/{zoom}/{x}/{y}.png
	 */
	protected String tmsURL;

	/**
	 * The number of tiles wide at each zoom level
	 */
	protected int[] mapWidthInTilesAtZoom;
	/**
	 * An array of coordinates in <em>pixels</em> that indicates the center in
	 * the world map for the given zoom level.
	 */
	protected Point2D[] mapCenterInPixelsAtZoom;// = new Point2D.Double[18];

	/**
	 * An array of doubles that contain the number of pixels per degree of
	 * longitude at a give zoom level.
	 */
	protected double[] longitudeDegreeWidthInPixels;

	/**
	 * An array of doubles that contain the number of radians per degree of
	 * longitude at a given zoom level (where longitudeRadianWidthInPixels[0] is
	 * the most zoomed out)
	 */
	protected double[] longitudeRadianWidthInPixels;

	public TileFactoryInfoX(String name, String tmsURL, int minimumZoomLevel,
			int maximumZoomLevel) {
		this(name, tmsURL, minimumZoomLevel, maximumZoomLevel, maximumZoomLevel
				- minimumZoomLevel + 1, 256, true, true);
	}

	public TileFactoryInfoX(String name, String tmsURL, int minimumZoomLevel,
			int maximumZoomLevel, int totalMapZoom, int tileSize, boolean xr2l,
			boolean yt2b) {
		super(name, minimumZoomLevel, maximumZoomLevel, totalMapZoom, tileSize,
				xr2l, yt2b, null, null, null, null);

		this.tmsURL = tmsURL;

		this.minimumZoomLevel = minimumZoomLevel;
		this.maximumZoomLevel = maximumZoomLevel;
		this.totalMapZoom = totalMapZoom;
		this.tileSize = tileSize;

		int tilesize = this.getTileSize(0);

		longitudeDegreeWidthInPixels = new double[totalMapZoom + 1];
		longitudeRadianWidthInPixels = new double[totalMapZoom + 1];
		mapCenterInPixelsAtZoom = new Point2D.Double[totalMapZoom + 1];
		mapWidthInTilesAtZoom = new int[totalMapZoom + 1];

		// // for each zoom level
		// for (int z = 0; z <= totalMapZoom; z++)
		// {
		// // how wide is each degree of longitude in pixels
		// longitudeDegreeWidthInPixels[z] = (double) tilesize / 360;
		// // how wide is each radian of longitude in pixels
		// longitudeRadianWidthInPixels[z] = (double) tilesize
		// / (2.0 * Math.PI);
		// int t2 = tilesize / 2;
		// mapCenterInPixelsAtZoom[z] = new Point2D.Double(t2, t2);
		// mapWidthInTilesAtZoom[z] = tilesize / this.getTileSize(0);
		// tilesize *= 2;
		// }

		// for each zoom level
		for (int z = minimumZoomLevel; z <= maximumZoomLevel; z++) {
			tilesize = 256 * (1 << z);

			// how wide is each degree of longitude in pixels
			longitudeDegreeWidthInPixels[z - minimumZoomLevel] = (double) tilesize / 360;
			// how wide is each radian of longitude in pixels
			longitudeRadianWidthInPixels[z - minimumZoomLevel] = (double) tilesize
					/ (2.0 * Math.PI);
			int t2 = tilesize / 2;
			mapCenterInPixelsAtZoom[z - minimumZoomLevel] = new Point2D.Double(
					t2, t2);
			mapWidthInTilesAtZoom[z - minimumZoomLevel] = tilesize
					/ this.getTileSize(0);
		}
	}

	/**
	 * 
	 * @param zoom
	 * @return
	 */
	public double getLongitudeDegreeWidthInPixels(int zoom) {
		zoom = zoom < minimumZoomLevel ? minimumZoomLevel : zoom;
		zoom = zoom > maximumZoomLevel ? maximumZoomLevel : zoom;
		return longitudeDegreeWidthInPixels[zoom - minimumZoomLevel];
	}

	/**
	 * 
	 * @param zoom
	 * @return
	 */
	public double getLongitudeRadianWidthInPixels(int zoom) {
		zoom = zoom < minimumZoomLevel ? minimumZoomLevel : zoom;
		zoom = zoom > maximumZoomLevel ? maximumZoomLevel : zoom;
		return longitudeRadianWidthInPixels[zoom - minimumZoomLevel];
	}

	/**
	 * 
	 * @param zoom
	 * @return
	 */
	public int getMapWidthInTilesAtZoom(int zoom) {
		zoom = zoom < minimumZoomLevel ? minimumZoomLevel : zoom;
		zoom = zoom > maximumZoomLevel ? maximumZoomLevel : zoom;
		return mapWidthInTilesAtZoom[zoom - minimumZoomLevel];
	}

	/**
	 * 
	 * @param zoom
	 * @return
	 */
	public Point2D getMapCenterInPixelsAtZoom(int zoom) {
		zoom = zoom < minimumZoomLevel ? minimumZoomLevel : zoom;
		zoom = zoom > maximumZoomLevel ? maximumZoomLevel : zoom;

		return mapCenterInPixelsAtZoom[zoom - minimumZoomLevel];
	}

	public String getTileUrl(int x, int y, int zoom) {
		// http://tile.openstreetmap.org/{zoom}/{x}/{y}.png
		String url = this.tmsURL;

		url = url.replace("{zoom}", String.valueOf(zoom));
		url = url.replace("{x}", String.valueOf(x));
		url = url.replace("{y}", String.valueOf(y));

		// String url = this.baseURL + "/" + zoom + "/" + x + "/" + y + ".png";
		return url;
	}

	public int[] getTileZoomXY(URI url) {
		Integer[] order = new Integer[3];
		order[0] = tmsURL.indexOf("{zoom}");
		order[1] = tmsURL.indexOf("{x}");
		order[2] = tmsURL.indexOf("{y}");

		Integer[] arr = Arrays.copyOf(order, order.length);
		Arrays.sort(arr);
		List<Integer> list = Arrays.asList(arr);

		int[] index = new int[order.length];

		for (int i = 0; i < order.length; i++)
			index[i] = list.indexOf(order[i]);

		String regex = tmsURL.replace("{zoom}", "(\\d+)")
				.replace("{x}", "(\\d+)").replace("{y}", "(\\d+)");

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(url.toString());

		int[] find = new int[3];

		while (matcher.find()) {
			for (int i = 1; i <= matcher.groupCount(); i++)
				find[i - 1] = Integer.parseInt(matcher.group(i));
		}

		return new int[] { find[index[0]], find[index[1]], find[index[2]] };
	}

	public String getTileType() {
		return ".png";
	}
}
