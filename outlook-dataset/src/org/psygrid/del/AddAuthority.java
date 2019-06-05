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
package org.psygrid.del;

import java.io.FileOutputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.axis.encoding.Base64;
import org.apache.axis.utils.Options;
import org.opensaml.SAMLAssertion;
import org.psygrid.data.client.DataElementClient;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.authentication.client.LoginClient;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.security.utils.PropertyUtilities;

/**
 * This class adds a new authority to the Data Element Library.
 *
 * The authority name should match the project ID of a project that has been added to the security system.
 *
 * If Elements are to be imported for the new authority, please use the ImportScript in the del-import
 * project instead.
 *
 *
 * Example Usage: -u username -w password https://psygrid.smb.man.ac.uk/del/services/delElement org.psygrid
 *
 * Where the username is for a user with the SystemAdministrator role within the DEL project
 *
 *
 * This class requires the following certificates:
 *	- client_truststore.jks
 *	- default.jks
 *
 * It also requires a 'test.properties' file to be placed in the same directory, containing
 * the following information:
 *
 *	org.psygrid.security.attributeauthority.client.serviceURL=
 *	org.psygrid.security.attributeauthority.admin.serviceURL=
 *	org.psygrid.security.attributeauthority.client.ldapDirectoryURL=
 *
 *	org.psygrid.security.authentication.client.trustStoreLocation=client_truststore.jks
 *	org.psygrid.security.authentication.client.trustStorePassword=
 *	org.psygrid.security.authentication.client.keyStoreLocation=default.jks
 *	org.psygrid.security.authentication.client.keyStorePassword=
 *	org.psygrid.security.authentication.client.serviceURL=
 *	org.psygrid.security.authentication.host=
 *	org.psygrid.security.authentication.port=
 *	org.psygrid.security.authentication.lifetime=
 *
 *
 * @author Lucy Bridges
 *
 */
public class AddAuthority {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		if(args.length != 6){
			throw new IllegalArgumentException("Requires four arguments: username, password, DEL URL and new authority.\n\n" +
			"Usage Example: -u username -w password https://psygrid.smb.man.ac.uk/del/services/delElement org.psygrid");
		}

		String saml = login(args);

		String delURL = args[4];
		String authority = args[5];

		addAuthority(delURL, authority, saml);
	}

	private static String login(String[] args) throws Exception {

		System.setProperty("axis.socketSecureFactory",
		"org.psygrid.security.components.net.PsyGridClientSocketFactory");
		Options opts = new Options(args);

		Properties properties = PropertyUtilities.getProperties("test.properties");
		System.out.println(properties.getProperty("org.psygrid.security.authentication.client.trustStoreLocation"));
		LoginClient tc = null;

		try {
			tc = new LoginClient("test.properties");
		} catch (Exception e) {
			e.printStackTrace();
		}

		char[] password = opts.getPassword().toCharArray();
		short[] pwd = new short[password.length];
		for (int i = 0; i < pwd.length; i++) {
			pwd[i] = (short) password[i];
		}
		String credential = tc.getPort().login(opts.getUser(), pwd);
		if (credential != null) {
			byte[] ks = Base64.decode(credential);
			FileOutputStream fos = new FileOutputStream(properties
					.getProperty("org.psygrid.security.authentication.client.keyStoreLocation"));
			fos.write(ks);
			fos.flush();
			fos.close();
		}
		System.out.println("loggedin");
		System.setProperty("javax.net.ssl.keyStorePassword", new String(password));
		PsyGridClientSocketFactory.reinit();
		AAQueryClient qc = new AAQueryClient("test.properties");
		System.out.println("getAssertion");
		SAMLAssertion sa = qc.getSAMLAssertion();
		System.out.println(sa.toString());
		System.out.println(properties.getProperty("org.psygrid.data.client.serviceURL"));

		return sa.toString();
	}

	private static void addAuthority(String delURL, String authority, String saml) throws Exception {
		//Initialise the del client.
		//use the argument as the location of the repository web-service
		DataElementClient client = new DataElementClient(new URL(delURL));

		//Save the authority to the database.
		client.saveAuthorityToDatabase(authority, saml);

	}
}
