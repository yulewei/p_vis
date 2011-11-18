/*
 * Copyright 2008, Friedrich Maier
 * 
 * This file is part of JTileDownloader.
 * (see http://wiki.openstreetmap.org/index.php/JTileDownloader)
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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Properties;

public class DownloadConfig
{
	private Properties prop = new Properties();

	private String _outputZoomLevels = "";
	private String _tileServer = "";
	private String _outputLocation = "";
	private String _type = "";

	private static final String OUTPUT_ZOOM_LEVEL = "OutputZoomLevel";
	private static final String TILE_SERVER = "TileServer";
	private static final String OUTPUTLOCATION = "OutputLocation";
	public static final String TYPE = "Type";

	private double _minLat = 0.0;
	private double _minLon = 0.0;
	private double _maxLat = 0.0;
	private double _maxLon = 0.0;

	private static final String MIN_LAT = "MinLat";
	private static final String MIN_LON = "MinLon";
	private static final String MAX_LAT = "MaxLat";
	private static final String MAX_LON = "MaxLon";

	public static final String ID = "BBoxLatLon";

	public DownloadConfig(String propertyFileName)
	{
		loadFromFile(propertyFileName);
	}

	private void loadFromFile(String fileName)
	{
		try
		{
			prop.loadFromXML(new FileInputStream(fileName));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		_type = prop.getProperty(TYPE, "");
		_outputZoomLevels = prop.getProperty(OUTPUT_ZOOM_LEVEL, "12");
		_tileServer = prop.getProperty(TILE_SERVER, "");
		_outputLocation = prop.getProperty(OUTPUTLOCATION, "tiles");

		load(prop);
	}

	public void save(Properties prop)
	{
		setTemplateProperty(prop, TYPE, ID);

		setTemplateProperty(prop, MIN_LAT, "" + _minLat);
		setTemplateProperty(prop, MIN_LON, "" + _minLon);
		setTemplateProperty(prop, MAX_LAT, "" + _maxLat);
		setTemplateProperty(prop, MAX_LON, "" + _maxLon);
	}

	protected void setTemplateProperty(Properties prop, String key, String value)
	{
		System.out.println("setting property " + key + " to value " + value);
		prop.setProperty(key, value);
	}

	public void load(Properties prop)
	{
		_minLat = Double.parseDouble(prop.getProperty(MIN_LAT, "0.0"));
		_minLon = Double.parseDouble(prop.getProperty(MIN_LON, "0.0"));
		_maxLat = Double.parseDouble(prop.getProperty(MAX_LAT, "0.0"));
		_maxLon = Double.parseDouble(prop.getProperty(MAX_LON, "0.0"));
	}

	/**
	 * Getter for outputZoomLevel
	 * 
	 * @return the outputZoomLevel
	 */
	public final String getOutputZoomLevels()
	{
		return _outputZoomLevels;
	}

	/**
	 * Setter for outputZoomLevel
	 * 
	 * @param outputZoomLevel
	 *            the outputZoomLevel to set
	 */
	public final void setOutputZoomLevels(String outputZoomLevel)
	{
		_outputZoomLevels = outputZoomLevel;
	}

	/**
	 * Getter for tileServer
	 * 
	 * @return the tileServer
	 */
	public final String getTileServer()
	{
		return _tileServer;
	}

	/**
	 * Setter for tileServer
	 * 
	 * @param tileServer
	 *            the tileServer to set
	 */
	public final void setTileServer(String tileServer)
	{
		_tileServer = tileServer;
	}

	/**
	 * Getter for outputLocation
	 * 
	 * @return the outputLocation
	 */
	public final String getOutputLocation()
	{
		return _outputLocation;
	}

	/**
	 * Setter for outputLocation
	 * 
	 * @param outputLocation
	 *            the outputLocation to set
	 */
	public final void setOutputLocation(String outputLocation)
	{
		_outputLocation = outputLocation;
	}

	/**
	 * Getter for minLat
	 * 
	 * @return the minLat
	 */
	public final double getMinLat()
	{
		return _minLat;
	}

	/**
	 * Setter for minLat
	 * 
	 * @param minLat
	 *            the minLat to set
	 */
	public final void setMinLat(double minLat)
	{
		_minLat = minLat;
	}

	/**
	 * Getter for minLon
	 * 
	 * @return the minLon
	 */
	public final double getMinLon()
	{
		return _minLon;
	}

	/**
	 * Setter for minLon
	 * 
	 * @param minLon
	 *            the minLon to set
	 */
	public final void setMinLon(double minLon)
	{
		_minLon = minLon;
	}

	/**
	 * Getter for maxLat
	 * 
	 * @return the maxLat
	 */
	public final double getMaxLat()
	{
		return _maxLat;
	}

	/**
	 * Setter for maxLat
	 * 
	 * @param maxLat
	 *            the maxLat to set
	 */
	public final void setMaxLat(double maxLat)
	{
		_maxLat = maxLat;
	}

	/**
	 * Getter for maxLon
	 * 
	 * @return the maxLon
	 */
	public final double getMaxLon()
	{
		return _maxLon;
	}

	/**
	 * Setter for maxLon
	 * 
	 * @param maxLon
	 *            the maxLon to set
	 */
	public final void setMaxLon(double maxLon)
	{
		_maxLon = maxLon;
	}

	/**
	 * @see org.openstreetmap.fma.jtiledownloader.config.DownloadConfiguration#getType()
	 */
	public String getType()
	{
		return ID;
	}

	public static int[] getOutputZoomLevelArray(String zoomLevelString)
	{
		LinkedList<Integer> zoomLevels = new LinkedList<Integer>();
		for (String zoomLevel : Arrays.asList(zoomLevelString.split(",")))
		{
			int selectedZoom = Integer.parseInt(zoomLevel.trim());

			if (!zoomLevels.contains(selectedZoom))
			{
				zoomLevels.add(selectedZoom);
			}

		}
		int[] parsedLevels = new int[zoomLevels.size()];
		for (int i = 0; i < zoomLevels.size(); i++)
		{
			parsedLevels[i] = zoomLevels.get(i);
		}
		return parsedLevels;
	}

	/**
	 * @see org.openstreetmap.fma.jtiledownloader.config.DownloadConfiguration#getTileList(DownloadJob)
	 */
	public TileListCommonBBox getTileList()
	{
		TileListCommonBBox tileList = new TileListCommonBBox();

		tileList.setDownloadZoomLevels(getOutputZoomLevelArray(getOutputZoomLevels()));

		tileList.calculateTileValuesXY(getMinLat(), getMinLon(), getMaxLat(),
				getMaxLon());

		return tileList;
	}
}
