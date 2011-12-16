package edu.zjut.treemap.io;

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
		xstream.aliasField("allVariables", DataConfig.class, "varList");
		xstream.aliasField("allSummarys", DataConfig.class, "sumList");

		// xstream.addImplicitCollection(DataConfig.class, "list");
		// xstream.addImplicitCollection(DataConfig.class, "varList");
		// xstream.addImplicitCollection(DataConfig.class, "sumList");

		xstream.alias("hideData", DataConfig.class);
		xstream.alias("variable", DataConfig.Variable.class);

		xstream.alias("hier", DataConfig.Variable.Hier.class);
		xstream.useAttributeFor(DataConfig.Variable.Hier.class, "order");
		xstream.useAttributeFor(DataConfig.Variable.Hier.class, "useAllValues");

		xstream.alias("values", DataConfig.Variable.Values.class);
		xstream.alias("item", DataConfig.Variable.Values.Item.class);
		xstream.addImplicitCollection(DataConfig.Variable.Values.class, "items");
		xstream.useAttributeFor(DataConfig.Variable.Values.Item.class, "value");
		xstream.useAttributeFor(DataConfig.Variable.Values.Item.class, "order");

		xstream.alias("summary", DataConfig.Variable.Summary.class);

		xstream.alias("colorMap", DataConfig.ColorMap.class);
		xstream.useAttributeFor(DataConfig.ColorMap.class, "preset");
		xstream.useAttributeFor(DataConfig.ColorMap.class, "scaling");
		xstream.alias("rule", DataConfig.ColorMap.Rule.class);
		xstream.addImplicitCollection(DataConfig.ColorMap.class, "rules");
		xstream.useAttributeFor(DataConfig.ColorMap.Rule.class, "numValue");
		xstream.useAttributeFor(DataConfig.ColorMap.Rule.class, "color");

		return xstream;
	}

	static class ColorMap {
		static class Rule {
			int numValue;
			String color;
		}

		ArrayList<Rule> rules;
		String preset;
		String scaling;
	}

	static class Variable {
		static class Hier {
			int order;
			Boolean useAllValues;
		}

		static class Summary {
			String summaryType;
			ColorMap colorMap;

			String order;
			String size;
			String colour;
		}

		static class Values {
			static class Item {
				String value;
				int order;
			}

			ArrayList<Item> items;
		}

		String name;
		int colIdx;
		String dataType;
		Hier hier;
		Values values;
		ColorMap colorMap;		
		Summary summary;
	}

	String fileName;
	String fileType;
	Boolean ignoreFirstLine;
	String datasetName;

	ArrayList<Variable> varList;

	public static DataConfig defaultConfig() {
		DataConfig config = new DataConfig();
		config.datasetName = "uselection";

		return null;
	}

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
		DataConfig config = DataConfig.loadConfig("data/soufan_top100.xml");

		System.out.println(config.datasetName);
		// System.out.println(var.hier.order);

		// DataConfig config = new DataConfig();
		// DataConfig.saveConfig(config, "test.xml");
	}
}
