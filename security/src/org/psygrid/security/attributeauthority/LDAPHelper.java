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


//Created on Oct 11, 2005 by John Ainsworth
package org.psygrid.security.attributeauthority;

import java.io.IOException;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.security.LDAPDirectoryVendors;
import org.psygrid.security.LDAPPasswordHashScheme;
import org.psygrid.security.utils.PasswordUtilities;

/**
 * LDAP helper methods.
 * 
 * @author Terry Child
 */

public class LDAPHelper {
	
	/** Logger */
	private static Log log = LogFactory.getLog(LDAPHelper.class);

	/*
	 * The following are injected in the application context.
	 */
	
	String ldapDir = null;

	String ldapRoot = null;

	String ldapRootPassword = null;
	
	String ldapBaseDN = null;
	
	String ldapUserBaseDN = null;
	
	String ldapPasswordHash = null;
	
	String ldapVendor = null;
	
	boolean ldapUseTLS = false;
	

	public LDAPHelper() {
	}
	
	/**
	 * @param ldapDir the ldapDir to set
	 */
	public void setLdapDir(String ldapDir) {
		this.ldapDir = ldapDir;
	}


	/**
	 * @param ldapRoot the ldapRoot to set
	 */
	public void setLdapRoot(String ldapRoot) {
		this.ldapRoot = ldapRoot;
	}


	/**
	 * @param ldapRootPassword the ldapRootPassword to set
	 */
	public void setLdapRootPassword(String ldapRootPassword) {
		this.ldapRootPassword = ldapRootPassword;
	}


	/**
	 * @param ldapBaseDN the ldapBaseDN to set
	 */
	public void setLdapBaseDN(String ldapBaseDN) {
		this.ldapBaseDN = ldapBaseDN;
	}


	/**
	 * @param ldapUserBaseDN the ldapUserBaseDN to set
	 */
	public void setLdapUserBaseDN(String ldapUserBaseDN) {
		this.ldapUserBaseDN = ldapUserBaseDN;
	}


	/**
	 * @param ldapPasswordHash the ldapPasswordHash to set
	 */
	public void setLdapPasswordHash(String ldapPasswordHash) {
		this.ldapPasswordHash = ldapPasswordHash;
	}
	
	/**
	 * @param ldapVendor the ldapVendor to set
	 */
	public void setLdapVendor(String ldapVendor) {
		this.ldapVendor = ldapVendor;
	}

	/**
	 * @param ldapUseTLS the ldapUseTLS to set
	 */
	public void setLdapUseTLS(boolean ldapUseTLS) {
		this.ldapUseTLS = ldapUseTLS;
	}


	private LdapContext createLDAPContext(String cid, char[] password) throws NamingException,IOException {

		Hashtable<String, Object> env = new Hashtable<String, Object>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapDir);
		LdapContext ctx = null;
		
		if(ldapUseTLS){
			
			StartTlsResponse tls = null;			
			ctx = new InitialLdapContext(env, null);
			tls = (StartTlsResponse) ctx.extendedOperation(new StartTlsRequest());
			tls.negotiate();
			ctx.addToEnvironment(Context.SECURITY_AUTHENTICATION, "simple");
			ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, cid);
			ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
			ctx.reconnect(null);
		
		} else {
			
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			env.put(Context.SECURITY_PRINCIPAL, cid);
			env.put(Context.SECURITY_CREDENTIALS, password);
			ctx = new InitialLdapContext(env, null);	
		
		}
		return ctx;
	}
		
	private void closeLDAPContext(LdapContext ctx){
		try {
			if(ctx != null){
				ctx.close();
			}
		} catch (NamingException ne) {
			log.info("LDAP context close failure");
		}
	}

	/**
	 * Look up a LDAP distinguished name (DN) given a login name.
	 * 
	 * @param userID the login name
	 * @return the LDAP DN or null if non exists.
	 */
	public String getUserDN(String userID) {
		Attributes matchAttrs = new BasicAttributes(true); // ignore attribute name case
		matchAttrs.put(new BasicAttribute("uid", userID));
		String filter = "(uid="+userID+")";
		SearchControls searchCtrls = new SearchControls();
		searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String dn = null;
		LdapContext ctx = null;
		try {
			ctx = createLDAPContext(ldapRoot, ldapRootPassword.toCharArray());		
			NamingEnumeration<SearchResult> answer = ctx.search(ldapBaseDN, filter, searchCtrls);
			while(answer.hasMoreElements()){
				dn = answer.next().getNameInNamespace();
				dn = dn.replace(",", ", ");
				dn = dn.replace("cn=", "CN=");
				dn = dn.replace("ou=", "OU=");
				dn = dn.replace("o=", "O=");
				dn = dn.replace("c=", "C=");
				
				//there should only be one!
				break;
			}
		} catch (NamingException ne) {
			log.error(ne.getMessage(),ne);
		} catch (IOException io) {
			log.error("Error communicating with LDAP server",io);
		} finally {
			closeLDAPContext(ctx);
		}
		return dn;
	}
	
	/**
	 * Returns the email address for a user,
	 * @param userDN the DN
	 * @return email address or null if non exists
	 */
	public String getUserEmailAddress(String userDN){
		String email = null;
		String[] attrIDs = { "mail"};
		LdapContext ctx = null;
		try {
			ctx = createLDAPContext(ldapRoot, ldapRootPassword.toCharArray());
			Attributes attrs = ctx.getAttributes(userDN, attrIDs);
			if (attrs.get("mail") != null) {
				email = (String) attrs.get("mail").get();
			} 
		} catch (NamingException ne) {
			log.error(ne.getMessage(),ne);
		} catch (IOException io) {
			log.error("Error communicating with LDAP server",io);
		} finally {
			closeLDAPContext(ctx);
		}
		return email;
	}
	
	/**
	 * Saves a new password in the ldap directory and returns the saved value.
	 * 
	 * The returned value may be a hash of the supplied password.
	 * 
	 * @param userDN the user name
	 * @param password the new plain text password
	 * @return the saved password as either plain text or a hash value or null if the password could not be saved.
	 */
	public String setPassword(String userDN, String password) {
		
			char[] passwordChars = password.toCharArray();

			ModificationItem[] mods = new ModificationItem[1];
			
			if(ldapPasswordHash!=null){
				if(ldapPasswordHash.equals(LDAPPasswordHashScheme.SHA.toString())){
					passwordChars = PasswordUtilities.hashPassword(passwordChars, LDAPPasswordHashScheme.SHA, log);
				}
			}
		
			if(ldapVendor.equals(LDAPDirectoryVendors.MICROSOFT.toString())){
				BasicAttribute unicodePwd = new BasicAttribute("unicodePwd");
				unicodePwd.add(PasswordUtilities.UTFPassword(new String(passwordChars)));
				mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, unicodePwd);
			} else {
				mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
						new BasicAttribute("userPassword", new String(passwordChars)));				
			}
			
			LdapContext ctx = null;
			try {
				ctx = createLDAPContext(ldapRoot, ldapRootPassword.toCharArray());
				ctx.modifyAttributes(userDN, mods);
				return new String(passwordChars);
			} catch (NamingException ne) {
				log.error(ne.getMessage(),ne);
			} catch (IOException io) {
				log.error("Error communicating with LDAP server",io);
			} finally {
				closeLDAPContext(ctx);
			}

			return null;
	}

}
