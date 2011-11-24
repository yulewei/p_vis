/**
 * 
 */
package edu.zjut.vis.marshal;

import edu.zjut.common.ctrl.DataWindow;
import edu.zjut.vis.core.GeoVizToolkit;
import edu.zjut.vis.core.ToolkitBean;
import edu.zjut.vis.core.VizState;
import edu.zjut.vis.map.GeoMap;
import edu.zjut.vis.pcp.ParallelPlot;
import edu.zjut.vis.time.TimeSeries;
import edu.zjut.vis.treemap.Treemap;
import geovista.animation.ConditioningAnimator;
import geovista.animation.IndicationAnimator;
import geovista.animation.SelectionAnimator;

import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;

/**
 * @author localadmin
 * 
 */
public class Marshaler {

	XStream streamer;
	public final static Marshaler INSTANCE = new Marshaler();
	static final Logger logger = Logger.getLogger(Marshaler.class.getName());

	private Marshaler() {

		streamer = new XStream(new PureJavaReflectionProvider());

		streamer.alias("VizState", VizState.class);
		
		streamer.alias("DataWindow", DataWindow.class);
		streamer.registerConverter(new VariablePickerConverter());

		streamer.alias("IndicationAnimator", IndicationAnimator.class);
		streamer.registerConverter(new IndicationAnimatorConverter());

		streamer.alias("SelectionAnimator", SelectionAnimator.class);
		streamer.registerConverter(new SelectionAnimatorConverter());

		streamer.alias("ConditioningAnimator", ConditioningAnimator.class);
		streamer.registerConverter(new ConditioningAnimatorConverter());

		streamer.alias("GeoMap", GeoMap.class);
		streamer.registerConverter(new GeoMapConverter());

		streamer.alias("ParallelPlot", ParallelPlot.class);
		streamer.registerConverter(new ParallelPlotConverter());

		streamer.alias("TreemapViewer", Treemap.class);
		streamer.registerConverter(new TreemapConverter());

		streamer.alias("TimeSeries", TimeSeries.class);
		streamer.registerConverter(new TimeSeriesConverter());

		
		streamer.alias("ToolkitBean", ToolkitBean.class);
		streamer.registerConverter(new ToolkitBeanConverter());

		streamer.registerConverter(new JInternalFrameConverter());
	}

	// as yet unimplemented. If the constructor with every class listed
	// approach gets cumbersome, then it would not be too hard.
	public void registerConverter(Class clazz) {
		logger.finest(clazz.getName());
	}

	static String JAVA_NEWLINE = "\n";
	static String XML_NEWLINE = "&#010";

	static String newLinesToXML(String input) {
		return input.replaceAll(JAVA_NEWLINE, XML_NEWLINE);
	}

	static String newLinesToJava(String input) {

		return input.replaceAll(XML_NEWLINE, JAVA_NEWLINE);
	}

	public static void main(String[] args) {
		String testString = "123 \n 456";

		String newString = newLinesToXML(testString);
		String oldString = newLinesToJava(newString);
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JTextArea area = new JTextArea();
		area.setText(oldString);
		frame.add(area);
		frame.pack();
		frame.setVisible(true);

	}

	public String vizToXML(GeoVizToolkit gvt) {
		logger.info(gvt.toString());
		return null;
	}

	public String toXML(Object obj) {
		String xml = streamer.toXML(obj);
		return xml;
	}

	public Object fromXML(String xml) {
		Object gvz2 = streamer.fromXML(xml);
		return gvz2;
	}

}
