package edu.zjut.vis.core;

import java.awt.Component;
import java.awt.Dimension;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;

import edu.zjut.vis.util.IconUtils;

public class ToolkitBean {

	final static Logger logger = Logger.getLogger(ToolkitBean.class.getName());

	private Object originalBean;
	private String uniqueName;

	private JMenuItem removeMenuItem;
	private JInternalFrame internalFrame;
	public int zOrder;

	public ToolkitBean() {

	}

	public ToolkitBean(Object originalBean, String uniqueName) {
		init(originalBean, uniqueName);
	}

	public void init(Object originalBean, String uniqueName) {
		logger.fine("in toolkitbean, in init");

		this.originalBean = originalBean;
		this.uniqueName = uniqueName;

		Icon ic = IconUtils.findSmallIcon(originalBean.getClass());
		this.internalFrame = makeInternalFrame(originalBean, uniqueName, ic);
		this.removeMenuItem = new JMenuItem(uniqueName, ic);
	}

	public Object getOriginalBean() {
		return originalBean;
	}

	public String getUniqueName() {
		return uniqueName;
	}

	public String getObjectClass() {
		return this.originalBean.getClass().getName();
	}

	public JInternalFrame getInternalFrame() {
		return internalFrame;
	}

	public JMenuItem getRemoveMenuItem() {
		return removeMenuItem;
	}

	private JInternalFrame makeInternalFrame(Object newInstance,
			String uniqueName, Icon ic) {

		JInternalFrame newFrame = new JInternalFrame(uniqueName, true, true,
				true, true);

		newFrame.setVisible(true);
		newFrame.setFrameIcon(ic);

		Component newComp = null;
		if (newInstance instanceof Component) {
			newComp = (Component) newInstance;
		} else {
			newComp = new JButton(uniqueName);
		}
		Dimension d = newComp.getPreferredSize();
		Dimension newD = new Dimension(d.width + 5, d.height + 10);
		newFrame.setSize(newD);
		newFrame.getContentPane().add(newComp);
		newFrame.revalidate();
		return newFrame;
	}
}
