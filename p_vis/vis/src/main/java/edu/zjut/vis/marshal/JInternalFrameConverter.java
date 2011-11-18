package edu.zjut.vis.marshal;

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JInternalFrame;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class JInternalFrameConverter implements Converter {

	public boolean canConvert(Class clazz) {
		return clazz.equals(JInternalFrame.class);
	}

	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		JInternalFrame frame = (JInternalFrame) value;

		writer.startNode("location");
		context.convertAnother(frame.getLocation());
		writer.endNode();
		writer.startNode("size");
		context.convertAnother(frame.getSize());
		writer.endNode();
	}

	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		JInternalFrame frame = new JInternalFrame();
		reader.moveDown();
		Point loc = (Point) context.convertAnother(frame, Point.class);
		frame.setLocation(loc);
		reader.moveUp();

		reader.moveDown();
		Dimension size = (Dimension) context.convertAnother(frame,
				Dimension.class);
		reader.moveUp();
		frame.setSize(size);

		return frame;
	}

}
