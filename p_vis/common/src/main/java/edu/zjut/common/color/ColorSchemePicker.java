package edu.zjut.common.color;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.gicentre.utils.colour.ColourTable;

/**
 * ColorBrewer
 * 
 * @author yulewei
 * 
 */
public class ColorSchemePicker extends JDialog {

	int mouseX = -1;
	int mouseY = -1;

	private Vector<ColorSchemeListener> pickerListeners;

	ColorSchemeType type;
	String title;
	ColourTable[] colors;

	ColourTable activeColorTable;
	ColourTable refColorTable;

	boolean isDiscrete = false;
	private int conUnits = 9;

	private int catIndex = 2;

	int barw = 200, barh = 20;
	int cx, cy;

	public ColorSchemePicker() {

		this.setVisible(false);
		this.setSize(500, 600);

		JPanel plotPanel = new JPanel();
		plotPanel.setLayout(new BorderLayout());
		plotPanel.setBackground(Color.white);
		this.add(plotPanel);

		plotPanel.add(new ColorSchemePanel());

		pickerListeners = new Vector<ColorSchemeListener>();

		type = ColorSchemeType.Div;

		initColorType(type);
	}

	public void setRefColorTable(ColourTable cTable) {
		refColorTable = cTable;
		activeColorTable = refColorTable;
		type = ColorUtils.getColorSchemeType(refColorTable);

		initColorType(type);
	}

	public void initColorType(ColorSchemeType type) {
		switch (type) {
		case Cont:
			title = "Sequential";
			colors = ColorUtils.getColoursCont();
			this.setMinimumSize(new Dimension(230, 420));
			this.setSize(230, 420);
			break;
		case Div:
			title = "Diverging";
			colors = ColorUtils.getColoursDiv();
			this.setMinimumSize(new Dimension(230, 250));
			this.setSize(230, 250);
		case Cat:
			title = "Categorical";
			if (refColorTable != null)
				catIndex = refColorTable.getColourRules().size() - 4;
			colors = ColorUtils.getColoursCatAll()[catIndex];
			this.setMinimumSize(new Dimension(230, 250));
			this.setSize(230, 250);
			break;
		}

		this.setTitle(title);
	}

	public void addPickerListener(ColorSchemeListener pickerListener) {
		pickerListeners.add(pickerListener);
	}

	public boolean removePickerListener(ColorSchemeListener pickerListener) {
		return pickerListeners.remove(pickerListener);
	}

	private void fireColorSelectionEvent(ColourTable cTable) {
		for (ColorSchemeListener listener : pickerListeners) {
			listener.colorChosen(cTable);
		}
	}

	public ColourTable getActiveColorTable() {
		return activeColorTable;
	}

	class ColorSchemePanel extends JComponent implements MouseListener,
			MouseMotionListener, KeyListener {

		public ColorSchemePanel() {
			this.addMouseListener(this);
			this.addMouseMotionListener(this);
			this.addKeyListener(this);

			// this.setVisible(false);
			this.setFocusable(true);
		}

		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			Font font = g2.getFont();
			Font samllFont = font.deriveFont(10.f);
			g2.setFont(samllFont);

			int w = barw + 10, h = barh * colors.length + 10;
			int x = (this.getWidth() - w) / 2, y = (this.getHeight() - h) / 2;
			int barx = x + 5, bary = y + 5;

			// ÑÕÉ«
			drawColours(g2, colors, barx, bary, barw, barh);
		}

