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

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.data.model.hibernate.EslCustomField;
import org.psygrid.datasetdesigner.actions.AddTreatmentAction;
import org.psygrid.datasetdesigner.actions.RemoveFromListAction;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.custom.CustomCopyPasteJList;
import org.psygrid.datasetdesigner.custom.TextFieldWithStatus;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.model.ESLEmailModel;
import org.psygrid.datasetdesigner.model.RandomisationHolderModel;
import org.psygrid.datasetdesigner.renderer.EntryTableCellRenderer;
import org.psygrid.datasetdesigner.renderer.OptionListCellRenderer;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.utils.ListModelUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.Utils;

import org.psygrid.esl.model.IRole;

import org.psygrid.randomization.model.hibernate.Stratum;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;


/**
 * Dialog to display randomisation settings
 * for the dataset; allows configuration
 * of strata, treatments, minimum and maximum block sizes,
 * the randomisation trigger document occurrence
 * and email notifications.
 *
 * @author pwhelan
 */
public class ConfigureRandomiserDialog extends JDialog implements ActionListener,
    FocusListener {
    /**
     * Preferred width of the dialog
     */
    private final static int PREF_WIDTH = 545;

    /**
     * Ok button; save changes and dismiss
     */
    private JButton okButton;

    /**
     * Cancel button; dismiss
     */
    private JButton cancelButton;

    /**
     * The list of treatments provided to the randomiser
     */
    private CustomCopyPasteJList treatmentList;

    /**
     * Add a treatment
     */
    private JButton addTreatmentButton;

    /**
     * Edit a treatment
     */
    private JButton editTreatmentButton;

    /**
     * Remove a treatment 
     */
    private JButton removeTreatmentButton;

    /**
     * The table containing esl email details 
     */
    private JTable eslTable;

    /**
     * The minimum block size of the block randomizer
     */
    private TextFieldWithStatus minimumBlockSizeField;

    /**
     * The maximum block size of the block randomizer
     */
    private TextFieldWithStatus maximumBlockSizeField;

    /**
     * Stratify by sex checkbox
     */
    private JCheckBox stratifyBySexBox;

    /**
     * Stratify by center checkbox
     */
    private JCheckBox stratifyByCentre;

    private Map<EslCustomField, JCheckBox> stratifyByCustomFields = new HashMap<EslCustomField, JCheckBox>();
    
    /**
     * List of document occurrences that can be set to trigger randomization
     */
    private JComboBox randomBox;

    /**
     * Model containing all doc occurrences in the dataset
     * from which a randomization trigger can be chosen
     */
    private DefaultComboBoxModel randomListModel;

    /**
     * The currently active DSDataSet
     */
    private StudyDataSet activeDs;

    /**
     * Local copy of random model; to enable cancel to work properly
     */
    private RandomisationHolderModel randomModel;

    /**
     * Local copy of esl email model; to enable cancel to work properly
     */
    private ESLEmailModel eslModel;

    /**
     * Local copy of esl email model; to enable cancel to work properly
     */
    private boolean readOnly;
    
    /**
     * Constructor
     * Default to editable mode
     * @param frame the main application frame
     */
    public ConfigureRandomiserDialog(MainFrame frame) {
    	this(frame, false);
    }


    /**
     * Constructor
     *
     * @param frame the main application frame
     * @param viewOnly indicates read-only mode (true if read-only; false if not)
     */
    public ConfigureRandomiserDialog(MainFrame frame, boolean readOnly) {
        super(frame);
        
        //if the active study is published, no changes can be made
        if (DatasetController.getInstance().getActiveDs() != null
        		&& DatasetController.getInstance().getActiveDs().getDs().isPublished()) {
        	readOnly = true;
        }
        
        if (readOnly ){
            setTitle(PropertiesHelper.getStringFor(
            "org.psygrid.datasetdesigner.ui.viewrandomiser"));
        } else {
            setTitle(PropertiesHelper.getStringFor(
            "org.psygrid.datasetdesigner.ui.configurerandomiser"));
        }
        this.readOnly = readOnly;
        activeDs = DatasetController.getInstance().getActiveDs();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(Utils.buildDsHeaderPanel(), BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(buildMainPanel()), BorderLayout.CENTER);
        getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        init();
    }

    
    /**
     * Build the panel containing the ESL email
     * notification settings
     * @return the configured email ESL randomisation settings
     */
    private JPanel buildEmailPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createTitledBorder(
                PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.emailsettings")));
        mainPanel.setLayout(new BorderLayout());
        eslTable = new JTable(new ESLTableModel());

        JScrollPane scrollPane = new JScrollPane(eslTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.setPreferredSize(new Dimension(PREF_WIDTH + 130, 200));
        mainPanel.setMinimumSize(new Dimension(PREF_WIDTH + 130, 200));
        mainPanel.setMaximumSize(new Dimension(PREF_WIDTH + 130, 200));

        return mainPanel;
    }

    /**
     * Initialise; configure the relevant settings for the selected
     * dataset; 
     * Populate the local random models and esl models
     */
    private void init() {
        if (activeDs != null) {
            randomModel = activeDs.getRandomHolderModel();

            if (randomModel != null) {
                treatmentList.setModel(ListModelUtility.convertArrayListToListModel(
                        randomModel.getRandomisationTreatments()));

                if (randomModel.getMinimumBlockSize() != -1) {
                    minimumBlockSizeField.setText(new Integer(
                            randomModel.getMinimumBlockSize()).toString());
                }

                if (randomModel.getMaximumBlockSize() != -1) {
                    maximumBlockSizeField.setText(new Integer(
                            randomModel.getMaximumBlockSize()).toString());
                }

                ArrayList<Stratum> strata = randomModel.getRandomisationStrata();

                //initially set disabled
                stratifyBySexBox.setSelected(false);
                stratifyByCentre.setSelected(false);
            	for (Map.Entry<EslCustomField, JCheckBox> entry: stratifyByCustomFields.entrySet() ){
            		entry.getValue().setSelected(false);
            	}

                for (int i = 0; i < strata.size(); i++) {
                	Stratum s = strata.get(i);
                    if (s.getName().equals(PropertiesHelper.getStringFor(
                                    "org.psygrid.datasetdesigner.randomiser.sex"))) {
                        stratifyBySexBox.setSelected(true);
                    } else if (s.getName().equals(PropertiesHelper.getStringFor(
                                    "org.psygrid.datasetdesigner.randomiser.centre"))) {
                        stratifyByCentre.setSelected(true);
                    }
                	for (Map.Entry<EslCustomField, JCheckBox> entry: stratifyByCustomFields.entrySet() ){
                		if ( s.getName().equals(entry.getKey().getName()) ){
                			entry.getValue().setSelected(true);
                		}
                	}

                }
            } else {
                randomModel = new RandomisationHolderModel();
            }
            
            DataSet dsSet = activeDs.getDs();
            randomListModel.addElement(" ");

            for (int i = 0; i < dsSet.numDocuments(); i++) {
                Document doc = dsSet.getDocument(i);

                for (int j = 0; j < doc.numOccurrences(); j++) {
                    DocumentOccurrence docOcc = doc.getOccurrence(j);

                    if (!docOcc.getName().startsWith("Preview")) {
                        randomListModel.addElement(docOcc);
                    }

                    if (docOcc.isRandomizationTrigger()) {
                        randomBox.setSelectedItem(docOcc);
                    }
                }
            }
            initESLModel();
        }
        
        if (readOnly) {
        	randomBox.setEnabled(false);
        	stratifyByCentre.setEnabled(false);
        	stratifyBySexBox.setEnabled(false);
        	for (Map.Entry<EslCustomField, JCheckBox> entry: stratifyByCustomFields.entrySet() ){
        		entry.getValue().setEnabled(false);
        	}
        	addTreatmentButton.setEnabled(false);
        	editTreatmentButton.setEnabled(false);
        	removeTreatmentButton.setEnabled(false);
        	minimumBlockSizeField.setEditable(false);
        	maximumBlockSizeField.setEditable(false);
        }
        
    }

    /**
     * Initialise the table containing the ESL
     * settings for randomisation email notifications
     */
    private void initESLModel() {
        if (activeDs != null) {
            eslModel = activeDs.getEslModel();

            ESLTableModel tableModel = new ESLTableModel();

            if (eslModel != null) {
                for (int i = 0; i < eslModel.getRoles().size(); i++) {
                    IRole role = eslModel.getRoles().get(i);
                    Vector<Object> rowVector = new Vector<Object>();
                    rowVector.add(role);
                    rowVector.add(role.isNotifyOfRSInvocation());
                    rowVector.add(role.isNotifyOfRSDecision());
                    rowVector.add(role.isNotifyOfRSTreatment());
                    tableModel.addRow(rowVector);
                }

                eslTable.setModel(tableModel);
            } else {
                eslModel = new ESLEmailModel();
            }

            TableColumn zeroColumn = eslTable.getColumnModel().getColumn(0);
            zeroColumn.setCellRenderer(new EntryTableCellRenderer());
            eslTable.getColumnModel().getColumn(0)
                    .setHeaderValue(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.rolename"));
            eslTable.getColumnModel().getColumn(1)
                    .setHeaderValue(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.nofifyrsinvocation"));
            eslTable.getColumnModel().getColumn(2)
                    .setHeaderValue(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.notifyrsdecision"));
            eslTable.getColumnModel().getColumn(3)
                    .setHeaderValue(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.notifyrstreatment"));

            eslTable.getModel()
                    .addTableModelListener(new EmailTableChangedListener());
        }
    }

    /**
     * Build the Main Panel;
     * Lay out hte various components (stratum settings, treatment etc)
     * @return the configured main panel of the window
     */
    private JPanel buildMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(buildOccurrencePanel());
        mainPanel.add(buildStratumPanel());
        mainPanel.add(buildTreatmentPanel());
        mainPanel.add(buildBlockSizePanel());
        mainPanel.add(buildEmailPanel());

        return mainPanel;
    }

    /**
     * Build the panel containing the randomisation trigger
     * for the dataset
     * @return the configured panel containing the combobox
     */
    private JPanel buildOccurrencePanel() {
        JPanel occurrencePanel = new JPanel();
        occurrencePanel.setBorder(BorderFactory.createTitledBorder(
                PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.randomisationtrigger")));
        occurrencePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel randomTrigger = new JLabel(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.selectrandomisationtrigger"));
        randomBox = new JComboBox();
        randomBox.setRenderer(new OptionListCellRenderer());
        randomListModel = new DefaultComboBoxModel();
        randomBox.setModel(randomListModel);
        occurrencePanel.add(randomTrigger);
        occurrencePanel.add(randomBox);

        return occurrencePanel;
    }

    /**
     * Build the panel containing the strata settings
     * for the panel
     * @return the configured panel
     */
    private JPanel buildStratumPanel() {
        JPanel stratumPanel = new JPanel();
        stratumPanel.setBorder(BorderFactory.createTitledBorder(
                PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.configurestrata")));
        stratumPanel.setLayout(new BoxLayout(stratumPanel, BoxLayout.X_AXIS));

        JPanel strata1Panel = new JPanel(new BorderLayout());
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        headerPanel.add(new JLabel(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.stratifybysex")));
        stratifyBySexBox = new JCheckBox();
        stratifyBySexBox.addActionListener(this);
        headerPanel.add(stratifyBySexBox);

        strata1Panel.add(headerPanel, BorderLayout.NORTH);
        stratumPanel.add(strata1Panel, BorderLayout.CENTER);

        JPanel strata2Panel = new JPanel(new BorderLayout());
        JPanel header2Panel = new JPanel();
        header2Panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        header2Panel.add(new JLabel(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.stratifybycentre")));
        stratifyByCentre = new JCheckBox();
        stratifyByCentre.addActionListener(this);
        header2Panel.add(stratifyByCentre);
        strata2Panel.add(header2Panel, BorderLayout.NORTH);
        stratumPanel.add(strata2Panel);

        //Add ESl custom fields as stratification options
        for ( EslCustomField field: activeDs.getEslCustomFields() ){
            JPanel strataCPanel = new JPanel(new BorderLayout());
            JPanel headerCPanel = new JPanel();
            headerCPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            headerCPanel.add(new JLabel(PropertiesHelper.getStringFor(
            	"org.psygrid.datasetdesigner.ui.stratifyby")+field.getName()));
            JCheckBox cb = new JCheckBox();
            stratifyByCustomFields.put(field, cb);
            cb.addActionListener(this);
            headerCPanel.add(cb);
            strataCPanel.add(headerCPanel, BorderLayout.NORTH);
            stratumPanel.add(strataCPanel);

        }
        
        return stratumPanel;
    }

    /**
     * Build the treatment panel containing a list
     * of existing treatments and buttons to add, edit and remove
     * @return the configured treatment panel
     */
    private JPanel buildTreatmentPanel() {
        JPanel treatmentPanel = new JPanel();
        treatmentPanel.setLayout(new BorderLayout());
        treatmentPanel.setBorder(BorderFactory.createTitledBorder(
                PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.configuretreatment")));

        treatmentList = new CustomCopyPasteJList();

        DefaultListModel treatmentListModel = new DefaultListModel();
        treatmentList.setModel(treatmentListModel);
        treatmentList.setCellRenderer(new OptionListCellRenderer());

        JPanel treatmentButtonPanel = new JPanel();
        treatmentButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        addTreatmentButton = new JButton(new AddTreatmentAction(this,
                    treatmentList));
        editTreatmentButton = new JButton(new AddTreatmentAction(this,
                    treatmentList, true));
        editTreatmentButton.setEnabled(false);
        removeTreatmentButton = new JButton(new RemoveFromListAction(
                    treatmentList));
        removeTreatmentButton.setEnabled(false);

        treatmentButtonPanel.add(addTreatmentButton);
        treatmentButtonPanel.add(editTreatmentButton);
        treatmentButtonPanel.add(removeTreatmentButton);

        //Only enable the buttons if a treatment is selected
        treatmentList.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent event) {
                	boolean enabled = true;

                	if (readOnly){
                		enabled = false;
                	}
                	
                    if ((treatmentList == null) ||
                            (treatmentList.getSelectedValue() == null)) {
                        enabled = false;
                    }

                    if (removeTreatmentButton != null) {
                        removeTreatmentButton.setEnabled(enabled);
                    }

                    if (editTreatmentButton != null) {
                        editTreatmentButton.setEnabled(enabled);
                    }
                }
            });

        treatmentPanel.add(treatmentButtonPanel, BorderLayout.NORTH);
        treatmentPanel.add(new JScrollPane(treatmentList), BorderLayout.CENTER);

        return treatmentPanel;
    }

    /**
     * Build the panel containing options to
     * configure minimum and maximum block sizes
     * @return the configured panel containing block sizes
     */
    private JPanel buildBlockSizePanel() {
        JPanel blockSizePanel = new JPanel();
        blockSizePanel.setLayout(new FlowLayout());
        minimumBlockSizeField = new TextFieldWithStatus(10, true);
        minimumBlockSizeField.addFocusListener(this);
        maximumBlockSizeField = new TextFieldWithStatus(10, true);
        maximumBlockSizeField.addFocusListener(this);
        blockSizePanel.setBorder(BorderFactory.createTitledBorder(
                "Block Size Setting"));
        blockSizePanel.add(new JLabel(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.minblocksize")));
        blockSizePanel.add(minimumBlockSizeField);
        blockSizePanel.add(new JLabel(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.maxblocksize")));
        blockSizePanel.add(maximumBlockSizeField);

        return blockSizePanel;
    }

    /**
     * Build the ok and cancel buttons
     * in a panel
     * @return the configured panel containing ok and cancel buttons
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
        
        if (!readOnly) {
            buttonPanel.add(cancelButton);
        }

        return buttonPanel;
    }

    /**
     * Validate the existing entries
     * Ensure that strata are set,
     * at least two treatments,
     * min and max block size are sensible values
     *
     * @return false if validation fails; true if it succeeds
     */
    private boolean validatePanel() {

        if (!(randomBox.getSelectedItem() instanceof DocumentOccurrence)) {
            JOptionPane.showMessageDialog(this,
                PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.mustselecttrigger"));

            return false;
        }

        int stratumCounter = 0;
        if ( stratifyBySexBox.isSelected() ){
        	stratumCounter++;
        }
        if ( stratifyByCentre.isSelected() ){
        	stratumCounter++;
        }
        for ( Map.Entry<EslCustomField, JCheckBox> entry: stratifyByCustomFields.entrySet() ){
        	if ( entry.getValue().isSelected() ){
        		stratumCounter++;
        	}
        }
        
        /*
        if (0 == stratumCounter) {
            JOptionPane.showMessageDialog(this,
                PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.atleastonestratum"));

            return false;
        }
        */

        //two treatments at least
        if (treatmentList.getModel().getSize() < 2) {
            JOptionPane.showMessageDialog(this,
                PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.atleasttwotreatmentarms"));

            return false;
        }

        //minimum block valid integer and multiple of number of treatment arms
        try {
            Integer minBlockSize = new Integer(minimumBlockSizeField.getText());

            if ((minBlockSize % treatmentList.getModel().getSize()) != 0) {
                JOptionPane.showMessageDialog(this,
                    PropertiesHelper.getStringFor(
                        "org.psygrid.datasetdesigner.ui.minblockmultiplestrata"));

                return false;
            }
        } catch (NumberFormatException nex) {
            JOptionPane.showMessageDialog(this,
                PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.minblockinteger"));

            return false;
        } catch (ArithmeticException aex) {
            JOptionPane.showMessageDialog(this,
            		PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.atleastonestratum"));

            return false;
        }

        //maximum block valid integer and multiple of number of treatment arms
        //and not less than minimum block
        try {
            Integer maxBlockSize = new Integer(maximumBlockSizeField.getText());

            if ((maxBlockSize % treatmentList.getModel().getSize()) != 0) {
                JOptionPane.showMessageDialog(this,
                    PropertiesHelper.getStringFor(
                        "org.psygrid.datasetdesigner.ui.maxblockmultiplestrata"));

                return false;
            }
            
            Integer minBlockSize = new Integer(minimumBlockSizeField.getText());
            if ( maxBlockSize.intValue() < minBlockSize.intValue() ){
                JOptionPane.showMessageDialog(this,
                        PropertiesHelper.getStringFor(
                            "org.psygrid.datasetdesigner.ui.maxblocklessthanmin"));

                    return false;
            }
            
        } catch (NumberFormatException nex) {
            JOptionPane.showMessageDialog(this,
                PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.maxblockinteger"));

            return false;
        } catch (ArithmeticException aex) {
            JOptionPane.showMessageDialog(this,
                PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.atleastonestratum"));

            return false;
        }

        return true;
    }

    /**
     * Handle an action event
     * @param aet the trigger action event
     */
    public void actionPerformed(ActionEvent aet) {
        if (aet.getSource() == okButton) {
        	if (!readOnly) {
                if (validatePanel()) {
                    if (activeDs != null) {
                        randomModel.setMaximumBlockSize(new Integer(
                                maximumBlockSizeField.getText()).intValue());
                        randomModel.setMinimumBlockSize(new Integer(
                                minimumBlockSizeField.getText()).intValue());
                        
                       randomModel.setRandomisationTreatments(ListModelUtility.convertListModelToTreatmentHolderModelList((DefaultListModel)treatmentList.getModel()));
                       activeDs.setRandomHolderModel(randomModel);
                        
                       Object selectedDocOcc = randomListModel.getSelectedItem();

                        //set the new doc occ to be the randomisation trigger
                        if (!(selectedDocOcc.equals(" "))) {
                            ((DocumentOccurrence) selectedDocOcc).setRandomizationTrigger(true);
                        }

                        //remove it from all the others
                        for (int j = 0; j < activeDs.getDs().numDocuments(); j++) {
                            Document doc = activeDs.getDs().getDocument(j);

                            for (int z = 0; z < doc.numOccurrences(); z++) {
                                DocumentOccurrence docOcc = doc.getOccurrence(z);

                                if (!docOcc.equals(selectedDocOcc)) {
                                    docOcc.setRandomizationTrigger(false);
                                }
                            }
                        }

                        //save the local copies to the current dataset
                        activeDs.setRandomHolderModel(randomModel);
                        activeDs.setEslModel(eslModel);

                        this.dispose();
                    }
        	}
            } else {
            	this.dispose();
            }
        } else if (aet.getSource() == cancelButton) {
            this.dispose();
        } else if (aet.getSource() == stratifyBySexBox) {
            if (stratifyBySexBox.isSelected()) {
                Stratum sexStratum = new Stratum();
                sexStratum.setName(PropertiesHelper.getStringFor(
                        "org.psygrid.datasetdesigner.randomiser.sex"));
                randomModel.getRandomisationStrata().add(sexStratum);
            } else {
                for (int i = 0;
                        i < randomModel.getRandomisationStrata().size(); i++) {
                    if (randomModel.getRandomisationStrata().get(i).getName()
                                       .equals(PropertiesHelper.getStringFor(
                                    "org.psygrid.datasetdesigner.randomiser.sex"))) {
                        randomModel.getRandomisationStrata()
                                   .remove(randomModel.getRandomisationStrata()
                                                      .get(i));
                    }
                }
            }
        } else if (aet.getSource() == stratifyByCentre) {
            Stratum centreStratum = new Stratum();
            centreStratum.setName(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.randomiser.centre"));

            if (stratifyByCentre.isSelected()) {
                randomModel.getRandomisationStrata().add(centreStratum);
            } else {
                for (int i = 0;
                        i < randomModel.getRandomisationStrata().size(); i++) {
                    if (randomModel.getRandomisationStrata().get(i).getName()
                                       .equals(PropertiesHelper.getStringFor(
                                    "org.psygrid.datasetdesigner.randomiser.centre"))) {
                        randomModel.getRandomisationStrata()
                                   .remove(randomModel.getRandomisationStrata()
                                                      .get(i));
                    }
                }
            }
        }
        else{
        	for ( EslCustomField field: activeDs.getEslCustomFields() ){
        		JCheckBox check = stratifyByCustomFields.get(field);
        		if ( aet.getSource() == check ){
                    if (check.isSelected()) {
                        Stratum s = new Stratum();
                        s.setName(field.getName());
                        randomModel.getRandomisationStrata().add(s);
                    } else {
                        for (int i = 0;
                                i < randomModel.getRandomisationStrata().size(); i++) {
                            if (randomModel.getRandomisationStrata().get(i).getName()
                                               .equals(field.getName())) {
                                randomModel.getRandomisationStrata()
                                           .remove(randomModel.getRandomisationStrata()
                                                              .get(i));
                            }
                        }
                    }
        		}
        	}
        }
    }

    /**
     * Implemented because focusLost is needed.  Not overridden here.
     * @param e the <code>FocusEvent</code> trigger
     */
    public void focusGained(FocusEvent e) {
    }

    /**
     * On a focus lost, ensure that minimum and maximum block size
     * values are sensible
     * @param FocusEvent the trigger event
     */
    public void focusLost(FocusEvent e) {
        if (e.getSource() == minimumBlockSizeField) {
            try {
                int minBlockSize = new Integer(minimumBlockSizeField.getText()).intValue();
                randomModel.setMinimumBlockSize(minBlockSize);
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        }

        if (e.getSource() == maximumBlockSizeField) {
            try {
                int maxBlockSize = new Integer(maximumBlockSizeField.getText()).intValue();
                randomModel.setMaximumBlockSize(maxBlockSize);
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        }
    }

    /**
     * ESL Email table model
     * @author pwhelan
     *
     */
    private class ESLTableModel extends DefaultTableModel {
        private Vector<Object> rows;

        public ESLTableModel() {
            rows = new Vector<Object>();
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
            return 4;
        }

        public Class getColumnClass(int columnIndex) {
            if (columnIndex != 0) {
                return Boolean.class;
            }

            return String.class;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            if (readOnly) {
            	return false;
            }
        	
        	if (column == 0) {
                return false;
            }

            return true;
        }

        @Override
        public Object getValueAt(int row, int column) {
            Vector rowData = (Vector) rows.get(row);

            return rowData.get(column);
        }

        public void setValueAt(Object value, int row, int column) {
            ((Vector) rows.get(row)).setElementAt(value, column);
            fireTableCellUpdated(row, column);
        }
    }


    private class EmailTableChangedListener implements TableModelListener {
        public void tableChanged(TableModelEvent e) {
            if (activeDs != null) {
                ESLEmailModel eslModel = activeDs.getEslModel();
                ArrayList<IRole> roles = new ArrayList<IRole>();

                for (int i = 0; i < eslTable.getModel().getRowCount(); i++) {
                    IRole role = (IRole) eslTable.getModel().getValueAt(i, 0);
                    role.setNotifyOfRSInvocation(((Boolean) eslTable.getModel()
                                                                    .getValueAt(i,
                            1)).booleanValue());
                    role.setNotifyOfRSDecision(((Boolean) eslTable.getModel()
                                                                  .getValueAt(i,
                            2)).booleanValue());
                    role.setNotifyOfRSTreatment(((Boolean) eslTable.getModel()
                                                                   .getValueAt(i,
                            3)).booleanValue());
                    roles.add(role);
                }

                eslModel.setRoles(roles);
            }
        }
    }
}
