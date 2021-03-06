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

package org.psygrid.data.utils;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.axis.encoding.Base64;
import org.apache.axis.utils.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.SAMLAssertion;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Identifier;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.model.hibernate.RecordData;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.authentication.client.LoginClient;
import org.psygrid.security.authentication.service.LoginServicePortType;
import org.psygrid.security.utils.PropertyUtilities;
import org.psygrid.www.xml.security.core.types.ProjectType;

public class ReserveIdentifierClient {

    public static final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

    public static final String DS_NAMESPACE = "http://www.w3.org/2000/09/xmldsig#";

    /**
     * logger
     */
    private static LoginServicePortType aa1 = null;

    private static Log _log = LogFactory.getLog(ReserveIdentifierClient.class);

    public static void main(String[] args) throws Exception {
        System.setProperty("axis.socketSecureFactory",
                "org.psygrid.security.components.net.PsyGridClientSocketFactory");
        Options opts = new Options(args);

        String[] otherArgs = opts.getRemainingArgs();
        Long dataSetId = null;
        String identifier = null;
        Date schStartDate = null;

        if ( 3 != otherArgs.length ){
            System.out.println("Usage: ReserveIdentifier <dataSetId> <identifier> <schedule start date>");
            return;
        }
        else{
            dataSetId = Long.parseLong(otherArgs[0]);
            identifier = otherArgs[1];
            schStartDate = dateFormatter.parse(otherArgs[2]);
        }        

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
        AAQueryClient qc = new AAQueryClient("test.properties");
        System.out.println("getAssertion");
        SAMLAssertion sa = qc.getSAMLAssertion(new ProjectType("Outlook", "OLK", null, null, false));

        RepositoryClient client = new RepositoryClient();
        Identifier id = new Identifier();
        id.initialize(identifier);
        Identifier savedId = client.addIdentifier(dataSetId, id, sa.toString());
        DataSet ds = client.getDataSetSummary(savedId.getProjectPrefix(), new Date(0), sa.toString());
        Record r = ds.generateInstance();
        r.setIdentifier(savedId);
        RecordData rd = r.generateRecordData();
        rd.setScheduleStartDate(schStartDate);
        rd.setStudyEntryDate(schStartDate);
        r.setRecordData(rd, null);
        client.saveRecord(r, false, sa.toString());
        System.out.println("Successfully created record for identifier '"+identifier+"'");
    }

}


