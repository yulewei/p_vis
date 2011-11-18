package edu.zjut.vis.treemap;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.gicentre.apps.hide.TreemapPanel;
import org.gicentre.apps.hide.TreemapStateGui;

public class TreemapViewer extends JPanel {

	ConfigDataLoader dataLoader = null;
	TreemapStateGui treemapStateGui; // dual role of graphic state and GUI
	TreemapPanel treemapPanel; // draws the graphic

	PTreemap pTreemap;

	public TreemapViewer() {
		BorderLayout layout = new BorderLayout();
		this.setLayout(layout);

		String filename = "data/soufan_top100.xml";
		dataLoader = new ConfigDataLoader(filename);
		// create a new treemap state

		String hive = "sHier(/,$区县,$楼盘); sOrder(/,HIER,HIER); sSize(/,FX,FX); sColor(/,HIER,$均价); sLayout(/,SF,SF);";

		pTreemap = new PTreemap(dataLoader, hive);
		this.add(pTreemap, BorderLayout.CENTER);

//		JPanel panel = new JPanel();
//		panel.setBackground(Color.RED);
//		this.add(panel, BorderLayout.NORTH);

		pTreemap.init();

		this.setPreferredSize(new Dimension(500, 500));
	}

	public static void main(String[] args) {

		TreemapViewer treemap = new TreemapViewer();

		JFrame jframe = new JFrame();
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		jframe.setLocation(100, 100);
		jframe.setSize(800, 800);

		jframe.add(treemap);

		jframe.setVisible(true);
	}
}
