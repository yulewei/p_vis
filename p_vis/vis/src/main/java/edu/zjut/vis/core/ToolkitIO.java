/* -------------------------------------------------------------------
 Java source file for the class ToolkitIO
 Copyright (c), 2005 Ke Liao, Frank Hardisty
 $Author: hardisty $
 $Id: ToolkitIO.java,v 1.6 2005/04/11 17:52:14 hardisty Exp $
 $Date: 2005/04/11 17:52:14 $
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 -------------------------------------------------------------------   */

package edu.zjut.vis.core;

import edu.zjut.vis.marshal.Marshaler;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

public class ToolkitIO {
	protected final static Logger logger = Logger.getLogger(ToolkitIO.class
			.getName());

	public static String dataSetPathFromXML = " ";

	public static void saveVizStateToFile(VizState state) {
		Marshaler marsh = Marshaler.INSTANCE;
		String xml = marsh.toXML(state);
		logger.info(xml);
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("state.gvz"));
			out.write(xml);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveImage(Component c, String fileName) {
		BufferedImage buff = new BufferedImage(c.getWidth(), c.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = buff.getGraphics();
		c.paint(g);

		File fi = new File(fileName);
		try {
			ImageIO.write(buff, "png", fi);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void writeLayout(String xmlFullName, String xml) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(xmlFullName));
			out.write(xml);
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getVizStateXML(String fileName) {
		InputStreamReader inReader = null;
		try {
			inReader = new FileReader(fileName);
		} catch (FileNotFoundException e1) {

			e1.printStackTrace();
		}
		return readCharStream(inReader);
	}

	private static String readCharStream(InputStreamReader inReader) {
		StringBuffer strBuff = new StringBuffer();
		try {
			BufferedReader in = new BufferedReader(inReader);
			String str;
			while ((str = in.readLine()) != null) {
				strBuff.append(str);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("built string " + strBuff.toString());
		}
		return strBuff.toString();
	}

	public static VizState openDefaultLayout() {
		String xmlName = "anthony";

		return getVizStateFromResource(xmlName);
	}

	public static VizState getVizStateFromFile(String xmlName) {
		InputStream inStream = null;
		try {
			inStream = new FileInputStream(xmlName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String xml = readCharStream(new InputStreamReader(inStream));
		if (logger.isLoggable(Level.FINEST)) {
			logger.info("getting mashaller");
		}
		Marshaler marsh = Marshaler.INSTANCE;
		if (logger.isLoggable(Level.FINEST)) {
			logger.info("about to instantiate VizState");
		}
		VizState state = (VizState) marsh.fromXML(xml);
		if (logger.isLoggable(Level.FINEST)) {
			logger.info("instantiated VizState");
		}
		return state;
	}

	public static VizState getVizStateFromResource(String xmlName) {
		InputStream inStream = null;
		try {
			Class cl = ToolkitIO.class;

			inStream = cl.getResourceAsStream("resources/" + xmlName + ".xml");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		String xml = readCharStream(new InputStreamReader(inStream));
		if (logger.isLoggable(Level.FINEST)) {
			logger.info("getting mashaller");
		}
		Marshaler marsh = Marshaler.INSTANCE;
		if (logger.isLoggable(Level.FINEST)) {
			logger.info("about to instantiate VizState");
		}
		VizState state = (VizState) marsh.fromXML(xml);
		if (logger.isLoggable(Level.FINEST)) {
			logger.info("instantiated VizState");
		}
		return state;
	}

	public static VizState openStarPlotMapLayout() {
		String xmlName = "starmap";
		return getVizStateFromResource(xmlName);
	}

	public static VizState openAllComponentsLayout() {
		String xmlName = "new_all";
		return getVizStateFromResource(xmlName);
	}

	public static VizState getVizState(String xml) {
		if (logger.isLoggable(Level.FINEST)) {
			logger.info("getting mashaller");
		}
		Marshaler marsh = Marshaler.INSTANCE;
		if (logger.isLoggable(Level.FINEST)) {
			logger.info("about to instantiate VizState");
		}
		VizState state = (VizState) marsh.fromXML(xml);
		if (logger.isLoggable(Level.FINEST)) {
			logger.info("instantiated VizState");
		}
		return state;
	}

	public static String makeVizStateXML(String xmlFullName) {
		logger.info("xmlFullName = " + xmlFullName);
		return ToolkitIO.getVizStateXML(xmlFullName);
	}

	public static void copyComponentImage(Component c) {
		BufferedImage buff = new BufferedImage(c.getWidth(), c.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = buff.getGraphics();
		c.paint(g);
		ToolkitIO.sendImageToClipboard(buff);
	}

	// I got this method and the next class from an online article in devx.
	// the code was available for download with no copyright
	// notice, so I judge the risk of problems to be small - Frank
	public static void sendImageToClipboard(Image image) {
		ImageSelection imageSelection = new ImageSelection(image);
		Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(imageSelection, null);
	}

	// Inner class is used to hold an image while on the clipboard.
	public static class ImageSelection implements Transferable {
		// the Image object which will be housed by the ImageSelection
		private final Image image;

		public ImageSelection(Image image) {
			this.image = image;
		}

		// Returns the supported flavors of our implementation
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { DataFlavor.imageFlavor };
		}

		// Returns true if flavor is supported
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return DataFlavor.imageFlavor.equals(flavor);
		}

		// Returns Image object housed by Transferable object
		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException, IOException {
			if (!DataFlavor.imageFlavor.equals(flavor)) {
				throw new UnsupportedFlavorException(flavor);
			}
			// else return the payload
			return image;
		}
	}
}
