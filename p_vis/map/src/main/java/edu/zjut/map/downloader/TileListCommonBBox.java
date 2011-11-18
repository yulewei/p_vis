/*
 * Copyright 2008, Friedrich Maier
 * 
 * This file is part of JTileDownloader.
 *
 * JTileDownloader is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JTileDownloader is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy (see file COPYING.txt) of the GNU 
 * General Public License along with JTileDownloader.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package edu.zjut.map.downloader;

import java.util.Vector;

import org.jdesktop.swingx.mapviewer.Tile;

/**
 * 
 */
public class TileListCommonBBox
{
	private int[] _downloadZoomLevels;

	private int[] _xTopLeft = new int[] { 0 };
	private int[] _yTopLeft = new int[] { 0 };
	private int[] _xBottomRight = new int[] { 0 };
	private int[] _yBottomRight = new int[] { 0 };

	/**
	 * @see org.openstreetmap.fma.jtiledownloader.tilelist.TileList#getTileListToDownload()
	 */
	public Vector<Tile> getTileListToDownload()
	{
		Vector<Tile> tilesToDownload = new Vector<Tile>();

		for (int indexZoomLevel = 0; indexZoomLevel < getDownloadZoomLevels().length; indexZoomLevel++)
		{
			int xStart = getMin(_xTopLeft[indexZoomLevel],
					_xBottomRight[indexZoomLevel]);
			int xEnd = getMax(_xTopLeft[indexZoomLevel],
					_xBottomRight[indexZoomLevel]);

			int yStart = getMin(_yTopLeft[indexZoomLevel],
					_yBottomRight[indexZoomLevel]);
			int yEnd = getMax(_yTopLeft[indexZoomLevel],
					_yBottomRight[indexZoomLevel]);

			for (int downloadTileXIndex = xStart; downloadTileXIndex <= xEnd; downloadTileXIndex++)
			{
				for (int downloadTileYIndex = yStart; downloadTileYIndex <= yEnd; downloadTileYIndex++)
				{
					String urlPathToFile = getDownloadZoomLevels()[indexZoomLevel]
							+ "_"
							+ downloadTileXIndex
							+ "_"
							+ downloadTileYIndex + ".png";

					log("add " + urlPathToFile + " to download list.");
					tilesToDownload.addElement(new Tile(downloadTileXIndex,
							downloadTileYIndex,
							getDownloadZoomLevels()[indexZoomLevel]));
				}
			}

		}

		log("finished");

		return tilesToDownload;

	}

	/**
	 * @param minLat
	 * @param minLon
	 * @param maxLat
	 * @param maxLon
	 */
	public void calculateTileValuesXY(double minLat, double minLon,
			double maxLat, double maxLon)
	{
		int zoomLevelSize = getDownloadZoomLevels().length;

		_xTopLeft = new int[zoomLevelSize];
		_yTopLeft = new int[zoomLevelSize];
		_xBottomRight = new int[zoomLevelSize];
		_yBottomRight = new int[zoomLevelSize];

		for (int indexZoomLevel = 0; indexZoomLevel < zoomLevelSize; indexZoomLevel++)
		{
			setXTopLeft(
					calculateTileX(minLon,
							getDownloadZoomLevels()[indexZoomLevel]),
					indexZoomLevel);
			setYTopLeft(
					calculateTileY(maxLat,
							getDownloadZoomLevels()[indexZoomLevel]),
					indexZoomLevel);
			setXBottomRight(
					calculateTileX(maxLon,
							getDownloadZoomLevels()[indexZoomLevel]),
					indexZoomLevel);
			setYBottomRight(
					calculateTileY(minLat,
							getDownloadZoomLevels()[indexZoomLevel]),
					indexZoomLevel);

			log("XTopLeft=" + getXTopLeft()[indexZoomLevel]);
			log("YTopLeft=" + getYTopLeft()[indexZoomLevel]);
			log("XBottomRight=" + getXBottomRight()[indexZoomLevel]);
			log("YBottomRight=" + getYBottomRight()[indexZoomLevel]);
		}

	}

	/**
	 * Setter for topLeft
	 * 
	 * @param value
	 *            the xTopLeft to set
	 */
	public final void initXTopLeft(int value)
	{
		_xTopLeft = new int[getDownloadZoomLevels().length];
		for (int index = 0; index < getDownloadZoomLevels().length; index++)
		{
			_xTopLeft[index] = value;

		}
	}

	/**
	 * Setter for topLeft
	 * 
	 * @param value
	 *            the xTopLeft to set
	 */
	public final void initYTopLeft(int value)
	{
		_yTopLeft = new int[getDownloadZoomLevels().length];
		for (int index = 0; index < getDownloadZoomLevels().length; index++)
		{
			_yTopLeft[index] = value;

		}
	}

	/**
	 * Setter for BottomRight
	 * 
	 * @param value
	 *            the xBottomRight to set
	 */
	public final void initXBottomRight(int value)
	{
		_xBottomRight = new int[getDownloadZoomLevels().length];
		for (int index = 0; index < getDownloadZoomLevels().length; index++)
		{
			_xBottomRight[index] = value;

		}
	}

