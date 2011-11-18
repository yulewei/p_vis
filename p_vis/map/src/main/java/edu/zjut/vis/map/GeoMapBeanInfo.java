package edu.zjut.vis.map;

import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.SimpleBeanInfo;

public class GeoMapBeanInfo extends SimpleBeanInfo {
	public static String iconColor16file = "map16.png";
	public static String iconColor32file = "map32.png";
	public static String iconMono16file;
	public static String iconMono32file;

	private Image iconColor16;
	private Image iconColor32;
	private Image iconMono16;
	private Image iconMono32;

	public GeoMapBeanInfo() {
		iconColor16 = loadImage(iconColor16file);
		iconColor32 = loadImage(iconColor32file);
		iconMono16 = loadImage(iconMono16file);
		iconMono32 = loadImage(iconMono32file);
	}

	public Image getIcon(int iconKind) {
		switch (iconKind) {
		case BeanInfo.ICON_COLOR_16x16:
			return iconColor16;
		case BeanInfo.ICON_COLOR_32x32:
			return iconColor32;
		case BeanInfo.ICON_MONO_16x16:
			return iconMono16;
		case BeanInfo.ICON_MONO_32x32:
			return iconMono32;
		}
		return null;
	}
}