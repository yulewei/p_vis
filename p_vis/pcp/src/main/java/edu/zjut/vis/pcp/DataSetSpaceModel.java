package edu.zjut.vis.pcp;

import edu.zjut.common.data.DataSetForApps;
import edu.zjut.common.data.attr.AttributeData;

import java.util.Vector;

import org.mediavirus.parvis.gui.ProgressEvent;
import org.mediavirus.parvis.gui.ProgressListener;
import org.mediavirus.parvis.model.SimpleParallelSpaceModel;

/**
 * 
 * ²Î¿¼ÊµÏÖ, {@link org.mediavirus.parvis.file.STFFile}
 * 
 * @author yulewei
 * 
 */
public class DataSetSpaceModel extends SimpleParallelSpaceModel {

	DataSetForApps dataSet;

	public DataSetSpaceModel(DataSetForApps dataSet) {
		this.dataSet = dataSet;
	}

	/**
	 * Reads the contents of the file and exposes them vis the
	 * ParallelSpaceModel interface of the class. String values are stripped out
	 * of the model and set as record labels.
	 */
	public void readContents() {

		fireProgressEvent(new ProgressEvent(this, ProgressEvent.PROGRESS_START,
				0.0f, "loading file"));

		parseData();

		fireProgressEvent(new ProgressEvent(this,
				ProgressEvent.PROGRESS_FINISH, 1.0f, "loading file"));

	}

	private void parseData() {
		AttributeData attrData = dataSet.getAttrData();

		int numDimensions = attrData.getNumMeasures();
		int numObservations = attrData.getNumObservations();

		String[] numericAttributeNames = attrData.getMeasureNames();
		String[] observationNames = attrData.getObservationNames();

		this.initNumDimensions(numDimensions);
		this.setAxisLabels(numericAttributeNames);

		for (int i = 0; i < numObservations; i++) {
			float[] dataVals = new float[numDimensions];
			Object[] numericArrays = attrData.getMeasures();
			for (int j = 0; j < numDimensions; j++) {
				Object colum = numericArrays[j];
				if (colum instanceof double[]) {
					double[] someDoubles = (double[]) colum;
					dataVals[j] = (float) someDoubles[i];
				} else if (colum instanceof int[]) {
					int[] someInts = (int[]) colum;
					dataVals[j] = someInts[i];
				}
			}
			this.addRecord(dataVals, observationNames[i]);
		}
	}

	private Vector<ProgressListener> progressListeners = new Vector<ProgressListener>();

	/**
	 * Method to add a ProgressListener to get notified of the loading progress.
	 */
	public void addProgressListener(ProgressListener l) {
		progressListeners.add(l);
	}

	/**
	 * Remove a ProgressListener.
	 */
	public void removeProgressListener(ProgressListener l) {
		progressListeners.remove(l);
	}

	/**
	 * Dispatches a ProgressEvent to all listeners.
	 * 
	 * @param e
	 *            The ProgressEvent to send.
	 */
	protected void fireProgressEvent(ProgressEvent e) {
		Vector<ProgressListener> list = (Vector<ProgressListener>) progressListeners
				.clone();
		for (int i = 0; i < list.size(); i++) {
			ProgressListener l = (ProgressListener) list.elementAt(i);
			l.processProgressEvent(e);
		}
	}
}
