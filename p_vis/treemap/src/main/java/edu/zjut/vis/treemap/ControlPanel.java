package edu.zjut.vis.treemap;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.TransferHandler;

import org.gicentre.apps.hide.Layout;
import org.gicentre.apps.hide.TreemapState;
import org.gicentre.data.summary.SummariseField;
import org.gicentre.data.summary.SummariseNull;

import edu.zjut.common.ctrl.FieldDnD;
import edu.zjut.common.ctrl.FieldList;
import edu.zjut.common.data.attr.DataField;
import edu.zjut.common.data.attr.DimensionField;
import edu.zjut.common.data.attr.MeasureField;

public class ControlPanel extends JPanel {

	private JLabel hierLabel;
	private JLabel sizeLabel;
	private JLabel orderLabel;
	private JLabel colorLabel;
	private JLabel layoutLabel;

	private FieldList<DataField> hierList;
	private FieldList<SummariseField> sizeList;
	private FieldList<SummariseField> orderList;
	private FieldList<SummariseField> colorList;
	private FieldList<Layout> layoutList;

	DefaultListModel<DataField> hierModel;
	DefaultListModel<SummariseField> sizeModel;
	DefaultListModel<SummariseField> orderModel;
	DefaultListModel<SummariseField> colorModel;
	DefaultListModel<Layout> layoutModel;

	TreemapApplet pTreemap;
	TreemapState treemapState;

	List<DataField> allowedHierFields;
	List<SummariseField> allowedSummariseFields;

	protected DataField[] hierFields;
	protected SummariseField[][] sizeFields;
	protected SummariseField[][] orderFields;
	protected SummariseField[][] colorFields;
	protected Layout[] layouts;

	public ControlPanel(TreemapApplet pTreemap) {
		this.pTreemap = pTreemap;

		MouseDragListener mouseDrag = new MouseDragListener();
		this.addMouseMotionListener(mouseDrag);
		this.addMouseListener(mouseDrag);

		this.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		SpringLayout layout = new SpringLayout();
		this.setLayout(layout);

		hierModel = new DefaultListModel<DataField>();
		sizeModel = new DefaultListModel<SummariseField>();
		orderModel = new DefaultListModel<SummariseField>();
		colorModel = new DefaultListModel<SummariseField>();
		layoutModel = new DefaultListModel<Layout>();

		hierList = new FieldList<DataField>();
		hierList.setDropMode(DropMode.ON_OR_INSERT);
		hierList.setDragEnabled(true);
		hierList.setModel(hierModel);
		hierList.addKeyListener(new HierKeyDelete());
		hierList.setTransferHandler(new HierFieldImporter());
		this.add(hierList);

		sizeList = new FieldList<SummariseField>();
		sizeList.setDropMode(DropMode.ON);
		sizeList.setDragEnabled(true);
		sizeList.setModel(sizeModel);
		sizeList.addKeyListener(new NoStructKeyDelete(NoStructType.SIZE));
		sizeList.setTransferHandler(new RepalceFieldImporter(NoStructType.SIZE));
		this.add(sizeList);

		orderList = new FieldList<SummariseField>();
		orderList.setDropMode(DropMode.ON);
		orderList.setDragEnabled(true);
		orderList.setModel(orderModel);
		orderList.addKeyListener(new NoStructKeyDelete(NoStructType.ORDER));
		orderList.setTransferHandler(new RepalceFieldImporter(
				NoStructType.ORDER));
		this.add(orderList);

		colorList = new FieldList<SummariseField>();
		colorList.setDropMode(DropMode.ON);
		colorList.setDragEnabled(true);
		colorList.setModel(colorModel);
		colorList.addKeyListener(new NoStructKeyDelete(NoStructType.COLOR));
		colorList.setTransferHandler(new RepalceFieldImporter(
				NoStructType.COLOR));
		this.add(colorList);

		layoutList = new FieldList<Layout>();
		layoutList.setDropMode(DropMode.ON);
		layoutList.setDragEnabled(true);
		layoutList.setModel(layoutModel);
		this.add(layoutList);

		// labels
		hierLabel = new JLabel("Hier");
		this.add(hierLabel);
		sizeLabel = new JLabel("Size");
		this.add(sizeLabel);
		orderLabel = new JLabel("Order");
		this.add(orderLabel);
		colorLabel = new JLabel("Color");
		this.add(colorLabel);
		layoutLabel = new JLabel("Layout");
		this.add(layoutLabel);

		initSpringLayout(layout);
	}

