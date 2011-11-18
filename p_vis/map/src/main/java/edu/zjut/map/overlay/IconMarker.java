package edu.zjut.map.overlay;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;

/**
 * 图标参考自搜房网
 * 
 * @author yulewei
 * 
 */
public class IconMarker extends DefaultMapMarker {
	public static final int PURPLE = 0; // z, 紫色
	public static final int PINK = 1; // f, 粉色
	public static final int BLUE = 2;// l, 蓝色
	public static final int GREEN = 3; // n, 绿色
	public static final int YELLOW = 4; // h, 黄色
	public static final int BROWN = 5; // y, 棕色
	private String[] colors = { "z", "f", "l", "n", "h", "y" };

	private int iconColor = BLUE;

	private BufferedImage[] left;
	private BufferedImage[] middle;
	private BufferedImage[] right;
	private BufferedImage[] jt;

	public IconMarker(GeoPosition coord, String title) {
		super(coord, title);

		loadAllImages(colors);
	}

	private void loadAllImages(String[] colors) {
		try {
			left = new BufferedImage[colors.length];
			middle = new BufferedImage[colors.length];
			right = new BufferedImage[colors.length];
			jt = new BufferedImage[colors.length];

			for (int i = 0; i < colors.length; i++) {
				left[i] = ImageIO.read(getClass().getResource(
						String.format("resources/%s_left.png", colors[i])));
				middle[i] = ImageIO.read(getClass().getResource(
						String.format("resources/%s_middle.png", colors[i])));
				right[i] = ImageIO.read(getClass().getResource(
						String.format("resources/%s_right.png", colors[i])));
				jt[i] = ImageIO.read(getClass().getResource(
						String.format("resources/%s_jt.png", colors[i])));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void paintOverlay(Graphics2D g, JXMapViewer map) {
		Point pt = GeoUtils.getScreenCoord(map, this.getPosition());

		g.translate(pt.x, pt.y);

		// drawIcon(BLUE, 28);
		Font f = g.getFont();
		FontRenderContext frc = g.getFontRenderContext();
		Rectangle2D rec = f.getStringBounds(title, frc);

		int gap = 8;
		int textWidth = (int) rec.getWidth() + gap * 2;

		int h = left[iconColor].getHeight() + jt[iconColor].getHeight() - 5;
		int w = left[iconColor].getWidth();

		g.drawImage(left[iconColor], 0, -h, null);
		g.drawImage(right[iconColor], w + textWidth, -h, null);

		int iw = middle[iconColor].getWidth();
		int n = textWidth / iw + 1;
		for (int i = 0; i < n; i++)
			g.drawImage(middle[iconColor], w + iw * i, -h, null);

		g.drawImage(jt[iconColor], 1, -jt[iconColor].getHeight(), null);

		g.setPaint(Color.WHITE);

		g.drawString(title, w + gap, -jt[iconColor].getHeight() - 5);
	}

}
