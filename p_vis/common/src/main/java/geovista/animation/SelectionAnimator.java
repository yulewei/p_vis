/* Licensed under LGPL v. 2.1 or any later version;
 see GNU LGPL for details.
 Original Author: Frank Hardisty */

package geovista.animation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Vector;
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
import edu.zjut.common.event.SelectionEvent;
import edu.zjut.common.event.SelectionListener;
import edu.zjut.common.event.SubspaceEvent;
import edu.zjut.common.event.SubspaceListener;

import geovista.common.classification.ClassifierPicker;

/**
 * SelectionAnimator is used to send out indication signals that corrispond to
 * current classifications.
 * 
 * @author Frank Hardisty
 * 
 */
public class SelectionAnimator extends JPanel implements ActionListener,
		ChangeListener, DataSetListener, SubspaceListener,
		ClassificationListener {
	private final Timer ticker;
	private transient int currClassIndex;
	private final Vector selections;
	private transient JButton startStopButton;
	private transient JButton clearSelectionButton;
	private transient boolean going = false;
	private int speed; // in milliseconds
	private transient DataSetForApps data;
	private transient int maxClass = 0;
	private final ClassifierPicker classPick;
	private transient int[] classes;
	private transient ClassedObs[] obs;
	private transient int[] subspace;
	private transient int subspaceIndex;
	private transient JSlider timeSlider;
	private transient JCheckBox subspaceBox;
	private boolean usingSubspace;
	private transient int[] tempArray;
	final static Logger logger = Logger.getLogger(SelectionAnimator.class
			.getName());

	/**
	 * null ctr
	 */
	public SelectionAnimator() {
		usingSubspace = true;
		speed = 250;
		selections = new Vector();
		ticker = new Timer(speed, this);

		this.add(makeTopPanel());

		classPick = new ClassifierPicker();
		classPick
				.setVariableChooserMode(ClassifierPicker.VARIABLE_CHOOSER_MODE_ACTIVE);
		this.add(classPick);
		classPick.addClassificationListener(this);
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
		timeSlider = new JSlider(1, 10, 5);
		topPanel.add(timeSlider);
		timeSlider.addChangeListener(this);

		clearSelectionButton = new JButton("Clear Selection");
		topPanel.add(clearSelectionButton);
		clearSelectionButton.addActionListener(this);

		return topPanel;
	}

	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == timeSlider && !timeSlider.getValueIsAdjusting()) {
			speed = timeSlider.getValue() * 50;
			ticker.setDelay(speed);
		}
	}

	private void iterateSelections() { // main loop

		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("speed = " + speed);
			logger.finest("index = " + obs[currClassIndex].index);
		}

		if (currClassIndex <= maxClass && selections.size() > currClassIndex) { // go
			// up
			// one
			int[] whichClass = (int[]) selections.get(currClassIndex);
			fireSelectionChanged(whichClass);

			ticker.setDelay(speed);
			currClassIndex++;
		} else { // go back to zero
			ticker.setDelay(speed * 10);

			currClassIndex = 0; // reset
			if (usingSubspace) {
				iterateSubspace();
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (data == null) {
			// without data, we don't do anything
			return;
		}
		if (e.getSource() == ticker) {
			iterateSelections();
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
		} else if (e.getSource() == clearSelectionButton) {
			fireSelectionChanged(new int[0]);
		}
	}

	private void reclassObs() {
		if (data == null) {
			return;
		}
		Arrays.sort(obs);
		selections.removeAllElements();

		maxClass = obs[obs.length - 1].classed;
		int classCounter = 0;
		int prevClass = -1;
		for (int i = 0; i < obs.length; i++) {
			int currClass = obs[i].classed;
			if (currClass != prevClass) {
				// new class
				classCounter = 0;
				prevClass = currClass;
				tempArray[classCounter] = obs[i].index;
			} else {
				classCounter++;
				tempArray[classCounter] = obs[i].index;

			}
			// peek ahead for either last obs or change in class
			if ((i == obs.length - 1) || (obs[i + 1].classed != currClass)) {
				int[] thisClass = new int[classCounter + 1];
				// copy temp array into thisClass;
				for (int j = 0; j <= classCounter; j++) {
					thisClass[j] = tempArray[j];
				}
				// put thisClass into vector
				selections.add(thisClass);

			}
		}
	}

	private void iterateSubspace() {

		if (subspaceIndex + 1 >= subspace.length) {
			subspaceIndex = 0;
		} else {
			subspaceIndex++;
		}

		int currVar = subspace[subspaceIndex]; //
		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("subspace.length = " + subspace.length);
			logger.finest("subspaceIndex = " + subspaceIndex);
			logger.finest("currVar = " + currVar);

		}

		classPick.setCurrVariableIndex(currVar);
	}

	public void subspaceChanged(SubspaceEvent e) {
		subspace = e.getSubspace();
		iterateSubspace();
	}

	public void dataSetChanged(DataSetEvent e) {

		data = e.getDataSetForApps();

		classPick.setDataSet(e.getDataSetForApps());
		tempArray = new int[data.getAttrData().getNumObservations()];

		obs = new ClassedObs[data.getAttrData().getNumObservations()];
		for (int i = 0; i < obs.length; i++) {
			obs[i] = new ClassedObs();
			obs[i].index = i;
		}
		classPick.fireClassificationChanged();
		subspace = new int[data.getAttrData().getNumberNumericAttributes()];
		for (int i = 0; i < subspace.length; i++) {
			subspace[i] = i;// + 1; //oh, the agony
		}
	}

	public void classificationChanged(ClassificationEvent e) {
		classes = e.getClassification();
		for (int i = 0; i < obs.length; i++) {
			int index = obs[i].index;
			obs[i].classed = classes[index];
		}

		reclassObs();
	}

	/**
	 * adds an SelectionListener
	 */
	public void addSelectionListener(SelectionListener l) {
		listenerList.add(SelectionListener.class, l);
	}

	/**
	 * removes an SelectionListener from the component
	 */
	public void removeSelectionListener(SelectionListener l) {
		listenerList.remove(SelectionListener.class, l);
	}

	/**
	 * Notify all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @see EventListenerList
	 */
	private void fireSelectionChanged(int[] newSelection) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		SelectionEvent e = null;

		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SelectionListener.class) {
				// Lazily create the event:
				if (e == null) {
					e = new SelectionEvent(this, newSelection);
				}

				((SelectionListener) listeners[i + 1]).selectionChanged(e);
			}
		}

		// next i
	}

	public static void main(String[] args) {
		SelectionAnimator inAnim = new SelectionAnimator();

		JFrame app = new JFrame();
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.getContentPane().setLayout(new BorderLayout());

		app.getContentPane().add(inAnim);
		app.pack();
		app.setVisible(true);
	}

	private class ClassedObs implements Comparable {
		int index;
		int classed;

		// we compare by classed
		public int compareTo(Object o) {
			ClassedObs e = (ClassedObs) o;
			int val = 0;
			if (Double.isNaN(e.classed)) {
				if (Double.isNaN(classed)) {
					return 0;
				}
				return 1;
			} // end if the other classed is NaN

			if (Double.isNaN(classed)) {
				val = -1; // everything is bigger than NaN
			} else if (classed < e.classed) {
				val = -1;
			} else if (classed > e.classed) {
				val = 1;
			}

			return val;
		}
	}
}
