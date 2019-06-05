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
package org.psygrid.datasetdesigner.ui.editdialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.psygrid.data.model.hibernate.CompositeEntry;
import org.psygrid.data.model.hibernate.HibernateFactory;
import org.psygrid.data.model.hibernate.TextEntry;
import org.psygrid.datasetdesigner.utils.ListModelUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.custom.TextFieldWithStatus;

public class CompositeFixedLabelDialog extends JDialog implements ActionListener {
	
	private JButton okButton;
	private JButton cancelButton;
	
	private TextFieldWithStatus entryDisplayText;
	private TextFieldWithStatus addRemoveField;
	private JButton addButton;
	private JButton removeButton;
	private JList lists;
	
	private CompositeEntry parentEntry = null;
	
	private Vector okListeners = new Vector();
	
	public CompositeFixedLabelDialog(JFrame frame, CompositeEntry parentEntry) {
		super(frame, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.compositefixedlabels"));
		setModal(true);
		this.parentEntry = parentEntry;
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildCenterPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		init();
		pack();
		setLocationRelativeTo(null);  
	}
	
	public void init() {
		DefaultListModel compModel = new DefaultListModel();
		if (parentEntry != null) {
			for (int i=0; i<parentEntry.numRowLabels(); i++) {
				compModel.addElement(parentEntry.getRowLabel(i));
			}
			
			try {
				if (parentEntry.getEntry(0).getName().equals(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.fixedlabel"))) {
					entryDisplayText.setText(parentEntry.getEntry(0).getDisplayText());
				}
				
			} catch (org.psygrid.data.model.hibernate.ModelException mex) {
				//no basic entry at position 0
			}
			
		}
		lists.setModel(compModel);
	}
	
	
	private JPanel  buildCenterPanel() {
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());

		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
		
		JPanel entryDisplayTextPanel = new JPanel();
		entryDisplayTextPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel headerLabel = new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.columnheader"));
		entryDisplayText = new TextFieldWithStatus(40, true);
		entryDisplayTextPanel.add(headerLabel);
		entryDisplayTextPanel.add(entryDisplayText);
		
		
		addRemoveField = new TextFieldWithStatus(40, true);
		addButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.add"));
		addButton.addActionListener(this);
		removeButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.remove"));
		removeButton.addActionListener(this);
		
		JPanel addRemovePanel = new JPanel();
		addRemovePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		addRemovePanel.add(addRemoveField);
		addRemovePanel.add(addButton);
		addRemovePanel.add(removeButton);
		headerPanel.add(entryDisplayTextPanel);
		headerPanel.add(addRemovePanel);
		
		centerPanel.add(headerPanel, BorderLayout.NORTH);
		
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BorderLayout());
		lists = new JList();
		JScrollPane listScroll = new JScrollPane(lists);
		listScroll.setMinimumSize(new Dimension(400, (int)listScroll.getMinimumSize().getHeight()));
		listScroll.setMaximumSize(new Dimension(600, (int)listScroll.getMaximumSize().getHeight()));
		lists.setModel(new DefaultListModel());
		listPanel.setBorder(BorderFactory.createTitledBorder(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.rowlabels")));
		listPanel.add(listScroll, BorderLayout.CENTER);
		
		centerPanel.add(listPanel, BorderLayout.CENTER);
		
		return centerPanel;
	}
	
	private JPanel buildButtonPanel() {
		okButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ok"));
		okButton.addActionListener(this);
		cancelButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.cancel"));
		cancelButton.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		return buttonPanel;
	}
	
	public void fireActionEvent() {
		Iterator atIt = okListeners.iterator();
		
		while(atIt.hasNext()) {
			ActionListener al = (ActionListener)atIt.next();
			al.actionPerformed(new ActionEvent(this, 1, ""));
		}
	}

	
	public void addOKListener(ActionListener al) {
		okListeners.add(al);
	}
	
	public void removeOKListener(ActionListener al) {
		okListeners.remove(al);
	}
	
	public void actionPerformed(ActionEvent aet) {
		if (aet.getSource() == okButton) {
			
			((CompositeEntry)parentEntry).setRowLabels(ListModelUtility.convertListModelToStringList((DefaultListModel)lists.getModel()));
			
			boolean containsFixedLabelAlready = false;
			for (int z=0; z<parentEntry.numEntries(); z++) {
				if (parentEntry.getEntry(z) instanceof TextEntry) {
					if (((TextEntry)parentEntry.getEntry(z)).getName().equals("Fixed Label")) {
						((TextEntry)parentEntry.getEntry(z)).setDisplayText(entryDisplayText.getText());
						containsFixedLabelAlready = true;
					}
				}
			}

			if (!containsFixedLabelAlready) {
				HibernateFactory factory = new HibernateFactory();
				TextEntry text = factory.createTextEntry(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.fixedlabel"), entryDisplayText.getText());
				text.setSection(parentEntry.getSection());
				parentEntry.insertEntry(text, 0);
			}
			
			
			fireActionEvent();
			this.dispose();
		} else if (aet.getSource() == cancelButton) {
			this.dispose();
		} else if (aet.getSource() == addButton) {
			if (((DefaultListModel)lists.getModel()).contains(addRemoveField.getText())) {
				JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.labelexists"));
				return;
			}
			((DefaultListModel)lists.getModel()).addElement(addRemoveField.getText());
			addRemoveField.setText("");
		} else if (aet.getSource() == removeButton) {
			((DefaultListModel)lists.getModel()).removeElement(lists.getSelectedValue());
		}
		
		
	}
	
}

