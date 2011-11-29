package edu.zjut.vis.treemap;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import org.gicentre.apps.hide.Layout;
import org.gicentre.apps.hide.TreemapState;
import org.gicentre.data.summary.SummariseField;

import edu.zjut.common.ctrl.FieldList;
import edu.zjut.common.data.attr.DataField;

public class ControlPanel extends JPanel implements MouseMotionListener,
		MouseListener {

	private JLabel hierLabel;
	private JLabel sizeLabel;
	private JLabel orderLabel;
	private JLabel colorLabel;
	private JLabel layoutLabel;

	private FieldList<DataField> hierFiledList;
	private FieldList<SummariseField> sizeFieldList;
	private FieldList<SummariseField> orderFieldList;
	private FieldList<SummariseField> colorFieldList;
	private FieldList<Layout> layoutList;

	ListModel<DataField> hierListModel;
	ListModel<SummariseField> sizeListModel;
	ListModel<SummariseField> orderListModel;
	ListModel<SummariseField> colorListModel;

	private TreemapApplet pTreemap;
	TreemapState treemapState;

	public ControlPanel() {
		this.addMouseMotionListener(this);
		this.addMouseListener(this);

		// this.setBackground(Color.RED);

		this.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		SpringLayout sl_panel = new SpringLayout();
		this.setLayout(sl_panel);

		hierLabel = new JLabel("Hier");
		hierLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(hierLabel);

		hierFiledList = new FieldList<DataField>(FieldList.DIMENSION);
		hierFiledList.setDropMode(DropMode.ON_OR_INSERT);
		hierFiledList.setDragEnabled(true);
		hierListModel = new DefaultListModel<DataField>();
		hierFiledList.setModel(hierListModel);
		this.add(hierFiledList);

		sizeLabel = new JLabel("Size");
		sizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(sizeLabel);
		sizeFieldList = new FieldList<SummariseField>(FieldList.MEASURE);
		sizeFieldList.setDropMode(DropMode.ON_OR_INSERT);
		sizeFieldList.setDragEnabled(true);
		sizeListModel = new DefaultListModel<SummariseField>();
		sizeFieldList.setModel(sizeListModel);
		this.add(sizeFieldList);

		orderLabel = new JLabel("Order");
		orderLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(orderLabel);
		orderFieldList = new FieldList<SummariseField>(FieldList.MEASURE);
		orderFieldList.setDropMode(DropMode.ON_OR_INSERT);
		orderFieldList.setDragEnabled(true);
		orderListModel = new DefaultListModel<SummariseField>();
		orderFieldList.setModel(orderListModel);
		this.add(orderFieldList);

		colorLabel = new JLabel("Color");
		colorLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(colorLabel);
		colorFieldList = new FieldList<SummariseField>(FieldList.MEASURE);
		colorFieldList.setDropMode(DropMode.ON_OR_INSERT);
		colorFieldList.setDragEnabled(true);
		colorListModel = new DefaultListModel<SummariseField>();
		colorFieldList.setModel(colorListModel);
		this.add(colorFieldList);

		layoutLabel = new JLabel("Layout");
		layoutLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(layoutLabel);
		layoutList = new FieldList<Layout>();
		this.add(layoutList);

		initLayout(sl_panel);
	}

	public void initLayout(SpringLayout sl_panel) {

		// hier
		sl_panel.putConstraint(SpringLayout.WEST, hierLabel, 12,
				SpringLayout.WEST, this);
		sl_panel.putConstraint(SpringLayout.NORTH, hierLabel, 5,
				SpringLayout.NORTH, hierFiledList);
		sl_panel.putConstraint(SpringLayout.WEST, hierFiledList, 5,
				SpringLayout.EAST, hierLabel);
		sl_panel.putConstraint(SpringLayout.EAST, hierFiledList, -5,
				SpringLayout.EAST, this);
		sl_panel.putConstraint(SpringLayout.NORTH, hierFiledList, 10,
				SpringLayout.NORTH, this);

		// size
		sl_panel.putConstraint(SpringLayout.EAST, sizeLabel, 0,
				SpringLayout.EAST, hierLabel);
		sl_panel.putConstraint(SpringLayout.NORTH, sizeLabel, 5,
				SpringLayout.NORTH, sizeFieldList);
		sl_panel.putConstraint(SpringLayout.NORTH, sizeFieldList, 6,
				SpringLayout.SOUTH, hierFiledList);
		sl_panel.putConstraint(SpringLayout.WEST, sizeFieldList, 0,
				SpringLayout.WEST, hierFiledList);
		sl_panel.putConstraint(SpringLayout.EAST, sizeFieldList, 0,
				SpringLayout.EAST, hierFiledList);

		// order
		sl_panel.putConstraint(SpringLayout.EAST, orderLabel, 0,
				SpringLayout.EAST, hierLabel);
		sl_panel.putConstraint(SpringLayout.NORTH, orderLabel, 5,
				SpringLayout.NORTH, orderFieldList);
		sl_panel.putConstraint(SpringLayout.NORTH, orderFieldList, 6,
				SpringLayout.SOUTH, sizeFieldList);
		sl_panel.putConstraint(SpringLayout.WEST, orderFieldList, 0,
				SpringLayout.WEST, hierFiledList);
		sl_panel.putConstraint(SpringLayout.EAST, orderFieldList, 0,
				SpringLayout.EAST, hierFiledList);

		// color
		sl_panel.putConstraint(SpringLayout.EAST, colorLabel, 0,
				SpringLayout.EAST, hierLabel);
		sl_panel.putConstraint(SpringLayout.NORTH, colorLabel, 5,
				SpringLayout.NORTH, colorFieldList);
		sl_panel.putConstraint(SpringLayout.NORTH, colorFieldList, 6,
				SpringLayout.SOUTH, orderFieldList);
		sl_panel.putConstraint(SpringLayout.WEST, colorFieldList, 0,
				SpringLayout.WEST, hierFiledList);
		sl_panel.putConstraint(SpringLayout.EAST, colorFieldList, 0,
				SpringLayout.EAST, hierFiledList);

		// layout
		sl_panel.putConstraint(SpringLayout.EAST, layoutLabel, 0,
				SpringLayout.EAST, hierLabel);
		sl_panel.putConstraint(SpringLayout.NORTH, layoutLabel, 5,
				SpringLayout.NORTH, layoutList);
		sl_panel.putConstraint(SpringLayout.NORTH, layoutList, 6,
				SpringLayout.SOUTH, colorFieldList);
		sl_panel.putConstraint(SpringLayout.WEST, layoutList, 0,
				SpringLayout.WEST, hierFiledList);
		sl_panel.putConstraint(SpringLayout.EAST, layoutList, 0,
				SpringLayout.EAST, hierFiledList);
	}

	public void setState(TreemapState treemapState, TreemapApplet pTreemap) {
		this.treemapState = treemapState;
		this.pTreemap = pTreemap;

		DataField[] hierFields = treemapState.getHierFields();
		hierFiledList.setListData(hierFields);

		SummariseField[] sizeFields = treemapState.getSizeFields()[0];
		sizeFieldList.setListData(sizeFields);

		SummariseField[] orderFields = treemapState.getOrderFields()[0];
		orderFieldList.setListData(orderFields);

		SummariseField[] colorFields = treemapState.getColourFields()[0];
		colorFieldList.setListData(colorFields);
		
		Layout[] layouts = treemapState.getLayouts();
		layoutList.setListData(layouts);
	}

	boolean isDrag = false;
	int w = 200;

	@Override
	public void mouseDragged(MouseEvent e) {
		if (isDrag) {
			int h = this.getHeight();
			w = e.getX();
			if (w < 10)
				w = 10;
			if (w > getParent().getWidth() - 20)
				w = getParent().getWidth() - 20;

			this.setPreferredSize(new Dimension(w, h));
			pTreemap.setPreferredSize(new Dimension(getParent().getWidth() - w,
					h));

			doLayout();
			getParent().doLayout();
			getParent().repaint();
			repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (isDrag || Math.abs(w - e.getX() - 3) < 2) {
			getParent().setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
		} else {
			getParent().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (Math.abs(w - e.getX() - 3) < 2) {
			isDrag = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		isDrag = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (!isDrag) {
			getParent().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

}
