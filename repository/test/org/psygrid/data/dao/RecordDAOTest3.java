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

import org.psygrid.data.model.ITextValue;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.repository.dao.RepositoryDAO;

public class RecordDAOTest3 extends DAOTest {

    private RepositoryDAO dao = null;
    private Factory factory = null;
    
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
    

    public void testSaveRecord_DeleteResponse(){
        try{
            String name = "testExport4 - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            ds.addGroup(factory.createGroup("FOO"));
            
            Document doc1 = factory.createDocument("Doc 1", "Doc 1");
            ds.addDocument(doc1);
            
            DocumentOccurrence docOcc1 = factory.createDocumentOccurrence("Occ 1");
            doc1.addOccurrence(docOcc1);
            
            Section sec1 = factory.createSection("Sec 1", "Sec 1");
            doc1.addSection(sec1);
            
            SectionOccurrence secOcc1 = factory.createSectionOccurrence("Sec Occ 1");
            sec1.addOccurrence(secOcc1);
            
            TextEntry te1 = factory.createTextEntry("TE1", "TE1");
            te1.setSection(sec1);
            doc1.addEntry(te1);
            
            TextEntry te2 = factory.createTextEntry("TE2", "TE2");
            te2.setSection(sec1);
            doc1.addEntry(te2);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 3, "FOO");
            
            doc1 = ds.getDocument(0);
            docOcc1 = doc1.getOccurrence(0);
            sec1 = doc1.getSection(0);
            secOcc1 = sec1.getOccurrence(0);
            te1 = (TextEntry)doc1.getEntry(0);
            te2 = (TextEntry)doc1.getEntry(1);

            Record rec = ds.generateInstance();
            rec.setIdentifier(ids[0]);
            DocumentInstance docInst = doc1.generateInstance(docOcc1);
            rec.addDocumentInstance(docInst);
            BasicResponse br1 = te1.generateInstance(secOcc1);
            docInst.addResponse(br1);
            ITextValue tv1 = te1.generateValue();
            tv1.setValue("Foo");
            br1.setValue(tv1);
            BasicResponse br2 = te2.generateInstance(secOcc1);
            docInst.addResponse(br2);
            ITextValue tv2 = te2.generateValue();
            tv2.setValue("Foo");
            br2.setValue(tv2);
            
            Long recId = dao.saveRecord(rec.toDTO(), true, null, "NoUser");
            rec = dao.getRecord(recId, RetrieveDepth.RS_COMPLETE).toHibernate();
            rec.attach(ds);
            
            docInst = rec.getDocumentInstance(docOcc1);
            br1 = (BasicResponse)docInst.getResponse(te1, secOcc1);
            Long respId = br1.getId();
            Long valId = br1.getValue().getId();
            docInst.removeResponse(te1, secOcc1);
            
            dao.saveRecord(rec.toDTO(), true, null, "NoUser");
            
            rec = dao.getRecord(recId, RetrieveDepth.RS_COMPLETE).toHibernate();
            rec.attach(ds);

            docInst = rec.getDocumentInstance(docOcc1);
            assertNull("Response that was deleted still exists in the doc instance",docInst.getResponse(te1, secOcc1));
            
            assertFalse("Deleted response still exists in the database", dao.doesObjectExist("BasicResponse", respId));
            assertFalse("Value of deleted response still exists in the database", dao.doesObjectExist("TextValue", valId));
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
}
