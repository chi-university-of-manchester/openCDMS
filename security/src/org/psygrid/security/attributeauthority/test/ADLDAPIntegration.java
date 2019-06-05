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
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.InvalidAttributesException;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;
import javax.net.ssl.SSLSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.security.LDAPPasswordHashScheme;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.utils.PasswordUtilities;

/**
 * @author jda
 * 
 */
public class ADLDAPIntegration {
	/**
	 * logger
	 */
	private static Log log = LogFactory.getLog(AAQueryClient.class);

	private static boolean ldapUseTLS = true;
	private static String ldapDir = "ldap://opencdms1.opencdms.local:389/";
	private static String ldapRoot = "cn=Administrator,cn=users,dc=opencdms,dc=local";
	private static String ldapRootPassword = "cherry";
	private static String ldapPasswordHash = "NONE";
	private static String ldapUserBaseDN = "cn=users, dc=opencdms,dc=local";

	private class User {
	 void print(){
			System.out.println(first+" "+last+" "+mail+" "+mobile+" "+passwd+" "+uid);
		}
		String last;
		String first;
		String mail;
		String passwd;
		String mobile;
		public String uid;

	};

	private User user = new User();
	public int UF_NORMAL_ACCOUNT = 0x0200;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ADLDAPIntegration ldap = new ADLDAPIntegration();

		ldap.user.first = "Mr";
		ldap.user.last = "Big";
		ldap.user.mail = "jda@opencdms.org";
		ldap.user.mobile = "+4400000000000";
		ldap.user.passwd = "cherry";
		
		System.out.println(performLDAPAuthentication(
				"cn=Administrator, cn=users, dc=opencdms, dc=local", "cherry"
						.toCharArray()));
	
		System.out.println(ldap.addUserToAD(ldapRoot, ldapRootPassword));
		System.out.println(ldap.getUser(
				"cn=Administrator, cn=users, dc=opencdms, dc=local", "cherry",
				"cn=Mr Big, cn=users, dc=opencdms, dc=local"));		
		System.out.println(ldap.multiUserExists(
				"cn=Administrator, cn=users, dc=opencdms, dc=local", "cherry",
				"M", "B"));	
		System.out.println(TLSperformLDAPAuthentication(
				"cn=Mr Big, cn=users, dc=opencdms, dc=local", "cherry"
						.toCharArray()));

