package edu.zjut.map.overlay;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;

import com.vividsolutions.jts.geom.Point;

public class ClusterMarker extends MapMarker {

	private static BufferedImage img = null;

	static {
		try {
			img = ImageIO.read(DefaultMapMarker.class
					.getResource("resources/m1.png"));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected int[] anchor;
	private int textWidth = -1;

	public ClusterMarker(GeoPosition coord, String title) {
		super(coord, title);
		anchor = new int[] { img.getWidth() / 2, img.getHeight() / 2 };
	}

	public ClusterMarker(Point point, String title) {
		super(point, title);
		anchor = new int[] { img.getWidth() / 2, img.getHeight() / 2 };
	}

	@Override
	public void paintOverlay(Graphics2D g, JXMapViewer map) {
		if (isHighlighted)
			return;

		Point2D pt = GeoUtils.getScreenCoord(map, point);
		g.drawImage(img, (int) pt.getX() - anchor[0], (int) pt.getY()
				- anchor[1], null);

		if (textWidth == -1) {
			Font f = g.getFont();
			FontRenderContext frc = g.getFontRenderContext();
			Rectangle2D rec = f.getStringBounds(title, frc);
			textWidth = (int) rec.getWidth();
		}

		g.setPaint(Color.BLACK);
		g.drawString(title, (int) pt.getX() - textWidth / 2,
				(int) pt.getY() + 5);
	}

	@Override
	public void paintHighlightOverlay(Graphics2D g, JXMapViewer map) {
		if (!isHighlighted)
			return;

		Point2D pt = GeoUtils.getScreenCoord(map, point);
		g.drawImage(img, (int) pt.getX() - anchor[0], (int) pt.getY()
				- anchor[1], null);

		g.setPaint(Color.BLACK);
		g.drawString(title, (int) pt.getX() - textWidth / 2,
				(int) pt.getY() + 5);
	}

	@Override
	public boolean contains(JXMapViewer map, int x, int y) {
		Point2D pt = GeoUtils.getScreenCoord(map, point);

		return Math.abs(pt.getX() - x) < 10 && (pt.getY() - y) > 0
				&& (pt.getY() - y) < 34;
	}
}
