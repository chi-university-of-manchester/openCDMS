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

package org.psygrid.securitymanager.security;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.psygrid.common.proxy.ProxyAuthenticationMethods;
import org.psygrid.common.remote.RemoteManageable;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.attributeauthority.client.AAManagementClient;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.attributeauthority.service.ProcessingFaultMessage;
import org.psygrid.security.attributeauthority.types.PostProcessLoginResponseType;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.security.policyauthority.client.PAManagementClient;
import org.psygrid.security.policyauthority.client.PAQueryClient;
import org.psygrid.security.policyauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.securitymanager.Application;
import org.psygrid.securitymanager.ExceptionsHelper;
import org.psygrid.securitymanager.ui.ForceChangePasswordDialog;
import org.psygrid.securitymanager.utils.PropertiesHelper;
import org.psygrid.www.xml.security.core.types.AttributeType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectDescriptionType;
import org.psygrid.www.xml.security.core.types.ProjectType;

public class SecurityManager implements RemoteManageable {
        
    private String userName;

    private String encryptedPassword;
    private SecretKeySpec keySpec;
    
	private Boolean isSuperUser = null;

    private AAQueryClient aaClient;
    private AAManagementClient aaMgmtClient;

    private LoginClient loginClient = LoginClient.getInstance();
    private PAQueryClient paClient;
    private PAManagementClient paMgmtClient;
    
    private Date keyValidity = null;
    
    private static final SecurityManager INSTANCE = new SecurityManager();
    private static final Log LOG = LogFactory.getLog(SecurityManager.class);
    
    private static final String PACKAGE = "../etc/keystores/"; //$NON-NLS-1$
    
    //if using importedCert, set this so that key will not be refreshed and overwritten!
    private boolean importedCert = false;
    
    private SecurityManager()  {
        Security.addProvider(new BouncyCastleProvider());
        setProperties();
    }
    
    public String getVersion() throws ConnectException, RemoteServiceFault   {
        return loginClient.getVersion();
    }
    
    private void setProperties() {
    	final String TRUST_STORE_LOCATION = PersistenceManager.getInstance().getTrustStoreLocation();
    	System.setProperty("javax.net.ssl.trustStore", TRUST_STORE_LOCATION); //$NON-NLS-1$
        System.setProperty("javax.net.ssl.trustStorePassword",  //$NON-NLS-1$
        		SecurityHelper.getTrustStorePassword());
        System.setProperty("javax.net.ssl.keyStore",  //$NON-NLS-1$
                PersistenceManager.getInstance().getKeyStoreLocation());
		System.setProperty("javax.net.ssl.keyStorePassword", SecurityHelper.getKeyStorePassword()); //$NON-NLS-1$ //$NON-NLS-2$
        System.setProperty("org.apache.axis.components.net.SecureSocketFactory", //$NON-NLS-1$
                PsyGridClientSocketFactory.class.getName());
    }
    
    public static SecurityManager getInstance() {
        return INSTANCE;
    }
    
    public String decrypt(String cipherText) throws BadPaddingException    {
        checkText(cipherText);
        char[] password = getPassword();
        String clearText = null;
        try {
            clearText = SecurityHelper.decrypt(cipherText, password);
        } catch (GeneralSecurityException gse) {
            if (gse instanceof BadPaddingException == false) {
                SecurityHelper.wrapIntoRuntimeExceptionAndThrow(gse); 
            }
            else {
                BadPaddingException bpe = (BadPaddingException) gse;
                throw bpe;
            }
        }
        finally {
            SecurityHelper.clearCharArray(password);
        }
        return clearText;
    }
    
    private void checkText(String text) {
        if (text == null || text.equals("")) { //$NON-NLS-1$
            throw new IllegalArgumentException("clearText must be a non-empty String"); //$NON-NLS-1$
        }
    }
    
