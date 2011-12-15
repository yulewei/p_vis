package edu.zjut.vis.marshal;

import java.io.Reader;
import java.util.logging.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;

import edu.zjut.common.ctrl.DataWindow;
import edu.zjut.vis.core.ToolkitBean;
import edu.zjut.vis.core.VizState;
import edu.zjut.vis.map.GeoMap;
import edu.zjut.vis.pcp.ParallelPlot;
import edu.zjut.vis.time.TimeSeries;
import edu.zjut.vis.treemap.Treemap;
import geovista.animation.ConditioningAnimator;
import geovista.animation.IndicationAnimator;
import geovista.animation.SelectionAnimator;

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

	public String toXML(Object obj) {
		String xml = streamer.toXML(obj);
		return xml;
	}

	public Object fromXML(String xml) {
		Object obj = streamer.fromXML(xml);
		return obj;
	}
	
	public Object fromXML(Reader reader) {
		Object obj = streamer.fromXML(reader);
		return obj;
	}
}
