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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import org.psygrid.datasetdesigner.utils.ElementUtility;
import org.psygrid.datasetdesigner.utils.ListModelUtility;
import org.psygrid.datasetdesigner.utils.SpringUtilities;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

import org.psygrid.datasetdesigner.custom.TextFieldWithStatus;

import org.psygrid.data.model.hibernate.Site;

public class AddSiteDialog extends JDialog implements ActionListener {
		
	private JButton okButton;
	private JButton cancelButton;
	
	private JButton addConsultantButton;
	private JButton removeConsultantButton;
	
	private JList siteList;
	
	private TextFieldWithStatus siteNameField;
	private TextFieldWithStatus siteIDField;
	private TextFieldWithStatus geographicCodeField;
	private TextFieldWithStatus consultantField;
	
	private JList consultantList;
	
	private Site site;
	private boolean edit = false;
	
	public AddSiteDialog(JDialog parentDialog, JList siteList) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.addsite"));
		setModal(true);
		this.siteList = siteList;
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public AddSiteDialog(JDialog parentDialog, JList siteList, String titleAppendix) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.addsite") + " for " + titleAppendix);
		setModal(true);
		this.siteList = siteList;
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public AddSiteDialog(JDialog parentDialog, JList siteList, Site site) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.editsite"));
		setModal(true);
		this.site=site;
		this.siteList = siteList;
		this.edit = true;
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		pack();
		init(site);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void init(Site site) {
		siteNameField.setText(site.getSiteName());
		siteIDField.setText(site.getSiteId());
		geographicCodeField.setText(site.getGeographicCode());
		DefaultListModel consultantModel = new DefaultListModel();
		
		if (site.getConsultants() != null) {
			for (int i=0; i<site.getConsultants().size(); i++) {
				consultantModel.addElement(site.getConsultants().get(i));
			}
		}
		
		consultantList.setModel(consultantModel);

	}
	
	private JPanel buildMainPanel() {
		JPanel holderPanel = new JPanel();
		holderPanel.setLayout(new BorderLayout());
		
		JPanel mainPanel = new JPanel(new SpringLayout());
		siteNameField = new TextFieldWithStatus(40, true);
		siteIDField = new TextFieldWithStatus(40, true);
		geographicCodeField = new TextFieldWithStatus(40, true);
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.sitename")));
		mainPanel.add(siteNameField);
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.siteID")));
		mainPanel.add(siteIDField);
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.geocode")));
		mainPanel.add(geographicCodeField);
		
		SpringUtilities.makeCompactGrid(mainPanel,
                3, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad

		JPanel consultantPanel = new JPanel();
		consultantPanel.setBorder(BorderFactory.createTitledBorder(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.consultants")));
		consultantPanel.setLayout(new BorderLayout());
		JPanel addRemovePanel = new JPanel();
		addRemovePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		consultantField = new TextFieldWithStatus(40, false);
		addConsultantButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.add"));
		addConsultantButton.addActionListener(this);
		removeConsultantButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.remove"));
		removeConsultantButton.addActionListener(this);
		addRemovePanel.add(consultantField);
		addRemovePanel.add(addConsultantButton);
		addRemovePanel.add(removeConsultantButton);
		consultantPanel.add(addRemovePanel, BorderLayout.NORTH);
		consultantList = new JList();
		DefaultListModel consultantModel = new DefaultListModel();
		consultantList.setModel(consultantModel);
		JScrollPane consultantPane = new JScrollPane(consultantList);
		consultantPane.setPreferredSize(new Dimension(200, 100));
		consultantPane.setMinimumSize(new Dimension(200, 100));
		consultantPane.setMaximumSize(new Dimension(200, 100));
		consultantPanel.add(consultantPane, BorderLayout.CENTER);
		
		
		holderPanel.add(mainPanel, BorderLayout.NORTH);
		holderPanel.add(consultantPanel, BorderLayout.CENTER);
		
		return holderPanel;
	}
	
	private JPanel buildButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		okButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ok"));
		okButton.addActionListener(this);
		cancelButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.cancel"));
		cancelButton.addActionListener(this);
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		return buttonPanel;
	}
	
	public boolean validateEntries() {
		String siteName = siteNameField.getText();
		String siteCode = siteIDField.getText();
		
		if (siteName == null || siteName.equals("")) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.nonemptysitename"));
			return false;
		}
		
		if (siteCode == null || siteCode.equals("")) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.nonemptysitecode"));
			return false;
		}
		
		ArrayList<Site> sites = ListModelUtility.convertListModelToISiteList(((DefaultListModel)siteList.getModel()));
		for (Site curSite: sites) {
			if (curSite.getSiteName().equals(siteName)) {
				if (edit) {
					if (curSite.getSiteName().equals(site.getSiteName())) {
						continue;
					}
				}
				JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.sitenameexists"));
				return false;
			}
			if (curSite.getSiteId().equals(siteCode)) {
				if (edit) {
					if (curSite.getSiteName().equals(site.getSiteName())) {
						continue;
					}
				}
				JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.siteidexists"));
				return false;
			}
		}
		return true;
	}

	
	public void actionPerformed(ActionEvent aet) {
		if (aet.getSource() == okButton) {
			if (validateEntries()) {
				if (edit) {
					site.setSiteName(siteNameField.getText());
					site.setSiteId(siteIDField.getText());
					site.setGeographicCode(geographicCodeField.getText());
					site.setConsultants(ListModelUtility.convertListModelToStringList((DefaultListModel)consultantList.getModel()));
				} else { 
					//do something here
					Site site = ElementUtility.createISite(siteNameField.getText(), 
														   siteIDField.getText(),
														   geographicCodeField.getText(),
														   ListModelUtility.convertListModelToStringList((DefaultListModel)consultantList.getModel()));
					((DefaultListModel)siteList.getModel()).addElement(site);	
				}
				this.dispose();
			}
		} else if (aet.getSource() == cancelButton) {
			this.dispose();
		} else if (aet.getSource() == addConsultantButton) {
			if (consultantField.getText() == null || consultantField.getText().equals("")) {
				JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.consultantfieldnonempty"));
			} else {
				((DefaultListModel)consultantList.getModel()).addElement(consultantField.getText());
				consultantField.setText("");
			}
		} else if (aet.getSource() == removeConsultantButton) {
			((DefaultListModel)consultantList.getModel()).removeElement(consultantList.getSelectedValue());
		}
	}
	
}
