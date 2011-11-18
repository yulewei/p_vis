package edu.zjut.treemap;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.gicentre.apps.hide.TreemapPanel;
import org.gicentre.apps.hide.TreemapStateGui;

public class TreemapSwingPanel extends JPanel {

	ConfigDataLoader dataLoader = null;
	TreemapStateGui treemapStateGui; // dual role of graphic state and GUI
	TreemapPanel treemapPanel; // draws the graphic

	PTreemap pTreemap;

	public TreemapSwingPanel() {
		BorderLayout layout = new BorderLayout();
		this.setLayout(layout);

		String filename = "soufan_top100.xml";
		dataLoader = new ConfigDataLoader(filename);
		// create a new treemap state

		String hive = "sHier(/,$区县,$楼盘); sOrder(/,HIER,HIER); sSize(/,FX,FX); sColor(/,HIER,$均价); sLayout(/,SF,SF);";

		pTreemap = new PTreemap(dataLoader, hive);
		this.add(pTreemap);

		pTreemap.init();
	}

	public static void main(String[] args) {

		TreemapSwingPanel treemap = new TreemapSwingPanel();

		JFrame jframe = new JFrame();
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		jframe.setLocation(100, 100);
		jframe.setSize(800, 800);

		jframe.add(treemap, BorderLayout.CENTER);

		jframe.setVisible(true);
	}
}
