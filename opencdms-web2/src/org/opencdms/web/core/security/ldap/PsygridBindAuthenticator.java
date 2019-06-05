package org.opencdms.web.core.security.ldap;

import org.acegisecurity.ldap.InitialDirContextFactory;
import org.acegisecurity.providers.ldap.authenticator.BindAuthenticator;
import org.acegisecurity.userdetails.ldap.LdapUserDetails;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.security.LDAPPasswordHashScheme;
import org.psygrid.security.utils.PasswordUtilities;

public class PsygridBindAuthenticator extends BindAuthenticator {
	
	private String ldapPasswordHash = null;
    private static final Log logger = LogFactory.getLog(BindAuthenticator.class);
    
    public PsygridBindAuthenticator(InitialDirContextFactory initialDirContextFactory) {
        super(initialDirContextFactory);
    }
    
    public LdapUserDetails authenticate(String username, String password) {
		if(ldapPasswordHash!=null){
			if(ldapPasswordHash.equals(LDAPPasswordHashScheme.SHA.toString())){
			   	password = new String(PasswordUtilities.hashPassword(password.toCharArray(), LDAPPasswordHashScheme.SHA, logger));
			}
		}
     	return super.authenticate(username, password);
    }

	/**
	 * @return the ldapPasswordHash
	 */
	public String getLdapPasswordHash() {
		return ldapPasswordHash;
	}

	/**
	 * @param ldapPasswordHash the ldapPasswordHash to set
	 */
	public void setLdapPasswordHash(String ldapPasswordHash) {
		this.ldapPasswordHash = ldapPasswordHash;
	}

}
