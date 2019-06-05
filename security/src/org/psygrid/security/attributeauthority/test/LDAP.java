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


//Created on Mar 13, 2006 by John Ainsworth



package org.psygrid.security.attributeauthority.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;
import javax.net.ssl.SSLSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;

/**
 * @author jda
 *
 */
public class LDAP {
	/**
	 * logger
	 */
	private static Log log = LogFactory.getLog(AAQueryClient.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println(TLSperformLDAPAuthentication("cn=John Ainsworth, ou=users, o=opencdms, c=uk", "booboo".toCharArray()));

		System.out.println(performLDAPAuthentication("cn=John Ainsworth, ou=users, o=mhrn psygrid, c=uk", "1f0tcn-JN".toCharArray()));

		System.out.println(checkUID("JohnAinsworth"));
//		try{
//		AAQueryClient aaqc = new AAQueryClient("test.properties");
//		
//		aaqc.changePassword(null, "sausages".toCharArray(), "password".toCharArray());	
//		
//		} catch(Exception e){
//			System.out.println(e.getMessage());
//			e.printStackTrace();
//		}
		
//		String cid = getCallersIdentityByUID("cRoONe");
//		System.out.println(cid);
//		
//		System.out.println(checkUID("rob"));
//		System.out.println(checkUID("Rob"));
//		System.out.println(checkUID("CROOne"));
//		System.out.println(checkUID("jda"));
		//performLDAPAuthentication(cid, "sausages".toCharArray());
		
//		String ldapDir = "ldap://atisha.smb.man.ac.uk:389/O=PsyGrid,C=UK";
//		Hashtable env = new Hashtable();
//		env.put(Context.INITIAL_CONTEXT_FACTORY,
//				"com.sun.jndi.ldap.LdapCtxFactory");
//		env.put(Context.PROVIDER_URL, ldapDir);
//		Attributes matchAttrs = new BasicAttributes(true);
//		matchAttrs.put(new BasicAttribute("uniqueMember", "cn=John Ainsworth,ou=users,o=psygrid,c=uk"));
//	
////		 Specify the ids of the attributes to return
//		//String[] attrIDs = {"sn", "telephonenumber", "golfhandicap", "mail"};
//		SearchControls ctls = new SearchControls();
//		//ctls.setReturningAttributes(attrIDs);
//		ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
//
////		 Specify the search filter to match
////		 Ask for objects that have the attribute "sn" == "Geisel"
////		 and the "mail" attribute
////		String filter = "(uniqueMember=cn=John Ainsworth,ou=users,o=psygrid,c=uk)";
//		String filter = "(cn=*)";
////		 Search the subtree for objects by using the filter
//		
//		try {
//			DirContext ctx = new InitialDirContext(env);
//			//NamingEnumeration answer = ctx.search("OU=Outlook,OU=North West",matchAttrs);
//			NamingEnumeration answer = ctx.search("ou=users", filter, ctls);
//			
//			while (answer.hasMore()) {
//			    SearchResult sr = (SearchResult)answer.next();
//			    System.out.println(">>>" + sr.getName());
//			    System.out.println(sr.getAttributes().get("uid").get());
//			}
//		} catch (NamingException ne) {
//			log.info(ne.getMessage());
//		}
	}

