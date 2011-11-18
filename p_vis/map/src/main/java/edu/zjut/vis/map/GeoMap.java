package edu.zjut.vis.map;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import edu.zjut.common.data.DataSetForApps;
import edu.zjut.common.data.EsriFeatureObj;
import edu.zjut.common.data.GeometryData;
import edu.zjut.common.event.DataSetEvent;
import edu.zjut.common.event.DataSetListener;
import edu.zjut.common.event.IndicationEvent;
import edu.zjut.common.event.IndicationListener;
import edu.zjut.map.JMapPanel;
import edu.zjut.map.overlay.EsriLayer;
import edu.zjut.map.overlay.MapMarker;
import edu.zjut.map.overlay.Overlay;

public class GeoMap extends JMapPanel implements DataSetListener,
		IndicationListener {

	private DataSetForApps dataSet = null;

	private int indication = -1;
	private HashMap<Integer, Overlay> indexMarkerMap;
	private HashMap<Overlay, Integer> markerIndexMap;

	public GeoMap() {
		this.loadMapConfig("config/map_config.xml");
		this.setPreferredSize(new Dimension(500, 500));
	}

	@Override
	public void dataSetChanged(DataSetEvent e) {
		dataSet = e.getDataSetForApps();
		GeometryData geoData = dataSet.getGeoData();

		HashMap<String, Geometry> nameGeometryMap = geoData.nameGeometrys;
		indexMarkerMap = new HashMap<Integer, Overlay>();
		markerIndexMap = new HashMap<Overlay, Integer>();

		String[] observationNames = dataSet.getObservationNames();
		for (int i = 0; i < observationNames.length; i++) {
			String obs = observationNames[i];
			Geometry geo = nameGeometryMap.get(obs);
			if (geo != null && geo instanceof Point) {
				MapMarker marker = new MapMarker((Point) geo, obs);
				marker.setRadius(5);
				this.addOverlay(marker);

				indexMarkerMap.put(i, marker);
				markerIndexMap.put(marker, i);
			}
		}

		// Ìí¼ÓÍ¼²ã
		List<EsriFeatureObj[]> layers = geoData.layers;
		for (EsriFeatureObj[] features : layers) {
			EsriLayer layer = new EsriLayer(features);
			this.addLayer(layer);
		}
	}

	@Override
	public void indicationChanged(IndicationEvent e) {
		Overlay maker = indexMarkerMap.get(indication);
		if (maker != null)
			maker.setHighlighted(false);

		int indication = e.getIndication();
		maker = indexMarkerMap.get(indication);
		if (maker != null)
			maker.setHighlighted(true);
	}
}
