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

package org.psygrid.collection.entry.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.collection.entry.EntryMessages;
import org.psygrid.data.model.hibernate.StandardCode;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

public class ChooseStdCodeDialog extends JDialog {

    private static final long serialVersionUID = 82914510509163858L;

    private List<StandardCode> stdCodes;
    
    private String message;
    
    private DefaultFormBuilder builder;

    private JButton okButton;

    private JButton cancelButton;
    
    /**
     * The standatd code selected in the dialog.
     */
    private StandardCode stdCode = null;
    
    private List<JRadioButton> stdCodeButtons;

    private JRadioButton allSecButton;
    
    /**
     * If True the dialog is being used to select a standard code to apply to
     * section(s) of a document.
     * <p>
     * The radio buttons for choosing this section or all sections will be shown.
     */
    private boolean forSection;
    
    /**
     * If True, the user has selected that they wish to apply the standard code
     * to all sections of the current document.
     * <p>
     * If False, the user has selected that they wish to apply the standard code
     * to just the current section of the current document.
     */
    private boolean allSections;

    public ChooseStdCodeDialog(JFrame parent, String message, List<StandardCode> stdCodes) {
        super(parent, Messages.getString("ChooseStdCodeDialog.dialogTitle"), true);
        this.stdCodes = stdCodes;
        this.message = message;
        this.forSection = false;
        initBuilder();
        initComponents();
        pack();
        setLocation(WindowUtils.getPointForCentering(this));
    }
    
    public ChooseStdCodeDialog(JFrame parent, String message, List<StandardCode> stdCodes, boolean forSection) {
        super(parent, Messages.getString("ChooseStdCodeDialog.dialogTitle"), true);
        this.stdCodes = stdCodes;
        this.message = message;
        this.forSection = forSection;
        initBuilder();
        initComponents();
        pack();
        setLocation(WindowUtils.getPointForCentering(this));
    }
    
    private void initBuilder() {
        builder = new DefaultFormBuilder(new FormLayout("default"),  //$NON-NLS-1$
                new JPanel());
        builder.setDefaultDialogBorder();

    }
    
    private void initComponents()   {
        JLabel label = new JLabel(message);
        builder.append(label);
        stdCodeButtons = new ArrayList<JRadioButton>();
        ButtonGroup stdCodeGroup = new ButtonGroup();
        for (StandardCode sc: stdCodes) {
            JRadioButton radioButton = new JRadioButton(sc.getCode()+". "+sc.getDescription());
            stdCodeGroup.add(radioButton);
            stdCodeButtons.add(radioButton);
            builder.append(radioButton);
        }
        
        if ( forSection ){
        	//Add the radio buttons to select whether to apply the standard code
        	//to the current section or all sections
        	builder.appendUnrelatedComponentsGapRow();
        	builder.nextRow();
	        builder.append(new JLabel(Messages.getString("ChooseStdCodeDialog.sectionApplicationLabel")));
	        ButtonGroup secDocGroup = new ButtonGroup();
	        JRadioButton thisSecButton = new JRadioButton(Messages.getString("ChooseStdCodeDialog.thisSectionLabel"));
	        thisSecButton.setSelected(true);
	        secDocGroup.add(thisSecButton);
	        builder.append(thisSecButton);
	        allSecButton = new JRadioButton(Messages.getString("ChooseStdCodeDialog.allSectionsLabel"));
	        secDocGroup.add(allSecButton);
	        builder.append(allSecButton);
        }
       
        okButton = new JButton(EntryMessages.getString("Entry.ok")); //$NON-NLS-1$
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleOk();
            }
        });
        
        cancelButton = new JButton(EntryMessages.getString("Entry.cancel")); //$NON-NLS-1$
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        JPanel buttonsPanel = ButtonBarFactory.buildOKCancelBar(okButton, cancelButton);
        builder.appendUnrelatedComponentsGapRow();
        builder.nextLine(2);
        builder.append(buttonsPanel, builder.getColumnCount());
        
        getContentPane().add(builder.getPanel());
    }
    
    private void handleOk(){
        for ( int i=0; i<stdCodeButtons.size(); i++ ){
            JRadioButton b = stdCodeButtons.get(i);
            if ( b.isSelected() ){
                stdCode = stdCodes.get(i);
                break;
            }
        }
        if ( null == stdCode ){
            String title = Messages.getString("ChooseStdCodeDialog.errorTitle");
            String message = Messages.getString("ChooseStdCodeDialog.errorMessage");
            JOptionPane.showMessageDialog(this, message, title,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if ( forSection ){
        	allSections = allSecButton.isSelected();
        }
        dispose();
    }

    public StandardCode getStdCode() {
        return stdCode;
    }
    
    public boolean isAllSections(){
    	return allSections;
    }
    
}
