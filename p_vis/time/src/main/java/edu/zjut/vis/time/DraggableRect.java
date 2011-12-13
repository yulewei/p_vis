package edu.zjut.vis.time;

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import processing.core.PApplet;
import processing.core.PConstants;

/**
 * 可拖拽矩形区域
 * 
 * @author yulewei
 * 
 */
public class DraggableRect implements MouseListener, MouseMotionListener,
		KeyListener {

	PApplet p;

	// 当前索引范围
	int minIndex;
	int maxIndex;
	int leftIndex = 10;
	int rightIndex = 30;

	// 矩形区域, 绘制范围
	float minX, maxX, x1, x2, y1, y2;

	// 鼠标事件判断
	boolean rollover = false;
	boolean dragging = false;
	boolean leftRollover = false;
	boolean rightRollover = false;
	boolean leftResizing = false;
	boolean rightResizing = false;

	float offsetX = 0;
	float offsetY = 0;

	/**
	 * 对数组index的范围的导航
	 * 
	 * @param p
	 * @param arr
	 */
	public DraggableRect(PApplet p, int minIndex, int maxIndex) {
		this.p = p;
		this.minIndex = minIndex;
		this.maxIndex = maxIndex;

		// 默认值
		leftIndex = minIndex + (maxIndex - minIndex) / 10 * 5;
		rightIndex = maxIndex;

		p.addMouseListener(this);
		p.addMouseMotionListener(this);
		p.addKeyListener(this);
	}

	/**
	 * 完整绘制范围
	 * 
	 * @param minX
	 * @param maxX
	 * @param y1
	 * @param y2
	 */
	public void rangeSize(float minX, float maxX, float y1, float y2) {
		this.minX = minX;
		this.maxX = maxX;
		this.y1 = y1;
		this.y2 = y2;
	}

	// Method to display
	public void draw() {
		p.pushStyle();

		p.rectMode(PConstants.CORNERS);
		p.strokeWeight(1);
		p.stroke(0);

		p.noStroke();
		p.fill(175, 50);

		x1 = PApplet.map(leftIndex, minIndex, maxIndex, minX, maxX);
		x2 = PApplet.map(rightIndex, minIndex, maxIndex, minX, maxX);

		p.rect(minX, y1, x1, y2);
		p.rect(x2, y1, maxX, y2);

		p.stroke(50);
		p.strokeWeight(1.5f);
		p.line(x1, y2, x2, y2);

		p.strokeWeight(1);

		p.line(x1, y1, x1, y2);
		p.line(x2, y1, x2, y2);

		p.line(minX, y1, x1, y1);
		p.line(x2, y1, maxX, y1);

		// p.rect(x1, y1, x2, y2);

		drawHandle();

		p.popStyle();
	}

	private void drawHandle() {
		float hx = x1, hy = (y1 + y2) / 2;
		int linew = 2, gap = 3;

		p.strokeWeight(1);
		p.stroke(0);
		p.ellipseMode(PConstants.CENTER);
		p.fill(180);

		p.ellipse(hx, hy, 12, 20);
		p.line(hx - linew, hy - gap, hx + linew, hy - gap);
		p.line(hx - linew, hy, hx + linew, hy);
		p.line(hx - linew, hy + gap, hx + linew, hy + gap);

		hx = x2;
		p.ellipse(hx, hy, 12, 20);
		p.line(hx - linew, hy - gap, hx + linew, hy - gap);
		p.line(hx - linew, hy, hx + linew, hy);
		p.line(hx - linew, hy + gap, hx + linew, hy + gap);
	}

	public int getLeftIndex() {
		return leftIndex;
	}

	public void setLeftIndex(int leftIndex) {
		this.leftIndex = leftIndex;
	}

	public int getRightIndex() {
		return rightIndex;
	}

	public void setRightIndex(int rightIndex) {
		this.rightIndex = rightIndex;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		int mx = e.getX();
		int my = e.getY();

		int dis = 5;

		float x1 = PApplet.map(leftIndex, minIndex, maxIndex, minX, maxX);
		float x2 = PApplet.map(rightIndex, minIndex, maxIndex, minX, maxX);

		if (mx > x1 + dis && mx < x2 - dis && my > y1 + dis && my < y2 - dis) {
			rollover = true;
		} else {
			rollover = false;
		}

		if (Math.abs(my - (y1 + y2) / 2) < 12 && Math.abs(mx - x1) < dis)
			leftRollover = true;
		else if (Math.abs(my - (y1 + y2) / 2) < 12 && Math.abs(mx - x2) < dis)
			rightRollover = true;
		else {
			leftRollover = false;
			rightRollover = false;
		}

		if (rollover)
			p.cursor(PConstants.MOVE);
		else if (leftRollover || rightRollover)
			p.cursor(Cursor.E_RESIZE_CURSOR);
		else
			p.cursor(PConstants.ARROW);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int mx = e.getX();
		int my = e.getY();

		int dis = 5;

		float x1 = PApplet.map(leftIndex, minIndex, maxIndex, minX, maxX);
		float x2 = PApplet.map(rightIndex, minIndex, maxIndex, minX, maxX);

		if (mx > x1 + dis && mx < x2 - dis && my > y1 + dis && my < y2 - dis) {
			dragging = true;
			// If so, keep track of relative location of click to corner of
			// rectangle
			offsetX = x1 - mx;
			offsetY = y1 - my;
		}

		if (Math.abs(my - (y1 + y2) / 2) < 12 && Math.abs(mx - x1) < dis)
			leftResizing = true;
		else if (Math.abs(my - (y1 + y2) / 2) < 12 && Math.abs(mx - x2) < dis)
			rightResizing = true;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int mx = e.getX();

		if (dragging) {
			float x = mx + offsetX;
			int index = (int) PApplet.map(x, minX, maxX, minIndex, maxIndex);
			int range = rightIndex - leftIndex;

			if (index < minIndex)
				index = minIndex;
			if (index > maxIndex - range)
				index = maxIndex - range;

			leftIndex = index;
			rightIndex = leftIndex + range;
		}

		if (leftResizing) {
			float x = mx;
			int index = (int) PApplet.map(x, minX, maxX, minIndex, maxIndex);
			leftIndex = index;

			if (leftIndex < minIndex)
				leftIndex = minIndex;
			if (leftIndex >= rightIndex)
				leftIndex = rightIndex - 1;
		}

		if (rightResizing) {
			float x = mx;
			int index = (int) PApplet.map(x, minX, maxX, minIndex, maxIndex);
			rightIndex = index;

			if (rightIndex <= leftIndex)
				rightIndex = leftIndex + 1;
			if (rightIndex > maxIndex)
				rightIndex = maxIndex;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		dragging = false;

		leftResizing = false;
		rightResizing = false;
	}

	/**
	 * 键盘控制
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == PConstants.LEFT) {
			if (leftIndex > minIndex) {
				leftIndex--;
				rightIndex--;
			}
		}
		if (e.getKeyCode() == PConstants.RIGHT) {
			if (rightIndex < maxIndex) {
				leftIndex++;
				rightIndex++;
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
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

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}
}