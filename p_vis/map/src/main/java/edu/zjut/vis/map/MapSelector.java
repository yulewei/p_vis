package edu.zjut.vis.map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.swing.event.MouseInputListener;

import edu.zjut.map.JMapPanel;

public class MapSelector extends JComponent implements MouseInputListener {

	private JMapPanel mapPanel;

	private boolean isDrag = false;
	private int startX;
	private int startY;
	private int mouseX;
	private int mouseY;

	public MapSelector(JMapPanel mapPanel) {
		this.mapPanel = mapPanel;
		this.setBounds(mapPanel.getBounds());

		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;

		if (isDrag) {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			g2.setColor(Color.RED);
			g2.setStroke(new BasicStroke(2.0f));

			int minX = Math.min(mouseX, startX);
			int minY = Math.min(mouseY, startY);

			int maxX = Math.max(mouseX, startX);
			int maxY = Math.max(mouseY, startY);

			g2.drawOval(minX, minY, maxX - minX, maxY - minY);
			g2.drawRect(minX, minY, maxX - minX, maxY - minY);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		isDrag = true;
		startX = e.getX();
		startY = e.getY();

		mouseX = e.getX();
		mouseY = e.getY();

		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		isDrag = false;

		int minX = Math.min(mouseX, startX);
		int minY = Math.min(mouseY, startY);

		int maxX = Math.max(mouseX, startX);
		int maxY = Math.max(mouseY, startY);

		int[] out = new int[] { minX, minY, maxX, maxY };
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (isDrag) {
			mouseX = e.getX();
			mouseY = e.getY();
			repaint();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
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

	@Override
	public void mouseMoved(MouseEvent e) {

	}
}
