package edu.zjut.commom.color;

import java.awt.Color;

import org.gicentre.utils.colour.ColourTable;

/**
 * ÐòÁÐµ÷É«°å, <a href="http://colorbrewer2.org/">http://colorbrewer2.org/</a>
 */
public class PaletteSequential extends Palette
{

	public PaletteSequential(int type)
	{
		super(type);

		this.min = 0.0F;
		this.max = 1.0F;
		updataColourTable();
	}

	public PaletteSequential(int type, float min, float max)
	{
		super(type);

		this.min = min;
		this.max = max;
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

	private void updataColourTable()
	{
		colourTable = ColourTable.getPresetColourTable(type, min, max);
	}

	private float min = 0.0F;
	private float max = 1.0F;
	private ColourTable colourTable;

}
