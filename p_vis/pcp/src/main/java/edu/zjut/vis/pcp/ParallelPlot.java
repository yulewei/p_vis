package edu.zjut.vis.pcp;

import org.mediavirus.parvis.gui.ParallelDisplay;

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
 * 对ParvisPlot类组件事件协同封装
 * 
 * @author yulewei
 */
public class ParallelPlot extends ParvisPlot implements DataSetListener,
		SubspaceListener, IndicationListener, SelectionListener {

	private final int MAX_AXES = 6;

	int[] savedSelection;
	private DataSetForApps dataSet;
	AttributeData attrData;

	public ParallelPlot() {
		parallelDisplay.addIndicationListener(this);
		parallelDisplay.addSelectionListener(this);
	}

	@Override
	public void dataSetChanged(DataSetEvent e) {
		dataSet = e.getDataSetForApps();
		attrData = dataSet.getAttrData();

		int nVars = attrData.getNumberNumericAttributes();
		int min = nVars < MAX_AXES ? nVars : MAX_AXES;
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
		int nNumeric = attrData.getNumberNumericAttributes();
		int nVars = Math.min(vars.length, Math.min(nNumeric, MAX_AXES));

		// 属性名称
		String[] varNamesNew = new String[nVars + 1];
		varNamesNew[0] = "name";
		String[] numericAttributeNames = attrData.getNumericAttributeNames();
		for (int i = 1; i < (nVars + 1); i++) {
			varNamesNew[i] = numericAttributeNames[i - 1];
		}

		// 属性数据
		Object[] newDataSet = new Object[nVars + 1];
		newDataSet[0] = attrData.getObservationNames();
		for (int i = 0; i < nVars; i++) {
			int var = vars[i];
			if (var < nNumeric) {
				newDataSet[i + 1] = attrData.getNumericDataAsDouble(var);
			}
		}

		AttributeData attrData = new AttributeData(0, varNamesNew, newDataSet);
		this.setDataSet(new DataSetForApps(attrData, null, null));
	}

	public void setDataSet(DataSetForApps dataSet) {
		DataSetSpaceModel model = new DataSetSpaceModel(dataSet);
		model.addProgressListener(this);
		model.readContents();
		setModel(model);
	}

	@Override
	public void indicationChanged(IndicationEvent e) {		
		parallelDisplay.indicationChanged(e);
	}

	@Override
	public void selectionChanged(SelectionEvent e) {
		savedSelection = e.getSelection();

		if (getWidth() * getHeight() <= 0) {
			logger.info("ParallelPlot got selection when size was zero");
			return;
		}
		parallelDisplay.selectionChanged(e);
		parallelDisplay.setEditMode(ParallelDisplay.BRUSH);
	}

	@Override
	public SelectionEvent getSelectionEvent() {
		return new SelectionEvent(this, parallelDisplay.savedSelection);
	}

	public void addIndicationListener(IndicationListener l) {
		parallelDisplay.addIndicationListener(l);
	}

	public void removeIndicationListener(IndicationListener l) {
		parallelDisplay.removeIndicationListener(l);
	}

	public void addSelectionListener(SelectionListener l) {
		parallelDisplay.addSelectionListener(l);
	}

	public void removeSelectionListener(SelectionListener l) {
		parallelDisplay.removeSelectionListener(l);
	}
}
