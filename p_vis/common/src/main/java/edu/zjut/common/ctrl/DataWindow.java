package edu.zjut.common.ctrl;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import edu.zjut.common.data.DataSetBroadcaster;
import edu.zjut.common.data.DataSetForApps;
import edu.zjut.common.data.attr.AttributeData;
import edu.zjut.common.data.attr.DataField;
import edu.zjut.common.data.attr.DimensionField;
import edu.zjut.common.data.attr.MeasureField;
import edu.zjut.common.event.DataSetEvent;
import edu.zjut.common.event.DataSetListener;
import edu.zjut.common.event.SubspaceEvent;
import edu.zjut.common.event.SubspaceListener;
import edu.zjut.common.io.DataSetLoader;
import edu.zjut.coordination.CoordinationManager;

public class DataWindow extends JPanel implements DataSetListener,
		SubspaceListener, ActionListener {

	private static final long serialVersionUID = 1L;
	final static Logger logger = Logger.getLogger(DataWindow.class.getName());

	DataSetForApps dataSet;
	AttributeData attrData;
	DimensionField[] dimensionFields;
	MeasureField[] measureFields;

	private JPanel jPanel1;
	private JPanel jPanel2;

	JButton dimensionButton;
	JButton measureButton;
	FieldList<DataField> dimensionList;
	FieldList<DataField> measureList;

	// Creates Variable Picker
	public DataWindow() {
		super();

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		jPanel1 = new JPanel();
		jPanel1.setLayout(new BorderLayout());
		add(jPanel1);

		dimensionButton = new JButton("Dimensions");
		dimensionButton.addActionListener(this);
		jPanel1.add(dimensionButton, BorderLayout.NORTH);

		dimensionList = new FieldList<DataField>(FieldList.DIMENSION);
		dimensionList.setVisibleRowCount(10);
		dimensionList.setDragEnabled(true);

		JScrollPane scrollPane1 = new JScrollPane();
		JViewport scrollView1 = new JViewport();
		scrollView1.add(dimensionList);
		scrollPane1.setViewport(scrollView1);
		jPanel1.add(scrollPane1, BorderLayout.CENTER);

		jPanel2 = new JPanel();
		jPanel2.setLayout(new BorderLayout());
		add(jPanel2);

		measureButton = new JButton("Measures");
		measureButton.addActionListener(this);
		jPanel2.add(measureButton, BorderLayout.NORTH);

		measureList = new FieldList<DataField>(FieldList.MEASURE);
		measureList.setVisibleRowCount(10);
		measureList.setDropMode(DropMode.ON_OR_INSERT);
		measureList.setDragEnabled(true);

		JScrollPane scrollPane2 = new JScrollPane();
		JViewport scrollView2 = new JViewport();
		scrollView2.add(measureList);
		scrollPane2.setViewport(scrollView2);
		jPanel2.add(scrollPane2, BorderLayout.CENTER);
	}

	public void dataSetChanged(DataSetEvent e) {
		dataSet = e.getDataSetForApps();

		attrData = dataSet.getAttrData();

		dimensionFields = attrData.getDimensionFields();
		measureFields = attrData.getMeasureFields();

		DefaultListModel<DataField> listModel1 = new DefaultListModel<DataField>();
		for (int i = measureFields.length - 1; i >= 0; i--) {
			listModel1.add(0, measureFields[i]);
		}

		DefaultListModel<DataField> listModel2 = new DefaultListModel<DataField>();
		for (int i = dimensionFields.length - 1; i >= 0; i--) {
			listModel2.add(0, dimensionFields[i]);
		}

		measureList.setModel(listModel1);
		dimensionList.setModel(listModel2);

		repaint();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == measureButton) {
			fireSubspaceChanged(measureList.getSelectedIndices());
		}
	}

	public void subspaceChanged(SubspaceEvent e) {
		int[] subspace = e.getSubspace();
		measureList.setSelectedIndices(subspace);
	}

	public void fireSubspaceChanged(int[] selection) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		SubspaceEvent e = null;

		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SubspaceListener.class) {
				// Lazily create the event:
				if (e == null) {
					e = new SubspaceEvent(this, selection);
				}

				((SubspaceListener) listeners[i + 1]).subspaceChanged(e);
			}
		}
	}

	public void addSubspaceListener(SubspaceListener l) {
		listenerList.add(SubspaceListener.class, l);
	}

	public void removeSubspaceListener(SubspaceListener l) {
		listenerList.remove(SubspaceListener.class, l);
	}

	public static void main(String[] args) {
		String fileName = "hz_data/hz_house.xml";
		DataSetLoader loader = new DataSetLoader(fileName);
		DataSetForApps dataSet = loader.getDataForApps();

		CoordinationManager coord = new CoordinationManager();
		DataSetBroadcaster dataCaster = new DataSetBroadcaster();

		DataWindow dataWin = new DataWindow();
		coord.addBean(dataWin);
		coord.addBean(dataCaster);

		dataCaster.setAndFireDataSet(dataSet);

		JFrame jframe = new JFrame();
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setLocation(100, 100);
		jframe.setSize(300, 500);
		jframe.add(dataWin);
		jframe.setVisible(true);
	}
}
