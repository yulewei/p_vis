package edu.zjut.common.ctrl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

public class FieldComponent extends JComponent {

	public static enum ColorEnum {
		WHITE, GREEN, BLUE;

		public Color[] getColor() {
			switch (this) {
			case WHITE:
				return new Color[] { Color.WHITE, Color.WHITE };
			case GREEN:
				Color green = new Color(140, 200, 175);
				Color darkGreen = new Color(135, 170, 135);
				return new Color[] { green, darkGreen };
			case BLUE:
				Color blue = new Color(175, 200, 230);
				Color darkBlue = new Color(135, 160, 180);
				return new Color[] { blue, darkBlue };
			}
			return new Color[] { Color.WHITE, Color.WHITE };
		}
	}

	ColorEnum color = ColorEnum.WHITE;	

	boolean isSelected = false;

	String text = "hello";

	public FieldComponent() {
		this("", ColorEnum.WHITE, false);
	}

	public FieldComponent(String text, ColorEnum color, boolean isSelected) {
		this.text = text;
		this.color = color;
		this.isSelected = isSelected;

		// this.setOpaque(false);
		this.setPreferredSize(new Dimension(100, 25));
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		int width = this.getWidth();
		int height = this.getHeight();

		int w;
		int h = 20;
		int gap = 2;
		w = width - 2 * gap;
		int x = gap, y = height / 2 - h / 2;

		int textGap = 20;
		if (width < 100) {
			int textwidth = g2.getFontMetrics().stringWidth(text);
			textGap = (int) (width * 0.2);
			textGap = (width - textwidth) / 2;
		}

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		Color[] colorArr = color.getColor();
		if (!isSelected)
			g2.setColor(colorArr[0]);
		else
			g2.setColor(colorArr[1]);

		g2.fillRoundRect(x, y, w, h, 20, 20);

		g2.setColor(Color.BLACK);
		g2.drawRoundRect(x, y, w, h, 20, 20);
		g2.drawString(text, textGap, y + 15);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public ColorEnum getColor() {
		return color;
	}

	public void setColor(ColorEnum color) {
		this.color = color;
	}
}
