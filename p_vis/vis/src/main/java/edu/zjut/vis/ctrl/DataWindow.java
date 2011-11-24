package edu.zjut.vis.ctrl;

import java.util.Arrays;

import javax.swing.JFrame;

import edu.zjut.common.data.DataSetBroadcaster;
import edu.zjut.common.data.DataSetForApps;
import edu.zjut.common.data.attr.AttributeData;
import edu.zjut.common.event.DataSetEvent;
import edu.zjut.common.event.DataSetListener;
import edu.zjut.common.io.DataSetLoader;
import edu.zjut.coordination.CoordinationManager;
import processing.core.PApplet;
import processing.core.PFont;

public class DataWindow extends PApplet implements DataSetListener {
	DataSetForApps dataSet;
	AttributeData attrData;
	String[] measureNames;
	String[] dimensionNames;

	public void setup() {
		size(500, 500);

		PFont font = createFont("FFScala", 12);
		textFont(font);
		smooth();
	}

	// Draws the colour tables as horizontal colour bars.
	public void draw() {
		background(255);

		float x = 10, y = 10, w = 150, h = 180;
		drawList("Dimensions", dimensionNames, x, y, w, h);
		drawList("Measures", measureNames, x, y + h, w, h);
	}

	protected void drawList(String title, String[] names, float x, float y,
			float w, float h) {

		noFill();
		rect(x, y, w, h);

		float ih = 20;
		fill(200, 100);
		rect(x, y, w, ih);
		textAlign(LEFT, CENTER);
		fill(80);
		text(title, x + 10, y + ih / 2);

		float ly = y + ih;
		for (int i = 0; i < names.length; i++) {
			noFill();
			rect(x, ly + (i * ih), w, ih);
			fill(80);
			text(names[i], x + 10, ly + (i * ih) + ih / 2);
		}
	}

	@Override
	public void dataSetChanged(DataSetEvent e) {
		dataSet = e.getDataSetForApps();
		attrData = dataSet.getAttrData();
		dimensionNames = attrData.getDimensionNames();
		measureNames = attrData.getMeasureNames();
		System.out.println(Arrays.toString(dimensionNames));
		System.out.println(Arrays.toString(measureNames));
	}

	public static void main(String[] args) {
		String fileName = "hz_data/hz_house.xml";
		DataSetLoader loader = new DataSetLoader(fileName);
		DataSetForApps dataSet = loader.getDataForApps();

		CoordinationManager coord = new CoordinationManager();
		DataSetBroadcaster dataCaster = new DataSetBroadcaster();

		DataWindow dataWin = new DataWindow();
		coord.addBean(dataWin);
		coord.addBean(dataCaster);

		dataCaster.setAndFireDataSet(dataSet);

		JFrame jframe = new JFrame();
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setLocation(100, 100);
		jframe.setSize(300, 500);
		jframe.add(dataWin);
		dataWin.init();
		jframe.setVisible(true);
	}
}
