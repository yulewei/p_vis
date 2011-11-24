/* Licensed under LGPL v. 2.1 or any later version;
 see GNU LGPL for details.
 Original Author: Frank Hardisty */

package geovista.animation;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.zjut.common.data.DataSetForApps;
import edu.zjut.common.data.attr.AttributeData;
import edu.zjut.common.data.attr.MeasureField;
import edu.zjut.common.event.ConditioningEvent;
import edu.zjut.common.event.ConditioningListener;
import edu.zjut.common.event.DataSetEvent;
import edu.zjut.common.event.DataSetListener;
import edu.zjut.common.event.SubspaceEvent;
import edu.zjut.common.event.SubspaceListener;

/**
 * ConditioningAnimator is used to send out indication signals that correspond
 * to current classifications.
 */
public class ConditioningAnimator extends JPanel implements ActionListener,
		ChangeListener, DataSetListener, SubspaceListener {

	/** The timer. */
	private final Timer ticker;

	/** The current class index. */
	private transient int currClassIndex;

	/** The low conditioning index. */
	private transient int lowCondIndex;

	/** The high conditioning index. */
	private transient int highCondIndex;

	/** The current conditioning. */
	private transient int[] currConditioning;

	/** The start stop button. */
	private transient JButton startStopButton;

	/** Flag for going or not. */
	private transient boolean going = false;

	/** Delay, in milliseconds. */
	private int speed; // in milliseconds

	/** The data to animate over. */
	private transient DataSetForApps data;

	/** The classified observations */
	private transient ClassedObs[] obs;

	/** The subspace, in other words, which variables are selected. */
	private transient int[] subspace;

	/** Which variable is currently selected in the subspace. */
	private transient int subspaceIndex;

	/** The time slider. */
	private transient JSlider timeSlider;

	/** The variable dropdown box. */
	private transient JComboBox varCombo;

	private transient boolean varComboIsAdjusting;

	/** Do we iterate over just the subspace, or over all variables? */
	private transient JRadioButton subspaceButton;

	private transient JRadioButton oneVarButton;

	private boolean usingSubspace;

	final static Logger logger = Logger.getLogger(ConditioningAnimator.class
			.getName());

	/**
	 * null ctr.
	 */
	public ConditioningAnimator() {
		usingSubspace = true;
		speed = 250;
		ticker = new Timer(speed, this);

		this.add(makeTopPanel());

		setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

	}

	/**
	 * Make top panel.
	 * 
	 * @return the j panel
	 */
	private JPanel makeTopPanel() {
		JPanel topPanel = new JPanel();
		startStopButton = new JButton("Start");
		topPanel.add(startStopButton);
		startStopButton.addActionListener(this);

		varCombo = new JComboBox();
		varCombo.addActionListener(this);
		topPanel.add(varCombo);

		subspaceButton = new JRadioButton("Iterate over subspace");
		oneVarButton = new JRadioButton("Iterate over one variable");
		subspaceButton.setSelected(true);
		subspaceButton.addActionListener(this);
		oneVarButton.addActionListener(this);

		ButtonGroup buttGroup = new ButtonGroup();
		buttGroup.add(subspaceButton);
		buttGroup.add(oneVarButton);

		JPanel buttonHolder = new JPanel();
		BoxLayout boxLayout = new BoxLayout(buttonHolder, BoxLayout.Y_AXIS);
		buttonHolder.setLayout(boxLayout);

		buttonHolder.add(subspaceButton);
		buttonHolder.add(oneVarButton);

		topPanel.add(buttonHolder);
		timeSlider = new JSlider(1, 10, 5);
		topPanel.add(timeSlider);
		timeSlider.addChangeListener(this);
		return topPanel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent
	 * )
	 */
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == timeSlider && !timeSlider.getValueIsAdjusting()) {
			speed = timeSlider.getValue() * 50;
			ticker.setDelay(speed);
		}
	}

	/**
	 * Iterate conditionings.
	 */
	private void iterateConditionings() {// main loop

		if (highCondIndex < data.getAttrData().getNumObservations() - 1) { // go
																			// up
																			// one
			int index = obs[lowCondIndex].index;
			currConditioning[index] = 0;
			lowCondIndex++;
			highCondIndex++;
			index = obs[highCondIndex].index;
			currConditioning[index] = -1;
			fireConditioningChanged(currConditioning);

			ticker.setDelay(speed);
			currClassIndex++;
		} else {// go back to zero
			ticker.setDelay(speed * 10);

			currClassIndex = 0; // reset
			if (usingSubspace) {

				int currVar = iterateSubspace();

				instantiateCurrentVariable(currVar);
				conditionOutLowerRange();
				varComboIsAdjusting = true;
				varCombo.setSelectedIndex(currVar);
				varComboIsAdjusting = false;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (data == null) {
			// without data, we don't do anything
			return;
		}
		if (e.getSource() == ticker) {
			iterateConditionings();
		} else if (e.getSource() == startStopButton) {
			if (going) {
				going = false; // turn off
				ticker.stop();
				startStopButton.setText("Start");
			} else {
				going = true; // turn on
				ticker.start();
				startStopButton.setText("Stop");
			}
		} else if (e.getSource() == subspaceButton) {
			usingSubspace = true;
		} else if (e.getSource() == oneVarButton) {
			usingSubspace = false;
		} else if (e.getSource() == varCombo) {
			if (obs != null && varComboIsAdjusting == false) { // might be null
				// if
				// we are getting
				// started
				int currVar = varCombo.getSelectedIndex();
				instantiateCurrentVariable(currVar);
				conditionOutLowerRange();
			}
		}
	}

	/**
	 * Iterate subspace.
	 * 
	 * @return the int
	 */
	private int iterateSubspace() {
		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("iterating subspace");
		}

		if (subspaceIndex >= subspace.length - 1) {
			subspaceIndex = 0;
		} else {
			subspaceIndex++;
		}
		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("subspaceIndex = " + subspaceIndex);
		}

		int currVar = subspace[subspaceIndex] + 1;// evil strikes again
		return currVar;

	}

	/**
	 * Instantiate current variable.
	 * 
	 * @param currVar
	 *            the curr var
	 */
	private void instantiateCurrentVariable(int currVar) {

		double[] values = data.getAttrData().getMeasureColumnAsDouble(currVar);
		for (int i = 0; i < obs.length; i++) {
			obs[i].index = i;
			obs[i].value = values[i];
		}
		Arrays.sort(obs);
	}

	/**
	 * Condition out lower range.
	 */
	private void conditionOutLowerRange() {

		for (int i = 0; i < currConditioning.length; i++) {
			currConditioning[i] = 0;// default == activated
		}
		int numObs = data.getAttrData().getNumObservations();
		int fifth = numObs / 5;
		for (int i = 0; i < fifth; i++) {
			int index = obs[i].index;
			currConditioning[index] = -1;
		}
		lowCondIndex = 0;
		highCondIndex = fifth - 1;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * geovista.common.event.SubspaceListener#subspaceChanged(geovista.common
	 * .event.SubspaceEvent)
	 */
	public void subspaceChanged(SubspaceEvent e) {
		subspace = e.getSubspace();
		subspaceIndex = 0;
		int currVar = subspace[subspaceIndex];
		instantiateCurrentVariable(currVar);
		conditionOutLowerRange();
		varComboIsAdjusting = true;
		varCombo.setSelectedIndex(currVar);
		varComboIsAdjusting = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * geovista.common.event.DataSetListener#dataSetChanged(geovista.common.
	 * event.DataSetEvent)
	 */
	public void dataSetChanged(DataSetEvent e) {
		data = e.getDataSetForApps();

		AttributeData attrData = data.getAttrData();
		currConditioning = new int[attrData.getNumObservations()];
		MeasureField[] measureFeilds = attrData.getMeasureFeilds();
		for (MeasureField element : measureFeilds) {
			varCombo.addItem(element.getName());
		}
		// tempArray = new int[data.getNumObservations()];

		obs = new ClassedObs[attrData.getNumObservations()];
		for (int i = 0; i < obs.length; i++) {
			obs[i] = new ClassedObs();
		}
		subspace = new int[measureFeilds.length];
		for (int i = 0; i < subspace.length; i++) {
			subspace[i] = i;
		}
		subspaceIndex = 0;
		instantiateCurrentVariable(subspace[subspaceIndex]);
		conditionOutLowerRange();
	}

	/**
	 * adds an ConditioningListener.
	 * 
	 * @param l
	 *            the l
	 */
	public void addConditioningListener(ConditioningListener l) {
		listenerList.add(ConditioningListener.class, l);
	}

	/**
	 * removes an ConditioningListener from the component.
	 * 
	 * @param l
	 *            the l
	 */
	public void removeConditioningListener(ConditioningListener l) {
		listenerList.remove(ConditioningListener.class, l);
	}

	/**
	 * Notify all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @param newConditioning
	 *            the new conditioning
	 * 
	 * @see EventListenerList
	 */
	private void fireConditioningChanged(int[] newConditioning) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		ConditioningEvent e = null;

		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ConditioningListener.class) {
				// Lazily create the event:
				if (e == null) {
					e = new ConditioningEvent(this, newConditioning);
				}

				((ConditioningListener) listeners[i + 1])
						.conditioningChanged(e);
			}
		}

		// next i
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		ConditioningAnimator inAnim = new ConditioningAnimator();

		JFrame app = new JFrame();
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.getContentPane().setLayout(new BorderLayout());

		app.getContentPane().add(inAnim);
		app.pack();
		app.setVisible(true);
	}

	/**
	 * The Class ClassedObs.
	 */
	private class ClassedObs implements Comparable {

		/** The index. */
		int index;

		/** The value. */
		double value;

		// we compare by value
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(Object o) {
			ClassedObs e = (ClassedObs) o;
			int val = 0;
			if (Double.isNaN(e.value)) {
				if (Double.isNaN(value)) {
					return 0;
				}
				return 1;
			}// end if the other value is NaN

			if (Double.isNaN(value)) {
				val = -1;// everything is bigger than NaN
			} else if (value < e.value) {
				val = -1;
			} else if (value > e.value) {
				val = 1;
			}

			return val;
		}
	}
}