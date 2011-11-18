package edu.zjut.common.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import com.thoughtworks.xstream.XStream;

/**
 * 
 * @author yulewei
 * 
 */
public class DataConfig {

	public static XStream getXStreamInstance() {

		XStream xstream = new XStream();
		xstream.alias("dataConfig", DataConfig.class);

		xstream.alias("attr", DataConfig.Attr.class);
		xstream.addImplicitCollection(DataConfig.Attr.class, "attrList");
		xstream.alias("attribute", DataConfig.Attr.Attribute.class);

		xstream.alias("geo", DataConfig.Geo.class);
		xstream.addImplicitCollection(DataConfig.Geo.class, "featureList");
		xstream.alias("feature", DataConfig.Geo.Feature.class);
		xstream.alias("col", DataConfig.Geo.Feature.Col.class);

		xstream.alias("time", DataConfig.Time.class);

		return xstream;
	}

	static class Attr {
		static class Attribute {
			String name;
			int colIdx;
			String dataType;
		}

		String fileName;
		String fileType;

		ArrayList<Attribute> attrList;
	}

	static class Geo {
		static class Feature {
			static class Col {
				int key, x, y;
			}

			String name;
			String geoType;
			String fileName;
			String fileType;
			Col col;
		}

		ArrayList<Feature> featureList;
	}

	static class Time {
		String fileName;
	}

	String datasetName;
	String key;
	String name;
	Attr attr;
	Geo geo;
	Time time;

	public static DataConfig loadConfig(String filename) {
		DataConfig config = null;
		XStream xstream = getXStreamInstance();
		try {
			InputStreamReader in = new InputStreamReader(new FileInputStream(
					filename), "utf-8");
			config = (DataConfig) xstream.fromXML(in);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return config;
	}

	public static void saveConfig(DataConfig config, String filename) {
		XStream xstream = getXStreamInstance();
		String xml = xstream.toXML(config);
		System.out.println(xml);

		try {
			OutputStreamWriter out = new OutputStreamWriter(
					new FileOutputStream(filename), "utf-8");
			xstream.toXML(config, out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		DataConfig config = DataConfig.loadConfig("hz_data/hz_house.xml");

		// DataConfig config = defaultConfig();
		DataConfig.saveConfig(config, "test.xml");
	}
}
