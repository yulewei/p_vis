package edu.zjut.common.data;

import java.util.logging.Logger;

import edu.zjut.common.data.attr.AttributeData;
import edu.zjut.common.data.geo.GeometryData;
import edu.zjut.common.data.time.TimeData;

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


	public String getDataSourceName() {
		return dataSourceName;
	}

	public String[] getObservationNames() {
		return attrData.getObservationNames();
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
