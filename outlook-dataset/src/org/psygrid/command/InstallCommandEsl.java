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

package org.psygrid.command;

import java.io.FileOutputStream;
import java.util.Properties;

import org.apache.axis.encoding.Base64;
import org.apache.axis.utils.Options;
import org.opensaml.SAMLAssertion;
import org.psygrid.esl.model.IFactory;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.hibernate.HibernateFactory;
import org.psygrid.esl.services.client.EslClient;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.authentication.client.LoginClient;
import org.psygrid.security.authentication.service.LoginServicePortType;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.security.utils.PropertyUtilities;
import org.psygrid.www.xml.security.core.types.ProjectType;



/**
 * @author Lucy Bridges
 *
 */
public class InstallCommandEsl {


	private static EslClient client = new EslClient();
	private static LoginServicePortType aa1 = null;


	public static void main(String[] args){

		SAMLAssertion sa = null;
		IProject project = null;
        try{
        	sa = login(args);
        	String saml = sa.toString();

        	project = CommandEsl.createProject();
        	client.saveProject(project, saml);
        	System.out.println(project.getProjectCode()+" project has been setup");
        	project = client.retrieveProjectByCode(project.getProjectCode(), saml);
        	project = CommandEsl.createGroups(project);
        	client.saveProject(project, saml);
        	System.out.println("Groups have been setup");
        	project = client.retrieveProjectByCode(project.getProjectCode(), saml);
        	project = CommandEsl.setupRandomisation(project);
        	client.saveProject(project, saml);
        	System.out.println("Randomisation has been setup");
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

	}

	private static SAMLAssertion login(String[] args) throws Exception {

		System.setProperty("axis.socketSecureFactory",
		"org.psygrid.security.components.net.PsyGridClientSocketFactory");
		Options opts = new Options(args);
		Properties properties = PropertyUtilities.getProperties("test.properties");
		System.out.println(properties.getProperty("org.psygrid.security.authentication.client.trustStoreLocation"));
		LoginClient tc = null;

		try {
			tc = new LoginClient("test.properties");
			aa1 = tc.getPort();
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
		SAMLAssertion sa = qc.getSAMLAssertion(new ProjectType("Command", "COM", "Command", "?", false));

		return sa;
	}

}
