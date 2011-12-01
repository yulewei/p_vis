package edu.zjut.vis.treemap;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.gicentre.apps.hide.Layout;
import org.gicentre.apps.hide.TreemapPanel;
import org.gicentre.apps.hide.TreemapState;
import org.gicentre.data.summary.SummariseField;

import processing.core.PApplet;
import processing.core.PFont;

public class TreemapApplet extends PApplet {
	PFont font;

	TreemapState treemapState;
	TreemapPanel treemapPanel;

	private List<Layout> layouts;
	List<SummariseField> summariseFields;
	List<Object[]> records;

	int oldW, oldH;

	public TreemapApplet() {
		layouts = new ArrayList<Layout>();
		layouts.add(Layout.ST);
		layouts.add(Layout.HZ);
		layouts.add(Layout.VT);
		layouts.add(Layout.SP);
		layouts.add(Layout.SA);
	}

	public void setData(TreemapState treemapState,
			List<SummariseField> summariseFields, List<Object[]> records) {
		this.treemapState = treemapState;
		this.summariseFields = summariseFields;
		this.records = records;
	}

	public void setup() {
		font = createFont("FFScala", 12);

		treemapPanel = new TreemapPanel(this, font, new Rectangle(0, 0, width,
				height), records, summariseFields);
		
		frameRate(30);
	}

	public void draw() {
		if ((oldW != width || oldH != height)) {
			oldW = width;
			oldH = height;
			treemapPanel.setBounds(new Rectangle(0, 0, width, height));
		}

		treemapPanel.draw(treemapState);
	}
}
