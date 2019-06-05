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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import javax.swing.DefaultComboBoxModel;

import org.psygrid.collection.entry.FormView;

import org.psygrid.data.model.hibernate.AssociatedConsentForm;
import org.psygrid.data.model.hibernate.ConsentForm;
import org.psygrid.data.model.hibernate.PrimaryConsentForm;

import org.psygrid.datasetdesigner.custom.SizeConstrainedTextArea;
import org.psygrid.datasetdesigner.custom.TextFieldWithStatus;

import org.psygrid.datasetdesigner.renderer.OptionListCellRenderer;
import org.psygrid.datasetdesigner.utils.ElementUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class ConfigureConsentDialog extends JDialog implements ActionListener {

	private final static String STRING_PREFIX = "org.psygrid.datasetdesigner.ui.configurationdialogs.configureconsentdialog.";
	
	private JButton okButton;
	private JButton cancelButton;
	
	private JTextArea consentArea;
	
	private TextFieldWithStatus referenceField;
	
	private JList consentList;

	private boolean edit = false;
	
	private ConsentForm consentForm;
	
	private JRadioButton andRelationshipBox; 
	
	private JRadioButton orRelationshipBox;
	
	private JComboBox primaryComboBox;
	
	private JLabel linkLabel;
	
	public ConfigureConsentDialog(JDialog parentDialog,
								  JList consentList)	{
		super(parentDialog, PropertiesHelper.getStringFor(STRING_PREFIX + "configureconsent"));
		setModal(true);
		this.consentList = consentList;
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new JScrollPane(buildPanels()));
		setPreferredSize(new Dimension(650, 500));
		init();
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
	}
	
	public ConfigureConsentDialog(JDialog parentDialog,
								  JList consentList,
								  ConsentForm primaryConsentForm) {
		super(parentDialog, PropertiesHelper.getStringFor(STRING_PREFIX + "configureconsent"));
		setModal(true);
		this.edit = true;
		this.consentForm = primaryConsentForm;
		this.consentList = consentList;
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new JScrollPane(buildPanels()));
		setPreferredSize(new Dimension(650, 500));
		pack();
		init();
		setLocationRelativeTo(null);
		setVisible(true);
	}


	private void init() {
		if (edit) {
			ConsentForm icf = ((ConsentForm)consentList.getSelectedValue());
			consentArea.setText(icf.getQuestion());
			referenceField.setText(icf.getReferenceNumber());
			if (icf instanceof PrimaryConsentForm) {
				orRelationshipBox.setSelected(true);
			} else {
				andRelationshipBox.setSelected(true);
			}
			//in editing mode, this cannot be changed
			andRelationshipBox.setEnabled(false);
			orRelationshipBox.setEnabled(false);
			
			primaryComboBox.setEnabled(false);
			
		} else {
			orRelationshipBox.setSelected(true);
		}
		
		DefaultComboBoxModel comboModel = new DefaultComboBoxModel();
		for (int i=0; i<consentList.getModel().getSize(); i++) {
			Object curElement = consentList.getModel().getElementAt(i);
			if (curElement instanceof PrimaryConsentForm) {
				comboModel.addElement(curElement);
			}
		}
		
		primaryComboBox.setModel(comboModel);

		if (edit) {
			ConsentForm icf = ((ConsentForm)consentList.getSelectedValue());
			if (icf instanceof AssociatedConsentForm) {
				for (int i=0; i<consentList.getModel().getSize(); i++) {
					ConsentForm curForm = (ConsentForm)consentList.getModel().getElementAt(i);
					if (curForm instanceof PrimaryConsentForm) {
						if (((PrimaryConsentForm)curForm).getAssociatedConsentForms().contains(icf)) {
							primaryComboBox.setSelectedItem(curForm);
						}
					}
				}
			}
		}
	}
	
	private JPanel buildPanels() {
		JPanel allPanel = new JPanel();
		allPanel.setLayout(new BoxLayout(allPanel, BoxLayout.Y_AXIS));
		allPanel.add(buildMainPanel());
		allPanel.add(buildButtonPanel());
		return allPanel;
	}
	
	private JPanel buildMainPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createTitledBorder(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.primaryconsentform")));
		consentArea = new SizeConstrainedTextArea(1024);
		consentArea.setWrapStyleWord(true);
		JScrollPane pane = new JScrollPane(consentArea);
		pane.setPreferredSize(new Dimension(200, 100));
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor(STRING_PREFIX + "consentquestion")), BorderLayout.NORTH);
		mainPanel.add(pane, BorderLayout.CENTER);
		
		JPanel refPanel = new JPanel();
		refPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		refPanel.add(new JLabel(PropertiesHelper.getStringFor(STRING_PREFIX + "enterrefnumber")));
		referenceField = new TextFieldWithStatus(20, false);
		refPanel.add(referenceField);
	
		JPanel fullPanel = new JPanel();
		fullPanel.setLayout(new BorderLayout());
		fullPanel.add(mainPanel,BorderLayout.CENTER);
		fullPanel.add(refPanel, BorderLayout.SOUTH);
		
		JPanel andOrPanel = new JPanel();
        FormLayout layout = new FormLayout("10dlu, 100dlu, default:grow"); //$NON-NLS-1$
        DefaultFormBuilder builder = new DefaultFormBuilder(layout, 
                andOrPanel);
        builder.appendRow(FormView.getDefaultRowSpec());
		builder.append(new JLabel(PropertiesHelper.getStringFor(STRING_PREFIX + "typeofconsent")), builder.getColumnCount());
		builder.nextLine();
		
		ButtonGroup bGroup = new ButtonGroup();
		andRelationshipBox = new JRadioButton(PropertiesHelper.getStringFor(STRING_PREFIX + "andconsent"));
		orRelationshipBox = new JRadioButton(PropertiesHelper.getStringFor(STRING_PREFIX + "orconsent"));
		builder.append(orRelationshipBox, builder.getColumnCount());
		builder.nextLine();
		bGroup.add(andRelationshipBox);
		bGroup.add(orRelationshipBox);
		builder.append(andRelationshipBox, builder.getColumnCount());
		builder.nextLine();
		andRelationshipBox.addActionListener(this);
		orRelationshipBox.addActionListener(this);
		
		JPanel primaryComboPanel = new JPanel();
		primaryComboBox = new JComboBox();
		primaryComboBox.setRenderer(new OptionListCellRenderer());
		primaryComboPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		linkLabel = new JLabel(PropertiesHelper.getStringFor(STRING_PREFIX + "linkconsent"));
		primaryComboPanel.add(linkLabel);
		primaryComboPanel.add(primaryComboBox);
		
		JPanel holderPanel = new JPanel();
		holderPanel.setLayout(new BorderLayout());
		holderPanel.add(fullPanel, BorderLayout.CENTER);
		
		//only show this panel if there is already a consent existing
		if (consentList.getModel().getSize() > 0) {
			holderPanel.add(andOrPanel, BorderLayout.SOUTH);
		}
		
		JPanel holderWithComboPanel = new JPanel();
		holderWithComboPanel.setLayout(new BorderLayout());
		holderWithComboPanel.add(holderPanel, BorderLayout.CENTER);
		
		if (consentList.getModel().getSize() > 0) {
			holderWithComboPanel.add(primaryComboPanel, BorderLayout.SOUTH);
		}
		
		return holderWithComboPanel;
	}
	
	private JPanel buildButtonPanel(){
		okButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ok"));
		okButton.addActionListener(this);
		cancelButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.cancel"));
		cancelButton.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		return buttonPanel;
	}
	
	public boolean validateEntries() {
		return true;
	}
	
	public void actionPerformed(ActionEvent aet) {
		if (aet.getSource() == okButton) {
			if (edit) {
				consentForm.setQuestion(consentArea.getText());
				consentForm.setReferenceNumber(referenceField.getText());
			} else {
				if (consentList.getModel().getSize() > 0) {
					if (orRelationshipBox.isSelected()) {
						PrimaryConsentForm ipcf = ElementUtility.createIPrimaryConsent(consentArea.getText(), 
								referenceField.getText()); 
						((DefaultListModel)consentList.getModel()).addElement(ipcf);
					} else {
						AssociatedConsentForm acf = ElementUtility.createIAssociatedConsentForm(consentArea.getText(),
                                referenceField.getText());
						((DefaultListModel)consentList.getModel()).addElement(acf);
						PrimaryConsentForm ipcf = (PrimaryConsentForm)primaryComboBox.getSelectedItem();
						ipcf.addAssociatedConsentForm(acf);
					}
				//if nothing in the list, AND and OR boxes aren't visible so just create a primary consent form
				} else {
					PrimaryConsentForm ipcf = ElementUtility.createIPrimaryConsent(consentArea.getText(), 
							referenceField.getText()); 
					((DefaultListModel)consentList.getModel()).addElement(ipcf);
				}
			}
			this.dispose();
		} else if (aet.getSource() == cancelButton) {
			this.dispose();
		} else if (aet.getSource() == andRelationshipBox) {
			if (andRelationshipBox.isSelected()) {
				primaryComboBox.setEnabled(true);
				linkLabel.setEnabled(true);
			}
		} else if (aet.getSource() == orRelationshipBox) {
			if (orRelationshipBox.isSelected()) {
				primaryComboBox.setEnabled(false);
				linkLabel.setEnabled(false);
			}
		}
	}
	
}
