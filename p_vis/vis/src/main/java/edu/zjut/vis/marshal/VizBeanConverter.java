package edu.zjut.vis.marshal;

import edu.zjut.common.event.SelectionEvent;
import edu.zjut.common.event.SelectionListener;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public abstract class VizBeanConverter implements Converter {

	private static String SELECTION = "selection";

	public boolean canConvert(Class clazz) {
		return false;
	}

	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext context) {

		if (value instanceof SelectionListener) {
			SelectionListener selList = (SelectionListener) value;
			int[] selection = selList.getSelectionEvent().getSelection();
			if (selection != null) {
				writer.startNode(VizBeanConverter.SELECTION);
				context.convertAnother(selection);
				writer.endNode();
			}
		}

	}

	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		return new Object();

	}

	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context, Object instantiatedObject) {
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			if (VizBeanConverter.SELECTION.equals(reader.getNodeName())) {
				SelectionListener selList = (SelectionListener) instantiatedObject;
				int[] selection = (int[]) context.convertAnother(
						instantiatedObject, int[].class);
				SelectionEvent e = new SelectionEvent(this, selection);
				selList.selectionChanged(e);
			}
			reader.moveUp();
		}

		return instantiatedObject;
	}

}
