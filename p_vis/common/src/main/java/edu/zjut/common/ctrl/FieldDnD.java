package edu.zjut.common.ctrl;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

public class FieldDnD<E> implements Transferable {

	private final Class<E> type;

	private List<E> values;

	public FieldDnD(Class<E> type, List<E> values) {
		this.type = type;
		this.values = values;
	}

	public Class<E> getType() {
		return type;
	}

	public List<E> getValues() {
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
