/*

Copyright (c) 2001, 2002, 2003 Flo Ledermann <flo@subnet.at>

This file is part of parvis - a parallel coordiante based data visualisation
tool written in java. You find parvis and additional information on its
website at http://www.mediavirus.org/parvis.

parvis is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

parvis is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with parvis (in the file LICENSE.txt); if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 */

package org.mediavirus.parvis.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

/**
 * @author yulewei
 * @author flo
 */
public class PrefsDialog extends JDialog {

	ParallelDisplay parent;

	/** Creates new form PrefsDialog */
	public PrefsDialog(ParallelDisplay parent) {
		this.parent = parent;

		initComponents();

		updatePrefs();
	}

	private void updatePrefs() {
		binsField.setText(parent.getIntPreference("histogramBins") + "");

		switch (parent.getIntPreference("histogramWidth")) {
		case ParallelDisplay.HISTO_TOTALREC:
			totalRadio.setSelected(true);
			break;
		case ParallelDisplay.HISTO_BINREC:
			binRadio.setSelected(true);
			break;
		case ParallelDisplay.HISTO_BRUSHREC:
			brushedRadio.setSelected(true);
			break;
		}

		bgColButton.setBackground(parent.getBackground());
		recordColButton.setBackground(parent.getColorPreference("recordColor"));
		axisColButton.setBackground(parent.getForeground());
		brushColButton.setBackground(parent.getColorPreference("brushColor"));
	}

