package edu.zjut.common.ctrl;

import java.awt.BorderLayout;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import edu.zjut.common.data.DataSetBroadcaster;
import edu.zjut.common.data.DataSetForApps;
import edu.zjut.common.data.attr.AttributeData;
import edu.zjut.common.data.attr.DimensionField;
import edu.zjut.common.data.attr.MeasureField;
import edu.zjut.common.data.geo.GeoLayer;
import edu.zjut.common.data.geo.GeometryData;
import edu.zjut.common.data.time.TimeData;
import edu.zjut.common.event.DataSetEvent;
import edu.zjut.common.event.DataSetListener;
import edu.zjut.common.io.DataSetLoader;
import edu.zjut.coordination.CoordinationManager;

public class DataWindow extends JPanel implements DataSetListener {

	private static final long serialVersionUID = 1L;
	final static Logger logger = Logger.getLogger(DataWindow.class.getName());

	private DataSetForApps dataSet;
	private AttributeData attrData;
	private GeometryData geoData;
	private TimeData timeData;

	JPanel attrPanel;
	JPanel geoPanel;
	JPanel timePanel;

	FieldList<DimensionField> dimensionList;
	FieldList<MeasureField> measureList;
	FieldList<String> layerList;
	FieldList<String> timeList;

	public DataWindow() {
		super();

		setLayout(new BorderLayout());

		JTabbedPane jTabbedPane1 = new JTabbedPane();
		add(jTabbedPane1);

		initAttrPanel();
		jTabbedPane1.addTab("attr", null, attrPanel, null);

		initGeoPanel();
		jTabbedPane1.addTab("geo", null, geoPanel, null);

		initTimePanel();
		jTabbedPane1.addTab("time", null, timePanel, null);
	}

	private void initAttrPanel() {
		attrPanel = new JPanel();

		attrPanel.setLayout(new BoxLayout(attrPanel, BoxLayout.Y_AXIS));

		JPanel jPanel1 = new JPanel();
		jPanel1.setLayout(new BorderLayout());
		attrPanel.add(jPanel1);

		JLabel dimensionLabel = new JLabel("Dimensions");
		dimensionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		jPanel1.add(dimensionLabel, BorderLayout.NORTH);

		dimensionList = new FieldList<DimensionField>();
		dimensionList.setVisibleRowCount(10);
		dimensionList.setDragEnabled(true);
		dimensionList.setTransferHandler(new FieldExporter<DimensionField>(
				DimensionField.class));

		jPanel1.add(new JScrollPane(dimensionList), BorderLayout.CENTER);

		JPanel jPanel2 = new JPanel();
		jPanel2.setLayout(new BorderLayout());
		attrPanel.add(jPanel2);

		JLabel measureLabel = new JLabel("Measures");
		measureLabel.setHorizontalAlignment(SwingConstants.CENTER);
		jPanel2.add(measureLabel, BorderLayout.NORTH);

		measureList = new FieldList<MeasureField>();
		measureList.setVisibleRowCount(10);
		measureList.setDropMode(DropMode.ON_OR_INSERT);
		measureList.setDragEnabled(true);
		measureList.setTransferHandler(new FieldExporter<MeasureField>(
				MeasureField.class));

		jPanel2.add(new JScrollPane(measureList), BorderLayout.CENTER);
	}

	private void initGeoPanel() {
		geoPanel = new JPanel();

		geoPanel.setLayout(new BoxLayout(geoPanel, BoxLayout.Y_AXIS));

		JPanel jPanel1 = new JPanel();
		jPanel1.setLayout(new BorderLayout());
		geoPanel.add(jPanel1);

		JLabel layerLabel = new JLabel("Layers");
		layerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		jPanel1.add(layerLabel, BorderLayout.NORTH);

		layerList = new FieldList<String>();
		layerList.setVisibleRowCount(10);
		layerList.setDropMode(DropMode.ON_OR_INSERT);
		layerList.setDragEnabled(true);
		
		jPanel1.add(new JScrollPane(layerList), BorderLayout.CENTER);
	}

	private void initTimePanel() {
		timePanel = new JPanel();
		
		timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));

		JPanel jPanel1 = new JPanel();
		jPanel1.setLayout(new BorderLayout());
		timePanel.add(jPanel1);

		JLabel timeLabel = new JLabel("TimeSeries");
		timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		jPanel1.add(timeLabel, BorderLayout.NORTH);

		timeList = new FieldList<String>();
		jPanel1.add(timeList, BorderLayout.CENTER);
	}

	public void dataSetChanged(DataSetEvent e) {
		dataSet = e.getDataSetForApps();
		attrData = dataSet.getAttrData();
		geoData = dataSet.getGeoData();
		timeData = dataSet.getTimeData();

		DimensionField[] dimensionFields = attrData.getDimensionFields();
		MeasureField[] measureFields = attrData.getMeasureFields();

		DefaultListModel<DimensionField> dimensionModel = new DefaultListModel<DimensionField>();
		for (DimensionField field : dimensionFields) {
			dimensionModel.addElement(field);
		}

		DefaultListModel<MeasureField> measureModel = new DefaultListModel<MeasureField>();
		for (MeasureField field : measureFields) {
			measureModel.addElement(field);
		}

		dimensionList.setModel(dimensionModel);
		measureList.setModel(measureModel);

		List<GeoLayer> layers = geoData.getLayers();
		DefaultListModel<String> layerModel = new DefaultListModel<String>();
		for (GeoLayer layer : layers) {
			layerModel.addElement(layer.getLayerName());
		}

		layerList.setModel(layerModel);

		repaint();
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
