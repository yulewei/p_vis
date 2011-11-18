package edu.zjut.common.data;

import edu.zjut.common.event.DataSetEvent;
import edu.zjut.common.event.DataSetListener;

import java.util.logging.Logger;

import javax.swing.event.EventListenerList;

/**
 * 
 */
public class DataSetBroadcaster {

	private transient EventListenerList listenerList;

	DataSetForApps dataSet;

	final static Logger logger = Logger.getLogger(DataSetBroadcaster.class
			.getName());

	public DataSetBroadcaster() {
		super();
		listenerList = new EventListenerList();
	}

	public void setAndFireDataSet(DataSetForApps dataSetApps) {
		dataSet = dataSetApps;
		this.fireDataSetChanged(dataSet);
	}

	public void setListenerList(EventListenerList listenerList) {
		this.listenerList = listenerList;
	}

	public EventListenerList getListenerList() {
		return listenerList;
	}

	/**
	 * adds a DataSetListener
	 */
	public void addDataSetListener(DataSetListener l) {
		listenerList.add(DataSetListener.class, l);
	}

	/**
	 * removes a DataSetListener
	 */
	public void removeDataSetListener(DataSetListener l) {
		listenerList.remove(DataSetListener.class, l);
	}

	protected void fireDataSetChanged(DataSetForApps data) {
		Object[] listeners = listenerList.getListenerList();
		DataSetEvent e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == DataSetListener.class) {
				// Lazily create the event:
				if (e == null) {
					e = new DataSetEvent(data, this);

				}
				((DataSetListener) listeners[i + 1]).dataSetChanged(e);
			}
		}
	}
}
