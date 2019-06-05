package org.psygrid.common.proxy;

import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.collection.entry.remote.ProxySetting;
import org.psygrid.common.remote.RemoteManageable;
import org.psygrid.common.ui.WrappedJOptionPane;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

public class AddEditProxyDialog extends JDialog {

	private RemoteManageable rManager;
	
	private JList proxyList;
	
	private ProxySetting proxy;
	
	private DefaultFormBuilder builder;

	private JLabel namesLabel;

	private JTextField name;

	private JLabel authMethodLabel;

	private JComboBox authMethod;

	private JTextField server;

	private JLabel serverLabel;

	private JTextField port;

	private JLabel portLabel;

	private JTextField ntDomain;

	private JLabel ntDomainLabel;

	private JLabel defaultLabel;

	private JCheckBox defaultCheck;
	
	private JButton testButton;

	private JButton okButton;
	
	private JButton cancelButton;
	
	private boolean direct;
	
	public AddEditProxyDialog(Dialog owner, RemoteManageable rManager, JList proxyList, ProxySetting proxy) throws HeadlessException {
		super(owner, Messages.getString("Proxy.title"), true);
		this.rManager = rManager;
		this.proxyList = proxyList;
		this.proxy = proxy;
		initBuilder();
		initComponents();
		initEventHandling();
		build();
		pack();
		setLocation(WindowUtils.getPointForCentering(this));
	}
	
	public AddEditProxyDialog(Dialog owner, RemoteManageable rManager, JList proxyList) throws HeadlessException {
		super(owner, Messages.getString("Proxy.title"), true);
		this.rManager = rManager;
		this.proxyList = proxyList;
		initBuilder();
		initComponents();
		initEventHandling();
		build();
		pack();
		setLocation(WindowUtils.getPointForCentering(this));
	}

	public AddEditProxyDialog(Dialog owner, RemoteManageable rManager, JList proxyList, boolean direct) throws HeadlessException {
		super(owner, Messages.getString("Proxy.title"), true);
		this.rManager = rManager;
		this.proxyList = proxyList;
		this.direct = direct;
		initBuilder();
		initComponents();
		initEventHandling();
		build();
		pack();
		setLocation(WindowUtils.getPointForCentering(this));
	}

