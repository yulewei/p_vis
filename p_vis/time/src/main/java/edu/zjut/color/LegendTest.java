package edu.zjut.color;

import org.gicentre.utils.colour.ColourTable;

import processing.core.PApplet;

public class LegendTest extends PApplet {

	ColourTable cTable;
	Legend legend;

	float min, max;
	float curValue;
	float[] values;
	String[] cat;

	int index = 1;

	public void setup() {
		size(600, 600);
		smooth();

		values = new float[5];
		for (int i = 0; i < values.length; i++) {
			values[i] = (float) (Math.random() * 100);
		}

		cat = new String[] { "Apple", "Orange", "Banana", "Other" };

		cTable = ColourTable.getPresetColourTable(ColourTable.SET1_8);
		// cTable = ColourTable.getPresetColourTable(ColourTable.YL_OR_RD, 0,
		// 1);

		// legend = new Legend(cTable, values);
		legend = new Legend(this, cat, cTable);
		legend.size(20, 20, 300, 50);
	}

	public void draw() {
		background(255);
		legend.draw();
	}

	public void keyPressed() {
		if (key == ' ') {
			index++;
			index %= cat.length;

			legend.setIndex(index);
		}
	}
}
