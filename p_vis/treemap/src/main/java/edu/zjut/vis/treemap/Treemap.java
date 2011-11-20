package edu.zjut.vis.treemap;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import edu.zjut.common.data.AttributeData;
import edu.zjut.common.data.DataSetForApps;
import edu.zjut.common.event.DataSetEvent;
import edu.zjut.common.event.DataSetListener;

public class Treemap extends JPanel implements DataSetListener {

	private DataSetForApps dataSet;
	private AttributeData attrData;

	PTreemap pTreemap;

	public Treemap() {
		BorderLayout layout = new BorderLayout();
		this.setLayout(layout);

		// pTreemap = new PTreemap(dataLoader, hive);
		// this.add(pTreemap);
		// pTreemap.init();

		this.setPreferredSize(new Dimension(500, 500));
	}

	@Override
	public void dataSetChanged(DataSetEvent e) {
		dataSet = e.getDataSetForApps();
		attrData = dataSet.getAttrData();

		buildTreemapData();
	}

	private void buildTreemapData() {

	}
}
