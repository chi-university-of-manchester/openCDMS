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

package org.psygrid.securitymanager.actions;

import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JPasswordField;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.securitymanager.Application;
import org.psygrid.securitymanager.security.PersistenceManager;
import org.psygrid.securitymanager.security.SecurityHelper;
import org.psygrid.securitymanager.security.SecurityManager;
import org.psygrid.securitymanager.ui.ImportDialog;
import org.psygrid.securitymanager.ui.TextFieldWithStatus;
import org.psygrid.securitymanager.utils.PropertiesHelper;

public class OKImportAction extends AbstractAction
{
	private static final Log LOG = LogFactory.getLog(OKImportAction.class);
	
	private JDialog dialog;
	private JDialog parentDialog;
	private TextFieldWithStatus keystoreField;
	private JPasswordField passwordField;
	
	private Application application;
	
	/**
	 * Action to indicate that importing of a certificate should be performed
	 * @param frame the main frame of the application
	 * @param dialog the calling dialog
	 * @param parentDialog the parent dialog
	 * @param keystoreField textfield holding the keystore location
	 * @param password password to go with keystore
	 */
	public OKImportAction(Application application, JDialog dialog, JDialog parentDialog, TextFieldWithStatus keystoreField, JPasswordField password)
	{
		super(PropertiesHelper.getPropertyHelper().getStringFor("org.psygrid.securitymanager.actions.ok"));
		this.dialog = dialog;
		this.parentDialog = parentDialog;
		this.keystoreField = keystoreField;
		this.passwordField = password;
		this.application = application;
	}
	
	/**
	 * Import the keystore
	 */
	public void actionPerformed(ActionEvent e) {
        try {
        	SecurityManager.getInstance().setImportedCert(true);
        	
        	KeyStore ks = KeyStore.getInstance("JKS"); //$NON-NLS-1$
			InputStream fis = new BufferedInputStream(new FileInputStream(keystoreField.getText()));
			ks.load(fis, passwordField.getPassword());
			Certificate[] certs = ks.getCertificateChain(SecurityHelper.getKeyStoreAlias()); //$NON-NLS-1$
			// Only support X.509
			X509Certificate x509 = (X509Certificate) certs[0];
			x509.checkValidity();
			
		} catch (CertificateExpiredException cee) {
			LOG.error("OKImport : certificate expired " + cee.getMessage()); 
			//reinstall the default and continue
			try
			{
				PersistenceManager.getInstance().restoreDefaultKeystore();
			} catch (IOException ioe)
			{
				
				((ImportDialog)dialog).refreshPanel("Invalid certificate or password.");
				return;
			}
			PsyGridClientSocketFactory.reinit();
			((ImportDialog)dialog).refreshPanel("Certificate has expired.");
			return;
		} catch (IOException ioe)
		{
			LOG.error("OKImportAction : IOException " + ioe.getMessage());
			((ImportDialog)dialog).refreshPanel("Invalid certificate or password.");
			return;
		} catch (CertificateNotYetValidException cnyve) {
			LOG.error("OKImportAction : Cert not yet valid " + cnyve.getMessage());
			((ImportDialog)dialog).refreshPanel("Certifcate not yet valid.");
			return;
		} catch (KeyStoreException kse) {
			LOG.error("OKImportAction : key store exception : " + kse.getMessage());
			((ImportDialog)dialog).refreshPanel("Certificate/password not valid.");
			return;
		} catch (CertificateException ce) {
			LOG.error("OKImportAction : certificate exception " + ce.getMessage());
			((ImportDialog)dialog).refreshPanel("Certifcate not valid.");
			return;
		} catch (NoSuchAlgorithmException nsae) {
			LOG.error("OKImportAction : " + nsae.getMessage());
			((ImportDialog)dialog).refreshPanel("Certificate/password not valid.");
			return;
		}
	
		PersistenceManager.getInstance().setKeyStoreLocation(keystoreField.getText());
        System.setProperty("javax.net.ssl.keyStore",  keystoreField.getText());
        System.setProperty("javax.net.ssl.keyStorePassword", passwordField.getText()); //$NON-NLS-1$ //$NON-NLS-2$
 
        parentDialog.dispose();
		dialog.dispose();
		application.checkSystemSecurityConfiguration();
		application.init();
        application.setToolsEnabled(true);
	}
}