		System.out.println(checkUID("MrBig"));
		System.out.println(ldap.getUIDFromCN("cn=Administrator, cn=users, dc=opencdms, dc=local", "cherry",
				"Mr Big"));
		System.out.println(getCallersIdentityByUID("MrBig"));
		System.out.println(ldap.userExists("cn=Administrator, cn=users, dc=opencdms, dc=local", "cherry",
				"cn=Mr Big, cn=users, dc=opencdms, dc=local"));
		System.out.println(ldap.userChangePassword(
				"cn=Mr Big, cn=users, dc=opencdms, dc=local", "cherry",
				"newcherry"));
		System.out.println(TLSperformLDAPAuthentication(
				"cn=Mr Big, cn=users, dc=opencdms, dc=local", "newcherry"
						.toCharArray()));
		System.out.println(ldap.resetPassword(
				"cn=Administrator, cn=users, dc=opencdms, dc=local", "cherry",
				"oldcherry"));
		System.out.println(TLSperformLDAPAuthentication(
				"cn=Mr Big, cn=users, dc=opencdms, dc=local", "oldcherry"
						.toCharArray()));
		System.out.println(ldap.deleteUser(
				"cn=Administrator, cn=users, dc=opencdms, dc=local", "cherry",
				"cn=Mr Big, cn=users, dc=opencdms, dc=local"));	
		System.out.println(ldap.addUserToAD(ldapRoot, ldapRootPassword));
		System.out.println(TLSperformLDAPAuthentication(
				"cn=Mr Big, cn=users, dc=opencdms, dc=local", "cherry"
						.toCharArray()));
		System.out.println(ldap.getUser(
				"cn=Administrator, cn=users, dc=opencdms, dc=local", "cherry",
				"cn=Mr Big, cn=users, dc=opencdms, dc=local"));	
		ldap.user.mail = null;
		ldap.user.mobile = null;
		System.out.println(ldap.updateUser(
				"cn=Administrator, cn=users, dc=opencdms, dc=local", "cherry",
				"cn=Mr Big, cn=users, dc=opencdms, dc=local"));	
		System.out.println(ldap.getUser(
				"cn=Administrator, cn=users, dc=opencdms, dc=local", "cherry",
				"cn=Mr Big, cn=users, dc=opencdms, dc=local"));	
		ldap.user.mail = "noone@opencdms.local";
		ldap.user.mobile = null;
		System.out.println(ldap.updateUser(
				"cn=Administrator, cn=users, dc=opencdms, dc=local", "cherry",
				"cn=Mr Big, cn=users, dc=opencdms, dc=local"));	
		System.out.println(ldap.getUser(
				"cn=Administrator, cn=users, dc=opencdms, dc=local", "cherry",
				"cn=Mr Big, cn=users, dc=opencdms, dc=local"));	
		ldap.user.mail = "noone@opencdms.local";
		ldap.user.mobile = "999";
		System.out.println(ldap.updateUser(
				"cn=Administrator, cn=users, dc=opencdms, dc=local", "cherry",
				"cn=Mr Big, cn=users, dc=opencdms, dc=local"));	
		System.out.println(ldap.getUser(
				"cn=Administrator, cn=users, dc=opencdms, dc=local", "cherry",
				"cn=Mr Big, cn=users, dc=opencdms, dc=local"));	
		ldap.user.mail = "me@somewhere";
		ldap.user.mobile = "0000";
		System.out.println(ldap.updateUser(
				"cn=Administrator, cn=users, dc=opencdms, dc=local", "cherry",
				"cn=Mr Big, cn=users, dc=opencdms, dc=local"));	
		System.out.println(ldap.getUser(
				"cn=Administrator, cn=users, dc=opencdms, dc=local", "cherry",
				"cn=Mr Big, cn=users, dc=opencdms, dc=local"));	
		System.out.println(ldap.deleteUser(
				"cn=Administrator, cn=users, dc=opencdms, dc=local", "cherry",
				"cn=Mr Big, cn=users, dc=opencdms, dc=local"));	

		// try{
		// AAQueryClient aaqc = new AAQueryClient("test.properties");
		//			
		// aaqc.changePassword(null, "sausages".toCharArray(),
		// "password".toCharArray());
		//			
		// } catch(Exception e){
		// System.out.println(e.getMessage());
		// e.printStackTrace();
		// }

		// String cid = getCallersIdentityByUID("cRoONe");
		// System.out.println(cid);
		//			
		// System.out.println(checkUID("rob"));
		// System.out.println(checkUID("Rob"));
		// System.out.println(checkUID("CROOne"));
		// System.out.println(checkUID("jda"));
		// performLDAPAuthentication(cid, "sausages".toCharArray());

