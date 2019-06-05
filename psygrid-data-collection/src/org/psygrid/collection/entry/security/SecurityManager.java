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


package org.psygrid.collection.entry.security;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
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

import org.apache.axis.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.EntryHelper;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.ui.ForceChangePasswordDialog;
import org.psygrid.common.security.LoginInterfaceFrame;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.RBACAction;
import org.psygrid.security.attributeauthority.client.AAManagementClient;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.attributeauthority.types.PostProcessLoginResponseType;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.security.policyauthority.client.PAManagementClient;
import org.psygrid.security.policyauthority.client.PAQueryClient;
import org.psygrid.security.policyauthority.service.InputFaultMessage;
import org.psygrid.security.policyauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.security.policyauthority.service.ProcessingFaultMessage;
import org.psygrid.www.xml.security.core.types.ActionType;
import org.psygrid.www.xml.security.core.types.AllowedType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectActionType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.TargetType;

public class SecurityManager {

	private String userName;

	private String encryptedPassword;
	private SecretKeySpec keySpec;

	private AAQueryClient aaClient;
	private AAManagementClient aaMgmtClient;

	private LoginClient loginClient;
	private PAQueryClient paClient;
	private PAManagementClient paMgmtClient;

	private Date keyValidity = null;

    private LoginStatus loginStatus = LoginStatus.PreInitialLogin;
    private LastLoginAttemptResult lastLoginResult = LastLoginAttemptResult.NotApplicable;
    
	private static final SecurityManager INSTANCE = new SecurityManager();
	private static final Log LOG = LogFactory.getLog(SecurityManager.class);

    public enum LoginStatus{
    	PreInitialLogin,
    	PostInitialLogin
    }
    
    public enum LastLoginAttemptResult{
    	NotApplicable,
    	Success,
    	ProxyFailed,
    	NoConnection,
    	WrongCredentials,
    	DifferentUserNameFromInitialLogin
    }
    
	private SecurityManager()  {
		Security.addProvider(new BouncyCastleProvider());
		setProperties();
	}
    
    public LoginStatus getLoginStatus(){
    	return loginStatus;
    }
    
	public String getVersion() throws ConnectException, RemoteServiceFault   {
        if (loginClient == null) {
            loginClient = LoginClient.getInstance();
        }
		return loginClient.getVersion();
	}

	public String getUserName() {
		return userName;
	}
	
