package edu.zjut.commom.color;

import java.awt.Color;

import org.gicentre.utils.colour.ColourTable;

/**
 * 二分调色板, <a href="http://colorbrewer2.org/">http://colorbrewer2.org/</a>
 */
public class PaletteDiverging extends Palette
{
	public PaletteDiverging(int type)
	{
		super(type);
	}

	public PaletteDiverging(int type, float min, float max)
	{
		super(type);

		min = Math.abs(min);
		max = Math.abs(max);

		float t = Math.min(min, max);

		this.min = -t;
		this.max = t;

		pointOfDivergence = 0.0F;

		updataColourTable();
	}

	public Color getColour(Object index)
	{
		float v = ((Number) index).floatValue();
		return new Color(colourTable.findColour(v));
	}

	public float getMin()
	{
		return min;
	}

	public float getMax()
	{
		return max;
	}

	public void setMin(float min)
	{
		this.min = min;
		updataColourTable();
	}

	public void setMax(float max)
	{
		this.max = max;
		updataColourTable();
	}

	public void setMinMax(float min, float max)
	{
		this.min = min;
		this.max = max;
		updataColourTable();
	}

	public float getPointOfDivergence()
	{
		return pointOfDivergence;
	}

	public void setPointOfDivergence(float pointOfDivergence)
	{
		float newMin = min - this.pointOfDivergence + pointOfDivergence;
		float newMax = max - this.pointOfDivergence + pointOfDivergence;
		this.pointOfDivergence = pointOfDivergence;
		setMinMax(newMin, newMax);
	}

	private void updataColourTable()
	{
		colourTable = ColourTable.getPresetColourTable(type, min, max);
	}

	private float min = 0.0F;
	private float max = 1.0F;
	private float pointOfDivergence;

	private ColourTable colourTable;
}
