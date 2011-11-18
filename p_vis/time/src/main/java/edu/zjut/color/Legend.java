package edu.zjut.color;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import org.gicentre.utils.colour.ColourTable;
import org.gicentre.utils.colour.PickerListener;

import processing.core.PApplet;
import processing.core.PConstants;

public class Legend implements MouseListener {

	PApplet p;
	float x, y, w, h;

	ColourTable cTable;

	float min, max;
	float curValue;
	float[] values;
	String[] cat;

	boolean isActive = false;
	int index = 1;

	private ColorSchemePicker colourPicker;
	private ColourListener colourListener;
	private Vector<LegendListener> legendListeners;

	public Legend(PApplet p, float[] values, ColourTable cTable) {
		this.p = p;
		this.values = values;
		this.cTable = cTable;

		min = values[0];
		max = values[0];
		for (int i = 0; i < values.length; i++) {
			if (values[i] < min)
				min = values[i];
			if (values[i] > max)
				max = values[i];
		}

		initColorPicker();
	}

	public Legend(PApplet p, String[] cat, ColourTable cTable) {
		this.p = p;
		this.cat = cat;
		this.cTable = cTable;

		initColorPicker();
	}

	private void initColorPicker() {
		p.addMouseListener(this);

		legendListeners = new Vector<LegendListener>();

		colourPicker = new ColorSchemePicker(p);
		colourPicker.setRefColorTable(cTable);

		colourListener = new ColourListener();
		colourPicker.addPickerListener(colourListener);
	}

	public void size(float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public void draw() {		
		p.pushStyle();

		p.stroke(0, 100);
		p.strokeJoin(PConstants.ROUND);
		p.noFill();
		p.textSize(12);

		float gap = 3.0f;
		float th = 15, ty = y + h;
		float ah = 5, ay = y + h - th - ah + 2;

		float bx = x, by = y, bw = w, bh = h - th - ah;

		// rect(bx, by, bw, bh);
		bx = bx + gap;
		by = by + gap;
		bw = bw - 2 * gap;
		bh = bh - gap;

		p.rect(x, y, w, h);
		// rect(x, ty - th, w, th);

		p.stroke(0, 100);
		if (!cTable.getIsDiscrete()) {
			float inc = 0.001f;
			for (float i = 0; i < 1; i += inc) {
				p.fill(cTable.findColour(i));
				p.stroke(cTable.findColour(i));
				p.rect(bx + bw * i, by, bw * inc, bh);
			}

			p.stroke(0, 100);
			p.noFill();
			p.rect(bx, by, bw, bh);

			p.fill(80);
			p.textAlign(PConstants.LEFT, PConstants.BOTTOM);
			p.text(min, x, ty);
			p.textAlign(PConstants.RIGHT, PConstants.BOTTOM);
			p.text(max, x + w, ty);

			float ax = PApplet.map(values[index], min, max, bx, bx + bw);
			if (index != -1) {
				p.strokeWeight(1.5f);
				p.line(ax, ay, ax + ah - 1, ay + ah - 1);
				p.line(ax, ay, ax - ah + 1, ay + ah - 1);
				p.strokeWeight(1.0f);
			}

		} else {

			float colorGap = 3.0f;
			p.textAlign(PConstants.CENTER, PConstants.BOTTOM);
			int numColours = cat.length;
			for (int i = 0; i < numColours; i++) {
				p.fill(cTable.findColour(i + 1));

				float rx = bx + bw * i / numColours, rw = bw / numColours;
				p.rect(rx + colorGap, by, bw / numColours - 2 * colorGap, bh);

				float rc = rx + rw / 2;

				if (index == i) {
					p.strokeWeight(1.5f);
					p.line(rc, ay, rc + ah - 1, ay + ah - 1);
					p.line(rc, ay, rc - ah + 1, ay + ah - 1);
					p.strokeWeight(1.0f);
				}

				p.fill(80);
				p.text(cat[i], rc, ty);
			}
		}

		if (isActive) {
			p.noFill();
			p.strokeWeight(3.0f);
			p.stroke(255, 255, 0, 200);
			p.rect(x, y, w, h);
			p.strokeWeight(1.0f);
		}

		colourPicker.draw();
		
		p.popStyle();
	}

	public ColourTable getColorTable() {
		return cTable;
	}

	public void setColorTable(ColourTable cTable) {
		this.cTable = cTable;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public float getCurValue() {
		return curValue;
	}

	public void setCurValue(float curValue) {
		this.curValue = curValue;
	}

	private class ColourListener implements PickerListener {
		public void colourChosen() {
			cTable = colourPicker.activeColorTable;
			fireColorChangedEvent();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// Êó±êË«»÷, ¼¤»îcolourPicker
		int mx = e.getX(), my = e.getY();
		if (e.getClickCount() == 2 && mx > x && mx < x + w & my > y
				&& my < y + h) {
			isActive = true;
			colourPicker.setActive(true);
			colourPicker.setRefColorTable(cTable);
		}

		// Êó±êÓÒ¼ü, reset
		if (e.getButton() == MouseEvent.BUTTON3) {
			colourPicker.setActive(false);
			isActive = false;
		}
	}

	public void addLegendListener(LegendListener legendListener) {
		legendListeners.add(legendListener);
	}

	public boolean removeLegendListener(LegendListener legendListener) {
		return legendListeners.remove(legendListener);
	}

	private void fireColorChangedEvent() {
		for (LegendListener listener : legendListeners) {
			listener.colorChanged();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
