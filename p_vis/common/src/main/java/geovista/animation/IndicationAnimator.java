/* Licensed under LGPL v. 2.1 or any later version;
 see GNU LGPL for details.
 Original Author: Frank Hardisty */

package geovista.animation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.zjut.common.data.DataSetForApps;
import edu.zjut.common.event.ClassificationEvent;
import edu.zjut.common.event.ClassificationListener;
import edu.zjut.common.event.DataSetEvent;
import edu.zjut.common.event.DataSetListener;
import edu.zjut.common.event.DimensionEvent;
import edu.zjut.common.event.DimensionListener;
import edu.zjut.common.event.IndicationEvent;
import edu.zjut.common.event.IndicationListener;
import edu.zjut.common.event.SubspaceEvent;
import edu.zjut.common.event.SubspaceListener;

import geovista.common.classification.ClassifierPicker;

/**
 * IndicationAnimator is used to send out indication signals that corrispond to
 * current classifications.
 * 
 */
public class IndicationAnimator extends JPanel implements ActionListener,
		ChangeListener, DataSetListener, SubspaceListener,
		ClassificationListener {
	private transient final Timer ticker;
	private transient int currObs;
	private transient JButton startStopButton;
	private transient boolean going = false;
	private transient final int fps;
	private transient int delay;// in milliseconds
	static final int FPS_MIN = 0;
	static final int FPS_MAX = 30;
	static final int FPS_INIT = 15; // initial frames per second
	private transient DataSetForApps data;
	private transient int maxIndication = 0;
	private transient final ClassifierPicker classPick;
	private transient int[] classes;
	private transient double[] values;
	private transient ClassedObs[] obs;
	private transient int[] subspace;
	private transient int subspaceIndex;
	private transient JSlider timeSlider;
	private transient JCheckBox subspaceBox;
	private transient boolean usingSubspace;
	private transient String[] varNames;
	final transient static Logger logger = Logger
			.getLogger(IndicationAnimator.class.getName());

	/**
	 * null ctr
	 */
	public IndicationAnimator() {
		usingSubspace = true;
		fps = FPS_INIT;

		ticker = new Timer(fps, this);

		this.add(makeTopPanel());

		classPick = new ClassifierPicker();
		classPick
				.setVariableChooserMode(ClassifierPicker.VARIABLE_CHOOSER_MODE_ACTIVE);
		this.add(classPick);
		classPick.addClassificationListener(this);
		classPick.addActionListener(this);
		classPick.setBorder(new LineBorder(Color.white));
		setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

	}

	private JPanel makeTopPanel() {
		JPanel topPanel = new JPanel();
		startStopButton = new JButton("Start");
		topPanel.add(startStopButton);
		startStopButton.addActionListener(this);
		subspaceBox = new JCheckBox("Subspace?", true);
		subspaceBox.addActionListener(this);
		topPanel.add(subspaceBox);
		timeSlider = new JSlider(FPS_MIN, FPS_MAX, FPS_INIT);
		timeSlider.setMajorTickSpacing(20);
		timeSlider.setPaintLabels(true);
		topPanel.add(timeSlider);
		timeSlider.addChangeListener(this);
		return topPanel;
	}

	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider) e.getSource();
		if (e.getSource() == timeSlider && !source.getValueIsAdjusting()) {
			int fps = source.getValue();
			if (fps == 0) {
				// if (!frozen) stopAnimation();
			} else {

				delay = 1000 / fps;
				ticker.setDelay(delay);
			}
			if (logger.isLoggable(Level.FINEST)) {
				logger.finest("delay = " + delay);
			}
		}
	}

	private void iterateObs() { // main loop
		int whichObs = obs[currObs].index;
		fireIndicationChanged(whichObs);

		if (logger.isLoggable(Level.FINEST) && currObs == 0) {
			logger.finest("zero obs index = " + obs[currObs].index
					+ ", value = " + obs[currObs].value);
		}

		if (currObs < maxIndication) { // go up one
			ticker.setDelay(delay);
			currObs++;
		} else {
			ticker.setDelay(delay * 10);
			if (usingSubspace) {
				if (logger.isLoggable(Level.FINEST)) {
					logger.finest("new var!!!");
				}

				iterateSubspace();
			}
			currObs = 0; // reset
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == ticker) {
			iterateObs();
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
		} else if (e.getSource() == subspaceBox) {
			usingSubspace = subspaceBox.isSelected();
		} else if (e.getSource() == classPick
				&& e.getActionCommand().equals(
						ClassifierPicker.COMMAND_SELECTED_VARIABLE_CHANGED)) {
			// xxx hack for demo
			subspaceIndex = classPick.getCurrVariableIndex();

		}

	}

	private void reclassObs() {
		if (data == null) {
			return;
		}
		Arrays.sort(obs);
	}

	private void iterateSubspace() {

		if (subspaceIndex >= subspace.length - 1) {
			subspaceIndex = 0;
		} else {
			subspaceIndex++;
		}
		int currVar = subspace[subspaceIndex];
		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("iterating subspace");
			logger.finest("subspaceIndex = " + subspaceIndex);
			logger.finest("currVar = " + currVar);
		}
		classPick.setCurrVariableIndex(currVar);
		String varName = varNames[currVar];
		DimensionEvent dimEvent = new DimensionEvent(this, currVar, varName);
		fireDimensionChanged(dimEvent);
	}

	public void subspaceChanged(SubspaceEvent e) {
		subspace = e.getSubspace();
		iterateSubspace();
	}

	public void dataSetChanged(DataSetEvent e) {
		data = e.getDataSetForApps();
		maxIndication = data.getAttrData().getNumObservations() - 1;
		classPick.removeActionListener(this);
		classPick.setDataSet(data);
		classPick.addActionListener(this);

		initObs();
		classPick.fireClassificationChanged();
		subspace = new int[data.getAttrData().getNumberNumericAttributes()];
		varNames = data.getAttrData().getNumericAttributeNames();
		for (int i = 0; i < subspace.length; i++) {
			subspace[i] = i; // oh, the agony
		}
	}

	private void initObs() {
		obs = new ClassedObs[data.getAttrData().getNumObservations()];
		for (int i = 0; i < obs.length; i++) {
			obs[i] = new ClassedObs();
			obs[i].index = i;
		}
	}

	public void classificationChanged(ClassificationEvent e) {
		classes = e.getClassification();
		if (e.getSource() == classPick) {
			values = data.getAttrData().getNumericDataAsDouble(classPick
					.getCurrVariableIndex());
			if (classes.length != values.length) {
				return;
			}
			if (obs.length != values.length) {
				initObs();
			}
			for (int i = 0; i < obs.length; i++) {
				int index = obs[i].index;

				double aVal = values[index];
				obs[i].value = aVal;
			}
		} else {
			for (int i = 0; i < obs.length; i++) {
				int index = obs[i].index;

				obs[i].value = classes[index];
			}
		}

		reclassObs();
	}

	/**
	 * adds an DimensionListener
	 */
	public void addDimensionListener(DimensionListener l) {
		listenerList.add(DimensionListener.class, l);
	}

	/**
	 * removes an DimensionListener from the component
	 */
	public void removeDimensionListener(DimensionListener l) {
		listenerList.remove(DimensionListener.class, l);
	}

	/**
	 * Notify all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @see EventListenerList
	 */
	private void fireDimensionChanged(DimensionEvent e) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();

		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == DimensionListener.class) {

				((DimensionListener) listeners[i + 1]).dimensionChanged(e);
			}
		}

		// next i
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
	private void fireIndicationChanged(int newIndication) {
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
		}

		// next i
	}

	public static void main(String[] args) {
		IndicationAnimator inAnim = new IndicationAnimator();

		JFrame app = new JFrame();
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.getContentPane().setLayout(new BorderLayout());

		app.getContentPane().add(inAnim);
		app.pack();
		app.setVisible(true);
	}

	private class ClassedObs implements Comparable {
		int index;

		double value;

		// we compare by value
		public int compareTo(Object o) {
			ClassedObs e = (ClassedObs) o;
			int val = 0;
			if (Double.isNaN(e.value)) {
				if (Double.isNaN(value)) {
					return 0;
				}
				return 1;
			} // end if the other value is NaN

			if (Double.isNaN(value)) {
				val = -1; // everything is bigger than NaN
			} else if (value < e.value) {
				val = -1;
			} else if (value > e.value) {
				val = 1;
			}

			return val;
		}
	}
}
