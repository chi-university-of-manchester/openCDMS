/*
Copyright (c) 2006-2008, The University of Manchester, UK.

This file is part of PsyGrid.

PsyGrid is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as
published by the Free Software Foundation, either version 3 of
the License, or (at your option) any later version.

PsyGrid is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with PsyGrid.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.psygrid.datasetdesigner.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.hibernate.DateEntry;
import org.psygrid.data.model.hibernate.DateValue;
import org.psygrid.data.model.hibernate.DerivedEntry;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.IntegerEntry;
import org.psygrid.data.model.hibernate.IntegerValue;
import org.psygrid.data.model.hibernate.NumericEntry;
import org.psygrid.data.model.hibernate.NumericValue;
import org.psygrid.data.model.hibernate.OptionEntry;
import org.psygrid.data.model.hibernate.TextValue;
import org.psygrid.data.model.hibernate.Value;

/**
 * @author Lucy Bridges
 *
 */
public class TestCaseTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	final String[] columnNames = {"Entry", "Label", "Input"};

	//Rows
	private List<Entry> testEntries;
	private List<String> testLabels;
	private List<Value> testValues;

	private JDialog parentDialog;

	public TestCaseTableModel(JDialog parentDialog, Map variables) {
		testEntries = new ArrayList<Entry>();
		testLabels = new ArrayList<String>();
		testValues = new ArrayList<Value>();
		testLabels.addAll(variables.keySet());
		for (Object label: variables.keySet()) {
			Entry entry = (Entry)variables.get(label);
			testValues.add(getNewValue(entry));

			testEntries.add((Entry)variables.get(label));
		}
	}

	public TestCaseTableModel(JDialog parentDialog, Map variables, Map<String,Value> testMap) {
		testEntries = new ArrayList<Entry>();
		testLabels = new ArrayList<String>();
		testValues = new ArrayList<Value>();

		for (String key: testMap.keySet()) {
			testEntries.add((Entry)variables.get(key));
			testLabels.add(key);
			testValues.add(testMap.get(key));
		}
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		if (testLabels == null) {
			return 0;
		}
		return testLabels.size();
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Object getValueAt(int row, int col) {
		switch (col) {
		case 0:
			return testEntries.get(row).getName();
		case 1:
			return testLabels.get(row);
		case 2:
			Value value = testValues.get(row);
			if (value == null) {
				return "";
			}
			return value.getValueAsString();
		default:
			return "";
		}
	}

	public void removeRow(int row) {
		testEntries.remove(row);
		testLabels.remove(row);
		testValues.remove(row);

		fireTableDataChanged();
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		if (column == 0 || column == 1) {
			return false;
		}
		return true;
	}

	public void setValueAt(Object value, int row, int column) {
		if (column == 0) {
		}
		else if (column == 1) {
		}
		else if (column == 2) {
			if (value == null) {
				Entry entry = testEntries.get(row);
				testValues.add(getNewValue(entry));
			}
			else {
				Value oldValue = testValues.get(row);

				Value v = null;
				String object = (String)value;
				if (oldValue instanceof TextValue) {
					v = new TextValue();
					((TextValue)v).setValue((String)object);
				}
				else {
					try {
						Double d = Double.parseDouble(object);
						v = new NumericValue();
						((NumericValue)v).setValue(d);
					}
					catch (Exception e1) {
						try {
							Integer i = Integer.parseInt(object);
							v = new IntegerValue();
							((IntegerValue)v).setValue(i);
						}
						catch (Exception e2) {
							try {
								DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
								Date d = format.parse(object);	
								v = new DateValue();
								((DateValue)v).setValue(d);
							}
							catch (Exception e4) {
								try {
									DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
									Date d = format.parse(object);	
									v = new DateValue();
									((DateValue)v).setValue(d);
								}
								catch (Exception e5) {
									//If everything else fails it must be a string
									v = new TextValue();
									((TextValue)v).setValue((String)object);
								}
							}
						}
					}
				}

				if (! oldValue.getClass().equals(v.getClass())) {
					if (oldValue instanceof DateValue) {
						WrappedJOptionPane.showMessageDialog(parentDialog, "The value entered must be a date.", "", JOptionPane.INFORMATION_MESSAGE);
					}
					else if (oldValue instanceof IntegerValue) {
						WrappedJOptionPane.showMessageDialog(parentDialog, "The value entered must be an integer.", "", JOptionPane.INFORMATION_MESSAGE);
					}
					else if (oldValue instanceof NumericValue) {
						WrappedJOptionPane.showMessageDialog(parentDialog, "The value entered must be a number.", "", JOptionPane.INFORMATION_MESSAGE);
					}
					else {
						//Shouldn't get this far??
						WrappedJOptionPane.showMessageDialog(parentDialog, "The value entered must be text.", "", JOptionPane.INFORMATION_MESSAGE);
					}
					return;
				}

				testValues.set(row, v);

			}
		}

		fireTableDataChanged();
	}

	public Value getNewValue(Entry entry) {
		Value value;
		if (entry instanceof DateEntry) {
			value = new DateValue();
		}
		else if (entry instanceof IntegerEntry) {
			value = new IntegerValue();
		}
		else if (entry instanceof NumericEntry
				|| entry instanceof DerivedEntry
				|| entry instanceof OptionEntry) {
			value = new NumericValue();
		}
		else {
			value = new TextValue("");
		}
		return value;
	}

	public List<String> getTestLabels() {
		return testLabels;
	}
	public List<Value> getTestValues() {
		return testValues;
	}

	/*
	 * JTable uses this method to determine the default renderer/
	 * editor for each cell.  If we didn't implement this method,
	 * then the last column would contain text ("true"/"false"),
	 * rather than a check box.
	 */
	//public Class getColumnClass(int c) {
	//	return getValueAt(0, c).getClass();
	//}

}
