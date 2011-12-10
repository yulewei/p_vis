package edu.zjut.map.cluster;

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

import edu.zjut.map.GeoUtils;
import edu.zjut.map.overlay.MapMarker;

public class ClusterIcon extends MapMarker {

	/**
	 * 根据聚集大小, 显示5种不同颜色的图标
	 * <ul>
	 * <li>Blue < 10</li>
	 * <li>Yellow < 100</li>
	 * <li>Red < 1000</li>
	 * <li>Purple < 10000</li>
	 * <li>Dark purple < 10000+</li>
	 * </ul>
	 */
	private static BufferedImage[] images;

	static {
		try {
			images = new BufferedImage[5];
			for (int i = 0; i < 5; i++) {
				images[i] = ImageIO.read(ClusterIcon.class.getResource(String
						.format("m%d.png", i + 1)));
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected int sum;

	private int textWidth = -1;

	public ClusterIcon(GeoPosition coord, int sum) {
		super(coord, "");
		this.sum = sum;
	}

	public ClusterIcon(Point point, int sum) {
		super(point, "");
		this.sum = sum;
	}

	@Override
	public void paintOverlay(Graphics2D g, JXMapViewer map) {
		if (isHighlighted)
			return;

		// 按10进制位数计算
		int index = 0;
		for (int k = sum; k / 10 != 0; k /= 10, index++)
			;

		index = index > 5 ? 5 : index;
		BufferedImage img = images[index];
		Point2D pt = GeoUtils.getScreenCoord(map, point);
		g.drawImage(img, (int) pt.getX() - img.getWidth() / 2, (int) pt.getY()
				- img.getHeight() / 2, null);

		if (textWidth == -1) {
			Font f = g.getFont();
			FontRenderContext frc = g.getFontRenderContext();
			Rectangle2D rec = f.getStringBounds(String.valueOf(sum), frc);
			textWidth = (int) rec.getWidth();
		}

		g.setPaint(Color.BLACK);
		g.drawString(String.valueOf(sum), (int) pt.getX() - textWidth / 2,
				(int) pt.getY() + 5);
	}

	@Override
	public void paintHighlightOverlay(Graphics2D g, JXMapViewer map) {
		if (!isHighlighted)
			return;

		BufferedImage img = images[(int) Math.log10(sum)];
		Point2D pt = GeoUtils.getScreenCoord(map, point);
		g.drawImage(img, (int) pt.getX() - img.getWidth() / 2, (int) pt.getY()
				- img.getHeight() / 2, null);

		g.setPaint(Color.BLACK);
		g.drawString(String.valueOf(sum), (int) pt.getX() - textWidth / 2,
				(int) pt.getY() + 5);
	}

	@Override
	public boolean contains(JXMapViewer map, int x, int y) {
		Point2D pt = GeoUtils.getScreenCoord(map, point);

		return Math.abs(pt.getX() - x) < 10 && (pt.getY() - y) > 0
				&& (pt.getY() - y) < 34;
	}
}
