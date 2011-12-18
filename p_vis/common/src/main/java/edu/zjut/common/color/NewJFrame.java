package edu.zjut.common.color;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.gicentre.utils.colour.ColourTable;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class NewJFrame extends javax.swing.JFrame {
	private JPanel jPanel1;
	private JPanel jPanel2;
	private ColorPanel colorPanel1;
	ColorSchemePicker schemePanel;
	Legend legend;

	/**
	 * Auto-generated main method to display this JFrame
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				NewJFrame inst = new NewJFrame();
				inst.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public NewJFrame() {
		this.setLayout(new BorderLayout());
		this.setSize(602, 535);

		jPanel1 = new JPanel();
		jPanel1.setLayout(new BorderLayout());
		this.add(jPanel1, BorderLayout.CENTER);
		jPanel1.setBackground(new java.awt.Color(255, 255, 255));

		jPanel2 = new JPanel();
		this.add(jPanel2, BorderLayout.NORTH);

		colorPanel1 = new ColorPanel();
		jPanel2.add(colorPanel1);

//		float[] values = new float[5];
//		for (int i = 0; i < values.length; i++) {
//			values[i] = (float) (Math.random() * 100);
//		}
//		ColourTable cTable = ColourTable.getPresetColourTable(
//				ColourTable.YL_OR_RD, 0, 1);
		legend = new Legend();
//		legend.setData(values, cTable);

		 String[] cat = new String[] { "Apple", "Orange", "Banana", "Other" };
		 ColourTable cTable = ColourTable
		 .getPresetColourTable(ColourTable.SET1_8);

		legend.setData(cat, cTable);

		legend.addLegendActionListener(new LegendActionListener() {
			@Override
			public void actionPerformed(boolean isActive) {
				schemePanel.setRefColorTable(legend.getColorTable());
				schemePanel.setLocationRelativeTo(NewJFrame.this);
				schemePanel.setVisible(isActive);
			}
		});

		jPanel2.add(legend);

		schemePanel = new ColorSchemePicker();
		schemePanel.addPickerListener(new ColorListener());
	}

	private class ColorListener implements ColorSchemeListener {
		public void colorChosen(ColourTable cTable) {
			legend.setColorTable(cTable);
			repaint();
		}
	}
}