    private void doRemoteLogin(char[] password) 
            throws ConnectException, 
            	   AuthenticationException,
            	   AccountLockedException,
            	   AccountDormantException,
            	   RemoteServiceFault,
            IOException {
		// If the current keystore has expired, then to log back in
		// we will need the default keystore
        // Bug#487
		try {
			KeyStore ks = KeyStore.getInstance("JKS"); //$NON-NLS-1$
			InputStream fis = Thread.currentThread().getContextClassLoader().getResourceAsStream(SecurityHelper.PACKAGE + "defaultKeystore.jks");
			ks.load(fis, SecurityHelper.getKeyStorePassword().toCharArray());
			Certificate[] certs = ks.getCertificateChain(SecurityHelper.getKeyStoreAlias()); //$NON-NLS-1$
			// Only support X.509
			X509Certificate x509 = (X509Certificate) certs[0];
			x509.checkValidity();
		} catch (CertificateExpiredException cee) {
			//reinstall the default and continue
			PersistenceManager.getInstance().restoreDefaultKeystore();
	        //Force the new key in over the existing key
			PsyGridClientSocketFactory.reinit();
		} catch (CertificateNotYetValidException cnyve) {
			throw new AuthenticationException(cnyve.getMessage(), cnyve);
		} catch (KeyStoreException kse) {
			throw new AuthenticationException(kse.getMessage(), kse);
		} catch (CertificateException ce) {
			throw new AuthenticationException(ce.getMessage(), ce);
		} catch (NoSuchAlgorithmException nsae) {
			SecurityHelper.wrapIntoRuntimeExceptionAndThrow(nsae);
		} 
		loginClient.login(userName, password);
    }
    
    public String getSAMLAssertion(ProjectType project) throws 
            RemoteServiceFault, ConnectException, IOException,
            EntrySAMLException {
        
        try {
            return aaClient.getSAMLAssertion(project).toString();
        } catch (PGSecuritySAMLVerificationException e) {
            throw new EntrySAMLException(e);
        } catch (PGSecurityInvalidSAMLException e) {
            throw new EntrySAMLException(e);
        } catch (PGSecurityException e) {
            throw new RemoteServiceFault(e);
        }
    }

    public String getSAMLAssertion() throws 
    RemoteServiceFault, ConnectException, IOException,
    EntrySAMLException {

        try {
        	System.out.println("SecurityManager : aa client " + aaClient);
            return aaClient.getSAMLAssertion().toString();
        } catch (PGSecuritySAMLVerificationException e) {
            throw new EntrySAMLException(e);
        } catch (PGSecurityInvalidSAMLException e) {
            throw new EntrySAMLException(e);
        } catch (PGSecurityException e) {
            throw new RemoteServiceFault(e);
        }
    }

    public List<ProjectType> getUserProjects() throws ConnectException, 
            EntrySAMLException, RemoteServiceFault, NotAuthorisedFaultMessage {
        try {
            return aaClient.getMyProjects();
        } catch (PGSecuritySAMLVerificationException e) {
            throw new EntrySAMLException(e);
        } catch (PGSecurityInvalidSAMLException e) {
            throw new EntrySAMLException(e);
        } catch (PGSecurityException e) {
            throw new RemoteServiceFault(e); 
        } catch (org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage e) {
        	throw new RuntimeException();
        }
    }
    
    public List<GroupType> getUsersGroupsInProject(ProjectType project) 
            throws ConnectException, EntrySAMLException, RemoteServiceFault 
            {
        try {
            return aaClient.getMyGroupsInProject(project);
        } catch (PGSecuritySAMLVerificationException e) {
            throw new EntrySAMLException(e);
        } catch (PGSecurityInvalidSAMLException e) {
            throw new EntrySAMLException(e);
        } catch (PGSecurityException e) {
            throw new RemoteServiceFault(e);
        } catch (org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage e) {
        	throw new RuntimeException();
        }
    }
    
