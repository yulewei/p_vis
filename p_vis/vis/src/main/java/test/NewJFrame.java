package test;

import java.awt.BorderLayout;

import javax.swing.Icon;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;

import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;

import edu.zjut.vis.core.ToolkitBean;
import edu.zjut.vis.core.ToolkitIO;
import edu.zjut.vis.core.VizState;
import edu.zjut.vis.treemap.Treemap;
import edu.zjut.vis.util.IconUtils;

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

	{
		// Set Look & Feel
		try {
			javax.swing.UIManager
					.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JDesktopPane jDesktopPane1;
	private JInternalFrame jInternalFrame1;

	VizState vizState;
	
	/**
	 * Auto-generated main method to display this JFrame
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				NewJFrame inst = new NewJFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public NewJFrame() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		jDesktopPane1 = new JDesktopPane();
		getContentPane().add(jDesktopPane1, BorderLayout.CENTER);

		
		vizState = ToolkitIO.getVizStateFromFile("treemap.xml");
		
		for (ToolkitBean tBean : vizState.getBeanSet().values()) {
			JInternalFrame internalFrame = tBean.getInternalFrame();
			jDesktopPane1.add(internalFrame, 0); // add on top
		}	
		
		pack();
		setSize(800, 600);
	}

}
