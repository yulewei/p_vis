package edu.zjut.chart.axis;

import processing.core.PApplet;

public abstract class Axis {

	public enum Orientation {
		VERTICAL, HORIZONTAL
	};

	PApplet p;

	float plotX1, plotY1;
	float plotX2, plotY2;

	int intervalMinor = 5;
	int interval = 10;

	// 自适应调整后的结果
	int interMin = 5;
	int inter = 10;

	Orientation orient = Orientation.HORIZONTAL;
	boolean isDrawGrid = true;

	int axisColor = 128;
	int gridColor = 240;

	public Axis(PApplet p) {
		this.p = p;
	}

	public void plotSize(float plotX1, float plotY1, float plotX2, float plotY2) {
		this.plotX1 = plotX1;
		this.plotY1 = plotY1;
		this.plotX2 = plotX2;
		this.plotY2 = plotY2;
	}

	public void setOrientation(Orientation orient) {
		this.orient = orient;
	}

	public void interval(int interval) {
		this.interval = interval;
	}

	public void intervalMinor(int intervalMinor) {
		this.intervalMinor = intervalMinor;
	}

	public void draw() {
		p.fill(0);
		p.textSize(10);
		p.strokeWeight(1);

		if (orient == Orientation.HORIZONTAL)
			adjustIntervalHor();

		if (orient == Orientation.VERTICAL)
			adjustIntervalVer();

		if (isDrawGrid)
			drawGrid();

		if (orient == Orientation.HORIZONTAL)
			drawHor();

		if (orient == Orientation.VERTICAL)
			drawVer();

		p.strokeWeight(1);
		p.stroke(axisColor);
		if (orient == Orientation.HORIZONTAL)
			p.line(plotX1, plotY2, plotX2, plotY2);
		else
			p.line(plotX1, plotY1, plotX1, plotY2);
	}

	protected abstract void adjustIntervalHor();

	protected abstract void adjustIntervalVer();

	protected abstract void drawHor();

	protected abstract void drawVer();

	protected abstract void drawGrid();
	
	public boolean isDrawGrid() {
		return isDrawGrid;
	}

	public void setDrawGrid(boolean isDrawGrid) {
		this.isDrawGrid = isDrawGrid;
	}	
}