    private char[] getPassword()    {
        if (encryptedPassword == null) {
        	throw new IllegalArgumentException("encryptedPassword cannot be null"); //$NON-NLS-1$
        }
        try {
        	return SecurityHelper.decrypt(encryptedPassword, keySpec);
        }
        catch (GeneralSecurityException gse) {
        	// Should not happen
            SecurityHelper.wrapIntoRuntimeExceptionAndThrow(gse);
            return null;
        }
    }
    
    public boolean login(String user, char[] password, boolean skipLogin)
            throws 
            	RemoteException, 
            	AuthenticationException,
            	AccountLockedException,
         	    AccountDormantException,
            	IOException,
            	RemoteServiceFault,
            	ConnectException {
    	
    	this.userName = user;
        try {
        	keySpec = SecurityHelper.getRandomKeySpec();

        	keyValidity = refreshKey(password);
        	if (LOG.isDebugEnabled()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy 'at' HH:mm:ss z"); //$NON-NLS-1$
            }
            
            if (aaClient == null) {
                aaClient = SecurityHelper.getAAQueryClient();
            }
            if (aaMgmtClient == null) {
                aaMgmtClient = SecurityHelper.getAAManagementClient();
            }
            
            if (paClient == null) {
                paClient = SecurityHelper.getPAQueryClient();
            }
            if (paMgmtClient == null) {
                paMgmtClient = SecurityHelper.getPAManagementClient();
            }
            
            encryptedPassword = SecurityHelper.encrypt(password, keySpec);
        } catch (GeneralSecurityException e) {
            // Should not happen
        	LOG.error("SecurityManager : general security exception " + e.getMessage());
            SecurityHelper.wrapIntoRuntimeExceptionAndThrow(e);

        }
        finally {
            SecurityHelper.clearCharArray(password);
        }

        // If no exception is thrown, then authentication is successful
        return true;
    }

    public void login(String user, char[] password) throws RemoteServiceFault,ConnectException,
            RemoteException, AuthenticationException, AccountDormantException, AccountLockedException, IOException {
        login(user, password, false);
    }
    
    public Date getKeyValidity() {
        return keyValidity;
    }
    
    /**
     * Refreshes the certificate used within the security subsystem to establish
     * what actions (if any) the user is allowed to execute. This method will
     * only work correctly if it's called after {@link #login(String, char[])}
     * has been called at least once.
     * 
     * @return Date until which the new key is valid.
     * @throws IOException
     */
    public Date refreshKey() throws ConnectException, IOException, 
            AccountLockedException, AccountDormantException, RemoteServiceFault   {
        char[] password = null;
        try {
        	password = getPassword();
        	keyValidity = refreshKey(password);
        }
        // Should never happen if used after login
        catch (AuthenticationException e) {
            SecurityHelper.wrapIntoRuntimeExceptionAndThrow(e);
        }
        finally {
            if (password != null) {
                SecurityHelper.clearCharArray(password);
            }
        }
        return keyValidity;
    }
	
    private Date refreshKey(char[] password) throws AuthenticationException, 
	    ConnectException, AccountLockedException, AccountDormantException, IOException, RemoteServiceFault {
		try {
			setImportedCert(false);
			doRemoteLogin(password);
		    long localTime = System.currentTimeMillis();
		    KeyStore ks = KeyStore.getInstance("JKS"); //$NON-NLS-1$
		    InputStream fis = new BufferedInputStream(new FileInputStream(PersistenceManager.getInstance().getKeyStoreLocation()));
		    ks.load(fis, password);
		    Certificate[] certs = ks.getCertificateChain(SecurityHelper.getKeyStoreAlias()); //$NON-NLS-1$
		    // Only support X.509
		    X509Certificate x509 = (X509Certificate) certs[0];
		    long keyNotAfter = x509.getNotAfter().getTime();
		    long keyNotBefore = x509.getNotBefore().getTime();
		    long duration = keyNotAfter - keyNotBefore;
		    long validity = localTime + duration;
		    return new Date(validity);
		} catch (KeyStoreException kse) {
		    LOG.error("SecurityManager refresh key " + kse.getMessage());
			throw new AuthenticationException(kse.getMessage(), kse);
		} catch (CertificateException ce) {
			LOG.error("Securitymanager refresh key " + ce.getMessage());
			throw new AuthenticationException(ce.getMessage(), ce);
		} catch (NoSuchAlgorithmException nsae) {
			LOG.error("SecurityManager : no such algorithm " + nsae.getMessage());
		}
		//Should never happen
		return null;
    }
    
