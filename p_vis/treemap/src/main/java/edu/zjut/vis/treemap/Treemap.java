package edu.zjut.vis.treemap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import edu.zjut.common.data.DataSetForApps;
import edu.zjut.common.data.attr.AttributeData;
import edu.zjut.common.data.attr.DataField;
import edu.zjut.common.data.attr.DimensionField;
import edu.zjut.common.data.attr.MeasureField;
import edu.zjut.common.event.DataSetEvent;
import edu.zjut.common.event.DataSetListener;
import edu.zjut.common.event.IndicationEvent;
import edu.zjut.common.event.IndicationListener;
import edu.zjut.common.event.SelectionEvent;
import edu.zjut.common.event.SelectionListener;
import edu.zjut.treemap.core.TreemapState;
import edu.zjut.treemap.hive.Expression;
import edu.zjut.treemap.summary.SummariseField;

public class Treemap extends JPanel implements DataSetListener,
		IndicationListener, SelectionListener {

	private DataSetForApps dataSet;
	private AttributeData attrData;

	private DimensionField observationField;
	private String[] observationNames;

	private JToolBar jToolBar;
	private JToggleButton sideBarTglbtn;
	private JButton preHierBtn;
	private JButton nextHierBtn;

	private JSplitPane jSplitPane;
	private TreemapCtrlPanel ctrlPanel;
	// private PTreemap pTreemap;
	private PTreemap pTreemap;

	private String defaultHive = null;
	private TreemapState treemapState;
	private List<DataField> hierFields;
	private List<SummariseField> summariseFields;
	private List<Object[]> records;

	public Treemap() {
		this.setLayout(new BorderLayout());

		initToolbar();
		this.add(jToolBar, BorderLayout.NORTH);

		jSplitPane = new JSplitPane();
		jSplitPane.setOneTouchExpandable(true);
		this.add(jSplitPane, BorderLayout.CENTER);

		// pTreemap = new PTreemap();
		pTreemap = new PTreemap();
		jSplitPane.add(pTreemap, JSplitPane.RIGHT);

		ctrlPanel = new TreemapCtrlPanel(pTreemap);
		jSplitPane.add(ctrlPanel, JSplitPane.LEFT);

		jSplitPane.setDividerLocation(180);
		this.setPreferredSize(new Dimension(500, 500));

		pTreemap.init();

		defaultHive = "sHier(/,$城区,$楼盘); sColor(/,HIER,$毛坯均价);";
	}

	private void initToolbar() {
		jToolBar = new JToolBar();

		sideBarTglbtn = new JToggleButton();
		sideBarTglbtn.setIcon(new ImageIcon(getClass().getResource(
				"sidebar.png")));
		sideBarTglbtn.setSelected(true);
		sideBarTglbtn.addActionListener(new ActionListener() {
			private int dividerSize = -1;

			public void actionPerformed(ActionEvent evt) {
				if (dividerSize == -1) {
					dividerSize = jSplitPane.getDividerSize();
				}
				if (!sideBarTglbtn.isSelected()) {
					jSplitPane.setDividerLocation(0);
					jSplitPane.setDividerSize(0);
					ctrlPanel.setVisible(false);
				} else {
					jSplitPane.setDividerLocation(180);
					jSplitPane.setDividerSize(dividerSize);
					ctrlPanel.setVisible(true);
				}
			}
		});

		jToolBar.add(sideBarTglbtn);

		JSeparator separator1 = new JSeparator(SwingConstants.VERTICAL);
		jToolBar.add(separator1);

		// 对选择treemap哪一层节点的控制, 默认为最底层
		preHierBtn = new JButton();
		preHierBtn
				.setIcon(new ImageIcon(getClass().getResource("previous.png")));
		preHierBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

			}
		});

		nextHierBtn = new JButton();
		nextHierBtn
				.setIcon(new ImageIcon(getClass().getResource("next.png")));
		nextHierBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

			}
		});
		
		jToolBar.add(preHierBtn);
		jToolBar.add(nextHierBtn);
	}

	@Override
	public void dataSetChanged(DataSetEvent e) {
		dataSet = e.getDataSetForApps();
		attrData = dataSet.getAttrData();

		observationField = attrData.getObservationField();
		observationNames = attrData.getObservationNames();

		pTreemap.observationField = observationField;
		pTreemap.observationNames = observationNames;

		buildTreemapData();

		treemapState = new TreemapState(hierFields, summariseFields);
		Collection<Expression> expressions = Expression
				.parseExpressions(defaultHive);
		treemapState.applyExpressions(expressions);

		ctrlPanel.setState(treemapState, hierFields, summariseFields);
		pTreemap.setData(treemapState, summariseFields, records);

		repaint();
	}

	private void buildTreemapData() {
		hierFields = new ArrayList<DataField>();
		summariseFields = new ArrayList<SummariseField>();
		records = new ArrayList<Object[]>();

		DimensionField[] dimensionFeilds = attrData.getDimensionFields();
		MeasureField[] measureFeilds = attrData.getMeasureFields();

		// 地理相关feild
		List<DimensionField> geoFeilds = new ArrayList<>();
		for (DimensionField field : dimensionFeilds) {
			hierFields.add(field);
			if (field.isGeoName())
				geoFeilds.add(field);
		}

		for (MeasureField field : measureFeilds) {
			SummariseField summariseField = SummariseField
					.createSummaryField(field);
			summariseFields.add(summariseField);
		}

		// 添加地理相关SummariseField
		int startIndex = attrData.getFields().length;
		int len = geoFeilds.size();

		MeasureField[] geoMeasureFields = new MeasureField[len * 2];
		Object[][] geoColumnArrays = new Double[len * 2][];
		for (int i = 0; i < geoFeilds.size(); i++) {
			DimensionField field = geoFeilds.get(i);
			MeasureField[] latlonFields = field.buildLatLonFields();

			latlonFields[0].setColIdx(startIndex + 2 * i);
			latlonFields[1].setColIdx(startIndex + 2 * i + 1);

			geoMeasureFields[2 * i] = latlonFields[0];
			geoMeasureFields[2 * i + 1] = latlonFields[1];

			geoColumnArrays[2 * i] = latlonFields[0].getColumnValues();
			geoColumnArrays[2 * i + 1] = latlonFields[1].getColumnValues();

			SummariseField latFiled = SummariseField
					.createSummaryField(latlonFields[0]);
			SummariseField lonFiled = SummariseField
					.createSummaryField(latlonFields[1]);

			summariseFields.add(latFiled);
			summariseFields.add(lonFiled);
		}

		DataField[] fields = attrData.getFields();
		DataField[] newfields = new DataField[fields.length
				+ geoMeasureFields.length];
		for (int i = 0; i < fields.length; i++) {
			newfields[i] = fields[i];
		}
		for (int i = 0; i < geoColumnArrays.length; i++) {
			newfields[fields.length + i] = geoMeasureFields[i];
		}

		Object[][] columnArrays = attrData.getColumnArrays();
		Object[][] newColumnArrays = new Object[columnArrays.length
				+ geoColumnArrays.length][];
		for (int i = 0; i < columnArrays.length; i++) {
			newColumnArrays[i] = columnArrays[i];
		}
		for (int i = 0; i < geoColumnArrays.length; i++) {
			newColumnArrays[columnArrays.length + i] = geoColumnArrays[i];
		}

		Object[][] rowArrays = attrData.transformRowArrays(newfields,
				newColumnArrays);
		for (Object[] row : rowArrays) {
			records.add(row);
		}

		// Object[][] rowArrays = attrData.getRowArrays();
		// for (Object[] row : rowArrays) {
		// records.add(row);
		// }
	}

	@Override
	public void indicationChanged(IndicationEvent e) {
		pTreemap.indicationChanged(e.getIndication());
	}

	@Override
	public void selectionChanged(SelectionEvent e) {
		pTreemap.selectionChanged(e.getSelection());
	}

	@Override
	public SelectionEvent getSelectionEvent() {
		return new SelectionEvent(this, pTreemap.selections);
	}

	// 触发事件

	public void addIndicationListener(IndicationListener l) {
		pTreemap.addIndicationListener(l);
	}

	public void removeIndicationListener(IndicationListener l) {
		pTreemap.removeIndicationListener(l);
	}

	public void addSelectionListener(SelectionListener l) {
		pTreemap.addSelectionListener(l);
	}

	public void removeSelectionListener(SelectionListener l) {
		pTreemap.removeSelectionListener(l);
	}
}
