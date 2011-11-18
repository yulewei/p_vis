package edu.zjut.chart.plot;

import processing.core.PApplet;

public abstract class Plot {

	protected PApplet p;
	protected float x, y, width, height;
	
	public Plot(PApplet p) {
		this.p = p;
	}

	public void size(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public abstract void draw();	
}
