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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.*;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.collection.entry.EntryMessages;
import org.apache.commons.validator.UrlValidator;

/**
 * @author admin
 *
 */
public class URLEntryDialog extends JDialog {
	
    public static interface Callable    {
        public void call(URL chosenURL);
    }

	
	private JTextField 			urlEntryField;
	private JButton				urlValidateButton;
	private String				urlTextFieldLabelMessage;
	private JButton				okButton;
	private JButton				cancelButton;
	private DefaultFormBuilder	formBuilder;
	private Callable			callable;
	
	/**
	 * @param owner
	 * @throws HeadlessException
	 */
	public URLEntryDialog(Frame owner, Callable callable) throws HeadlessException {
		super(owner, Messages.getString("URLEntryDialog.dialogTitle"), true);
		this.callable = callable;
		initComponents();
		initEventHandling();
		initBuilder();
		build();
		pack();
		this.setVisible(true);
	}

	private void build() {
        formBuilder.append(urlTextFieldLabelMessage);
        formBuilder.append(urlEntryField);
        formBuilder.append(urlValidateButton);
        formBuilder.appendUnrelatedComponentsGapRow();
        formBuilder.nextLine(2);
        JPanel buttonPanel = ButtonBarFactory.buildOKCancelBar(okButton, cancelButton);
        formBuilder.append(buttonPanel, formBuilder.getColumnCount());
        
        Dimension size = getSize();
        if (size.width < 415) {
            setSize(415, size.height);   
        }
        setLocation(WindowUtils.getPointForCentering(this));
	}

	private void initBuilder() {
        formBuilder = new DefaultFormBuilder(new FormLayout("default, 3dlu, default:grow"),  //$NON-NLS-1$
                new JPanel());
        formBuilder.setDefaultDialogBorder();
        getContentPane().add(formBuilder.getPanel());
	}

	private void initEventHandling() {
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	URL url = null;
            	callable.call(url); //conveys that the user cancelled.
                dispose();   
            }
        });
        
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processOk();
            }
        });
        
        urlValidateButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		validateURL();
        	}
        });
	}

	private void initComponents() {
		urlEntryField = new JTextField(Messages.getString("URLEntryDialog.urlEntryFieldPrefix"));
		urlValidateButton = new JButton();
		urlTextFieldLabelMessage = new String(Messages.getString("URLEntryDialog.urlLabel"));
		okButton = new JButton(EntryMessages.getString("Entry.ok"));
		cancelButton = new JButton(EntryMessages.getString("Entry.cancel"));
		urlValidateButton = new JButton(Messages.getString("URLEntryDialog.validateButtonText"));
	}
	
	private void processOk(){
		if(validateURL()){
			try {
			callable.call(new URL(urlEntryField.getText()));
			} catch (MalformedURLException e) {
				//Do nothing. The text string has already been validated, so this will not occur.
			}
			dispose();
		}
		else{
			String message = new String(Messages.getString("URLEntryDialog.malformedURLMessage"));
			JOptionPane.showMessageDialog(null, message);
		}
	}
	
	private boolean validateURL(){
		UrlValidator validator = new UrlValidator();
		String theURL = urlEntryField.getText();
		boolean retVal = validator.isValid(theURL);
		return retVal;
	}
}
