package edu.zjut.map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.DefaultTileFactory;
import org.jdesktop.swingx.mapviewer.Tile;
import org.jdesktop.swingx.mapviewer.TileCache;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;

import edu.zjut.common.event.IndicationEvent;
import edu.zjut.common.event.IndicationListener;

/**
 * 对JXMapViewer的bug hack!!
 * 
 * @author yulewei
 * 
 */
public class JXMapViewerX extends JXMapViewer {

	public JXMapViewerX() {
		super();

		this.setLayout(null);

		// 替换JXMapViewer鼠标滚动事件
		MouseWheelListener[] mouseWheelListeners = getMouseWheelListeners();
		for (MouseWheelListener l : mouseWheelListeners)
			removeMouseWheelListener(l);

		// zooms using the mouse wheel
		this.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (isZoomEnabled()) {
					setZoom(getZoom() - e.getWheelRotation());
					fireZoomLevelChanged();
				}
			}
		});

		// 重写
		try {
			this.setLoadingImage(ImageIO.read(JXMapViewer.class
					.getResource("mapviewer/resources/loading.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addZoomLevelListener(ZoomLevelListener l) {
		listenerList.add(ZoomLevelListener.class, l);
	}

	public void removeZoomLevelListener(ZoomLevelListener l) {
		listenerList.remove(ZoomLevelListener.class, l);
	}

	public void fireZoomLevelChanged() {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();

		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ZoomLevelListener.class) {
				// Lazily create the event:
				((ZoomLevelListener) listeners[i + 1]).zoomLevelChanged();
			}
		}// next i
	}

	/**
	 * Draw the map tiles. This method is for implementation use only.
	 * 
	 * @param g
	 *            Graphics
	 * @param zoom
	 *            zoom level to draw at
	 * @param viewportBounds
	 *            the bounds to draw within
	 */
	protected void drawMapTiles(final Graphics g, final int zoom,
			Rectangle viewportBounds) {
		int size = getTileFactory().getTileSize(zoom);

		// calculate the "visible" viewport area in tiles
		int numWide = viewportBounds.width / size + 2;
		int numHigh = viewportBounds.height / size + 2;

		// top left tile
		TileFactoryInfo info = getTileFactory().getInfo();
		int tpx = (int) Math.floor(viewportBounds.getX() / info.getTileSize(0));
		int tpy = (int) Math.floor(viewportBounds.getY() / info.getTileSize(0));

		for (int x = 0; x <= numWide; x++) {
			for (int y = 0; y <= numHigh; y++) {
				int itpx = x + tpx;
				int itpy = y + tpy;

				// only proceed if the specified tile point lies within the area
				// being painted
				if (g.getClipBounds().intersects(
						new Rectangle(itpx * size - viewportBounds.x, itpy
								* size - viewportBounds.y, size, size))) {
					Tile tile = getTileFactory().getTile(itpx, itpy, zoom);
					int ox = itpx * size - viewportBounds.x;
					int oy = itpy * size - viewportBounds.y;

					if (tile.isLoaded()) {
						g.drawImage(tile.getImage(), ox, oy, null);
					} else {
						BufferedImage image = loadPlaceholderFromCache(itpx,
								itpy, zoom);
						if (image != null) {
							g.drawImage(image, ox, oy, null);
						} else {
							int imageX = (size - getLoadingImage().getWidth(
									null)) / 2;
							int imageY = (size - getLoadingImage().getHeight(
									null)) / 2;
							g.setColor(Color.GRAY);
							g.fillRect(ox, oy, size, size);
							g.drawImage(getLoadingImage(), ox + imageX, oy
									+ imageY, null);
						}
					}
					if (isDrawTileBorders()) {
						g.setColor(Color.black);
						g.drawRect(ox, oy, size, size);
						g.drawRect(ox + size / 2 - 5, oy + size / 2 - 5, 10, 10);
						g.setColor(Color.white);
						g.drawRect(ox + 1, oy + 1, size, size);

						String text = itpx + ", " + itpy + ", " + getZoom();
						g.setColor(Color.BLACK);
						g.drawString(text, ox + 10, oy + 30);
						g.drawString(text, ox + 10 + 2, oy + 30 + 2);
						g.setColor(Color.WHITE);
						g.drawString(text, ox + 10 + 1, oy + 30 + 1);
					}
				}
			}
		}
	}

	/**
	 * <p>
	 * 添加瓦片预加载功能. 实现代码参考: OSM JMapViewer.
	 * http://wiki.openstreetmap.org/wiki/JMapViewer
	 * </p>
	 * <p>
	 * Tries to get tiles of a lower or higher zoom level (one or two level
	 * difference) from cache and use it as a placeholder until the tile has
	 * been loaded.
	 * </p>
	 */
	private BufferedImage loadPlaceholderFromCache(int xtile, int ytile,
			int zoom) {
		TileFactoryInfo info = getTileFactory().getInfo();
		int size = getTileFactory().getTileSize(zoom);

		BufferedImage tmpImage = new BufferedImage(size, size,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) tmpImage.getGraphics();
		// g.drawImage(image, 0, 0, null);
		for (int zoomDiff = 1; zoomDiff < 5; zoomDiff++) {
			// first we check if there are already the 2^x tiles
			// of a higher detail level
			int zoom_high = zoom + zoomDiff;
			if (zoomDiff < 3 && zoom_high <= info.getMaximumZoomLevel()) {
				int factor = 1 << zoomDiff;
				int xtile_high = xtile << zoomDiff;
				int ytile_high = ytile << zoomDiff;
				double scale = 1.0 / factor;
				g.setTransform(AffineTransform.getScaleInstance(scale, scale));
				int paintedTileCount = 0;
				for (int x = 0; x < factor; x++) {
					for (int y = 0; y < factor; y++) {
						BufferedImage img = getTileCache(xtile_high + x,
								ytile_high + y, zoom_high);
						if (img != null) {
							paintedTileCount++;
							g.drawImage(img, x * size, y * size, null);
						}
					}
				}
				if (paintedTileCount == factor * factor)
					return tmpImage;
			}

			int zoom_low = zoom - zoomDiff;
			if (zoom_low >= info.getMinimumZoomLevel()) {
				int xtile_low = xtile >> zoomDiff;
				int ytile_low = ytile >> zoomDiff;
				int factor = (1 << zoomDiff);
				double scale = factor;
				AffineTransform at = new AffineTransform();
				int translate_x = (xtile % factor) * size;
				int translate_y = (ytile % factor) * size;
				at.setTransform(scale, 0, 0, scale, -translate_x, -translate_y);
				g.setTransform(at);

				BufferedImage img = getTileCache(xtile_low, ytile_low, zoom_low);

				if (img != null) {
					g.drawImage(img, 0, 0, null);
					return tmpImage;
				}
			}
		}
		return null;
	}

	private BufferedImage getTileCache(int xtile, int ytile, int zoom) {
		DefaultTileFactory tf = (DefaultTileFactory) getTileFactory();
		TileFactoryInfo info = tf.getInfo();
		TileCache cache = tf.getTileCache();

		BufferedImage img = null;
		try {
			img = cache.get(new URI(info.getTileUrl(xtile, ytile, zoom)));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return img;
	}
}
