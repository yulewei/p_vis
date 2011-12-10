package edu.zjut.common.io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gicentre.utils.colour.ColourTable;

import au.com.bytecode.opencsv.CSVReader;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import edu.zjut.common.data.DataSetForApps;
import edu.zjut.common.data.attr.AttributeData;
import edu.zjut.common.data.attr.DataField;
import edu.zjut.common.data.attr.DimensionField;
import edu.zjut.common.data.attr.FieldType;
import edu.zjut.common.data.attr.MeasureField;
import edu.zjut.common.data.attr.SummaryType;
import edu.zjut.common.data.geo.EsriFeatureObj;
import edu.zjut.common.data.geo.GeoLayer;
import edu.zjut.common.data.geo.GeometryData;
import edu.zjut.common.data.time.TimeData;
import edu.zjut.common.data.time.TimeSeriesCollection;
import edu.zjut.common.io.DataConfig.Attr;
import edu.zjut.common.io.DataConfig.Attr.Attribute;
import edu.zjut.common.io.DataConfig.ColorMap;
import edu.zjut.common.io.DataConfig.Geo;
import edu.zjut.common.io.DataConfig.Geo.Feature;
import edu.zjut.common.io.DataConfig.Time;
import edu.zjut.common.io.DataConfig.Time.Series;

/**
 * 
 * @author yulewei
 * 
 */
public class DataSetLoader {

	/**
	 * 配置文件
	 */
	protected String configFile;

	protected DataConfig config;

	private AttributeData attrData;
	private GeometryData geoData;
	private TimeData timeData;

	protected DataSetForApps dataForApps;

	public DataSetLoader(String xmlfile) {
		this.configFile = xmlfile;
		this.config = DataConfig.loadConfig(xmlfile);

		readAttributeData(config.attr);
		readGeometryData(config.geo);
		readTimeData(config.time);

		dataForApps = new DataSetForApps(attrData, geoData, timeData);
	}

	/**
	 * 属性数据
	 * 
	 * @param config
	 */
	public void readAttributeData(Attr attrConfig) {
		ArrayList<Attribute> attrList = attrConfig.attrList;

		int keyCol = 0;
		int nameCol = 1;

		int len = attrList.size();

		DataField[] feilds = new DataField[len];

		FieldType[] dataTypes = new FieldType[len];
		String[] attributeNames = new String[len];
		SummaryType[] summaryTypes = new SummaryType[len];
		ColourTable[] colorTables = new ColourTable[len];
		for (int i = 0; i < len; i++) {
			Attribute attr = attrList.get(i);
			dataTypes[i] = FieldType.valueOf(attr.dataType.toUpperCase());
			attributeNames[i] = attr.name;
			summaryTypes[i] = attr.summaryType == null ? null : SummaryType
					.valueOf(attr.summaryType.toUpperCase());
			colorTables[i] = (attr.colorMap == null ? null
					: parseColorMap(attr.colorMap));

			if (attr.name.equals(attrConfig.name))
				nameCol = i;
		}

		Object[][] columnArrays = readFileContent(dataTypes, attrList,
				attrConfig.fileName, 1);

		for (int i = 0; i < len; i++) {
			FieldType attrType = dataTypes[i];
			if (attrType.isDimensionType()) {
				feilds[i] = new DimensionField(i, attributeNames[i], attrType,
						columnArrays[i], nameCol == i);
			}
			if (attrType.isMeasureType()) {
				feilds[i] = new MeasureField(i, attributeNames[i], attrType,
						columnArrays[i], summaryTypes[i], colorTables[i]);
			}
		}
		this.attrData = new AttributeData(feilds, columnArrays);
	}

