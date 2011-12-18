package edu.zjut.common.color;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JComponent;

import org.gicentre.utils.colour.ColourTable;

import processing.core.PApplet;

public class Legend extends JComponent implements MouseListener {

	ColourTable cTable;

	float min, max;
	float curValue;
	float[] values;
	String[] cat;

	boolean isActive = false;
	int index = 1;

	private Vector<LegendActionListener> legendListeners;

	public Legend() {
		this.setPreferredSize(new Dimension(200, 50));

		this.addMouseListener(this);
		legendListeners = new Vector<LegendActionListener>();
	}

	public void setData(float[] values, ColourTable cTable) {
		this.values = values;
		this.cTable = cTable;

		min = values[0];
		max = values[0];
		for (int i = 0; i < values.length; i++) {
			if (values[i] < min)
				min = values[i];
			if (values[i] > max)
				max = values[i];
		}
	}

	public void setData(String[] cat, ColourTable cTable) {
		this.cat = cat;
		this.cTable = cTable;
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		int x = 5;
		int y = 5;
		int w = this.getWidth() - 10;
		int h = this.getHeight() - 10;

		Font font = g2.getFont();
		Font font12 = font.deriveFont(11.f);
		FontMetrics fm = g2.getFontMetrics();

		Color black = new Color(0, 0, 0, 100);
		g2.setPaint(black);
		g2.setFont(font12);

		int gap = 3;
		int th = 15, ty = y + h;
		int ah = 5, ay = y + h - th - ah + 2;

		int bx = x, by = y, bw = w, bh = h - th - ah;

		// rect(bx, by, bw, bh);
		bx = bx + gap;
		by = by + gap;
		bw = bw - 2 * gap;
		bh = bh - gap;

		g2.drawRect(x, y, w, h);
		// rect(x, ty - th, w, th);

		if (cTable == null)
			return;

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

			String minStr = String.format("%.2f", min);
			String maxStr = String.format("%.2f", max);
			int tw = fm.stringWidth(maxStr);

			g2.drawString(minStr, x + 3, ty - 3);
			g2.drawString(maxStr, x + 3 + w - tw, ty - 3);

			int ax = (int) PApplet.map(values[index], min, max, bx, bx + bw);
			if (index != -1) {
				g2.setStroke(new BasicStroke(1.5f));
				g2.drawLine(ax, ay, ax + ah - 1, ay + ah - 1);
				g2.drawLine(ax, ay, ax - ah + 1, ay + ah - 1);
				g2.setStroke(new BasicStroke(1.0f));
			}

		} else {

			int colorGap = 3;
			// p.textAlign(PConstants.CENTER, PConstants.BOTTOM);
			int numColours = cat.length;
			for (int i = 0; i < numColours; i++) {
				g2.setPaint(new Color(cTable.findColour(i + 1)));

				int rx = (int) (bx + 1.0 * bw * i / numColours);
				int rw = (int) (1.0 * bw / numColours);
				g2.fillRect(rx + colorGap, by, rw - 2 * colorGap, bh);
				g2.setPaint(black);
				g2.drawRect(rx + colorGap, by, rw - 2 * colorGap, bh);

				int rc = (int) (rx + rw / 2);
				if (index == i) {
					g2.setStroke(new BasicStroke(1.5f));
					g2.drawLine(rc, ay, rc + ah - 1, ay + ah - 1);
					g2.drawLine(rc, ay, rc - ah + 1, ay + ah - 1);
					g2.setStroke(new BasicStroke(1.0f));
				}

				int tw = fm.stringWidth(cat[i]);
				g2.setPaint(Color.black);
				g2.drawString(cat[i], rc - tw / 2, ty - 3);
			}
		}

		if (isActive) {
			g2.setStroke(new BasicStroke(3.0f));
			g2.setPaint(new Color(255, 255, 0, 200));
			g2.drawRect(x, y, w, h);
			g2.setStroke(new BasicStroke(1.0f));
		}
	}

	public ColourTable getColorTable() {
		return cTable;
	}

	public void setColorTable(ColourTable cTable) {
		this.cTable = cTable;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public float getCurValue() {
		return curValue;
	}

	public void setCurValue(float curValue) {
		this.curValue = curValue;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// Êó±êË«»÷, ¼¤»îcolourPicker
		if (e.getClickCount() == 2) {
			isActive = true;
			fireLegendActionEvent(isActive);
		}

		// Êó±êÓÒ¼ü, reset
		if (e.getButton() == MouseEvent.BUTTON3) {
			isActive = false;
			fireLegendActionEvent(isActive);
		}

		repaint();
	}

	public void addLegendActionListener(LegendActionListener legendListener) {
		legendListeners.add(legendListener);
	}

	public boolean removeLegendActionListener(
			LegendActionListener legendListener) {
		return legendListeners.remove(legendListener);
	}

	private void fireLegendActionEvent(boolean isActive) {
		for (LegendActionListener listener : legendListeners) {
			listener.actionPerformed(isActive);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}
}
