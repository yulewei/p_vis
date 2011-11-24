package edu.zjut.common.color;

import java.awt.Color;
import java.lang.reflect.Field;

import org.gicentre.utils.colour.ColourTable;

/**
 * 调色板, 数值映射为颜色. 基于<a
 * href=http://code.google.com/p/gicentreutils/>giCentreUtils</a>,
 * 
 * @see org.gicentre.utils.colour.ColourTable
 */
public abstract class Palette
{
	public Palette(int type)
	{
		this.type = type;
	}

	public abstract Color getColour(Object index);

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	/**
	 * 当前颜色策略(type)所属的分组(3种)
	 * 
	 * @return
	 */
	public static int getTypeGroup(int type)
	{
		int group = -1;
		for (int i = 0; i < SequentialSchemes.length; i++)
		{
			if (SequentialSchemes[i] == type)
				group = Sequential;
		}
		for (int i = 0; i < DivergingSchemes.length; i++)
		{
			if (DivergingSchemes[i] == type)
				group = Diverging;
		}
		for (int i = 0; i < QualitativeSchemes.length; i++)
		{
			if (QualitativeSchemes[i] == type)
				group = Qualitative;
		}

		return group;
	}

	/**
	 * gicentre的ColourTable类对应的颜色策略静态字段数值
	 * 
	 * @param name
	 * @return
	 */
	public static int getColourTableType(String type)
	{
		type = type.toUpperCase();
		try
		{
			Class<?> ct = Class
					.forName("org.gicentre.utils.colour.ColourTable");
			Field field = ct.getField(type); // 静态字段
			return field.getInt(ct);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}

		return -1;
	}

	protected int type;

	public static final int Sequential = 0;
	public static final int Diverging = 1;
	public static final int Qualitative = 2;

	public static final int[] SequentialSchemes = { ColourTable.YL_GN,
			ColourTable.YL_GN_BU, ColourTable.GN_BU, ColourTable.BU_GN,
			ColourTable.PU_BU_GN, ColourTable.PU_BU, ColourTable.BU_PU,
			ColourTable.RD_PU, ColourTable.PU_RD, ColourTable.OR_RD,
			ColourTable.YL_OR_RD, ColourTable.YL_OR_BR, ColourTable.PURPLES,
			ColourTable.BLUES, ColourTable.GREENS, ColourTable.ORANGES,
			ColourTable.REDS, ColourTable.GREYS };
	public static final int[] DivergingSchemes = { ColourTable.PU_OR,
			ColourTable.BR_B_G, ColourTable.P_R_GN, ColourTable.PI_Y_G,
			ColourTable.RD_BU, ColourTable.RD_GY, ColourTable.RD_YL_BU,
			ColourTable.SPECTRAL, ColourTable.RD_YL_GN };
	public static final int[] QualitativeSchemes = { ColourTable.SET1_3,
			ColourTable.SET1_4, ColourTable.SET1_5, ColourTable.SET1_6,
			ColourTable.SET1_7, ColourTable.SET1_8, ColourTable.SET1_9,
			ColourTable.SET2_3, ColourTable.SET2_4, ColourTable.SET2_5,
			ColourTable.SET2_6, ColourTable.SET2_7, ColourTable.SET2_8,
			ColourTable.SET3_3, ColourTable.SET3_4, ColourTable.SET3_5,
			ColourTable.SET3_6, ColourTable.SET3_7, ColourTable.SET3_8,
			ColourTable.SET3_9, ColourTable.SET3_10, ColourTable.SET3_11,
			ColourTable.SET3_12, ColourTable.PASTEL1_3, ColourTable.PASTEL1_4,
			ColourTable.PASTEL1_5, ColourTable.PASTEL1_6,
			ColourTable.PASTEL1_7, ColourTable.PASTEL1_8,
			ColourTable.PASTEL1_9, ColourTable.PASTEL2_3,
			ColourTable.PASTEL2_4, ColourTable.PASTEL2_5,
			ColourTable.PASTEL2_6, ColourTable.PASTEL2_7,
			ColourTable.PASTEL2_8, ColourTable.DARK2_3, ColourTable.DARK2_4,
			ColourTable.DARK2_5, ColourTable.DARK2_6, ColourTable.DARK2_7,
			ColourTable.DARK2_8, ColourTable.PAIRED_3, ColourTable.PAIRED_4,
			ColourTable.PAIRED_5, ColourTable.PAIRED_6, ColourTable.PAIRED_7,
			ColourTable.PAIRED_8, ColourTable.PAIRED_9, ColourTable.PAIRED_10,
			ColourTable.PAIRED_11, ColourTable.PAIRED_12, ColourTable.ACCENT_3,
			ColourTable.ACCENT_4, ColourTable.ACCENT_5, ColourTable.ACCENT_6,
			ColourTable.ACCENT_7, ColourTable.ACCENT_8 };

}