	private void initComponents() {
		GridBagConstraints gridBagConstraints;

		histoWidthGroup = new ButtonGroup();
		colorPanel = new JPanel();
		label1 = new JLabel();
		bgColButton = new JButton();
		label2 = new JLabel();
		recordColButton = new JButton();
		label3 = new JLabel();
		axisColButton = new JButton();
		label31 = new JLabel();
		brushColButton = new JButton();
		histogramPanel = new JPanel();
		label32 = new JLabel();
		binsField = new JTextField();
		label33 = new JLabel();
		jPanel1 = new JPanel();
		totalRadio = new JRadioButton();
		binRadio = new JRadioButton();
		brushedRadio = new JRadioButton();

		getContentPane().setLayout(new GridBagLayout());

		colorPanel.setLayout(new GridBagLayout());

		colorPanel.setBorder(new TitledBorder(new EtchedBorder(), "Colors"));
		label1.setHorizontalAlignment(SwingConstants.TRAILING);
		label1.setText("Background");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.insets = new Insets(4, 4, 2, 4);
		colorPanel.add(label1, gridBagConstraints);

		bgColButton.setBackground(new Color(255, 255, 255));
		bgColButton.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		bgColButton.setHorizontalTextPosition(SwingConstants.CENTER);
		bgColButton.setMargin(new Insets(0, 0, 0, 0));
		bgColButton.setMaximumSize(new Dimension(10, 10));
		bgColButton.setMinimumSize(new Dimension(10, 10));
		bgColButton.setPreferredSize(new Dimension(14, 14));
		bgColButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				colButtonActionPerformed(evt);
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new Insets(4, 4, 4, 4);
		colorPanel.add(bgColButton, gridBagConstraints);

		label2.setHorizontalAlignment(SwingConstants.TRAILING);
		label2.setText("Records");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.insets = new Insets(2, 4, 2, 4);
		colorPanel.add(label2, gridBagConstraints);

		recordColButton.setBackground(new Color(0, 0, 0));
		// recordColButton.setFont(new Font("Dialog", 1, 3));
		recordColButton.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		recordColButton.setHorizontalTextPosition(SwingConstants.CENTER);
		recordColButton.setMargin(new Insets(0, 0, 0, 0));
		recordColButton.setMaximumSize(new Dimension(10, 10));
		recordColButton.setMinimumSize(new Dimension(10, 10));
		recordColButton.setPreferredSize(new Dimension(14, 14));
		recordColButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				colButtonActionPerformed(evt);
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new Insets(4, 4, 4, 4);
		colorPanel.add(recordColButton, gridBagConstraints);

		label3.setFont(new java.awt.Font("Dialog", 0, 11));
		label3.setHorizontalAlignment(SwingConstants.TRAILING);
		label3.setText("Axes & Labels");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new Insets(2, 4, 2, 4);
		colorPanel.add(label3, gridBagConstraints);

		axisColButton.setBackground(new Color(0, 0, 0));
		axisColButton.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		axisColButton.setHorizontalTextPosition(SwingConstants.CENTER);
		axisColButton.setMargin(new Insets(0, 0, 0, 0));
		axisColButton.setMaximumSize(new Dimension(10, 10));
		axisColButton.setMinimumSize(new Dimension(10, 10));
		axisColButton.setPreferredSize(new Dimension(14, 14));
		axisColButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				colButtonActionPerformed(evt);
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new Insets(4, 4, 4, 4);
		colorPanel.add(axisColButton, gridBagConstraints);

		label31.setHorizontalAlignment(SwingConstants.TRAILING);
		label31.setText("Default Brush");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.insets = new Insets(2, 4, 2, 4);
		colorPanel.add(label31, gridBagConstraints);

		brushColButton.setBackground(new Color(0, 0, 0));
		brushColButton.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		brushColButton.setHorizontalTextPosition(SwingConstants.CENTER);
		brushColButton.setMargin(new Insets(0, 0, 0, 0));
		brushColButton.setMaximumSize(new Dimension(10, 10));
		brushColButton.setMinimumSize(new Dimension(10, 10));
		brushColButton.setPreferredSize(new Dimension(14, 14));
		brushColButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				colButtonActionPerformed(evt);
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.insets = new Insets(4, 4, 4, 4);
		colorPanel.add(brushColButton, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		getContentPane().add(colorPanel, gridBagConstraints);

		histogramPanel.setLayout(new GridBagLayout());

		histogramPanel.setBorder(new TitledBorder(new EtchedBorder(),
				"Histogram"));
		label32.setHorizontalAlignment(SwingConstants.TRAILING);
		label32.setText("Bins: ");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		histogramPanel.add(label32, gridBagConstraints);

		binsField.setHorizontalAlignment(JTextField.TRAILING);
		binsField.setText("10");
		// binsField.setPreferredSize(new Dimension(36, 25));
		binsField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				binsFieldActionPerformed(evt);
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		histogramPanel.add(binsField, gridBagConstraints);

		label33.setHorizontalAlignment(SwingConstants.TRAILING);
		label33.setText("Width: ");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		histogramPanel.add(label33, gridBagConstraints);

		jPanel1.setLayout(new GridLayout(3, 1));

		totalRadio.setSelected(true);
		totalRadio.setText("total # of records");
		histoWidthGroup.add(totalRadio);
		totalRadio.setMargin(new Insets(0, 0, 0, 0));
		totalRadio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				binWidthActionPerformed(evt);
			}
		});

		jPanel1.add(totalRadio);

		binRadio.setText("max # in bin");
		histoWidthGroup.add(binRadio);
		binRadio.setMargin(new Insets(0, 0, 0, 0));
		binRadio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				binWidthActionPerformed(evt);
			}
		});

		jPanel1.add(binRadio);

		brushedRadio.setText("max brushed in bin");
		histoWidthGroup.add(brushedRadio);
		brushedRadio.setMargin(new Insets(0, 0, 0, 0));
		brushedRadio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				binWidthActionPerformed(evt);
			}
		});

		jPanel1.add(brushedRadio);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		histogramPanel.add(jPanel1, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		getContentPane().add(histogramPanel, gridBagConstraints);

		pack();
	}

	private void colButtonActionPerformed(ActionEvent evt) {
		Color current = bgColButton.getBackground();

		if (evt.getSource() == recordColButton) {
			current = recordColButton.getBackground();
		} else if (evt.getSource() == axisColButton) {
			current = axisColButton.getBackground();
		} else if (evt.getSource() == brushColButton) {
			current = brushColButton.getBackground();
		}

		// Bring up a color chooser
		Color c = JColorChooser.showDialog(this, "Choose Color", current);

		if (evt.getSource() == bgColButton) {
			bgColButton.setBackground(c);
			parent.setBackground(c);
		} else if (evt.getSource() == recordColButton) {
			recordColButton.setBackground(c);
			parent.setPreference("recordColor", c);
		} else if (evt.getSource() == axisColButton) {
			axisColButton.setBackground(c);
			parent.setForeground(c);
		} else {
			brushColButton.setBackground(c);
			parent.setPreference("brushColor", c);
		}
	}

	private void binsFieldActionPerformed(ActionEvent evt) {
		parent.setIntPreference("histogramBins",
				Integer.parseInt(binsField.getText()));
	}

	private void binWidthActionPerformed(ActionEvent evt) {
		if (totalRadio.isSelected()) {
			parent.setIntPreference("histogramWidth",
					ParallelDisplay.HISTO_TOTALREC);
		} else if (binRadio.isSelected()) {
			parent.setIntPreference("histogramWidth",
					ParallelDisplay.HISTO_BINREC);
		} else if (brushedRadio.isSelected()) {
			parent.setIntPreference("histogramWidth",
					ParallelDisplay.HISTO_BRUSHREC);
		}
	}

	private JLabel label3;
	private JLabel label2;
	private JLabel label1;
	private JButton brushColButton;
	private JPanel jPanel1;
	private JRadioButton binRadio;
	private JButton axisColButton;
	private JRadioButton brushedRadio;
	private JPanel colorPanel;
	private JRadioButton totalRadio;
	private JLabel label33;
	private JLabel label32;
	private JLabel label31;
	private JTextField binsField;
	private JButton bgColButton;
	private ButtonGroup histoWidthGroup;
	private JButton recordColButton;
	private JPanel histogramPanel;
}
