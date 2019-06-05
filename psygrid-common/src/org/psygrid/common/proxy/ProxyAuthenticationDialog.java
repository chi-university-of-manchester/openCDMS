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

package org.psygrid.common.proxy;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.jdesktop.swingx.util.WindowUtils;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Supplementary login dialog used when Windows proxy authentication
 * is being used.
 * 
 * @author Rob Harper
 *
 */
public class ProxyAuthenticationDialog extends JDialog {


	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    private DefaultFormBuilder builder;

    private JButton okButton;

    private JPasswordField password1;

    private JLabel password1Label;
    
    private JTextField uid;

    private JLabel uidLabel;   
    
    public ProxyAuthenticationDialog(Dialog parent)   {
        super(parent, "Enter Proxy Credentials", true);
        initBuilder();
        initComponents();
        initEventHandling();
        build();
        pack();
        setLocation(WindowUtils.getPointForCentering(this));
    }

   public ProxyAuthenticationDialog(JFrame parent)   {
        super(parent, "Enter Proxy Credentials", true);
        initBuilder();
        initComponents();
        initEventHandling();
        build();
        pack();
        setLocation(WindowUtils.getPointForCentering(this));
    }

    private void initComponents()   {
        uid = new JTextField();
        uid.setEditable(true);
        uidLabel = new JLabel("User Name");
        password1 = new JPasswordField();
        password1.setEditable(true);
        password1Label = new JLabel("Password");
        okButton = new JButton(Messages.getString("Entry.ok")); //$NON-NLS-1$
    }

    private void initEventHandling() {
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleOk();
            }
        });
    }
    
    private void handleOk() {
		System.setProperty("http.proxyUser", uid.getText());
		System.setProperty("https.proxyUser", uid.getText());
		System.setProperty("http.proxyPassword", new String(password1
				.getPassword()));
		System.setProperty("https.proxyPassword", new String(password1
				.getPassword()));
		dispose();
	}
    
    private void initBuilder() {
        builder = new DefaultFormBuilder(new FormLayout("right:default,3dlu,70dlu:grow"),  //$NON-NLS-1$
                new JPanel());
        builder.setDefaultDialogBorder();

    }
    
    private void build() {
        builder.setRowGroupingEnabled(true);
//        builder.append(new JLabel("Please enter the user"), new JLabel(" name and password"));
//        builder.append(new JLabel("you use to log on"), new JLabel(" to this computer"));
        builder.append(uidLabel, uid);
        builder.append(password1Label, password1);
        builder.setRowGroupingEnabled(false);
        builder.appendUnrelatedComponentsGapRow();
        builder.nextLine(2);
        JPanel buttonsPanel = ButtonBarFactory.buildOKBar(okButton);
        builder.append(buttonsPanel, builder.getColumnCount());
        getContentPane().add(builder.getPanel());
    }
}
