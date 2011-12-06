package edu.zjut.common.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * <p>
 * ArcGIS Map Service下的全部Layers, 解析JSON返回结果．
 * </p>
 * 
 * <p>
 * 参考: <a href=
 * "http://help.arcgis.com/en/arcgisserver/10.0/apis/rest/index.html?mapserver.html"
 * >ArcGIS Server REST API: Map Service</a>, 见JSON Response Syntax部分
 * </p>
 * 
 * @author yulewei
 * 
 */
public class EsriLayersParser {

	private List<String> layerIdLists;
	private List<String> layerNameLists;

	public EsriLayersParser(String fileName) {
		try {
			parse(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void parse(String fileName) throws IOException, JSONException {
		layerIdLists = new ArrayList<String>();
		layerNameLists = new ArrayList<String>();

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(fileName), "UTF-8"));

		JSONObject jsonobject = new JSONObject(reader.readLine());

		JSONArray jsonfeatures = (JSONArray) jsonobject.get("layers");

		int len = jsonfeatures.length();
		for (int i = 0; i < len; i++) {
			JSONObject object = (JSONObject) jsonfeatures.get(i);
			String id = ((Integer) object.get("id")).toString();
			String name = (String) object.get("name");

			layerIdLists.add(id);
			layerNameLists.add(name);
		}
	}

	public List<String> getLayerIdLists() {
		return layerIdLists;
	}

	public List<String> getLayerNameLists() {
		return layerNameLists;
	}
}
