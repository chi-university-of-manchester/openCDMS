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

import org.psygrid.data.model.hibernate.Group;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.utils.IconsHelper;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.SpringUtilities;
import org.psygrid.datasetdesigner.utils.Utils;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.psygrid.data.model.hibernate.DataSet;

/**
 * The validation dialog displays the basic validation results of a study
 * Checks are conducted against minimum group configuration,
 * doc occurrences configured etc.
 * @author pwhelan
 */
public class ShowValidationDialog extends JDialog implements ActionListener {
    /**
     * Ok button; dismiss dialog
     */
    private JButton okButton;

    /**
     * Constructor: lay out and initialise
     * @param frame the main window of the application
     */
    public ShowValidationDialog(MainFrame frame) {
        super(frame);
        setTitle(PropertiesHelper.getStringFor(
                "org.psygrid.datasetdesigner.ui.validatedataset"));
        setModal(true);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(Utils.buildDsHeaderPanel(), BorderLayout.NORTH);
        getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
        getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Build the main panel; showing validation results
     * Item to validate with a tick or x beside it
     * @return the configured main panel
     */
    private JPanel buildMainPanel() {
        JPanel mainPanel = new JPanel();
        StudyDataSet activeDs = DatasetController.getInstance().getActiveDs();

        int rows = 7;

        mainPanel.setLayout(new SpringLayout());

        mainPanel.add(new JLabel(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.groupsconfigured")));

        if (activeDs.getGroups().size() > 0) {
            mainPanel.add(new JLabel(IconsHelper.getInstance()
                                                .getImageIcon("check.png")));
        } else {
            mainPanel.add(new JLabel(IconsHelper.getInstance()
                                                .getImageIcon("cross.png")));
        }

        mainPanel.add(new JLabel(PropertiesHelper.getStringFor(
        "org.psygrid.datasetdesigner.ui.sitesconfigured")));

        if (activeDs.getGroups().size()== 0) {
            mainPanel.add(new JLabel(IconsHelper.getInstance()
                                                .getImageIcon("cross.png")));
        } else {
        	boolean allSites = true;
        	for (Group group: ((DataSet)activeDs.getDs()).getGroups()) {
        		if (group.numSites() == 0) {
        			allSites = false;
        		}
        	}
        	if (allSites) {
                mainPanel.add(new JLabel(IconsHelper.getInstance()
                        .getImageIcon("check.png")));
        	} else {
                mainPanel.add(new JLabel(IconsHelper.getInstance()
                        .getImageIcon("cross.png")));
        	}
        }
        
        mainPanel.add(new JLabel(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.rolesconfigured")));

        if (activeDs.getRoles().size() > 0) {
            mainPanel.add(new JLabel(IconsHelper.getInstance()
                                                .getImageIcon("check.png")));
        } else {
            mainPanel.add(new JLabel(IconsHelper.getInstance()
                                                .getImageIcon("cross.png")));
        }

        mainPanel.add(new JLabel(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.consentconfigured")));

        if ((activeDs.getDs()).numAllConsentFormGroups() > 0) {
            mainPanel.add(new JLabel(IconsHelper.getInstance()
                                                .getImageIcon("check.png")));
        } else {
            mainPanel.add(new JLabel(IconsHelper.getInstance()
                                                .getImageIcon("cross.png")));
        }

        mainPanel.add(new JLabel(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.statusesconfigured")));

        if ((activeDs.getDs()).numStatus() > 0) {
            mainPanel.add(new JLabel(IconsHelper.getInstance()
                                                .getImageIcon("check.png")));
        } else {
            mainPanel.add(new JLabel(IconsHelper.getInstance()
                                                .getImageIcon("cross.png")));
        }

        mainPanel.add(new JLabel(PropertiesHelper.getStringFor(
        "org.psygrid.datasetdesigner.ui.studystagesconfigured")));

		if (activeDs.isStudyStagesValidated()) {
            mainPanel.add(new JLabel(IconsHelper.getInstance()
                    .getImageIcon("check.png")));
		} else {
            mainPanel.add(new JLabel(IconsHelper.getInstance()
                    .getImageIcon("cross.png")));
		}

        mainPanel.add(new JLabel(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.documentoccurrencesconfigured")));

        boolean noOccs = false;

        if (activeDs.getDs().numDocuments() == 0) {
            noOccs = true;
        }

        for (int z = 0; z < activeDs.getDs().numDocuments(); z++) {
            if (activeDs.getDs().getDocument(z).numOccurrences() == 0) {
                noOccs = true;
            } else {
                for (int m = 0; m < activeDs.getDs().numDocuments(); m++) {
                    //remove the preview occurrences
                    int realNumDocOccs = activeDs.getDs().getDocument(z)
                                                 .numOccurrences();

                    for (int y = 0;
                            y < activeDs.getDs().getDocument(z).numOccurrences();
                            y++) {
                        if (activeDs.getDs().getDocument(z).getOccurrence(y)
                                        .getName().startsWith("Preview")) {
                            realNumDocOccs--;
                        }
                    }

                    if (realNumDocOccs == 0) {
                        noOccs = true;
                    }
                }
            }
        }

        if (noOccs) {
            mainPanel.add(new JLabel(IconsHelper.getInstance()
                                                .getImageIcon("cross.png")));
        } else {
            mainPanel.add(new JLabel(IconsHelper.getInstance()
                                                .getImageIcon("check.png")));
        }
        
        if (activeDs.getDs().isRandomizationRequired()) {
            mainPanel.add(new JLabel(PropertiesHelper.getStringFor(
                        "org.psygrid.datasetdesigner.ui.randomisationparamset")));

            if (activeDs.getRandomHolderModel() == null) {
                mainPanel.add(new JLabel(IconsHelper.getInstance()
                                                    .getImageIcon("cross.png")));
            } else if (activeDs.getRandomHolderModel().validate()) {
                mainPanel.add(new JLabel(IconsHelper.getInstance()
                                                    .getImageIcon("check.png")));
            } else {
                mainPanel.add(new JLabel(IconsHelper.getInstance()
                                                    .getImageIcon("cross.png")));
            }

            mainPanel.add(new JLabel(PropertiesHelper.getStringFor(
                        "org.psygrid.datasetdesigner.ui.randomisationtriggerset")));

            if (activeDs.isRandomizationTriggerSet()) {
                mainPanel.add(new JLabel(IconsHelper.getInstance()
                                                    .getImageIcon("check.png")));
            } else {
                mainPanel.add(new JLabel(IconsHelper.getInstance()
                                                    .getImageIcon("cross.png")));
            }

            rows+=2;
        }

        SpringUtilities.makeCompactGrid(mainPanel, rows, 2, //rows, cols
            6, 6, //initX, initY
            6, 6); //xPad, yPad

        return mainPanel;
    }

    /**
     * Build the ok button panel
     * @return the configured button panel
     */
    private JPanel buildButtonPanel() {
        okButton = new JButton(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ok"));
        okButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(okButton);

        return buttonPanel;
    }

    /**
     * Handle ok; dismiss dialog
     * @param aet The trigger event
     */
    public void actionPerformed(ActionEvent aet) {
        if (aet.getSource() == okButton) {
            this.dispose();
        }
    }
}
