package edu.zjut.common.ctrl;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;

public class FieldList<DataField> extends JList<DataField> implements
		KeyListener {

	public static final int VERTICAL = 0;
	public static final int HORIZONTAL = 3;

	public static final int DIMENSION = 0;
	public static final int MEASURE = 1;

	private int fieldType = MEASURE;

	private FieldListCell<DataField> cellRenderer;
	private boolean isHorizontal = false;
	private int listHeight;

	public FieldList() {
		init(MEASURE);
	}

	public FieldList(int fieldType) {
		init(fieldType);
	}

	public FieldList(int fieldType, ListModel<DataField> dataModel) {
		super(dataModel);
		init(fieldType);
	}

	public FieldList(final DataField[] listData) {
		super(listData);
		init(fieldType);
	}

	private void init(int fieldType) {
		this.fieldType = fieldType;

		this.addKeyListener(this);

		this.setTransferHandler(new FieldTransferHandler(fieldType));

		this.cellRenderer = new FieldListCell<DataField>();
		this.setCellRenderer(cellRenderer);
		this.setPreferredSize(new Dimension(100, 25));
	}

	public int getFieldType() {
		return fieldType;
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
