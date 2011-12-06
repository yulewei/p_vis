package edu.zjut.vis.treemap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

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

	private JToolBar jToolBar;
	private JToggleButton sideBarTglbtn;
	private JSplitPane jSplitPane;
	private TreemapCtrlPanel ctrlPanel;
	// private PTreemap pTreemap;
	private TreemapApplet pTreemap;

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
		pTreemap = new TreemapApplet();
		jSplitPane.add(pTreemap, JSplitPane.RIGHT);

		ctrlPanel = new TreemapCtrlPanel(pTreemap);
		jSplitPane.add(ctrlPanel, JSplitPane.LEFT);

		jSplitPane.setDividerLocation(200);
		this.setPreferredSize(new Dimension(500, 500));

		pTreemap.init();

		defaultHive = "sHier(/,$����,$¥��); sOrder(/,HIER,HIER); sSize(/,FX,FX); sColor(/,HIER,$ë������); sLayout(/,SF,SF);";
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
					jSplitPane.setDividerLocation(200);
					jSplitPane.setDividerSize(dividerSize);
					ctrlPanel.setVisible(true);
				}
			}
		});

		jToolBar.add(sideBarTglbtn);
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
