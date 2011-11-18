/* -------------------------------------------------------------------
 Java source file for the class ToolkitBean
 Copyright (c), 2005 Frank Hardisty
 $Author: hardisty $
 $Id: ToolkitBean.java,v 1.1 2005/02/13 03:26:27 hardisty Exp $
 $Date: 2005/02/13 03:26:27 $
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 -------------------------------------------------------------------   */

package edu.zjut.vis.core;

import edu.zjut.common.event.IndicationEvent;
import edu.zjut.common.event.IndicationListener;
import edu.zjut.common.event.SelectionEvent;
import edu.zjut.common.event.SelectionListener;
import edu.zjut.common.event.SubspaceEvent;
import edu.zjut.common.event.SubspaceListener;

import java.util.HashMap;

/**
 * A holder for the state of a GeoVizToolkit. Intended to help with marshalling
 * and unmarshalling.
 */
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
