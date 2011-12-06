package edu.zjut.common.ctrl;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import edu.zjut.common.ctrl.FieldComponent.ColorEnum;
import edu.zjut.common.data.attr.DimensionField;

public class FieldListCell<E> implements ListCellRenderer<E> {
	@Override
	public Component getListCellRendererComponent(JList<? extends E> list,
			E value, int index, boolean isSelected, boolean cellHasFocus) {
		ColorEnum color = ColorEnum.GREEN;

		if (value instanceof DimensionField) {
			color = ColorEnum.GREEN;
		} else {
			color = ColorEnum.BLUE;
		}

		FieldComponent fieldComp = new FieldComponent(value.toString(), color,
				isSelected);

		return fieldComp;
	}
}