		// String ldapDir = "ldap://atisha.smb.man.ac.uk:389/O=PsyGrid,C=UK";
		// Hashtable env = new Hashtable();
		// env.put(Context.INITIAL_CONTEXT_FACTORY,
		// "com.sun.jndi.ldap.LdapCtxFactory");
		// env.put(Context.PROVIDER_URL, ldapDir);
		// Attributes matchAttrs = new BasicAttributes(true);
		// matchAttrs.put(new BasicAttribute("uniqueMember",
		// "cn=John Ainsworth,ou=users,o=psygrid,c=uk"));
		//	
		// // Specify the ids of the attributes to return
		// //String[] attrIDs = {"sn", "telephonenumber", "golfhandicap",
		// "mail"};
		// SearchControls ctls = new SearchControls();
		// //ctls.setReturningAttributes(attrIDs);
		// ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		//
		// // Specify the search filter to match
		// // Ask for objects that have the attribute "sn" == "Geisel"
		// // and the "mail" attribute
		// // String filter =
		// "(uniqueMember=cn=John Ainsworth,ou=users,o=psygrid,c=uk)";
		// String filter = "(cn=*)";
		// // Search the subtree for objects by using the filter
		//			
		// try {
		// DirContext ctx = new InitialDirContext(env);
		// //NamingEnumeration answer =
		// ctx.search("OU=Outlook,OU=North West",matchAttrs);
		// NamingEnumeration answer = ctx.search("ou=users", filter, ctls);
		//				
		// while (answer.hasMore()) {
		// SearchResult sr = (SearchResult)answer.next();
		// System.out.println(">>>" + sr.getName());
		// System.out.println(sr.getAttributes().get("uid").get());
		// }
		// } catch (NamingException ne) {
		// System.out.println(ne.getMessage());
		// }
	}

	// public List<InternetAddress> lookUpEmailAddress(ProjectType pt, GroupType
	// gt, RoleType rt){
	// String[] users = null;
	// List<InternetAddress> lia = new ArrayList<InternetAddress>();
	// // if(pt != null){
	// // if(gt != null){
	// // if (rt != null) {
	// // users = getUsersInGroupInProjectWithRole(pt, gt, rt);
	// // } else {
	// // users = getUsersInGroupInProject(pt, gt);
	// // }
	// // } else {
	// // if (rt != null) {
	// // users = getUsersInProjectWithRole(pt, rt);
	// // } else {
	// // users = getUsersInProject(pt);
	// // }
	// // }
	// // }
	// String ldapDir = "ldap://atisha.smb.man.ac.uk:389/dc=opencdms,dc=local";
	// Hashtable env = new Hashtable();
	// env.put(Context.INITIAL_CONTEXT_FACTORY,
	// "com.sun.jndi.ldap.LdapCtxFactory");
	// env.put(Context.PROVIDER_URL, ldapDir);
	// try {
	// DirContext ctx = new InitialDirContext(env);
	// for (int i = 0; i < users.length; i++) {
	// try{
	// Attributes attrs = ctx.getAttributes("uid=" + users[i]);
	// String mail = (String) attrs.get("mail").get();
	// try {
	// lia.add(new InternetAddress(mail));
	// } catch (AddressException ae) {
	// System.out.println("invalid email address found " + mail
	// + " for user " + users[i]);
	// }
	// } catch (NamingException ne) {
	// System.out.println(ne.getMessage());
	// }
	// }
	// } catch (NamingException ne) {
	// System.out.println(ne.getMessage());
	// }
	// return lia;
	// }
	static public String getCallersIdentityByUID(String uid) {
		String ldapDir = "ldap://opencdms1.opencdms.local:389/";
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapDir);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL,
				"cn=administrator,cn=users,dc=opencdms,dc=local");
		env.put(Context.SECURITY_CREDENTIALS, "cherry");
		Attributes matchAttrs = new BasicAttributes(true); // ignore attribute
															// name case
		matchAttrs.put(new BasicAttribute("uid", uid));
		String filter = "(uid=" + uid + ")";
		SearchControls searchCtrls = new SearchControls();
		searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String result = null;
		try {
			DirContext ctx = new InitialDirContext(env);
			NamingEnumeration answer = ctx.search("dc=opencdms,dc=local",
					filter, searchCtrls);
			while (answer.hasMoreElements()) {
				result = ((SearchResult) answer.next()).getNameInNamespace();
				// there should only be one!
				System.out.println(result);
				// break;
			}
		} catch (NamingException ne) {
			System.out.println(ne.getMessage());
		}
		return result;
	}

	public static boolean performLDAPAuthentication(String cid, char[] password) {
		String ldapDir = "ldap://opencdms1.opencdms.local:389/dc=opencdms,dc=local";
		boolean result = false;
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapDir);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, cid);
		env.put(Context.SECURITY_CREDENTIALS, password);

		try {
			DirContext ctx = new InitialDirContext(env);
			result = true;
		} catch (NamingException ne) {
			System.out.println("Failed to bind to LDAP server for " + cid);
		}
		return result;
	}

	static public boolean checkUID(String uid) {
		String ldapDir = "ldap://opencdms1.opencdms.local:389/";
		boolean result = false;
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapDir);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL,
				"CN=administrator,cn=users,dc=opencdms,dc=local");
		env.put(Context.SECURITY_CREDENTIALS, "cherry");
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
				System.out.println(ne.getMessage());
			}

		} catch (NamingException ne) {
			System.out.println(ne.getMessage());
		}
		return result;
	}

	public static boolean TLSperformLDAPAuthentication(String cid,
			char[] password) {
		String ldapDir = "ldap://opencdms1.opencdms.local:389/dc=opencdms,dc=local";
		boolean result = false;
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapDir);
		// env.put(Context.SECURITY_AUTHENTICATION, "simple");
		// env.put(Context.SECURITY_PRINCIPAL, cid);
		// env.put(Context.SECURITY_CREDENTIALS, password);

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
			System.out.println("Failed to bind to LDAP server for " + cid);
		} catch (IOException io) {
			System.out.println("Failed TLS negotiation with LDAP server");
		} finally {
			try {
				tls.close();
				ctx.close();

			} catch (NamingException ne) {
				System.out.println("LDAP context close failure");
			} catch (IOException io) {
				System.out.println("LDAP TLS close failure");
			}
		}
		return result;
	}

	public boolean addUserToAD(String cid, String password) {

		boolean success = false;

		// ldap stuff
		LdapContext ctx = null;
		try {
			ctx = createLDAPContext(ldapRoot, ldapRootPassword.toCharArray());

			Attributes atrs = new BasicAttributes(true);
			// fixed
			BasicAttribute objcls = new BasicAttribute("objectClass");
			objcls.add("inetOrgPerson");
			objcls.add("organizationalPerson");
			objcls.add("person");
			objcls.add("top");
			atrs.put(objcls);

			BasicAttribute sn = new BasicAttribute("sn");
			sn.add(user.last);

			BasicAttribute cn = new BasicAttribute("cn");
			String commonName = user.first + " " + user.last;
			cn.add(commonName);

			BasicAttribute mail = new BasicAttribute("mail");
			mail.add(user.mail);

			BasicAttribute userpassword = new BasicAttribute("unicodePwd");
			if (ldapPasswordHash != null) {
				if (ldapPasswordHash.equals(LDAPPasswordHashScheme.SHA
						.toString())) {
					char[] hpwd = PasswordUtilities.hashPassword(user.passwd
							.toCharArray(), LDAPPasswordHashScheme.SHA, log);
					user.passwd = (new String(hpwd));
				}
			}
			DirectoryUtility du = new DirectoryUtility();
			String quotedPassword = "\"cherry\"";
			byte[] utfPassword = quotedPassword.getBytes("UTF-16LE");
			// userpassword.add(du.createUnicodePassword(user.passwd));
			userpassword.add(utfPassword);
			BasicAttribute uac = new BasicAttribute("userAccountControl");
			uac.add(Integer.toString(UF_NORMAL_ACCOUNT));

			BasicAttribute uid = new BasicAttribute("uid");
			uid.add(user.first + user.last);

			// set all the attributes
			atrs.put(objcls);
			// atrs.put("samAccountName", user.first+user.last);
			atrs.put("givenName", user.first);
			atrs.put(sn);
			atrs.put(cn);
			atrs.put(mail);
			atrs.put(uid);
			// if mobile isn't empty set it too
			if (user.mobile != null && (!user.mobile.equals(""))) {
				BasicAttribute mobile = new BasicAttribute("mobile");
				mobile.add(user.mobile);
				atrs.put(mobile);
			}
			atrs.put(userpassword);
			atrs.put(uac);

			// create the new user
			ctx.createSubcontext("cn=" + commonName + ", " + ldapUserBaseDN,
					atrs);

			ModificationItem[] mods = new ModificationItem[2];
			mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE,
					new BasicAttribute("pwdLastSet"));
			mods[1] = new ModificationItem(DirContext.ADD_ATTRIBUTE,
					new BasicAttribute("pwdLastSet", Integer.toString(-1)));
			ctx.modifyAttributes("cn=" + commonName + ", " + ldapUserBaseDN,
					mods);

			ctx.close();
			success = true;

		} catch (NameAlreadyBoundException nex) {
			System.out.println("LDAPController " + nex.getMessage());
		} catch (InvalidAttributesException iae) {
			System.out.println("LDAPController " + iae.getMessage());
		} catch (InvalidNameException ine) {
			System.out.println("LDAPController " + ine.getMessage());
		} catch (NamingException ne) {
			System.out.println("LDAPController " + ne.getMessage());
		} catch (IOException io) {
			System.out.println("Failed TLS negotiation with LDAP server");
		} finally {
			closeLDAPContext(ctx);
		}
		return success;
	}

	public boolean userChangePassword(String uid, String oldPassword,
			String newPassword) {

		boolean success = false;

		// ldap stuff
		LdapContext ctx = null;
		try {
			ctx = createLDAPContext(uid, oldPassword.toCharArray());

			Attributes atrs = new BasicAttributes(true);

			String commonName = user.first + " " + user.last;

			DirectoryUtility du = new DirectoryUtility();

			byte[] oldPasswd = (du.createUnicodePassword(oldPassword));
			byte[] newPasswd = (du.createUnicodePassword(newPassword));
			ModificationItem[] mods = new ModificationItem[2];
			mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE,
					new BasicAttribute("unicodePwd", oldPasswd));
			mods[1] = new ModificationItem(DirContext.ADD_ATTRIBUTE,
					new BasicAttribute("unicodePwd", newPasswd));
			ctx.modifyAttributes("cn=" + commonName + ", " + ldapUserBaseDN,
					mods);

			ctx.close();
			success = true;

		} catch (NameAlreadyBoundException nex) {
			System.out.println("LDAPController " + nex.getMessage());
		} catch (InvalidAttributesException iae) {
			System.out.println("LDAPController " + iae.getMessage());
		} catch (InvalidNameException ine) {
			System.out.println("LDAPController " + ine.getMessage());
		} catch (NamingException ne) {
			System.out.println("LDAPController " + ne.getMessage());
		} catch (IOException io) {
			System.out.println("Failed TLS negotiation with LDAP server");
		} finally {
			closeLDAPContext(ctx);
		}
		return success;
	}


	public boolean resetPassword(String cid, String password,
			String newPassword) {

		boolean success = false;

		// ldap stuff
		LdapContext ctx = null;
		try {
			ctx = createLDAPContext(cid, password.toCharArray());

			Attributes atrs = new BasicAttributes(true);

			String commonName = user.first + " " + user.last;

			DirectoryUtility du = new DirectoryUtility();

			byte[] newPasswd = (du.createUnicodePassword(newPassword));
			ModificationItem[] mods = new ModificationItem[1];
			mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
					new BasicAttribute("unicodePwd", newPasswd));
			ctx.modifyAttributes("cn=" + commonName + ", " + ldapUserBaseDN,
					mods);

			ctx.close();
			success = true;

		} catch (NameAlreadyBoundException nex) {
			System.out.println("LDAPController " + nex.getMessage());
		} catch (InvalidAttributesException iae) {
			System.out.println("LDAPController " + iae.getMessage());
		} catch (InvalidNameException ine) {
			System.out.println("LDAPController " + ine.getMessage());
		} catch (NamingException ne) {
			System.out.println("LDAPController " + ne.getMessage());
		} catch (IOException io) {
			System.out.println("Failed TLS negotiation with LDAP server");
		} finally {
			closeLDAPContext(ctx);
		}
		return success;
	}

	public boolean deleteUser(String cid, String password,
			String uname) {

		boolean success = false;

		// ldap stuff
		LdapContext ctx = null;
		try {
			ctx = createLDAPContext(cid, password.toCharArray());

			ctx.lookup(uname);
			ctx.destroySubcontext(uname);
			ctx.close();
			success = true;

		} catch (NameAlreadyBoundException nex) {
			System.out.println("LDAPController " + nex.getMessage());
		} catch (InvalidAttributesException iae) {
			System.out.println("LDAPController " + iae.getMessage());
		} catch (InvalidNameException ine) {
			System.out.println("LDAPController " + ine.getMessage());
		} catch (NamingException ne) {
			System.out.println("LDAPController " + ne.getMessage());
		} catch (IOException io) {
			System.out.println("Failed TLS negotiation with LDAP server");
		} finally {
			closeLDAPContext(ctx);
		}
		return success;
	}
	
	public boolean attributeExists(String at, DirContext ctx, String uname) throws NamingException{
		boolean flag = false;

		Attributes attrs = ctx.getAttributes(uname, new String[]{at});
		if(attrs.get(at)!=null){
			flag=true;
		}
		
		return flag;
	}
	
	public boolean updateUser(String cid, String password,
			String uname) {

		boolean success = false;

		// ldap stuff
		LdapContext ctx = null;
		try {
			ctx = createLDAPContext(cid, password.toCharArray());

			if(user.mobile==null){
				//Need to check that attribute exists before removing!!
				if(attributeExists("mobile", ctx, uname)){
				ModificationItem[] mods = new ModificationItem[1];
				mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE,
						new BasicAttribute("mobile"));
				ctx.modifyAttributes(uname,
						mods);
				}
			} else {
				ModificationItem[] mods = new ModificationItem[1];
				mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
						new BasicAttribute("mobile", user.mobile));
				ctx.modifyAttributes(uname,
						mods);
			}
			
			if(user.mail==null){
				if(attributeExists("mail", ctx, uname)){
				ModificationItem[] mods = new ModificationItem[1];
				mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE,
						new BasicAttribute("mail"));
				ctx.modifyAttributes(uname,
						mods);
				}
			} else {
				ModificationItem[] mods = new ModificationItem[1];
				mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
						new BasicAttribute("mail", user.mail));
				ctx.modifyAttributes(uname,
						mods);
			}
			success = true;

		} catch (NameAlreadyBoundException nex) {
			System.out.println("LDAPController " + nex.getMessage());
		} catch (InvalidAttributesException iae) {
			System.out.println("LDAPController " + iae.getMessage());
		} catch (InvalidNameException ine) {
			System.out.println("LDAPController " + ine.getMessage());
		} catch (NamingException ne) {
			System.out.println("LDAPController " + ne.getMessage());
		} catch (IOException io) {
			System.out.println("Failed TLS negotiation with LDAP server");
		} finally {
			closeLDAPContext(ctx);
		}
		return success;
	}
	
	public boolean getUser(String cid, String password,
			String uname) {

		boolean success = false;

		// ldap stuff
		LdapContext ctx = null;
		try {
			ctx = createLDAPContext(cid, password.toCharArray());
			String[] IDs = new String[]{"mail", "uid", "sn", "mobile"}; 
			Attributes attrs = ctx.getAttributes(uname, IDs);
			user.last = (String)attrs.get("sn").get();
			if(attrs.get("mobile")!=null){
			user.mobile = (String)attrs.get("mobile").get();
			}else{user.mobile=null;}
			user.uid = (String)attrs.get("uid").get();
			if(attrs.get("mail")!=null){
			 user.mail = (String)attrs.get("mail").get();
			}else{user.mail=null;}
			user.print();
			success=true;
		} catch (NameAlreadyBoundException nex) {
			System.out.println("LDAPController " + nex.getMessage());
		} catch (InvalidAttributesException iae) {
			System.out.println("LDAPController " + iae.getMessage());
		} catch (InvalidNameException ine) {
			System.out.println("LDAPController " + ine.getMessage());
		} catch (NamingException ne) {
			System.out.println("LDAPController " + ne.getMessage());
		} catch (IOException io) {
			System.out.println("Failed TLS negotiation with LDAP server");
		} finally {
			closeLDAPContext(ctx);
		}
		return success;
	}
	
	
	public boolean userExists(String cid, String password,
			String uname) {

		boolean success = false;

		// ldap stuff
		LdapContext ctx = null;
		try {
			ctx = createLDAPContext(cid, password.toCharArray());
			ctx.list(uname);
			success = true;
		} catch (NameAlreadyBoundException nex) {
			System.out.println("LDAPController " + nex.getMessage());
		} catch (InvalidAttributesException iae) {
			System.out.println("LDAPController " + iae.getMessage());
		} catch (InvalidNameException ine) {
			System.out.println("LDAPController " + ine.getMessage());
		} catch (NamingException ne) {
			System.out.println("LDAPController " + ne.getMessage());
		} catch (IOException io) {
			System.out.println("Failed TLS negotiation with LDAP server");
		} finally {
			closeLDAPContext(ctx);
		}
		return success;
	}
	
	public boolean getUIDFromCN(String cid, String password,
			String uname) {

		boolean success = false;

		// ldap stuff
		LdapContext ctx = null;
		try {
			ctx = createLDAPContext(cid, password.toCharArray());
			Attributes matchAttrs = new BasicAttributes(true); // ignore attribute
			// name case
			matchAttrs.put(new BasicAttribute("cn", uname));
			String filter = "(cn=" + uname + ")";
			SearchControls searchCtrls = new SearchControls();
			searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String result = null;

			NamingEnumeration answer = ctx.search("dc=opencdms,dc=local",
					filter, searchCtrls);
			while (answer.hasMoreElements()) {
				Attributes a = ((SearchResult) answer.next()).getAttributes();
				// there should only be one!
				result = (String)a.get("uid").get();
				System.out.println(result);
				// break;

				success = true;
			}
		} catch (NameAlreadyBoundException nex) {
			System.out.println("LDAPController " + nex.getMessage());
		} catch (InvalidAttributesException iae) {
			System.out.println("LDAPController " + iae.getMessage());
		} catch (InvalidNameException ine) {
			System.out.println("LDAPController " + ine.getMessage());
		} catch (NamingException ne) {
			System.out.println("LDAPController " + ne.getMessage());
		} catch (IOException io) {
			System.out.println("Failed TLS negotiation with LDAP server");
		} finally {
			closeLDAPContext(ctx);
		}
		return success;
	}
	
	public boolean multiUserExists(String cid, String password,
			String first, String last) {

		boolean success = false;

		// ldap stuff
		LdapContext ctx = null;
		try {
			ctx = createLDAPContext(cid, password.toCharArray());
			String filter = "cn=" + first +"*"+ last +"*" + "";
			SearchControls searchCtrls = new SearchControls();
			searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);

			String result = null;

			NamingEnumeration answer = ctx.search("dc=opencdms,dc=local",
					filter, searchCtrls);
			while (answer.hasMoreElements()) {
				Attributes a = ((SearchResult) answer.next()).getAttributes();
				result = (String)a.get("cn").get();
				System.out.println(result);
				success = true;
			}
		} catch (NameAlreadyBoundException nex) {
			System.out.println("LDAPController " + nex.getMessage());
		} catch (InvalidAttributesException iae) {
			System.out.println("LDAPController " + iae.getMessage());
		} catch (InvalidNameException ine) {
			System.out.println("LDAPController " + ine.getMessage());
		} catch (NamingException ne) {
			System.out.println("LDAPController " + ne.getMessage());
		} catch (IOException io) {
			System.out.println("Failed TLS negotiation with LDAP server");
		} finally {
			closeLDAPContext(ctx);
		}
		return success;
	}
	
	LdapContext createLDAPContext(String cid, char[] password)
			throws NamingException, IOException {

		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapDir);
		LdapContext ctx = null;

		if (ldapUseTLS) {

			StartTlsResponse tls = null;
			ctx = new InitialLdapContext(env, null);
			tls = (StartTlsResponse) ctx
					.extendedOperation(new StartTlsRequest());
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

	protected void closeLDAPContext(LdapContext ctx) {
		try {
			if (ctx != null) {
				ctx.close();
			}
		} catch (NamingException ne) {
			System.out.println("LDAP context close failure");
		}
	}

	public class DirectoryUtility {

		public byte[] createUnicodePassword(String password) {
			return toUnicodeBytes(doubleQuoteString(password));
		}

		private byte[] toUnicodeBytes(String str) {
			byte[] unicodeBytes = null;
			try {
				unicodeBytes = str.getBytes("UTF-16LE");
//				unicodeBytes = new byte[unicodeBytesWithQuotes.length - 2];
//				System.arraycopy(unicodeBytesWithQuotes, 2, unicodeBytes, 0,
//						unicodeBytesWithQuotes.length - 2);
			} catch (Exception e) {
				// This should never happen.
				e.printStackTrace();
			}
			return unicodeBytes;
		}

		private String doubleQuoteString(String str) {
			StringBuffer sb = new StringBuffer();
			sb.append("\"");
			sb.append(str);
			sb.append("\"");
			return sb.toString();
		}
	}
}
