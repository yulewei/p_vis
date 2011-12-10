package edu.zjut.vis.core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

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
public class GeoVizToolkit extends JFrame implements IndicationListener {

	final static Logger logger = Logger
			.getLogger(GeoVizToolkit.class.getName());

	final GeoVizToolkit that = this;

	String fileName;
	DataSetBroadcaster dataCaster;
	CoordinationManager coord;
	DataSetForApps dataSet;

	IndicationConnectUI<JComponent> indUI;

	HashMap<JComponent, String> classNameHashMap;

	// collection of active beans
	HashMap<String, ToolkitBean> tBeanSet;

	// Create JDesktopPane to hold the internal frames
	JDesktopPane desktop = new JDesktopPane();

	private JToolBar toolBar;

	JMenuBar menuBar;
	JMenu menuFile;
	JMenu menuAddTool;
	JMenu menuRemoveTool;
	JMenu menuAbout;
	JMenuItem menuItemLoadData;
	JMenuItem menuItemExitProgram;
	JMenuItem menuItemRemoveAllTools;
	JMenuItem menuItemAboutGeoviz;
	JMenuItem menuItemOpenProject;
	JMenuItem menuItemSaveProject;
	JMenuItem menuItemCopyApplication;
	JMenuItem menuItemCopySelectedWindow;
	JMenuItem menuItemSaveWholeImage;
	JMenuItem menuItemSaveSelectedWindow;

	VizState vizState;

	public GeoVizToolkit() {
		super();
		init(null);
	}

	public GeoVizToolkit(String fileName) {
		super("Visual Analyzer");
		init(fileName);
	}

	public void init(String fileName) {
		initUI();
		setExtendedState(Frame.MAXIMIZED_BOTH);
		pack();
		setVisible(true);

		vizState = new VizState();
		dataCaster = new DataSetBroadcaster();
		coord = new CoordinationManager();
		coord.addBean(this);
		coord.addBean(dataCaster);
		coord.addBean(vizState);

		if (fileName != null)
			this.fileName = fileName;

		setTitle("Visual Analyzer - " + this.fileName);

		DataSetLoader loader = new DataSetLoader(this.fileName);
		dataSet = loader.getDataForApps();

		// VizState state = ToolkitIO.openDefaultLayout();
		// VizState state = ToolkitIO.getVizStateFromFile("vis_state.xml");
		// VizState state = ToolkitIO.getVizStateFromFile("treemap.xml");
		// VizState state = ToolkitIO.getVizStateFromFile("pcp.xml");
//		VizState state = ToolkitIO.getVizStateFromFile("map.xml");
		VizState state = ToolkitIO.getVizStateFromFile("time.xml");
		// VizState state = ToolkitIO.getVizStateFromFile("final.xml");

		setVizState(state);

		dataCaster.setAndFireDataSet(dataSet);
	}

