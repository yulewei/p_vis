package edu.zjut.map.tile;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.jdesktop.swingx.mapviewer.TileCache;


// TODO: Auto-generated Javadoc
/*
 * ============================================================================
 * Created:  May 12, 2007 1:49:17 AM
 * ============================================================================
 */

/**
 * Install custom cache hashmap implementation for SwingX mapviewer build
 * 20070506.
 */
public final class CustomTileCache extends TileCache
{
	private TileFactoryInfoX info;

	/** The logger. */
	private static Logger logger = Logger.getLogger(CustomTileCache.class
			.getName());

	/** The Constant DEFAULT_MAX_CACHE_SIZE in bytes. */
	private static final int DEFAULT_MAX_CACHE_SIZE = 100 * 1024 * 1024;

	/** The Constant DEFAULT_MAX_AGE in days. */
	private static final int DEFAULT_MAX_AGE = 30;

	/** The instance. */
	private static CustomTileCache instance;

	// instance variables

	/** The cache dir. */
	private File cacheDir;

	/** The max cache size in bytes. */
	private long maxCacheSize = DEFAULT_MAX_CACHE_SIZE;

	/** The max age of cached tile in days. */
	private int maxAge = DEFAULT_MAX_AGE;

	/** The Constant WRITE_BUFFER_SIZE. */
	private static final int WRITE_BUFFER_SIZE = 4096;

	/**
	 * Private constructor, create instance through createInstance() method.
	 * 
	 * @param pCacheDir
	 *            the cache dir
	 */
	private CustomTileCache(final File pCacheDir, TileFactoryInfoX info)
	{
		this.cacheDir = pCacheDir;
		this.info = info;
	}

	/**
	 * (non-Javadoc).
	 * 
	 * @param pURI
	 *            the uRI
	 * @param pImgBytes
	 *            the img bytes
	 * @param pImage
	 *            the image
	 * 
	 * @see org.jdesktop.swingx.mapviewer.TileCache#put(java.net.URI, byte[],
	 *      java.awt.image.BufferedImage)
	 */
	@Override
	public void put(final URI pURI, final byte[] pImgBytes,
			final BufferedImage pImage)
	{
		super.put(pURI, pImgBytes, pImage);

		synchronized (this)
		{
			putImageData(pURI, pImgBytes);
		}
	}

	/**
	 * (non-Javadoc).
	 * 
	 * @param pURI
	 *            the uRI
	 * 
	 * @return the buffered image
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * 
	 * @see org.jdesktop.swingx.mapviewer.TileCache#get(java.net.URI)
	 */
	@Override
	public BufferedImage get(final URI pURI) throws IOException
	{
		BufferedImage lImage = super.get(pURI);
		synchronized (this)
		{
			if (lImage == null && containsImageURI(pURI))
			{
				byte[] lImageBytes = getImageData(pURI);
				lImage = ImageIO.read(new ByteArrayInputStream(lImageBytes));
				super.put(pURI, lImageBytes, lImage);
				return lImage;
			}
		}

		// Not found
		return lImage;
	}

	/**
	 * Test if file exists based on given URI.
	 * 
	 * @param pFullImageURI
	 *            the full image uri
	 * 
	 * @return true, if contains image uri
	 */
	private boolean containsImageURI(final URI pFullImageURI)
	{
		File lFile = new File(cacheDir, getFileName(pFullImageURI));
		return lFile.isFile() && !isTooOld(lFile, instance.getMaxAge());
	}

	private String getFileName(final URI pFullImageURI)
	{
		int[] zxy = info.getTileZoomXY(pFullImageURI);
		String name = zxy[0] + "_" + zxy[1] + "_" + zxy[2] + info.getTileType();
		return name;
	}

	/**
	 * Gets the image data.
	 * 
	 * @param pFullImageURI
	 *            the full image uri
	 * 
	 * @return the image data
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public byte[] getImageData(final URI pFullImageURI) throws IOException
	{
		// return byte array with compressed image contents
		if (containsImageURI(pFullImageURI))
		{
			File lFile = new File(cacheDir, getFileName(pFullImageURI));
//			logger.info("Load cached file: " + lFile);
			return copyFile(lFile);
		}

		// Not found
		return null;
	}

	/**
	 * Put image data.
	 * 
	 * @param pFullImageUrlName
	 *            the full image url name
	 * @param pData
	 *            the data
	 */
	public void putImageData(final URI pFullImageURI, final byte[] pData)
	{

		if (logger.isLoggable(Level.INFO))
		{
			logger.info("Caching: " + pFullImageURI + ", size: " + pData.length);
		}

		File lFile = new File(cacheDir, getFileName(pFullImageURI));
		try
		{
			copyFile(pData, lFile);
		}
		catch (IOException e)
		{
			logger.log(Level.WARNING, "Error saving file " + lFile, e);
		}
	}

	// ===================================
	// Getters and setters
	// ===================================

