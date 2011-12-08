package edu.zjut.vis.treemap;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.gicentre.apps.hide.AppearanceType;
import org.gicentre.apps.hide.Layout;
import org.gicentre.apps.hide.TreemapState;
import org.gicentre.data.summary.SummariseField;
import org.gicentre.data.summary.SummariseNull;

import edu.zjut.common.ctrl.FieldComponent;
import edu.zjut.common.ctrl.FieldComponent.ColorEnum;
import edu.zjut.common.ctrl.FieldExporter;
import edu.zjut.common.ctrl.FieldImporter;
import edu.zjut.common.ctrl.FieldList;
import edu.zjut.common.ctrl.ListGroup;
import edu.zjut.common.data.attr.DataField;
import edu.zjut.common.data.attr.DimensionField;
import edu.zjut.common.data.attr.MeasureField;

public class TreemapCtrlPanel extends JPanel {

	private SpringLayout layout;
	private JLabel hierLabel;
	private JLabel sizeLabel;
	private JLabel orderLabel;
	private JLabel colorLabel;
	private JLabel layoutLabel;
	private JLabel appearanceLabel;
	private JLabel showLabel;
	private JLabel verticalLabel;
	private JLabel opacityLabel;
	private JLabel paddingLabel;

	private FieldList<DataField> hierList;
	private FieldList<SummariseField> sizeList;
	private FieldList<SummariseField> orderList;
	private FieldList<SummariseField> colorList;
	private FieldList<Layout> layoutList;
	private FieldList<Layout> allowedLayouts;

	ArrayList<JCheckBox> showChckbxList;
	ArrayList<JCheckBox> verticalChckbxList;
	ArrayList<JSlider> opacitySldrList;
	ArrayList<JSlider> paddingSldrList;

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

	public TreemapCtrlPanel(TreemapApplet pTreemap) {
		this.pTreemap = pTreemap;
		this.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		layout = new SpringLayout();
		this.setLayout(layout);

		hierModel = new DefaultListModel<DataField>();
		sizeModel = new DefaultListModel<SummariseField>();
		orderModel = new DefaultListModel<SummariseField>();
		colorModel = new DefaultListModel<SummariseField>();
		layoutModel = new DefaultListModel<Layout>();

		hierList = new FieldList<DataField>();
		hierList.setCellRenderer(new FieldComponent<DataField>(ColorEnum.GREEN));
		hierList.setLayoutOrientation(FieldList.HORIZONTAL);
		hierList.setDropMode(DropMode.ON_OR_INSERT);
		hierList.setDragEnabled(true);
		hierList.setModel(hierModel);
		hierList.addKeyListener(new HierKeyDelete());
		hierList.setTransferHandler(new HierFieldImporter());
		this.add(hierList);

		sizeList = new FieldList<SummariseField>();
		sizeList.setCellRenderer(new FieldComponent<SummariseField>(
				ColorEnum.BLUE));
		sizeList.setLayoutOrientation(FieldList.HORIZONTAL);
		sizeList.setDropMode(DropMode.ON);
		sizeList.setDragEnabled(true);
		sizeList.setModel(sizeModel);
		sizeList.addKeyListener(new NoStructKeyDelete(NoStructType.SIZE));
		sizeList.setTransferHandler(new RepalceFieldImporter(NoStructType.SIZE));
		this.add(sizeList);

		orderList = new FieldList<SummariseField>();
		orderList.setCellRenderer(new FieldComponent<SummariseField>(
				ColorEnum.BLUE));
		orderList.setLayoutOrientation(FieldList.HORIZONTAL);
		orderList.setDropMode(DropMode.ON);
		orderList.setDragEnabled(true);
		orderList.setModel(orderModel);
		orderList.addKeyListener(new NoStructKeyDelete(NoStructType.ORDER));
		orderList.setTransferHandler(new RepalceFieldImporter(
				NoStructType.ORDER));
		this.add(orderList);

		colorList = new FieldList<SummariseField>();
		colorList.setCellRenderer(new FieldComponent<SummariseField>(
				ColorEnum.BLUE));
		colorList.setLayoutOrientation(FieldList.HORIZONTAL);
		colorList.setDropMode(DropMode.ON);
		colorList.setDragEnabled(true);
		colorList.setModel(colorModel);
		colorList.addKeyListener(new NoStructKeyDelete(NoStructType.COLOR));
		colorList.setTransferHandler(new RepalceFieldImporter(
				NoStructType.COLOR));
		this.add(colorList);

		layoutList = new FieldList<Layout>();
		layoutList.setCellRenderer(new FieldComponent<Layout>(ColorEnum.BLUE));
		layoutList.setLayoutOrientation(FieldList.HORIZONTAL);
		layoutList.setDropMode(DropMode.ON);
		layoutList.setDragEnabled(true);
		layoutList.setModel(layoutModel);
		layoutList.setTransferHandler(new LayoutImporter());
		this.add(layoutList);

		ListGroup group = new ListGroup();
		group.add(hierList);
		group.add(sizeList);
		group.add(orderList);
		group.add(colorList);
		group.add(layoutList);

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
		appearanceLabel = new JLabel("Appearance");
		this.add(appearanceLabel);

		showLabel = new JLabel("Show");
		verticalLabel = new JLabel("Vertical");
		opacityLabel = new JLabel("Opacity");
		paddingLabel = new JLabel("Padding");
		this.add(showLabel);
		this.add(verticalLabel);
		this.add(opacityLabel);
		this.add(paddingLabel);

		allowedLayouts = new FieldList<Layout>();
		allowedLayouts.setCellRenderer(new FieldComponent<Layout>(
				ColorEnum.BLUE));
		allowedLayouts.setLayoutOrientation(FieldList.HORIZONTAL);
		allowedLayouts.setFixedCellWidth(30);
		allowedLayouts.setDropMode(DropMode.ON);
		allowedLayouts.setDragEnabled(true);
		allowedLayouts.setTransferHandler(new FieldExporter<Layout>(
				Layout.class));
		this.add(allowedLayouts);

		showChckbxList = new ArrayList<JCheckBox>();
		verticalChckbxList = new ArrayList<JCheckBox>();
		opacitySldrList = new ArrayList<JSlider>();
		paddingSldrList = new ArrayList<JSlider>();

		initSpringLayout();
	}

