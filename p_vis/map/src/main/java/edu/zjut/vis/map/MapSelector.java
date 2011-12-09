package edu.zjut.vis.map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

import edu.zjut.map.JMapPanel;

public class MapSelector extends JComponent implements MouseInputListener {

	private JMapPanel mapPanel;

	public enum SelectType {
		REST, ELLIPSE, LASSO
	}

	private SelectType selectType = SelectType.REST;

	private boolean isDrag = false;

	private Point2D start;
	private Point2D mouse;
	private List<Point2D> points;

	public MapSelector(JMapPanel mapPanel) {
		this.mapPanel = mapPanel;

		this.setBounds(mapPanel.getBounds());

		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		points = new ArrayList<Point2D>();
	}

	public void setSelectType(SelectType selectType) {
		this.selectType = selectType;
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

			int minX = (int) Math.min(mouse.getX(), start.getX());
			int minY = (int) Math.min(mouse.getY(), start.getY());
			int maxX = (int) Math.max(mouse.getX(), start.getX());
			int maxY = (int) Math.max(mouse.getY(), start.getY());

			switch (selectType) {
			case REST:
				g2.drawRect(minX, minY, maxX - minX, maxY - minY);
				break;
			case ELLIPSE:
				g2.drawOval(minX, minY, maxX - minX, maxY - minY);
				break;
			case LASSO:
				int len = points.size();
				for (int i = 1; i < len; i++) {
					g2.drawLine((int) points.get(i - 1).getX(), (int) points
							.get(i - 1).getY(), (int) points.get(i).getX(),
							(int) points.get(i).getY());
				}

				g2.setStroke(new BasicStroke(1.0f));
				g2.drawLine((int) points.get(len - 1).getX(),
						(int) points.get(len - 1).getY(), (int) points.get(0)
								.getX(), (int) points.get(0).getY());
				break;
			}
		}
	}

	private void selectChanged() {
		if (selectType == SelectType.LASSO && points.size() > 2) {
			mapPanel.selectMarkers(points, selectType);
			return;
		}

		if (selectType == SelectType.REST || selectType == SelectType.ELLIPSE) {
			double minX = Math.min(mouse.getX(), start.getX());
			double minY = Math.min(mouse.getY(), start.getY());
			double maxX = Math.max(mouse.getX(), start.getX());
			double maxY = Math.max(mouse.getY(), start.getY());

			Point2D min = new Point2D.Double(minX, minY);
			Point2D max = new Point2D.Double(maxX, maxY);

			List<Point2D> points = new ArrayList<>();
			points.add(min);
			points.add(max);

			mapPanel.selectMarkers(points, selectType);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		isDrag = true;

		start = new Point(e.getX(), e.getY());
		mouse = new Point(e.getX(), e.getY());

		if (selectType == SelectType.LASSO) {
			points.clear();
			points.add(start);
		}

		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		isDrag = false;

		if (selectType == SelectType.LASSO) {
			points.clear();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (isDrag) {
			mouse = new Point(e.getX(), e.getY());
			if (selectType == SelectType.LASSO) {
				points.add(mouse);
			}

			selectChanged();

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
