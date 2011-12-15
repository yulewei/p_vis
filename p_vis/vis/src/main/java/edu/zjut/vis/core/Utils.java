package edu.zjut.vis.core;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import edu.zjut.vis.marshal.Marshaler;

public class Utils {
	protected final static Logger logger = Logger.getLogger(Utils.class
			.getName());

	public static void saveImage(Component c, File file) {
		BufferedImage buff = new BufferedImage(c.getWidth(), c.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = buff.getGraphics();
		c.paint(g);

		try {
			ImageIO.write(buff, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeLayout(File file, String xml) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(xml);
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static VizState getVizStateFromFile(String xmlName) {
		VizState state = null;
		try {
			InputStreamReader in = new InputStreamReader(new FileInputStream(
					xmlName), "utf-8");
			Marshaler marsh = Marshaler.INSTANCE;
			state = (VizState) marsh.fromXML(in);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return state;
	}
}