	public void initSpringLayout() {
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

		// allowed layouts
		layout.putConstraint(SpringLayout.NORTH, allowedLayouts, 20,
				SpringLayout.SOUTH, layoutList);
		layout.putConstraint(SpringLayout.WEST, allowedLayouts, -20,
				SpringLayout.WEST, hierList);
		layout.putConstraint(SpringLayout.EAST, allowedLayouts, 0,
				SpringLayout.EAST, hierList);

		// Appearance
		layout.putConstraint(SpringLayout.WEST, appearanceLabel, 0,
				SpringLayout.WEST, layoutLabel);
		layout.putConstraint(SpringLayout.NORTH, appearanceLabel, 22,
				SpringLayout.SOUTH, allowedLayouts);

		// Appearance
		layout.putConstraint(SpringLayout.EAST, showLabel, 10,
				SpringLayout.EAST, layoutLabel);
		layout.putConstraint(SpringLayout.NORTH, showLabel, 10,
				SpringLayout.SOUTH, appearanceLabel);

		layout.putConstraint(SpringLayout.EAST, verticalLabel, 0,
				SpringLayout.EAST, showLabel);
		layout.putConstraint(SpringLayout.NORTH, verticalLabel, 20,
				SpringLayout.NORTH, showLabel);

		layout.putConstraint(SpringLayout.EAST, opacityLabel, 0,
				SpringLayout.EAST, verticalLabel);
		layout.putConstraint(SpringLayout.NORTH, opacityLabel, 50,
				SpringLayout.NORTH, verticalLabel);

		layout.putConstraint(SpringLayout.EAST, paddingLabel, 0,
				SpringLayout.EAST, opacityLabel);
		layout.putConstraint(SpringLayout.NORTH, paddingLabel, 50,
				SpringLayout.NORTH, opacityLabel);
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

		final List<Layout> layouts = treemapState.getAllowedLayouts();
		allowedLayouts.setModel(new AbstractListModel<Layout>() {
			public int getSize() {
				return layouts.size();
			}

			public Layout getElementAt(int i) {
				return layouts.get(i);
			}
		});

		for (int i = showChckbxList.size() - 1; i >= 0; i--) {
			removeAppearHier(i);
		}

		for (int i = 0; i < hierFields.length; i++) {
			HashMap<AppearanceType, Integer> appearanceValues = treemapState
					.getAppearance(i);
			addAppearHier(i, appearanceValues);
		}
	}

