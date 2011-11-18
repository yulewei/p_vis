package edu.zjut.vis.pcp;

import java.util.logging.Logger;

import edu.zjut.common.event.IndicationEvent;
import edu.zjut.common.event.IndicationListener;
import edu.zjut.common.event.SelectionListener;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.mediavirus.parvis.file.STFFile;
import org.mediavirus.parvis.gui.BrushList;
import org.mediavirus.parvis.gui.BrushListener;
import org.mediavirus.parvis.gui.ParallelDisplay;
import org.mediavirus.parvis.gui.PrefsFrame;
import org.mediavirus.parvis.gui.ProgressEvent;
import org.mediavirus.parvis.gui.ProgressListener;
import org.mediavirus.parvis.model.Brush;
import org.mediavirus.parvis.model.ParallelSpaceModel;

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

	private JButton resetBrushButton;
	private JSeparator jSeparator2;
	private JPanel quickPrefPanel;
	private JPanel statusPanel;
	private JToolBar modeBar;
	private JCheckBox hoverBox;
	private JLabel timeLabel;
	private JProgressBar progressBar;
	private JLabel countLabel;
	private JButton resetAllButton;
	private JToggleButton translateButton;
	private JToggleButton orderButton;
	private JToggleButton scaleButton;
	private JCheckBox fuzzyBrushBox;
	private JPanel progressPanel;
	private JLabel modeLabel;
	private JToggleButton brushButton;
	private JPanel toolbarPanel;
	private JTextField radiusField;
	private JCheckBox tooltipBox;
	private JCheckBox histogramBox;
	private JLabel progressLabel;
	private ButtonGroup buttonEditGroup;
	private ButtonGroup buttonViewGroup;
	private JToolBar viewBar;
	private JLabel viewLabel;
	private JToggleButton scaleZeroMaxButton;
	private JToggleButton scaleMinMaxButton;
	private JToggleButton scaleMinMaxAbsButton;
	private JSeparator jSeparator1;
	private JButton preferencesButton;

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
		parallelDisplay = new ParallelDisplay();
		buttonEditGroup = new ButtonGroup();
		buttonViewGroup = new ButtonGroup();
		statusPanel = new JPanel();
		progressPanel = new JPanel();
		progressLabel = new JLabel();
		progressBar = new JProgressBar();
		timeLabel = new JLabel();
		quickPrefPanel = new JPanel();
		histogramBox = new JCheckBox();
		tooltipBox = new JCheckBox();
		hoverBox = new JCheckBox();
		fuzzyBrushBox = new JCheckBox();
		radiusField = new JTextField();
		toolbarPanel = new JPanel();
		modeBar = new JToolBar();
		modeLabel = new JLabel();
		orderButton = new JToggleButton();
		scaleButton = new JToggleButton();
		translateButton = new JToggleButton();
		brushButton = new JToggleButton();
		jSeparator2 = new JSeparator();
		countLabel = new JLabel();
		resetBrushButton = new JButton();
		resetAllButton = new JButton();

		viewBar = new JToolBar();
		viewLabel = new JLabel();
		scaleZeroMaxButton = new JToggleButton();
		scaleMinMaxButton = new JToggleButton();
		scaleMinMaxAbsButton = new JToggleButton();
		jSeparator1 = new JSeparator();
		preferencesButton = new JButton();

		this.setLayout(new java.awt.BorderLayout());

		statusPanel.setLayout(new java.awt.BorderLayout());

		statusPanel.setBorder(new javax.swing.border.TitledBorder(
				new javax.swing.border.EtchedBorder(), "status",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("Dialog", 0, 10)));
		statusPanel.setFont(new java.awt.Font("Dialog", 0, 10));
		statusPanel.setPreferredSize(new java.awt.Dimension(272, 50));
		progressPanel.setLayout(new java.awt.FlowLayout(
				java.awt.FlowLayout.CENTER, 5, 0));

		progressLabel.setFont(new java.awt.Font("Dialog", 0, 10));
		progressLabel.setText("progress:");
		progressPanel.add(progressLabel);

		progressBar.setFont(new java.awt.Font("Dialog", 0, 10));
		progressBar.setMaximumSize(new java.awt.Dimension(32767, 18));
		progressBar.setMinimumSize(new java.awt.Dimension(10, 16));
		progressBar.setPreferredSize(new java.awt.Dimension(100, 18));
		progressBar.setStringPainted(true);
		progressPanel.add(progressBar);

		timeLabel.setFont(new java.awt.Font("Dialog", 0, 10));
		timeLabel.setText("(0.0 s)");
		progressPanel.add(timeLabel);

		statusPanel.add(progressPanel, java.awt.BorderLayout.WEST);

		quickPrefPanel.setLayout(new java.awt.FlowLayout(
				java.awt.FlowLayout.CENTER, 5, 0));

		histogramBox.setFont(new java.awt.Font("Dialog", 0, 10));
		histogramBox.setText("hist.");
		histogramBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				histogramBoxActionPerformed(evt);
			}
		});

		quickPrefPanel.add(histogramBox);

		tooltipBox.setFont(new java.awt.Font("Dialog", 0, 10));
		tooltipBox.setSelected(true);
		tooltipBox.setText("tooltips");
		tooltipBox.setMargin(new java.awt.Insets(0, 2, 0, 2));
		tooltipBox.setEnabled(false);
		tooltipBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				tooltipBoxActionPerformed(evt);
			}
		});

		quickPrefPanel.add(tooltipBox);

		hoverBox.setFont(new java.awt.Font("Dialog", 0, 10));
		hoverBox.setText("line");
		hoverBox.setMargin(new java.awt.Insets(0, 2, 0, 2));
		hoverBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				hoverBoxActionPerformed(evt);
			}
		});

		quickPrefPanel.add(hoverBox);

		fuzzyBrushBox.setFont(new java.awt.Font("Dialog", 0, 10));
		fuzzyBrushBox.setSelected(true);
		fuzzyBrushBox.setText("Brush Fuzziness:");
		fuzzyBrushBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		fuzzyBrushBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				fuzzyBrushBoxActionPerformed(evt);
			}
		});

		quickPrefPanel.add(fuzzyBrushBox);

		radiusField.setFont(new java.awt.Font("Dialog", 0, 10));
		radiusField.setText(" 20 %");
		radiusField.setBorder(new javax.swing.border.LineBorder(
				(java.awt.Color) javax.swing.UIManager.getDefaults().get(
						"Button.select")));
		radiusField.setPreferredSize(new java.awt.Dimension(30, 17));
		radiusField.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				radiusFieldActionPerformed(evt);
			}
		});

		radiusField.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusGained(java.awt.event.FocusEvent evt) {
				radiusFieldFocusGained(evt);
			}
		});

		quickPrefPanel.add(radiusField);

		statusPanel.add(quickPrefPanel, java.awt.BorderLayout.EAST);

		this.add(statusPanel, java.awt.BorderLayout.SOUTH);

		toolbarPanel.setLayout(new java.awt.GridLayout(2, 0));
		// toolbarPanel.setLayout(new java.awt.BorderLayout());

		modeLabel.setFont(new java.awt.Font("Dialog", 0, 10));
		modeLabel.setText("Edit Mode: ");
		modeBar.add(modeLabel);

		orderButton.setFont(new java.awt.Font("Dialog", 0, 10));
		orderButton.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/org/mediavirus/parvis/gui/reorder.gif")));
		orderButton.setSelected(true);
		orderButton.setText("Order");
		orderButton
				.setToolTipText("Reorder axes by dragging them across the display.");
		buttonEditGroup.add(orderButton);
		orderButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		orderButton.setMaximumSize(new java.awt.Dimension(65, 27));
		orderButton.setMinimumSize(new java.awt.Dimension(65, 27));
		orderButton.setPreferredSize(new java.awt.Dimension(65, 27));
		orderButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setEditModeOrder(evt);
			}
		});

		modeBar.add(orderButton);

		scaleButton.setFont(new java.awt.Font("Dialog", 0, 10));
		scaleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/org/mediavirus/parvis/gui/scale.gif")));
		scaleButton.setText("Scale");
		scaleButton
				.setToolTipText("Scale axes by dragging up (zoom out) or down (zoom in).");
		buttonEditGroup.add(scaleButton);
		scaleButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		scaleButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setEditModeScale(evt);
			}
		});

		modeBar.add(scaleButton);

		translateButton.setFont(new java.awt.Font("Dialog", 0, 10));
		translateButton.setIcon(new javax.swing.ImageIcon(getClass()
				.getResource("/org/mediavirus/parvis/gui/move.gif")));
		translateButton.setText("Translate");
		translateButton
				.setToolTipText("Translate axes by dragging up or down.");
		buttonEditGroup.add(translateButton);
		translateButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		translateButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setEditModeTranslate(evt);
			}
		});

		modeBar.add(translateButton);

		brushButton.setFont(new java.awt.Font("Dialog", 0, 10));
		brushButton.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/org/mediavirus/parvis/gui/brush.gif")));
		brushButton.setText("Brush");
		brushButton.setToolTipText("Translate axes by dragging up or down.");
		buttonEditGroup.add(brushButton);
		brushButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		brushButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setEditModeBrush(evt);
			}
		});

		modeBar.add(brushButton);

		jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
		modeBar.add(jSeparator2);
		modeBar.add(Box.createHorizontalGlue());

		countLabel.setFont(new java.awt.Font("Dialog", 0, 10));
		countLabel.setText("0 / 0   ");
		countLabel.setMaximumSize(new java.awt.Dimension(100, 16));
		modeBar.add(countLabel);

		resetBrushButton.setFont(new java.awt.Font("Dialog", 0, 10));
		resetBrushButton.setText("Reset Brush");
		resetBrushButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				resetBrushButtonActionPerformed(evt);
			}
		});

		modeBar.add(resetBrushButton);

		resetAllButton.setBackground(new java.awt.Color(255, 153, 153));
		resetAllButton.setFont(new java.awt.Font("Dialog", 0, 10));
		resetAllButton.setText("Reset All");
		resetAllButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				resetAllButtonActionPerformed(evt);
			}
		});

		modeBar.add(resetAllButton);

		toolbarPanel.add(modeBar);

		// 界面简单修改
		viewLabel.setFont(new java.awt.Font("Dialog", 0, 10));
		viewLabel.setText("View Mode: ");
		viewBar.add(viewLabel);

		scaleZeroMaxButton.setFont(new java.awt.Font("Dialog", 0, 11));
		scaleZeroMaxButton.setText("0-max");
		scaleZeroMaxButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						scaleZeroMaxItemActionPerformed(evt);
					}
				});
		buttonViewGroup.add(scaleZeroMaxButton);
		viewBar.add(scaleZeroMaxButton);

		scaleMinMaxButton.setFont(new java.awt.Font("Dialog", 0, 11));
		scaleMinMaxButton.setText("min-max");
		scaleMinMaxButton.setSelected(true);
		scaleMinMaxButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						scaleMinMaxItemActionPerformed(evt);
					}
				});
		buttonViewGroup.add(scaleMinMaxButton);
		viewBar.add(scaleMinMaxButton);

		scaleMinMaxAbsButton.setFont(new java.awt.Font("Dialog", 0, 11));
		scaleMinMaxAbsButton.setText("min-max (abs)");
		scaleMinMaxAbsButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						scaleMinMaxAbsItemActionPerformed(evt);
					}
				});
		buttonViewGroup.add(scaleMinMaxAbsButton);
		viewBar.add(scaleMinMaxAbsButton);

		jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
		viewBar.add(jSeparator1);
		viewBar.add(Box.createHorizontalGlue());

		preferencesButton.setFont(new java.awt.Font("Dialog", 0, 11));
		preferencesButton.setText("Preferences...");
		preferencesButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						preferencesMenuActionPerformed(evt);
					}
				});
		viewBar.add(preferencesButton);

		toolbarPanel.add(viewBar);

		this.add(toolbarPanel, java.awt.BorderLayout.NORTH);

		parallelDisplay.setPreferredSize(new java.awt.Dimension(800, 500));
		this.add(parallelDisplay, java.awt.BorderLayout.CENTER);

	}

	private void histogramBoxActionPerformed(java.awt.event.ActionEvent evt) {
		parallelDisplay.setBoolPreference("histogram",
				histogramBox.isSelected());
		parallelDisplay.repaint();
	}

	private void fuzzyBrushBoxActionPerformed(java.awt.event.ActionEvent evt) {
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

	private void resetAllButtonActionPerformed(java.awt.event.ActionEvent evt) {
		parallelDisplay.resetAll();
	}

	private void resetBrushButtonActionPerformed(java.awt.event.ActionEvent evt) {
		parallelDisplay.setCurrentBrush(null);
	}

	private void hoverBoxActionPerformed(java.awt.event.ActionEvent evt) {
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

	private void radiusFieldActionPerformed(java.awt.event.ActionEvent evt) {
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

	private void radiusFieldFocusGained(java.awt.event.FocusEvent evt) {
		radiusField.selectAll();
	}

	private void setEditModeTranslate(java.awt.event.ActionEvent evt) {
		parallelDisplay.setEditMode(ParallelDisplay.TRANSLATE);
		translateButton.setSelected(true);
	}

	private void setEditModeScale(java.awt.event.ActionEvent evt) {
		parallelDisplay.setEditMode(ParallelDisplay.SCALE);
		scaleButton.setSelected(true);
	}

	private void setEditModeOrder(java.awt.event.ActionEvent evt) {
		parallelDisplay.setEditMode(ParallelDisplay.REORDER);
		orderButton.setSelected(true);
	}

	private void setEditModeBrush(java.awt.event.ActionEvent evt) {
		parallelDisplay.setEditMode(ParallelDisplay.BRUSH);
		brushButton.setSelected(true);
	}

	private void tooltipBoxActionPerformed(java.awt.event.ActionEvent evt) {
		parallelDisplay.setBoolPreference("hoverText", tooltipBox.isSelected());
	}

	private void scaleMinMaxAbsItemActionPerformed(
			java.awt.event.ActionEvent evt) {
		parallelDisplay.minMaxAbsScale();
	}

	private void scaleMinMaxItemActionPerformed(java.awt.event.ActionEvent evt) {
		parallelDisplay.minMaxScale();
	}

	private void scaleZeroMaxItemActionPerformed(java.awt.event.ActionEvent evt) {
		parallelDisplay.zeroMaxScale();
	}

	private void preferencesMenuActionPerformed(java.awt.event.ActionEvent evt) {
		PrefsFrame pf = new PrefsFrame(parallelDisplay);
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
}
