package edu.zjut.vis.marshal;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JInternalFrame;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import edu.zjut.vis.core.ToolkitBean;


public class ToolkitBeanConverter implements Converter {
	protected final static Logger logger = Logger
			.getLogger(ToolkitBeanConverter.class.getName());

	public ToolkitBeanConverter() {

	}

	public boolean canConvert(Class clazz) {
		return clazz.equals(ToolkitBean.class);
	}

	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		ToolkitBean tBeans = (ToolkitBean) value;

		writer.startNode("objectClass");
		context.convertAnother(tBeans.getObjectClass());

		writer.endNode();
		writer.startNode("originalBean");
		context.convertAnother(tBeans.getOriginalBean());

		writer.endNode();

		writer.startNode("uniqueName");
		context.convertAnother(tBeans.getUniqueName());
		writer.endNode();
		writer.startNode("internalFrame");
		context.convertAnother(tBeans.getInternalFrame());
		writer.endNode();
		writer.startNode("zOrder");
		context.convertAnother(tBeans.zOrder);
		writer.endNode();
	}

	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {

		ToolkitBean bean = new ToolkitBean();
		String uniqueName = null;
		Object originalBean = null;
		String className = null;
		JInternalFrame internalFrame = null;
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			if ("uniqueName".equals(reader.getNodeName())) {
				uniqueName = (String) context
						.convertAnother(bean, String.class);
			}

			else if ("objectClass".equals(reader.getNodeName())) {
				className = (String) context.convertAnother(bean, String.class);
				if (logger.isLoggable(Level.FINEST)) {
					logger.finest(className);
				}

				// originalBean = GeoVizToolkit.makeObject(className.trim());
			}

			else if ("originalBean".equals(reader.getNodeName())) {
				Class<?> clazz = null;
				try {
					clazz = Class.forName(className);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				originalBean = clazz.cast(context.convertAnother(bean, clazz));
				// Object originalBean = (String) context.convertAnother(bean,
				// String.class);
				// logger.info(className);
				// originalBean = GeoVizToolkit.makeObject(className.trim());
			} else if ("internalFrame".equals(reader.getNodeName())) {
				internalFrame = (JInternalFrame) context.convertAnother(bean,
						JInternalFrame.class);
			} else if ("zOrder".equals(reader.getNodeName())) {
				bean.zOrder = (Integer) context.convertAnother(bean,
						Integer.class);
			}

			reader.moveUp();
		}

		bean.init(originalBean, uniqueName);
		bean.getInternalFrame().setSize(internalFrame.getSize());
		bean.getInternalFrame().setLocation(internalFrame.getLocation());

		return bean;
	}
}
