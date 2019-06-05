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
package org.psygrid.datasetdesigner.ui.configurationdialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import java.io.StringReader;

import org.psygrid.data.model.hibernate.*;
import org.xml.sax.InputSource;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.datasetdesigner.renderer.EntryTableCellRenderer;
import org.psygrid.datasetdesigner.renderer.UneditableRedCellTableRenderer;
import org.psygrid.datasetdesigner.utils.HelpHelper;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

//JAXP
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

//DOM
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class ConfigureImportMappingDialog extends JDialog implements ActionListener {

	private final static String STRING_PREFIX = "org.psygrid.datasetdesigner.ui.configurationdialogs.configureimportmappingdialog.";
	
	/**
	 * The logger for this class
	 */
	private static final Log LOG = LogFactory.getLog(ConfigureImportMappingDialog.class);

	
	private JButton okButton;
	private JButton cancelButton;	
	
	private org.psygrid.data.model.hibernate.Document document;
	
	private JTable mappingTable;
	
	private JCheckBox importEnabled;
	
	private JFrame frame;

	
	public ConfigureImportMappingDialog(JFrame frame, org.psygrid.data.model.hibernate.Document document) {
		super(frame, PropertiesHelper.getStringFor(STRING_PREFIX + "configureimportmapping"));
		this.frame = frame;
		this.document = document;
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildApplyImportMappingPanel(),  BorderLayout.NORTH);
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		init();
		pack();
		setLocationRelativeTo(null);  
		setVisible(true);
	}

	private JPanel buildApplyImportMappingPanel() {
		JPanel applyImportMappingPanel = new JPanel();
		applyImportMappingPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdconfimportmapping"));
		applyImportMappingPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		applyImportMappingPanel.add(new JLabel(PropertiesHelper.getStringFor(STRING_PREFIX + "useimportmapping")));
		importEnabled = new JCheckBox();
		applyImportMappingPanel.add(importEnabled);
		return applyImportMappingPanel;
	}
	
	private JPanel buildMainPanel() {
		JPanel mainPanel = new JPanel();
		
		mappingTable = new JTable(new CustomTableModel());
		
		mappingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mappingTable.getColumnModel().getColumn(0).setHeaderValue("Name");
		mappingTable.getColumnModel().getColumn(1).setHeaderValue("Column");
		 
		TableColumn col = mappingTable.getColumnModel().getColumn(0);
		col.setCellRenderer(new EntryTableCellRenderer());
	    
		TableColumn selectColumn = mappingTable.getColumnModel().getColumn(1);
		selectColumn.setCellRenderer(new UneditableRedCellTableRenderer());
		
		mainPanel.add(new JScrollPane(mappingTable));
		
		return mainPanel;
	}

	
	private JPanel buildButtonPanel(){
		okButton = new JButton("Ok");
		okButton.addActionListener(this);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		return buttonPanel;
	}
	
	private boolean validatePanel() {
		//check that each entry has a column to map to
		for (int i=0; i<mappingTable.getRowCount(); i++) {
			//only check on entries that are editable
			if (mappingTable.getModel().isCellEditable(i, 1)) {
				if (mappingTable.getValueAt(i, 1) == null || 
						mappingTable.getValueAt(i, 1).equals("")) {
						//empty values are actually OK
				} else {
					try {
						new Integer(mappingTable.getValueAt(i, 1).toString());
					} catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(frame, PropertiesHelper.getStringFor(STRING_PREFIX + "mustbenumber"));
						return false;						
					}
				}
			}
		}
		return true;
	}		
	
	private void init() {
		//populate the table
		for (int i=0; i<document.numEntries(); i++) {
			Vector rowData = new Vector();
			rowData.add(document.getEntry(i).getName());
			rowData.add("");
			((CustomTableModel)mappingTable.getModel()).addRow(rowData);
		}

		
		importEnabled.setSelected(document.getIsImportEnabled());
		
		if (document.getIsImportEnabled()) {
			
			//parse the string
			//StringBuilder mappingString = new StringBuilder(document.getImportMappingString());
            try {
                DocumentBuilderFactory factory = 
                    DocumentBuilderFactory.newInstance();
	            DocumentBuilder builder = factory.newDocumentBuilder();
	            InputSource inStream = new InputSource();
	            inStream.setCharacterStream(new StringReader(document.getImportMappingString()));
	            Document doc = builder.parse(inStream);
	            
	            NodeList nodes = doc.getElementsByTagName("source_entry");
	            
	            for (int i=0; i<nodes.getLength(); i++) {
	            	Node node = nodes.item(i);
	            	try {
		            	Integer nodeInt = new Integer (node.getChildNodes().item(0).getNodeValue());
		            	mappingTable.setValueAt(nodeInt, i, 1);
	            	} catch (Exception ex) {
	            		LOG.error("Exception setting an integer value for the source entry", ex);
	            	}
	            }

	            //verify that the number of doc_entries match the number of entries in the document
	            NodeList docNodes = doc.getElementsByTagName("document_entry");
	            
	            if (docNodes.getLength() != document.numEntries()) {
	            	JOptionPane.showMessageDialog(frame, 
        			PropertiesHelper.getStringFor(STRING_PREFIX + "docchanged"));
	            }
            } catch (Exception ex) {
            	LOG.error("Exception parsing XML", ex);
            }
		}
	}
	
	private boolean isEntryTypeCofigurable(int indexInDocument) {
		boolean configurable = true;
		
		try {
			Entry entry = document.getEntry(indexInDocument);
			
			if (entry instanceof NarrativeEntry ||
				entry instanceof DerivedEntry ||
				entry instanceof CompositeEntry ||
				entry instanceof ExternalDerivedEntry) {
				configurable = false;
			}
		} catch (Exception ex) {
			LOG.error("CIM : error checking if entry is configurable",ex);
		}
		
		return configurable;
	}
	
	private void buildAndSetImportMapping() {
		StringBuilder mappingString = new StringBuilder();
		mappingString.append("<importmapping><csv_startingrow>1</csv_startingrow>");
		
		for (int i=0;i<mappingTable.getRowCount(); i++) {
			//entries in documents are 0-based but mapping doc_entry is 1-based
			//only apply mapping string for configured mappings
			if (mappingTable.getModel().getValueAt(i, 1).toString() != null && 
				!mappingTable.getModel().getValueAt(i, 1).equals("")) {
				mappingString.append("<mapping><document_entry>" + (i+1) + "</document_entry><source_entry>" + mappingTable.getModel().getValueAt(i, 1)  + "</source_entry></mapping>");
			}
		}
				
		mappingString.append("</importmapping>");
		document.setImportMappingString(mappingString.toString());
	}
	
	public void actionPerformed(ActionEvent aet) {
		if (aet.getSource() == okButton) {
			if (validatePanel()) {
				if (document.getIsImportEnabled() != importEnabled.isSelected()) {
					int returnVal = JOptionPane.NO_OPTION;
					//ask for user confirmation that import mappings are chaning!
					if (importEnabled.isSelected()) {
						returnVal = JOptionPane.showConfirmDialog(this, PropertiesHelper.getStringFor(STRING_PREFIX + "changeimportstatuson"), "Confirm Changes", JOptionPane.YES_NO_OPTION);
					} else {
						returnVal = JOptionPane.showConfirmDialog(this, PropertiesHelper.getStringFor(STRING_PREFIX + "changeimportstatusoff"), "Confirm Changes", JOptionPane.YES_NO_OPTION);
					}
				
					//no user confirmation; exit out of here
					if (returnVal == JOptionPane.NO_OPTION) {
						//exit out of this
						return;
					}
				}
					
				//set the import mapping settings
				document.setIsImportEnabled(importEnabled.isSelected());
				if (importEnabled.isSelected()) {
					//set the new mapping string to the document
					buildAndSetImportMapping();
				} else {
					//clear the import mapping string
					document.setImportMappingString(null);
				}
				this.dispose();
			}
		} else if (aet.getSource() == cancelButton) {
			this.dispose();
		}
	}
	
	private class CustomTableModel extends DefaultTableModel {
		
		private Vector rows;
		
		public CustomTableModel() {
			rows = new Vector();
		}

		@Override
		public void addRow(Vector rowData) {
			rows.add(rowData);
			fireTableDataChanged();
		}
		
		public void removeRow(int row) {
			rows.remove(row);
			fireTableDataChanged();
		}

		public int getRowCount() {
			if (rows != null) {
				return rows.size();
			}
			return 0;
		}

		public int getColumnCount() {
			return 2;
		}
		
		@Override
		public boolean isCellEditable(int row, int column) {
			//only the entry column is editable
			if (column == 1) {
				//false if entry is a narrative, derived entry etc.
				if (isEntryTypeCofigurable(row)) {
					return true;
				} 
			}
			
			return false;
		}

		@Override
		public Object getValueAt(int row, int column) {
			Vector rowData = (Vector)rows.get(row);
			return rowData.get(column);
		}
		
		public void setValueAt(Object value, int row, int column) {
			((Vector)rows.get(row)).setElementAt(value, column);
		}
		
	}

	
}