	private void initUI() {
		classNameHashMap = new HashMap<JComponent, String>();

		this.setPreferredSize(new Dimension(1000, 800));

		// Create JDesktopPane to hold the internal frames
		desktop = new JDesktopPane();
		desktop.setBackground(Color.white);
		desktop.setBackground(new Color(20, 20, 80));

		getContentPane().add(desktop, BorderLayout.CENTER);

		// JXLayer<JComponent> layer = new JXLayer<JComponent>(desktop);
		// indUI = new IndicationConnectUI<JComponent>();
		// getContentPane().add(layer, BorderLayout.CENTER);
		// layer.setUI(indUI);
		// setIndicationUI(true);

		toolBar = new JToolBar();
		getContentPane().add(toolBar, BorderLayout.NORTH);

		menuBar = new JMenuBar();
		menuFile = new JMenu();
		menuAddTool = new JMenu();
		menuRemoveTool = new JMenu();
		menuAbout = new JMenu();

		menuItemExitProgram = new JMenuItem();
		menuItemLoadData = new JMenuItem();
		menuItemRemoveAllTools = new JMenuItem();
		menuItemAboutGeoviz = new JMenuItem();
		menuItemOpenProject = new JMenuItem();
		menuItemSaveProject = new JMenuItem();
		menuItemCopyApplication = new JMenuItem();
		menuItemCopySelectedWindow = new JMenuItem();
		menuItemSaveWholeImage = new JMenuItem();
		menuItemSaveSelectedWindow = new JMenuItem();

		setJMenuBar(menuBar);

		menuFile.setText("File");
		menuItemLoadData.setText("Open Data");
		menuItemCopyApplication
				.setText("Copy image of main window to clipboard");
		menuItemCopySelectedWindow
				.setText("Copy image of currently selected window to clipboard");
		menuItemSaveWholeImage.setText("Export image of main window to file");
		menuItemSaveSelectedWindow
				.setText("Export image of currently selected window to file");
		menuItemOpenProject.setText("Open Project");
		menuItemSaveProject.setText("Save Project");
		menuItemExitProgram.setText("Exit");

		menuAddTool.setText("Add Tool");
		menuItemRemoveAllTools.setText("Remove All Tools");

		menuRemoveTool.setText("Remove Tool");
		menuAbout.setText("About");
		menuItemAboutGeoviz.setText("About...");

		menuBar.add(menuFile);
		menuBar.add(menuAddTool);
		menuBar.add(menuRemoveTool);
		menuBar.add(menuAbout);
		menuFile.add(menuItemLoadData);
		menuFile.addSeparator();
		menuFile.add(menuItemCopyApplication);
		menuFile.add(menuItemCopySelectedWindow);
		menuFile.add(menuItemSaveWholeImage);
		menuFile.add(menuItemSaveSelectedWindow);
		menuFile.addSeparator();
		menuFile.add(menuItemOpenProject);
		menuFile.add(menuItemSaveProject);
		menuFile.addSeparator();
		menuFile.add(menuItemExitProgram);
		menuRemoveTool.addSeparator();
		menuRemoveTool.add(menuItemRemoveAllTools);
		menuAbout.add(menuItemAboutGeoviz);

		menuItemLoadData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

			}
		});
		menuItemOpenProject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

			}
		});
		menuItemSaveProject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Marshaler marsh = Marshaler.INSTANCE;
				String xml = marsh.toXML(getVizState());
				ToolkitIO.writeLayout("save.xml", xml);
			}
		});
		menuItemRemoveAllTools.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				removeAllBeans();
			}
		});

		menuItemSaveWholeImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				ToolkitIO.saveImage(that, "2.png");
			}
		});
		menuItemSaveSelectedWindow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JInternalFrame frame = desktop.getSelectedFrame();
				if (frame != null)
					ToolkitIO.saveImage(frame, "2.png");
			}
		});
		menuItemCopyApplication.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
			}
		});
		menuItemCopySelectedWindow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
			}
		});

		menuItemAboutGeoviz.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
			}
		});
		menuItemExitProgram.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.exit(0);
			}
		});

		addToolToGui(DataWindow.class);
		addToolToGui(GeoMap.class);
		addToolToGui(ParallelPlot.class);
		addToolToGui(Treemap.class);
		addToolToGui(TimeSeries.class);
	}

	/**
	 * flow of tool added to menu: add JMenuItem with appropriate name and icon
	 * to menu add app as a listener to JMenuItem add entry in ListArray of
	 * added menu tools
	 */
	private void addToolToGui(Class<?> tool) {

		String className = tool.getName();
		int place = className.lastIndexOf(".") + 1;
		String shortName = className.substring(place, className.length());

		Icon smallIcon = IconUtils.findSmallIcon(tool);
		Icon bigIcon = IconUtils.findIcon(tool);

		AddToolActionListener actionListener = new AddToolActionListener();
		JMenuItem menuItem = smallIcon != null ? new JMenuItem(shortName,
				smallIcon) : new JMenuItem(shortName);
		menuAddTool.add(menuItem);
		menuItem.addActionListener(actionListener);

		JButton button = new JButton(bigIcon);
		button.setToolTipText(shortName);
		toolBar.add(button);
		button.addActionListener(actionListener);

		AddToolMouseListener mouseListener = new AddToolMouseListener();
		button.addMouseListener(mouseListener);
		button.addMouseMotionListener(mouseListener);

		classNameHashMap.put(menuItem, className);
		classNameHashMap.put(button, className);
	}

	private final class AddToolActionListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			String className = classNameHashMap.get(evt.getSource());
			if (className != null) {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				addNewBean(className);

				setCursor(Cursor.getDefaultCursor());
			}
		}
	}

	private final class AddToolMouseListener extends MouseAdapter {

		boolean isDragged = false;

		public void mouseDragged(MouseEvent evt) {
			isDragged = true;
		}

		public void mouseReleased(MouseEvent evt) {
			if (!isDragged)
				return;
			isDragged = false;
			String className = classNameHashMap.get(evt.getSource());
			if (className != null) {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				ToolkitBean newToolkitBean = addNewBean(className);
				JInternalFrame internalFrame = newToolkitBean
						.getInternalFrame();

				internalFrame.setLocation(evt.getX() - 50, evt.getY() - 50);

				setCursor(Cursor.getDefaultCursor());
			}
		}
	}

	private ToolkitBean addNewBean(String className) {
		Object newBean = GeoVizToolkit.makeObject(className);

		FiringBean newFBean = coord.addBean(newBean);
		String uniqueName = newFBean.getBeanName();

		ToolkitBean newToolkitBean = new ToolkitBean(newBean, uniqueName);
		addBeanToGui(newToolkitBean);
		tBeanSet.put(uniqueName, newToolkitBean);

		fireNewBeanMethods(newBean);

		return newToolkitBean;
	}

	public void setIndicationUI(boolean indicationUI) {
		indUI.paintIndication = indicationUI;
	}

	public void setVizState(VizState newState) {
		removeAllBeans();
		coord.removeBean(vizState);

		vizState = newState;
		coord.addBean(vizState);

		addToolkitBeanSet(vizState.getBeanSet());
		for (ToolkitBean tBean : tBeanSet.values()) {
			desktop.setComponentZOrder(tBean.getInternalFrame(), tBean.zOrder);
		}

		String uniqueName = vizState.getSelectedBean();
		ToolkitBean tBean = tBeanSet.get(uniqueName);
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
		ToolkitBean tBean = tBeanSet.get(desktop.getSelectedFrame());
		if (tBean == null) {
			vizState.setSelectedBean("");
		} else {
			vizState.setSelectedBean(tBean.getUniqueName());
		}
	}

	static Object makeObject(String className) {
		Object obj = null;
		try {
			Class<?> beanClass = Class.forName(className);
			obj = beanClass.newInstance();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return obj;
	}

	/**
	 * 
	 * @param beanSet
	 */
	private void addToolkitBeanSet(HashMap<String, ToolkitBean> beanSet) {
		logger.fine(" addtoolkitbeanset setting tbeanset, size = "
				+ beanSet.size());
		tBeanSet = beanSet;

		for (ToolkitBean tBean : tBeanSet.values()) {
			Object newBean = tBean.getOriginalBean();
			coord.addBean(newBean);
			addBeanToGui(tBean);
			fireNewBeanMethods(newBean);
		}
	}

	/**
	 * Adds bean to the main gui area, also to the remove bean menu
	 */
	private void addBeanToGui(ToolkitBean newToolkitBean) {
		JInternalFrame internalFrame = newToolkitBean.getInternalFrame();

		desktop.add(internalFrame, 0); // add on top

		internalFrame.addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
				JInternalFrame iFrame = e.getInternalFrame();
				ToolkitBean tBean = getToolkitBean(iFrame);
				removeBeanFromGui(tBean);
				tBeanSet.remove(tBean.getUniqueName());
			}
		});

		JMenuItem item = newToolkitBean.getRemoveMenuItem();
		menuRemoveTool.add(item, 0); // add at the top

		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JMenuItem item = (JMenuItem) evt.getSource();
				ToolkitBean oldTool = getToolkitBean(item);
				removeBeanFromGui(oldTool);
				tBeanSet.remove(oldTool.getUniqueName());
			}
		});
	}

	private void removeBeanFromGui(ToolkitBean oldBean) {
		JMenuItem item = oldBean.getRemoveMenuItem();
		menuRemoveTool.remove(item);
		coord.removeBean(oldBean.getOriginalBean());

		JInternalFrame iFrame = oldBean.getInternalFrame();
		desktop.remove(iFrame);
		desktop.repaint();
	}

	/**
	 * deleting all beans
	 */
	private void removeAllBeans() {
		if (tBeanSet == null)
			return;

		for (ToolkitBean bean : tBeanSet.values()) {
			removeBeanFromGui(bean);
		}
		tBeanSet.clear();
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

	public static void main(String[] args) {
		Logger logger = Logger.getLogger("geovista");
		logger.setLevel(Level.INFO);

		logger.info(System.getProperty("java.version"));
		try {
			FileHandler fHandler = new FileHandler("gv_toolkit.log");
			fHandler.setFormatter(new SimpleFormatter());
			logger.addHandler(fHandler);
		} catch (IOException e) {
			System.err.println("could not create log file");
			e.printStackTrace();
		}

		System.setProperty("swing.aatext", "true");

		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			logger.info("couldn't find Nimbus look and feel, continue");
			e.printStackTrace();
		}

		logger.finest("java.version = " + System.getProperty("java.version"));

		String fileName = "hz_data/hz_house.xml";

		// String fileName = "data/PennaPCA";

		if (args.length == 0) {
		} else {
			fileName = args[0];
		}

		GeoVizToolkit app = new GeoVizToolkit(fileName);

		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.setPreferredSize(new Dimension(800, 600));
		app.setMinimumSize(new Dimension(480, 320));
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

	public ToolkitBean getToolkitBean(JMenuItem item) {
		for (ToolkitBean tBean : tBeanSet.values()) {
			JMenuItem beanItem = tBean.getRemoveMenuItem();
			if (item.equals(beanItem))
				return tBean;
		}
		return null;
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
