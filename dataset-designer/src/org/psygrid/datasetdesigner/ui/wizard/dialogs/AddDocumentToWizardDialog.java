/*
Copyright (c) 2006, The University of Manchester, UK.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301, USA.
*/
package org.psygrid.datasetdesigner.ui.wizard.dialogs;

import org.psygrid.data.model.hibernate.Document;
import org.psygrid.datasetdesigner.ui.wizard.WizardModel;

import org.psygrid.datasetdesigner.utils.ElementUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.SpringUtilities;

import org.psygrid.datasetdesigner.custom.TextFieldWithStatus;

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


/**
 * Called from the wizard to add a document to the study
 * Produces a simple dialog asking for the document name
 * @author pwhelan
 *
 */
public class AddDocumentToWizardDialog extends JDialog implements ActionListener {
    /**
     * The list of existing documents
     */
    private JList documentList;

    /**
     * The document to edit
     */
    private Document document;

    /**
     * The name of the document
     */
    private TextFieldWithStatus nameField;

    /**
     * Ok button; save document details
     */
    private JButton okButton;

    /**
     * Cancel button; dismiss the dialog
     */
    private JButton cancelButton;

    /**
     * Model for the wizard; contains dataset details
     */
    private WizardModel wm;

    /**
     * Creates a new AddDocumentToWizardDialog object.
     *
     * @param parentDialog
     * @param documentList
     * @param wm
     */
    public AddDocumentToWizardDialog(JDialog parentDialog, JList documentList,
        WizardModel wm) {
        super(parentDialog,
            PropertiesHelper.getStringFor(
                "org.psygrid.datasetdesigner.wizard.studyproc"));
        this.documentList = documentList;
        this.wm = wm;
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
     * Creates a new AddDocumentToWizardDialog object.
     *
     * @param parentDialog
     * @param documentList
     * @param document
     * @param wm
     */
    public AddDocumentToWizardDialog(JDialog parentDialog, JList documentList,
        Document document, WizardModel wm) {
        super(parentDialog,
            PropertiesHelper.getStringFor(
                "org.psygrid.datasetdesigner.wizard.studyproc"));
        this.documentList = documentList;
        this.wm = wm;
        this.document = document;
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
     *
     */
    private void init() {
        if (document != null) {
            nameField.setText(document.getName());
        }
    }

    /**
     *
     *
     * @return
     */
    private JPanel buildMainPanel() {
        JPanel mainPanel = new JPanel(new SpringLayout());

        nameField = new TextFieldWithStatus(20, true);
        mainPanel.add(new JLabel(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.wizard.studyprocname")));
        mainPanel.add(nameField);

        SpringUtilities.makeCompactGrid(mainPanel, 1, 2, //rows, cols
            6, 6, //initX, initY
            6, 6); //xPad, yPad

        return mainPanel;
    }

    /**
     *
     *
     * @return
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
     *
     *
     * @return
     */
    public boolean validateEntry() {
        boolean result = true;

        if (nameField.getText().equals("")) {
            JOptionPane.showMessageDialog(this,
                PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.wizard.docnameempty"));
        }

        for (int i = 0; i < documentList.getModel().getSize(); i++) {
            Document curDoc = (Document) documentList.getModel()
                                                       .getElementAt(i);

            if (!curDoc.equals(document)) {
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
     *
     *
     * @param aet
     */
    public void actionPerformed(ActionEvent aet) {
        if (aet.getSource() == okButton) {
            if (validateEntry()) {
                if (document != null) {
                    document.setName(nameField.getText());
                    document.setDisplayText(nameField.getText());
                } else {
                    Document doc = ElementUtility
                                                .createIDocumentWizard(nameField.getText(),
                                                        wm.getWizardDs());
                    ((DefaultListModel) documentList.getModel()).addElement(doc);
                }

                this.dispose();
            }
        } else if (aet.getSource() == cancelButton) {
            this.dispose();
        }
    }
}
