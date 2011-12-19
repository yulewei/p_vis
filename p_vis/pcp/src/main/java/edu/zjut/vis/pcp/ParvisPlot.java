package edu.zjut.vis.pcp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import org.gicentre.utils.colour.ColourTable;
import org.mediavirus.parvis.gui.BrushList;
import org.mediavirus.parvis.gui.BrushListener;
import org.mediavirus.parvis.gui.ParallelDisplay;
import org.mediavirus.parvis.gui.PrefsDialog;
import org.mediavirus.parvis.gui.ProgressEvent;
import org.mediavirus.parvis.gui.ProgressListener;
import org.mediavirus.parvis.model.Brush;
import org.mediavirus.parvis.model.ParallelSpaceModel;

import edu.zjut.common.color.ColorSchemeListener;
import edu.zjut.common.color.ColorSchemePicker;
import edu.zjut.common.color.Legend;
import edu.zjut.common.color.Legend.Orientation;
import edu.zjut.common.color.LegendActionListener;
import edu.zjut.common.ctrl.FieldComponent;
import edu.zjut.common.ctrl.FieldImporter;
import edu.zjut.common.ctrl.FieldComponent.ColorEnum;
import edu.zjut.common.data.attr.MeasureField;

/**
 * This code was edited or generated using CloudGarden's Jigloo
 * SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation,
 * company or business for any purpose whatever) then you
 * should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details.
 * Use of Jigloo implies acceptance of these licensing terms.
 * A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
 * THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
 * LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
/**
 * parvis主界面. 修改自 {@link org.mediavirus.parvis.gui.MainFrame}
 * 
 * @author yulewei
 * @author flo
 */
