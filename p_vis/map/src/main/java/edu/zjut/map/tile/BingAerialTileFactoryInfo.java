package edu.zjut.map.tile;

import org.jdesktop.swingx.mapviewer.TileFactoryInfo;

public class BingAerialTileFactoryInfo extends TileFactoryInfo
{
	static final int max = 17;

	public BingAerialTileFactoryInfo()
	{
		super(0, max, max, 256, true, true,
				"http://ecn.t2.tiles.virtualearth.net/tiles/", "x", "y", "z");
	}

	public String getTileUrl(int x, int y, int zoom)
	{
		zoom = max - zoom;
		String quadtree = computeQuadTree(zoom, x, y);
		return baseURL + "tiles/a" + quadtree + ".jpeg?g=587";
	}

	static String computeQuadTree(int zoom, int tilex, int tiley)
	{
		StringBuilder k = new StringBuilder();
		for (int i = zoom; i > 0; i--)
		{
			char digit = 48;
			int mask = 1 << (i - 1);
			if ((tilex & mask) != 0)
			{
				digit += 1;
			}
			if ((tiley & mask) != 0)
			{
				digit += 2;
			}
			k.append(digit);
		}
		return k.toString();
	}
}
