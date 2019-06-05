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
package org.psygrid.datasetdesigner.ui.wizard.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.DocumentGroup;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.ElementUtility;

import org.psygrid.datasetdesigner.custom.DSDRendererTable;

import org.psygrid.datasetdesigner.ui.wizard.WizardPanel;
import org.psygrid.datasetdesigner.ui.wizard.WizardModel;

import org.psygrid.datasetdesigner.model.DSDocumentOccurrence;

import org.psygrid.datasetdesigner.utils.HelpHelper;


/**
 * A grid-like panel for mapping documents onto document stages
 * @author pwhelan
 */
public class SchedulePanel extends JPanel implements WizardPanel {
	
	/**
	 * The main model of the wizard
	 */
	private WizardModel wm;
	
	/**
	 * The table containing the mappings of documents onto document groups
	 */
	private JTable scheduleTable;
	
	/**
	 * Constructor - create the schedule panel 
	 * @param wm the main model of the wizard
	 */
	public SchedulePanel(WizardModel wm) {
		super();
		this.wm = wm;
		setLayout(new BorderLayout());
		add(buildNorthPanel(), BorderLayout.NORTH);
		add(buildMainPanel(), BorderLayout.CENTER);
	}

	/**
	 * Create the header panel
	 * @return the header panel containing the labels
	 */
	private JPanel buildNorthPanel() {
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		northPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdwizardvisitschedule"));
		northPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.wizard.visitschedule")));
		return northPanel;
	}
	
	/**
	 * Create the main panel containing a table with the mappings
	 * @return the main panel containing a table with the mappings
	 */
	private JPanel buildMainPanel() {
		JPanel holderPanel = new JPanel();
		holderPanel.setLayout(new BorderLayout());
		scheduleTable = new DSDRendererTable();
		scheduleTable.setModel(new CustomTableModel());
		//substance 4.0 defaults table headers to the left
		((DefaultTableCellRenderer)scheduleTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		holderPanel.add(new JScrollPane(scheduleTable), BorderLayout.CENTER);
		return holderPanel;
	}

	/*
	 * Called when next button is hit
	 * @return always true
	 */
	public boolean next() {
		return true;
	}
	
	/*
	 * Called when this wizard page is selected 
	 * Refresh the document and document groups based on options set in 
	 * previous pages 
	 */
	public void refreshPanel() {
		scheduleTable.setModel(new CustomTableModel());
		
		scheduleTable.getColumnModel().getColumn(0).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.documents"));
		for (int i=0; i<wm.getWizardDs().getDs().numDocumentGroups(); i++) {
			scheduleTable.getColumnModel().getColumn(i+1).setHeaderValue(wm.getWizardDs().getDs().getDocumentGroup(i).getName());
		}
		
		scheduleTable.validate();
		scheduleTable.repaint();
		
	}
	
	/**
	 * Table model to handle the specific document/document groups
	 * setup required in the schedule panel
	 */
	private class CustomTableModel extends DefaultTableModel {
		
		/**
		 * Get the number of documents from the wizard model
		 * @return the number of rows
		 */
		public int getRowCount() {
			if (wm.getWizardDs().getDs() == null) {
				return 0;
			}
			return wm.getWizardDs().getDs().numDocuments();
		}

		/**
		 * Get the number of document groups from the wizard model
		 * @return the number of columns
		 */
		public int getColumnCount() {
			if (wm.getWizardDs().getDs() == null) {
				return 0;
			}
			return wm.getWizardDs().getDs().numDocumentGroups() + 1;
		}
		
		/**
		 * Is the cell editable
		 * @param row index of the row
		 * @param col index of the col
		 * @return true if the cell is editable; false if not
		 */
		public boolean isCellEditable(int row, int column) {
			if (column == 0) {
				return false;
			}
			return true;
		}

		/**
		 * Return the class of the column 
		 * @param columnIndex the index of the column to check
		 * @return the class of the column
		 */
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex > 0) {
				return Boolean.class;
			}
			
			return super.getColumnClass(columnIndex);
		}

		/**
		 * Return the value at the given row and column
		 * @param row the index of the row
		 * @param column the index of the column
		 * @return the value at the given row, column
		 */
		public Object getValueAt(int row, int column) {
			if (column == 0) {
				return wm.getWizardDs().getDs().getDocument(row);
			}
			
			Document doc = wm.getWizardDs().getDs().getDocument(row);
			DocumentGroup docGroup = wm.getWizardDs().getDs().getDocumentGroup(column - 1);
			
			for (int i=0; i<doc.numOccurrences(); i++) {
				if (doc.getOccurrence(i).getDocumentGroup().equals(docGroup)) {
					return new Boolean(true);
				}
			}
		
			return new Boolean(false);
		}
		
		/**
		 * Set the value at the given row and column
		 * @param row the index of the row
		 * @param column the index of the column
		 * @param value the value to set for the given row, column
		 */
		public void setValueAt(Object value, int row, int column) {
			Document doc = wm.getWizardDs().getDs().getDocument(row);
			DocumentGroup docGroup = wm.getWizardDs().getDs().getDocumentGroup(column - 1);
			
			if (((Boolean)value).booleanValue()) {
				DSDocumentOccurrence docOcc = ElementUtility.createIDocumentOccurrence(doc, 
						docGroup, 
						docGroup.getName()+ "-" +doc.getName(), 
						docGroup.getName()+ "-" + doc.getName(), 
						docGroup.getName()+ "-" + doc.getName(), "", 
						false, false);
				doc.addOccurrence(docOcc.getDocOccurrence());
			} else {
				for (int i=0; i<doc.numOccurrences(); i++) {
					if (doc.getOccurrence(i).getDocumentGroup().equals(docGroup)) {
						doc.removeOccurrence(i);
					}
				}
			}
		}
	}
	
}
