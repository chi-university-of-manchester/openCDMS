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


//Created on Oct 12, 2005 by John Ainsworth
package org.psygrid.edie;

import java.io.FileOutputStream;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Properties;

import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.apache.axis.encoding.Base64;
import org.apache.axis.utils.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.SAMLAssertion;
import org.psygrid.randomization.client.RandomizationClient;
import org.psygrid.randomization.model.hibernate.StratifiedRandomizer;
import org.psygrid.randomization.model.hibernate.Stratum;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.authentication.client.LoginClient;
import org.psygrid.security.authentication.service.LoginServicePortType;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.security.utils.PropertyUtilities;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.random.www.RandomDotOrg_wsdl.RandomDotOrg;
import org.random.www.RandomDotOrg_wsdl.RandomDotOrgLocator;
import org.random.www.RandomDotOrg_wsdl.RandomDotOrgPortType;

import randomX.randomHotBits;
import randomX.randomX;

public class InstallEDIERandomizer {
	public static final String DS_NAMESPACE = "http://www.w3.org/2000/09/xmldsig#";

	/**
	 * logger
	 */
	private static LoginServicePortType aa1 = null;

	private static Log _log = LogFactory.getLog(InstallEDIERandomizer.class);

	public static void main(String[] args) throws Exception {
		/*System.setProperty("axis.socketSecureFactory",
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
		SAMLAssertion sa = qc.getSAMLAssertion(new ProjectType("EDIE 2", "ED2", "EDIE-2", "2171", false));

		System.out.println("insert");
		System.out.println(sa.toString());
		System.out.println(properties.getProperty("org.psygrid.randomization.client.serviceURL"));
		String randomization = properties.getProperty("org.psygrid.randomization.client.serviceURL");
*/        try{
            RandomizationClient client = null;

  //          client = new RandomizationClient(new URL(randomization));
            client = new RandomizationClient();

            StratifiedRandomizer rnd = new StratifiedRandomizer("ED2");

            Stratum s = new Stratum();
            s.setName("sex");
            s.getValues().add("Male");
            s.getValues().add("Female");
            rnd.addStratum(s);
            Stratum s2 = new Stratum();
            s2.setName("centreNumber");
            s2.getValues().add("001001");
            s2.getValues().add("002001");
            s2.getValues().add("003001");
            s2.getValues().add("004001");
            s2.getValues().add("005001");
            rnd.addStratum(s2);
            rnd.generateCombinations("org.psygrid.randomization.model.hibernate.IBRpbrblRandomizer", 2, 4);
            int seedlen = (s.getValues().size()*s2.getValues().size());
            long[] seeds= new long[seedlen];
            for(int i=0;i<seedlen;i++){
                seeds[i]=getSeed();
            }
            rnd.createRngs(seeds);
            rnd.addTreatment("Control","ED2-000");
            rnd.addTreatment("Cognitive Behaviour Therapy","ED2-001");
           // client.saveRandomizer(rnd, sa.toString());
            client.saveRandomizer(rnd, null);

            System.out.println("Randomizer successfully saved to the service");
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
	static Long getSeed() throws ConnectException{

		randomX randomizer = new randomHotBits();
		return randomizer.nextLong();

		/*
        try{
            // Make a service
            RandomDotOrg service =
                new RandomDotOrgLocator();

            // Now use the service to get a stub which implements the SDI
            // (Service Definition Interface)
            RandomDotOrgPortType port =
                service.getRandomDotOrgPort();

            // Make the actual calls
            return port.mrand48();
        }
        catch(AxisFault fault){
            if ( fault.getCause() instanceof ConnectException ){
                throw (ConnectException)fault.getCause();
            }
            else if ( fault.getCause() instanceof UnknownHostException ||
                        fault.getCause() instanceof NoRouteToHostException ){
                ConnectException cex = new ConnectException(fault.getCause().getMessage());
                cex.initCause(fault.getCause());
                throw cex;
            }
            else{
                throw new RuntimeException(fault);
            }
        }
        catch(ServiceException ex){
            throw new RuntimeException(ex);
        }
        catch(RemoteException ex){
            throw new RuntimeException(ex);
        }
        */
	}
}
