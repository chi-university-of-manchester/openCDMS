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
package org.psygrid.drn.address;

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

public class AddGroupsToAddressEsl7 {

	private static EslClient client = new EslClient();
	private static IFactory factory = new HibernateFactory();
	private static LoginServicePortType aa1 = null;


	public static void main(String[] args){

		SAMLAssertion sa = null;
		IProject project = null;
        try{
        	sa = login(args);
        	String saml = sa.toString();

        	//project = createProject();
        	project = client.retrieveProjectByCode("ADD", saml);
        	System.out.println("Project ID "+project.getId());
        	project = createGroups(project);
        	client.saveProject(project, saml);
        	System.out.println("Groups have been setup");
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

	}

	public static SAMLAssertion login(String[] args) throws Exception {

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
		SAMLAssertion sa = qc.getSAMLAssertion(new ProjectType("ADDRESS", "ADD", "ADDRESS", "?", false));

		return sa;
	}

	private static IProject createProject() {
		IProject project = factory.createProject("ADDRESS");
		project.setProjectCode("ADD");
		return project;
	}

	private static IProject createGroups(IProject project) {
		project.setGroup(factory.createGroup("East Lancashire NHS trust", "651006"));
		project.setGroup(factory.createGroup("St Helens and Knowsley Hospitals NHS ", "651007"));
		project.setGroup(factory.createGroup("University Hospital of South Manchester NHS Foundation Trust", "651008"));
		project.setGroup(factory.createGroup("Wirral University Teaching Hospital NHS Foundation Trust", "651009"));
		project.setGroup(factory.createGroup("West Hertfordshire hospital NHS trust", "650008"));
		project.setGroup(factory.createGroup("Wycombe Hospital", "649002"));
		project.setGroup(factory.createGroup("George Eliot Hospital", "649003"));
		project.setGroup(factory.createGroup("Countess of Chester Hospital NHS Foundation Trust", "651010"));
		project.setGroup(factory.createGroup("James Paget University Hospitals NHS Foundation Trust", "654005"));
		project.setGroup(factory.createGroup("United Lincolnshire Hospitals NHS Trust", "655002"));
		project.setGroup(factory.createGroup("Queen Elizabeth Hospital NHS Trust", "640006"));
		project.setGroup(factory.createGroup("Newcastle Diabetes Centre", "640008"));
		project.setGroup(factory.createGroup("South Tyneside Healthcare NHS Trust", "640009"));
		project.setGroup(factory.createGroup("Northumbria Healthcare NHS Trust", "640010"));
		project.setGroup(factory.createGroup("North Tees and Hartlepool NHS Foundation Trust", "640011"));
		return project;
	}

}