	public List<InternetAddress> lookUpEmailAddress(ProjectType pt, GroupType gt, RoleType rt){
		String[] users = null;
		List<InternetAddress> lia = new ArrayList<InternetAddress>();
//		if(pt != null){
//			if(gt != null){
//				if (rt != null) {
//					users = getUsersInGroupInProjectWithRole(pt, gt, rt);
//				} else {
//					users = getUsersInGroupInProject(pt, gt);
//				}
//			} else {
//				if (rt != null) {
//					users = getUsersInProjectWithRole(pt, rt);
//				} else {
//					users = getUsersInProject(pt);
//				}
//			}
//		}
		String ldapDir = "ldap://atisha.smb.man.ac.uk:389/O=PsyGrid,C=UK";
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapDir);
		try {
			DirContext ctx = new InitialDirContext(env);
			for (int i = 0; i < users.length; i++) {
				try{
					Attributes attrs = ctx.getAttributes("uid=" + users[i]);
					String mail = (String) attrs.get("mail").get();
					try {
						lia.add(new InternetAddress(mail));
					} catch (AddressException ae) {
						log.info("invalid email address found " + mail
								+ " for user " + users[i]);
					}				
				} catch (NamingException ne) {
					log.info(ne.getMessage());
				}				
			}
		} catch (NamingException ne) {
			log.info(ne.getMessage());
		} 
		return lia;
	}	
	static public String getCallersIdentityByUID(String uid){
		String ldapDir = "ldap://mhrn.psygrid.org:389/";
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapDir);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, "CN=root,O=MHRN PsyGrid,C=UK");
		env.put(Context.SECURITY_CREDENTIALS, "TRy6-kW3");
		Attributes matchAttrs = new BasicAttributes(true); // ignore attribute name case
		matchAttrs.put(new BasicAttribute("uid", uid));
		String filter = "(uid="+uid+")";
		SearchControls searchCtrls = new SearchControls();
		searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String result = null;
		try {
			DirContext ctx = new InitialDirContext(env);			
			NamingEnumeration answer = ctx.search("O=mhrn psygrid,C=UK", filter, searchCtrls);
			while(answer.hasMoreElements()){
				result = ((SearchResult)answer.next()).getNameInNamespace();
				//there should only be one!
				System.out.println(result);
				//break;
			}
		} catch (NamingException ne) {
			log.error(ne.getMessage());
		}
		return result;
	}
	public static boolean performLDAPAuthentication(String cid, char[] password){
		String ldapDir = "ldap://mhrn.psygrid.org:389/O=MHRN PsyGrid,C=UK";
		boolean result = false;
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapDir);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, cid);
		env.put(Context.SECURITY_CREDENTIALS, password);
		
		try{
			DirContext ctx = new InitialDirContext(env);	
			result = true;
		} catch (NamingException ne){
			System.out.println("Failed to bind to LDAP server for "+cid);
		}	
		return result;
	}
	static public boolean checkUID(String uid) {
		String ldapDir = "ldap://mhrn.psygrid.org:389";
		boolean result = false;
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapDir);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, "CN=root,O=MHRN PsyGrid,C=UK");
		env.put(Context.SECURITY_CREDENTIALS, "TRy6-kW3");
		try {
			DirContext ctx = new InitialDirContext(env);

			try {
				String cn = getCallersIdentityByUID(uid);
				String[] attrIDs = { "uid" };
				Attributes attrs = ctx.getAttributes(cn, attrIDs);
				String userId = (String) attrs.get("uid").get();
				if (userId.equals(uid)) {
					result = true;
				}
			} catch (NamingException ne) {
				log.info(ne.getMessage());
			}

		} catch (NamingException ne) {
			log.info(ne.getMessage());
		}
		return result;
	}
	
	public static boolean TLSperformLDAPAuthentication(String cid, char[] password){
		String ldapDir = "ldap://localhost:389/O=opencdms,C=UK";
		boolean result = false;
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
		"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapDir);
//		env.put(Context.SECURITY_AUTHENTICATION, "simple");
//		env.put(Context.SECURITY_PRINCIPAL, cid);
//		env.put(Context.SECURITY_CREDENTIALS, password);

		LdapContext ctx = null;
		StartTlsResponse tls = null;

		try {
			ctx = new InitialLdapContext(env, null);
			tls = (StartTlsResponse) ctx
			.extendedOperation(new StartTlsRequest());
			SSLSession sess = tls.negotiate();
			ctx.addToEnvironment(Context.SECURITY_AUTHENTICATION, "simple");
			ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, cid);
			ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
			
			ctx.reconnect(null);
			
			result = true;

		} catch (NamingException ne) {
			log.info("Failed to bind to LDAP server for " + cid);
		} catch (IOException io) {
			log.error("Failed TLS negotiation with LDAP server");
		} finally {
			try {
				tls.close();
				ctx.close();

			} catch (NamingException ne) {
				log.info("LDAP context close failure");
			} catch (IOException io) {
				log.info("LDAP TLS close failure");
			}
		}
		return result;
	}
}
