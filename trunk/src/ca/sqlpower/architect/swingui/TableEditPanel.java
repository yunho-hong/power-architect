package ca.sqlpower.architect.swingui;

import javax.swing.*;
import ca.sqlpower.architect.*;

public class TableEditPanel extends JPanel implements ArchitectPanel {

	protected SQLTable table;
	JTextField name;
	JTextField pkName;
	JTextArea remarks;

	public TableEditPanel(SQLTable t) {
		super(new FormLayout());
		add(new JLabel("Table Name"));
		add(name = new JTextField("", 30));
		add(new JLabel("Primary Key Name"));
		add(pkName = new JTextField("", 30));
		add(new JLabel("Remarks"));
		add(new JScrollPane(remarks = new JTextArea(4, 30)));
		editTable(t);
	}

	public void editTable(SQLTable t) {
		table = t;
		name.setText(t.getName());
		pkName.setText(t.getPrimaryKeyName());
		remarks.setText(t.getRemarks());
	}

	// --------------------- ArchitectPanel interface ------------------
	public void applyChanges() {
		table.setTableName(name.getText());
		table.setPrimaryKeyName(pkName.getText());
		table.setRemarks(remarks.getText());
	}

	public void discardChanges() {
	}
}
