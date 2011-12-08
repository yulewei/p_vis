package edu.zjut.common.ctrl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import edu.zjut.common.data.attr.DimensionField;
import edu.zjut.common.data.attr.MeasureField;

public class FieldComponent<E> extends JComponent implements
		ListCellRenderer<E> {

	public static enum ColorEnum {
		WHITE, GREEN, BLUE, YELLOW;

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
			case YELLOW:
				Color yellow = new Color(230, 200, 50);
				Color darkYellow = new Color(200, 170, 30);
				return new Color[] { yellow, darkYellow };
			}
			return new Color[] { Color.WHITE, Color.WHITE };
		}
	}

	ColorEnum color;
	boolean isSelected = false;
	E value;

	public FieldComponent() {
		this(null, null, false);
	}

	public FieldComponent(ColorEnum color) {
		this(null, color, false);
	}

	public FieldComponent(E value, ColorEnum color, boolean isSelected) {
		this.value = value;
		this.color = color;
		this.isSelected = isSelected;

		// this.setOpaque(false);
		this.setPreferredSize(new Dimension(100, 25));
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		String text = "";
		if (value != null)
			text = value.toString();

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

		if (color == null)
			color = ColorEnum.WHITE;

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

	public void setValue(E value) {
		this.value = value;
	}

	public void setColor(ColorEnum color) {
		this.color = color;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends E> list,
			E value, int index, boolean isSelected, boolean cellHasFocus) {
		if (color == null) {
			color = ColorEnum.GREEN;
			if (value instanceof DimensionField)
				color = ColorEnum.GREEN;
			else if (value instanceof MeasureField)
				color = ColorEnum.BLUE;
			else
				color = ColorEnum.YELLOW;
		}

		this.setValue(value);
		this.setColor(color);
		this.setSelected(isSelected);

		return this;
	}
}
