package edu.zjut.color;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

import org.gicentre.utils.colour.ColourTable;
import org.gicentre.utils.colour.PickerListener;

import processing.core.PApplet;
import processing.core.PConstants;

public class ColorSchemePicker implements MouseListener, MouseMotionListener,
		KeyListener {

	enum ColorSchemeType {
		Cont, Div, Cat
	};

	private PApplet p;
	private boolean isActive;
	private boolean isClose;

	private Vector<PickerListener> pickerListeners;

	ColourTable[] coloursCont;
	ColourTable[] coloursDiv;
	ColourTable[][] catAll;

	ColourTable activeColorTable;
	ColorSchemeType type;
	ColourTable refColorTable;

	boolean isDiscrete = true;
	private int conUnits = 9;

	private int catIndex = 2;

	int barw = 200, barh = 20;

	int cx, cy;

	public ColorSchemePicker(PApplet p) {
		this(p, 200, 20);
	}

	public ColorSchemePicker(PApplet p, int barw, int barh) {
		this.p = p;

		this.barw = barw;
		this.barh = barh;

		pickerListeners = new Vector<PickerListener>();

		type = ColorSchemeType.Cont;
		coloursCont = ColourUtils.getColoursCont();
		coloursDiv = ColourUtils.getColoursDiv();
		catAll = ColourUtils.getColoursCatAll();
	}

	public void setRefColorTable(ColourTable refColorTable) {
		this.refColorTable = refColorTable;
		type = getColorSchemeType(refColorTable);

		activeColorTable = refColorTable;

		if (type == ColorSchemeType.Cat) {
			catIndex = refColorTable.getColourRules().size() - 4;
		}
	}

	public ColorSchemeType getColorSchemeType(ColourTable cTable) {
		if (cTable.getIsDiscrete())
			return ColorSchemeType.Cat;

		for (int i = 0; i < coloursCont.length; i++) {
			if (cTable.getName().equals(coloursCont[i].getName()))
				return ColorSchemeType.Cont;
		}

		for (int i = 0; i < coloursDiv.length; i++) {
			if (cTable.getName().equals(coloursDiv[i].getName()))
				return ColorSchemeType.Div;
		}

		return ColorSchemeType.Cont;
	}

	public void draw() {
		if (!isActive)
			return;

		ColourTable[] colours = coloursCont;
		String title = "Sequential";
		if (type == ColorSchemeType.Cont) {
			colours = coloursCont;
			title = "Sequential";
		}
		if (type == ColorSchemeType.Div) {
			colours = coloursDiv;
			title = "Diverging";
		}
		if (type == ColorSchemeType.Cat) {
			colours = catAll[catIndex];
			title = "Categorical";
		}

		int th = 25;
		int w = barw + 10, h = barh * colours.length + th + 10;
		int x = (p.width - w) / 2, y = (p.height - h) / 2;
		int barx = x + 5, bary = y + 5 + th;

		p.pushStyle();

		p.fill(200, 80);
		p.stroke(0);
		p.strokeWeight(1.0f);
		p.rect(x, y, w, h);

		// 标题
		p.fill(100);
		p.textAlign(PConstants.CENTER, PConstants.CENTER);
		p.textSize(18);
		p.text(title, x + w / 2, y + th / 2);

		// 关闭按钮
		cx = x + w - 5;
		cy = y + 5;
		p.stroke(0);

		if (isClose) {
			p.fill(150);
			p.rect(cx - 5, cy - 5, 10, 10);
			p.stroke(240);
		}

		int cw = 2;
		p.line(cx - cw, cy - cw, cx + cw, cy + cw);
		p.line(cx - cw, cy + cw, cx + cw, cy - cw);

		drawColours(colours, barx, bary, barw, barh);

		p.popStyle();
	}

	private void drawColours(ColourTable[] colours, float x, float y, float w,
			float h) {
		for (int i = 0; i < colours.length; i++) {
			float yi = y + h * i;
			drawColorTable(colours[i], x, yi, w, h);
		}

		p.noFill();
		p.strokeWeight(3.0f);
		activeColorTable = null;
		for (int i = 0; i < colours.length; i++) {
			float yi = y + h * i;
			if (refColorTable != null
					&& colours[i].getName().equals(refColorTable.getName())) {
				p.stroke(255, 0, 0, 200);
				p.rect(x, yi, w, h);
			}

			if (p.mouseX > x && p.mouseX < x + w && p.mouseY > yi
					&& p.mouseY < yi + h) {
				activeColorTable = colours[i];

				p.stroke(255, 255, 0, 200);
				p.rect(x, yi, w, h);
			}
		}
		p.strokeWeight(1.0f);
	}

	void drawColorTable(ColourTable cTable, float x, float y, float w, float h) {
		p.fill(255);
		p.stroke(0);
		p.strokeWeight(1.0f);
		p.rect(x, y, w, h);

		float textlen = 45;
		float gap = 3.0f;

		String name = cTable.getName();
		if (cTable.getIsDiscrete())
			name = name.split("_")[0];

		p.textSize(10);
		p.textAlign(PConstants.CENTER, PConstants.CENTER);
		p.fill(80);
		p.text(name, x + textlen / 2, y + h / 2);

		float bx = x + textlen, by = y, bw = w - textlen, bh = h;

		// p.fill(200, 100);
		// p.rect(bx, by, bw, bh);

		bx = bx;
		by = by + gap;
		bw = bw - gap;
		bh = bh - 2 * gap;

		p.stroke(0, 100);
		p.strokeWeight(1.0f);
		if (!cTable.getIsDiscrete()) {
			float count = 1000;
			float offset = 0.0f;
			float inc = 1.0f / count;
			if (isDiscrete) {
				count = conUnits;
				inc = 1.0f / count;
				offset = 0.5f * inc;
				p.stroke(0, 100);
			}

			for (int i = 0; i < count; i++) {
				float index = i * inc + offset;
				if (!isDiscrete)
					p.stroke(cTable.findColour(index));
				p.fill(cTable.findColour(index));
				p.rect(bx + bw * i * inc, by, bw * inc, bh);
			}

			p.stroke(0, 100);
			p.noFill();
			p.rect(bx, by, bw, bh);
		} else {
			int numColours = cTable.getColourRules().size() - 1;
			for (float i = 0; i < numColours; i++) {
				p.fill(cTable.findColour(i + 1));
				p.rect(bx + bw * i / numColours, by, bw / numColours, bh);
			}
		}
	}

	public void keyPressed() {

	}

	public void addPickerListener(PickerListener pickerListener) {
		pickerListeners.add(pickerListener);
	}

	public boolean removePickerListener(PickerListener pickerListener) {
		return pickerListeners.remove(pickerListener);
	}

	private void fireColourSelectionEvent() {
		for (PickerListener listener : pickerListeners) {
			listener.colourChosen();
		}
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;

		if (isActive) {
			p.addMouseListener(this);
			p.addMouseMotionListener(this);
			p.addKeyListener(this);
		} else {
			p.removeMouseListener(this);
			p.removeMouseMotionListener(this);
			p.removeKeyListener(this);
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		isClose = false;
		if (p.mouseX > cx - 5 && p.mouseX < cx + 5 && p.mouseY > cy - 5
				&& p.mouseY < cy + 5) {
			isClose = true;
			p.cursor(PConstants.HAND);
		} else {
			p.cursor(PConstants.ARROW);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (activeColorTable != null)
			fireColourSelectionEvent();
		if (isClose) {
			setActive(false);
			p.cursor(PConstants.ARROW);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
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
	public void keyPressed(KeyEvent e) {
		char key = e.getKeyChar();
		int keyCode = e.getKeyCode();

		if (key == 'd')
			isDiscrete = !isDiscrete;

		int min = 0, max = 9;

		if (keyCode == PConstants.LEFT) {
			catIndex--;
			if (catIndex < min)
				catIndex = min;
		}
		if (keyCode == PConstants.RIGHT) {
			catIndex++;
			if (catIndex > max)
				catIndex = max;
		}

		if (keyCode == PConstants.DOWN)
			catIndex = min;

		if (p.keyCode == PConstants.UP)
			catIndex = max;

		// units
		int minUnits = 5, maxUnits = 12;
		if (keyCode == PConstants.LEFT) {
			conUnits--;
			if (conUnits < minUnits)
				conUnits = minUnits;
		}
		if (keyCode == PConstants.RIGHT) {
			conUnits++;
			if (conUnits > maxUnits)
				conUnits = maxUnits;
		}

		if (keyCode == PConstants.DOWN)
			conUnits = minUnits;

		if (keyCode == PConstants.UP)
			conUnits = maxUnits;
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}
}
