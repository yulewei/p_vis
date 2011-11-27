package edu.zjut.vis.treemap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JPanel;

import org.gicentre.data.summary.SummariseField;

import edu.zjut.common.ctrl.FieldList;
import edu.zjut.common.ctrl.FieldTransferHandler;
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

	private FieldList<DataField> filedList;
	private PTreemap pTreemap;

	private String defaultHive = null;
	private List<DataField> hierFields;
	private List<SummariseField> summariseFields;
	private List<Object[]> records;
	private List<Object[]> columnValues;

	public Treemap() {
		this.setLayout(new BorderLayout());
		filedList = new FieldList<DataField>();
		filedList.setDropMode(DropMode.ON_OR_INSERT);
		filedList.setDragEnabled(true);
		filedList.setTransferHandler(new FieldTransferHandler(
				FieldList.DIMENSION));
		DefaultListModel<DataField> listModel = new DefaultListModel<DataField>();
		filedList.setModel(listModel);

		this.add(filedList, BorderLayout.NORTH);

		pTreemap = new PTreemap();
		this.add(pTreemap, BorderLayout.CENTER);
		pTreemap.init();

		this.setPreferredSize(new Dimension(500, 500));

		defaultHive = "sHier(/,$城区,$楼盘); sOrder(/,HIER,HIER); sSize(/,FX,FX); sColor(/,HIER,$累积成交毛坯均价); sLayout(/,SF,SF);";

	}

	@Override
	public void dataSetChanged(DataSetEvent e) {
		dataSet = e.getDataSetForApps();
		attrData = dataSet.getAttrData();

		buildTreemapData();

		filedList.setFields(attrData.getDimensionFields());

		pTreemap.setData(hierFields, summariseFields, records, columnValues,
				defaultHive);

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