	private void setProperties() {
		final String TRUST_STORE_LOCATION = 
			PersistenceManager.getInstance().getTrustStoreLocation();
		System.setProperty("javax.net.ssl.trustStore", TRUST_STORE_LOCATION); //$NON-NLS-1$
		System.setProperty("javax.net.ssl.trustStorePassword",  //$NON-NLS-1$
				SecurityHelper.getTrustStorePassword());
		System.setProperty("javax.net.ssl.keyStore",  //$NON-NLS-1$
				PersistenceManager.getInstance().getKeyStoreLocation());
		System.setProperty("javax.net.ssl.keyStorePassword", SecurityHelper.getKeyStorePassword()); //$NON-NLS-1$ 
		System.setProperty("org.apache.axis.components.net.SecureSocketFactory", //$NON-NLS-1$
				PsyGridClientSocketFactory.class.getName());
		//System.setProperty("javax.net.debug", "ssl,handshake");
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
	throws ConnectException, AuthenticationException, AccountLockedException, AccountDormantException, RemoteServiceFault,
	IOException {
		// If the current keystore has expired, then to log back in
		// we will need the default keystore
		// Bug#487
		KeyStore ks = null;
		InputStream fis = null;
		Certificate[] certs = null;
		X509Certificate x509 = null;
		try {
			ks = KeyStore.getInstance("JKS"); //$NON-NLS-1$
			fis = PersistenceManager.getInstance().loadKeyStore();
			ks.load(fis, System.getProperty("javax.net.ssl.keyStorePassword").toCharArray());
			certs = ks.getCertificateChain(SecurityHelper.getKeyStoreAlias()); //$NON-NLS-1$
			// Only support X.509
			x509 = (X509Certificate) certs[0];
			x509.checkValidity();
		} catch (CertificateExpiredException cee) {
			//reinstall the default and continue
			PersistenceManager.getInstance().restoreDefaultKeystore();
			//Force the new key in over the existing key
			System.setProperty("javax.net.ssl.keyStorePassword", SecurityHelper.getKeyStorePassword()); //$NON-NLS-1$ 
			PsyGridClientSocketFactory.reinit();
		} catch (CertificateNotYetValidException cnyve) {
			throw new AuthenticationException(cnyve.getMessage(), cnyve);
		} catch (KeyStoreException kse) {
			throw new AuthenticationException(kse.getMessage(), kse);
		} catch (CertificateException ce) {
			throw new AuthenticationException(ce.getMessage(), ce);
		} catch (NoSuchAlgorithmException nsae) {
			ExceptionsHelper.handleFatalException(nsae);
		} 
		finally{
			//release resources
			x509 = null;
			certs = null;
			fis.close();
			fis = null;
			ks = null;
		}

		loginClient.login(userName, password);
		int index = updateLocalUsers(password);
		PersistenceManager.getInstance().initUserLocations(String.valueOf(index));
	}

	/**
	 * @param password
	 * @return index of user in users list.
	 * @throws IOException
	 */
	private int updateLocalUsers(char[] password) throws IOException {
		List<User> users = PersistenceManager.getInstance().loadUsers();
		String hashedPassword = SecurityHelper.hash(password);
		String hashedUserName = SecurityHelper.hash(userName.toCharArray());
		int index = checkUser(users, hashedUserName, hashedPassword);
		if (index != -1) {
			return index;
		}
		users.add(new User(hashedUserName, hashedPassword));
		PersistenceManager.getInstance().saveUsers(users);
		return users.size() - 1;
	}


	public String getSAMLAssertion(ProjectType project) throws 
	RemoteServiceFault, ConnectException, IOException,
	EntrySAMLException, NotAuthorisedFault  {

		try {
			return aaClient.getSAMLAssertion(project).toString();
		} catch (PGSecuritySAMLVerificationException e) {
			throw new EntrySAMLException(e);
		} catch (PGSecurityInvalidSAMLException e) {
			throw new EntrySAMLException(e);
		} catch (PGSecurityException e) {
			throw new RemoteServiceFault(e);
		} catch (org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage e) {
			throw new NotAuthorisedFault(e);
		}
	}

	public String getSAMLAssertion() throws 
	RemoteServiceFault, ConnectException, IOException,
	EntrySAMLException, NotAuthorisedFault  {

		try {
			return aaClient.getSAMLAssertion().toString();
		} catch (PGSecuritySAMLVerificationException e) {
			throw new EntrySAMLException(e);
		} catch (PGSecurityInvalidSAMLException e) {
			throw new EntrySAMLException(e);
		} catch (PGSecurityException e) {
			throw new RemoteServiceFault(e);
		} catch (org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage e) {
			throw new NotAuthorisedFault(e);
		}
	}

	public List<ProjectType> getUserProjects() throws ConnectException, 
	EntrySAMLException, RemoteServiceFault, NotAuthorisedFault  {
		try {
			return aaClient.getMyProjects();
		} catch (PGSecuritySAMLVerificationException e) {
			throw new EntrySAMLException(e);
		} catch (PGSecurityInvalidSAMLException e) {
			throw new EntrySAMLException(e);
		} catch (PGSecurityException e) {
			throw new RemoteServiceFault(e);
		} catch (org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage e) {
			throw new NotAuthorisedFault(e);
		}
	}

	public List<GroupType> getUsersGroupsInProject(ProjectType project) 
	throws ConnectException, EntrySAMLException, RemoteServiceFault, 
	NotAuthorisedFault {
		try {
			return aaClient.getMyGroupsInProject(project);
		} catch (PGSecuritySAMLVerificationException e) {
			throw new EntrySAMLException(e);
		} catch (PGSecurityInvalidSAMLException e) {
			throw new EntrySAMLException(e);
		} catch (PGSecurityException e) {
			throw new RemoteServiceFault(e);
		} catch (org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage e) {
			throw new NotAuthorisedFault(e);
		}
	}

	public boolean changePassword(char[] newPassword) throws ConnectException,
	EntrySAMLException, RemoteServiceFault, NotAuthorisedFault {
		try {
			boolean rc = aaClient.changePassword(newPassword, getPassword());

			// delete out of date files
			//It is safe to do this as we only allow a password change if
			//all local records have been committed, which will result in
			//both the records list and esl subjects lists being empty
			PersistenceManager.getInstance().deleteRecordsList();
			PersistenceManager.getInstance().deleteEslSubjectsList();

			// Update user list
			List<User> lu = PersistenceManager.getInstance().loadUsers();
			String hashedUserName = SecurityHelper.hash(userName.toCharArray());
			String hashedPassword = SecurityHelper.hash(getPassword());
			int location = checkUser(lu, hashedUserName, hashedPassword);
			lu.remove(location);
			lu.add(location, new User(hashedUserName, SecurityHelper
					.hash(newPassword)));
			PersistenceManager.getInstance().saveUsers(lu);
			encryptedPassword = SecurityHelper.encrypt(newPassword, keySpec);
			return rc;
		} catch (PGSecurityException e) {
			throw new RemoteServiceFault(e);
		} catch (org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage e) {
			throw new NotAuthorisedFault(e);
		} catch (GeneralSecurityException e) {
			// Should not happen
			ExceptionsHelper.handleFatalException(e);
		} catch (IOException ioe) {
			// Should not happen
			ExceptionsHelper.handleFatalException(ioe);
		} finally {
			SecurityHelper.clearCharArray(newPassword);
		}
		return false;
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
	throws RemoteException, AuthenticationException, AccountLockedException, IOException,
	RemoteServiceFault, FirstLoginNoConnectionException, AccountDormantException {

    	if(loginStatus == LoginStatus.PostInitialLogin) {
    		if(user.equals(userName)) {
    			String newPassword = new String(password);
    			String oldPassword = new String(getPassword());
    			if(newPassword.equals(oldPassword)) {
    				lastLoginResult = LastLoginAttemptResult.Success;
    				return true;
    			}else {
    				lastLoginResult = LastLoginAttemptResult.WrongCredentials;
    				return false;
    			}
    		}else {
    			lastLoginResult = LastLoginAttemptResult.DifferentUserNameFromInitialLogin;
    			return false;
    		}
    		
    	}else{
	    	this.userName = user;
	        try {
	            keySpec = SecurityHelper.getRandomKeySpec();
	
	            if (loginClient == null) {
	                loginClient = LoginClient.getInstance();
	            }
	            
	            if (skipLogin) {
	                if (LOG.isDebugEnabled()) {
	                    LOG.debug("Login skipped");
	                }
	                
	                PersistenceManager.getInstance().initUserLocations(userName);
	            }
	            else {
	                boolean connectionAvailable = RemoteManager.getInstance().isConnectionAvailable();
	                if (LOG.isDebugEnabled()) {
	                    LOG.debug("Login - connectionAvailable="+connectionAvailable);
	                }
	                if (connectionAvailable) {
	                    try {
	                        keyValidity = refreshKey(password);
	                        if (LOG.isDebugEnabled()) {
	                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy 'at' HH:mm:ss z"); //$NON-NLS-1$
	                            LOG.debug("keyValidity: " + dateFormat.format(keyValidity)); //$NON-NLS-1$
	                        }
	                    }
	                    catch (ConnectException re) {
	                        if (LOG.isDebugEnabled()) {
	                            LOG.debug("Failed to do remote login", re); //$NON-NLS-1$
	                        }
	                        doLocalLogin(password);
	                    }
	                    
	                } else  {
	                	doLocalLogin(password);
	                }
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
	            SecurityHelper.wrapIntoRuntimeExceptionAndThrow(e);
	
	        }
	        finally {
	            SecurityHelper.clearCharArray(password);
	        }
	
	        // If no exception is thrown, then authentication is successful
	        loginStatus = LoginStatus.PostInitialLogin;
	        lastLoginResult = LastLoginAttemptResult.Success;
	        return true;
    	}
	}

	public void login(String user, char[] password) throws RemoteServiceFault,
	RemoteException, AuthenticationException, AccountLockedException, AccountDormantException, IOException, FirstLoginNoConnectionException {
		login(user, password, false);
	}

	public Date getKeyValidity() {
		return keyValidity;
	}

	private int checkUser(List<User> users, String hashedUserName, 
			String hashedPassword) {

		User currentUser = new User(hashedUserName, hashedPassword);

		for (int i = 0, c = users.size(); i < c; ++i) {
			User user = users.get(i);
			if (currentUser.equals(user)) {
				return i;
			}
		}
		return -1;
	}

	private void doLocalLogin(char[] password) throws IOException, 
	AuthenticationException, FirstLoginNoConnectionException {
		if ( LOG.isDebugEnabled() ){
			LOG.debug("Performing local login for user '"+userName+"'");
		}
		List<User> users = PersistenceManager.getInstance().loadUsers();

		if ( null == users || users.size()<1 ){
			//No connection, and no users in the user list i.e.
			//there have not been any previous successful logins.
			//Assume that the proxy has not been configured
			throw new FirstLoginNoConnectionException("Local login with no users in the user list");
		}

		String hashedPassword = SecurityHelper.hash(password);
		String hashedUserName = SecurityHelper.hash(userName.toCharArray());

		int index = checkUser(users, hashedUserName, hashedPassword);
		if (index == -1) {
			if ( LOG.isDebugEnabled() ){
				LOG.debug("Logal login for user '"+userName+"' failed");
			}
			throw new AuthenticationException("Either the user does not exist or the password is incorrect");
		}
		PersistenceManager.getInstance().initUserLocations(String.valueOf(index));
	}

	public String encrypt(String clearText) {
		checkText(clearText);
		char[] password = getPassword();
		String cipherText = null;
		try {
			cipherText = SecurityHelper.encrypt(clearText, password);
		} catch (GeneralSecurityException e) {
			SecurityHelper.wrapIntoRuntimeExceptionAndThrow(e);
		}
		SecurityHelper.clearCharArray(password);
		return cipherText;
	}

	private Date refreshKey(char[] password) throws AuthenticationException, 
	AccountLockedException, AccountDormantException, ConnectException, IOException, RemoteServiceFault {
		KeyStore ks = null;
		InputStream fis = null;
		Certificate[] certs = null;
		X509Certificate x509 = null;
		Date validity = null;
		try {
			doRemoteLogin(password);
			long localTime = System.currentTimeMillis();
			ks = KeyStore.getInstance("JKS"); //$NON-NLS-1$
			fis = PersistenceManager.getInstance().loadKeyStore();
			ks.load(fis, password);
			certs = ks.getCertificateChain(SecurityHelper.getKeyStoreAlias()); //$NON-NLS-1$
			// Only support X.509
			x509 = (X509Certificate) certs[0];
			validity = new Date(localTime + (x509.getNotAfter().getTime() - x509.getNotBefore().getTime()));

		} catch (KeyStoreException kse) {
			throw new AuthenticationException(kse.getMessage(), kse);
		} catch (CertificateException ce) {
			throw new AuthenticationException(ce.getMessage(), ce);
		} catch (NoSuchAlgorithmException nsae) {
			ExceptionsHelper.handleFatalException(nsae);
		}
		finally{
			//release resources
			x509 = null;
			certs = null;
			if(fis != null){
				fis.close();
				fis = null;		
			}
	
			ks = null;
		}
		//Should never happen
		return validity;
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
	RemoteServiceFault   {
		char[] password = null;
		try {
			password = getPassword();
			keyValidity = refreshKey(password);
		}
		// Should never happen if used after login
		catch (AuthenticationException e) {
			SecurityHelper.wrapIntoRuntimeExceptionAndThrow(e);
		} catch (AccountLockedException e) {
			SecurityHelper.wrapIntoRuntimeExceptionAndThrow(e);
		} catch (AccountDormantException e) {
			SecurityHelper.wrapIntoRuntimeExceptionAndThrow(e);
		}
		finally {
			if (password != null) {
				SecurityHelper.clearCharArray(password);
				password = null;
			}
		}
		return keyValidity;
	}

	private ActionType getCanLoadPendingDocumentsActionType() {
		return RBACAction.getRecordsByGroupsAndDocStatus_Pending.toActionType();
	}

	public boolean autoGenerateIdentifiers(ProjectType project, GroupType group,
			String saml) throws ConnectException, NotAuthorisedFault, 
			IOException, RemoteServiceFault {
		try {
			return paClient.getPort().makePolicyDecision(
					project,
					new TargetType(group.getName(), group.getIdCode()),
					getAutoGenerateIdsAction(), saml);


		} catch (NotAuthorisedFaultMessage nafm) {
			throw new NotAuthorisedFault(nafm);
		} catch (ProcessingFaultMessage pfm) {
			throw new RemoteServiceFault(pfm);
		} catch (InputFaultMessage ifm) {
			SecurityHelper.wrapIntoRuntimeExceptionAndThrow(ifm);
		} catch (AxisFault af) {
			Throwable cause = af.getCause();
			if (cause instanceof ConnectException) {
				throw (ConnectException) cause;
			}
			else if (cause instanceof UnknownHostException || 
					cause instanceof NoRouteToHostException) {
				ConnectException ce = new ConnectException(cause.getMessage());
				ce.initCause(cause);
				throw ce;
			}
			else {
				throw new RuntimeException(af);
			}
		}
		catch (RemoteException re) {
			throw new RuntimeException(re);
		}
		return false;
	}

	private ActionType getAutoGenerateIdsAction() {
		return RBACAction.ACTION_DCC_AUTO_GENERATE_IDENTIFIERS.toActionType();
	}

	public boolean canLoadPendingDocuments() throws ConnectException, 
	EntrySAMLException, RemoteServiceFault, NotAuthorisedFault, 
	IOException {
		try {
			for (ProjectType project : getUserProjects()) {
				String saml = getSAMLAssertion(project);
				for (GroupType group : getUsersGroupsInProject(project)) {
					boolean canLoad = paClient.getPort().makePolicyDecision(
							project,
							new TargetType(group.getName(), group.getIdCode()),
							getCanLoadPendingDocumentsActionType(), saml);
					if (canLoad) {
						return true;
					}
				}
			}
		} catch (NotAuthorisedFaultMessage nafm) {
			throw new NotAuthorisedFault(nafm);
		} catch (InputFaultMessage ifm) {
			SecurityHelper.wrapIntoRuntimeExceptionAndThrow(ifm);
		} catch (AxisFault af) {
			Throwable cause = af.getCause();
			if (cause instanceof ConnectException) {
				throw (ConnectException) cause;
			}
			else if (cause instanceof UnknownHostException || 
					cause instanceof NoRouteToHostException) {
				ConnectException ce = new ConnectException(cause.getMessage());
				ce.initCause(cause);
				throw ce;
			}
			else {
				throw new RuntimeException(af);
			}
		}
		catch (RemoteException re) {
			throw new RuntimeException(re);
		}
		return false;
	}

	public boolean canLoadPendingDocuments(String projectCode, String groupCode) throws ConnectException, 
	EntrySAMLException, RemoteServiceFault, NotAuthorisedFault, 
	IOException {
		try {
			PersistenceManager pManager = PersistenceManager.getInstance();
			ProjectType project = pManager.getData().getProject(projectCode).getProject();
			String saml = getSAMLAssertion(project);
			for (GroupType group : getUsersGroupsInProject(project)) {
				if ( group.getIdCode().equals(groupCode)){
					boolean canLoad = paClient.getPort().makePolicyDecision(
							project,
							new TargetType(group.getName(), group.getIdCode()),
							getCanLoadPendingDocumentsActionType(), saml);
					if (canLoad) {
						return true;
					}
				}
			}

		} catch (NotAuthorisedFaultMessage nafm) {
			throw new NotAuthorisedFault(nafm);
		} catch (InputFaultMessage ifm) {
			SecurityHelper.wrapIntoRuntimeExceptionAndThrow(ifm);
		} catch (AxisFault af) {
			Throwable cause = af.getCause();
			if (cause instanceof ConnectException) {
				throw (ConnectException) cause;
			}
			else if (cause instanceof UnknownHostException || 
					cause instanceof NoRouteToHostException) {
				ConnectException ce = new ConnectException(cause.getMessage());
				ce.initCause(cause);
				throw ce;
			}
			else {
				throw new RuntimeException(af);
			}
		}
		catch (RemoteException re) {
			throw new RuntimeException(re);
		}
		return false;
	}

	public String getAaVersion() throws ConnectException {
		try{
			return aaMgmtClient.getPort().getVersion();
		} catch (AxisFault af) {
			Throwable cause = af.getCause();
			if (cause instanceof ConnectException) {
				throw (ConnectException) cause;
			}
			else if (cause instanceof UnknownHostException || 
					cause instanceof NoRouteToHostException) {
				ConnectException ce = new ConnectException(cause.getMessage());
				ce.initCause(cause);
				throw ce;
			}
			else {
				throw new RuntimeException(af);
			}
		}
		catch (RemoteException re) {
			throw new RuntimeException(re);
		}
	}

	public String getPaVersion() throws ConnectException {
		try{
			return paMgmtClient.getPort().getVersion();
		} catch (AxisFault af) {
			Throwable cause = af.getCause();
			if (cause instanceof ConnectException) {
				throw (ConnectException) cause;
			}
			else if (cause instanceof UnknownHostException || 
					cause instanceof NoRouteToHostException) {
				ConnectException ce = new ConnectException(cause.getMessage());
				ce.initCause(cause);
				throw ce;
			}
			else {
				throw new RuntimeException(af);
			}
		}
		catch (RemoteException re) {
			throw new RuntimeException(re);
		}
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

	public PostProcessLoginResponseType postProcessLogin()
	throws RemoteServiceFault, ConnectException, NotAuthorisedFault {
		try {
			return aaClient.postProcessLogin();
		} catch (PGSecurityException e) {
			throw new RemoteServiceFault(e);
		} catch (org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage e) {
			throw new NotAuthorisedFault(e);
		}
	}

	static private void forcePasswordChange(Application app) {
		String title = SecurityMessages
		.getString("SecurityManager.passwordChangeTitle"); //$NON-NLS-1$
		String message = SecurityMessages
		.getString("SecurityManager.passwordChangeMessage"); //$NON-NLS-1$
		JOptionPane.showMessageDialog(app, message, title,
				JOptionPane.ERROR_MESSAGE);
		if (EntryHelper.hasUncommittedDocs(app)) {
			title = SecurityMessages
			.getString("SecurityManager.uncommittedDocumentsTitle"); //$NON-NLS-1$
			message = SecurityMessages
			.getString("SecurityManager.uncommittedDocumentsMessage"); //$NON-NLS-1$
			JOptionPane.showMessageDialog(app, message, title,
					JOptionPane.ERROR_MESSAGE);
		} else {
			ForceChangePasswordDialog dlg = new ForceChangePasswordDialog(app);
			dlg.setVisible(true);
		}
	}

	static private void forcePasswordChange(LoginInterfaceFrame ppf) {
		String title = SecurityMessages
		.getString("SecurityManager.passwordChangeTitle"); //$NON-NLS-1$
		String message = SecurityMessages
		.getString("SecurityManager.passwordChangeMessage"); //$NON-NLS-1$
		JOptionPane.showMessageDialog(ppf, message, title,
				JOptionPane.ERROR_MESSAGE);
		ForceChangePasswordDialog dlg = new ForceChangePasswordDialog(ppf);
		dlg.setVisible(true);
	}

	public void postProcessLogin(LoginInterfaceFrame ppi){
		try {
			PostProcessLoginResponseType pplrt = SecurityManager.getInstance().postProcessLogin();
			ppi.setUserName(userName);
			String title = SecurityMessages
			.getString("SecurityManager.login.notice.title");
			StringBuilder message = new StringBuilder();
			message.append(SecurityMessages.getString("SecurityManager.login.notice.message"));
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
				message.append(SecurityMessages.getString("SecurityManager.login.notice.report")); //$NON-NLS-1$
			}
			message.append(SecurityMessages.getString("SecurityManager.login.notice.continueQuestion"));
			ppi.refreshContentPane();
			int i = JOptionPane.showConfirmDialog(ppi, message.toString(), title,
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if(i==JOptionPane.NO_OPTION){
				System.exit(0);
			}
			if(pplrt.isForcePasswordChange()){
				forcePasswordChange(ppi);
			}
		} catch (RemoteServiceFault rsf) {
			ExceptionsHelper.handleRemoteServiceFault(ppi, rsf);
		} catch (NotAuthorisedFault naf) {
			ExceptionsHelper.handleNotAuthorisedFault(ppi, naf);
		} catch (ConnectException ce) {
			ExceptionsHelper.handleConnectException(ppi, ce);
		}
	}

	/**
	 * Retrieve a list of projects (authorities) for which the user
	 * has the role of curator.
	 * 
	 * @return projects
	 * @throws ConnectException
	 * @throws EntrySAMLException
	 * @throws RemoteServiceFault
	 * @throws NotAuthorisedFault
	 * @throws IOException
	 */
	public List<String> getAuthoritiesAsDELCurator() throws ConnectException, 
	EntrySAMLException, RemoteServiceFault, NotAuthorisedFault, 
	IOException {
		List<String> projectCodes = new ArrayList<String>();
		try {
			String saml = getSAMLAssertion();
			List<ProjectActionType> lpat = new ArrayList<ProjectActionType>();
			List<ProjectType> lpt = getUserProjects();
			for(ProjectType p: lpt){
				lpat.add(new ProjectActionType(p,new ActionType[]{getCanApproveElementActionType()}));
			}
			AllowedType[] at = paClient.getPort().testPrivileges(lpat.toArray(new ProjectActionType[lpat.size()]), saml);
			
			if (at != null) {
				for(int i=0;i<at.length;i++){
					for(int j=0; j<at[i].getActionTarget().length; j++){
						if(at[i].getActionTarget(j).getAction().getName().equals(getCanApproveElementActionType().getName())
								|| at[i].getActionTarget(j).getAction().getIdCode().equals(getCanApproveElementActionType().getIdCode())){
							projectCodes.add(at[i].getProject().getIdCode());
						}
					}
				}
			}
		} catch (NotAuthorisedFaultMessage nafm) {
			throw new NotAuthorisedFault(nafm);
		} catch (InputFaultMessage ifm) {
			SecurityHelper.wrapIntoRuntimeExceptionAndThrow(ifm);
		} catch (AxisFault af) {
			Throwable cause = af.getCause();
			if (cause instanceof ConnectException) {
				throw (ConnectException) cause;
			}
			else if (cause instanceof UnknownHostException || 
					cause instanceof NoRouteToHostException) {
				ConnectException ce = new ConnectException(cause.getMessage());
				ce.initCause(cause);
				throw ce;
			}
			else {
				throw new RuntimeException(af);
			}
		}
		catch (RemoteException re) {
			throw new RuntimeException(re);
		}
		return projectCodes;
	}

	/**
	 * Retrieve a list of projects (authorities) for which the user
	 * has the role of author.
	 * 
	 * @return projects
	 * @throws ConnectException
	 * @throws EntrySAMLException
	 * @throws RemoteServiceFault
	 * @throws NotAuthorisedFault
	 * @throws IOException
	 */
	public List<String> getAuthoritiesAsDELAuthor() throws ConnectException, 
	EntrySAMLException, RemoteServiceFault, NotAuthorisedFault, 
	IOException {
		List<String> projectCodes = new ArrayList<String>();
		try {
			String saml = getSAMLAssertion();
			List<ProjectActionType> lpat = new ArrayList<ProjectActionType>();
			List<ProjectType> lpt = getUserProjects();
			for(ProjectType p: lpt){
				lpat.add(new ProjectActionType(p,new ActionType[]{getCanSaveNewElementActionType()}));
			}
			AllowedType[] at = paClient.getPort().testPrivileges(lpat.toArray(new ProjectActionType[lpat.size()]), saml);
			
			if (at != null) {
				for(int i=0;i<at.length;i++){
					for(int j=0; j<at[i].getActionTarget().length; j++){
						if(at[i].getActionTarget(j).getAction().getName().equals(getCanSaveNewElementActionType().getName())
								|| at[i].getActionTarget(j).getAction().getIdCode().equals(getCanSaveNewElementActionType().getIdCode())){
							projectCodes.add(at[i].getProject().getIdCode());
						}
					}
				}
			}
		} catch (NotAuthorisedFaultMessage nafm) {
			throw new NotAuthorisedFault(nafm);
		} catch (InputFaultMessage ifm) {
			SecurityHelper.wrapIntoRuntimeExceptionAndThrow(ifm);
		} catch (AxisFault af) {
			Throwable cause = af.getCause();
			if (cause instanceof ConnectException) {
				throw (ConnectException) cause;
			}
			else if (cause instanceof UnknownHostException || 
					cause instanceof NoRouteToHostException) {
				ConnectException ce = new ConnectException(cause.getMessage());
				ce.initCause(cause);
				throw ce;
			}
			else {
				throw new RuntimeException(af);
			}
		}
		catch (RemoteException re) {
			throw new RuntimeException(re);
		}
		return projectCodes;
	}

	public boolean canPatchStudy(DataSet ds)throws ConnectException,
	EntrySAMLException, RemoteServiceFault, NotAuthorisedFault, 
	IOException {
		ProjectType sysProject = new ProjectType("SYSTEM", "-1", null, null, false);

		try {
			String saml = getSAMLAssertion();
			boolean canSave = paClient.getPort().makePolicyDecision(
					sysProject,
					new TargetType("", ""),
					//Must be a study patcher in the SYSTEM project to patch a dataset.
					getCanPatchDataSetActionType(), saml);
			
			if(!canSave){
				return false;
			}
			
			ProjectType curProject = new ProjectType(ds.getName(), ds.getProjectCode(), null, null, false);
			
			canSave = paClient.getPort().makePolicyDecision(
					curProject,
					new TargetType("", ""),
					//Must also be a PM in the project to be patched.
					getCanPatchDataSetActionType(), saml);
			
			return canSave;
			

		} catch (NotAuthorisedFaultMessage nafm) {
			throw new NotAuthorisedFault(nafm);
		} catch (InputFaultMessage ifm) {
			SecurityHelper.wrapIntoRuntimeExceptionAndThrow(ifm);
		} catch (AxisFault af) {
			Throwable cause = af.getCause();
			if (cause instanceof ConnectException) {
				throw (ConnectException) cause;
			}
			else if (cause instanceof UnknownHostException || 
					cause instanceof NoRouteToHostException) {
				ConnectException ce = new ConnectException(cause.getMessage());
				ce.initCause(cause);
				throw ce;
			}
			else {
				throw new RuntimeException(af);
			}
		}
		catch (RemoteException re) {
			throw new RuntimeException(re);
		}
		return false;
	}
 
	
	public boolean canSaveDataSet() throws ConnectException, 
	EntrySAMLException, RemoteServiceFault, NotAuthorisedFault, 
	IOException {
		ProjectType sysProject = new ProjectType("SYSTEM", "-1", null, null, false);
		ProjectActionType[] requestList = new ProjectActionType[1];
		ProjectActionType type = new ProjectActionType(sysProject, new ActionType[]{getCanSaveNewDataSetActionType()});
		requestList[0] = type;
		try {
			String saml = getSAMLAssertion();
			boolean canSave = paClient.getPort().makePolicyDecision(
					sysProject,
					new TargetType("", ""),
					//Only PMs and sys admins can save new datasets
					getCanSaveNewDataSetActionType(), saml);
			
			return canSave;
		} catch (NotAuthorisedFaultMessage nafm) {
			throw new NotAuthorisedFault(nafm);
		} catch (InputFaultMessage ifm) {
			SecurityHelper.wrapIntoRuntimeExceptionAndThrow(ifm);
		} catch (AxisFault af) {
			Throwable cause = af.getCause();
			if (cause instanceof ConnectException) {
				throw (ConnectException) cause;
			}
			else if (cause instanceof UnknownHostException || 
					cause instanceof NoRouteToHostException) {
				ConnectException ce = new ConnectException(cause.getMessage());
				ce.initCause(cause);
				throw ce;
			}
			else {
				throw new RuntimeException(af);
			}
		}
		catch (RemoteException re) {
			throw new RuntimeException(re);
		}
		return false;
	}

	private ActionType getCanApproveElementActionType() {
		return RBACAction.ACTION_DEL_APPROVE_ELEMENT.toActionType();
	}

	private ActionType getCanSaveNewElementActionType() {
		return RBACAction.ACTION_DEL_SAVE_NEW_ELEMENT.toActionType();
	}

	private ActionType getCanSaveNewDataSetActionType() {
		return RBACAction.ACTION_AA_ADD_PROJECT.toActionType();
	}
	
	private ActionType getCanPatchDataSetActionType() {
		return RBACAction.ACTION_DR_PATCH_DATASET.toActionType();
	}
}
