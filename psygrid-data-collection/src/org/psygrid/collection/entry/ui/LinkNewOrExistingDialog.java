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

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.collection.entry.Application;
import org.psygrid.common.ui.WrappedJOptionPane;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Record;

/**
 * Dialog used to select whether to link to a new or existing
 * secondary record, for an existing primary record
 * 
 * @author Rob Harper
 *
 */
public class LinkNewOrExistingDialog extends JDialog {
 
	private static final long serialVersionUID = -2001657903588840458L;

	protected DefaultFormBuilder builder;

    protected JRadioButton newOption;

    protected JRadioButton existingOption;
    
    protected JButton okButton;

    protected JButton cancelButton;
    
    protected final Record primaryRecord;

    protected final DataSet secondaryDataSet;
    
    private Result result;
    
    public LinkNewOrExistingDialog(Application parent, Record primaryRecord, DataSet secondaryDataSet)   {
        super(parent, Messages.getString("LinkNewOrExistingDialog.dialogTitle"), true);
        this.primaryRecord = primaryRecord;
        this.secondaryDataSet = secondaryDataSet;
        setModal(true);
        initBuilder();
        initComponents();
        initEventHandling();
        build();
        pack();
        setLocation(WindowUtils.getPointForCentering(this));
    }

    protected void initBuilder() {
        builder = new DefaultFormBuilder(
        		new FormLayout("default:grow"),  //$NON-NLS-1$
                new JPanel());
        builder.setDefaultDialogBorder();
    }

    protected void initComponents()   {
    	newOption = new JRadioButton(Messages.getString("LinkNewOrExistingDialog.newParticipant")); //$NON-NLS-1$
    	existingOption = new JRadioButton(Messages.getString("LinkNewOrExistingDialog.existingParticipant")); //$NON-NLS-1$
    	ButtonGroup options = new ButtonGroup();
    	options.add(newOption);
    	options.add(existingOption);
    	cancelButton = new JButton(Messages.getString("LinkNewOrExistingDialog.cancel"));
    	okButton = new JButton(Messages.getString("LinkNewOrExistingDialog.ok"));
    }

    protected void initEventHandling() {
    	okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	handleOK();
            }
        });
     	cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	result = Result.CANCEL;
                dispose();
            }
        });
    }
    
    protected void build() {
        builder.append(newOption);
        builder.append(existingOption);
        builder.appendUnrelatedComponentsGapRow();
        builder.nextLine(2);
		JPanel okButtonPanel = ButtonBarFactory.buildOKCancelBar(okButton,
				cancelButton);

        builder.append(okButtonPanel);
       
        getContentPane().add(builder.getPanel());
    }
    
    private void handleOK(){
    	if ( newOption.isSelected() ){
    		result = Result.NEW;
    	}
    	else if ( existingOption.isSelected() ){
    		result = Result.EXISTING;
    	}
    	else{
    		WrappedJOptionPane.showWrappedMessageDialog(
    				this, Messages.getString("LinkNewOrExistingDialog.noSelectionMessage"), 
    				Messages.getString("LinkNewOrExistingDialog.noSelectionTitle"), 
    				WrappedJOptionPane.INFORMATION_MESSAGE);
    		return;
    	}
    	dispose();
    }
    
	public Result getResult() {
		return result;
	}

    public enum Result {
    	NEW,
    	EXISTING,
    	CANCEL
    }

}