		private void drawColours(Graphics2D g2, ColourTable[] colours, int x,
				int y, int w, int h) {

			for (int i = 0; i < colours.length; i++) {
				int yi = y + h * i;
				drawColorTable(g2, colours[i], x, yi, w, h);
			}

			g2.setStroke(new BasicStroke(3.0f));
			activeColorTable = null;
			for (int i = 0; i < colours.length; i++) {
				int yi = y + h * i;
				if (refColorTable != null
						&& colours[i].getName().equals(refColorTable.getName())) {
					g2.setPaint(new Color(255, 0, 0, 200));
					g2.drawRect(x, yi, w, h);
				}

				if (mouseX > x && mouseX < x + w && mouseY > yi
						&& mouseY < yi + h) {
					activeColorTable = colours[i];
					g2.setPaint(new Color(255, 255, 0, 200));
					g2.drawRect(x, yi, w, h);
				}
			}

			g2.setStroke(new BasicStroke(1.0f));
		}

		private void drawColorTable(Graphics2D g2, ColourTable cTable, int x,
				int y, int w, int h) {
			int gap = 3;
			int textlen = 40;

			String name = cTable.getName();
			if (cTable.getIsDiscrete())
				name = name.split("_")[0];

			int textWidth = g2.getFontMetrics().stringWidth(name);
			int textHeight = g2.getFontMetrics().getAscent();

			g2.setPaint(new Color(100, 100, 100));
			g2.drawString(name, x + textlen / 2 - textWidth / 2, y + h / 2
					+ textHeight / 2);

			int bx = x + textlen, by = y, bw = w - textlen, bh = h;
			bx = bx;
			by = by + gap;
			bw = bw - gap;
			bh = bh - 2 * gap;

			Color black = new Color(0, 0, 0, 100);
			if (!cTable.getIsDiscrete()) {
				int count = 1000;
				float offset = 0.0f;
				float inc = 1.0f / count;
				if (isDiscrete) {
					count = conUnits;
					inc = 1.0f / count;
					offset = 0.5f * inc;
				}

				if (isDiscrete) {
					int rw = (int) (bw * inc);
					bx = (int) (bx + (bw - rw * count) / 2.0 + 0.5);
					bw = rw * count;
					int rx = bx;
					for (int i = 0; i < count; i++) {
						float index = i * inc + offset;
						rx = bx + i * rw;
						g2.setColor(new Color(cTable.findColour(index)));
						g2.fillRect(rx, by, rw, bh);
						g2.setPaint(black);
						g2.drawRect(rx, by, rw, bh);
					}
				} else {
					for (int i = 0; i < count; i++) {
						float index = i * inc + offset;
						int rx = (int) (bx + bw * i * inc);
						int rw = (int) (bw * inc);
						g2.setColor(new Color(cTable.findColour(index)));
						g2.drawRect(rx, by, rw, bh);
					}
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

		@Override
		public void mouseDragged(MouseEvent arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();

			repaint();
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (activeColorTable != null)
				fireColorSelectionEvent(activeColorTable);
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
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void keyPressed(KeyEvent e) {
			char key = e.getKeyChar();
			int keyCode = e.getKeyCode();

			if (key == 'd')
				isDiscrete = !isDiscrete;

			int min = 0, max = 9;

			if (keyCode == KeyEvent.VK_LEFT) {
				catIndex--;
				if (catIndex < min)
					catIndex = min;
			}
			if (keyCode == KeyEvent.VK_RIGHT) {
				catIndex++;
				if (catIndex > max)
					catIndex = max;
			}

			if (keyCode == KeyEvent.VK_DOWN)
				catIndex = min;

			if (keyCode == KeyEvent.VK_UP)
				catIndex = max;

			// units
			int minUnits = 5, maxUnits = 12;
			if (keyCode == KeyEvent.VK_LEFT) {
				conUnits--;
				if (conUnits < minUnits)
					conUnits = minUnits;
			}
			if (keyCode == KeyEvent.VK_RIGHT) {
				conUnits++;
				if (conUnits > maxUnits)
					conUnits = maxUnits;
			}

			if (keyCode == KeyEvent.VK_DOWN)
				conUnits = minUnits;

			if (keyCode == KeyEvent.VK_UP)
				conUnits = maxUnits;

			repaint();
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub
		}
	}

}
