package edu.zjut.chart.plot;

import processing.core.PApplet;

public abstract class Plot {

	protected PApplet p;
	protected int x = -1, y = -1, width = -1, height = -1;
	protected boolean needRedraw = true;

	public Plot(PApplet p) {
		this.p = p;
	}

	public void size(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public abstract void draw();
}
