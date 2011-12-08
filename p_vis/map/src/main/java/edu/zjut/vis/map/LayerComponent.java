package edu.zjut.vis.map;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import edu.zjut.common.ctrl.FieldComponent;
import edu.zjut.common.ctrl.FieldComponent.ColorEnum;

public class LayerComponent extends JPanel implements
		ListCellRenderer<LayerCheckableItem> {

	boolean isVisible = true;

	ImageIcon visibleOn;
	ImageIcon visibleOff;

	JLabel visibleLabel;
	FieldComponent<LayerCheckableItem> fieldComp;

	public LayerComponent() {
		this.setLayout(new BorderLayout());
		this.setOpaque(false);

		visibleOn = new ImageIcon(getClass().getResource("visible-on.png"));
		visibleOff = new ImageIcon(getClass().getResource("visible-off.png"));

		visibleLabel = new JLabel(isVisible ? visibleOn : visibleOff);
		this.add(visibleLabel, BorderLayout.WEST);

		fieldComp = new FieldComponent<LayerCheckableItem>();
		this.add(fieldComp, BorderLayout.CENTER);
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
		visibleLabel.setIcon(isVisible ? visibleOn : visibleOff);
	}

	@Override
	public Component getListCellRendererComponent(
			JList<? extends LayerCheckableItem> list, LayerCheckableItem value,
			int index, boolean isSelected, boolean cellHasFocus) {

		fieldComp.setValue(value);
		fieldComp.setColor(ColorEnum.YELLOW);
		fieldComp.setSelected(isSelected);

		this.setVisible(((LayerCheckableItem) value).isVisible());

		return this;
	}
}
