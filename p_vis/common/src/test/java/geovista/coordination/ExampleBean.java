/* Licensed under LGPL v. 2.1 or any later version;
 see GNU LGPL for details.
 Original Author: Frank Hardisty */

package geovista.coordination;

import java.util.logging.Logger;

import javax.swing.event.EventListenerList;

import edu.zjut.common.event.IndicationEvent;
import edu.zjut.common.event.IndicationListener;


/**
 * This class demonstrates a simple bean that fires an event whenever one of two
 * member variables are set, adding them together.
 * 
 * This class is a bean because it has a constructor which takes no arguments (a
 * null constructor) and it has "get" and "set" accessors for all non-static and
 * non-transient member variables.
 * 
 * The methods "addActionListener", "removeActionListener", and
 * "fireActionPerformed" can be dropped in with no changes to classes that wish
 * to fire ActionEvents.
 * 
 * 
 */
public class ExampleBean implements IndicationListener {

	private int result;
	private int indication;
	private EventListenerList listenerList = new EventListenerList();
	final static Logger logger = Logger.getLogger(ExampleBean.class.getName());

	// null constructor
	public ExampleBean() {
		logger.finest("ExampleBean, result = " + result);
	}

	public void setListenerList(EventListenerList listenerList) {
		this.listenerList = listenerList;
	}

	public EventListenerList getListenerList() {
		return listenerList;
	}

	public void indicationChanged(IndicationEvent e) {
		indication = e.getIndication();
	}

	/**
	 * adds an IndicationListener
	 */
	public void addIndicationListener(IndicationListener l) {
		listenerList.add(IndicationListener.class, l);
	}

	/**
	 * removes an IndicationListener from the component
	 */
	public void removeIndicationListener(IndicationListener l) {
		listenerList.remove(IndicationListener.class, l);
	}

	/**
	 * Notify all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @see EventListenerList
	 */
	public void fireIndicationChanged(int newIndication) {

		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		IndicationEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == IndicationListener.class) {
				// Lazily create the event:
				if (e == null) {
					e = new IndicationEvent(this, newIndication);
				}
				((IndicationListener) listeners[i + 1]).indicationChanged(e);
			}
		}// next i

	}

	public int getIndication() {
		return indication;
	}

	public void setIndication(int ind) {
		indication = ind;
	}

}
