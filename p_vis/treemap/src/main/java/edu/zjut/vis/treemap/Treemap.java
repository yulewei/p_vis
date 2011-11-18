package edu.zjut.vis.treemap;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Treemap extends JPanel {

	ConfigDataLoader dataLoader;
	PTreemap pTreemap;

	public Treemap() {
		BorderLayout layout = new BorderLayout();
		this.setLayout(layout);

//		pTreemap = new PTreemap(dataLoader, hive);
//		this.add(pTreemap);
//		pTreemap.init();

		this.setPreferredSize(new Dimension(500, 500));
	}

	public static void main(String[] args) {

		String filename = "data/soufan_top100.xml";
		ConfigDataLoader dataLoader = new ConfigDataLoader(filename);
		// create a new treemap state

		String hive = "sHier(/,$区县,$楼盘); sOrder(/,HIER,HIER); sSize(/,FX,FX); sColor(/,HIER,$均价); sLayout(/,SF,SF);";

		PTreemap pTreemap = new PTreemap(dataLoader, hive);
		pTreemap.setPreferredSize(new Dimension(500, 500));
		
		JFrame jframe = new JFrame();
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		jframe.setLocation(100, 100);
		jframe.setSize(800, 800);

		jframe.add(pTreemap);
		
		pTreemap.init();

		jframe.setVisible(true);
	}
}
