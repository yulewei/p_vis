package edu.zjut.vis.map;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;

import javax.swing.TransferHandler;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import edu.zjut.common.ctrl.FieldDnD;
import edu.zjut.common.data.DataSetForApps;
import edu.zjut.common.data.attr.AttributeData;
import edu.zjut.common.data.attr.MeasureField;
import edu.zjut.common.data.geo.EsriFeatureObj;
import edu.zjut.common.data.geo.GeometryData;
import edu.zjut.common.event.DataSetEvent;
import edu.zjut.common.event.DataSetListener;
import edu.zjut.common.event.IndicationEvent;
import edu.zjut.common.event.IndicationListener;
import edu.zjut.common.event.SelectionEvent;
import edu.zjut.common.event.SelectionListener;
import edu.zjut.map.JMapPanel;
import edu.zjut.map.overlay.EsriLayer;
import edu.zjut.map.overlay.MapMarker;
import edu.zjut.map.overlay.Overlay;

public class GeoMap extends JMapPanel implements DataSetListener,
		IndicationListener, SelectionListener {

	private DataSetForApps dataSet;
	private AttributeData attrData;
	private GeometryData geoData;

	private int indication = -1;
	private int[] selection;

	private HashMap<Integer, Overlay> indexMarkerMap;
	private HashMap<Overlay, Integer> markerIndexMap;

	public GeoMap() {
		this.loadMapConfig("config/map_config.xml");
		this.setTransferHandler(new MapTransferHandler());

		this.setPreferredSize(new Dimension(500, 500));
	}

	@Override
	public void dataSetChanged(DataSetEvent e) {
		dataSet = e.getDataSetForApps();
		attrData = dataSet.getAttrData();
		geoData = dataSet.getGeoData();

		HashMap<String, Geometry> nameGeometrys = geoData.getNameGeometrys();
		indexMarkerMap = new HashMap<Integer, Overlay>();
		markerIndexMap = new HashMap<Overlay, Integer>();

		String[] observationNames = dataSet.getObservationNames();
		for (int i = 0; i < observationNames.length; i++) {
			String obs = observationNames[i];
			Geometry geo = nameGeometrys.get(obs);
			if (geo != null && geo instanceof Point) {
				MapMarker marker = new MapMarker((Point) geo, obs);
				marker.setRadius(5);

				this.addOverlay(marker);

				indexMarkerMap.put(i, marker);
				markerIndexMap.put(marker, i);
			}
		}

		// 添加图层
		List<EsriFeatureObj[]> layers = geoData.getLayers();
		for (EsriFeatureObj[] features : layers) {
			EsriLayer layer = new EsriLayer(features);
			layer.setHighlightBorderWidth(3.0f);
			this.addLayer(layer);
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

	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);

		if (curOverlayIndex != -1)
			fireIndicationChanged(curOverlayIndex);
	}

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

		repaint();
	}

	class MapTransferHandler extends TransferHandler {

		public MapTransferHandler() {
		}

		@SuppressWarnings("unchecked")
		public boolean canImport(TransferHandler.TransferSupport support) {
			if (!support.isDrop()) {
				return false;
			}

			FieldDnD<MeasureField> data;
			try {
				data = (FieldDnD<MeasureField>) support
						.getTransferable()
						.getTransferData(
								new DataFlavor(FieldDnD.class,
										DataFlavor.javaSerializedObjectMimeType));
			} catch (Exception e) {
				System.out.println(e);
				return false;
			}

			return data.getType() == MeasureField.class;
		}

		@SuppressWarnings("unchecked")
		public boolean importData(TransferHandler.TransferSupport support) {
			FieldDnD<MeasureField> data;
			try {
				data = (FieldDnD<MeasureField>) support
						.getTransferable()
						.getTransferData(
								new DataFlavor(FieldDnD.class,
										DataFlavor.javaSerializedObjectMimeType));
			} catch (Exception e) {
				return false;
			}

			List<MeasureField> values = data.getValues();
			MeasureField[] measureFeilds = new MeasureField[values.size()];
			for (int i = 0; i < values.size(); i++)
				measureFeilds[i] = values.get(i);

			buildMarkerAppearance(measureFeilds);

			return true;
		}
	}
}
