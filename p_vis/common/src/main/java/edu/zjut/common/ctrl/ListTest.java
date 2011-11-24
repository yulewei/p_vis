package edu.zjut.common.ctrl;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.border.LineBorder;

public class ListTest extends javax.swing.JFrame {

	private JPanel textPanel;
	private JPanel listPanel;
	private JPanel jPanel1;
	private JPanel jPanel2;
	private JPanel jPanel3;
	private JLabel jLabel1;
	private JLabel jLabel2;
	private JList<String> jList1;
	private JList<String> jList2;
	private FieldList<String> jList3;

	/**
	 * Auto-generated main method to display this JFrame
	 */
	public static void main(String[] args) {
		ListTest inst = new ListTest();
		inst.setLocationRelativeTo(null);
		inst.setVisible(true);
	}

	public ListTest() {
		BorderLayout thisLayout = new BorderLayout();
		getContentPane().setLayout(thisLayout);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel,
				javax.swing.BoxLayout.Y_AXIS));
		getContentPane().add(listPanel, BorderLayout.WEST);
		listPanel.setPreferredSize(new java.awt.Dimension(129, 190));
		listPanel.setBorder(BorderFactory.createTitledBorder("list"));

		jPanel1 = new JPanel();
		listPanel.add(jPanel1);
		jPanel1.setLayout(new BorderLayout());

		jLabel1 = new JLabel();
		jPanel1.add(jLabel1, BorderLayout.NORTH);
		jLabel1.setText("jLabel1");
		jLabel1.setBorder(new LineBorder(new java.awt.Color(0, 0, 0), 1, false));

		jList1 = new JList<String>();
		jPanel1.add(jList1, BorderLayout.CENTER);
		DefaultListModel<String> jList1Model = new DefaultListModel<String>();
		for (int i = 5; i >= 0; i--) {
			jList1Model.add(0, "Source item " + i);
		}

		jList1.setModel(jList1Model);
		jList1.setDragEnabled(true);
		jList1.setPreferredSize(new java.awt.Dimension(205, 34));

		jPanel2 = new JPanel();
		listPanel.add(jPanel2);
		jPanel2.setLayout(new BorderLayout());

		jLabel2 = new JLabel();
		jPanel2.add(jLabel2, BorderLayout.NORTH);
		jLabel2.setText("jLabel2");
		jLabel2.setBorder(new LineBorder(new java.awt.Color(0, 0, 0), 1, false));

		jList2 = new JList<String>();
		jPanel2.add(jList2, BorderLayout.CENTER);
		DefaultListModel<String> jList2Model = new DefaultListModel<String>();
		for (int i = 5; i >= 0; i--) {
			jList2Model.add(0, "Source item " + i);
		}

		jList2.setModel(jList2Model);
		jList2.setDragEnabled(true);
		jList2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jList2.setPreferredSize(new java.awt.Dimension(107, 190));

		textPanel = new JPanel();
		BorderLayout jPanel4Layout = new BorderLayout();
		textPanel.setLayout(jPanel4Layout);
		getContentPane().add(textPanel, BorderLayout.CENTER);
		textPanel.setPreferredSize(new java.awt.Dimension(239, 190));
		textPanel.setBorder(BorderFactory.createTitledBorder("text"));

		jPanel3 = new JPanel();
		jPanel3.setLayout(new BorderLayout());
		textPanel.add(jPanel3, BorderLayout.NORTH);

		jList3 = new FieldList<String>();
		DefaultListModel<String> jList3Model = new DefaultListModel<String>();
		for (int i = 2; i >= 0; i--) {
			jList3Model.add(0, "Target item " + i);
		}

		jList3.setModel(jList3Model);
		jList3.setDropMode(javax.swing.DropMode.INSERT);
		jList3.setLayoutOrientation(FieldList.HORIZONTAL);
		jList3.setVisibleRowCount(1);
		jList3.setTransferHandler(new ToTransferHandler());
		jList3.setPreferredSize(new java.awt.Dimension(281, 30));

		jPanel3.add(jList3, BorderLayout.CENTER);

		this.setSize(500, 600);
	}

	class FromTransferHandler extends TransferHandler {
		public int getSourceActions(JComponent comp) {
			return COPY_OR_MOVE;
		}

		public Transferable createTransferable(JComponent comp) {
			JList<String> list = (JList<String>) comp;

			return new StringSelection(list.getSelectedValue());
		}
	}

	class ToTransferHandler extends TransferHandler {

		public boolean canImport(TransferHandler.TransferSupport support) {
			return true;
		}

		public boolean importData(TransferHandler.TransferSupport support) {
			JList.DropLocation dl = (JList.DropLocation) support
					.getDropLocation();

			int index = dl.getIndex();

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
			model.insertElementAt(data, index);

			return true;
		}
	}
}
