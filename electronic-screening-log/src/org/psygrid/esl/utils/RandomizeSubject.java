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
package org.psygrid.esl.utils;

import java.io.FileOutputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.axis.encoding.Base64;
import org.apache.axis.utils.Options;
import org.opensaml.SAMLAssertion;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.services.client.EslClient;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.authentication.client.LoginClient;
import org.psygrid.security.authentication.service.LoginServicePortType;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.security.utils.PropertyUtilities;

public class RandomizeSubject {

    public static final String DS_NAMESPACE = "http://www.w3.org/2000/09/xmldsig#";

    private static LoginServicePortType aa1 = null;

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        System.setProperty("axis.socketSecureFactory",
        "org.psygrid.security.components.net.PsyGridClientSocketFactory");
        Options opts = new Options(args);
        String[] rndArgs = opts.getRemainingArgs();
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
        SAMLAssertion sa = qc.getSAMLAssertion();
        System.out.println(sa.toString());
        System.out.println(properties.getProperty("org.psygrid.esl.client.serviceURL"));

        String saml = sa.toString();
        String projCode = rndArgs[0];
        System.out.println("Project Code = "+projCode);
        String studyNumber = rndArgs[1];
        System.out.println("Study Number = "+studyNumber);
        
        EslClient client = new EslClient(new URL(properties.getProperty("org.psygrid.esl.client.serviceURL")));
        IProject project = client.retrieveProjectByCode(projCode, saml);
        ISubject subject = client.retrieveSubjectByStudyNumber(project, studyNumber, saml);
        client.randomiseSubject(subject, null, saml);
        System.out.println("Randomization successful");
    }

}
