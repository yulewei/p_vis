package edu.zjut.common.ctrl;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

public class FieldTransferHandler extends TransferHandler {

	public int getSourceActions(JComponent comp) {
		return COPY_OR_MOVE;
	}

	public Transferable createTransferable(JComponent comp) {
		JList<String> list = (JList<String>) comp;
		List<String> values = list.getSelectedValuesList();

		// 导出选中的多个值
		StringBuffer buff = new StringBuffer();
		for (String v : values) {
			buff.append(v + "\n");
		}

		return new StringSelection(buff.toString());

		// return new StringSelection(list.getSelectedValue());
	}

	public boolean canImport(TransferHandler.TransferSupport support) {
		return true;
	}

	public boolean importData(TransferHandler.TransferSupport support) {
		JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();

		int index = dl.getIndex();
		boolean insert = dl.isInsert();

		String data;
		try {
			data = (String) support.getTransferable().getTransferData(
					DataFlavor.stringFlavor);
		} catch (Exception e) {
			return false;
		}

		JList<String> list = (JList<String>) support.getComponent();
		DefaultListModel<String> model = (DefaultListModel<String>) list
				.getModel();

		String[] values = data.split("\n");
		for (int i = 0; i < values.length; i++) {
			if (insert)
				model.insertElementAt(values[i], index++);
			else
				model.setElementAt(values[i], index++);
		}

		// model.insertElementAt(data, index);

		return true;
	}
}