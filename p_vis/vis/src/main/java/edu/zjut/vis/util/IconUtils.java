package edu.zjut.vis.util;

import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * 利用Java内省, 加载bean类对用的icon
 * 
 * @author yulewei
 * 
 */
public final class IconUtils {

	public static String iconbean16 = "bean16.png";
	public static String iconbean32 = "bean32.png";
	private static HashMap<Class<?>, Icon> iconHashmap = new HashMap<Class<?>, Icon>();
	private static HashMap<Class<?>, Icon> smallIconHashmap = new HashMap<Class<?>, Icon>();

	public static Icon findIcon(Class<?> clazz) {
		Icon icon = iconHashmap.get(clazz);

		if (icon != null)
			return icon;

		BeanInfo info = null;
		try {
			info = Introspector.getBeanInfo(clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Image img = info.getIcon(BeanInfo.ICON_COLOR_32x32);

		if (img == null) {
			try {
				URL url = IconUtils.class.getResource(iconbean32);
				img = ImageIO.read(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		icon = new ImageIcon(img);

		iconHashmap.put(clazz, icon);

		return icon;
	}

	public static Icon findSmallIcon(Class<?> clazz) {
		Icon icon = smallIconHashmap.get(clazz);
		if (icon != null)
			return icon;

		BeanInfo info = null;
		try {
			info = Introspector.getBeanInfo(clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Image img = info.getIcon(BeanInfo.ICON_COLOR_16x16);

		if (img != null) {
			icon = new ImageIcon(img);
		} else {
			Image bigImg = info.getIcon(BeanInfo.ICON_COLOR_32x32);
			if (bigImg != null) {
				img = bigImg.getScaledInstance(16, 16, Image.SCALE_DEFAULT);
				icon = new ImageIcon(img);
			} else {
				// URL url = IconUtils.class.getClass().getResource(iconbean16);
				// icon = new ImageIcon(url);
			}
		}

		smallIconHashmap.put(clazz, icon);

		return icon;
	}
}
