package edu.zjut.color;

import org.gicentre.utils.colour.ColourTable;

import processing.core.PApplet;

public class ColorListBox extends PApplet {

	private ColourTable[] coloursCont, coloursDiv, coloursCat;

	boolean isExpansed = false;
	ColourTable activeColorTable;

	ColourTable cTable;

	int x = 50, y = 20, w = 300, h = 20;

	public void setup() {
		size(800, 800);

		coloursCont = ColourUtils.getColoursCont();
		coloursDiv = ColourUtils.getColoursDiv();
		coloursCat = ColourUtils.getColoursCat();

		cTable = ColourTable.getPresetColourTable(ColourTable.YL_OR_RD, 0, 1);

		smooth();
	}

	// Draws the colour tables as horizontal colour bars.
	public void draw() {
		background(255);

		float bx = x + w - 10, by = y, bw = 10, bh = h;
		drawBotton(isExpansed, bx, by, bw, bh);

		float barw = w - 12;
		if (!isExpansed)
			drawColorTable(cTable, x, y, barw, h);
		if (isExpansed) {
			if (!cTable.getIsDiscrete())
				drawColours(coloursCont, x, y, barw, h);
			if (cTable.getIsDiscrete())
				drawColours(coloursCat, x, y, barw, h);
		}
	}

	private void drawColours(ColourTable[] colours, float x, float y, float w,
			float h) {
		for (int i = 0; i < colours.length; i++) {
			float yi = y + (h + 2) * i;
			drawColorTable(colours[i], x, yi, w, h);
		}
	}

	private void drawBotton(boolean isActive, float x, float y, float w, float h) {
		stroke(10);

		if (isActive)
			fill(200, 100);
		else
			noFill();

		rect(x, y, w, h);

		float cx = x + w / 2, cy = y + h / 2;
		line(cx - 2, cy - 4, cx, cy);
		line(cx + 2, cy - 4, cx, cy);
		cy = cy + 4;
		line(cx - 2, cy - 4, cx, cy);
		line(cx + 2, cy - 4, cx, cy);
	}

	void drawColorTable(ColourTable cTable, float x, float y, float w, float h) {
		float textlen = 40;

		textSize(10);
		textAlign(CENTER, CENTER);
		fill(80);
		text(cTable.getName(), x + textlen / 2, y + h / 2);

		float bx = x + textlen, by = y, bw = w - textlen, bh = h;
		stroke(0, 100);
		strokeWeight(1.0f);
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

		noFill();
		stroke(0);
		strokeWeight(1.0f);
		rect(x, y, w, h);

		strokeWeight(2.5f);

		if (isExpansed && cTable.getName().equals(this.cTable.getName())) {
			stroke(255, 0, 0, 200);
			rect(x, y, w, h);
		}

		if (isExpansed && mouseX > x && mouseX < x + w && mouseY > y
				&& mouseY < y + h) {
			activeColorTable = cTable;

			stroke(255, 255, 0, 200);
			rect(x, y, w, h);
		}

		strokeWeight(1.0f);
	}

	public void mouseClicked() {
		if (isExpansed && activeColorTable != null) {
			cTable = activeColorTable;
			isExpansed = false;
			return;
		}

		if (mouseX > x && mouseX < x + w && mouseY > y && mouseY < y + h)
			isExpansed = !isExpansed;
	}
}