public class ParvisPlot extends JPanel implements ProgressListener,
		BrushListener {

	protected final static Logger logger = Logger.getLogger(ParvisPlot.class
			.getName());

	protected boolean isShowBrushList = false;

	protected ParallelDisplay parallelDisplay;
	private JPanel plotPanel;
	private JPanel legendPanel;
	protected Legend legend;
	protected ColorSchemePicker schemePicker;
	protected FieldComponent<MeasureField> colorFieldComp;

	private JPanel statusPanel;
	private JPanel progressPanel;
	private JPanel quickPrefPanel;
	private JCheckBox hoverBox;
	private JLabel timeLabel;
	private JProgressBar progressBar;
	private JCheckBox fuzzyBrushBox;
	private JTextField radiusField;
	private JCheckBox tooltipBox;
	private JCheckBox histogramBox;
	private JLabel progressLabel;

	private JPanel toolbarPanel;
	private JToolBar toolBar;
	private JToggleButton orderButton;
	private JToggleButton scaleButton;
	private JToggleButton translateButton;
	private JToggleButton brushButton;
	private JToggleButton scaleZeroMaxButton;
	private JToggleButton scaleMinMaxButton;
	private JToggleButton scaleMinMaxAbsButton;
	private JButton prefsButton;

	private JLabel countLabel;
	private JButton resetBrushButton;
	private JButton resetAllButton;

	public ParvisPlot() {
		initComponents();

		parallelDisplay.addProgressListener(this);
		parallelDisplay.addBrushListener(this);

		BrushList brushList = new BrushList(parallelDisplay);
		brushList.setLocation(this.getX() + this.getWidth(), this.getY());
		brushList.setVisible(isShowBrushList);

		// CorrelationFrame correlationFrame = new CorrelationFrame(
		// parallelDisplay);
		// correlationFrame.setLocation(this.getX() + this.getWidth(),
		// this.getY()
		// + brushList.getHeight());
		// correlationFrame.show();

		this.setSize(800, 600);
	}

	private void initComponents() {
		this.setLayout(new BorderLayout());

		plotPanel = new JPanel();
		plotPanel.setLayout(new BorderLayout());
		this.add(plotPanel, BorderLayout.CENTER);

		legendPanel = new JPanel();
		legendPanel.setLayout(new BorderLayout());
		legendPanel.setBackground(Color.white);
		legendPanel.setPreferredSize(new Dimension(60, 500));
		legendPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 25, 0));
		plotPanel.add(legendPanel, BorderLayout.WEST);

		colorFieldComp = new FieldComponent<MeasureField>();
		colorFieldComp.setTransferHandler(new ColorImporter());
		legendPanel.add(colorFieldComp, BorderLayout.NORTH);

		schemePicker = new ColorSchemePicker();
		schemePicker.addPickerListener(new ColorSchemeListener() {
			public void colorChosen(ColourTable cTable) {
				legend.setColorTable(cTable);
				setColor(colorValues, cTable);
				repaint();
			}
		});

		legend = new Legend();		
		legend.setMinMax(false);
		legend.setOrient(Orientation.VERTICAL);
		legend.addLegendActionListener(new LegendActionListener() {
			@Override
			public void actionPerformed(boolean isActive) {
				schemePicker.setRefColorTable(legend.getColorTable());
				schemePicker.setLocationRelativeTo(ParvisPlot.this);
				schemePicker.setVisible(isActive);
				// schemePicker.requestFocus();
			}
		});		
		legendPanel.add(legend, BorderLayout.CENTER);

		parallelDisplay = new ParallelDisplay();
		parallelDisplay.setPreferredSize(new Dimension(800, 500));
		plotPanel.add(parallelDisplay, BorderLayout.CENTER);

		statusPanel = new JPanel();
		statusPanel.setLayout(new BorderLayout());
		statusPanel.setPreferredSize(new java.awt.Dimension(800, 30));
		this.add(statusPanel, BorderLayout.SOUTH);

		progressPanel = new JPanel();
		progressPanel.setLayout(new FlowLayout());
		statusPanel.add(progressPanel, BorderLayout.WEST);
		progressPanel.setPreferredSize(new java.awt.Dimension(216, 37));

		progressLabel = new JLabel("progress:");
		progressPanel.add(progressLabel);

		progressBar = new JProgressBar();
		progressBar.setMaximumSize(new Dimension(32767, 18));
		progressBar.setMinimumSize(new Dimension(10, 16));
		progressBar.setPreferredSize(new Dimension(100, 18));
		progressBar.setStringPainted(true);
		progressPanel.add(progressBar);

		timeLabel = new JLabel("(0.0 s)");
		progressPanel.add(timeLabel);

		quickPrefPanel = new JPanel();
		quickPrefPanel.setLayout(new FlowLayout());
		statusPanel.add(quickPrefPanel, BorderLayout.EAST);

		histogramBox = new JCheckBox("hist.");
		histogramBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				histogramBoxActionPerformed(evt);
			}
		});

		quickPrefPanel.add(histogramBox);

		tooltipBox = new JCheckBox("tooltips");
		tooltipBox.setSelected(true);
		tooltipBox.setEnabled(false);
		tooltipBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				tooltipBoxActionPerformed(evt);
			}
		});

		quickPrefPanel.add(tooltipBox);

		hoverBox = new JCheckBox("line");
		hoverBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				hoverBoxActionPerformed(evt);
			}
		});

		quickPrefPanel.add(hoverBox);

		fuzzyBrushBox = new JCheckBox("Brush Fuzziness:");
		fuzzyBrushBox.setSelected(true);
		fuzzyBrushBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				fuzzyBrushBoxActionPerformed(evt);
			}
		});

		quickPrefPanel.add(fuzzyBrushBox);

		radiusField = new JTextField(" 20 %");
		radiusField.setBorder(new LineBorder((Color) UIManager.getDefaults()
				.get("Button.select")));
		radiusField.setPreferredSize(new Dimension(30, 17));
		radiusField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				radiusFieldActionPerformed(evt);
			}
		});
		radiusField.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent evt) {
				radiusFieldFocusGained(evt);
			}
		});

		quickPrefPanel.add(radiusField);

		toolbarPanel = new JPanel();
		toolbarPanel.setLayout(new BorderLayout());
		this.add(toolbarPanel, BorderLayout.NORTH);

		toolBar = new JToolBar();
		toolbarPanel.add(toolBar);

		orderButton = new JToggleButton();
		orderButton
				.setIcon(new ImageIcon(getClass().getResource("reorder.gif")));
		orderButton.setSelected(true);
		orderButton.setToolTipText("Reorder Axes");
		orderButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				setEditModeOrder(evt);
			}
		});

		scaleButton = new JToggleButton();
		scaleButton.setIcon(new ImageIcon(getClass().getResource("scale.gif")));
		scaleButton.setToolTipText("Scale Axes");
		scaleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				setEditModeScale(evt);
			}
		});

		translateButton = new JToggleButton();
		translateButton.setIcon(new ImageIcon(getClass()
				.getResource("move.gif")));
		translateButton.setToolTipText("Translate Axes");
		translateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				setEditModeTranslate(evt);
			}
		});

		brushButton = new JToggleButton();
		brushButton.setIcon(new ImageIcon(getClass().getResource("brush.gif")));
		brushButton.setToolTipText("Brush");
		brushButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				setEditModeBrush(evt);
			}
		});

		ButtonGroup buttonEditGroup = new ButtonGroup();
		buttonEditGroup.add(orderButton);
		buttonEditGroup.add(scaleButton);
		buttonEditGroup.add(translateButton);
		buttonEditGroup.add(brushButton);

		toolBar.add(orderButton);
		toolBar.add(scaleButton);
		toolBar.add(translateButton);
		toolBar.add(brushButton);

		toolBar.add(new JSeparator(SwingConstants.VERTICAL));

		// 界面简单修改
		scaleZeroMaxButton = new JToggleButton("0-max");
		scaleZeroMaxButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				scaleZeroMaxActionPerformed(evt);
			}
		});

		scaleMinMaxButton = new JToggleButton("min-max");
		scaleMinMaxButton.setSelected(true);
		scaleMinMaxButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				scaleMinMaxActionPerformed(evt);
			}
		});

		scaleMinMaxAbsButton = new JToggleButton("min-max (abs)");
		scaleMinMaxAbsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				scaleMinMaxAbsActionPerformed(evt);
			}
		});

		ButtonGroup buttonViewGroup = new ButtonGroup();
		buttonViewGroup.add(scaleZeroMaxButton);
		buttonViewGroup.add(scaleMinMaxButton);
		buttonViewGroup.add(scaleMinMaxAbsButton);

		toolBar.add(scaleZeroMaxButton);
		toolBar.add(scaleMinMaxButton);
		toolBar.add(scaleMinMaxAbsButton);

		toolBar.add(Box.createHorizontalGlue());

		countLabel = new JLabel("0 / 0   ");

		resetBrushButton = new JButton("Reset Brush");
		resetBrushButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				resetBrushActionPerformed(evt);
			}
		});

		resetAllButton = new JButton("Reset All");
		resetAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				resetAllActionPerformed(evt);
			}
		});

		toolBar.add(countLabel);
		toolBar.add(resetBrushButton);
		toolBar.add(resetAllButton);

		toolBar.add(new JSeparator(SwingConstants.VERTICAL));

		prefsButton = new JButton();
		prefsButton.setIcon(new ImageIcon(getClass().getResource("cog.png")));
		prefsButton.setToolTipText("Preferences");
		prefsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				prefsActionPerformed(evt);
			}
		});

		toolBar.add(prefsButton);
	}

	private void histogramBoxActionPerformed(ActionEvent evt) {
		parallelDisplay.setBoolPreference("histogram",
				histogramBox.isSelected());
		parallelDisplay.repaint();
	}

	private void fuzzyBrushBoxActionPerformed(ActionEvent evt) {
		if (fuzzyBrushBox.isSelected()) {
			radiusField.setEnabled(true);
			String txt = radiusField.getText();
			if (txt.indexOf('%') > -1) {
				txt = txt.substring(0, txt.indexOf('%'));
			}
			txt = txt.trim();

			int num = Integer.parseInt(txt);
			parallelDisplay.setFloatPreference("brushRadius",
					((float) num) / 100.0f);
		} else {
			radiusField.setEnabled(false);
			parallelDisplay.setFloatPreference("brushRadius", 0.0f);
		}
	}

	private void resetAllActionPerformed(ActionEvent evt) {
		parallelDisplay.resetAll();
	}

	private void resetBrushActionPerformed(ActionEvent evt) {
		parallelDisplay.setCurrentBrush(null);
	}

	private void hoverBoxActionPerformed(ActionEvent evt) {
		if (hoverBox.isSelected()) {
			tooltipBox.setEnabled(true);
			parallelDisplay.setBoolPreference("hoverText",
					tooltipBox.isSelected());
			parallelDisplay.setBoolPreference("hoverLine",
					hoverBox.isSelected());
		} else {
			tooltipBox.setEnabled(false);
			parallelDisplay.setBoolPreference("hoverText", false);
			parallelDisplay.setBoolPreference("hoverLine",
					hoverBox.isSelected());
		}
	}

	private void radiusFieldActionPerformed(ActionEvent evt) {
		String txt = radiusField.getText();
		if (txt.indexOf('%') > -1) {
			txt = txt.substring(0, txt.indexOf('%'));
		}
		txt = txt.trim();

		int num = Integer.parseInt(txt);
		parallelDisplay.setFloatPreference("brushRadius",
				((float) num) / 100.0f);
		radiusField.setText(" " + num + " %");
		radiusField.transferFocus();
	}

	private void radiusFieldFocusGained(FocusEvent evt) {
		radiusField.selectAll();
	}

	private void setEditModeTranslate(ActionEvent evt) {
		parallelDisplay.setEditMode(ParallelDisplay.TRANSLATE);
		translateButton.setSelected(true);
	}

	private void setEditModeScale(ActionEvent evt) {
		parallelDisplay.setEditMode(ParallelDisplay.SCALE);
		scaleButton.setSelected(true);
	}

	private void setEditModeOrder(ActionEvent evt) {
		parallelDisplay.setEditMode(ParallelDisplay.REORDER);
		orderButton.setSelected(true);
	}

	private void setEditModeBrush(ActionEvent evt) {
		parallelDisplay.setEditMode(ParallelDisplay.BRUSH);
		brushButton.setSelected(true);
	}

	private void tooltipBoxActionPerformed(ActionEvent evt) {
		parallelDisplay.setBoolPreference("hoverText", tooltipBox.isSelected());
	}

	private void scaleMinMaxAbsActionPerformed(ActionEvent evt) {
		parallelDisplay.minMaxAbsScale();
	}

	private void scaleMinMaxActionPerformed(ActionEvent evt) {
		parallelDisplay.minMaxScale();
	}

	private void scaleZeroMaxActionPerformed(ActionEvent evt) {
		parallelDisplay.zeroMaxScale();
	}

	private void prefsActionPerformed(ActionEvent evt) {
		PrefsDialog pf = new PrefsDialog(parallelDisplay);
		pf.setLocationRelativeTo(this);
		pf.setVisible(true);
	}

	private long progressstart = 0;

	public void processProgressEvent(ProgressEvent e) {
		switch (e.getType()) {
		case ProgressEvent.PROGRESS_START:
			progressstart = e.getTimestamp();
			progressBar.setValue(0);
			timeLabel.setText("0 s");

			if (parallelDisplay.getCurrentBrush() == null) {
				// workaround because we are not notified otherways if model
				// changes
				countLabel.setText("0 / " + parallelDisplay.getNumRecords()
						+ "   ");
			}
			break;

		case ProgressEvent.PROGRESS_UPDATE:
			progressBar.setValue((int) (e.getProgress() * 100));
			timeLabel.setText(((e.getTimestamp() - progressstart) / 1000)
					+ " s");
			break;

		case ProgressEvent.PROGRESS_FINISH:
			progressBar.setValue(100);
			timeLabel.setText(((e.getTimestamp() - progressstart) / 1000)
					+ " s");
			break;
		}
		progressLabel.setText(e.getMessage());
		// System.out.println(e.getMessage() + ": " + ((int)(e.getProgress() *
		// 100))+"%");

	}

	public void brushChanged(Brush b) {
		// TODO Auto-generated method stub
	}

	public void brushModified(Brush b) {
		if (b != null) {
			countLabel.setText(b.getNumBrushed() + " / " + b.getNumValues()
					+ "   ");
		} else {
			countLabel
					.setText("0 / " + parallelDisplay.getNumRecords() + "   ");
		}
	}

	public void setModel(ParallelSpaceModel model) {
		parallelDisplay.setModel(model);
	}

	protected MeasureField colorField;
	protected double[] colorValues;
	protected ColourTable cTable;

	public void setColor(double[] values, ColourTable cTable) {		
		
		this.colorValues = values;
		this.cTable = cTable;

		int len = values.length;

		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for (int i = 0; i < len; i++) {
			if (values[i] < min)
				min = values[i];
			if (values[i] > max)
				max = values[i];
		}

		Color[] colors = new Color[len];
		for (int i = 0; i < len; i++) {
			double index = (float) ((values[i] - min) / (max - min));
			colors[i] = new Color(cTable.findColour((float) index));
		}

		legend.setData(values, cTable);
		parallelDisplay.setColors(colors);
	}

	class ColorImporter extends FieldImporter<MeasureField> {
		public ColorImporter() {
			super(MeasureField.class);
		}

		public boolean importData(TransferHandler.TransferSupport support) {
			getTransferData(support);
			List<MeasureField> values = data.getValues();
			if (!values.isEmpty()) {
				colorFieldComp.setValue(values.get(0));
				colorFieldComp.setColor(ColorEnum.BLUE);
				colorField = values.get(0);
				
				double[] colorValues = colorField.getColumnAsDouble();
				colorFieldComp.setValue(colorField);
				colorFieldComp.setColor(ColorEnum.WHITE);
				setColor(colorValues, colorField.getColorTable());
				
				repaint();
			}

			return true;
		}
	}
}
