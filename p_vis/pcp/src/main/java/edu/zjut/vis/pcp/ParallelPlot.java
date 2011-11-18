package edu.zjut.vis.pcp;

import java.awt.Dimension;

import edu.zjut.common.data.AttributeData;
import edu.zjut.common.data.DataSetForApps;
import edu.zjut.common.event.DataSetEvent;
import edu.zjut.common.event.DataSetListener;
import edu.zjut.common.event.IndicationEvent;
import edu.zjut.common.event.IndicationListener;
import edu.zjut.common.event.SelectionEvent;
import edu.zjut.common.event.SelectionListener;
import edu.zjut.common.event.SubspaceEvent;
import edu.zjut.common.event.SubspaceListener;
import edu.zjut.vis.pcp.ParvisPlot;

/**
 * 对ParvisPlot类组件协同封装
 * 
 * @author yulewei
 */
public class ParallelPlot extends ParvisPlot implements DataSetListener,
		SubspaceListener {

	private final int MAX_AXES = 6;

	int[] savedSelection;
	private DataSetForApps dataSet = null;
	AttributeData attrData;

	public ParallelPlot() {

	}

	@Override
	public void dataSetChanged(DataSetEvent e) {

		dataSet = e.getDataSetForApps();
		attrData = dataSet.getAttrData();

		int nVars = attrData.getNumberNumericAttributes();
		int min = MAX_AXES;
		if (nVars < min) {
			min = nVars;
		}
		int[] vars = new int[min];
		for (int i = 0; i < min; i++) {
			vars[i] = i;
		}
		setSubspace(vars);
	}

	@Override
	public void subspaceChanged(SubspaceEvent e) {
		int[] selectedVars = e.getSubspace();
		setSubspace(selectedVars);
	}

	private void setSubspace(int[] vars) {

		int nVars = MAX_AXES;
		String[] obsNames = attrData.getObservationNames();

		int nNumeric = attrData.getNumberNumericAttributes();

		if (nNumeric < nVars) {
			nVars = nNumeric;
		}
		if (vars.length < nVars) {
			nVars = vars.length;
		}

		String[] varNamesNew = new String[nVars + 1];
		Object[] newDataSet = new Object[nVars + 1];

		// 属性名称
		varNamesNew[0] = "name";
		String[] numericAttributeNames = attrData.getNumericAttributeNames();
		for (int i = 1; i < (nVars + 1); i++) {
			varNamesNew[i] = numericAttributeNames[i - 1];
		}

		// 添加名称列
		newDataSet[0] = obsNames;

		for (int i = 0; i < nVars; i++) {
			int var = vars[i];
			if (var < nNumeric) {
				newDataSet[i + 1] = attrData.getNumericDataAsDouble(var);
			}
		}

		AttributeData attrData = new AttributeData(varNamesNew, newDataSet, 0);
		this.setDataSet(new DataSetForApps(attrData, null, null));
	}

	public void setDataSet(DataSetForApps dataSet) {
		DataSetSpaceModel f = new DataSetSpaceModel(dataSet);
		f.addProgressListener(this);
		f.readContents();
		setModel(f);
	}
}
