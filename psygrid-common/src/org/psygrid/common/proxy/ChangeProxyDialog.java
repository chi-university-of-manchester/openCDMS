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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.collection.entry.remote.ProxySetting;
import org.psygrid.common.icons.Icons;
import org.psygrid.common.remote.RemoteManageable;
import org.psygrid.common.security.LoginInterfaceFrame;
import org.psygrid.common.ui.WrappedJOptionPane;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Dialog used to configure proxy settings for network connections.
 * 
 * @author Rob Harper
 *
 */
public class ChangeProxyDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final JFileChooser jfc = new JFileChooser();
	
	private DefaultFormBuilder builder;

	private JPanel listPanel;
	
	private JList list;
	
	private JButton addButton;
	
	private JButton editButton;
	
	private JButton removeButton;
	
	private JButton cancelButton;

	private JButton okButton;

	private JButton importButton;
	
	private JButton exportButton;
	
	private LoginInterfaceFrame parent;

	private List<ProxySetting> lps = null;

	private final ProxyPersistence pManager;
	
	private final RemoteManageable rManager;
	
	private static final Log LOG = LogFactory
			.getLog(ChangeProxyDialog.class);

	public ChangeProxyDialog(LoginInterfaceFrame parent, ProxyPersistence pManager, RemoteManageable rManager) {
		super(parent, "Change Proxy Settings", true);
		this.parent = parent;
		this.pManager = pManager;
		this.rManager = rManager;
		initBuilder();
		initComponents();
		initEventHandling();
		build();
		pack();
		setLocation(WindowUtils.getPointForCentering(this));
	}

	private void initComponents() {

		try {
			lps = pManager.loadProxySettings();
		} catch (IOException ioe) {
			// There may not be any defined
		}

		listPanel = new JPanel();
	    listPanel.setBorder(BorderFactory.createTitledBorder("Proxy Settings"));
	    listPanel.setLayout(new BorderLayout(0, 10));
	    
		exportButton = new JButton(Messages.getString("Entry.export"));
		importButton = new JButton(Messages.getString("Entry.import"));
		JPanel importExportPanel = ButtonBarFactory.buildCenteredBar(importButton, exportButton);
		listPanel.add(importExportPanel, BorderLayout.NORTH);	    
	    
		list = new JList();
		list.setCellRenderer(new ProxyListCellRenderer());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		populateList();
	    JScrollPane listPane = new JScrollPane(list);
	    Dimension listSize = new Dimension(300, 200);
	    listPane.setPreferredSize(listSize);
	    listPane.setMinimumSize(listSize);
	    listPane.setMaximumSize(listSize);
	    
		listPanel.add(listPane, BorderLayout.CENTER);
		
		addButton = new JButton(Messages.getString("Entry.add"));
		editButton = new JButton(Messages.getString("Entry.edit"));
		removeButton = new JButton(Messages.getString("Entry.remove"));
		
		JPanel addEditRemovePanel = ButtonBarFactory.buildLeftAlignedBar(new JButton[]{addButton, editButton, removeButton});
		listPanel.add(addEditRemovePanel, BorderLayout.SOUTH);
		
		cancelButton = new JButton(Messages.getString("Entry.cancel")); //$NON-NLS-1$
		okButton = new JButton(Messages.getString("Entry.ok")); //$NON-NLS-1$
				
	}

	private void initEventHandling() {
		addButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				AddEditProxyDialog dlg = new AddEditProxyDialog(ChangeProxyDialog.this, rManager, list);
				dlg.setVisible(true);
			}
		});
		editButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				handleEdit();
			}
		});
		removeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				handleRemove();
			}
		});
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleOk();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		importButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleImport();
			}
		});
		exportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleExport();
			}
		});

	}

	private void handleOk(){
		//save the new proxy settings to disk
		DefaultListModel model = ((DefaultListModel)list.getModel());
		List<ProxySetting> newProxies = getProxiesFromList();
		try{
			pManager.saveProxySettings(newProxies);
		}
		catch(IOException ex){
			WrappedJOptionPane.showWrappedMessageDialog(
					this, 
					Messages.getString("Proxy.errorMessage"), 
					Messages.getString("Proxy.errorTitle"), 
					WrappedJOptionPane.ERROR_MESSAGE);
			return;
		}
		
		//reset the proxy settings in use
		boolean directConnection = true;
		for ( ProxySetting ps: newProxies ){
			if ( ps.getDefaultProxy().booleanValue() ){
				if (LOG.isInfoEnabled()) {
					LOG.info("Proxy settings: https.proxyHost=" + ps.getServer() + ", https.proxyPort=" + ps.getPort()); //$NON-NLS-1$
				}
				System.setProperty("http.proxyHost", ps.getServer());
				System.setProperty("http.proxyPort", ps.getPort());
				System.setProperty("https.proxyHost", ps.getServer());
				System.setProperty("https.proxyPort", ps.getPort());

				if(ProxyAuthenticationMethods.NONE.name().equals(ps.getAuthenticationMethod())){
					parent.setDoProxyAuth(false);
					rManager.configureProxyAuthentication(ProxyAuthenticationMethods.NONE, null);
				} else {
					parent.setDoProxyAuth(true);
					if((ProxyAuthenticationMethods.WINDOWS.name().equals(ps.getAuthenticationMethod()))){
						rManager.configureProxyAuthentication(ProxyAuthenticationMethods.valueOf(ps.getAuthenticationMethod()), ps.getDomain());
					} else {
						rManager.configureProxyAuthentication(ProxyAuthenticationMethods.valueOf(ps.getAuthenticationMethod()), "");
					}
				}
				
				directConnection = false;
				break;
			}
		}
		
		if ( directConnection ){
			if (LOG.isInfoEnabled()) {
				LOG.info("Proxy settings: direct connection"); //$NON-NLS-1$
			}
			System.clearProperty("http.proxyHost");
			System.clearProperty("http.proxyPort");
			System.clearProperty("https.proxyHost");
			System.clearProperty("https.proxyPort");
			System.clearProperty("http.proxyUser");
			System.clearProperty("http.proxyPassword");
			System.clearProperty("https.proxyUser");
			System.clearProperty("https.proxyPassword");
			parent.setDoProxyAuth(false);
		}
		
		dispose();
	}
	
	private void handleEdit(){
		if ( null == list.getSelectedValue() ){
			WrappedJOptionPane.showWrappedMessageDialog(
					this, 
					Messages.getString("Proxy.noProxySelectedMessage"), 
					Messages.getString("Proxy.noProxySelectedTitle"), 
					WrappedJOptionPane.ERROR_MESSAGE);
			return;
		}
		if ( list.getSelectedValue() instanceof ProxySetting ){
			AddEditProxyDialog dlg = new AddEditProxyDialog(
					ChangeProxyDialog.this, rManager, list, (ProxySetting)list.getSelectedValue());
			dlg.setVisible(true);
		}
		else{
			//this is the <direct connection> option. It can be opened only to
			//set it as the default
			AddEditProxyDialog dlg = new AddEditProxyDialog(
					ChangeProxyDialog.this, rManager, list, true);
			dlg.setVisible(true);
		}
		list.repaint();
	}
	
	private void handleRemove(){
		if ( null == list.getSelectedValue() ){
			WrappedJOptionPane.showWrappedMessageDialog(
					this, 
					Messages.getString("Proxy.noProxySelectedMessage"), 
					Messages.getString("Proxy.noProxySelectedTitle"), 
					WrappedJOptionPane.ERROR_MESSAGE);
			return;
		}
		if ( list.getSelectedValue() instanceof ProxySetting ){
			((DefaultListModel)list.getModel()).removeElement(list.getSelectedValue());
		}
		else{
			WrappedJOptionPane.showWrappedMessageDialog(
					this, 
					Messages.getString("Proxy.removeDirectMessage"), 
					Messages.getString("Proxy.removeDirectTitle"), 
					WrappedJOptionPane.ERROR_MESSAGE);
			return;
		}
	}
	
	private void initBuilder() {
		builder = new DefaultFormBuilder(new FormLayout(
				"right:default,3dlu,50dlu:grow"), //$NON-NLS-1$
				new JPanel());
		builder.setDefaultDialogBorder();

	}

	private void build() {
		builder.setRowGroupingEnabled(true);
		builder.append(listPanel, builder.getColumnCount());
		builder.setRowGroupingEnabled(false);
		builder.appendUnrelatedComponentsGapRow();
		builder.nextLine(2);

        JPanel okCancelPanel = ButtonBarFactory.buildOKCancelBar(okButton, cancelButton);
		builder.append(okCancelPanel, builder.getColumnCount());
		
		getContentPane().add(builder.getPanel());
	}

	private void handleExport(){
		try{
			int result = jfc.showSaveDialog(this);
			if ( JFileChooser.APPROVE_OPTION == result ){
				List<ProxySetting> proxies = getProxiesFromList();
				pManager.exportProxySettingsFile(jfc.getSelectedFile(), proxies);
				WrappedJOptionPane.showWrappedMessageDialog(
						this, "Proxy settings have been exported successfully.", "Success", WrappedJOptionPane.INFORMATION_MESSAGE);
			}
		}
		catch(IOException ex){
			WrappedJOptionPane.showWrappedMessageDialog(
					this, "There was an error whilst trying to export the proxy settings.", "Error", WrappedJOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void handleImport(){
		try{
			if ( WrappedJOptionPane.OK_OPTION == 
				 WrappedJOptionPane.showWrappedConfirmDialog(
					this, "Importing proxy settings from a file will overwrite your existing settings. Continue?", 
					"Confirm", WrappedJOptionPane.OK_CANCEL_OPTION, WrappedJOptionPane.INFORMATION_MESSAGE) ){
				int result = jfc.showOpenDialog(this);
				if ( JFileChooser.APPROVE_OPTION == result ){
					//check settings
					if ( !pManager.checkProxySettings(jfc.getSelectedFile())){
						WrappedJOptionPane.showWrappedMessageDialog(
								this, "The selected file did not contain valid proxy settings.", "Import Error", WrappedJOptionPane.ERROR_MESSAGE);
						return;
					}
					//import settings
					pManager.importProxySettingsFile(jfc.getSelectedFile());
					//reload settings
					lps = pManager.loadProxySettings();
					populateList();
					
					WrappedJOptionPane.showWrappedMessageDialog(
							this, "Proxy settings have been imported successfully.", "Success", WrappedJOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
		catch(IOException ex){
			WrappedJOptionPane.showWrappedMessageDialog(
					this, "There was an error whilst trying to import the proxy settings.", "Error", WrappedJOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void populateList(){
		DefaultListModel listModel = new DefaultListModel();
		listModel.addElement(Messages.getString("Proxy.direct"));		
		if (lps != null) {
			for (ProxySetting ps : lps) {
				listModel.addElement(ps);
			}
		}
		list.setModel(listModel);
	}
	
	private List<ProxySetting> getProxiesFromList(){
		DefaultListModel model = ((DefaultListModel)list.getModel());
		List<ProxySetting> proxies = new ArrayList<ProxySetting>();
		for ( int i=0, c=model.size(); i<c; i++ ){
			Object item = model.getElementAt(i);
			if ( item instanceof ProxySetting ){
				proxies.add((ProxySetting)item);
			}
		}
		return proxies;
	}
	
	public class ProxyListCellRenderer extends DefaultListCellRenderer {

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			boolean defaultValue = false;
			if ( value instanceof ProxySetting ){
				defaultValue = ((ProxySetting)value).getDefaultProxy().booleanValue();
				value = ((ProxySetting)value).getName();
			}
			else{
				//this is the <direct connection> setting, and should be marked as
				//default if none of the other proxy settings are
				DefaultListModel model = (DefaultListModel)list.getModel();
				boolean result = true;
				for ( int i=0, c=model.size(); i<c; i++ ){
					Object item = model.get(i);
					if ( item instanceof ProxySetting ){
						result &= !((ProxySetting)item).getDefaultProxy().booleanValue();
					}
				}
				defaultValue = result;
			}
	        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus); 
	        Icon icon = null;
	        if ( defaultValue ){
	        	icon = Icons.getInstance().getIcon("tick");
	        }
	        else{
	        	icon = Icons.getInstance().getIcon("blank");
	        }
        	label.setIcon(icon);
	        return label;
		}
		
	}
	
}
