package edu.zjut.vis.treemap;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JFrame;

import org.gicentre.data.summary.SummariseField;

import edu.zjut.common.data.attr.DataField;

public class Test {

	public static void main(String[] args) {

		String filename = "data/soufan_top100.xml";
		ConfigDataLoader dataLoader = new ConfigDataLoader(filename);
		// create a new treemap state

		String hive = "sHier(/,$区县,$楼盘); sOrder(/,HIER,HIER); sSize(/,FX,FX); sColor(/,HIER,$均价); sLayout(/,SF,SF);";

		List<DataField> hierFields = dataLoader.getAllowedHierVars();
		List<SummariseField> summariseFields = dataLoader.getSummariseFields();
		List<Object[]> records = dataLoader.getRecords();

		PTreemap pTreemap = new PTreemap();
		pTreemap.setData(hierFields, summariseFields, records, hive);
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
