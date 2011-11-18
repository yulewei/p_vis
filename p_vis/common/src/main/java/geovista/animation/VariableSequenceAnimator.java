/* Licensed under LGPL v. 2.1 or any later version;
 see GNU LGPL for details.
 Original Author: Frank Hardisty */

package geovista.animation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.zjut.common.data.DataSetForApps;
import edu.zjut.common.event.DataSetEvent;
import edu.zjut.common.event.DataSetListener;
import edu.zjut.common.event.VariableSelectionEvent;
import edu.zjut.common.event.VariableSelectionListener;


/**
 * VariableSequenceAnimator is used to send out variable index that corrispond
 * to current variable displayed.
 * 
 */
public class VariableSequenceAnimator extends JPanel implements ActionListener,
		ChangeListener, DataSetListener {
	private final Timer ticker;

	private transient JButton startStopButton;
	private transient boolean going = false;
	private int speed; // in milliseconds
	private transient DataSetForApps data;

	private transient ClassedObs[] obs;
	private transient JSlider timeSlider;

	private transient JComboBox varCombo;
	private transient boolean varComboIsAdjusting;

	private transient JRadioButton xVarButton;
	private transient JRadioButton yVarButton;
	private transient int currentVarIndex;

	/**
	 * null ctr
	 */
	public VariableSequenceAnimator() {
		speed = 1000;
		ticker = new Timer(speed, this);

		this.add(makeTopPanel());
		setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

	}

	private JPanel makeTopPanel() {
		JPanel topPanel = new JPanel();
		startStopButton = new JButton("Start");
		topPanel.add(startStopButton);
		startStopButton.addActionListener(this);

		varCombo = new JComboBox();
		varCombo.addActionListener(this);
		topPanel.add(varCombo);

		xVarButton = new JRadioButton("Iterate over X variable");
		yVarButton = new JRadioButton("Iterate over Y variable");
		xVarButton.setSelected(true);
		xVarButton.addActionListener(this);
		yVarButton.addActionListener(this);

		ButtonGroup buttGroup = new ButtonGroup();
		buttGroup.add(xVarButton);
		buttGroup.add(yVarButton);

		JPanel buttonHolder = new JPanel();
		BoxLayout boxLayout = new BoxLayout(buttonHolder, BoxLayout.Y_AXIS);
		buttonHolder.setLayout(boxLayout);

		buttonHolder.add(xVarButton);
		buttonHolder.add(yVarButton);

		topPanel.add(buttonHolder);
		timeSlider = new JSlider(1, 10, 5);
		topPanel.add(timeSlider);
		timeSlider.addChangeListener(this);
		return topPanel;
	}

	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == timeSlider && !timeSlider.getValueIsAdjusting()) {
			speed = timeSlider.getValue() * 50;
			ticker.setDelay(speed);
		}
	}

	private void iterateVariables() {
		if (currentVarIndex < data.getAttrData().getNumberNumericAttributes()) { // go up one
			// this.fireConditioningChanged(this.currConditioning);
			fireVariableSelectionChanged(currentVarIndex);
			ticker.setDelay(speed);
			currentVarIndex++;

		} else {
			ticker.setDelay(speed * 10);
			currentVarIndex = 0;
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (data == null) {
			// without data, we don't do anything
			return;
		}
		if (e.getSource() == ticker) {
			iterateVariables();
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
		} else if (e.getSource() == xVarButton) {
		} else if (e.getSource() == yVarButton) {
		} else if (e.getSource() == varCombo) {
			if (obs != null && varComboIsAdjusting == false) { // might be null
				// if we are
				// getting
				// started
				currentVarIndex = varCombo.getSelectedIndex();
			}
		}
	}

	public void dataSetChanged(DataSetEvent e) {
		data = e.getDataSetForApps();
		varCombo.removeAllItems();
		String[] numericVarNames = data.getAttrData().getNumericAttributeNames();
		for (String element : numericVarNames) {
			varCombo.addItem(element);
		}
		currentVarIndex = 0;
	}

	/**
	 * adds an VariableSelectionListener
	 */
	public void addVariableSelectionListener(VariableSelectionListener l) {
		listenerList.add(VariableSelectionListener.class, l);
	}

	/**
	 * removes an VariableSelectionListener from the component
	 */
	public void removeVariableSelectionListener(VariableSelectionListener l) {
		listenerList.remove(VariableSelectionListener.class, l);
	}

	/**
	 * Notify all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @see EventListenerList
	 */
	private void fireVariableSelectionChanged(int newVariableSelection) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		VariableSelectionEvent e = null;

		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == VariableSelectionListener.class) {
				// Lazily create the event:
				if (e == null) {
					e = new VariableSelectionEvent(this, newVariableSelection);
				}

				((VariableSelectionListener) listeners[i + 1])
						.variableSelectionChanged(e);
			}
		}
		// next i
	}

	private class ClassedObs implements Comparable {

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
