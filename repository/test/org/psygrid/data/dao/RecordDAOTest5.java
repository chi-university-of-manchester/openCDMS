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

import org.psygrid.data.model.dto.extra.SearchRecordChangeHistoryResult;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.model.hibernate.Identifier;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.repository.dao.RepositoryDAO;

/**
 * @author Rob Harper
 *
 */
public class RecordDAOTest5 extends DAOTest {

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
    
    public void testSearchRecordChangeHistory(){
    	try{
            String name = "testSearchRecordChangeHistory - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            ds.addGroup(factory.createGroup("FOO"));
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 5, "FOO");
            
            Record rec1 = ds.generateInstance();
            rec1.setIdentifier(ids[0]);            
            Long rec1Id = dao.saveRecord(rec1.toDTO(), true, null, "NoUser");
            
            Record rec2 = ds.generateInstance();
            rec2.setIdentifier(ids[1]);            
            Long rec2Id = dao.saveRecord(rec2.toDTO(), true, null, "MyUser");
            
            Record rec3 = ds.generateInstance();
            rec3.setIdentifier(ids[2]);            
            Long rec3Id = dao.saveRecord(rec3.toDTO(), true, null, "MyUser");
            
            SearchRecordChangeHistoryResult result = dao.searchRecordChangeHistory(projectCode, null, null, null, null, 0);            
            assertEquals("Wrong number of RecordChangeHistoryResults (all)", 3, result.getResults().length);
            
            result = dao.searchRecordChangeHistory(projectCode, null, null, "MyUser", null, 0);
            assertEquals("Wrong number of RecordChangeHistoryResults (user=MyUser)", 2, result.getResults().length);
            
            result = dao.searchRecordChangeHistory(projectCode, new Date(0), null, null, null, 0);
            assertEquals("Wrong number of RecordChangeHistoryResults (start=past)", 3, result.getResults().length);
            
            result = dao.searchRecordChangeHistory(projectCode, new Date((new Date()).getTime()+3600), null, null, null, 0);
            assertEquals("Wrong number of RecordChangeHistoryResults (start=future)", 0, result.getResults().length);
            
            result = dao.searchRecordChangeHistory(projectCode, null, new Date((new Date()).getTime()+3600), null, null, 0);
            assertEquals("Wrong number of RecordChangeHistoryResults (end=future)", 3, result.getResults().length);
            
            result = dao.searchRecordChangeHistory(projectCode, null, new Date(0), null, null, 0);
            assertEquals("Wrong number of RecordChangeHistoryResults (end=past)", 0, result.getResults().length);
            
            result = dao.searchRecordChangeHistory(projectCode, null, null, null, ids[1].getIdentifier(), 0);
            assertEquals("Wrong number of RecordChangeHistoryResults (identifier="+ids[1].getIdentifier()+")", 1, result.getResults().length);
            
            result = dao.searchRecordChangeHistory(projectCode, new Date(0), new Date((new Date()).getTime()+3600), "MyUser", null, 0);
            assertEquals("Wrong number of RecordChangeHistoryResults (start=past, end=future, user=MyUser)", 2, result.getResults().length);
            
            
            
    	}
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    

}
