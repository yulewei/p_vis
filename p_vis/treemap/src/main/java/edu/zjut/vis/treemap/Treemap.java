package edu.zjut.vis.treemap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JPanel;

import org.gicentre.apps.hide.TreemapState;
import org.gicentre.data.summary.SummariseField;
import org.gicentre.hive.Expression;

import edu.zjut.common.data.DataSetForApps;
import edu.zjut.common.data.attr.AttributeData;
import edu.zjut.common.data.attr.DataField;
import edu.zjut.common.data.attr.DimensionField;
import edu.zjut.common.data.attr.MeasureField;
import edu.zjut.common.event.DataSetEvent;
import edu.zjut.common.event.DataSetListener;

public class Treemap extends JPanel implements DataSetListener {

	private DataSetForApps dataSet;
	private AttributeData attrData;

	ControlPanel statePanel;
	// private PTreemap pTreemap;
	private TreemapApplet pTreemap;

	private String defaultHive = null;
	private TreemapState treemapState;
	private List<DataField> hierFields;
	private List<SummariseField> summariseFields;
	private List<Object[]> records;

	public Treemap() {
		this.setLayout(new BorderLayout());

		// pTreemap = new PTreemap();
		pTreemap = new TreemapApplet();
		this.add(pTreemap, BorderLayout.CENTER);

		statePanel = new ControlPanel(pTreemap);
		this.add(statePanel, BorderLayout.WEST);
		statePanel.setPreferredSize(new Dimension(200, 500));

		this.setPreferredSize(new Dimension(500, 500));

		pTreemap.init();

		defaultHive = "sHier(/,$城区,$楼盘); sOrder(/,HIER,HIER); sSize(/,FX,FX); sColor(/,HIER,$毛坯均价); sLayout(/,SF,SF);";

	}

	@Override
	public void dataSetChanged(DataSetEvent e) {
		dataSet = e.getDataSetForApps();
		attrData = dataSet.getAttrData();

		buildTreemapData();

		treemapState = new TreemapState(hierFields, summariseFields);
		Collection<Expression> expressions = Expression
				.parseExpressions(defaultHive);
		treemapState.applyExpressions(expressions);

		statePanel.setState(treemapState, hierFields, summariseFields);
		pTreemap.setData(treemapState, summariseFields, records);

		repaint();
	}

	private void buildTreemapData() {
		hierFields = new ArrayList<DataField>();
		summariseFields = new ArrayList<SummariseField>();
		records = new ArrayList<Object[]>();

		DimensionField[] dimensionFeilds = attrData.getDimensionFields();
		MeasureField[] measureFeilds = attrData.getMeasureFields();

		for (DimensionField field : dimensionFeilds) {
			hierFields.add(field);
		}

		for (MeasureField field : measureFeilds) {
			SummariseField summariseField = SummariseField
					.createSummaryField(field);
			summariseFields.add(summariseField);
		}

		Object[][] rowArrays = attrData.getRowArrays();
		for (Object[] row : rowArrays) {
			records.add(row);
		}
	}
}
