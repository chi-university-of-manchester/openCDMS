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

package org.psygrid.datasetdesigner.ui.wizard.dialogs;


import org.psygrid.data.model.hibernate.DocumentGroup;
import org.psygrid.datasetdesigner.utils.ElementUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.SpringUtilities;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.psygrid.datasetdesigner.custom.TextFieldWithStatus;

/**
 * Called from the study wizard - produces a simple dialog
 * asking for the document group name
 *
 * @author pwhelan
 */
public class AddDocumentGroupToWizardDialog extends JDialog
    implements ActionListener {
    /**
     * The list of configured documents
     */
    private JList documentGroupList;

    /**
     * The document group to edit
     */
    private DocumentGroup documentGroup;

    /**
     * The name of the document group
     */
    private TextFieldWithStatus nameField;

    /**
     * Ok Button; commit chanes
     */
    private JButton okButton;

    /**
     * Cancel button; dismiss changes
     */
    private JButton cancelButton;

    /**
     * Creates a new AddDocumentGroupToWizardDialog object.
     *
     * @param parentDialog
     * @param documentGroupList
     */
    public AddDocumentGroupToWizardDialog(JDialog parentDialog,
        JList documentGroupList) {
        super(parentDialog,
            PropertiesHelper.getStringFor(
                "org.psygrid.datasetdesigner.ui.adddocumentgroup"));
        this.documentGroupList = documentGroupList;
        setModal(true);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
        getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
        init();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Creates a new AddDocumentGroupToWizardDialog object.
     *
     * @param parentDialog
     * @param documentGroupList
     * @param documentGroup
     */
    public AddDocumentGroupToWizardDialog(JDialog parentDialog,
        JList documentGroupList, DocumentGroup documentGroup) {
        super(parentDialog,
            PropertiesHelper.getStringFor(
                "org.psygrid.datasetdesigner.ui.adddocumentgroup"));
        this.documentGroupList = documentGroupList;
        this.documentGroup = documentGroup;
        setModal(true);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
        getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
        init();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * If the document group exists (i.e. in edit mode), set it here
     */
    private void init() {
        if (documentGroup != null) {
            nameField.setText(documentGroup.getName());
        }
    }

    /**
     * Lay out the main panel
     *
     * @return
     */
    private JPanel buildMainPanel() {
        JPanel mainPanel = new JPanel(new SpringLayout());

        nameField = new TextFieldWithStatus(20, true);
        mainPanel.add(new JLabel(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.documentgroupname")));
        mainPanel.add(nameField);

        SpringUtilities.makeCompactGrid(mainPanel, 1, 2, //rows, cols
            6, 6, //initX, initY
            6, 6); //xPad, yPad

        return mainPanel;
    }

    /**
     * Build the ok and cancel button panel
     *
     * @return the configured button panel
     */
    private JPanel buildButtonPanel() {
        okButton = new JButton(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ok"));
        okButton.addActionListener(this);
        cancelButton = new JButton(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.cancel"));
        cancelButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    /**
     * Validate the entry; ensure the name isn't null and is unique
     *
     * @return true if valiation is successful; false if not
     */
    public boolean validateEntry() {
        boolean result = true;

        if (nameField.getText().equals("")) {
            JOptionPane.showMessageDialog(this,
                PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.wizard.docnameempty"));
        }

        for (int i = 0; i < documentGroupList.getModel().getSize(); i++) {
            DocumentGroup curDoc = (DocumentGroup) documentGroupList.getModel()
                                                                      .getElementAt(i);

            if (!curDoc.equals(documentGroup)) {
                if (curDoc.getName().equals(nameField.getText())) {
                    JOptionPane.showMessageDialog(this,
                        PropertiesHelper.getStringFor(
                            "org.psygrid.datasetdesigner.ui.wizard.docnamealreadyexists"));

                    return false;
                }
            }
        }

        return result;
    }

    /**
     * if ok, commit changes; if cancel, dismiss
     *
     * @param aet the calling action event
     */
    public void actionPerformed(ActionEvent aet) {
        if (aet.getSource() == okButton) {
            if (documentGroup != null) {
                documentGroup.setName(nameField.getText());
                documentGroup.setDisplayText(nameField.getText());
            } else {
                DocumentGroup docGroup = ElementUtility.createIDocumentGroupWizard(nameField.getText());
                ((DefaultListModel) documentGroupList.getModel()).addElement(docGroup);
            }

            this.dispose();
        } else if (aet.getSource() == cancelButton) {
            this.dispose();
        }
    }
}
