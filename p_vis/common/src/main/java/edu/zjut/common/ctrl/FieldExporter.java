package edu.zjut.common.ctrl;

import java.awt.datatransfer.Transferable;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

public class FieldExporter<E> extends TransferHandler {

	private final Class<E> type;

	public FieldExporter(Class<E> type) {
		this.type = type;
	}

	public int getSourceActions(JComponent comp) {
		return COPY_OR_MOVE;
	}

	public Transferable createTransferable(JComponent comp) {
		FieldList<E> list = (FieldList<E>) comp;
		List<E> values = list.getSelectedValuesList();
		return new FieldDnD<E>(type, values);
	}
}