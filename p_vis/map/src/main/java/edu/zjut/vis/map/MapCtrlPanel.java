package edu.zjut.vis.map;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;

import edu.zjut.common.ctrl.FieldComponent;
import edu.zjut.common.ctrl.FieldImporter;
import edu.zjut.common.ctrl.FieldList;
import edu.zjut.common.ctrl.FieldComponent.ColorEnum;
import edu.zjut.common.data.attr.MeasureField;
import edu.zjut.common.data.geo.GeoLayer;
import edu.zjut.map.JMapPanel;
import edu.zjut.map.overlay.EsriLayer;

public class MapCtrlPanel extends JPanel {

	GeoMap geoMap;
	JMapPanel mapPanel;

	JPanel markerPanel;
	JPanel layerPanel;
	JPanel bookmarkPanel;

	FieldComponent<MeasureField> colorFieldComp;
	FieldComponent<MeasureField> sizeFieldcomp;
	JList<LayerCheckableItem> layerList;
	FieldList<String> bookmarkList;

	DefaultListModel<LayerCheckableItem> layerModel;

	public MapCtrlPanel(GeoMap geoMap, JMapPanel mapPanel) {
		this.geoMap = geoMap;
		this.mapPanel = mapPanel;

		this.setLayout(new BorderLayout());

		initMarkerPanel();
		this.add(markerPanel, BorderLayout.NORTH);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		this.add(panel, BorderLayout.CENTER);

		initLayerPanel();
		panel.add(layerPanel);

		initBookmarkPanel();
		panel.add(bookmarkPanel);
	}

	private void initMarkerPanel() {
		markerPanel = new JPanel();
		markerPanel.setPreferredSize(new Dimension(200, 75));

		markerPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

		SpringLayout sl_jPanel1 = new SpringLayout();
		markerPanel.setLayout(sl_jPanel1);

		colorFieldComp = new FieldComponent<MeasureField>();
		colorFieldComp.setTransferHandler(new ColorImporter());

		sl_jPanel1.putConstraint(SpringLayout.NORTH, colorFieldComp, 5,
				SpringLayout.NORTH, markerPanel);
		sl_jPanel1.putConstraint(SpringLayout.WEST, colorFieldComp, 40,
				SpringLayout.WEST, markerPanel);
		sl_jPanel1.putConstraint(SpringLayout.EAST, colorFieldComp, -5,
				SpringLayout.EAST, markerPanel);
		markerPanel.add(colorFieldComp);

		sizeFieldcomp = new FieldComponent<MeasureField>();
		sizeFieldcomp.setTransferHandler(new SizeImporter());

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

		colorFieldComp.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					colorFieldComp.setValue(null);
					colorFieldComp.setColor(ColorEnum.WHITE);
					geoMap.colorField = null;
					geoMap.updateMarkerAppearance();

					repaint();
					mapPanel.repaint();
				}
			}
		});
	}

	private void initLayerPanel() {
		layerPanel = new JPanel();
		layerPanel.setLayout(new BorderLayout());

		JLabel layerLabel = new JLabel("Layers");
		layerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		layerPanel.add(layerLabel, BorderLayout.NORTH);

		layerModel = new DefaultListModel<LayerCheckableItem>();

		layerList = new JList<LayerCheckableItem>();
		layerList.setCellRenderer(new LayerComponent());
		layerList.setVisibleRowCount(10);
		layerList.setDropMode(DropMode.ON_OR_INSERT);
		layerList.setDragEnabled(true);
		layerList.setModel(layerModel);
		layerList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				if (event.getX() > 16)
					return;

				int index = layerList.locationToIndex(event.getPoint());
				LayerCheckableItem item = layerModel.getElementAt(index);
				item.setVisible(!item.isVisible());
				layerList.repaint(layerList.getCellBounds(index, index));

				updataMapLayers();
			}
		});

		layerList.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int index = layerList.getSelectedIndex();
				if (index != -1 && e.getKeyCode() == KeyEvent.VK_DELETE) {
					layerModel.remove(index);
					updataMapLayers();
				}
			}
		});

		layerList.setTransferHandler(new LayerImporter());

		layerPanel.add(new JScrollPane(layerList), BorderLayout.CENTER);
	}

	private void initBookmarkPanel() {
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
	}

	public void updataMapLayers() {
		List<EsriLayer> layerList = new ArrayList<EsriLayer>();

		int len = layerModel.size();
		for (int i = 0; i < len; i++) {
			LayerCheckableItem item = layerModel.get(i);

			if (item.isVisible()) {
				layerList.add(new EsriLayer(item.getLayer().getFeatures()));
			}
		}

		mapPanel.setLayerList(layerList);
		mapPanel.repaint();
	}

	public class LayerImporter extends FieldImporter<GeoLayer> {

		public LayerImporter() {
			super(GeoLayer.class);
		}

		public boolean importData(TransferHandler.TransferSupport support) {
			JList.DropLocation dl = (JList.DropLocation) support
					.getDropLocation();

			int index = dl.getIndex();

			getTransferData(support);

			List<GeoLayer> values = data.getValues();
			for (int i = 0; i < values.size(); i++, index++) {
				layerModel.insertElementAt(
						new LayerCheckableItem(values.get(i)), index);
			}

			updataMapLayers();

			return true;
		}
	}

	class ColorImporter extends FieldImporter<MeasureField> {
		public ColorImporter() {
			super(MeasureField.class);
		}

		public boolean importData(TransferHandler.TransferSupport support) {
			getTransferData(support);
			List<MeasureField> values = data.getValues();
			if (!values.isEmpty()) {
				colorFieldComp.setValue(values.get(0));
				colorFieldComp.setColor(ColorEnum.BLUE);
				geoMap.colorField = values.get(0);
				geoMap.updateMarkerAppearance();

				repaint();
				mapPanel.repaint();
			}

			return true;
		}
	}

	class SizeImporter extends FieldImporter<MeasureField> {
		public SizeImporter() {
			super(MeasureField.class);
		}

		public boolean importData(TransferHandler.TransferSupport support) {
			getTransferData(support);
			List<MeasureField> values = data.getValues();
			if (!values.isEmpty()) {
				sizeFieldcomp.setValue(values.get(0));
				sizeFieldcomp.setColor(ColorEnum.BLUE);
				geoMap.sizeField = values.get(0);
				geoMap.updateMarkerAppearance();

				repaint();
				mapPanel.repaint();
			}

			return true;
		}
	}
}
