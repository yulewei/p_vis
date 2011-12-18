package edu.zjut.common.color;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.gicentre.utils.colour.ColourTable;

import edu.zjut.common.color.Legend.Orientation;

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
	private JPanel jPanel3;
	private JPanel jPanel2;
	private ColorPanel colorPanel1;
	ColorSchemePicker schemePanel;
	Legend legend;
	Legend legend2;

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
		jPanel1.setPreferredSize(new Dimension(513, 441));

		jPanel2 = new JPanel();
		this.add(jPanel2, BorderLayout.NORTH);

		jPanel3 = new JPanel();
		this.add(jPanel3, BorderLayout.WEST);
		jPanel3.setPreferredSize(new Dimension(112, 441));

		float[] values = new float[5];
		for (int i = 0; i < values.length; i++) {
			values[i] = (float) (Math.random() * 200);
		}

		ColourTable cTable = ColourTable.getPresetColourTable(
				ColourTable.YL_OR_RD, 0, 1);

		String[] cat = new String[] { "Apple", "Orange", "Banana", "Other" };
		ColourTable cTable2 = ColourTable.getPresetColourTable(ColourTable.SET1_8);

		colorPanel1 = new ColorPanel();
		jPanel2.add(colorPanel1);

		legend = new Legend();
		jPanel2.add(legend);
		legend.setData(values, cTable);
		legend.addLegendActionListener(new LegendActionListener() {
			@Override
			public void actionPerformed(boolean isActive) {
				schemePanel.setRefColorTable(legend.getColorTable());
				schemePanel.setLocationRelativeTo(NewJFrame.this);
				schemePanel.setVisible(isActive);
			}
		});

		legend2 = new Legend();
//		legend2.setData(cat, cTable2);
		legend2.setData(values, cTable);
		legend2.setOrient(Orientation.VERTICAL);		
		jPanel3.add(legend2);
		legend2.addLegendActionListener(new LegendActionListener() {
			@Override
			public void actionPerformed(boolean isActive) {
				schemePanel.setRefColorTable(legend2.getColorTable());
				schemePanel.setLocationRelativeTo(NewJFrame.this);
				schemePanel.setVisible(isActive);
			}
		});

		schemePanel = new ColorSchemePicker();
		schemePanel.addPickerListener(new ColorListener());
	}

	private class ColorListener implements ColorSchemeListener {
		public void colorChosen(ColourTable cTable) {
			legend2.setColorTable(cTable);
			repaint();
		}
	}
}