	/**
	 * Setter for BottomRight
	 * 
	 * @param value
	 *            the xBottomRight to set
	 */
	public final void initYBottomRight(int value)
	{
		_yBottomRight = new int[getDownloadZoomLevels().length];
		for (int index = 0; index < getDownloadZoomLevels().length; index++)
		{
			_yBottomRight[index] = value;

		}
	}

	/**
	 * Getter for xTopLeft
	 * 
	 * @return the xTopLeft
	 */
	public final int[] getXTopLeft()
	{
		return _xTopLeft.clone();
	}

	/**
	 * Setter for topLeft
	 * 
	 * @param topLeft
	 *            the xTopLeft to set
	 * @param index
	 */
	public final void setXTopLeft(int topLeft, int index)
	{
		_xTopLeft[index] = topLeft;
	}

	/**
	 * Getter for yTopLeft
	 * 
	 * @return the yTopLeft
	 */
	public final int[] getYTopLeft()
	{
		return _yTopLeft.clone();
	}

	/**
	 * Setter for topLeft
	 * 
	 * @param topLeft
	 *            the yTopLeft to set
	 * @param index
	 */
	public final void setYTopLeft(int topLeft, int index)
	{
		_yTopLeft[index] = topLeft;
	}

	/**
	 * Getter for xBottomRight
	 * 
	 * @return the xBottomRight
	 */
	public final int[] getXBottomRight()
	{
		return _xBottomRight.clone();
	}

	/**
	 * Setter for bottomRight
	 * 
	 * @param bottomRight
	 *            the xBottomRight to set
	 * @param index
	 */
	public final void setXBottomRight(int bottomRight, int index)
	{
		_xBottomRight[index] = bottomRight;
	}

	/**
	 * Getter for yBottomRight
	 * 
	 * @return the yBottomRight
	 */
	public final int[] getYBottomRight()
	{
		return _yBottomRight.clone();
	}

	/**
	 * Setter for bottomRight
	 * 
	 * @param bottomRight
	 *            the yBottomRight to set
	 * @param index
	 */
	public final void setYBottomRight(int bottomRight, int index)
	{
		_yBottomRight[index] = bottomRight;
	}

	/**
	 * @return tile count
	 */
	public int getTileCount()
	{
		int count = 0;
		for (int indexZoomLevels = 0; indexZoomLevels < getDownloadZoomLevels().length; indexZoomLevels++)
		{
			count += Integer.parseInt(""
					+ (Math.abs(getXBottomRight()[indexZoomLevels]
							- getXTopLeft()[indexZoomLevels]) + 1)
					* (Math.abs(getYBottomRight()[indexZoomLevels]
							- getYTopLeft()[indexZoomLevels]) + 1));
		}

		return count;
	}

	
	/**
	 * @param lat
	 * @param zoomLevel
	 * @return tileY
	 */
	protected final int calculateTileY(double lat, int zoomLevel)
	{
		if (lat < Constants.MIN_LAT)
		{
			lat = Constants.MIN_LAT;
		}
		if (lat > Constants.MAX_LAT)
		{
			lat = Constants.MAX_LAT;
		}
		int y = (int) Math.floor((1 - Math.log(Math.tan(lat * Math.PI / 180)
				+ 1 / Math.cos(lat * Math.PI / 180))
				/ Math.PI)
				/ 2 * (1 << zoomLevel));
		return y;
	}

	/**
	 * @param lon
	 * @param zoomLevel
	 * @return tileX
	 */
	protected final int calculateTileX(double lon, int zoomLevel)
	{
		if (lon < Constants.MIN_LON)
		{
			lon = Constants.MIN_LON;
		}
		if (lon > Constants.MAX_LON)
		{
			lon = Constants.MAX_LON;
		}

		int x = (int) Math.floor((lon + 180) / 360 * (1 << zoomLevel));
		return x;
	}

	/**
	 * @param minValue
	 * @param maxValue
	 * @return
	 */
	protected final int getMax(int minValue, int maxValue)
	{
		if (minValue > maxValue)
		{
			return minValue;
		}
		return maxValue;
	}

	/**
	 * @param minValue
	 * @param maxValue
	 * @return
	 */
	protected final int getMin(int minValue, int maxValue)
	{
		if (minValue > maxValue)
		{
			return maxValue;
		}
		return minValue;
	}

	/**
	 * Getter for downloadZoomLevel
	 * 
	 * @return the downloadZoomLevel
	 */
	public final int[] getDownloadZoomLevels()
	{
		return _downloadZoomLevels.clone();
	}

	/**
	 * Setter for downloadZoomLevel
	 * 
	 * @param downloadZoomLevel
	 *            the downloadZoomLevel to set
	 */
	public final void setDownloadZoomLevels(int[] downloadZoomLevel)
	{
		_downloadZoomLevels = downloadZoomLevel.clone();
	}
	
	/**
	 * method to write to System.out
	 * 
	 * @param msg
	 *            message to log
	 */
	public void log(String msg)
	{
		System.out.println(msg);
	}
}
