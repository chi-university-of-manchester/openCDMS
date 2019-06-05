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
package org.psygrid.data;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;

import org.apache.axis.encoding.Base64;
import org.apache.axis.utils.Options;
import org.opensaml.SAMLAssertion;
import org.psygrid.data.client.DataElementClient;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.LSIDAuthority;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.attributeauthority.model.IProject;
import org.psygrid.security.authentication.client.LoginClient;
import org.psygrid.security.authentication.service.LoginServicePortType;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.security.utils.PropertyUtilities;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * Class for testing access to the DataElementClient with security
 * enabled.
 * 
 * @author Lucy Bridges
 *
 */
public class TestDELSecurity {
	
	private static DataElementClient client = new DataElementClient();
	private static LoginServicePortType aa1 = null;
	
	
	public static void main(String[] args){
		
		SAMLAssertion sa = null;
		IProject project = null;
        try{
        	sa = login(args);
        	String saml = sa.toString();
        	
        	System.out.println("Do stuff for: "+client.getUrl());
        	
        	//Should work always
        	System.out.println("Getting element types");
        	
        	System.out.println(client.getElementTypes());
        	
        	//Need to test with different users
        	System.out.println("Getting LSID Authorities");
        	LSIDAuthority[] auths = client.getLSIDAuthorities(saml);
        	for (LSIDAuthority authority: auths) {
        		System.out.println(authority.getAuthorityID());
        	}
        	
        	//Should work
        	System.out.println("Getting document summary list");
        	List<Document> documents = client.getDocumentSummaryList("org.psygrid", saml);
        	for (Document document: documents) {
        		System.out.println(document+" : "+document.getDisplayText());
        	}
        //TODO update tests	
        	//Should always work
       // 	client.getElementAsRepositoryTemplate(lsid, saml);
        //	client.getMetaData(lsid, saml);

        	//Should always work
        	//client.sophisticatedSearchByTypeAndName(queryObject, saml);
        	
        	//Should always fail (sys admin only)
        	//client.importDataElement(element, info, authority, saml);
        	
        	//Should work for author only
        	//client.reviseElement(elem, adminInfo, authority, saml);
        	
        	//Should work for author only
        	//client.saveNewElement(element, info, authority, saml);
        	
        	
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
		}System.out.println("about to call login via my proxy");
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
		SAMLAssertion sa = qc.getSAMLAssertion(new ProjectType("DataElementLibrary", "DEL", "DataElementLibrary", "1234", false));

		return sa;
	}
}
