package edu.zjut.common.data;

import java.util.logging.Logger;

/**
 * 
 * @author yulewei
 */
public class DataSetForApps {

	private String dataSourceName;

	private AttributeData attrData;
	private GeometryData geoData;
	private TimeData timeData;

	public static int NULL_INT_VALUE = Integer.MIN_VALUE;

	final static Logger logger = Logger.getLogger(DataSetForApps.class
			.getName());

	public DataSetForApps(AttributeData attrData, GeometryData geoData,
			TimeData timeData) {
		this.attrData = attrData;
		this.geoData = geoData;
		this.timeData = timeData;
	}

	public DataSetForApps(String[] varNames, Object[] data, int nameCol) {
		attrData = new AttributeData(varNames, data, nameCol);
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public String[] getObservationNames() {
		return attrData.observationNames;
	}

	public AttributeData getAttrData() {
		return attrData;
	}

	public GeometryData getGeoData() {
		return geoData;
	}

	public TimeData getTimeData() {
		return timeData;
	}
}
