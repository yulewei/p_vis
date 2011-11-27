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

public class FieldListCell<DataField> extends JComponent implements
		ListCellRenderer<DataField> {

	Color color;
	Color selectedColor;

	boolean isSelected = false;

	String text = "hello";
	int w = 120;
	int h = 20;

	public FieldListCell() {
		this.setOpaque(false);
		this.setPreferredSize(new Dimension(100, 25));
	}

	@Override
	protected void paintComponent(Graphics g) {
		int width = this.getWidth();
		int height = this.getHeight();

		w = width - 3;
		int x = 0, y = height / 2 - h / 2;

		// System.out.println(x + " " + y);

		Graphics2D g2 = (Graphics2D) g;
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
		g2.drawString(text, 20, y + 15);
	}

	@Override
	public Component getListCellRendererComponent(
			JList<? extends DataField> list, DataField value, int index,
			boolean isSelected, boolean cellHasFocus) {
		this.text = value.toString();
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
