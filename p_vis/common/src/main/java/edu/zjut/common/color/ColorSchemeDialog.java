package edu.zjut.common.color;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

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
public class ColorSchemeDialog extends JDialog {

	{
		// Set Look & Feel
		try {
			javax.swing.UIManager
					.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JTabbedPane jTabbedPane;
	private JPanel contPanel;
	private JPanel divPanel;
	private JPanel catPanel;
	private ColorSchemePicker contPicker;
	private ColorSchemePicker divPicker;
	private ColorSchemePicker catPicker;

	public ColorSchemeDialog() {
		this.setTitle("ColorBrewer");

		jTabbedPane = new JTabbedPane();
		getContentPane().add(jTabbedPane, BorderLayout.CENTER);

		contPanel = new JPanel();
		contPanel.setLayout(new BorderLayout());
		jTabbedPane.addTab("Sequential", null, contPanel, null);

		divPanel = new JPanel();
		divPanel.setLayout(new BorderLayout());
		jTabbedPane.addTab("Diverging", null, divPanel, null);

		catPanel = new JPanel();
		catPanel.setLayout(new BorderLayout());
		jTabbedPane.addTab("Categorical", null, catPanel, null);

		contPicker = new ColorSchemePicker();
		contPicker.initColorType(ColorSchemeType.Cont);
		contPanel.add(contPicker, BorderLayout.CENTER);

		divPicker = new ColorSchemePicker();
		divPicker.initColorType(ColorSchemeType.Div);
		divPanel.add(divPicker, BorderLayout.CENTER);

		catPicker = new ColorSchemePicker();
		catPicker.initColorType(ColorSchemeType.Cat);
		catPanel.add(catPicker, BorderLayout.CENTER);

		this.setSize(415, 321);
	}

	public static void main(String[] args) {
		ColorSchemeDialog dialog = new ColorSchemeDialog();
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}
}
