package edu.zjut.map.downloader;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Vector;

import org.jdesktop.swingx.mapviewer.Tile;

import edu.zjut.map.config.TileServer;
import edu.zjut.map.tile.TileFactoryInfoX;

public class TileDownloader {
	public static byte[] cacheInputStream(URL url) throws IOException {
		InputStream ins = url.openStream();
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[] buf = new byte[256];
		while (true) {
			int n = ins.read(buf);
			if (n == -1)
				break;
			bout.write(buf, 0, n);
		}
		return bout.toByteArray();
	}

	public static void doDownload(String downloadPath,
			Vector<Tile> tilesToDownload, TileFactoryInfoX info) {
		File testDir = new File(downloadPath);
		if (!testDir.exists()) {
			testDir.mkdirs();
		}

		for (int i = 0; i < tilesToDownload.size(); i++) {
			Tile tile = tilesToDownload.get(i);

			String fileName = String.format("%s/%d_%d_%d.png", downloadPath,
					tile.getZoom(), tile.getX(), tile.getY());
			File outFile = new File(fileName);

			if (outFile.exists())
				continue;

			String url = info.getTileUrl(tile.getX(), tile.getY(),
					tile.getZoom());

			// System.out.println(url);
			System.out.format("%d / %d\n", i, tilesToDownload.size());

			try {
				byte[] bimg = cacheInputStream(new URL(url));

				BufferedOutputStream os = new BufferedOutputStream(
						new FileOutputStream(outFile));
				os.write(bimg, 0, bimg.length);
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		DownloadConfig config = new DownloadConfig("osm_hz.xml");
		TileListCommonBBox tileList = config.getTileList();

		System.out.println(tileList.getTileCount());

		TileFactoryInfoX info = new TileFactoryInfoX(
				TileServer.DEFAULT_SERVER_NAME,
				TileServer.DEFAULT_TEMPLATE_URL, 10, 18);

		doDownload(config.getOutputLocation(),
				tileList.getTileListToDownload(), info);
	}
}
