package edu.zjut.common.ctrl;

import java.awt.datatransfer.DataFlavor;

import javax.swing.TransferHandler;

public class FieldImporter<E> extends TransferHandler {

	protected final Class<E> classType;

	protected FieldDnD<E> data;

	public FieldImporter(Class<E> classType) {
		this.classType = classType;
	}

	@SuppressWarnings("unchecked")
	protected boolean getTransferData(TransferHandler.TransferSupport support) {
		try {
			data = (FieldDnD<E>) support.getTransferable().getTransferData(
					new DataFlavor(FieldDnD.class,
							DataFlavor.javaSerializedObjectMimeType));
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public boolean canImport(TransferHandler.TransferSupport support) {
		if (!support.isDrop())
			return false;

		if (!getTransferData(support))
			return false;

		return data.getType() == classType;
	}
}