	/**
	 * 几何数据
	 * 
	 * @param config
	 */
	public void readGeometryData(Geo geoConfig) {
		HashMap<String, Point> nameGeometrys = new HashMap<String, Point>();
		List<GeoLayer> layers = new ArrayList<GeoLayer>();

		ArrayList<Feature> featureList = geoConfig.featureList;

		for (Feature feature : featureList) {
			if (feature.fileType.equalsIgnoreCase("csv")) {
				try {
					loadGeoCSV(nameGeometrys, feature);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (feature.fileType.equalsIgnoreCase("geojson")) {
				if (feature.geoType.equals("layers")) {
					EsriLayersParser parser = new EsriLayersParser(
							feature.fileName);
					List<String> layerIdLists = parser.getLayerIdLists();
					List<String> layerNameLists = parser.getLayerNameLists();
					for (int i = 0; i < layerIdLists.size(); i++) {
						String id = layerIdLists.get(i);
						String layerName = layerNameLists.get(i);
						String fileName = feature.subDir + "/" + id + ".json";

						File file = new File(fileName);
						if (file.exists()) {
							EsriJSONParser layerParser = new EsriJSONParser(
									fileName);
							EsriFeatureObj[] features = layerParser
									.getFeatures();
							layers.add(new GeoLayer(layerName, features));
						}
					}
				} else {
					EsriJSONParser layerParser = new EsriJSONParser(
							feature.fileName);
					EsriFeatureObj[] features = layerParser.getFeatures();
					layers.add(new GeoLayer(feature.name, features));
				}
			}
		}

		this.geoData = new GeometryData(nameGeometrys, layers);
	}

	/**
	 * 时间数据
	 * 
	 * @param config
	 */
	public void readTimeData(Time timeConfig) {
		List<TimeSeriesCollection> seriesList = new ArrayList<>();

		ArrayList<Series> configSeriesList = timeConfig.seriesList;
		for (Series series : configSeriesList) {
			TimeSeriesCollection timeSeries = null;
			try {
				timeSeries = TimeSeriesLoader.loadDataSet(series.fileName,
						series.dateCol, series.groupCol, series.valueCol, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			seriesList.add(timeSeries);
		}

		this.timeData = new TimeData(seriesList);
	}

	/**
	 * 只读取配置文件指定的列
	 * 
	 * @param attrList
	 * @param fileName
	 */
	private Object[][] readFileContent(FieldType[] dataTypes,
			ArrayList<Attribute> attrList, String fileName, int beg) {

		Object[][] columnArrays = new Object[attrList.size()][];

		List<String[]> fileContent = null;

		try {
			CSVReader reader = new CSVReader(new FileReader(fileName));
			fileContent = reader.readAll();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		int len = fileContent.size() - beg;

		// 初始化columnArrays
		for (int i = 0; i < dataTypes.length; i++) {
			switch (dataTypes[i]) {
			case ID:
			case INT:
				columnArrays[i] = new Integer[len];
				break;
			case DOUBLE:
				columnArrays[i] = new Double[len];
				break;
			case STRING:
				columnArrays[i] = new String[len];
				break;
			}
		}

		for (int row = beg; row < len + beg; row++) {
			String[] line = fileContent.get(row);

			Integer[] ints = null;
			Double[] doubles = null;
			String[] strings = null;

			for (int i = 0; i < attrList.size(); i++) {
				Attribute attr = attrList.get(i);
				int col = attr.colIdx;
				String item = line[col - 1];
				switch (dataTypes[i]) {
				case ID:
				case INT:
					ints = (Integer[]) columnArrays[i];
					ints[row - beg] = Integer.parseInt(item);
					break;
				case DOUBLE:
					doubles = (Double[]) columnArrays[i];
					doubles[row - beg] = item.equals("") ? 0 : Double
							.parseDouble(item);
					break;
				case STRING:
					strings = (String[]) columnArrays[i];
					strings[row - beg] = item;
					break;
				}
			}
		}

		return columnArrays;
	}

	private ColourTable parseColorMap(ColorMap colorMap) {
		ColourTable colourTable = null;

		if (colorMap.preset != null) {
			int type = 0;
			try {
				type = ColourTable.class.getField(colorMap.preset).getInt(
						ColourTable.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			colourTable = ColourTable.getPresetColourTable(type);
		} else {
			colourTable = new ColourTable();
		}

		return colourTable;
	}

	private void loadGeoCSV(HashMap<String, Point> nameGeometrys,
			Feature feature) throws IOException {
		GeometryFactory geometryFactory = new GeometryFactory();

		CSVReader reader = new CSVReader(new FileReader(feature.fileName));
		List<String[]> fileContent = reader.readAll();

		for (int i = 0; i < fileContent.size(); i++) {
			String[] line = fileContent.get(i);
			String name = line[feature.col.key - 1];
			String xs = line[feature.col.x - 1];
			String ys = line[feature.col.y - 1];
			double x = Double.parseDouble(xs);
			double y = Double.parseDouble(ys);
			Coordinate coord = new Coordinate(y, x);
			Point geometry = geometryFactory.createPoint(coord);
			nameGeometrys.put(name, geometry);
		}
	}

	public void setDataForApps(DataSetForApps dataForApps) {
		this.dataForApps = dataForApps;
	}

	public DataSetForApps getDataForApps() {
		return dataForApps;
	}

	public String getFileName() {
		return configFile;
	}
}