	private void initComponents(){
		namesLabel = new JLabel(Messages.getString("Proxy.name"));
		name = new JTextField();
		port = new JTextField();
		port.setEditable(true);
		portLabel = new JLabel(Messages.getString("Proxy.port"));
		server = new JTextField();
		server.setEditable(true);
		serverLabel = new JLabel(Messages.getString("Proxy.server"));
		ntDomain = new JTextField();
		ntDomainLabel = new JLabel(Messages.getString("Proxy.domain"));
		ntDomain.setEnabled(false);
		authMethodLabel = new JLabel(Messages.getString("Proxy.authMethod"));
		authMethod = new JComboBox();
		defaultLabel = new JLabel(Messages.getString("Proxy.default"));
		defaultCheck = new JCheckBox();
		
		for (ProxyAuthenticationMethods pam : ProxyAuthenticationMethods
				.values()) {
			authMethod.addItem(pam);
		}
		authMethod.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox) e.getSource();
				if (cb.getSelectedItem().equals(
						ProxyAuthenticationMethods.WINDOWS)) {
					ntDomain.setEnabled(true);
				} else {
					ntDomain.setEnabled(false);
					ntDomain.setText("");
				}
			}
		});
		
		if ( null != proxy ){
			name.setText(proxy.getName());
			port.setText(proxy.getPort());
			server.setText(proxy.getServer());
			ntDomain.setText(proxy.getDomain());
			for ( int i=0, c=authMethod.getItemCount(); i<c; i++ ){
				if ( ((ProxyAuthenticationMethods)authMethod.getItemAt(i)).name().equals(proxy.getAuthenticationMethod()) ){
					authMethod.setSelectedIndex(i);
					break;
				}
			}
			defaultCheck.setSelected(proxy.getDefaultProxy().booleanValue());
		}
		
		if ( direct ){
			//Direct connection
			name.setText(Messages.getString("Proxy.direct"));
			name.setEnabled(false);
			port.setEnabled(false);
			server.setEnabled(false);
			ntDomain.setEnabled(false);
			authMethod.setEnabled(false);
			boolean isDefault = true;
			DefaultListModel model = ((DefaultListModel)proxyList.getModel());
			for ( int i=0, c=model.getSize(); i<c; i++ ){
				Object o = model.get(i);
				if ( o instanceof ProxySetting ){
					if (((ProxySetting)o).getDefaultProxy().booleanValue() ){
						isDefault = false;
						break;
					}
				}
			}
			if ( isDefault ){
				defaultCheck.setSelected(true);
				//if direct connection is already the default it cannot be deselected,
				//as this would mean no settings were the default!
				defaultCheck.setEnabled(false);
			}
		}
		
		testButton = new JButton(Messages.getString("Entry.test"));
		okButton = new JButton(Messages.getString("Entry.ok"));
		cancelButton = new JButton(Messages.getString("Entry.cancel"));
	}
	
	private void initBuilder() {
		builder = new DefaultFormBuilder(new FormLayout(
				"right:default,10dlu,75dlu:grow"), //$NON-NLS-1$
				new JPanel());
		builder.setDefaultDialogBorder();
	}

	private void initEventHandling(){
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				handleOk();
			}
		});
		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		testButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				handleTest();
			}
		});
	}
	
	private void build() {
		builder.setRowGroupingEnabled(true);
		builder.append(namesLabel, name);
		builder.append(serverLabel, server);
		builder.append(portLabel, port);
		builder.append(authMethodLabel, authMethod);
		builder.append(ntDomainLabel, ntDomain);
		builder.append(defaultLabel, defaultCheck);
		builder.setRowGroupingEnabled(false);
		builder.appendUnrelatedComponentsGapRow();
		builder.nextLine(2);
		
		ButtonBarBuilder testBarBuilder = new ButtonBarBuilder();
        testBarBuilder.setLeftToRightButtonOrder(true);
        testBarBuilder.addGlue();
        testBarBuilder.addFixed(testButton);
        JPanel testButtonPanel = testBarBuilder.getPanel();       
		builder.append(testButtonPanel, builder.getColumnCount());
		builder.appendUnrelatedComponentsGapRow();
		builder.nextLine(2);

		builder.append(ButtonBarFactory.buildOKCancelBar(okButton, cancelButton), builder.getColumnCount());
		
		getContentPane().add(builder.getPanel());
	}
	
	private void handleTest(){

		if (verify()) {

			//1. store current proxy settings
			Map<String, String> oldSettings = new HashMap<String, String>();
			oldSettings.put("http.proxyHost", System.getProperty("http.proxyHost"));
			oldSettings.put("http.proxyPort", System.getProperty("http.proxyPort"));
			oldSettings.put("https.proxyHost", System.getProperty("https.proxyHost"));
			oldSettings.put("https.proxyPort", System.getProperty("https.proxyPort"));
			oldSettings.put("http.proxyUser", System.getProperty("http.proxyUser"));
			oldSettings.put("http.proxyPassword", System.getProperty("http.proxyPassword"));
			oldSettings.put("https.proxyUser", System.getProperty("https.proxyUser"));
			oldSettings.put("https.proxyPassword", System.getProperty("https.proxyPassword"));
			
			ProxyAuthenticationMethods oldProxyAuthMethod = rManager.getProxyAuthenticationMethod();
			String oldNtDomain = rManager.getNtDomain();
			
			//2. apply new proxy settings
			boolean doProxyAuth = false;
			// Set the JVM properties with the current values
			System.setProperty("http.proxyHost", server.getText());
			System.setProperty("http.proxyPort", port.getText());
			System.setProperty("https.proxyHost", server.getText());
			System.setProperty("https.proxyPort", port.getText());

			if(ProxyAuthenticationMethods.NONE.equals(authMethod.getSelectedItem())){
				rManager.configureProxyAuthentication(ProxyAuthenticationMethods.NONE, null);
			} else {
				if((ProxyAuthenticationMethods.WINDOWS.equals(authMethod.getSelectedItem()))){
					rManager.configureProxyAuthentication((ProxyAuthenticationMethods)authMethod.getSelectedItem(), ntDomain.getText());
					doProxyAuth = true;
				} else {
					rManager.configureProxyAuthentication((ProxyAuthenticationMethods)authMethod.getSelectedItem(), "");
				}
			}

			//3. test out the network connectivity
			if ( doProxyAuth ){
				ProxyAuthenticationDialog dlg = new ProxyAuthenticationDialog(this);
				dlg.setVisible(true);
			}
			boolean result = false;
			try{
				result = rManager.isConnectionAvailable(true);
			}
			catch(Exception ex){
				//do nothing - result of connectivity test will come up as false
			}
				
			//4. set the proxy settings back to what they were
			for ( Entry<String, String> entry: oldSettings.entrySet()){
				if ( null == entry.getValue() ){
					System.clearProperty(entry.getKey());
				}
				else{
					System.setProperty(entry.getKey(), entry.getValue());
				}
			}
			rManager.configureProxyAuthentication(oldProxyAuthMethod, oldNtDomain);
			
			//5. Inform user of the result
			if ( result ){
				//connection successful
				WrappedJOptionPane.showWrappedMessageDialog(
						this, 
						"A connection to the central PsyGrid servers was successfully established.\n" +
						"To use these proxy settings click 'Save As Default'.", 
						"Connection Successful", 
						WrappedJOptionPane.INFORMATION_MESSAGE);
			}
			else{
				//connection unsuccessful
				WrappedJOptionPane.showWrappedMessageDialog(
						this, 
						"A connection to the central PsyGrid servers could not be established.\n" +
						"Your proxy settings are not correct; please modify them and try again.", 
						"Connection Failed", 
						WrappedJOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private void handleOk(){
		
		if ( verify() ){
			
			DefaultListModel model = ((DefaultListModel)proxyList.getModel());
			if ( direct ){
				//do nothing - all we need to do for a direct connection is
				//check if it is being set as the default (in which case all
				//other settings need to be reset to default=false) which is
				//handled below
			}
			else if ( null == proxy ){
				ProxySetting newSetting = new ProxySetting(
						name.getText(),
						server.getText(),
						port.getText(),
						((ProxyAuthenticationMethods)authMethod.getSelectedItem()).name(),
						ntDomain.getText(),
						Boolean.valueOf(defaultCheck.isSelected()));
				model.addElement(newSetting);
			}
			else{
				proxy.setName(name.getText());
				proxy.setServer(server.getText());
				proxy.setPort(port.getText());
				proxy.setAuthenticationMethod(((ProxyAuthenticationMethods)authMethod.getSelectedItem()).name());
				proxy.setDomain(ntDomain.getText());
				proxy.setDefaultProxy(Boolean.valueOf(defaultCheck.isSelected()));
			}
			
			//if this proxy seeting is set to be the default we need to make sure that
			//none of the others in the list are
			if ( defaultCheck.isSelected() ){
				for ( int i=0, c=model.size(); i<c; i++ ){
					Object o = model.get(i);
					if ( o instanceof ProxySetting ){
						ProxySetting ps = (ProxySetting)o;
						if ( !ps.getName().equals(name.getText()) ){
							ps.setDefaultProxy(Boolean.FALSE);
						}
					}
				}
			}
			
			dispose();
		}
		
	}
	
	private boolean verify() {
		if ( direct ){
			return true;
		}
		
		//validate name
		if ( null == name.getText() || 0 == name.getText().length() ){
			WrappedJOptionPane.showWrappedMessageDialog(
					getParent(), 
					Messages.getString("Proxy.invalidNameMessage"), 
					Messages.getString("Proxy.invalidNameTitle"),
					WrappedJOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		DefaultListModel model = ((DefaultListModel)proxyList.getModel());
		for ( int i=0, c=model.size(); i<c; i++ ){
			Object o = model.get(i);
			if ( o instanceof ProxySetting && o != proxy ){
				if ( ((ProxySetting)o).getName().equals(name.getText()) ){
					WrappedJOptionPane.showWrappedMessageDialog(
							getParent(), 
							Messages.getString("Proxy.duplicateNameMessage"), 
							Messages.getString("Proxy.duplicateNameTitle"),
							WrappedJOptionPane.INFORMATION_MESSAGE);
					return false;
				}
			}
		}
		
		//validate server
		if ( null == server.getText() || 0 == server.getText().length() ){
			WrappedJOptionPane.showWrappedMessageDialog(
					getParent(), 
					Messages.getString("Proxy.invalidServerMessage"), 
					Messages.getString("Proxy.invalidServerTitle"),
					WrappedJOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		
		try {
			new Integer(port.getText());
		} catch (Exception e) {
			WrappedJOptionPane.showWrappedMessageDialog(
					getParent(), 
					Messages.getString("Proxy.invalidPortMessage"), 
					Messages.getString("Proxy.invalidPortTitle"),
					WrappedJOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		
		return true;
	}

}
