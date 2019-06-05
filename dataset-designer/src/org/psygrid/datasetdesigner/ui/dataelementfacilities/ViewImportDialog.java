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
package org.psygrid.datasetdesigner.ui.dataelementfacilities;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.psygrid.data.model.hibernate.Document;
import org.psygrid.datasetdesigner.ui.DocumentPanel;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

public class ViewImportDialog extends JDialog implements ActionListener{
	
	private DocumentPanel panel;
	private Document document;
	private JButton		doneButton;
	
	public ViewImportDialog(JDialog parentDialog, Document document, boolean isDEL) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.previewimported"));
		this.document = document;
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildElementDisplayPanel(),  BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(null);  
		setVisible(true);
	}
	
	private JPanel buildElementDisplayPanel() {		
		panel = new DocumentPanel((JDialog)this, (Document)document, true);
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		
		
		JScrollPane filterColumnScrollPane = new javax.swing.JScrollPane(panel);
		filterColumnScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		filterColumnScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		filterColumnScrollPane.setMaximumSize(new java.awt.Dimension(800, 200));
		filterColumnScrollPane.setMinimumSize(new java.awt.Dimension(400, 180));
		filterColumnScrollPane.setPreferredSize(new java.awt.Dimension(600,200));
		
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		doneButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.done"));
		doneButton.addActionListener(this);
		buttonPanel.add(doneButton);
		panel.add(buttonPanel);
		
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == doneButton) {
			this.dispose();
		}
	}

}
