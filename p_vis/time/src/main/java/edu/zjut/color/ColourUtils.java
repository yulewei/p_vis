package edu.zjut.color;

import java.util.ArrayList;

import org.gicentre.utils.colour.ColourTable;

public class ColourUtils {

	private static ColourTable[] coloursCont, coloursDiv, coloursCat;

	public static ColourTable[] getColoursCont() {
		coloursCont = new ColourTable[18];
		coloursCont[0] = ColourTable.getPresetColourTable(ColourTable.YL_GN, 0,
				1);
		coloursCont[1] = ColourTable.getPresetColourTable(ColourTable.YL_GN_BU,
				0, 1);
		coloursCont[2] = ColourTable.getPresetColourTable(ColourTable.GN_BU, 0,
				1);
		coloursCont[3] = ColourTable.getPresetColourTable(ColourTable.BU_GN, 0,
				1);
		coloursCont[4] = ColourTable.getPresetColourTable(ColourTable.PU_BU_GN,
				0, 1);
		coloursCont[5] = ColourTable.getPresetColourTable(ColourTable.PU_BU, 0,
				1);
		coloursCont[6] = ColourTable.getPresetColourTable(ColourTable.BU_PU, 0,
				1);
		coloursCont[7] = ColourTable.getPresetColourTable(ColourTable.RD_PU, 0,
				1);
		coloursCont[8] = ColourTable.getPresetColourTable(ColourTable.PU_RD, 0,
				1);
		coloursCont[9] = ColourTable.getPresetColourTable(ColourTable.OR_RD, 0,
				1);
		coloursCont[10] = ColourTable.getPresetColourTable(
				ColourTable.YL_OR_RD, 0, 1);
		coloursCont[11] = ColourTable.getPresetColourTable(
				ColourTable.YL_OR_BR, 0, 1);
		coloursCont[12] = ColourTable.getPresetColourTable(ColourTable.PURPLES,
				0, 1);
		coloursCont[13] = ColourTable.getPresetColourTable(ColourTable.BLUES,
				0, 1);
		coloursCont[14] = ColourTable.getPresetColourTable(ColourTable.GREENS,
				0, 1);
		coloursCont[15] = ColourTable.getPresetColourTable(ColourTable.ORANGES,
				0, 1);
		coloursCont[16] = ColourTable.getPresetColourTable(ColourTable.REDS, 0,
				1);
		coloursCont[17] = ColourTable.getPresetColourTable(ColourTable.GREYS,
				0, 1);

		return coloursCont;
	}

	public static ColourTable[] getColoursDiv() {
		coloursDiv = new ColourTable[9];
		coloursDiv[0] = ColourTable.getPresetColourTable(ColourTable.PU_OR, 0,
				1);
		coloursDiv[1] = ColourTable.getPresetColourTable(ColourTable.BR_B_G, 0,
				1);
		coloursDiv[2] = ColourTable.getPresetColourTable(ColourTable.P_R_GN, 0,
				1);
		coloursDiv[3] = ColourTable.getPresetColourTable(ColourTable.PI_Y_G, 0,
				1);
		coloursDiv[4] = ColourTable.getPresetColourTable(ColourTable.RD_BU, 0,
				1);
		coloursDiv[5] = ColourTable.getPresetColourTable(ColourTable.RD_GY, 0,
				1);
		coloursDiv[6] = ColourTable.getPresetColourTable(ColourTable.RD_YL_BU,
				0, 1);
		coloursDiv[7] = ColourTable.getPresetColourTable(ColourTable.SPECTRAL,
				0, 1);
		coloursDiv[8] = ColourTable.getPresetColourTable(ColourTable.RD_YL_GN,
				0, 1);
		return coloursDiv;
	}

	public static ColourTable[] getColoursCat() {
		coloursCat = new ColourTable[8];
		coloursCat[0] = ColourTable.getPresetColourTable(ColourTable.SET1_9);
		coloursCat[1] = ColourTable.getPresetColourTable(ColourTable.SET2_8);
		coloursCat[2] = ColourTable.getPresetColourTable(ColourTable.SET3_12);
		coloursCat[3] = ColourTable.getPresetColourTable(ColourTable.PASTEL1_9);
		coloursCat[4] = ColourTable.getPresetColourTable(ColourTable.PASTEL2_8);
		coloursCat[5] = ColourTable.getPresetColourTable(ColourTable.DARK2_8);
		coloursCat[6] = ColourTable.getPresetColourTable(ColourTable.PAIRED_12);
		coloursCat[7] = ColourTable.getPresetColourTable(ColourTable.ACCENT_8);

		return coloursCat;
	}

	private static ColourTable getColourTableByName(String colorMap) {
		ColourTable colourTable = null;

		int type = 0;
		try {
			type = ColourTable.class.getField(colorMap).getInt(
					ColourTable.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		colourTable = ColourTable.getPresetColourTable(type);

		return colourTable;
	}

	public static ColourTable[][] getColoursCatAll() {
		String[] names = { "SET1", "SET2", "SET3", "PASTEL1", "PASTEL2",
				"DARK2", "PAIRED", "ACCENT" };
		int minCount = 3;
		int[] maxCount = { 9, 8, 12, 9, 8, 8, 12, 8 };

		ColourTable[][] colourTables = new ColourTable[10][];
		for (int count = 3; count <= 12; count++) {
			ArrayList<ColourTable> list = new ArrayList<ColourTable>();
			for (int i = 0; i < names.length; i++) {
				if (count <= maxCount[i]) {
					String colorName = names[i] + "_" + count;
					list.add(getColourTableByName(colorName.toUpperCase()));
				}
			}
			int len = list.size();
			ColourTable[] tables = new ColourTable[len];
			list.toArray(tables);

			colourTables[count - minCount] = tables;
		}

		return colourTables;
	}
}
