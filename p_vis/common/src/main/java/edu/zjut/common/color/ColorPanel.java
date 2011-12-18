package edu.zjut.common.color;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.gicentre.utils.colour.ColourTable;

public class ColorPanel extends JComponent {

	ColourTable cTable;

	public ColorPanel() {
		this.setOpaque(true);
		this.setPreferredSize(new Dimension(200, 30));

		cTable = ColourTable.getPresetColourTable(ColourTable.YL_OR_RD, 0, 1);
		// cTable = ColourTable.getPresetColourTable(ColourTable.SET1_6);
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		int w = this.getWidth();
		int h = this.getHeight();
		drawColorTable(g2, cTable, 5, 5, w - 10, h - 10);
	}

	private void drawColorTable(Graphics2D g2, ColourTable cTable, int x,
			int y, int w, int h) {
		int gap = 3;
		int textlen = 40;

		String name = cTable.getName();
		if (cTable.getIsDiscrete())
			name = name.split("_")[0];

		g2.setPaint(new Color(80));

		FontMetrics fm = g2.getFontMetrics();
		int textWidth = fm.stringWidth(name);
		int textHeight = fm.getAscent();
		g2.drawString(name, x + textlen / 2 - textWidth / 2, y + h / 2
				+ textHeight / 2);

		int bx = x + textlen, by = y, bw = w - textlen, bh = h;
		bx = bx;
		by = by + gap;
		bw = bw - gap;
		bh = bh - 2 * gap;

		Color black = new Color(0, 0, 0, 100);
		if (!cTable.getIsDiscrete()) {
			float inc = 0.001f;
			for (float i = 0; i < 1; i += inc) {
				int rx = (int) (bx + bw * i);
				int rw = (int) (bw * inc);
				g2.setPaint(new Color(cTable.findColour(i)));
				g2.drawRect(rx, by, rw, bh);
			}
			g2.setPaint(black);
			g2.drawRect(bx, by, bw, bh);
		} else {
			int numColours = cTable.getColourRules().size() - 1;
			int rw = (int) (1.0 * bw / numColours);
			int rx = bx;
			for (int i = 0; i < numColours; i++) {
				rx = bx + i * rw;
				g2.setPaint(new Color(cTable.findColour(i + 1)));
				g2.fillRect(rx, by, rw, bh);
				g2.setPaint(black);
				g2.drawRect(rx, by, rw, bh);
			}
		}

		g2.setPaint(black);
		g2.drawRect(x, y, w, h);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(new ColorPanel());
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
