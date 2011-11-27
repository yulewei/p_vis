package edu.zjut.common.ctrl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;

import edu.zjut.common.data.attr.DataField;
import edu.zjut.common.data.attr.DimensionField;
import edu.zjut.common.data.attr.MeasureField;

public class FieldList<DataField> extends JList<DataField> implements KeyListener {

	public static final int VERTICAL = 0;
	public static final int HORIZONTAL = 3;

	Color green = new Color(140, 200, 175, 200);
	Color darkGreen = new Color(135, 170, 135, 200);

	Color blue = new Color(175, 200, 230, 200);
	Color darkBlue = new Color(135, 160, 180, 200);

	public static final int DIMENSION = 0;
	public static final int MEASURE = 1;

	private int fieldType = MEASURE;
	private DataField[] fields;

	private FieldListCell<DataField> cellRenderer;
	private boolean isHorizontal = false;
	private int listHeight;

	public FieldList() {
		init();
	}

	public FieldList(ListModel<DataField> dataModel) {
		super(dataModel);
		init();
	}

	public FieldList(final DataField[] listData) {
		super(listData);
		init();
	}

	private void init() {
		this.addKeyListener(this);
		this.cellRenderer = new FieldListCell<DataField>();
		this.setCellRenderer(cellRenderer);
		this.setPreferredSize(new Dimension(100, 25));
	}

	public DataField[] getFields() {
		return fields;
	}

	public int getFieldType() {
		return fieldType;
	}

	public void setFields(DataField[] fields) {
		this.fields = fields;

		if (fields[0] instanceof DimensionField)
			this.fieldType = DIMENSION;
		if (fields[0] instanceof MeasureField)
			this.fieldType = MEASURE;

		if (fieldType == DIMENSION) {
			cellRenderer.setColor(green);
			cellRenderer.setSelectedColor(darkGreen);
		}

		if (fieldType == MEASURE) {
			cellRenderer.setColor(blue);
			cellRenderer.setSelectedColor(darkBlue);
		}
	}

	public void setLayoutOrientation(int layoutOrientation) {
		if (layoutOrientation == HORIZONTAL) {
			this.setVisibleRowCount(1);
			this.setFixedCellWidth(120);
			this.setFixedCellHeight(listHeight);
			isHorizontal = true;

			super.setLayoutOrientation(HORIZONTAL_WRAP);
		} else {
			this.setVisibleRowCount(10);
			this.setFixedCellHeight(25);
			isHorizontal = false;
			super.setLayoutOrientation(layoutOrientation);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (listHeight != this.getHeight()) {
			listHeight = this.getHeight();

			if (listHeight < 35)
				this.setLayoutOrientation(HORIZONTAL);
			else if (listHeight > 50)
				this.setLayoutOrientation(VERTICAL);

			if (isHorizontal)
				this.setFixedCellHeight(listHeight);
		}

		super.paintComponent(g);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	/**
	 * ¼üÅÌdelete¼ü, É¾³ýÑ¡ÖÐÔªËØ
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		int index = this.getSelectedIndex();
		if (index != -1 && e.getKeyCode() == KeyEvent.VK_DELETE) {
			DefaultListModel<DataField> model = (DefaultListModel<DataField>) getModel();
			model.remove(index);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}
}
