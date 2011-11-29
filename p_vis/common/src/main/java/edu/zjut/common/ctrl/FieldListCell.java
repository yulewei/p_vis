package edu.zjut.common.ctrl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import edu.zjut.common.data.attr.DimensionField;
import edu.zjut.common.data.attr.MeasureField;

public class FieldListCell<E> extends JComponent implements ListCellRenderer<E> {

	Color green = new Color(140, 200, 175, 200);
	Color darkGreen = new Color(135, 170, 135, 200);

	Color blue = new Color(175, 200, 230, 200);
	Color darkBlue = new Color(135, 160, 180, 200);

	Color color;
	Color selectedColor;

	boolean isSelected = false;

	String text = "hello";

	public FieldListCell() {
		this.setOpaque(false);
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

		g2.setColor(Color.BLACK);
		g2.drawRoundRect(x, y, w, h, 20, 20);

		if (!isSelected)
			g2.setColor(color);
		else
			g2.setColor(selectedColor);

		g2.fillRoundRect(x, y, w, h, 20, 20);
		g2.setColor(Color.BLACK);
		g2.drawString(text, textGap, y + 15);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends E> list,
			E value, int index, boolean isSelected, boolean cellHasFocus) {

		FieldList<E> fieldList = (FieldList<E>) list;
		int type = fieldList.getFieldType();

		if (type == FieldList.DIMENSION) {
			color = green;
			selectedColor = darkGreen;
		}

		if (type == FieldList.MEASURE) {
			color = blue;
			selectedColor = darkBlue;
		}

		this.text = value.toString();
		this.setToolTipText(text);
		this.isSelected = isSelected;
		return this;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getSelectedColor() {
		return selectedColor;
	}

	public void setSelectedColor(Color selectedColor) {
		this.selectedColor = selectedColor;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("ListDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new FieldListCell<String>());

		frame.setPreferredSize(new java.awt.Dimension(300, 200));

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
