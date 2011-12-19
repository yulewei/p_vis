package edu.zjut.vis.core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.zjut.common.ctrl.DataWindow;
import edu.zjut.common.data.DataSetBroadcaster;
import edu.zjut.common.data.DataSetForApps;
import edu.zjut.common.event.DataSetEvent;
import edu.zjut.common.event.DataSetListener;
import edu.zjut.common.event.IndicationEvent;
import edu.zjut.common.event.IndicationListener;
import edu.zjut.common.event.SelectionEvent;
import edu.zjut.common.event.SelectionListener;
import edu.zjut.common.event.SubspaceEvent;
import edu.zjut.common.event.SubspaceListener;
import edu.zjut.common.io.DataSetLoader;
import edu.zjut.common.ui.ShapeReporter;
import edu.zjut.coordination.CoordinationManager;
import edu.zjut.coordination.FiringBean;
import edu.zjut.vis.map.GeoMap;
import edu.zjut.vis.marshal.Marshaler;
import edu.zjut.vis.pcp.ParallelPlot;
import edu.zjut.vis.time.TimeSeries;
import edu.zjut.vis.treemap.Treemap;
import edu.zjut.vis.util.IconUtils;

/**
 * 
 * @author yulewei
 */
public class VisualAnalyzer extends JFrame implements IndicationListener {

	final static Logger logger = Logger.getLogger(VisualAnalyzer.class
			.getName());

	String fileName;
	DataSetForApps dataSet;
	CoordinationManager coord;
	DataSetBroadcaster dataCaster;

	VizState vizState;
	HashMap<String, ToolkitBean> tBeanSet;
	IndicationConnectUI<JComponent> indUI;

	private JDesktopPane desktop;
	private JToolBar toolBar;
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem loadDataItem;
	private JMenuItem exitItem;
	private JMenuItem openStateItem;
	private JMenuItem saveStateItem;
	private JMenuItem captureMainItem;
	private JMenuItem captureSelectedItem;
	private JMenu addToolMenu;
	private JMenu removeToolMenu;
	private JMenuItem removeAllItem;
	private JMenu helpMenu;
	private JMenuItem aboutItem;

	public VisualAnalyzer(String fileName) {
		super("Visual Analyzer");

		initUI();
		initTools();
		initData(fileName);
	}

	private void initData(String fileName) {
		if (fileName != null)
			this.fileName = fileName;

		setTitle("Visual Analyzer - " + this.fileName);

		// vizState = Utils.openDefaultLayout();
		// vizState = Utils.getVizStateFromFile("vis_state.xml");
		// vizState = Utils.getVizStateFromFile("treemap.xml");
		vizState = Utils.getVizStateFromFile("pcp2.xml");
		// vizState = Utils.getVizStateFromFile("map.xml");
		// vizState = Utils.getVizStateFromFile("time.xml");
		// vizState = Utils.getVizStateFromFile("final.xml");

		dataCaster = new DataSetBroadcaster();
		coord = new CoordinationManager();
		coord.addBean(this);
		coord.addBean(dataCaster);
		coord.addBean(vizState);

		DataSetLoader loader = new DataSetLoader(this.fileName);
		dataSet = loader.getDataForApps();

		setVizState(vizState);

		dataCaster.setAndFireDataSet(dataSet);
	}

	private void initUI() {
		this.setPreferredSize(new Dimension(1000, 800));

		desktop = new JDesktopPane();
		desktop.setBackground(Color.white);
		this.getContentPane().add(desktop, BorderLayout.CENTER);

		// JXLayer<JComponent> layer = new JXLayer<JComponent>(desktop);
		// indUI = new IndicationConnectUI<JComponent>();
		// getContentPane().add(layer, BorderLayout.CENTER);
		// layer.setUI(indUI);
		// setIndicationUI(true);

		toolBar = new JToolBar();
		this.getContentPane().add(toolBar, BorderLayout.NORTH);

		menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);

		fileMenu = new JMenu("File");
		loadDataItem = new JMenuItem("Open Data");
		openStateItem = new JMenuItem("Open State");
		saveStateItem = new JMenuItem("Save State");
		captureMainItem = new JMenuItem("Capture Main Window");
		captureSelectedItem = new JMenuItem("Capture Selected Window");

