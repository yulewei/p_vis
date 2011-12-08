package edu.zjut.map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.DefaultTileFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileCache;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.Painter;

import edu.zjut.map.config.MapConfig;
import edu.zjut.map.overlay.EsriLayer;
import edu.zjut.map.overlay.MapPolygon;
import edu.zjut.map.overlay.Overlay;
import edu.zjut.map.tile.CustomTileCache;
import edu.zjut.map.tile.TileFactoryInfoX;

public class JMapPanel extends JXMapViewerX implements MouseListener,
		MouseMotionListener {

	private int mouseX;
	private int mouseY;
	protected GeoPosition curGeoPosition;
	protected Overlay curPolygon;
	protected int curOverlayIndex = -1;

	protected List<Overlay> markerList;
	protected List<EsriLayer> layerList;

	protected int activeLayer = 0;

	protected boolean isCoordValid;

	public final int STATUS_BAR_HEIGHT = 25;
	protected boolean isDrawTileBorders = true;
	protected boolean isShowStatusBar = true;
	protected boolean isShowCenterCross = false;
	protected boolean isShowScaleRule = false;

	protected Painter<JXMapViewer> highlightOverlay;
	protected Painter<JXMapViewer> statusBarOverlay;
	protected Painter<JXMapViewer> centerCrossOverlay;
	protected Painter<JXMapViewer> scaleRuleOverlay;
	private boolean needUpdate;

	public JMapPanel() {
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		markerList = new ArrayList<Overlay>();
		layerList = new ArrayList<EsriLayer>();

		initPainters();
	}

	public void loadMapConfig(String filename) {
		MapConfig config = MapConfig.loadConfig(filename);

		TileFactoryInfoX info = new TileFactoryInfoX(config.serverName,
				config.templateUrl, config.minzoom, config.maxzoom);
		DefaultTileFactory tf = new DefaultTileFactory(info);
		File cacheDir = new File(config.cacheDir);
		TileCache tileCache = CustomTileCache.getInstance(cacheDir, info);
		tf.setTileCache(tileCache);
		this.setTileFactory(tf);
		this.setZoom(config.zoom);
		this.setAddressLocation(new GeoPosition(config.lat, config.lon));
		this.setDrawTileBorders(config.isDrawTileBorders);

		this.setSize(config.width, config.height);
		this.setPreferredSize(new Dimension(config.width, config.height));

		this.isShowStatusBar = config.isShowStatusBar;
		this.isShowCenterCross = config.isShowCenterCross;
		this.isShowScaleRule = config.isShowScaleRule;
	}

	private void initPainters() {

		highlightOverlay = new Painter<JXMapViewer>() {
			public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
				if (curOverlayIndex != -1) {
					Overlay marker = markerList.get(curOverlayIndex);
					String text = marker.getTitle();
					drawTooltip(g, text, mouseX, mouseY, new Color(1.0f, 1.0f,
							0.8f));
				}
			}
		};

		// 状态栏
		statusBarOverlay = new Painter<JXMapViewer>() {
			public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
				g.setPaint(new Color(0, 0, 0, 180));
				g.fillRect(0, getSize().height - STATUS_BAR_HEIGHT,
						getSize().width, STATUS_BAR_HEIGHT);
				g.setPaint(Color.WHITE);

				String latitude = "-";
				String longitude = "-";
				if (curGeoPosition != null && isCoordValid) {
					latitude = String
							.format("%f", curGeoPosition.getLatitude());
					longitude = String.format("%f",
							curGeoPosition.getLongitude());
				}

				g.drawString(String.format("%s: %s  %s: %s", "latitude",
						latitude, "longitude", longitude), 10,
						getSize().height - 8);
			}
		};

		// 中心点
		centerCrossOverlay = new Painter<JXMapViewer>() {
			public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
				int cx = getWidth() / 2;
				int cy = getHeight() / 2;
				int lw = 10;

				g.setStroke(new BasicStroke(2.5f));
				g.setColor(new Color(0, 128, 255));
				g.drawLine(cx, cy - lw, cx, cy + lw);
				g.drawLine(cx - lw, cy, cx + lw, cy);
			}
		};

		// 距离标尺
		scaleRuleOverlay = new Painter<JXMapViewer>() {
			public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
				// TODO 未实现
			}
		};

		updateOverlayPainterList();
	}

	public void addOverlay(Overlay overlay) {
		markerList.add(overlay);
		needUpdate = true;
	}

	public void addLayer(EsriLayer layer) {
		layerList.add(layer);
		needUpdate = true;
	}

	public List<Overlay> getMarkerList() {
		return markerList;
	}

	public void setMarkerList(List<Overlay> markerList) {
		this.markerList = markerList;
		needUpdate = true;
	}

	public List<EsriLayer> getLayerList() {
		return layerList;
	}

	public void setLayerList(List<EsriLayer> layerList) {
		this.layerList = layerList;
		needUpdate = true;
	}

	public void clearOverlays() {
		markerList.clear();
	}

	@Override
	public void paint(Graphics g) {
		if (needUpdate) {
			updateOverlayPainterList();
			// if (layerList != null && layerList.size() >= 1)
			// fitMapRectangle(layerList.get(0).getBoundingBox());
			needUpdate = false;
		}

		super.paint(g);
	}

	private void updateOverlayPainterList() {
		ArrayList<Painter<JXMapViewer>> list = new ArrayList<Painter<JXMapViewer>>();

		for (EsriLayer layer : layerList) {
			list.add(layer.getOverlayPainter());
		}

		for (EsriLayer layer : layerList) {
			list.add(layer.getHighlightOverlayPainter());
		}

		for (Overlay overlay : markerList) {
			list.add(overlay.getOverlayPainter());
		}

		for (Overlay overlay : markerList) {
			list.add(overlay.getHighlightOverlayPainter());
		}

		list.add(highlightOverlay);

		if (isShowStatusBar)
			list.add(statusBarOverlay);
		if (isShowCenterCross)
			list.add(centerCrossOverlay);
		if (isShowScaleRule)
			list.add(scaleRuleOverlay);

		CompoundPainter<Painter<JXMapViewer>> comp = new CompoundPainter<Painter<JXMapViewer>>();
		comp.setPainters(list.toArray(new Painter[list.size()]));
		comp.setCacheable(false);

		this.setOverlayPainter(comp);
	}

	/**
	 * 调整最佳zoom level, 以显示全部markers
	 */
	public void fitMapMarkers() {
		// TODO ???
	}

	public void fitMapRectangle(GeoPosition[] boundingBox) {
		GeoPosition minPos = boundingBox[0];
		GeoPosition maxPos = boundingBox[1];

		int zoom = getZoom();
		Point2D minpt = getTileFactory().geoToPixel(minPos, zoom);
		Point2D maxpt = getTileFactory().geoToPixel(maxPos, zoom);

		double minx = Math.min(minpt.getX(), maxpt.getX());
		double maxx = Math.max(minpt.getX(), maxpt.getX());
		double miny = Math.min(minpt.getY(), maxpt.getY());
		double maxy = Math.max(minpt.getY(), maxpt.getY());
		double w = maxx - minx;
		double h = maxy - miny;
		setCenter(new Point2D.Double(minpt.getX() + w / 2, maxpt.getY() + h / 2));

		int x = (int) (w);
		int y = (int) (h);
		int height = Math.max(0, getHeight());
		int width = Math.max(0, getWidth());

		while (true) {
			int tx = x << 1, ty = y << 1;
			if (tx > width || ty > height)
				break;
			else {
				x = tx;
				y = ty;
				zoom++;
			}
		}

		while (true) {
			if (x < width && y < height)
				break;
			else {
				x >>= 1;
				y >>= 1;
				zoom--;
			}
		}

		setZoom(zoom);
	}

	/**
	 * 代码实现参考自, parvis-0.3.1的BasicParallelDisplayUI类同名方法
	 */
	private void drawTooltip(Graphics2D g2, String text, int x, int y, Color col) {
		int i;
		int mheight, mwidth = 0;
		int numLines, lineHeight;

		StringTokenizer tok = new StringTokenizer(text, "\n");
		numLines = tok.countTokens();
		String lines[] = new String[numLines];

		for (i = 0; i < numLines; i++) {
			lines[i] = tok.nextToken();

			int tempwidth = g2.getFontMetrics().stringWidth(lines[i]) + 6;
			if (tempwidth > mwidth)
				mwidth = tempwidth;
		}

		lineHeight = g2.getFontMetrics().getHeight();
		mheight = numLines * lineHeight + 2;

		x += 15;
		y += 15;
		// if (x + mwidth > width) x -= (mwidth + 20);

		g2.setStroke(new BasicStroke(0.5f));
		g2.setColor(new Color(0.2f, 0.2f, 0.2f));
		g2.drawRect(x, y, mwidth, mheight);
		g2.setColor(col);
		g2.fillRect(x + 1, y + 1, mwidth - 1, mheight - 1);

		g2.setColor(Color.black);

		for (i = 0; i < numLines; i++) {
			g2.drawString(lines[i], x + 3, y + (i + 1) * lineHeight - 4);
		}
	}

	public boolean isShowCenterCross() {
		return isShowCenterCross;
	}

	public void setShowCenterCross(boolean isShowCenterCross) {
		this.isShowCenterCross = isShowCenterCross;

		updateOverlayPainterList();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		curGeoPosition = convertPointToGeoPosition(e.getPoint());
		curOverlayIndex = -1;
		curPolygon = null;
		mouseX = e.getX();
		mouseY = e.getY();

		for (int i = 0; i < markerList.size(); i++) {
			Overlay overlay = markerList.get(i);
			if (overlay.contains(this, mouseX, mouseY)) {
				curOverlayIndex = i;
			}
		}

		for (int i = 0; i < markerList.size(); i++) {
			Overlay overlay = markerList.get(i);
			if (i != curOverlayIndex)
				overlay.setHighlighted(false);
			else
				overlay.setHighlighted(true);
		}

		for (int i = 0; i < layerList.size(); i++) {
			EsriLayer layer = layerList.get(i);
			layer.setHighlighted(false);
			Overlay overlay = layer.containOverlay(this, mouseX, mouseY);
			if (overlay != null)
				overlay.setHighlighted(true);
		}

		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		for (int i = 0; i < markerList.size(); i++) {
			Overlay overlay = markerList.get(i);
			if (overlay.contains(this, e.getX(), e.getY())) {
				// this.overlayClicked(overlay, e);
			}
		}

		if (e.getClickCount() == 2 && !layerList.isEmpty()) {
			EsriLayer layer = layerList.get(activeLayer);
			Overlay overlay = layer.containOverlay(this, mouseX, mouseY);
			if (overlay != null && overlay instanceof MapPolygon) {
				MapPolygon ploygon = (MapPolygon) overlay;
				fitMapRectangle(ploygon.getBoundingBox());
			}
		}
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3 && !layerList.isEmpty()) {
			fitMapRectangle(layerList.get(0).getBoundingBox());
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		isCoordValid = true;
		repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		isCoordValid = false;
		repaint();
	}
}
