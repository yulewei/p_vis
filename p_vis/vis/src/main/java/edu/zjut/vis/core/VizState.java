package edu.zjut.vis.core;

import edu.zjut.common.event.IndicationEvent;
import edu.zjut.common.event.IndicationListener;
import edu.zjut.common.event.SelectionEvent;
import edu.zjut.common.event.SelectionListener;
import edu.zjut.common.event.SubspaceEvent;
import edu.zjut.common.event.SubspaceListener;

import java.util.HashMap;

public class VizState implements SelectionListener, IndicationListener,
		SubspaceListener {

	HashMap<String, ToolkitBean> beanSet;

	// ToolkitBeanSet beanSet;
	String dataSource;
	int[] selection;
	int indication;
	int[] subspace;

	private String selectedBean;

	public VizState() {
	}

	public HashMap<String, ToolkitBean> getBeanSet() {
		return beanSet;
	}

	public void setBeanSet(HashMap<String, ToolkitBean> beanSet) {
		this.beanSet = beanSet;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public void selectionChanged(SelectionEvent e) {
		selection = e.getSelection();
	}

	public void indicationChanged(IndicationEvent e) {
		indication = e.getIndication();
	}

	public void subspaceChanged(SubspaceEvent e) {
		subspace = e.getSubspace();
	}

	public SelectionEvent getSelectionEvent() {
		return new SelectionEvent(this, selection);
	}

	public IndicationEvent getIndicationEvent() {
		return new IndicationEvent(this, indication);
	}

	public String getSelectedBean() {
		return selectedBean;
	}

	public void setSelectedBean(String selectedBean) {
		this.selectedBean = selectedBean;
	}

	public SubspaceEvent getSubspaceEvent() {
		return new SubspaceEvent(this, subspace);
	}
}