		// ¿ì½Ý¼ü
		openStateItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				InputEvent.CTRL_MASK));
		saveStateItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_MASK));
		captureMainItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
				InputEvent.CTRL_MASK));
		captureSelectedItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_P, 0));

		exitItem = new JMenuItem("Exit");
		fileMenu.add(loadDataItem);
		fileMenu.addSeparator();
		fileMenu.add(openStateItem);
		fileMenu.add(saveStateItem);
		fileMenu.addSeparator();
		fileMenu.add(captureMainItem);
		fileMenu.add(captureSelectedItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);

		addToolMenu = new JMenu("Add");
		removeToolMenu = new JMenu("Remove");
		removeAllItem = new JMenuItem("Remove All");
		removeToolMenu.addSeparator();
		removeToolMenu.add(removeAllItem);

		helpMenu = new JMenu("Help");
		aboutItem = new JMenuItem("About...");
		helpMenu.add(aboutItem);

		menuBar.add(fileMenu);
		menuBar.add(addToolMenu);
		menuBar.add(removeToolMenu);
		menuBar.add(helpMenu);

		loadDataItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

			}
		});

		openStateItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				loadState();
			}
		});
		saveStateItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				saveState();
			}
		});

		captureMainItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				saveCaptureImage(VisualAnalyzer.this);
			}
		});
		captureSelectedItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				saveCaptureImage(desktop.getSelectedFrame());
			}
		});

		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.exit(0);
			}
		});

		removeAllItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				removeAllBeans();
			}
		});

		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JOptionPane.showMessageDialog(VisualAnalyzer.this,
						"Visual Analyzer", "About",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}

	private void loadState() {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"XML file", "xml");
		chooser.setFileFilter(filter);
		chooser.setCurrentDirectory(new File("."));

		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			VizState state = Utils.getVizStateFromFile(chooser
					.getSelectedFile().getPath());
			setVizState(state);
			repaint();
		}
	}

	private void saveState() {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"XML file", "xml");
		chooser.setFileFilter(filter);
		chooser.setCurrentDirectory(new File("."));

		String fileName = "state.xml";
		File file = new File(fileName);
		chooser.setSelectedFile(file);
		int returnVal = chooser.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			Marshaler marsh = Marshaler.INSTANCE;
			String xml = marsh.toXML(getVizState());
			Utils.writeLayout(chooser.getSelectedFile(), xml);
		}
	}

	private void saveCaptureImage(Component comp) {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"PNG image", "png");
		chooser.setFileFilter(filter);
		chooser.setCurrentDirectory(new File("."));

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd-HHmmss");
		String fileName = String
				.format("%s.png", dateFormat.format(new Date()));
		File file = new File(fileName);
		chooser.setSelectedFile(file);
		int returnVal = chooser.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			Utils.saveImage(this, chooser.getSelectedFile());
			Utils.saveImage(comp, chooser.getSelectedFile());
		}
	}

	protected void initTools() {

		Class<?>[] toolClassArr = { DataWindow.class, GeoMap.class,
				ParallelPlot.class, Treemap.class, TimeSeries.class };

		for (Class<?> toolClass : toolClassArr) {
			String shortName = toolClass.getSimpleName();
			Icon smallIcon = IconUtils.findSmallIcon(toolClass);
			Icon bigIcon = IconUtils.findIcon(toolClass);

			JMenuItem toolItem = new JMenuItem(shortName, smallIcon);
			addToolMenu.add(toolItem);
			JButton toolButton = new JButton(bigIcon);
			toolButton.setToolTipText(shortName);
			toolBar.add(toolButton);

			AddListener addListener = new AddListener(toolClass);
			toolItem.addActionListener(addListener);
			toolButton.addActionListener(addListener);
			toolButton.addMouseListener(addListener);
			toolButton.addMouseMotionListener(addListener);
		}
	}

	public void setVizState(VizState vizState) {
		if (tBeanSet != null)
			removeAllBeans();

		tBeanSet = vizState.getBeanSet();

		for (ToolkitBean tBean : tBeanSet.values()) {
			Object newBean = tBean.getOriginalBean();
			coord.addBean(newBean);
			addToolkitBean(tBean);
			tBeanSet.put(tBean.getUniqueName(), tBean);
			fireNewBeanMethods(newBean);
		}
		for (ToolkitBean tBean : tBeanSet.values()) {
			desktop.setComponentZOrder(tBean.getInternalFrame(), tBean.zOrder);
		}

		ToolkitBean tBean = tBeanSet.get(vizState.getSelectedBean());

		if (tBean != null) {
			desktop.setSelectedFrame(tBean.getInternalFrame());

			Object newBean = tBean.getOriginalBean();
			fireNewBeanMethods(newBean);
		}
	}

	public VizState getVizState() {
		updateVizState();
		return vizState;
	}

	private void updateVizState() {
		vizState.setBeanSet(tBeanSet);
		for (ToolkitBean tBean : tBeanSet.values()) {
			tBean.zOrder = desktop.getComponentZOrder(tBean.getInternalFrame());
		}

		JInternalFrame selectedFrame = desktop.getSelectedFrame();
		ToolkitBean tBean = getToolkitBean(selectedFrame);
		vizState.setSelectedBean(tBean.getUniqueName());
	}

	private class AddListener extends MouseAdapter implements ActionListener {
		private Class<?> toolClass;
		private boolean isDragged = false;

		public AddListener(Class<?> toolClass) {
			this.toolClass = toolClass;
		}

		public void actionPerformed(ActionEvent evt) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			addNewBean(toolClass);
			setCursor(Cursor.getDefaultCursor());
		}

		public void mouseDragged(MouseEvent evt) {
			isDragged = true;
			setCursor(DragSource.DefaultMoveDrop);
		}

		public void mouseReleased(MouseEvent evt) {
			if (!isDragged)
				return;

			isDragged = false;

			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			ToolkitBean newToolkitBean = addNewBean(toolClass);

			JInternalFrame internalFrame = newToolkitBean.getInternalFrame();
			int x = Math.max(evt.getX() - 50, 0);
			int y = Math.max(evt.getY() - 50, 0);
			internalFrame.setLocation(x, y);

			setCursor(Cursor.getDefaultCursor());
		}
	}

	private class RemoveListener extends InternalFrameAdapter implements
			ActionListener {
		private ToolkitBean tBean;

		public RemoveListener(ToolkitBean tBean) {
			this.tBean = tBean;
		}

		public void internalFrameClosing(InternalFrameEvent e) {
			removeToolkitBean(tBean);
			tBeanSet.remove(tBean.getUniqueName());
		}

		public void actionPerformed(ActionEvent evt) {
			removeToolkitBean(tBean);
			tBeanSet.remove(tBean.getUniqueName());
		}
	}

	private ToolkitBean addNewBean(Class<?> toolClass) {
		Object newBean = null;
		try {
			newBean = toolClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

		FiringBean newFBean = coord.addBean(newBean);
		String uniqueName = newFBean.getBeanName();

		ToolkitBean tBean = new ToolkitBean(newBean, uniqueName);
		addToolkitBean(tBean);
		tBeanSet.put(tBean.getUniqueName(), tBean);

		fireNewBeanMethods(newBean);

		return tBean;
	}

	/**
	 * Adds bean to the main gui area, also to the remove bean menu
	 */
	private void addToolkitBean(ToolkitBean tBean) {
		JInternalFrame internalFrame = tBean.getInternalFrame();
		desktop.add(internalFrame, 0);

		JMenuItem toolItem = tBean.getRemoveMenuItem();
		removeToolMenu.add(toolItem, 0);

		RemoveListener removeListener = new RemoveListener(tBean);
		internalFrame.addInternalFrameListener(removeListener);
		toolItem.addActionListener(removeListener);
	}

	private void removeToolkitBean(ToolkitBean tBean) {
		JMenuItem item = tBean.getRemoveMenuItem();
		removeToolMenu.remove(item);
		coord.removeBean(tBean.getOriginalBean());

		JInternalFrame iFrame = tBean.getInternalFrame();
		desktop.remove(iFrame);
		desktop.repaint();
	}

	/**
	 * deleting all beans
	 */
	private void removeAllBeans() {
		for (ToolkitBean bean : tBeanSet.values()) {
			removeToolkitBean(bean);
		}
		tBeanSet.clear();
	}

	private void fireNewBeanMethods(Object newBean) {
		if (dataSet == null) {
			logger.severe("data set is null");
			return;
		}

		if (newBean instanceof DataSetListener) {
			DataSetListener dataListener = (DataSetListener) newBean;
			dataListener.dataSetChanged(new DataSetEvent(dataSet, this));
		}

		if (newBean instanceof SubspaceListener) {
			SubspaceListener selListener = (SubspaceListener) newBean;
			SubspaceEvent e = vizState.getSubspaceEvent();
			if (e.getSubspace() != null) {
				selListener.subspaceChanged(e);
			}
		}

		if (newBean instanceof SelectionListener) {
			SelectionListener selListener = (SelectionListener) newBean;
			SelectionEvent e = vizState.getSelectionEvent();
			if (e.getSelection() != null) {
				selListener.selectionChanged(e);
			}
		}
		if (newBean instanceof IndicationListener) {
			IndicationListener indListener = (IndicationListener) newBean;
			IndicationEvent e = vizState.getIndicationEvent();
			indListener.indicationChanged(e);
		}
	}

	void pinBean(ToolkitBean beanToPin) {
		Point location = beanToPin.getInternalFrame().getLocation();
		int index = desktop.getIndexOf(beanToPin.getInternalFrame());
		Dimension size = beanToPin.getInternalFrame().getSize();
		desktop.remove(beanToPin.getInternalFrame());
		Component comp = (Component) beanToPin.getOriginalBean();
		desktop.add(comp, index);
		comp.setSize(size);
		comp.setLocation(location);
	}

	void unPinBean(ToolkitBean unPinBean) {
		Component comp = (Component) unPinBean.getOriginalBean();
		Point location = comp.getLocation();
		int index = desktop.getIndexOf(comp);
		Dimension size = comp.getSize();
		desktop.remove(comp);
		JInternalFrame fram = unPinBean.getInternalFrame();
		fram.add(comp);
		desktop.add(fram, index);
		fram.setSize(size);
		fram.setLocation(location);
	}

	public static void main(String[] args) {

		try {
			UIManager
					.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Logger logger = Logger.getLogger("vis");
		logger.setLevel(Level.INFO);
		logger.info(System.getProperty("java.version"));

		try {
			FileHandler fHandler = new FileHandler("vis_app.log");
			fHandler.setFormatter(new SimpleFormatter());
			logger.addHandler(fHandler);
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.finest("java.version = " + System.getProperty("java.version"));

		String fileName = "hz_data/hz_house.xml";
		VisualAnalyzer app = new VisualAnalyzer(fileName);
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.setPreferredSize(new Dimension(800, 600));
		app.setMinimumSize(new Dimension(480, 320));
		app.setExtendedState(Frame.MAXIMIZED_BOTH);
		app.pack();
		app.setVisible(true);
	}

	public void indicationChanged(IndicationEvent e) {
		if (indUI == null || indUI.paintIndication == false)
			return;

		indUI.clear();
		if (e.getIndication() < 0)
			return;

		for (ToolkitBean tBean : tBeanSet.values()) {
			Object obj = tBean.getOriginalBean();

			if (obj instanceof ShapeReporter) {
				ShapeReporter shpR = (ShapeReporter) obj;
				Rectangle rect = shpR.reportShape().getBounds();
				Point pt = SwingUtilities.convertPoint((Component) obj, rect.x,
						rect.y, desktop);

				Component comp = desktop.getComponentAt(pt);

				if (comp == null)
					continue;

				if (comp instanceof JInternalFrame) {
					JInternalFrame iFrame = (JInternalFrame) comp;
					ToolkitBean tBeanA = getToolkitBean(iFrame);
					Object originalBean = tBeanA.getOriginalBean();
					if (obj == originalBean)
						indUI.addShape(shpR.reportShape(),
								shpR.renderingComponent());
				}
			}
		}
	}

	public ToolkitBean getToolkitBean(JInternalFrame iFrame) {
		if (iFrame == null)
			return null;

		for (ToolkitBean tBean : tBeanSet.values()) {
			JInternalFrame internalFrame = tBean.getInternalFrame();
			if (iFrame.equals(internalFrame))
				return tBean;
		}
		return null;
	}
}