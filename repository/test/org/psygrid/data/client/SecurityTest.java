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

package org.psygrid.data.client;

import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.opensaml.SAMLAssertion;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.model.hibernate.Identifier;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.model.hibernate.Status;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.security.attributeauthority.client.AASAMLRequestor;
import org.psygrid.security.utils.PropertyUtilities;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SecurityTest extends TestCase {

    private Factory factory;
    
    protected ApplicationContext ctx = null;
    
    public SecurityTest() {
        String[] paths = {"applicationContext.xml"};
        ctx = new ClassPathXmlApplicationContext(paths);
    }
        
    protected void setUp() throws Exception {
        super.setUp();
        factory = (Factory) ctx.getBean("factory");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        factory = null;
    }

    public void testSaveDataSet(){
        try{
            
            DataSet ds = factory.createDataset("Secure DataSet?");
            Status s1 = factory.createStatus("Status 1", 1);
            Status s2 = factory.createStatus("Status 2", 2);
            s1.addStatusTransition(s2);
            ds.addStatus(s1);
            ds.addStatus(s2);
            ds.setProjectCode("fep");
            
            Properties prop = PropertyUtilities.getProperties("saml.properties");
            AASAMLRequestor aasii = new AASAMLRequestor(prop);
            SAMLAssertion sa = aasii.newAssertion("HelenRoberts", new ProjectType("PsyGridFEPProject", "fep", null, null, false));
            String saml = sa.toString();
            
            RepositoryClient client = new RepositoryClient();
            Long dsId = client.saveDataSet(ds, saml);
            client.publishDataSet(dsId, saml);
            
            List<Identifier> ids = client.generateIdentifiers(dsId, "nwh", 1, sa.toString());
            
            ds = client.getDataSet(dsId, saml);
            Record r = ds.generateInstance();
            r.setIdentifier(ids.get(0));
            
            client.saveRecord(r, true, saml);
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
}