	public void initSpringLayout(SpringLayout layout) {
		// hier
		layout.putConstraint(SpringLayout.WEST, hierLabel, 12,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, hierLabel, 3,
				SpringLayout.NORTH, hierList);
		layout.putConstraint(SpringLayout.WEST, hierList, 3, SpringLayout.EAST,
				hierLabel);
		layout.putConstraint(SpringLayout.EAST, hierList, -3,
				SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, hierList, 10,
				SpringLayout.NORTH, this);

		// size
		layout.putConstraint(SpringLayout.EAST, sizeLabel, 0,
				SpringLayout.EAST, hierLabel);
		layout.putConstraint(SpringLayout.NORTH, sizeLabel, 5,
				SpringLayout.NORTH, sizeList);
		layout.putConstraint(SpringLayout.NORTH, sizeList, 6,
				SpringLayout.SOUTH, hierList);
		layout.putConstraint(SpringLayout.WEST, sizeList, 0, SpringLayout.WEST,
				hierList);
		layout.putConstraint(SpringLayout.EAST, sizeList, 0, SpringLayout.EAST,
				hierList);

		// order
		layout.putConstraint(SpringLayout.EAST, orderLabel, 0,
				SpringLayout.EAST, hierLabel);
		layout.putConstraint(SpringLayout.NORTH, orderLabel, 5,
				SpringLayout.NORTH, orderList);
		layout.putConstraint(SpringLayout.NORTH, orderList, 6,
				SpringLayout.SOUTH, sizeList);
		layout.putConstraint(SpringLayout.WEST, orderList, 0,
				SpringLayout.WEST, hierList);
		layout.putConstraint(SpringLayout.EAST, orderList, 0,
				SpringLayout.EAST, hierList);

		// color
		layout.putConstraint(SpringLayout.EAST, colorLabel, 0,
				SpringLayout.EAST, hierLabel);
		layout.putConstraint(SpringLayout.NORTH, colorLabel, 5,
				SpringLayout.NORTH, colorList);
		layout.putConstraint(SpringLayout.NORTH, colorList, 6,
				SpringLayout.SOUTH, orderList);
		layout.putConstraint(SpringLayout.WEST, colorList, 0,
				SpringLayout.WEST, hierList);
		layout.putConstraint(SpringLayout.EAST, colorList, 0,
				SpringLayout.EAST, hierList);

		// layout
		layout.putConstraint(SpringLayout.EAST, layoutLabel, 0,
				SpringLayout.EAST, hierLabel);
		layout.putConstraint(SpringLayout.NORTH, layoutLabel, 5,
				SpringLayout.NORTH, layoutList);
		layout.putConstraint(SpringLayout.NORTH, layoutList, 6,
				SpringLayout.SOUTH, colorList);
		layout.putConstraint(SpringLayout.WEST, layoutList, 0,
				SpringLayout.WEST, hierList);
		layout.putConstraint(SpringLayout.EAST, layoutList, 0,
				SpringLayout.EAST, hierList);
	}

	public void setState(TreemapState treemapState,
			List<DataField> allowedHierFields,
			List<SummariseField> allowedSummariseFields) {
		this.treemapState = treemapState;
		this.allowedHierFields = allowedHierFields;
		this.allowedSummariseFields = allowedSummariseFields;

		hierFields = treemapState.getHierFields();
		sizeFields = treemapState.getSizeFields();
		orderFields = treemapState.getOrderFields();
		colorFields = treemapState.getColourFields();
		layouts = treemapState.getLayouts();

		hierModel.clear();
		sizeModel.clear();
		orderModel.clear();
		colorModel.clear();
		layoutModel.clear();

		for (DataField field : hierFields) {
			hierModel.addElement(field);
		}

		for (SummariseField field : sizeFields[0]) {
			sizeModel.addElement(field);
		}

		for (SummariseField field : orderFields[0]) {
			orderModel.addElement(field);
		}

		for (SummariseField field : colorFields[0]) {
			colorModel.addElement(field);
		}

		for (Layout layout : layouts) {
			layoutModel.addElement(layout);
		}
	}

