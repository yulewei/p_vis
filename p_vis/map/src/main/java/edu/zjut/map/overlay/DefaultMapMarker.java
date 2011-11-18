package edu.zjut.map.overlay;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;

import com.vividsolutions.jts.geom.Point;

/**
 * 
 * @author yulewei
 * 
 */
public class DefaultMapMarker extends MapMarker {

	BufferedImage img = null;
	BufferedImage highlightImg = null;

	/**
	 * 锚点. 默认情况下，锚点设置为图像的底部中间位置 (坐标宽度/2, 高度)
	 */
	protected java.awt.Point anchor;

	public DefaultMapMarker(GeoPosition coord) {
		this(coord, "");
	}

	public DefaultMapMarker(Point coord, String title) {
		super(coord, title);
	}

	public DefaultMapMarker(GeoPosition coord, String title) {
		super(coord, title);

		try {
			img = ImageIO.read(getClass().getResource(
					"resources/marker_s.png"));
			highlightImg = ImageIO.read(getClass().getResource(
					"resources/marker_h.png"));

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		anchor = new java.awt.Point(img.getWidth() / 2, img.getHeight());
	}

	@Override
	public void paintOverlay(Graphics2D g, JXMapViewer map) {		
		java.awt.Point pt = GeoUtils.getScreenCoord(map, this.getPosition());
		g.drawImage(img, pt.x - anchor.x, pt.y - anchor.y, null);
	}

	@Override
	public boolean contains(JXMapViewer map, int x, int y) {
		java.awt.Point pt = GeoUtils.getScreenCoord(map, this.getPosition());

		return Math.abs(pt.x - x) < 10 && (pt.y - y) > 0 && (pt.y - y) < 34;
	}
}
