package edu.zjut.vis.map;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
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
import edu.zjut.map.overlay.DefaultMapMarker;
import edu.zjut.map.overlay.MapMarker;
import edu.zjut.map.overlay.Overlay;

public class GeoMap extends JPanel implements DataSetListener,
		IndicationListener, SelectionListener {

	private DataSetForApps dataSet;
	private AttributeData attrData;
	private GeometryData geoData;

	private int indication = -1;
	private int[] selection;

	private enum MarkerType {
		circle, marker
	};

	MarkerType markerType = MarkerType.marker;

	private JToolBar jToolBar;
	private JToggleButton sideBarTglbtn;
	private JToggleButton circleTglbtn;
	private JToggleButton markerTglbtn;

	private JToggleButton moveTglbtn;
	private JToggleButton zoomInTglbtn;
	private JToggleButton zoomOutTglbtn;
	private JToggleButton selectTglbtn;
	private JToggleButton selectRestTgbtn;
	private JToggleButton selectEllpTgbtn;
	private JToggleButton selectLassoTgbtn;
	private JToggleButton gridTgbtn;
	private JToggleButton crossTgbtn;

	private JSplitPane jSplitPane;
	private MapCtrlPanel ctrlPanel;
	private JMapPanel mapPanel;

	private HashMap<Integer, Overlay> indexMarkerMap;
	private HashMap<Overlay, Integer> markerIndexMap;

	public GeoMap() {
		this.setLayout(new BorderLayout());

		initToolbar();
		this.add(jToolBar, BorderLayout.NORTH);

		jSplitPane = new JSplitPane();
		jSplitPane.setOneTouchExpandable(true);
		this.add(jSplitPane, BorderLayout.CENTER);
		this.setTransferHandler(new MapFieldImporter());

		mapPanel = new JMapPanel();
		mapPanel.loadMapConfig("config/map_config.xml");
		jSplitPane.add(mapPanel, JSplitPane.RIGHT);

		ctrlPanel = new MapCtrlPanel(mapPanel);
		jSplitPane.add(ctrlPanel, JSplitPane.LEFT);

		jSplitPane.setDividerLocation(150);

		this.setPreferredSize(new Dimension(500, 500));
	}

	private void initToolbar() {
		jToolBar = new JToolBar();

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

		circleTglbtn = new JToggleButton();
		circleTglbtn.setSelected(markerType == MarkerType.circle);
		circleTglbtn
				.setIcon(new ImageIcon(getClass().getResource("circle.png")));
		circleTglbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (circleTglbtn.isSelected()) {
					markerType = MarkerType.circle;
					updateMarkerList();
					mapPanel.repaint();
				}
			}
		});

		markerTglbtn = new JToggleButton();
		markerTglbtn.setSelected(markerType == MarkerType.marker);
		markerTglbtn
				.setIcon(new ImageIcon(getClass().getResource("marker.png")));
		markerTglbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (markerTglbtn.isSelected()) {
					markerType = MarkerType.marker;
					updateMarkerList();
					mapPanel.repaint();
				}
			}
		});

		jToolBar.add(circleTglbtn);
		jToolBar.add(markerTglbtn);

		ButtonGroup btnGrupIcon = new ButtonGroup();
		btnGrupIcon.add(circleTglbtn);
		btnGrupIcon.add(markerTglbtn);

		JSeparator separator2 = new JSeparator(SwingConstants.VERTICAL);
		jToolBar.add(separator2);

		moveTglbtn = new JToggleButton();
		moveTglbtn.setIcon(new ImageIcon(getClass().getResource("move.png")));
		moveTglbtn.setSelected(true);

		zoomInTglbtn = new JToggleButton();
		zoomInTglbtn.setIcon(new ImageIcon(getClass()
				.getResource("zoom_in.png")));

		zoomOutTglbtn = new JToggleButton();
		zoomOutTglbtn.setIcon(new ImageIcon(getClass().getResource(
				"zoom_out.png")));

		jToolBar.add(moveTglbtn);
		jToolBar.add(zoomInTglbtn);
		jToolBar.add(zoomOutTglbtn);

		ButtonGroup btnGrupZoom = new ButtonGroup();
		btnGrupZoom.add(moveTglbtn);
		btnGrupZoom.add(zoomInTglbtn);
		btnGrupZoom.add(zoomOutTglbtn);

		JSeparator separator3 = new JSeparator(SwingConstants.VERTICAL);
		jToolBar.add(separator3);

		selectTglbtn = new JToggleButton();
		selectTglbtn
				.setIcon(new ImageIcon(getClass().getResource("cursor.png")));
		selectTglbtn.setSelected(true);

		selectRestTgbtn = new JToggleButton();
		selectRestTgbtn.setIcon(new ImageIcon(getClass().getResource(
				"select_restangular.png")));

		selectEllpTgbtn = new JToggleButton();
		selectEllpTgbtn.setIcon(new ImageIcon(getClass().getResource(
				"select_ellipse.png")));

		selectLassoTgbtn = new JToggleButton();
		selectLassoTgbtn.setIcon(new ImageIcon(getClass().getResource(
				"select_lasso.png")));

		jToolBar.add(selectTglbtn);
		jToolBar.add(selectRestTgbtn);
		jToolBar.add(selectEllpTgbtn);
		jToolBar.add(selectLassoTgbtn);

		ButtonGroup btnGrupSelect = new ButtonGroup();
		btnGrupSelect.add(selectTglbtn);
		btnGrupSelect.add(selectRestTgbtn);
		btnGrupSelect.add(selectEllpTgbtn);
		btnGrupSelect.add(selectLassoTgbtn);

		JSeparator separator4 = new JSeparator(SwingConstants.VERTICAL);
		jToolBar.add(separator4);

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
	}

	@Override
	public void dataSetChanged(DataSetEvent e) {
		dataSet = e.getDataSetForApps();
		attrData = dataSet.getAttrData();
		geoData = dataSet.getGeoData();

		updateMarkerList();

		// // 添加图层
		// List<GeoLayer> layers = geoData.getLayers();
		// for (GeoLayer geoLayer : layers) {
		// EsriLayer layer = new EsriLayer(geoLayer.getFeatures());
		// layer.setHighlightBorderWidth(3.0f);
		// mapPanel.addLayer(layer);
		// }
	}

	private void updateMarkerList() {
		HashMap<String, Point> nameGeometrys = geoData.getNameGeometrys();

		mapPanel.clearOverlays();

		indexMarkerMap = new HashMap<Integer, Overlay>();
		markerIndexMap = new HashMap<Overlay, Integer>();

		String[] observationNames = dataSet.getObservationNames();
		for (int i = 0; i < observationNames.length; i++) {
			String obs = observationNames[i];
			Point geo = nameGeometrys.get(obs);

			Overlay marker = null;
			switch (markerType) {
			case circle:
				MapMarker circleMarker = new MapMarker((Point) geo, obs);
				circleMarker.setRadius(5);
				marker = circleMarker;
				break;
			case marker:
				marker = new DefaultMapMarker((Point) geo, obs);
				break;
			}

			mapPanel.addOverlay(marker);

			indexMarkerMap.put(i, marker);
			markerIndexMap.put(marker, i);
		}
	}

	@Override
	public void indicationChanged(IndicationEvent e) {
		Overlay maker = indexMarkerMap.get(indication);
		if (maker != null)
			maker.setHighlighted(false);

		indication = e.getIndication();
		maker = indexMarkerMap.get(indication);
		if (maker != null)
			maker.setHighlighted(true);

		repaint();
	}

	@Override
	public void selectionChanged(SelectionEvent e) {

		// 清空原先高亮
		if (selection != null) {
			for (int index : selection) {
				Overlay maker = indexMarkerMap.get(index);
				if (maker != null)
					maker.setHighlighted(false);
			}
		}

		selection = e.getSelection();

		for (int index : selection) {
			Overlay maker = indexMarkerMap.get(index);
			if (maker != null)
				maker.setHighlighted(true);
		}

		repaint();
	}

	@Override
	public SelectionEvent getSelectionEvent() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addIndicationListener(IndicationListener l) {
		listenerList.add(IndicationListener.class, l);
	}

	public void removeIndicationListener(IndicationListener l) {
		listenerList.remove(IndicationListener.class, l);
	}

	// public void mouseMoved(MouseEvent e) {
	// super.mouseMoved(e);
	//
	// if (curOverlayIndex != -1)
	// fireIndicationChanged(curOverlayIndex);
	// }

	public void fireIndicationChanged(int newIndication) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		IndicationEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == IndicationListener.class) {
				// Lazily create the event:
				if (e == null) {
					e = new IndicationEvent(this, newIndication);
				}
				((IndicationListener) listeners[i + 1]).indicationChanged(e);
			}
		}// next i
	}

	public void buildMarkerAppearance(MeasureField[] measureFeilds) {
		MeasureField colorField = measureFeilds[0];

		for (Integer index : indexMarkerMap.keySet()) {
			MapMarker marker = (MapMarker) indexMarkerMap.get(index);
			marker.setBorder(false);
			marker.setFillColor(new Color(colorField.findColor(index)));
		}

		ctrlPanel.colorFieldComp.setText(colorField.getName());
		ctrlPanel.colorFieldComp.setColor(ColorEnum.BLUE);

		repaint();
	}

	class MapFieldImporter extends FieldImporter<MeasureField> {

		public MapFieldImporter() {
			super(MeasureField.class);
		}

		public boolean importData(TransferHandler.TransferSupport support) {
			getTransferData(support);

			List<MeasureField> values = data.getValues();
			MeasureField[] measureFeilds = new MeasureField[values.size()];
			for (int i = 0; i < values.size(); i++)
				measureFeilds[i] = values.get(i);

			buildMarkerAppearance(measureFeilds);

			return true;
		}
	}
}
