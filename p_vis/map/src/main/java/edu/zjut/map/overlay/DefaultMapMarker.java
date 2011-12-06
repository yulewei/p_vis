package edu.zjut.map.overlay;

import java.awt.BasicStroke;
import java.awt.Color;
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

	private static BufferedImage img = null;
	private static BufferedImage highlightImg = null;

	static {
		try {
			img = ImageIO.read(DefaultMapMarker.class
					.getResource("resources/marker_h.png"));
			highlightImg = ImageIO.read(DefaultMapMarker.class
					.getResource("resources/marker_s.png"));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 锚点. 默认情况下，锚点设置为图像的底部中间位置 (坐标宽度/2, 高度)
	 */
	protected int[] anchor;

	public DefaultMapMarker(GeoPosition coord) {
		this(coord, "");
	}

	public DefaultMapMarker(Point coord, String title) {
		super(coord, title);
		anchor = new int[] { img.getWidth() / 2, img.getHeight() };
	}

	public DefaultMapMarker(GeoPosition coord, String title) {
		super(coord, title);
		anchor = new int[] { img.getWidth() / 2, img.getHeight() };
	}

	@Override
	public void paintOverlay(Graphics2D g, JXMapViewer map) {
		if (isHighlighted)
			return;

		java.awt.Point pt = GeoUtils.getScreenCoord(map, this.getPosition());
		g.drawImage(img, pt.x - anchor[0], pt.y - anchor[1], null);
	}

	@Override
	public void paintHighlightOverlay(Graphics2D g, JXMapViewer map) {
		if (!isHighlighted)
			return;

		java.awt.Point pt = GeoUtils.getScreenCoord(map, this.getPosition());
		g.drawImage(highlightImg, pt.x - anchor[0], pt.y - anchor[1], null);		
		
		int r = 5;
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(2.0f));
		g.translate(pt.x, pt.y);
		g.drawLine(-r, -r, +r, +r);
		g.drawLine(-r, +r, +r, -r);
	}

	@Override
	public boolean contains(JXMapViewer map, int x, int y) {
		java.awt.Point pt = GeoUtils.getScreenCoord(map, this.getPosition());

		return Math.abs(pt.x - x) < 10 && (pt.y - y) > 0 && (pt.y - y) < 34;
	}
}