    public void refreshKey(boolean onlyIfNeeded) throws ConnectException, 
    	AccountLockedException, AccountDormantException, IOException, RemoteServiceFault {
    	
    	Date keyValidity = getKeyValidity();
    	//check to see that there is a key to refresh
    	if (keyValidity != null) {
    		if (onlyIfNeeded) {
    			Date now = new Date();
    			long thirtySeconds = 30 * 1000;
    			now.setTime(now.getTime() + thirtySeconds);
    			if (keyValidity.getTime() < now.getTime()) {
    				refreshKey();
    			}
    		} else {
    			refreshKey();
    		}
    	}
    }
    
    public void setImportedCert(boolean importedCert)
    {
    	this.importedCert = importedCert;
    }
    
    public boolean getImportedCert()
    {
    	return importedCert;
    }

    public PostProcessLoginResponseType postProcessLogin()
		throws ConnectException, PGSecurityException, org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
		org.psygrid.security.policyauthority.service.NotAuthorisedFaultMessage {
			return aaClient.postProcessLogin();
	}

	static private void forcePasswordChange(Application application) {
		String title = PropertiesHelper.getPropertyHelper()
				.getStringFor("SecurityManager.passwordChangeTitle"); //$NON-NLS-1$
		String message = PropertiesHelper.getPropertyHelper()
				.getStringFor("SecurityManager.passwordChangeMessage"); //$NON-NLS-1$
		JOptionPane.showMessageDialog(application, message, title,
				JOptionPane.ERROR_MESSAGE);
			ForceChangePasswordDialog dlg = new ForceChangePasswordDialog(application);
			dlg.setVisible(true);
	}
	
    public boolean comparePasswords(char[] password) {
		return comparePasswords(password, getPassword());
	}

	public boolean comparePasswords(char[] p1, char[] p2) {
		if (p1.length != p2.length) {
			return false;
		}
		for (int i = 0; i < p1.length; i++) {
			if (p1[i] != p2[i]) {
				return false;
			}
		}
		return true;
	}


