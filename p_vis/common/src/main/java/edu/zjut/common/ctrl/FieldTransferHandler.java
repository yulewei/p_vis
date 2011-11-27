package edu.zjut.common.ctrl;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

import edu.zjut.common.data.attr.DataField;

class FieldTransferHandler extends TransferHandler {

	private int fieldType = FieldList.MEASURE;

	public FieldTransferHandler(int fieldType) {
		this.fieldType = fieldType;
	}

	public int getSourceActions(JComponent comp) {
		return COPY_OR_MOVE;
	}

	public Transferable createTransferable(JComponent comp) {
		FieldList<DataField> list = (FieldList<DataField>) comp;
		List<DataField> values = list.getSelectedValuesList();

		return new FieldDnD(fieldType, values);
	}

	public boolean canImport(TransferHandler.TransferSupport support) {
		if (!support.isDrop()) {
			return false;
		}

		FieldDnD data;
		try {
			data = (FieldDnD) support.getTransferable().getTransferData(
					new DataFlavor(FieldDnD.class,
							DataFlavor.javaSerializedObjectMimeType));
		} catch (Exception e) {
			return false;
		}

		return data.getFieldType() == fieldType;
	}

	public boolean importData(TransferHandler.TransferSupport support) {
		JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();

		int index = dl.getIndex();
		boolean insert = dl.isInsert();

		FieldDnD data;
		try {
			data = (FieldDnD) support.getTransferable().getTransferData(
					new DataFlavor(FieldDnD.class,
							DataFlavor.javaSerializedObjectMimeType));
		} catch (Exception e) {
			return false;
		}

		FieldList<DataField> list = (FieldList<DataField>) support
				.getComponent();
		DefaultListModel<DataField> model = (DefaultListModel<DataField>) list
				.getModel();

		List<DataField> values = data.getValues();
		for (int i = 0; i < values.size(); i++) {
			if (insert)
				model.insertElementAt(values.get(i), index++);
			else
				model.setElementAt(values.get(i), index++);
		}

		// model.insertElementAt(data, index);

		return true;
	}
}