	/**
	 * Gets the cache dir.
	 * 
	 * @return the cache dir
	 */
	public File getCacheDir()
	{
		return cacheDir;
	}

	/**
	 * Sets the cache dir.
	 * 
	 * @param pCacheDir
	 *            the new cache dir
	 */
	public void setCacheDir(final File pCacheDir)
	{
		cacheDir = pCacheDir;
	}

	/**
	 * Gets the max cache size.
	 * 
	 * @return the max cache size
	 */
	public long getMaxCacheSize()
	{
		return maxCacheSize;
	}

	/**
	 * Sets the max cache size.
	 * 
	 * @param pMaxCacheSize
	 *            the new max cache size
	 */
	public void setMaxCacheSize(final long pMaxCacheSize)
	{
		maxCacheSize = pMaxCacheSize;
	}

	/**
	 * Gets the max age.
	 * 
	 * @return the max age
	 */
	public int getMaxAge()
	{
		return maxAge;
	}

	/**
	 * Sets the max age.
	 * 
	 * @param pMaxAgeInDays
	 *            the new max age
	 */
	public void setMaxAge(final int pMaxAgeInDays)
	{
		maxAge = pMaxAgeInDays;
	}

	// ===================================
	// Utility methods
	// ===================================

	/**
	 * Install custom cache.
	 * 
	 * @param pCacheDir
	 *            the cache dir
	 * 
	 * @return the instance
	 */
	public static synchronized CustomTileCache getInstance(
			final File pCacheDir, TileFactoryInfoX info)
	{

		// Only one instance
		if (instance != null)
		{
			return instance;
		}

		if (!pCacheDir.isDirectory())
		{
			pCacheDir.mkdirs();
		}

		instance = new CustomTileCache(pCacheDir, info);
		return instance;
	}

	/**
	 * Clean cache, remove everything older then maxAge The new max size of
	 * cache will be the value of maxCacheSize.
	 * 
	 * @param pFullClean
	 *            When true, erase all files in cache and remove cache dir
	 */
	public static void cleanCache(final boolean pFullClean)
	{

		if (instance == null)
		{
			return;
		}

		File lCacheDirectory = instance.getCacheDir();
		if (!lCacheDirectory.exists())
		{
			logger.info("Cache dir does not exist, nothing to clean");
			return;
		}

		StringBuilder lResult = new StringBuilder();
		for (File lFile : lCacheDirectory.listFiles())
		{

			if (pFullClean || isTooOld(lFile, instance.getMaxAge()))
			{
				lFile.delete();
				if (logger.isLoggable(Level.INFO))
				{
					lResult.append("  Deleted: ").append(lFile).append("\n");
				}
			}
		}

		if (pFullClean)
		{
			lCacheDirectory.delete();
		}

		if (logger.isLoggable(Level.INFO) && lResult.length() > 0)
		{
			logger.info("Cache files removed: \n" + lResult);
		}
	}

	/**
	 * Copy byte array to given file.
	 * 
	 * @param inFile
	 *            the in file
	 * @param outFile
	 *            the out file
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void copyFile(final byte[] inFile, final File outFile)
			throws IOException
	{

		FileOutputStream lStream = null;
		try
		{
			lStream = new FileOutputStream(outFile);
			BufferedOutputStream os = new BufferedOutputStream(lStream);
			os.write(inFile, 0, inFile.length);
			os.close();
		}
		finally
		{
			if (lStream != null)
			{
				try
				{
					lStream.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
					// NOP
				}
			}
		}
	}

	/**
	 * copy file to byte array.
	 * 
	 * @param pInFile
	 *            the in file
	 * 
	 * @return the file as a byte array
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static byte[] copyFile(final File pInFile) throws IOException
	{

		FileInputStream lStream = null;
		try
		{
			lStream = new FileInputStream(pInFile);
			ByteArrayOutputStream lOutFile = new ByteArrayOutputStream();
			BufferedInputStream is = new BufferedInputStream(lStream);
			BufferedOutputStream os = new BufferedOutputStream(lOutFile);

			// Read and write until done
			byte[] buf = new byte[WRITE_BUFFER_SIZE];
			int len;

			// Buffercopy
			while ((len = is.read(buf)) >= 0)
			{
				os.write(buf, 0, len);
			}

			is.close();
			os.close();
			return lOutFile.toByteArray();

		}
		finally
		{
			if (lStream != null)
			{
				try
				{
					lStream.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
					// NOP
				}
			}
		}
	}

	/**
	 * Test if given file is older then maxAge.
	 * 
	 * @param pFile
	 *            the file
	 * @param pMaxAgeInDays
	 *            the max age in days
	 * 
	 * @return Return true if older
	 */
	public static boolean isTooOld(final File pFile, final int pMaxAgeInDays)
	{

		long lNow = System.currentTimeMillis();
		final long dayInMillis = 1000L * 60 * 60 * 24;
		long lHistory = lNow - dayInMillis * pMaxAgeInDays;
		return (pFile.lastModified() < lHistory);
	}
}
