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
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.psygrid.datasetdesigner.actions.RemoveFromListAction;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.custom.CustomCopyPasteJList;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.utils.HelpHelper;
import org.psygrid.datasetdesigner.utils.IconsHelper;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.Utils;

import sun.awt.geom.Crossings;


/**
 * Lays out the basic configuration dialog used for configuration
 * of dataset units, transformers, statuses etc.
 *
 * @author pwhelan
 */
public abstract class AbstractConfigurationDialog extends JDialog
    implements ActionListener {
	
	/**
	 * Validation icon showing a green tick mark
	 */
	protected final static ImageIcon checkIcon = IconsHelper.getInstance().getImageIcon("check.png");
	
	/**
	 * Validation icon showing a red X
	 */
	protected final static ImageIcon crossIcon = IconsHelper.getInstance().getImageIcon("cross.png");
	
    /**
    * The list of existing settings
    */
    protected JList list;

    /**
     * The currently active dataset
     */
    protected StudyDataSet activeDs;

    /**
     * Ok button, save settings
     */
    private JButton okButton;

    /**
     * Cancel button, dismiss settings
     */
    private JButton cancelButton;

    /**
     * Apply button, save settings now but do not dismiss dialog
     * Used when units are needed for base settings
     */
    private JButton applyButton;

    /**
     * Add an item button
     */
    protected JButton addButton;

    /**
     * Edit an itembutton
     */
    protected JButton editButton;

    /**
     * Remove an item button
     */
    protected JButton removeButton;

    /**
     * Move an item up the list
     */
    protected JButton upButton;

    /**
     * Move an item down the list
     */
    protected JButton downButton;
    
    /**
     * Indicates that the configuration dialog is being used in viewOnly mode
     */
    protected boolean viewOnly;

    /**
     * Indicates that the configuration dialog is being used in readOnly mode
     */
    protected boolean readOnly;
    
    /**
     * The header of the list panel
     */
    private String listTitle;
    
    /**
     * Display the buttons to move items in the list up and down
     * default = false
     */
    private boolean showReorderButtons = false;
    
    /**
     * Show the add remove panel
     * default = true
     */
    private boolean showAddRemovePanel = true;
    
    /**
     * Show the add remove panel
     * default = true
     */
    protected boolean validatePanel = false;

    
    /**
     * A label that updates according to whether the document groups
     * validate (allowed record statues etc. are configured correctly)
     */
    protected JLabel validateLabel;

    /**
     * Constructor - lay out the dialog and initialise
     * @param frame the parent frame
     * @param title the title of the main dialog
     * @param viewOnly if the dialog is to be used in view only mode (ie. DEL view)
     * @param readOnly if the dialog is to be used in read only mode (ie. file is open already)
     * @param applyButtonPresent; if the button panel uses an apply button
     * @param listTitle the header of the main list
     * @param validatePanel display a panel that validates the configuration and displays the result
     */
    public AbstractConfigurationDialog(MainFrame frame, String title,
            boolean viewOnly, boolean readOnly, boolean applyButtonPresent, String listTitle, boolean showReorderButtons,
            boolean showAddRemovePanel, boolean validatePanel) {
        super(frame, title);
        this.readOnly = readOnly;
        this.viewOnly = viewOnly;
        this.listTitle = listTitle;
        this.showReorderButtons = showReorderButtons;
        this.showAddRemovePanel = showAddRemovePanel;
        this.validatePanel = validatePanel;
        activeDs = DatasetController.getInstance().getActiveDs();
        setModal(true);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(Utils.buildDsHeaderPanel(), BorderLayout.NORTH);
        getContentPane().add(buildListPanel(showReorderButtons), BorderLayout.CENTER);
        getContentPane().add(buildButtonPanel(applyButtonPresent), BorderLayout.SOUTH);
        init();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

    }
    
    /**
     * Constructor - lay out the dialog and initialise
     * @param frame the parent frame
     * @param title the title of the main dialog
     * @param viewOnly if the dialog is to be used in view only mode (ie. DEL view)
     * @param readOnly if the dialog is to be used in read only mode (ie. file is open already)
     * @param applyButtonPresent; if the button panel uses an apply button
     * @param listTitle the header of the main list
     */
    public AbstractConfigurationDialog(MainFrame frame, String title,
        boolean viewOnly, boolean readOnly, boolean applyButtonPresent, String listTitle) {
        this(frame, title, viewOnly, readOnly, applyButtonPresent, listTitle, false, true);
    }
    
    /**
     * Constructor - lay out the dialog and initialise
     * @param frame the parent frame
     * @param title the title of the main dialog
     * @param viewOnly if the dialog is to be used in view only mode (ie. DEL view)
     * @param readOnly if the dialog is to be used in read only mode (ie. file is open already)
     * @param applyButtonPresent; if the button panel uses an apply button
     * @param listTitle the header of the main list
     * @param showReorderButtons display the little arrows to move items in the list up and down
     */
    public AbstractConfigurationDialog(MainFrame frame, String title,
        boolean viewOnly, boolean readOnly, boolean applyButtonPresent, String listTitle, boolean showReorderButtons,
        boolean showAddRemovePanel) {
    	this(frame, title, viewOnly, readOnly, applyButtonPresent, listTitle, showReorderButtons, showAddRemovePanel, false);
    }


    /**
     * Build the list panel with add/remove/edit buttons
     * @return the configured list panel with action buttons
     */
    private JPanel buildListPanel(boolean showReorderButtons) {
        JPanel listPanel = new JPanel();
        listPanel.setBorder(BorderFactory.createTitledBorder(listTitle));
        listPanel.setLayout(new BorderLayout());
        list = new CustomCopyPasteJList();
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane listPane = new JScrollPane(list);
        Utils.sizeComponent(listPane, new Dimension(300, 300));
        if (showAddRemovePanel) {
            //first initialise the add/edit/remove buttons
            initButtons();
            listPanel.add(buildAddRemovePanel(), BorderLayout.NORTH);
        }
        listPanel.add(listPane, BorderLayout.CENTER);
        
        if (showReorderButtons) {
        	if (validatePanel) {
        		JPanel holderPanel = new JPanel();
        		holderPanel.setLayout(new BorderLayout());
        		holderPanel.add(buildValidatePanel(), BorderLayout.WEST);
        		holderPanel.add(buildReorderPanel(), BorderLayout.EAST);
        		listPanel.add(holderPanel, BorderLayout.SOUTH);
        	} else {
            	listPanel.add(buildReorderPanel(), BorderLayout.SOUTH);
        	}
        }

        return listPanel;
    }

    /**
     * Build the ok/cancel/apply button panel
     * @param applyButtonPresent; true if an apply button is to be used
     * @return the ok/cancel/apply button panel
     */
    private JPanel buildButtonPanel(boolean applyButtonPresent) {
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

            if (applyButtonPresent) {
                applyButton = new JButton(PropertiesHelper.getStringFor(
                            "org.psygrid.datasetdesigner.apply"));
                applyButton.addActionListener(this);
                buttonPanel.add(applyButton);
            }
        }

        return buttonPanel;
    }

    /**
     * Build a panel that allows reordering of the items
     * in the list
     * @return the configured panel containing buttons to move 
     * items in the list up and down
     */
    private JPanel buildReorderPanel() {
    	JPanel reorderPanel = new JPanel();
		reorderPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		reorderPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.reorder")), BorderLayout.NORTH);
		upButton = new JButton(IconsHelper.getInstance().getImageIcon("1uparrow.png"));
		downButton = new JButton(IconsHelper.getInstance().getImageIcon("1downarrow.png"));
		upButton.addActionListener(this);
		downButton.addActionListener(this);
		
		//disable if published study
		if (DatasetController.getInstance().getActiveDs().getDs().isPublished()) {
			upButton.setEnabled(false);
			downButton.setEnabled(false);
		}
		
		reorderPanel.add(upButton);
		reorderPanel.add(downButton);
    	return reorderPanel;
    }

    
    protected  JPanel buildValidatePanel() {
    	JPanel validatePanel = new JPanel();
    	validatePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    	validatePanel.add(new JLabel("Validated: "));
    	validateLabel = new JLabel(crossIcon);
    	validatePanel.add(validateLabel);
    	return validatePanel;
    }
    
    public void updateValidationLabel(boolean validated, String validationString) {
    	if (validated) {
        	validateLabel.setIcon(checkIcon);
    	} else {
    		validateLabel.setIcon(crossIcon);
    	}
    	
    	validateLabel.setToolTipText(validationString);
    	
    	validate();
    	repaint();
    	
    }

    
    /**
     * Build a button panel with add/remove/edit buttons for the units
     * @return the configured add/remove/edit button panel
     */
    protected JPanel buildAddRemovePanel() {
        JPanel addRemovePanel = new JPanel();
        addRemovePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        //if readonly don't add any buttons
        if (readOnly){
        	return addRemovePanel;
        }
        
        if (viewOnly) {
            editButton.setEnabled(false);
            addRemovePanel.add(editButton);
        } else {
            addRemovePanel.add(addButton);
            editButton.setEnabled(false);
            addRemovePanel.add(editButton);
        }

        removeButton.setEnabled(false);
        addRemovePanel.add(removeButton);

        //Only enable the buttons if an item is selected
        list.addListSelectionListener(new ListSelectionListener() {
               	public void valueChanged(ListSelectionEvent event) {
                    boolean enabled = true;

                    if ((list == null) || (list.getSelectedValue() == null)) {
                        enabled = false;
                    }

                    if (removeButton != null && !activeDs.getDs().isPublished()) {
                        removeButton.setEnabled(enabled);
                    }

                    if (editButton != null && !activeDs.getDs().isPublished()) {
                        editButton.setEnabled(enabled);
                    }
                }
            });

        return addRemovePanel;
    }

    /**
     * Initialise the add/edit/remove buttons
     */
    protected void initButtons(){
    	removeButton = new JButton(new RemoveFromListAction(list));
    }

    /**
     * Get the remove button
     */
    protected JButton getRemoveButton() {
    	return removeButton;
    }
    
    
    /**
     * Initialise the main list with existing settings
     */
    protected abstract void init();

    /**
     * Save changes to the dataset
     */
    protected abstract void save();
    
	/**
	 * Swap the position of the items in the list
	 * @param a the position of the first item in the list
	 * @param b the position of the second item in the list 
	 */
	private void swap(int a, int b) {
		Object aObject = list.getModel().getElementAt(a);
	    Object bObject = list.getModel().getElementAt(b);
	    ((DefaultListModel)list.getModel()).set(a, bObject);
	    ((DefaultListModel)list.getModel()).set(b, aObject);
	}
    

    /**
     * Handle the ok, cancel and apply actions
     * ok = save and dismiss
     * cancel = dismiss
     * apply = save and do not dismiss
     * @aet The calling action event
     */
    public void actionPerformed(ActionEvent aet) {
        if (aet.getSource() == okButton) {
        	//in read-only mode, just dismiss dialog on ok
        	if (!readOnly) {
                save();
                DatasetController.getInstance().getActiveDs().setDirty(true);
                //if published then show the provenance dialog
				if (DatasetController.getInstance().getActiveDs().getDs().isPublished()) {
					new ProvenanceDialog(this, DatasetController.getInstance().getActiveDs().getDs());
					//don't dispose of dialog
					return;
				} 
        	}
            this.dispose();
        } else if (aet.getSource() == cancelButton) {
            this.dispose();
        } else if (aet.getSource() == applyButton) {
        	//in read-only mode, just dismiss dialog on apply
        	if (!readOnly) {
                save();
        	}
        } else if (aet.getSource() == upButton) {
			if (list.getSelectedIndex() != -1) {
				if ((list.getSelectedIndex()-1) >= 0) {
					int selectedIndex = list.getSelectedIndex();
					swap(selectedIndex, selectedIndex-1);
					list.setSelectedIndex(selectedIndex-1);
				}
			}
        } else if (aet.getSource() == downButton) {
			if (list.getSelectedIndex() != -1) {
				if ((list.getSelectedIndex()+1)< list.getModel().getSize()) {
					int selectedIndex = list.getSelectedIndex();
					swap(selectedIndex, selectedIndex+1);
					list.setSelectedIndex(selectedIndex+1);
				}
			}
        }
    }
    
}
