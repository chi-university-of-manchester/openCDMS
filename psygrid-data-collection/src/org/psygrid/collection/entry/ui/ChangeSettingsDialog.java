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

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.DefaultButtonModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.collection.entry.EntryHelper;
import org.psygrid.collection.entry.EntryMessages;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.util.ResetWaitRunnable;
import org.psygrid.collection.entry.util.WaitRunnable;
import org.psygrid.common.proxy.ChangeProxyDialog;
import org.psygrid.common.security.LoginInterfaceFrame;
import org.psygrid.common.ui.WrappedJOptionPane;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

/**
 * List the CoCoA settings available for configuration by the user.
 * Provides a short description of each option and launches the
 * relevant dialog to edit that option.
 * 
 * @author Lucy Bridges
 *
 */
public class ChangeSettingsDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private DefaultFormBuilder builder;

	private JButton doneButton;

	private JLabel proxySettingsLabel;
	private JButton proxySettingsButton;
	private JLabel proxySettingsDesc;
	
	private JLabel homeDirLabel;
	private JButton homeDirButton;
	private JLabel homeDirDesc1;
	
	private LoginInterfaceFrame parent;
	
	private JCheckBox alwaysOnlineMode;

    private boolean loginDone;

	private static final Log LOG = LogFactory
			.getLog(ChangeSettingsDialog.class);

    /**
     * @param loginDone
     *            If {@code true}, elements that are only available after a
     *            successful login are shown. Note that this is not a security
     *            feature, passing {@code true} before a successful login will
     *            not bypass the security system, the dialog will simply be
     *            broken.
     */
	public ChangeSettingsDialog(LoginInterfaceFrame parent, boolean loginDone) {
		super(parent, "Application Settings", true);
		this.parent = parent;
		this.loginDone = loginDone;
		initBuilder();
		initComponents();
		initEventHandling();
		build();
		pack();
		setLocation(WindowUtils.getPointForCentering(this));
	}

	private void initComponents() {
		proxySettingsLabel  = new JLabel(Messages.getString("SettingsDialog.proxy")); //$NON-NLS-1$
		proxySettingsLabel.setHorizontalAlignment(SwingConstants.LEFT);

		proxySettingsButton = new JButton(EntryMessages.getString("Entry.edit")); //$NON-NLS-1$
		
		proxySettingsDesc   = new JLabel(Messages.getString("SettingsDialog.proxyMessage")); //$NON-NLS-1$
		proxySettingsLabel.setHorizontalAlignment(SwingConstants.LEFT);
		
		proxySettingsDesc.setFont(new Font(proxySettingsDesc.getFont().getFontName(), Font.PLAIN, proxySettingsDesc.getFont().getSize()-2));		
		
		homeDirLabel  = new JLabel(Messages.getString("SettingsDialog.defaultLocation")); //$NON-NLS-1$
		homeDirLabel.setHorizontalAlignment(SwingConstants.LEFT);
		
		homeDirButton = new JButton(EntryMessages.getString("Entry.edit")); //$NON-NLS-1$
		homeDirButton.setEnabled(false);
		
		homeDirDesc1   = new JLabel(Messages.getString("SettingsDialog.defaultLocationMessage")); //$NON-NLS-1$
		homeDirDesc1.setHorizontalAlignment(SwingConstants.LEFT);
		homeDirDesc1.setFont(new Font(homeDirDesc1.getFont().getFontName(), Font.PLAIN, homeDirDesc1.getFont().getSize()-2));

		if (loginDone) {
			homeDirButton.setEnabled(true);
			
		    alwaysOnlineMode = new JCheckBox(Messages.getString("SettingsDialog.alwaysOnlineMode")); //$NON-NLS-1$
		    alwaysOnlineMode.setModel(new DefaultButtonModel() {
                private static final long serialVersionUID = 1L;

                @Override
                public boolean isSelected() {
                    return isAlwaysOnlineMode();
                }
            });
		    alwaysOnlineMode.addActionListener(createAlwaysOnlineActionListener());
		}
		doneButton = new JButton(EntryMessages.getString("Entry.done")); //$NON-NLS-1$

	}

    private ActionListener createAlwaysOnlineActionListener() {
        return new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        /* 
		         * We use a model for the combo box to make it easier to veto
		         * changes. As a result, we must manually change the selected
		         * status.
		         */
		        final boolean newStatus = !alwaysOnlineMode.isSelected();
		        synchronized (PersistenceManager.getInstance()) {
		            if (newStatus) {
		                String messagePrefix = "You have requested always-online mode to be enabled.";
                        EntryHelper.runWhenNoUncommittedRecords(parent, messagePrefix, new Runnable() {
		                    public void run() {
		                        setAlwaysOnlineMode(newStatus);
		                    }
		                },
		                null);
		            }
		            else{
		            	setAlwaysOnlineMode(newStatus);
		            	new WaitRunnable(ChangeSettingsDialog.this).run();
		            	EntryHelper.runWhenMoveToOnlineOfflineMode(parent, 
		            		new Runnable() {
			                    public void run() {
			                        setAlwaysOnlineMode(!newStatus);
			                        new ResetWaitRunnable(ChangeSettingsDialog.this).run();
			                    }
			            	},
		            		new Runnable() {
			                    public void run() {
			                        new ResetWaitRunnable(ChangeSettingsDialog.this).run();
			                    	WrappedJOptionPane.showWrappedMessageDialog(
			                    			ChangeSettingsDialog.this, 
			                    			Messages.getString("ChangeSettingsDialog.toOnlineOfflineSuccessMessage"), 
			                    			Messages.getString("ChangeSettingsDialog.toOnlineOfflineSuccessTitle"), 
			                    			WrappedJOptionPane.INFORMATION_MESSAGE);
			                    }
			            	});
		            }
		        }
		    }
		    
		    private void setAlwaysOnlineMode(boolean newStatus) {
                PersistenceManager.getInstance().getData().setAlwaysOnlineMode(newStatus);
                try {
                    PersistenceManager.getInstance().savePersistenceData();
                } catch (IOException e1) {
                    ExceptionsHelper.handleIOException(ChangeSettingsDialog.this, e1, false);
                }
                alwaysOnlineMode.revalidate();
		    }
		};
    }
	
	private boolean isAlwaysOnlineMode() {
	    synchronized (PersistenceManager.getInstance()) {
            return PersistenceManager.getInstance().getData().isAlwaysOnlineMode();
        }
	}

	private void initEventHandling() {
		doneButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		proxySettingsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChangeProxyDialog dlg = new ChangeProxyDialog(parent, PersistenceManager.getInstance(), RemoteManager.getInstance());
				dlg.setVisible(true);
			}
		});
		homeDirButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    String messagePrefix = "You have requested the home directory to be changed.";
			    EntryHelper.runWhenNoUncommittedRecords(parent, messagePrefix, new Runnable() {
			        public void run() {
			            ChangeHomeDirDialog dlg = new ChangeHomeDirDialog(parent);
			            dlg.setVisible(true);
			        }
			    }, 
			    null);
			}
		});
		
	}


	private void initBuilder() {
		builder = new DefaultFormBuilder(new FormLayout(
				"left:default,3dlu,50dlu:grow"), //$NON-NLS-1$
				new JPanel());
		builder.setDefaultDialogBorder();

	}

	private void build() {
		builder.setRowGroupingEnabled(true);
		
		builder.append(proxySettingsLabel);
		builder.append(proxySettingsButton);
		builder.nextLine();
		builder.append(proxySettingsDesc);
		
		builder.appendUnrelatedComponentsGapRow();
		builder.nextLine(2);
		
		builder.append(homeDirLabel);		
		builder.append(homeDirButton);
		builder.nextLine();
		builder.append(homeDirDesc1);
		
		builder.setRowGroupingEnabled(false);
		builder.appendUnrelatedComponentsGapRow();
		builder.nextLine(2);
		
		if (loginDone) {
            builder.append(alwaysOnlineMode);
            builder.appendUnrelatedComponentsGapRow();
            builder.nextLine(2);
        }
		
		JPanel buttonsPanel = ButtonBarFactory.buildRightAlignedBar(doneButton);
		builder.append(buttonsPanel, builder.getColumnCount());
		getContentPane().add(builder.getPanel());
	}

}
