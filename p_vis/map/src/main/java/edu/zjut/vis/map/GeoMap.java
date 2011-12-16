package edu.zjut.vis.map;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;

import com.vividsolutions.jts.geom.Point;

import edu.zjut.common.ctrl.FieldComponent.ColorEnum;
import edu.zjut.common.ctrl.FieldImporter;
import edu.zjut.common.data.DataSetForApps;
import edu.zjut.common.data.attr.AttributeData;
import edu.zjut.common.data.attr.MeasureField;
import edu.zjut.common.data.geo.GeometryData;
import edu.zjut.common.event.DataSetEvent;
import edu.zjut.common.event.DataSetListener;
import edu.zjut.common.event.IndicationEvent;
import edu.zjut.common.event.IndicationListener;
import edu.zjut.common.event.SelectionEvent;
import edu.zjut.common.event.SelectionListener;
import edu.zjut.map.JMapPanel;
import edu.zjut.map.cluster.GridClusterer;
import edu.zjut.map.cluster.MarkerClusterer;
import edu.zjut.map.overlay.DefaultMapMarker;
import edu.zjut.map.overlay.IconMarker;
import edu.zjut.map.overlay.MapMarker;
import edu.zjut.map.overlay.Overlay;
import edu.zjut.vis.map.MapSelector.SelectType;

