package edu.zjut.common.ctrl;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

import edu.zjut.common.data.attr.DataField;

public class FieldDnD implements Transferable {

	private int fieldType = FieldList.MEASURE;
	private List<DataField> values;

	public FieldDnD(int fieldType, List<DataField> values) {
		this.fieldType = fieldType;
		this.values = values;
	}

	public int getFieldType() {
		return fieldType;
	}

	public List<DataField> getValues() {
		return values;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { new DataFlavor(getClass(),
				DataFlavor.javaSerializedObjectMimeType) };
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return getClass().equals(flavor.getRepresentationClass());
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if (isDataFlavorSupported(flavor))
			return this;
		return null;
	}
}