	/**
	 * 鼠标拖动, 对面板大小控制
	 */
	class MouseDragListener extends MouseAdapter {
		private boolean isDrag = false;

		@Override
		public void mouseDragged(MouseEvent e) {
			if (isDrag) {
				int h = getHeight();
				int w = e.getX();
				if (w < 10)
					w = 10;
				if (w > getParent().getWidth() - 20)
					w = getParent().getWidth() - 20;

				setPreferredSize(new Dimension(w, h));

				doLayout();
				getParent().doLayout();
				getParent().repaint();
				repaint();
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (isDrag || Math.abs(getWidth() - e.getX() - 3) < 2) {
				getParent().setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
			} else {
				getParent().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (Math.abs(getWidth() - e.getX() - 3) < 2) {
				isDrag = true;
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			isDrag = false;
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (!isDrag) {
				getParent().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		}
	}

	public void swap(int level1, int level2) {

	}

	public void cut(int index) {
		hierModel.remove(index);
		sizeModel.remove(index);
		orderModel.remove(index);
		colorModel.remove(index);
		layoutModel.remove(index);

		treemapState.cut(index);
		treemapState.setHierChanged(true);
		pTreemap.treemapPanel.flagToRedraw();
		pTreemap.repaint();
	}

	public void insert(int index, DimensionField hierField) {
		SummariseField[] defaultOrderField = new SummariseField[orderFields.length];
		SummariseField[] defaultSizeField = new SummariseField[sizeFields.length];
		SummariseField[] defaultColourField = new SummariseField[colorFields.length];
		Layout defaultLayout = null;
		// if the first, use the top of the allowed lists
		if (index == 0) {
			SummariseField summariseNull = new SummariseNull("Null");
			for (int j = 0; j < orderFields.length; j++) {
				defaultOrderField[j] = summariseNull;
			}
			for (int j = 0; j < sizeFields.length; j++) {
				defaultSizeField[j] = summariseNull;
			}
			for (int j = 0; j < colorFields.length; j++) {
				defaultColourField[j] = summariseNull;
			}
			defaultLayout = Layout.ST;
		} else {
			// use the value to the left
			for (int j = 0; j < orderFields.length; j++) {
				defaultOrderField[j] = orderFields[j][index - 1];
			}
			for (int j = 0; j < sizeFields.length; j++) {
				defaultSizeField[j] = sizeFields[j][index - 1];
			}
			for (int j = 0; j < colorFields.length; j++) {
				defaultColourField[j] = colorFields[j][index - 1];
			}
			defaultLayout = layouts[index - 1];
		}

		hierModel.insertElementAt(hierField, index);
		sizeModel.insertElementAt(defaultSizeField[0], index);
		orderModel.insertElementAt(defaultOrderField[0], index);
		colorModel.insertElementAt(defaultColourField[0], index);
		layoutModel.insertElementAt(defaultLayout, index);

		treemapState.insert(index, hierField, defaultOrderField,
				defaultSizeField, defaultColourField, defaultLayout);
		treemapState.setHierChanged(true);
		pTreemap.repaint();
	}

	public void replaceHier(int index, DimensionField hierField) {
		hierModel.setElementAt(hierField, index);
		hierFields[index] = hierField;
		treemapState.setHierChanged(true);
		pTreemap.repaint();
	}

	static enum NoStructType {
		SIZE, ORDER, COLOR;
	}

	public void replace(int index, MeasureField measureField, NoStructType type) {
		SummariseField sumFiled = null;
		for (SummariseField filed : allowedSummariseFields) {
			if (filed.getName().equals(measureField.getName())) {
				sumFiled = filed;
				break;
			}
		}

		switch (type) {
		case SIZE:
			sizeModel.setElementAt(sumFiled, index);
			sizeFields[0][index] = sumFiled;
			break;
		case ORDER:
			orderModel.setElementAt(sumFiled, index);
			orderFields[0][index] = sumFiled;
			break;
		case COLOR:
			colorModel.setElementAt(sumFiled, index);
			colorFields[0][index] = sumFiled;
			break;
		}

		pTreemap.treemapPanel.flagToDoNonStructuralRebuild();
		pTreemap.repaint();
	}

	/**
	 * 键盘delete键, 删除整个层次hier
	 */
	class HierKeyDelete extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			int index = hierList.getSelectedIndex();
			if (index != -1 && e.getKeyCode() == KeyEvent.VK_DELETE) {
				cut(index);
			}
		}
	}

	class NoStructKeyDelete extends KeyAdapter {
		NoStructType type;

		NoStructKeyDelete(NoStructType type) {
			this.type = type;
		}

		@Override
		public void keyPressed(KeyEvent e) {
			FieldList<SummariseField> list = null;
			DefaultListModel<SummariseField> model = null;
			SummariseField[][] fields = null;

			switch (type) {
			case SIZE:
				list = sizeList;
				model = sizeModel;
				fields = sizeFields;
				break;
			case ORDER:
				list = orderList;
				model = orderModel;
				fields = orderFields;
				break;
			case COLOR:
				list = colorList;
				model = colorModel;
				fields = colorFields;
				break;
			}

			int index = list.getSelectedIndex();
			if (index != -1 && e.getKeyCode() == KeyEvent.VK_DELETE) {
				SummariseField summariseNull = new SummariseNull("Null");
				model.setElementAt(summariseNull, index);
				fields[0][index] = summariseNull;
				pTreemap.treemapPanel.flagToDoNonStructuralRebuild();
				pTreemap.repaint();
			}
		}
	}

	class HierFieldImporter extends TransferHandler {

		private FieldDnD<DimensionField> data;

		@SuppressWarnings("unchecked")
		private boolean getTransferData(TransferHandler.TransferSupport support) {
			try {
				data = (FieldDnD<DimensionField>) support
						.getTransferable()
						.getTransferData(
								new DataFlavor(FieldDnD.class,
										DataFlavor.javaSerializedObjectMimeType));
			} catch (Exception e) {
				return false;
			}

			return true;
		}

		public boolean canImport(TransferHandler.TransferSupport support) {
			if (!support.isDrop())
				return false;

			if (!getTransferData(support))
				return false;

			return data.getType() == DimensionField.class;
		}

		@SuppressWarnings("unchecked")
		public boolean importData(TransferHandler.TransferSupport support) {
			JList.DropLocation dl = (JList.DropLocation) support
					.getDropLocation();

			int index = dl.getIndex();
			boolean insert = dl.isInsert();

			getTransferData(support);

			List<DimensionField> values = data.getValues();
			for (int i = 0; i < values.size(); i++, index++) {
				if (insert) {
					insert(index, values.get(i));
				} else {
					replaceHier(index, values.get(i));
				}
			}

			return true;
		}
	}

	class RepalceFieldImporter extends TransferHandler {
		private FieldDnD<MeasureField> data;

		NoStructType type;

		public RepalceFieldImporter(NoStructType type) {
			this.type = type;
		}

		@SuppressWarnings("unchecked")
		private boolean getTransferData(TransferHandler.TransferSupport support) {
			try {
				data = (FieldDnD<MeasureField>) support
						.getTransferable()
						.getTransferData(
								new DataFlavor(FieldDnD.class,
										DataFlavor.javaSerializedObjectMimeType));
			} catch (Exception e) {
				return false;
			}

			return true;
		}

		@SuppressWarnings("unchecked")
		public boolean canImport(TransferHandler.TransferSupport support) {
			if (!support.isDrop())
				return false;

			if (!getTransferData(support))
				return false;

			return data.getType() == MeasureField.class;
		}

		@SuppressWarnings("unchecked")
		public boolean importData(TransferHandler.TransferSupport support) {
			JList.DropLocation dl = (JList.DropLocation) support
					.getDropLocation();

			int index = dl.getIndex();

			getTransferData(support);

			List<MeasureField> values = data.getValues();
			for (int i = 0; i < values.size(); i++, index++) {
				replace(index, values.get(i), type);
			}

			return true;
		}

	}
}
