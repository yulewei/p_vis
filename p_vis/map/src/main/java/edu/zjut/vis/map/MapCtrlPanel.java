package edu.zjut.vis.map;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DropMode;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import edu.zjut.common.ctrl.FieldComponent;
import edu.zjut.common.ctrl.FieldList;
import edu.zjut.map.JMapPanel;

public class MapCtrlPanel extends JPanel {

	JMapPanel mapPanel;

	JPanel markerPanel;
	JPanel layerPanel;
	JPanel bookmarkPanel;

	FieldComponent colorFieldComp;
	FieldComponent sizeFieldcomp;

	FieldList<String> layerList;
	FieldList<String> bookmarkList;

	public MapCtrlPanel(JMapPanel mapPanel) {
		this.mapPanel = mapPanel;

		this.setLayout(new BorderLayout());

		initMarkerPanel();
		this.add(markerPanel, BorderLayout.NORTH);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		this.add(panel, BorderLayout.CENTER);

		layerPanel = new JPanel();
		layerPanel.setLayout(new BorderLayout());

		JLabel layerLabel = new JLabel("Layers");
		layerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		layerPanel.add(layerLabel, BorderLayout.NORTH);

		layerList = new FieldList<String>();
		layerList.setVisibleRowCount(10);
		layerList.setDropMode(DropMode.ON_OR_INSERT);
		layerList.setDragEnabled(true);
		layerPanel.add(new JScrollPane(layerList), BorderLayout.CENTER);

		panel.add(layerPanel);
		
		bookmarkPanel = new JPanel();
		bookmarkPanel.setLayout(new BorderLayout());

		JLabel bookmarkLabel = new JLabel("Bookmarks");
		bookmarkLabel.setHorizontalAlignment(SwingConstants.CENTER);
		bookmarkPanel.add(bookmarkLabel, BorderLayout.NORTH);

		bookmarkList = new FieldList<String>();
		bookmarkList.setVisibleRowCount(10);
		bookmarkList.setDropMode(DropMode.ON_OR_INSERT);
		bookmarkList.setDragEnabled(true);
		bookmarkPanel.add(new JScrollPane(bookmarkList), BorderLayout.CENTER);
		
		panel.add(bookmarkPanel);
	}

	private void initMarkerPanel() {
		markerPanel = new JPanel();
		markerPanel.setPreferredSize(new Dimension(200, 75));

		markerPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

		SpringLayout sl_jPanel1 = new SpringLayout();
		markerPanel.setLayout(sl_jPanel1);

		colorFieldComp = new FieldComponent();
		sl_jPanel1.putConstraint(SpringLayout.NORTH, colorFieldComp, 5,
				SpringLayout.NORTH, markerPanel);
		sl_jPanel1.putConstraint(SpringLayout.WEST, colorFieldComp, 40,
				SpringLayout.WEST, markerPanel);
		sl_jPanel1.putConstraint(SpringLayout.EAST, colorFieldComp, -5,
				SpringLayout.EAST, markerPanel);
		markerPanel.add(colorFieldComp);

		sizeFieldcomp = new FieldComponent();
		sl_jPanel1.putConstraint(SpringLayout.NORTH, sizeFieldcomp, 6,
				SpringLayout.SOUTH, colorFieldComp);
		sl_jPanel1.putConstraint(SpringLayout.WEST, sizeFieldcomp, 0,
				SpringLayout.WEST, colorFieldComp);
		sl_jPanel1.putConstraint(SpringLayout.EAST, sizeFieldcomp, 0,
				SpringLayout.EAST, colorFieldComp);
		markerPanel.add(sizeFieldcomp);

		JLabel colorLbl = new JLabel("Color");
		sl_jPanel1.putConstraint(SpringLayout.SOUTH, colorLbl, -5,
				SpringLayout.SOUTH, colorFieldComp);
		sl_jPanel1.putConstraint(SpringLayout.WEST, colorLbl, 5,
				SpringLayout.WEST, markerPanel);
		markerPanel.add(colorLbl);

		JLabel lblSize = new JLabel("Size");
		sl_jPanel1.putConstraint(SpringLayout.SOUTH, lblSize, -5,
				SpringLayout.SOUTH, sizeFieldcomp);
		sl_jPanel1.putConstraint(SpringLayout.WEST, lblSize, 0,
				SpringLayout.WEST, colorLbl);
		markerPanel.add(lblSize);
	}
}