	public void postProcessLogin(Application application){
		try {
			PostProcessLoginResponseType pplrt = SecurityManager.getInstance().postProcessLogin();
			application.setUserName(userName);
			String title = PropertiesHelper.getPropertyHelper().getStringFor("SecurityManager.login.notice.title");
			StringBuilder message = new StringBuilder();
			message.append(PropertiesHelper.getPropertyHelper().getStringFor("SecurityManager.login.notice.message"));
			if(pplrt.getPreviousLoginDates()!=null){
				for(int i=1; i<6 && (pplrt.getPreviousLoginDates().length-i>=0); i++){
					message.append("On ");
					message.append(pplrt.getPreviousLoginDates(pplrt.getPreviousLoginDates().length-i).getTime().toString());
					message.append(" from ");
					message.append(pplrt.getPreviousLoginAddresses(pplrt.getPreviousLoginAddresses().length-i));
					message.append(" (");
					if ( pplrt.getAuthenticated(pplrt.getAuthenticated().length-i) ){
						message.append("Success");
					}
					else{
						message.append("Failure");
					}
					message.append(")");
					//Not used at present
					//message += " ";
					//message += pplrt.getPreviousLoginHosts(pplrt.getPreviousLoginHosts().length-i);	
					message.append("\n");
				}
				message.append(PropertiesHelper.getPropertyHelper().getStringFor("SecurityManager.login.notice.report")); //$NON-NLS-1$
			}
			message.append(PropertiesHelper.getPropertyHelper().getStringFor("SecurityManager.login.notice.continueQuestion"));
			application.refreshContentPane();
			int i = JOptionPane.showConfirmDialog(application, message.toString(), title,
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if(i==JOptionPane.NO_OPTION){
				System.exit(0);
			}
			if(pplrt.isForcePasswordChange()){
				forcePasswordChange(application);
			}
		} catch (PGSecurityException pgse) {
			ExceptionsHelper.handlePGSecurityException(application, pgse);
		} catch (org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage nafMessage) {
			ExceptionsHelper.handleNotAuthorisedFault(application, nafMessage);
		} catch (ConnectException ce) {
			ExceptionsHelper.handleConnectException(application, ce);
		} catch (org.psygrid.security.policyauthority.service.NotAuthorisedFaultMessage nafm) {
			ExceptionsHelper.handleNotAuthorisedFault(application, nafm);
		}
	}
	
	public boolean changePassword(char[] newPassword) throws ConnectException,
		org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
		PGSecurityException
	{
	    	try
	    	{
	    		boolean rc = aaClient.changePassword(newPassword, getPassword());
				return rc;
	    	} catch (PGSecurityException pgse)
	    	{
	    		SecurityHelper.wrapIntoRuntimeExceptionAndThrow(pgse);
	    	} catch (org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage nafm) {
	    		SecurityHelper.wrapIntoRuntimeExceptionAndThrow(nafm);
			} finally { 
				 SecurityHelper.clearCharArray(newPassword);
			}
			return false;
	}

	public void configureProxyAuthentication(
			ProxyAuthenticationMethods authScheme, String ntDomain) {
		PsyGridClientSocketFactory.setAuthScheme(authScheme);
		PsyGridClientSocketFactory.setNTdomain(ntDomain);
	}

	public String getNtDomain() {
		return PsyGridClientSocketFactory.getNTdomain();
	}

	public ProxyAuthenticationMethods getProxyAuthenticationMethod() {
		return PsyGridClientSocketFactory.getAuthScheme();
	}

	public boolean isConnectionAvailable() {
		return isConnectionAvailable(true);
	}

	public boolean isConnectionAvailable(boolean log) {
		try {
			InetAddress.getByName("localhost");
		} catch (Exception e) {
			if (log && LOG.isInfoEnabled()) {
				LOG.info(e.getMessage()); 
				e.printStackTrace();
			}
		}
		try {
			refreshKey(true);
			SecurityManager.getInstance().getVersion();
		} catch (AccountLockedException ale){
			LOG.info("refreshKey failure", ale);
			return offline(log);
		} catch (AccountDormantException ale){
			LOG.info("dormant account failure", ale);
			return offline(log);
		} catch (ConnectException e) {
			return offline(log);
		} catch (IOException ioe) {
			LOG.info("refreshKey failure", ioe);
			return offline(log);
		} catch (RemoteServiceFault rsf) {
			LOG.info("refreshKey failure", rsf);
			return offline(log);
		}
		return online(log);
	}
    
	private boolean online(boolean log) {
		if (log && LOG.isInfoEnabled()) {
			LOG.info("Application is online."); //$NON-NLS-1$
		}
		return true;
	}

	private boolean offline(boolean log) {
		if (log && LOG.isInfoEnabled()) {
			LOG.info("Application is offline."); //$NON-NLS-1$
		}
		return false;
	}
	
	/**
	 * @return the isSuperUser
	 */
	public boolean isSuperUser() {
		if(this.isSuperUser==null){
			try{
				this.isSuperUser = new Boolean(SecurityHelper.getAAManagementClient().getPort().isSuperUser());
			} catch (RemoteException re){
				LOG.warn("Can not verify user's privileges; administration privileges denied");
				this.isSuperUser = new Boolean(false);
			}
		}
		return this.isSuperUser.booleanValue();
	}
}