public class GeoMap extends JPanel implements DataSetListener,
		IndicationListener, SelectionListener {

	private DataSetForApps dataSet;
	private AttributeData attrData;
	private GeometryData geoData;

	private enum MarkerType {
		CIRCLE, MARKER, ICON
	};

	MarkerType markerType = MarkerType.MARKER;

	private JToolBar jToolBar;
	private JToggleButton sideBarTglbtn;
	private JToggleButton circleTglbtn;
	private JToggleButton markerTglbtn;
	private JToggleButton iconTglbtn;
	private JToggleButton visibleTglbtn;
	private ImageIcon visibleOn;
	private ImageIcon visibleOff;

	private JToggleButton moveTglbtn;
	private JButton zoomInTglbtn;
	private JButton zoomFitTglbtn;
	private JButton zoomOutTglbtn;
	private JToggleButton selectTglbtn;
	private JToggleButton selectRestTgbtn;
	private JToggleButton selectEllpTgbtn;
	private JToggleButton selectLassoTgbtn;
	private JToggleButton gridTgbtn;
	private JToggleButton crossTgbtn;
	private JToggleButton clusterTgbtn;

	private MapSelector selector;
	private MarkerClusterer clusterer;

	private JSplitPane jSplitPane;
	private MapCtrlPanel ctrlPanel;
	private JMapPanel mapPanel;

	protected MeasureField colorField;
	protected MeasureField sizeField;

	private HashMap<Integer, Overlay> indexMarkerMap;
	private HashMap<Overlay, Integer> markerIndexMap;

	public GeoMap() {

		this.setLayout(new BorderLayout());

		initToolbar();
		this.add(jToolBar, BorderLayout.NORTH);

		jSplitPane = new JSplitPane();
		jSplitPane.setOneTouchExpandable(true);
		this.add(jSplitPane, BorderLayout.CENTER);

		mapPanel = new JMapPanel();
		mapPanel.loadMapConfig("config/map_config.xml");
		mapPanel.setTransferHandler(new MapFieldImporter());
		mapPanel.setLayout(new BorderLayout());

		jSplitPane.add(mapPanel, JSplitPane.RIGHT);

		selector = new MapSelector(mapPanel);
		selector.setVisible(false);
		mapPanel.add(selector);

		clusterer = new GridClusterer(mapPanel);

		ctrlPanel = new MapCtrlPanel(this, mapPanel);
		jSplitPane.add(ctrlPanel, JSplitPane.LEFT);

		jSplitPane.setDividerLocation(150);

		this.setPreferredSize(new Dimension(500, 500));
	}

	private void initToolbar() {
		jToolBar = new JToolBar();

		// 侧边栏
		sideBarTglbtn = new JToggleButton();
		sideBarTglbtn.setIcon(new ImageIcon(getClass().getResource(
				"sidebar.png")));
		sideBarTglbtn.setSelected(true);
		sideBarTglbtn.addActionListener(new ActionListener() {
			private int dividerSize = -1;

			public void actionPerformed(ActionEvent evt) {
				if (dividerSize == -1) {
					dividerSize = jSplitPane.getDividerSize();
				}
				if (!sideBarTglbtn.isSelected()) {
					jSplitPane.setDividerLocation(0);
					jSplitPane.setDividerSize(0);
					ctrlPanel.setVisible(false);
				} else {
					jSplitPane.setDividerLocation(150);
					jSplitPane.setDividerSize(dividerSize);
					ctrlPanel.setVisible(true);
				}
			}
		});

		jToolBar.add(sideBarTglbtn);

		JSeparator separator1 = new JSeparator(SwingConstants.VERTICAL);
		jToolBar.add(separator1);

		// 显示模式
		circleTglbtn = new JToggleButton();
		circleTglbtn.setSelected(markerType == MarkerType.CIRCLE);
		circleTglbtn
				.setIcon(new ImageIcon(getClass().getResource("circle.png")));
		circleTglbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (circleTglbtn.isSelected()) {
					markerType = MarkerType.CIRCLE;
					updateMarkerList();

					mapPanel.repaint();
				}
			}
		});

		markerTglbtn = new JToggleButton();
		markerTglbtn.setSelected(markerType == MarkerType.MARKER);
		markerTglbtn
				.setIcon(new ImageIcon(getClass().getResource("marker.png")));
		markerTglbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (markerTglbtn.isSelected()) {
					markerType = MarkerType.MARKER;
					updateMarkerList();
					mapPanel.repaint();
				}
			}
		});

		iconTglbtn = new JToggleButton();
		iconTglbtn.setSelected(markerType == MarkerType.MARKER);
		iconTglbtn.setIcon(new ImageIcon(getClass().getResource("icon.png")));
		iconTglbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (iconTglbtn.isSelected()) {
					markerType = MarkerType.ICON;
					updateMarkerList();
					mapPanel.repaint();
				}
			}
		});

		jToolBar.add(circleTglbtn);
		jToolBar.add(markerTglbtn);
		jToolBar.add(iconTglbtn);

		ButtonGroup btnGrupIcon = new ButtonGroup();
		btnGrupIcon.add(circleTglbtn);
		btnGrupIcon.add(markerTglbtn);
		btnGrupIcon.add(iconTglbtn);

		JSeparator separator2 = new JSeparator(SwingConstants.VERTICAL);
		jToolBar.add(separator2);

		// markers是否可见
		visibleTglbtn = new JToggleButton();
		visibleTglbtn.setSelected(true);
		visibleOn = new ImageIcon(getClass().getResource("visible-on.png"));
		visibleOff = new ImageIcon(getClass().getResource("visible-off.png"));
		visibleTglbtn.setIcon(visibleOn);
		visibleTglbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (visibleTglbtn.isSelected()) {
					visibleTglbtn.setIcon(visibleOn);
					mapPanel.setShowMarkers(true);
				} else {
					visibleTglbtn.setIcon(visibleOff);
					mapPanel.setShowMarkers(false);
				}
				mapPanel.repaint();
			}
		});

		jToolBar.add(visibleTglbtn);
		JSeparator separator3 = new JSeparator(SwingConstants.VERTICAL);
		jToolBar.add(separator3);

		// 平移与缩放, pan + zoom
		moveTglbtn = new JToggleButton();
		moveTglbtn.setIcon(new ImageIcon(getClass().getResource("move.png")));
		moveTglbtn.setSelected(true);

		zoomInTglbtn = new JButton();
		zoomInTglbtn.setIcon(new ImageIcon(getClass()
				.getResource("zoom_in.png")));
		zoomInTglbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				mapPanel.setZoom(mapPanel.getZoom() + 1);
				mapPanel.repaint();
			}
		});

		zoomFitTglbtn = new JButton();
		zoomFitTglbtn.setIcon(new ImageIcon(getClass().getResource(
				"zoom_fit.png")));
		zoomFitTglbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				mapPanel.fitMapToMarkers();
				mapPanel.repaint();
			}
		});

		zoomOutTglbtn = new JButton();
		zoomOutTglbtn.setIcon(new ImageIcon(getClass().getResource(
				"zoom_out.png")));
		zoomOutTglbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				mapPanel.setZoom(mapPanel.getZoom() - 1);
				mapPanel.repaint();
			}
		});

		jToolBar.add(moveTglbtn);
		jToolBar.add(zoomInTglbtn);
		jToolBar.add(zoomFitTglbtn);
		jToolBar.add(zoomOutTglbtn);

		ButtonGroup btnGrupZoom = new ButtonGroup();
		btnGrupZoom.add(moveTglbtn);
		btnGrupZoom.add(zoomInTglbtn);
		btnGrupZoom.add(zoomFitTglbtn);
		btnGrupZoom.add(zoomOutTglbtn);

		JSeparator separator4 = new JSeparator(SwingConstants.VERTICAL);
		jToolBar.add(separator4);

		// 选择模式
		selectTglbtn = new JToggleButton();
		selectTglbtn
				.setIcon(new ImageIcon(getClass().getResource("cursor.png")));
		selectTglbtn.setSelected(true);
		selectTglbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				selector.setVisible(!selectTglbtn.isSelected());
			}
		});

		selectRestTgbtn = new JToggleButton();
		selectRestTgbtn.setIcon(new ImageIcon(getClass().getResource(
				"select_restangular.png")));
		selectRestTgbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				selector.setSelectType(SelectType.REST);
				selector.setVisible(true);
			}
		});

		selectEllpTgbtn = new JToggleButton();
		selectEllpTgbtn.setIcon(new ImageIcon(getClass().getResource(
				"select_ellipse.png")));
		selectEllpTgbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				selector.setSelectType(SelectType.ELLIPSE);
				selector.setVisible(true);
			}
		});

		selectLassoTgbtn = new JToggleButton();
		selectLassoTgbtn.setIcon(new ImageIcon(getClass().getResource(
				"select_lasso.png")));
		selectLassoTgbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				selector.setSelectType(SelectType.LASSO);
				selector.setVisible(true);
			}
		});

		jToolBar.add(selectTglbtn);
		jToolBar.add(selectRestTgbtn);
		jToolBar.add(selectEllpTgbtn);
		jToolBar.add(selectLassoTgbtn);

		ButtonGroup btnGrupSelect = new ButtonGroup();
		btnGrupSelect.add(selectTglbtn);
		btnGrupSelect.add(selectRestTgbtn);
		btnGrupSelect.add(selectEllpTgbtn);
		btnGrupSelect.add(selectLassoTgbtn);

		JSeparator separator5 = new JSeparator(SwingConstants.VERTICAL);
		jToolBar.add(separator5);

		// 地图辅助显示, 网格/中心点等
		gridTgbtn = new JToggleButton();
		gridTgbtn.setSelected(true);
		gridTgbtn.setIcon(new ImageIcon(getClass().getResource("grid.png")));
		gridTgbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				mapPanel.setDrawTileBorders(gridTgbtn.isSelected());
			}
		});

		crossTgbtn = new JToggleButton();
		crossTgbtn.setSelected(true);
		crossTgbtn.setIcon(new ImageIcon(getClass().getResource(
				"center_cross.png")));
		crossTgbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				mapPanel.setShowCenterCross(crossTgbtn.isSelected());
			}
		});

		jToolBar.add(gridTgbtn);
		jToolBar.add(crossTgbtn);

		JSeparator separator6 = new JSeparator(SwingConstants.VERTICAL);
		jToolBar.add(separator6);

		// 聚类
		clusterTgbtn = new JToggleButton();
		clusterTgbtn.setIcon(new ImageIcon(getClass()
				.getResource("cluster.png")));
		clusterTgbtn.addActionListener(new ActionListener() {
			List<Overlay> oldMarkers;

			public void actionPerformed(ActionEvent evt) {
				if (clusterTgbtn.isSelected()) {
					oldMarkers = mapPanel.getMarkers();
					clusterer.setMarkers(oldMarkers);
					mapPanel.setMarkers(clusterer.clustering());
					mapPanel.addZoomLevelListener(clusterer);
					mapPanel.repaint();
				} else {
					clusterer.setMarkers(null);
					mapPanel.setMarkers(oldMarkers);
					mapPanel.removeZoomLevelListener(clusterer);
					mapPanel.repaint();
				}
			}
		});

		jToolBar.add(clusterTgbtn);
	}

	@Override
	public void dataSetChanged(DataSetEvent e) {
		dataSet = e.getDataSetForApps();
		attrData = dataSet.getAttrData();
		geoData = dataSet.getGeoData();

		updateMarkerList();
	}

	public void updateMarkerList() {
		HashMap<String, Point> nameGeometrys = geoData.getNameGeometrys();

		mapPanel.clearOverlays();

		indexMarkerMap = new HashMap<Integer, Overlay>();
		markerIndexMap = new HashMap<Overlay, Integer>();

		List<Overlay> markerList = new ArrayList<Overlay>();
		String[] observationNames = dataSet.getObservationNames();
		for (int i = 0; i < observationNames.length; i++) {
			String obs = observationNames[i];
			Point geo = nameGeometrys.get(obs);

			Overlay marker = null;
			switch (markerType) {
			case CIRCLE:
				MapMarker circleMarker = new MapMarker(geo, obs);
				circleMarker.setRadius(5);
				marker = circleMarker;
				break;
			case MARKER:
				marker = new DefaultMapMarker(geo, obs);
				break;
			case ICON:
				marker = new IconMarker(geo, obs);
				break;
			}

			markerList.add(marker);

			indexMarkerMap.put(i, marker);
			markerIndexMap.put(marker, i);
		}

		mapPanel.setMarkers(markerList);

		updateMarkerAppearance();
	}

	public void updateMarkerAppearance() {
		if (colorField != null) {
			for (Integer index : indexMarkerMap.keySet()) {
				MapMarker marker = (MapMarker) indexMarkerMap.get(index);
				marker.setBorder(false);
				marker.setFillColor(new Color(colorField.findColor(index)));
			}
		}

		if (sizeField != null) {
			int min = 5;
			int max = 15;

			for (Integer index : indexMarkerMap.keySet()) {
				MapMarker marker = (MapMarker) indexMarkerMap.get(index);
				int v = (int) (sizeField.findPercent(index) * (max - min) + min);
				marker.setRadius(v);
			}
		}
	}

	protected void updataCtrlPanel() {
		ctrlPanel.colorFieldComp.setValue(colorField);
		ctrlPanel.colorFieldComp.setColor(ColorEnum.BLUE);

		ctrlPanel.sizeFieldcomp.setValue(sizeField);
		ctrlPanel.sizeFieldcomp.setColor(ColorEnum.BLUE);
	}

	@Override
	public void indicationChanged(IndicationEvent e) {
		mapPanel.indicationChanged(e.getIndication());
		repaint();
	}

	@Override
	public void selectionChanged(SelectionEvent e) {
		mapPanel.selectionChanged(e.getSelection());
		repaint();
	}

	@Override
	public SelectionEvent getSelectionEvent() {
		return new SelectionEvent(this, mapPanel.selections);
	}

	public void addIndicationListener(IndicationListener l) {
		mapPanel.addIndicationListener(l);
	}

	public void removeIndicationListener(IndicationListener l) {
		mapPanel.removeIndicationListener(l);
	}

	public void addSelectionListener(SelectionListener l) {
		mapPanel.addSelectionListener(l);
	}

	public void removeSelectionListener(SelectionListener l) {
		mapPanel.removeSelectionListener(l);
	}

	class MapFieldImporter extends FieldImporter<MeasureField> {

		public MapFieldImporter() {
			super(MeasureField.class);
		}

		public boolean importData(TransferHandler.TransferSupport support) {
			getTransferData(support);

			List<MeasureField> values = data.getValues();

			if (values.size() == 1) {
				if (colorField == null)
					colorField = values.get(0);
				else if (sizeField == null)
					sizeField = values.get(0);
				else
					colorField = values.get(0);
			}

			if (values.size() > 1) {
				colorField = values.get(0);
				sizeField = values.get(1);
			}

			updateMarkerAppearance();
			updataCtrlPanel();

			return true;
		}
	}
}
