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

import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import net.sourceforge.groboutils.junit.v1.TestRunnable;

import org.psygrid.data.dao.DAOTestHelper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.data.repository.dao.RepositoryDAO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ConcurrencyTests extends TestCase {

    private RepositoryDAO dao;
    private Factory factory;
    
    protected ApplicationContext ctx = null;
    
    public ConcurrencyTests() {
        String[] paths = {"applicationContext.xml"};
        ctx = new ClassPathXmlApplicationContext(paths);
    }
        
    protected void setUp() throws Exception {
        super.setUp();
        dao = (RepositoryDAO)ctx.getBean("repositoryDAOService");
        factory = (Factory) ctx.getBean("factory");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        dao = null;
        factory = null;
    }

    private class GenerateIdentifiers
            extends TestRunnable {

        private Long dataSetId;
        private String project;
        private String group;
        private int number;
        
        private GenerateIdentifiers(Long dataSetId, String project, String group, int number) {
            this.dataSetId = dataSetId;
            this.project = project;
            this.group = group;
            this.number = number;
        }

        public void runTest() throws Throwable {
            RepositoryClient client = new RepositoryClient();
            List<Identifier> ids = client.generateIdentifiers(dataSetId, group, number, null);
            assertEquals("Incorrect number of identifiers generated", number, ids.size());
            int lastSuffix = 0;
            for ( int i=0; i<number; i++){
                Identifier iid = ids.get(i);
                assertNotNull("Identifier is null", iid);
                assertNotNull("Identifier overall identifier is null", iid.getIdentifier());
                assertEquals("Identifier has the wrong project prefix", project, iid.getProjectPrefix());
                assertEquals("Identifier has the wrong group prefix", group, iid.getGroupPrefix());
                if ( i > 0){
                    assertEquals("The suffix of the identifier at index "+i+" is not one greater than the suffix of the previous identifier",lastSuffix+1,iid.getSuffix());
                }
                lastSuffix = iid.getSuffix();
            }
        }
    }

    public void testConcurrentGenerateIdentifiers(){
        try{
            Long id = null;
            int suffixSize = 6;
            String projectCode = null;
            {
                String name1 = "testGenerateIdentifiers - "+(new Date()).toString();
                DataSet ds = factory.createDataset(name1);
                //generate unique project code
                java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
                projectCode = DAOTestHelper.checkProjectCode(guid.toString());            
                ds.setProjectCode(projectCode);
                ds.setIdSuffixSize(suffixSize);
                Group grp1 = factory.createGroup("GRP1");
                Group grp2 = factory.createGroup("GRP2");
                Group grp3 = factory.createGroup("GRP3");
                ds.addGroup(grp1);
                ds.addGroup(grp2);
                ds.addGroup(grp3);
                id = dao.saveDataSet(ds.toDTO());
            }
            
            TestRunnable tr1 = new GenerateIdentifiers(id, projectCode, "GRP2", 10);
            TestRunnable tr2 = new GenerateIdentifiers(id, projectCode, "GRP2", 5);
            TestRunnable tr3 = new GenerateIdentifiers(id, projectCode, "GRP2", 8);
            TestRunnable tr4 = new GenerateIdentifiers(id, projectCode, "GRP2", 20);
            TestRunnable tr5 = new GenerateIdentifiers(id, projectCode, "GRP2", 30);
            TestRunnable tr6 = new GenerateIdentifiers(id, projectCode, "GRP2", 10);
            TestRunnable tr7 = new GenerateIdentifiers(id, projectCode, "GRP2", 5);
            TestRunnable tr8 = new GenerateIdentifiers(id, projectCode, "GRP2", 8);
            TestRunnable tr9 = new GenerateIdentifiers(id, projectCode, "GRP2", 20);
            TestRunnable tr10 = new GenerateIdentifiers(id, projectCode, "GRP2", 30);
            
            TestRunnable[] trs = {tr1, tr2, tr3, tr4, tr5, tr6, tr7, tr8, tr9, tr10};
            MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);

            //kickstarts the MTTR & fires off threads
            mttr.runTestRunnables();
        }
        catch(Throwable ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }
    
    private class SaveRecords extends TestRunnable {

        private DataSet ds;
        private Identifier id;
        private String user;
        
        public SaveRecords(DataSet ds, Identifier id, String user){
            this.ds = ds;
            this.id = id;
            this.user = user;
        }
        
        @Override
        public void runTest() throws Throwable {
            Record r = ds.generateInstance();
            r.setIdentifier(id);
            DocumentInstance docInst = ds.getDocument(0).generateInstance(ds.getDocument(0).getOccurrence(0));
            r.addDocumentInstance(docInst);
            RepositoryClient client = new RepositoryClient();
            try{
                client.saveRecordAsUser(r, user, null);
                //client.changeDocumentStatus(id.getIdentifier(), ds.getDocument(0).getOccurrence(0).getId(), ds.getDocument(0).getStatus(1).getId(), null);
            }
            catch(Exception ex){
                fail("Exception: "+ex);
            }
        }
        
    }
    
    public void testConcurrentSaveRecords(){
        try{
            DataSet ds = null;
            String projectCode = null;
            {
                String name1 = "testConcurrentSaveRecords - "+(new Date()).toString();
                ds = factory.createDataset(name1);
                //generate unique project code
                java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
                projectCode = DAOTestHelper.checkProjectCode(guid.toString());            
                ds.setProjectCode(projectCode);
                Group grp1 = factory.createGroup("GRP1");
                ds.addGroup(grp1);
                DocumentGroup docGrp = factory.createDocumentGroup("DocGrp1");
                ds.addDocumentGroup(docGrp);
                Document doc = factory.createDocument("Doc1");
                ds.addDocument(doc);
                DocumentOccurrence docOcc = factory.createDocumentOccurrence("DocOcc");
                doc.addOccurrence(docOcc);
                docOcc.setDocumentGroup(docGrp);
                Status stat1 = factory.createStatus("Status1", "Status1", 0);
                Status stat2 = factory.createStatus("Status2", "Status2", 1);
                stat1.addStatusTransition(stat2);
                doc.addStatus(stat1);
                doc.addStatus(stat2);
                Long id = dao.saveDataSet(ds.toDTO());
                dao.publishDataSet(id);
                ds = dao.getDataSet(id).toHibernate();
            }
            
            int nThreads = 100;

            RepositoryClient client = new RepositoryClient();
            List<Identifier> ids = client.generateIdentifiers(ds.getId(), "GRP1", nThreads, null);
            
            TestRunnable[] trs = new TestRunnable[nThreads];
            for ( int i=0; i<nThreads; i++ ){
                trs[i] = new SaveRecords(ds, ids.get(i), "User"+i);
            }
            
            MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);

            //kickstarts the MTTR & fires off threads
            mttr.runTestRunnables();
            
        }
        catch(Throwable ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }
    
}
