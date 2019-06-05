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

package org.psygrid.data.dao;

import java.util.Date;
import java.util.Map;

import org.apache.axis.client.Call;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.model.hibernate.Transformer;
import org.psygrid.data.repository.dao.RepositoryDAO;
import org.psygrid.data.repository.transformer.TransformerClient;

public class TransformerDAOTest extends DAOTest {

    private RepositoryDAO dao = null;
    private Factory factory = null;
    
    protected void setUp() throws Exception {
        super.setUp();
        dao = (RepositoryDAO)ctx.getBean("repositoryDAOService");
        factory = (Factory)ctx.getBean("factory");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        dao = null;
        factory = null;
    }
    
    public void testGetTransformerClients(){
        try{
            String name = "testGetTransformerClients - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            ds.setProjectCode(guid.toString());
            String url1 = "http://localhost:8080/repository/services/repository";
            String ns1 = "urn:data.psygrid.org";
            String op1 = "testOp";
            String resClass1 = "org.psygrid.data.model.hibernate.TextValue";
            Transformer t1 = factory.createTransformer(url1, ns1, op1, resClass1);
            String url2 = "http://localhost:8080/repository/services/reports";
            String ns2 = "urn:data.psygrid.org";
            String op2 = "testOp2";
            String resClass2 = "org.psygrid.data.model.hibernate.TextValue";
            Transformer t2 = factory.createTransformer(url2, ns2, op2, resClass2);
            ds.addTransformer(t1);
            ds.addTransformer(t2);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            ds = dao.getDataSet(dsId).toHibernate();
            
            Map<Long, TransformerClient> clients = dao.getTransformerClients(dsId);
            
            assertEquals("Clients map contains wrong number of items", 2, clients.size());
            TransformerClient client1 = clients.get(ds.getTransformer(0).getId());
            assertNotNull("No client exists for transformer 1", client1);
            Call call1 = client1.getWebService();
            assertEquals("Client 1 call has the wrong endpoint",url1,call1.getTargetEndpointAddress());
            assertEquals("Client 1 call has the wrong namespace",ns1,call1.getOperationName().getNamespaceURI());
            assertEquals("Client 1 call has the wrong namespace",op1,call1.getOperationName().getLocalPart());
            assertEquals("Client 1 has the wrong result class",resClass1,client1.getResultClass());
            TransformerClient client2 = clients.get(ds.getTransformer(1).getId());
            Call call2 = client2.getWebService();
            assertEquals("Client 2 call has the wrong endpoint",url2,call2.getTargetEndpointAddress());
            assertEquals("Client 2 call has the wrong namespace",ns2,call2.getOperationName().getNamespaceURI());
            assertEquals("Client 2 call has the wrong namespace",op2,call2.getOperationName().getLocalPart());
            assertEquals("Client 2 has the wrong result class",resClass2,client2.getResultClass());
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
}