	public void swap(int level1, int level2) {

	}

	private void addAppearHier(int index,
			final HashMap<AppearanceType, Integer> appearanceValues) {

		boolean show = appearanceValues.get(AppearanceType.SHOW_LABELS) == 1 ? true
				: false;
		final JCheckBox showChckbx = new JCheckBox(null, null, show);
		showChckbx.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean selected = showChckbx.isSelected();
				appearanceValues.put(AppearanceType.SHOW_LABELS, selected ? 1
						: 0);
				treemapState.setAppearanceChanged(true);
				pTreemap.redraw();
			}
		});

		boolean vertical = appearanceValues
				.get(AppearanceType.ALLOW_VERTICAL_LABELS) == 1 ? true : false;
		final JCheckBox verticalChckbx = new JCheckBox(null, null, vertical);
		verticalChckbx.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean selected = verticalChckbx.isSelected();
				appearanceValues.put(AppearanceType.ALLOW_VERTICAL_LABELS,
						selected ? 1 : 0);
				treemapState.setAppearanceChanged(true);
				pTreemap.redraw();
			}
		});

		Integer opacity = appearanceValues.get(AppearanceType.LABEL_OPACITY);
		final JSlider opacitySldr = new JSlider(
				AppearanceType.LABEL_OPACITY.minValue(),
				AppearanceType.LABEL_OPACITY.maxValue(), opacity);
		opacitySldr.setOrientation(SwingConstants.VERTICAL);
		opacitySldr.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				appearanceValues.put(AppearanceType.LABEL_OPACITY,
						opacitySldr.getValue());
				treemapState.setAppearanceChanged(true);
				pTreemap.redraw();
			}
		});

		Integer padding = appearanceValues.get(AppearanceType.PADDING);
		final JSlider paddingSldr = new JSlider(
				AppearanceType.PADDING.minValue(),
				AppearanceType.PADDING.maxValue(), padding);
		paddingSldr.setOrientation(SwingConstants.VERTICAL);
		paddingSldr.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				appearanceValues.put(AppearanceType.PADDING,
						paddingSldr.getValue());
				treemapState.setAppearanceChanged(true);
				treemapState.setHierChanged(true);
				pTreemap.redraw();
			}
		});

		showChckbxList.add(index, showChckbx);
		verticalChckbxList.add(index, verticalChckbx);
		opacitySldrList.add(index, opacitySldr);
		paddingSldrList.add(index, paddingSldr);
		this.add(showChckbx);
		this.add(verticalChckbx);
		this.add(opacitySldr);
		this.add(paddingSldr);

		layoutAppear(index);
		layoutAppear(index + 1);
	}

	private void layoutAppear(int index) {
		if (index < 0 || index >= showChckbxList.size())
			return;

		JCheckBox showChckbx = showChckbxList.get(index);
		JCheckBox verticalChckbx = verticalChckbxList.get(index);
		JSlider opacitySldr = opacitySldrList.get(index);
		JSlider paddingSldr = paddingSldrList.get(index);

		if (index == 0) {
			layout.putConstraint(SpringLayout.NORTH, showChckbx, 120,
					SpringLayout.NORTH, layoutList);
			layout.putConstraint(SpringLayout.WEST, showChckbx, 10,
					SpringLayout.WEST, layoutList);

		} else {
			JCheckBox preChckbx = showChckbxList.get(index - 1);
			layout.putConstraint(SpringLayout.NORTH, showChckbx, 0,
					SpringLayout.NORTH, preChckbx);
			layout.putConstraint(SpringLayout.WEST, showChckbx, 50,
					SpringLayout.WEST, preChckbx);
		}

		layout.putConstraint(SpringLayout.WEST, verticalChckbx, 0,
				SpringLayout.WEST, showChckbx);
		layout.putConstraint(SpringLayout.NORTH, verticalChckbx, 20,
				SpringLayout.NORTH, showChckbx);

		layout.putConstraint(SpringLayout.WEST, opacitySldr, -2,
				SpringLayout.WEST, verticalChckbx);
		layout.putConstraint(SpringLayout.NORTH, opacitySldr, 10,
				SpringLayout.SOUTH, verticalChckbx);
		layout.putConstraint(SpringLayout.SOUTH, opacitySldr, 60,
				SpringLayout.SOUTH, verticalChckbx);

		layout.putConstraint(SpringLayout.WEST, paddingSldr, 0,
				SpringLayout.WEST, verticalChckbx);
		layout.putConstraint(SpringLayout.NORTH, paddingSldr, 10,
				SpringLayout.SOUTH, opacitySldr);
		layout.putConstraint(SpringLayout.SOUTH, paddingSldr, 60,
				SpringLayout.SOUTH, opacitySldr);
	}

	private void removeAppearHier(int index) {
		if (showChckbxList.isEmpty())
			return;

		JCheckBox showChckbx = showChckbxList.get(index);
		this.remove(showChckbx);
		showChckbxList.remove(index);

		JCheckBox verticalChckbx = verticalChckbxList.get(index);
		this.remove(verticalChckbx);
		verticalChckbxList.remove(index);

		JSlider opacitySldr = opacitySldrList.get(index);
		this.remove(opacitySldr);
		opacitySldrList.remove(index);

		JSlider paddingSldr = paddingSldrList.get(index);
		this.remove(paddingSldr);
		paddingSldrList.remove(index);

		layoutAppear(index);
	}

	public void cut(int index) {
		hierModel.remove(index);
		sizeModel.remove(index);
		orderModel.remove(index);
		colorModel.remove(index);
		layoutModel.remove(index);

		treemapState.cut(index);
		treemapState.setHierChanged(true);

		removeAppearHier(index);

		pTreemap.treemapPanel.flagToRedraw();
		pTreemap.redraw();

		validate();
		repaint();
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

		addAppearHier(index, treemapState.getAppearance(index));

		pTreemap.redraw();

		validate();
		repaint();
	}

	public void replaceHier(int index, DimensionField hierField) {
		hierModel.setElementAt(hierField, index);

		hierFields[index] = hierField;
		treemapState.setHierChanged(true);
		pTreemap.redraw();
	}

	public void replaceLayout(int index, Layout layout) {
		layoutModel.setElementAt(layout, index);
		layouts[index] = layout;

		treemapState.setHierChanged(true);
		pTreemap.treemapPanel.flagToDoNonStructuralRebuild();
		pTreemap.redraw();
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
		pTreemap.redraw();
	}

	/**
	 * ¼üÅÌdelete¼ü, É¾³ýÕû¸ö²ã´Îhier
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
				pTreemap.redraw();
			}
		}
	}

	class HierFieldImporter extends FieldImporter<DimensionField> {

		public HierFieldImporter() {
			super(DimensionField.class);
		}

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

	class RepalceFieldImporter extends FieldImporter<MeasureField> {
		NoStructType type;

		public RepalceFieldImporter(NoStructType type) {
			super(MeasureField.class);
			this.type = type;
		}

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

	class LayoutImporter extends FieldImporter<Layout> {

		public LayoutImporter() {
			super(Layout.class);
		}

		public boolean importData(TransferHandler.TransferSupport support) {
			JList.DropLocation dl = (JList.DropLocation) support
					.getDropLocation();

			int index = dl.getIndex();

			getTransferData(support);

			List<Layout> values = data.getValues();
			for (int i = 0; i < values.size(); i++, index++) {
				replaceLayout(index, values.get(i));
			}

			return true;
		}
	}
}
