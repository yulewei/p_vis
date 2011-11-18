package edu.zjut.color;

import org.gicentre.utils.colour.ColourTable;
import org.gicentre.utils.colour.PickerListener;

import processing.core.PApplet;

public class Test extends PApplet {

	private ColorSchemePicker colourPicker;
	private ColourListener colourListener;

	ColourTable[] cTables;

	int active = -1;

	public void setup() {
		size(400, 600);

		cTables = new ColourTable[3];
		cTables[0] = ColourTable.getPresetColourTable(ColourTable.YL_OR_RD, 0,
				1);
		cTables[1] = ColourTable.getPresetColourTable(ColourTable.PU_OR, 0, 1);
		cTables[2] = ColourTable.getPresetColourTable(ColourTable.SET1_6);

		colourPicker = new ColorSchemePicker(this);
		colourPicker.setRefColorTable(cTables[0]);
		colourListener = new ColourListener();
		colourPicker.addPickerListener(colourListener);

		smooth();
	}

	public void draw() {
		background(255);

		int x = 50, y = 10, w = 100, h = 20;
		for (int i = 0; i < cTables.length; i++) {
			float yi = y + (h + 10) * i;
			drawColorTable(cTables[i], x, yi, w, h);
		}

		if (!colourPicker.isActive()) {
			// active = -1;
			for (int i = 0; i < cTables.length; i++) {
				float yi = y + (h + 10) * i;
				if (mouseX > x && mouseX < x + w && mouseY > yi
						&& mouseY < yi + h)
					active = i;
			}
		}
		if (active != -1) {
			noFill();
			strokeWeight(3.0f);
			stroke(255, 255, 0, 200);
			rect(x, y + (h + 10) * active, w, h);
			strokeWeight(1.0f);
		}

		colourPicker.draw();
	}

	void drawColorTable(ColourTable cTable, float x, float y, float w, float h) {
		float gap = 3.0f;
		float textlen = 40;

		String name = cTable.getName();
		if (cTable.getIsDiscrete())
			name = name.split("_")[0];

		textSize(10);
		textAlign(CENTER, CENTER);
		fill(80);
		text(name, x + textlen / 2, y + h / 2);

		float bx = x + textlen, by = y, bw = w - textlen, bh = h;

		bx = bx;
		by = by + gap;
		bw = bw - gap;
		bh = bh - 2 * gap;

		stroke(0, 100);
		if (!cTable.getIsDiscrete()) {
			float inc = 0.001f;
			for (float i = 0; i < 1; i += inc) {
				fill(cTable.findColour(i));
				stroke(cTable.findColour(i));
				rect(bx + bw * i, by, bw * inc, bh);
			}

			stroke(0, 100);
			noFill();
			rect(bx, by, bw, bh);
		} else {
			int numColours = cTable.getColourRules().size() - 1;
			for (float i = 0; i < numColours; i++) {
				fill(cTable.findColour(i + 1));
				rect(bx + bw * i / numColours, by, bw / numColours, bh);
			}
		}

		stroke(0, 100);
		noFill();
		rect(x, y, w, h);
	}

	public void mouseClicked() {
		// Êó±êË«»÷, ¼¤»îcolourPicker
		if (mouseEvent.getClickCount() == 2 && active != -1) {
			colourPicker.setActive(!colourPicker.isActive());
			colourPicker.setRefColorTable(cTables[active]);
		}

		// Êó±êÓÒ¼ü, reset
		if (mouseButton == RIGHT) {
			colourPicker.setActive(false);
			active = -1;
		}
	}

	public void keyPressed() {
		if ((key == 'c') || (key == 'C')) {
			colourPicker.setActive(!colourPicker.isActive());
		}
	}

	private class ColourListener implements PickerListener {
		public void colourChosen() {
			ColourTable cTable = colourPicker.activeColorTable;
			if (active != -1 && cTable != null)
				cTables[active] = cTable;
		}
	}
}
