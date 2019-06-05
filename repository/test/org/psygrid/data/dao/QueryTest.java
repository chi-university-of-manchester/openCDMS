/*
Copyright (c) 2006-2009, The University of Manchester, UK.

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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.psygrid.data.model.IDateValue;
import org.psygrid.data.model.IIntegerValue;
import org.psygrid.data.model.INumericValue;
import org.psygrid.data.model.IOptionValue;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.query.IDateStatement;
import org.psygrid.data.query.IIntegerStatement;
import org.psygrid.data.query.INumericStatement;
import org.psygrid.data.query.IOptionStatement;
import org.psygrid.data.query.IQuery;
import org.psygrid.data.query.QueryDAO;
import org.psygrid.data.query.QueryDAOHibernate;
import org.psygrid.data.query.QueryFactory;
import org.psygrid.data.query.QueryOperation;
import org.psygrid.data.query.QueryStatementValue;
import org.psygrid.data.query.hibernate.HibernateQueryFactory;
import org.psygrid.data.repository.dao.hibernate.RepositoryDAOHibernate;
import junit.framework.TestCase;

/**
 * @author Rob Harper
 *
 */
public class QueryTest extends TestCase {

    private RepositoryDAOHibernate repositoryDaoHibernate = null;
	
    private QueryDAO queryDAO = null;

	private Factory factory = null;
    
    private List<StandardCode> codes = null;
    
    private SessionFactory sessionFactory;
    
    protected void setUp() throws Exception {
		repositoryDaoHibernate = new RepositoryDAOHibernate();
		queryDAO = new QueryDAOHibernate();
        factory = new HibernateFactory();
        sessionFactory = new Configuration().configure().buildSessionFactory();
        repositoryDaoHibernate.setSessionFactory(sessionFactory);
        codes = new ArrayList<StandardCode>();
        for ( org.psygrid.data.model.dto.StandardCodeDTO sc: repositoryDaoHibernate.getStandardCodes() ){
        	codes.add(sc.toHibernate());
        }
    }

    protected void tearDown() throws Exception {
        repositoryDaoHibernate = null;
        factory = null;
        codes = null;
    }
    
    public void testNumericGt(){
    	try{
	    	DataSet ds = buildDataset();
	    	Long dsId = repositoryDaoHibernate.saveDataSet(ds.toDTO());
	    	ds = repositoryDaoHibernate.getDataSet(dsId).toHibernate();
	    	repositoryDaoHibernate.publishDataSet(dsId);
	    	Record r1 = buildRecord(ds, ds.getGroup(0));
	    	addNumericResponse(r1, 4.0);
	    	Record r2 = buildRecord(ds, ds.getGroup(0));
	    	addNumericResponse(r2, 5.0);
	    	Record r3 = buildRecord(ds, ds.getGroup(0));
	    	addNumericResponse(r3, 6.0);
	    	
	    	repositoryDaoHibernate.saveRecord(r1.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r2.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r3.toDTO(), true, null, "NoUser");
	    	
	    	IQuery q = buildQuery(ds, "AND");
	    	addNumericStatement(q, QueryOperation.GREATER_THAN, Double.valueOf(4.5));
	    	
	    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
	    	
	    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
	    	
	    	assertEquals("Query returned wrong number of identifiers", 2, ids.length);
	    	assertTrue("IDs do not include "+r2.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
	    	assertTrue("IDs do not include "+r3.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
	    	assertFalse("IDs do include "+r1.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
	    	
	    	Long count = queryDAO.executeQueryForCount(qId);
	    	assertEquals("executeQueryForCount returned wrong number of identifiers", Long.valueOf(2), count);
    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    		fail("Exception: "+ex.getMessage());
    	}
    }
    
/*    public void testNumericLt(){
    	try{
	    	IDataSet ds = buildDataset();
	    	Long dsId = repositoryDaoHibernate.saveDataSet(ds.toDTO());
	    	ds = repositoryDaoHibernate.getDataSet(dsId).toHibernate();
	    	repositoryDaoHibernate.publishDataSet(dsId);
	    	IRecord r1 = buildRecord(ds, ds.getGroup(0));
	    	addNumericResponse(r1, 4.0);
	    	IRecord r2 = buildRecord(ds, ds.getGroup(0));
	    	addNumericResponse(r2, 5.0);
	    	IRecord r3 = buildRecord(ds, ds.getGroup(0));
	    	addNumericResponse(r3, 6.0);
	    	
	    	repositoryDaoHibernate.saveRecord(r1.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r2.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r3.toDTO(), true, null, "NoUser");
	    	
	    	IQuery q = buildQuery(ds, "AND");
	    	addNumericStatement(q, QueryOperation.LESS_THAN, Double.valueOf(5.5));
	    	
	    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
	    	
	    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
	    	
	    	assertEquals("Query returned wrong number of identifiers", 2, ids.length);
	    	assertTrue("IDs do not include "+r1.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
	    	assertTrue("IDs do not include "+r2.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
	    	assertFalse("IDs do include "+r3.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
	    	
	    	Integer count = queryDAO.executeQueryForCount(qId);
	    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(2), count);
    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    		fail("Exception: "+ex.getMessage());
    	}
    }
    
    public void testNumericE(){
    	try{
	    	IDataSet ds = buildDataset();
	    	Long dsId = repositoryDaoHibernate.saveDataSet(ds.toDTO());
	    	ds = repositoryDaoHibernate.getDataSet(dsId).toHibernate();
	    	repositoryDaoHibernate.publishDataSet(dsId);
	    	
	    	IRecord r1 = buildRecord(ds, ds.getGroup(0));
	    	addNumericResponse(r1, 4.0);
	    	IRecord r2 = buildRecord(ds, ds.getGroup(0));
	    	addNumericResponse(r2, 5.0);
	    	IRecord r3 = buildRecord(ds, ds.getGroup(0));
	    	addNumericResponse(r3, 6.0);
	    	
	    	repositoryDaoHibernate.saveRecord(r1.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r2.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r3.toDTO(), true, null, "NoUser");
	    	
	    	IQuery q = buildQuery(ds, "AND");
	    	addNumericStatement(q, QueryOperation.EQUALS, Double.valueOf(5.0));
	    	
	    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
	    	
	    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
	    	
	    	assertEquals("Query returned wrong number of identifiers", 1, ids.length);
	    	assertTrue("IDs do not include "+r2.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
	    	assertFalse("IDs do include "+r3.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
	    	assertFalse("IDs do include "+r1.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
	    	
	    	Integer count = queryDAO.executeQueryForCount(qId);
	    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(1), count);
    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    		fail("Exception: "+ex.getMessage());
    	}
    }
    
    public void testNumericNe(){
    	try{
	    	IDataSet ds = buildDataset();
	    	Long dsId = repositoryDaoHibernate.saveDataSet(ds.toDTO());
	    	ds = repositoryDaoHibernate.getDataSet(dsId).toHibernate();
	    	repositoryDaoHibernate.publishDataSet(dsId);

	    	IRecord r1 = buildRecord(ds, ds.getGroup(0));
	    	addNumericResponse(r1, 4.0);
	    	IRecord r2 = buildRecord(ds, ds.getGroup(0));
	    	addNumericResponse(r2, 5.0);
	    	IRecord r3 = buildRecord(ds, ds.getGroup(0));
	    	addNumericResponse(r3, 6.0);
	    	
	    	repositoryDaoHibernate.saveRecord(r1.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r2.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r3.toDTO(), true, null, "NoUser");
	    	
	    	IQuery q = buildQuery(ds, "AND");
	    	addNumericStatement(q, QueryOperation.NOT_EQUALS, Double.valueOf(5.0));
	    	
	    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
	    	
	    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
	    	
	    	assertEquals("Query returned wrong number of identifiers", 2, ids.length);
	    	assertTrue("IDs do not include "+r1.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
	    	assertTrue("IDs do not include "+r3.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
	    	assertFalse("IDs do include "+r2.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
	    	
	    	Integer count = queryDAO.executeQueryForCount(qId);
	    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(2), count);
    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    		fail("Exception: "+ex.getMessage());
    	}
    }
    
    public void testNumericGe(){
    	try{
	    	IDataSet ds = buildDataset();
	    	Long dsId = repositoryDaoHibernate.saveDataSet(ds.toDTO());
	    	ds = repositoryDaoHibernate.getDataSet(dsId).toHibernate();
	    	repositoryDaoHibernate.publishDataSet(dsId);

	    	IRecord r1 = buildRecord(ds, ds.getGroup(0));
	    	addNumericResponse(r1, 4.0);
	    	IRecord r2 = buildRecord(ds, ds.getGroup(0));
	    	addNumericResponse(r2, 5.0);
	    	IRecord r3 = buildRecord(ds, ds.getGroup(0));
	    	addNumericResponse(r3, 6.0);
	    	
	    	repositoryDaoHibernate.saveRecord(r1.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r2.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r3.toDTO(), true, null, "NoUser");
	    	
	    	IQuery q = buildQuery(ds, "AND");
	    	addNumericStatement(q, QueryOperation.GREATER_THAN_EQUALS, Double.valueOf(5.0));
	    	
	    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
	    	
	    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
	    	
	    	assertEquals("Query returned wrong number of identifiers", 2, ids.length);
	    	assertTrue("IDs do not include "+r2.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
	    	assertTrue("IDs do not include "+r3.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
	    	assertFalse("IDs do include "+r1.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
	    	
	    	Integer count = queryDAO.executeQueryForCount(qId);
	    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(2), count);
    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    		fail("Exception: "+ex.getMessage());
    	}
    }
    
    public void testNumericLe(){
    	try{
	    	IDataSet ds = buildDataset();
	    	Long dsId = repositoryDaoHibernate.saveDataSet(ds.toDTO());
	    	ds = repositoryDaoHibernate.getDataSet(dsId).toHibernate();
	    	repositoryDaoHibernate.publishDataSet(dsId);

	    	IRecord r1 = buildRecord(ds, ds.getGroup(0));
	    	addNumericResponse(r1, 4.0);
	    	IRecord r2 = buildRecord(ds, ds.getGroup(0));
	    	addNumericResponse(r2, 5.0);
	    	IRecord r3 = buildRecord(ds, ds.getGroup(0));
	    	addNumericResponse(r3, 6.0);
	    	
	    	repositoryDaoHibernate.saveRecord(r1.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r2.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r3.toDTO(), true, null, "NoUser");
	    	
	    	IQuery q = buildQuery(ds, "AND");
	    	addNumericStatement(q, QueryOperation.LESS_THAN_EQUALS, Double.valueOf(5.0));
	    	
	    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
	    	
	    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
	    	
	    	assertEquals("Query returned wrong number of identifiers", 2, ids.length);
	    	assertTrue("IDs do not include "+r1.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
	    	assertTrue("IDs do not include "+r2.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
	    	assertFalse("IDs do include "+r3.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
	    	
	    	Integer count = queryDAO.executeQueryForCount(qId);
	    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(2), count);
    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    		fail("Exception: "+ex.getMessage());
    	}
    }
    
    public void testIntegerGt(){
    	try{
	    	IDataSet ds = buildDataset();
	    	Long dsId = repositoryDaoHibernate.saveDataSet(ds.toDTO());
	    	ds = repositoryDaoHibernate.getDataSet(dsId).toHibernate();
	    	repositoryDaoHibernate.publishDataSet(dsId);

	    	IRecord r1 = buildRecord(ds, ds.getGroup(0));
	    	addIntegerResponse(r1, 4);
	    	IRecord r2 = buildRecord(ds, ds.getGroup(0));
	    	addIntegerResponse(r2, 5);
	    	IRecord r3 = buildRecord(ds, ds.getGroup(0));
	    	addIntegerResponse(r3, 6);
	    	
	    	repositoryDaoHibernate.saveRecord(r1.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r2.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r3.toDTO(), true, null, "NoUser");
	    	
	    	IQuery q = buildQuery(ds, "AND");
	    	addIntegerStatement(q, QueryOperation.GREATER_THAN, Integer.valueOf(4));
	    	
	    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
	    	
	    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
	    	
	    	assertEquals("Query returned wrong number of identifiers", 2, ids.length);
	    	assertTrue("IDs do not include "+r2.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
	    	assertTrue("IDs do not include "+r3.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
	    	assertFalse("IDs do include "+r1.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
	    	
	    	Integer count = queryDAO.executeQueryForCount(qId);
	    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(2), count);
    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    		fail("Exception: "+ex.getMessage());
    	}
    }
    
    public void testIntegerLt(){
    	try{
	    	IDataSet ds = buildDataset();
	    	Long dsId = repositoryDaoHibernate.saveDataSet(ds.toDTO());
	    	ds = repositoryDaoHibernate.getDataSet(dsId).toHibernate();
	    	repositoryDaoHibernate.publishDataSet(dsId);

	    	IRecord r1 = buildRecord(ds, ds.getGroup(0));
	    	addIntegerResponse(r1, 4);
	    	IRecord r2 = buildRecord(ds, ds.getGroup(0));
	    	addIntegerResponse(r2, 5);
	    	IRecord r3 = buildRecord(ds, ds.getGroup(0));
	    	addIntegerResponse(r3, 6);
	    	
	    	repositoryDaoHibernate.saveRecord(r1.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r2.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r3.toDTO(), true, null, "NoUser");
	    	
	    	IQuery q = buildQuery(ds, "AND");
	    	addIntegerStatement(q, QueryOperation.LESS_THAN, Integer.valueOf(6));
	    	
	    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
	    	
	    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
	    	
	    	assertEquals("Query returned wrong number of identifiers", 2, ids.length);
	    	assertTrue("IDs do not include "+r1.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
	    	assertTrue("IDs do not include "+r2.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
	    	assertFalse("IDs do include "+r3.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
	    	
	    	Integer count = queryDAO.executeQueryForCount(qId);
	    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(2), count);
    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    		fail("Exception: "+ex.getMessage());
    	}
    }
    
    public void testIntegerE(){
    	try{
	    	IDataSet ds = buildDataset();
	    	Long dsId = repositoryDaoHibernate.saveDataSet(ds.toDTO());
	    	ds = repositoryDaoHibernate.getDataSet(dsId).toHibernate();
	    	repositoryDaoHibernate.publishDataSet(dsId);

	    	IRecord r1 = buildRecord(ds, ds.getGroup(0));
	    	addIntegerResponse(r1, 4);
	    	IRecord r2 = buildRecord(ds, ds.getGroup(0));
	    	addIntegerResponse(r2, 5);
	    	IRecord r3 = buildRecord(ds, ds.getGroup(0));
	    	addIntegerResponse(r3, 6);
	    	
	    	repositoryDaoHibernate.saveRecord(r1.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r2.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r3.toDTO(), true, null, "NoUser");
	    	
	    	IQuery q = buildQuery(ds, "AND");
	    	addIntegerStatement(q, QueryOperation.EQUALS, Integer.valueOf(5));
	    	
	    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
	    	
	    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
	    	
	    	assertEquals("Query returned wrong number of identifiers", 1, ids.length);
	    	assertTrue("IDs do not include "+r2.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
	    	assertFalse("IDs do include "+r1.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
	    	assertFalse("IDs do include "+r3.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
	    	
	    	Integer count = queryDAO.executeQueryForCount(qId);
	    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(1), count);
    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    		fail("Exception: "+ex.getMessage());
    	}
    }
    
    public void testIntegerNe(){
    	try{
	    	IDataSet ds = buildDataset();
	    	Long dsId = repositoryDaoHibernate.saveDataSet(ds.toDTO());
	    	ds = repositoryDaoHibernate.getDataSet(dsId).toHibernate();
	    	repositoryDaoHibernate.publishDataSet(dsId);

	    	IRecord r1 = buildRecord(ds, ds.getGroup(0));
	    	addIntegerResponse(r1, 4);
	    	IRecord r2 = buildRecord(ds, ds.getGroup(0));
	    	addIntegerResponse(r2, 5);
	    	IRecord r3 = buildRecord(ds, ds.getGroup(0));
	    	addIntegerResponse(r3, 6);
	    	
	    	repositoryDaoHibernate.saveRecord(r1.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r2.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r3.toDTO(), true, null, "NoUser");
	    	
	    	IQuery q = buildQuery(ds, "AND");
	    	addIntegerStatement(q, QueryOperation.NOT_EQUALS, Integer.valueOf(5));
	    	
	    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
	    	
	    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
	    	
	    	assertEquals("Query returned wrong number of identifiers", 2, ids.length);
	    	assertTrue("IDs do not include "+r1.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
	    	assertTrue("IDs do not include "+r3.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
	    	assertFalse("IDs do include "+r2.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
	    	
	    	Integer count = queryDAO.executeQueryForCount(qId);
	    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(2), count);
    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    		fail("Exception: "+ex.getMessage());
    	}
    }
    
    public void testIntegerGe(){
    	try{
	    	IDataSet ds = buildDataset();
	    	Long dsId = repositoryDaoHibernate.saveDataSet(ds.toDTO());
	    	ds = repositoryDaoHibernate.getDataSet(dsId).toHibernate();
	    	repositoryDaoHibernate.publishDataSet(dsId);

	    	IRecord r1 = buildRecord(ds, ds.getGroup(0));
	    	addIntegerResponse(r1, 4);
	    	IRecord r2 = buildRecord(ds, ds.getGroup(0));
	    	addIntegerResponse(r2, 5);
	    	IRecord r3 = buildRecord(ds, ds.getGroup(0));
	    	addIntegerResponse(r3, 6);
	    	
	    	repositoryDaoHibernate.saveRecord(r1.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r2.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r3.toDTO(), true, null, "NoUser");
	    	
	    	IQuery q = buildQuery(ds, "AND");
	    	addIntegerStatement(q, QueryOperation.GREATER_THAN_EQUALS, Integer.valueOf(5));
	    	
	    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
	    	
	    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
	    	
	    	assertEquals("Query returned wrong number of identifiers", 2, ids.length);
	    	assertTrue("IDs do not include "+r2.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
	    	assertTrue("IDs do not include "+r3.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
	    	assertFalse("IDs do include "+r1.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
	    	
	    	Integer count = queryDAO.executeQueryForCount(qId);
	    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(2), count);
    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    		fail("Exception: "+ex.getMessage());
    	}
    }
    
    public void testIntegerLe(){
    	try{
	    	IDataSet ds = buildDataset();
	    	Long dsId = repositoryDaoHibernate.saveDataSet(ds.toDTO());
	    	ds = repositoryDaoHibernate.getDataSet(dsId).toHibernate();
	    	repositoryDaoHibernate.publishDataSet(dsId);

	    	IRecord r1 = buildRecord(ds, ds.getGroup(0));
	    	addIntegerResponse(r1, 4);
	    	IRecord r2 = buildRecord(ds, ds.getGroup(0));
	    	addIntegerResponse(r2, 5);
	    	IRecord r3 = buildRecord(ds, ds.getGroup(0));
	    	addIntegerResponse(r3, 6);
	    	
	    	repositoryDaoHibernate.saveRecord(r1.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r2.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r3.toDTO(), true, null, "NoUser");
	    	
	    	IQuery q = buildQuery(ds, "AND");
	    	addIntegerStatement(q, QueryOperation.LESS_THAN_EQUALS, Integer.valueOf(5));
	    	
	    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
	    	
	    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
	    	
	    	assertEquals("Query returned wrong number of identifiers", 2, ids.length);
	    	assertTrue("IDs do not include "+r1.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
	    	assertTrue("IDs do not include "+r2.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
	    	assertFalse("IDs do include "+r3.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
	    	
	    	Integer count = queryDAO.executeQueryForCount(qId);
	    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(2), count);
    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    		fail("Exception: "+ex.getMessage());
    	}
    }
    
    public void testDateE(){
    	try{
	    	IDataSet ds = buildDataset();
	    	Long dsId = repositoryDaoHibernate.saveDataSet(ds.toDTO());
	    	ds = repositoryDaoHibernate.getDataSet(dsId).toHibernate();
	    	repositoryDaoHibernate.publishDataSet(dsId);
	    	
	    	Date d2 = new Date();
	    	Date d1 = new Date(d2.getTime()-200000);
	    	Date d3 = new Date(d2.getTime()+200000);
	    	IRecord r1 = buildRecord(ds, ds.getGroup(0));
	    	addDateResponse(r1,d1);
	    	IRecord r2 = buildRecord(ds, ds.getGroup(0));
	    	addDateResponse(r2, d2);
	    	IRecord r3 = buildRecord(ds, ds.getGroup(0));
	    	addDateResponse(r3, d3);
	    	
	    	repositoryDaoHibernate.saveRecord(r1.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r2.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r3.toDTO(), true, null, "NoUser");
	    	
	    	IQuery q = buildQuery(ds, "AND");
	    	addDateStatement(q, QueryOperation.EQUALS, d2);
	    	
	    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
	    	
	    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
	    	
	    	assertEquals("Query returned wrong number of identifiers", 1, ids.length);
	    	assertTrue("IDs do not include "+r2.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
	    	assertFalse("IDs do include "+r1.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
	    	assertFalse("IDs do include "+r3.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
	    	
	    	Integer count = queryDAO.executeQueryForCount(qId);
	    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(1), count);
    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    		fail("Exception: "+ex.getMessage());
    	}
    }
    
    public void testDateNe(){
       	try{
	    	IDataSet ds = buildDataset();
	    	Long dsId = repositoryDaoHibernate.saveDataSet(ds.toDTO());
	    	ds = repositoryDaoHibernate.getDataSet(dsId).toHibernate();
	    	repositoryDaoHibernate.publishDataSet(dsId);
	    	
	    	Date d2 = new Date();
	    	Date d1 = new Date(d2.getTime()-200000);
	    	Date d3 = new Date(d2.getTime()+200000);
	    	IRecord r1 = buildRecord(ds, ds.getGroup(0));
	    	addDateResponse(r1,d1);
	    	IRecord r2 = buildRecord(ds, ds.getGroup(0));
	    	addDateResponse(r2, d2);
	    	IRecord r3 = buildRecord(ds, ds.getGroup(0));
	    	addDateResponse(r3, d3);
	    	
	    	repositoryDaoHibernate.saveRecord(r1.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r2.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r3.toDTO(), true, null, "NoUser");
	    	
	    	IQuery q = buildQuery(ds, "AND");
	    	addDateStatement(q, QueryOperation.NOT_EQUALS, d2);
	    	
	    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
	    	
	    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
	    	
	    	assertEquals("Query returned wrong number of identifiers", 2, ids.length);
	    	assertTrue("IDs do not include "+r1.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
	    	assertTrue("IDs do not include "+r3.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
	    	assertFalse("IDs do include "+r2.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
	    	
	    	Integer count = queryDAO.executeQueryForCount(qId);
	    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(2), count);
    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    		fail("Exception: "+ex.getMessage());
    	}
    }
    
    public void testDateBefore(){
       	try{
	    	IDataSet ds = buildDataset();
	    	Long dsId = repositoryDaoHibernate.saveDataSet(ds.toDTO());
	    	ds = repositoryDaoHibernate.getDataSet(dsId).toHibernate();
	    	repositoryDaoHibernate.publishDataSet(dsId);
	    	
	    	Date d2 = new Date();
	    	Date d1 = new Date(d2.getTime()-200000);
	    	Date d3 = new Date(d2.getTime()+200000);
	    	IRecord r1 = buildRecord(ds, ds.getGroup(0));
	    	addDateResponse(r1,d1);
	    	IRecord r2 = buildRecord(ds, ds.getGroup(0));
	    	addDateResponse(r2, d2);
	    	IRecord r3 = buildRecord(ds, ds.getGroup(0));
	    	addDateResponse(r3, d3);
	    	
	    	repositoryDaoHibernate.saveRecord(r1.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r2.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r3.toDTO(), true, null, "NoUser");
	    	
	    	IQuery q = buildQuery(ds, "AND");
	    	addDateStatement(q, QueryOperation.IS_BEFORE, d3);
	    	
	    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
	    	
	    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
	    	
	    	assertEquals("Query returned wrong number of identifiers", 2, ids.length);
	    	assertTrue("IDs do not include "+r1.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
	    	assertTrue("IDs do not include "+r2.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
	    	assertFalse("IDs do include "+r3.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
	    	
	    	Integer count = queryDAO.executeQueryForCount(qId);
	    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(2), count);
    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    		fail("Exception: "+ex.getMessage());
    	}
    }
    
    public void testDateAfter(){
       	try{
	    	IDataSet ds = buildDataset();
	    	Long dsId = repositoryDaoHibernate.saveDataSet(ds.toDTO());
	    	ds = repositoryDaoHibernate.getDataSet(dsId).toHibernate();
	    	repositoryDaoHibernate.publishDataSet(dsId);
	    	
	    	Date d2 = new Date();
	    	Date d1 = new Date(d2.getTime()-200000);
	    	Date d3 = new Date(d2.getTime()+200000);
	    	IRecord r1 = buildRecord(ds, ds.getGroup(0));
	    	addDateResponse(r1,d1);
	    	IRecord r2 = buildRecord(ds, ds.getGroup(0));
	    	addDateResponse(r2, d2);
	    	IRecord r3 = buildRecord(ds, ds.getGroup(0));
	    	addDateResponse(r3, d3);
	    	
	    	repositoryDaoHibernate.saveRecord(r1.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r2.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r3.toDTO(), true, null, "NoUser");
	    	
	    	IQuery q = buildQuery(ds, "AND");
	    	addDateStatement(q, QueryOperation.IS_AFTER, d1);
	    	
	    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
	    	
	    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
	    	
	    	assertEquals("Query returned wrong number of identifiers", 2, ids.length);
	    	assertTrue("IDs do not include "+r2.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
	    	assertTrue("IDs do not include "+r3.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
	    	assertFalse("IDs do include "+r1.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
	    	
	    	Integer count = queryDAO.executeQueryForCount(qId);
	    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(2), count);
    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    		fail("Exception: "+ex.getMessage());
    	}
    }
    
    public void testOptionE(){
       	try{
	    	IDataSet ds = buildDataset();
	    	Long dsId = repositoryDaoHibernate.saveDataSet(ds.toDTO());
	    	ds = repositoryDaoHibernate.getDataSet(dsId).toHibernate();
	    	repositoryDaoHibernate.publishDataSet(dsId);
	    	
	    	IRecord r1 = buildRecord(ds, ds.getGroup(0));
	    	addOptionResponse(r1, 0);
	    	IRecord r2 = buildRecord(ds, ds.getGroup(0));
	    	addOptionResponse(r2, 1);
	    	IRecord r3 = buildRecord(ds, ds.getGroup(0));
	    	addOptionResponse(r3, 2);
	    	IRecord r4 = buildRecord(ds, ds.getGroup(0));
	    	addOptionResponse(r4, 0);
	    	
	    	repositoryDaoHibernate.saveRecord(r1.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r2.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r3.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r4.toDTO(), true, null, "NoUser");
	    	
	    	IQuery q = buildQuery(ds, "AND");
	    	addOptionStatement(q, QueryOperation.EQUALS, 0);
	    	
	    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
	    	
	    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
	    	
	    	assertEquals("Query returned wrong number of identifiers", 2, ids.length);
	    	assertTrue("IDs do not include "+r1.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
	    	assertTrue("IDs do not include "+r4.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r4.getIdentifier().getIdentifier()));
	    	assertFalse("IDs do include "+r2.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
	    	assertFalse("IDs do include "+r3.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
	    	
	    	Integer count = queryDAO.executeQueryForCount(qId);
	    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(2), count);
    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    		fail("Exception: "+ex.getMessage());
    	}
    }
    
    public void testOptionNe(){
       	try{
	    	IDataSet ds = buildDataset();
	    	Long dsId = repositoryDaoHibernate.saveDataSet(ds.toDTO());
	    	ds = repositoryDaoHibernate.getDataSet(dsId).toHibernate();
	    	repositoryDaoHibernate.publishDataSet(dsId);
	    	
	    	IRecord r1 = buildRecord(ds, ds.getGroup(0));
	    	addOptionResponse(r1, 0);
	    	IRecord r2 = buildRecord(ds, ds.getGroup(0));
	    	addOptionResponse(r2, 1);
	    	IRecord r3 = buildRecord(ds, ds.getGroup(0));
	    	addOptionResponse(r3, 2);
	    	IRecord r4 = buildRecord(ds, ds.getGroup(0));
	    	addOptionResponse(r4, 0);
	    	
	    	repositoryDaoHibernate.saveRecord(r1.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r2.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r3.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r4.toDTO(), true, null, "NoUser");
	    	
	    	IQuery q = buildQuery(ds, "AND");
	    	addOptionStatement(q, QueryOperation.NOT_EQUALS, 0);
	    	
	    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
	    	
	    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
	    	
	    	assertEquals("Query returned wrong number of identifiers", 2, ids.length);
	    	assertTrue("IDs do not include "+r2.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
	    	assertTrue("IDs do not include "+r3.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
	    	assertFalse("IDs do include "+r1.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
	    	assertFalse("IDs do include "+r4.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r4.getIdentifier().getIdentifier()));
	    	
	    	Integer count = queryDAO.executeQueryForCount(qId);
	    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(2), count);
    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    		fail("Exception: "+ex.getMessage());
    	}
    }
    
    public void testNull(){
       	try{
	    	IDataSet ds = buildDataset();
	    	Long dsId = repositoryDaoHibernate.saveDataSet(ds.toDTO());
	    	ds = repositoryDaoHibernate.getDataSet(dsId).toHibernate();
	    	repositoryDaoHibernate.publishDataSet(dsId);
	    	
	    	IRecord r1 = buildRecord(ds, ds.getGroup(0));
	    	addNumericResponse(r1, 5.5);
	    	//no integer response
	    	addDateNullResponse2(r1);
	    	addOptionNullResponse1(r1);

	    	IRecord r2 = buildRecord(ds, ds.getGroup(0));
	    	addNumericCodeResponse(r2);
	    	addIntegerResponse(r2, 7);
	    	//no date response
	    	addOptionNullResponse2(r2);

	    	IRecord r3 = buildRecord(ds, ds.getGroup(0));
	    	addNumericNullResponse1(r3);
	    	addIntegerCodeResponse(r3);
	    	addDateResponse(r3, new Date());
	    	//no option response
	    	
	    	IRecord r4 = buildRecord(ds, ds.getGroup(0));
	    	addNumericNullResponse2(r4);
	    	addIntegerNullResponse1(r4);
	    	addDateCodeResponse(r4);
	    	addOptionResponse(r4, 0);

	    	IRecord r5 = buildRecord(ds, ds.getGroup(0));
	    	//no numeric response
	    	addIntegerNullResponse2(r5);
	    	addDateNullResponse1(r5);
	    	addOptionCodeResponse(r5);
	    	
	    	repositoryDaoHibernate.saveRecord(r1.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r2.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r3.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r4.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r5.toDTO(), true, null, "NoUser");
	    	
	    	{
		    	IQuery q = buildQuery(ds, "AND");
		    	addNumericStatement(q, QueryOperation.IS_NULL, null);
		    	
		    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
		    	
		    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
		    	
		    	assertEquals("Query 1 returned wrong number of identifiers", 3, ids.length);
		    	assertFalse("1 IDs do include "+r1.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
		    	assertFalse("1 IDs do include "+r2.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
		    	assertTrue("1 IDs do not include "+r3.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
		    	assertTrue("1 IDs do not include "+r4.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r4.getIdentifier().getIdentifier()));
		    	assertTrue("1 IDs do not include "+r5.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r5.getIdentifier().getIdentifier()));

		    	Integer count = queryDAO.executeQueryForCount(qId);
		    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(3), count);
	    	}
	    	
	    	{
		    	IQuery q = buildQuery(ds, "AND");
		    	addIntegerStatement(q, QueryOperation.IS_NULL, null);
		    	
		    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
		    	
		    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
		    	
		    	assertEquals("Query 2 returned wrong number of identifiers", 3, ids.length);
		    	assertTrue("2 IDs do not include "+r1.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
		    	assertFalse("2 IDs do include "+r2.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
		    	assertFalse("2 IDs do include "+r3.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
		    	assertTrue("2 IDs do not include "+r4.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r4.getIdentifier().getIdentifier()));
		    	assertTrue("2 IDs do not include "+r5.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r5.getIdentifier().getIdentifier()));

		    	Integer count = queryDAO.executeQueryForCount(qId);
		    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(3), count);
	    	}

	    	{
		    	IQuery q = buildQuery(ds, "AND");
		    	addDateStatement(q, QueryOperation.IS_NULL, null);
		    	
		    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
		    	
		    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
		    	
		    	assertEquals("Query 3 returned wrong number of identifiers", 3, ids.length);
		    	assertTrue("3 IDs do not include "+r1.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
		    	assertTrue("3 IDs do not include "+r2.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
		    	assertFalse("3 IDs do include "+r3.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
		    	assertFalse("3 IDs do include "+r4.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r4.getIdentifier().getIdentifier()));
		    	assertTrue("3 IDs do not include "+r5.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r5.getIdentifier().getIdentifier()));

		    	Integer count = queryDAO.executeQueryForCount(qId);
		    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(3), count);
	    	}

	    	{
		    	IQuery q = buildQuery(ds, "AND");
		    	addOptionStatement(q, QueryOperation.IS_NULL, null);
		    	
		    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
		    	
		    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
		    	
		    	assertEquals("Query 4 returned wrong number of identifiers", 3, ids.length);
		    	assertTrue("4 IDs do not include "+r1.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
		    	assertTrue("4 IDs do not include "+r2.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
		    	assertTrue("4 IDs do not include "+r3.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
		    	assertFalse("4 IDs do include "+r4.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r4.getIdentifier().getIdentifier()));
		    	assertFalse("4 IDs do include "+r5.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r5.getIdentifier().getIdentifier()));

		    	Integer count = queryDAO.executeQueryForCount(qId);
		    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(3), count);
	    	}

    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    		fail("Exception: "+ex.getMessage());
    	}
    }
    
    public void testNotNull(){
      	try{
	    	IDataSet ds = buildDataset();
	    	Long dsId = repositoryDaoHibernate.saveDataSet(ds.toDTO());
	    	ds = repositoryDaoHibernate.getDataSet(dsId).toHibernate();
	    	repositoryDaoHibernate.publishDataSet(dsId);
	    	
	    	IRecord r1 = buildRecord(ds, ds.getGroup(0));
	    	addNumericResponse(r1, 5.5);
	    	//no integer response
	    	addDateNullResponse2(r1);
	    	addOptionNullResponse1(r1);

	    	IRecord r2 = buildRecord(ds, ds.getGroup(0));
	    	addNumericCodeResponse(r2);
	    	addIntegerResponse(r2, 7);
	    	//no date response
	    	addOptionNullResponse2(r2);

	    	IRecord r3 = buildRecord(ds, ds.getGroup(0));
	    	addNumericNullResponse1(r3);
	    	addIntegerCodeResponse(r3);
	    	addDateResponse(r3, new Date());
	    	//no option response
	    	
	    	IRecord r4 = buildRecord(ds, ds.getGroup(0));
	    	addNumericNullResponse2(r4);
	    	addIntegerNullResponse1(r4);
	    	addDateCodeResponse(r4);
	    	addOptionResponse(r4, 0);

	    	IRecord r5 = buildRecord(ds, ds.getGroup(0));
	    	//no numeric response
	    	addIntegerNullResponse2(r5);
	    	addDateNullResponse1(r5);
	    	addOptionCodeResponse(r5);
	    	
	    	repositoryDaoHibernate.saveRecord(r1.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r2.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r3.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r4.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r5.toDTO(), true, null, "NoUser");
	    	
	    	{
		    	IQuery q = buildQuery(ds, "AND");
		    	addNumericStatement(q, QueryOperation.IS_NOT_NULL, null);
		    	
		    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
		    	
		    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
		    	
		    	assertEquals("Query 1 returned wrong number of identifiers", 2, ids.length);
		    	assertTrue("1 IDs do not include "+r1.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
		    	assertTrue("1 IDs do not include "+r2.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
		    	assertFalse("1 IDs do include "+r3.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
		    	assertFalse("1 IDs do include "+r4.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r4.getIdentifier().getIdentifier()));
		    	assertFalse("1 IDs do include "+r5.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r5.getIdentifier().getIdentifier()));

		    	Integer count = queryDAO.executeQueryForCount(qId);
		    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(2), count);
	    	}
	    	
	    	{
		    	IQuery q = buildQuery(ds, "AND");
		    	addIntegerStatement(q, QueryOperation.IS_NOT_NULL, null);
		    	
		    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
		    	
		    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
		    	
		    	assertEquals("Query 2 returned wrong number of identifiers", 2, ids.length);
		    	assertFalse("2 IDs do include "+r1.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
		    	assertTrue("2 IDs do not include "+r2.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
		    	assertTrue("2 IDs do not include "+r3.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
		    	assertFalse("2 IDs do include "+r4.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r4.getIdentifier().getIdentifier()));
		    	assertFalse("2 IDs do include "+r5.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r5.getIdentifier().getIdentifier()));

		    	Integer count = queryDAO.executeQueryForCount(qId);
		    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(2), count);
	    	}

	    	{
		    	IQuery q = buildQuery(ds, "AND");
		    	addDateStatement(q, QueryOperation.IS_NOT_NULL, null);
		    	
		    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
		    	
		    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
		    	
		    	assertEquals("Query 3 returned wrong number of identifiers", 2, ids.length);
		    	assertFalse("3 IDs do include "+r1.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
		    	assertFalse("3 IDs do include "+r2.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
		    	assertTrue("3 IDs do not include "+r3.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
		    	assertTrue("3 IDs do not include "+r4.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r4.getIdentifier().getIdentifier()));
		    	assertFalse("3 IDs do include "+r5.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r5.getIdentifier().getIdentifier()));

		    	Integer count = queryDAO.executeQueryForCount(qId);
		    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(2), count);
	    	}

	    	{
		    	IQuery q = buildQuery(ds, "AND");
		    	addOptionStatement(q, QueryOperation.IS_NOT_NULL, null);
		    	
		    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
		    	
		    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
		    	
		    	assertEquals("Query 4 returned wrong number of identifiers", 2, ids.length);
		    	assertFalse("4 IDs do include "+r1.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
		    	assertFalse("4 IDs do include "+r2.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
		    	assertFalse("4 IDs do include "+r3.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
		    	assertTrue("4 IDs do not include "+r4.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r4.getIdentifier().getIdentifier()));
		    	assertTrue("4 IDs do not include "+r5.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r5.getIdentifier().getIdentifier()));

		    	Integer count = queryDAO.executeQueryForCount(qId);
		    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(2), count);
	    	}

    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    		fail("Exception: "+ex.getMessage());
    	}
    }
    
    public void testNumGeAndIntGe(){
    	try{
	    	IDataSet ds = buildDataset();
	    	Long dsId = repositoryDaoHibernate.saveDataSet(ds.toDTO());
	    	ds = repositoryDaoHibernate.getDataSet(dsId).toHibernate();
	    	repositoryDaoHibernate.publishDataSet(dsId);
	    	
	    	IRecord r1 = buildRecord(ds, ds.getGroup(0));
	    	addNumericResponse(r1, 4);
	    	addIntegerResponse(r1, 9);
	    	
	    	IRecord r2 = buildRecord(ds, ds.getGroup(0));
	    	addNumericResponse(r2, 5);
	    	addIntegerResponse(r2, 9);
	    	
	    	IRecord r3 = buildRecord(ds, ds.getGroup(0));
	    	addNumericResponse(r3, 4);
	    	addIntegerResponse(r3, 10);
	    	
	    	IRecord r4 = buildRecord(ds, ds.getGroup(0));
	    	addNumericResponse(r4, 5);
	    	addIntegerResponse(r4, 10);
	    	
	    	repositoryDaoHibernate.saveRecord(r1.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r2.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r3.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r4.toDTO(), true, null, "NoUser");

	    	IQuery q = buildQuery(ds, "AND");
	    	addNumericStatement(q, QueryOperation.GREATER_THAN_EQUALS, Double.valueOf(5));
	    	addIntegerStatement(q, QueryOperation.GREATER_THAN_EQUALS, Integer.valueOf(10));
	    	
	    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
	    	
	    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);

	    	assertEquals("Query returned wrong number of identifiers", 1, ids.length);
	    	assertFalse("4 IDs do include "+r1.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
	    	assertFalse("4 IDs do include "+r2.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
	    	assertFalse("4 IDs do include "+r3.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
	    	assertTrue("4 IDs do not include "+r4.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r4.getIdentifier().getIdentifier()));
	    	
	    	Integer count = queryDAO.executeQueryForCount(qId);
	    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(1), count);
	    	
    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    		fail("Exception: "+ex.getMessage());
    	}

    }
    
    public void testNumGeOrIntGe(){
    	try{
	    	IDataSet ds = buildDataset();
	    	Long dsId = repositoryDaoHibernate.saveDataSet(ds.toDTO());
	    	ds = repositoryDaoHibernate.getDataSet(dsId).toHibernate();
	    	repositoryDaoHibernate.publishDataSet(dsId);
	    	
	    	IRecord r1 = buildRecord(ds, ds.getGroup(0));
	    	addNumericResponse(r1, 4);
	    	addIntegerResponse(r1, 9);
	    	
	    	IRecord r2 = buildRecord(ds, ds.getGroup(0));
	    	addNumericResponse(r2, 5);
	    	addIntegerResponse(r2, 9);
	    	
	    	IRecord r3 = buildRecord(ds, ds.getGroup(0));
	    	addNumericResponse(r3, 4);
	    	addIntegerResponse(r3, 10);
	    	
	    	IRecord r4 = buildRecord(ds, ds.getGroup(0));
	    	addNumericResponse(r4, 5);
	    	addIntegerResponse(r4, 10);
	    	
	    	repositoryDaoHibernate.saveRecord(r1.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r2.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r3.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r4.toDTO(), true, null, "NoUser");

	    	IQuery q = buildQuery(ds, "OR");
	    	addNumericStatement(q, QueryOperation.GREATER_THAN_EQUALS, Double.valueOf(5));
	    	addIntegerStatement(q, QueryOperation.GREATER_THAN_EQUALS, Integer.valueOf(10));
	    	
	    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
	    	
	    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);

	    	assertEquals("Query returned wrong number of identifiers", 3, ids.length);
	    	assertFalse("4 IDs do include "+r1.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
	    	assertTrue("4 IDs do not include "+r2.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
	    	assertTrue("4 IDs do not include "+r3.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
	    	assertTrue("4 IDs do not include "+r4.getIdentifier().getIdentifier(), 
	    			idsInclude(ids, r4.getIdentifier().getIdentifier()));
	    	

	    	Integer count = queryDAO.executeQueryForCount(qId);
	    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(3), count);
    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    		fail("Exception: "+ex.getMessage());
    	}
    }
    
    
    public void testCode(){
       	try{
	    	IDataSet ds = buildDataset();
	    	Long dsId = repositoryDaoHibernate.saveDataSet(ds.toDTO());
	    	ds = repositoryDaoHibernate.getDataSet(dsId).toHibernate();
	    	repositoryDaoHibernate.publishDataSet(dsId);
	    	
	    	IRecord r1 = buildRecord(ds, ds.getGroup(0));
	    	addNumericCodeResponse(r1);
	    	addIntegerResponse(r1, 4);
	    	addDateResponse(r1, new Date());
	    	addOptionResponse(r1, 0);
	    	IRecord r2 = buildRecord(ds, ds.getGroup(0));
	    	addIntegerCodeResponse(r2);
	    	addNumericResponse(r2, 4.5);
	    	addDateResponse(r2, new Date());
	    	addOptionResponse(r2, 0);
	    	IRecord r3 = buildRecord(ds, ds.getGroup(0));
	    	addDateCodeResponse(r3);
	    	addNumericResponse(r3, 4.5);
	    	addIntegerResponse(r3, 4);
	    	addOptionResponse(r3, 0);
	    	IRecord r4 = buildRecord(ds, ds.getGroup(0));
	    	addOptionCodeResponse(r4);
	    	addNumericResponse(r4, 4.5);
	    	addIntegerResponse(r4, 4);
	    	addDateResponse(r4, new Date());
	    	
	    	repositoryDaoHibernate.saveRecord(r1.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r2.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r3.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r4.toDTO(), true, null, "NoUser");
	    	
	    	{
		    	IQuery q = buildQuery(ds, "AND");
		    	addNumericStatement(q, QueryOperation.IS_MISSING, null);
		    	
		    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
		    	
		    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
		    	
		    	assertEquals("Query 1 returned wrong number of identifiers", 1, ids.length);
		    	assertTrue("1 IDs do not include "+r1.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
		    	assertFalse("1 IDs do include "+r2.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
		    	assertFalse("1 IDs do include "+r3.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
		    	assertFalse("1 IDs do include "+r4.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r4.getIdentifier().getIdentifier()));

		    	Integer count = queryDAO.executeQueryForCount(qId);
		    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(1), count);
	    	}
	    	
	    	{
		    	IQuery q = buildQuery(ds, "AND");
		    	addIntegerStatement(q, QueryOperation.IS_MISSING, null);
		    	
		    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
		    	
		    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
		    	
		    	assertEquals("Query 2 returned wrong number of identifiers", 1, ids.length);
		    	assertFalse("2 IDs do include "+r1.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
		    	assertTrue("2 IDs do not include "+r2.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
		    	assertFalse("2 IDs do include "+r3.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
		    	assertFalse("2 IDs do include "+r4.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r4.getIdentifier().getIdentifier()));

		    	Integer count = queryDAO.executeQueryForCount(qId);
		    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(1), count);
	    	}

	    	{
		    	IQuery q = buildQuery(ds, "AND");
		    	addDateStatement(q, QueryOperation.IS_MISSING, null);
		    	
		    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
		    	
		    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
		    	
		    	assertEquals("Query 3 returned wrong number of identifiers", 1, ids.length);
		    	assertFalse("3 IDs do include "+r1.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
		    	assertFalse("3 IDs do include "+r2.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
		    	assertTrue("3 IDs do not include "+r3.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
		    	assertFalse("3 IDs do include "+r4.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r4.getIdentifier().getIdentifier()));
		    	
		    	Integer count = queryDAO.executeQueryForCount(qId);
		    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(1), count);
	    	}

	    	{
		    	IQuery q = buildQuery(ds, "AND");
		    	addOptionStatement(q, QueryOperation.IS_MISSING, null);
		    	
		    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
		    	
		    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
		    	
		    	assertEquals("Query 4 returned wrong number of identifiers", 1, ids.length);
		    	assertFalse("4 IDs do include "+r1.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
		    	assertFalse("4 IDs do include "+r2.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
		    	assertFalse("4 IDs do include "+r3.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
		    	assertTrue("4 IDs do not include "+r4.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r4.getIdentifier().getIdentifier()));
		    	
		    	Integer count = queryDAO.executeQueryForCount(qId);
		    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(1), count);
	    	}

    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    		fail("Exception: "+ex.getMessage());
    	}
    }
    
    public void testNotCode(){
       	try{
	    	IDataSet ds = buildDataset();
	    	Long dsId = repositoryDaoHibernate.saveDataSet(ds.toDTO());
	    	ds = repositoryDaoHibernate.getDataSet(dsId).toHibernate();
	    	repositoryDaoHibernate.publishDataSet(dsId);
	    	
	    	IRecord r1 = buildRecord(ds, ds.getGroup(0));
	    	addNumericCodeResponse(r1);
	    	addIntegerResponse(r1, 4);
	    	addDateResponse(r1, new Date());
	    	addOptionResponse(r1, 0);
	    	IRecord r2 = buildRecord(ds, ds.getGroup(0));
	    	addIntegerCodeResponse(r2);
	    	addNumericResponse(r2, 4.5);
	    	addDateResponse(r2, new Date());
	    	addOptionResponse(r2, 0);
	    	IRecord r3 = buildRecord(ds, ds.getGroup(0));
	    	addDateCodeResponse(r3);
	    	addNumericResponse(r3, 4.5);
	    	addIntegerResponse(r3, 4);
	    	addOptionResponse(r3, 0);
	    	IRecord r4 = buildRecord(ds, ds.getGroup(0));
	    	addOptionCodeResponse(r4);
	    	addNumericResponse(r4, 4.5);
	    	addIntegerResponse(r4, 4);
	    	addDateResponse(r4, new Date());
	    	
	    	repositoryDaoHibernate.saveRecord(r1.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r2.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r3.toDTO(), true, null, "NoUser");
	    	repositoryDaoHibernate.saveRecord(r4.toDTO(), true, null, "NoUser");
	    	
	    	{
		    	IQuery q = buildQuery(ds, "AND");
		    	addNumericStatement(q, QueryOperation.IS_NOT_MISSING, null);
		    	
		    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
		    	
		    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
		    	
		    	assertEquals("Query 1 returned wrong number of identifiers", 3, ids.length);
		    	assertFalse("1 IDs do include "+r1.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
		    	assertTrue("1 IDs do not include "+r2.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
		    	assertTrue("1 IDs do not include "+r3.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
		    	assertTrue("1 IDs do not include "+r4.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r4.getIdentifier().getIdentifier()));
		    	
		    	Integer count = queryDAO.executeQueryForCount(qId);
		    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(3), count);
	    	}
	    	
	    	{
		    	IQuery q = buildQuery(ds, "AND");
		    	addIntegerStatement(q, QueryOperation.IS_NOT_MISSING, null);
		    	
		    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
		    	
		    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
		    	
		    	assertEquals("Query 2 returned wrong number of identifiers", 3, ids.length);
		    	assertTrue("2 IDs do not include "+r1.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
		    	assertFalse("2 IDs do include "+r2.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
		    	assertTrue("2 IDs do not include "+r3.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
		    	assertTrue("2 IDs do not include "+r4.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r4.getIdentifier().getIdentifier()));
		    	
		    	Integer count = queryDAO.executeQueryForCount(qId);
		    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(3), count);
	    	}

	    	{
		    	IQuery q = buildQuery(ds, "AND");
		    	addDateStatement(q, QueryOperation.IS_NOT_MISSING, null);
		    	
		    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
		    	
		    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
		    	
		    	assertEquals("Query 3 returned wrong number of identifiers", 3, ids.length);
		    	assertTrue("3 IDs do not include "+r1.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
		    	assertTrue("3 IDs do not include "+r2.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
		    	assertFalse("3 IDs do include "+r3.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
		    	assertTrue("3 IDs do not include "+r4.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r4.getIdentifier().getIdentifier()));
		    	
		    	Integer count = queryDAO.executeQueryForCount(qId);
		    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(3), count);
	    	}

	    	{
		    	IQuery q = buildQuery(ds, "AND");
		    	addOptionStatement(q, QueryOperation.IS_NOT_MISSING, null);
		    	
		    	Long qId = queryDAO.saveQuery(q.toDTO(), "NoUser");
		    	
		    	String[] ids = queryDAO.executeQueryForIdentifiers(qId);
		    	
		    	assertEquals("Query 4 returned wrong number of identifiers", 3, ids.length);
		    	assertTrue("4 IDs do not include "+r1.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r1.getIdentifier().getIdentifier()));
		    	assertTrue("4 IDs do not include "+r2.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r2.getIdentifier().getIdentifier()));
		    	assertTrue("4 IDs do not include "+r3.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r3.getIdentifier().getIdentifier()));
		    	assertFalse("4 IDs do include "+r4.getIdentifier().getIdentifier(), 
		    			idsInclude(ids, r4.getIdentifier().getIdentifier()));
		    	
		    	Integer count = queryDAO.executeQueryForCount(qId);
		    	assertEquals("executeQueryForCount returned wrong number of identifiers", Integer.valueOf(3), count);
	    	}

    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    		fail("Exception: "+ex.getMessage());
    	}
    }
   */ 
    private DataSet buildDataset(){
        String name = "querytests - "+(new Date()).toString();
        DataSet ds = factory.createDataset(name);
        java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
        String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
        ds.setProjectCode(projectCode);
        Group grp1 = factory.createGroup("FOO");
        ds.addGroup(grp1);
        Site grp1Site = new Site("site 1", "site 1", "site 1");
        grp1.addSite(grp1Site);
        Group grp2 = factory.createGroup("BAR");
        ds.addGroup(grp2);
        Site grp2Site = new Site("site 1", "site 1", "site 1");
        grp2.addSite(grp2Site);
        
        Status status = factory.createStatus("status", 1);
        status.setGenericState(GenericState.ACTIVE);
        ds.addStatus(status);
        
        DocumentGroup group = factory.createDocumentGroup("group");
        ds.addDocumentGroup(group);
        
        Document testDoc1 = factory.createDocument("test doc 1");
        ds.addDocument(testDoc1);
        
        DocumentOccurrence testDoc1Occ1 = factory.createDocumentOccurrence("doc 1 occ 1");
        testDoc1Occ1.setDocumentGroup(group);
        testDoc1.addOccurrence(testDoc1Occ1);
        
        Section sec1 = factory.createSection("Sec 1");
        testDoc1.addSection(sec1);
        SectionOccurrence sec1Occ1 = factory.createSectionOccurrence("Sec 1 occ 1");
        sec1.addOccurrence(sec1Occ1);
        
        NumericEntry ne1 = factory.createNumericEntry("NE1");
        ne1.setSection(sec1);
        testDoc1.addEntry(ne1);
        
        IntegerEntry ie1 = factory.createIntegerEntry("IE1");
        ie1.setSection(sec1);
        testDoc1.addEntry(ie1);
        
        DateEntry de1 = factory.createDateEntry("DE1");
        de1.setSection(sec1);
        testDoc1.addEntry(de1);
        
        OptionEntry oe1 = factory.createOptionEntry("OE1");
        oe1.addOption(factory.createOption("Option 1"));
        oe1.addOption(factory.createOption("Option 2"));
        oe1.addOption(factory.createOption("Option 3"));
        oe1.setSection(sec1);
        testDoc1.addEntry(oe1);
        
        return ds;
    }
    
    private Record buildRecord(DataSet ds, Group group) throws Exception {
    	Identifier[] ids = DAOTestHelper.getIdentifiers(repositoryDaoHibernate, ds.getProjectCode(), ds.getId(), 1, group.getName());
    	Record r = ds.generateInstance();
    	r.setIdentifier(ids[0]);
    	r.setSite(group.getSite(0));
    	DocumentInstance docInst = ds.getDocument(0).generateInstance(ds.getDocument(0).getOccurrence(0));
    	r.addDocumentInstance(docInst);
    	return r;
    }
    
    private void addNumericResponse(Record r, double value){
    	NumericEntry ne = (NumericEntry)r.getDataSet().getDocument(0).getEntry(0);
    	BasicResponse resp = ne.generateInstance(r.getDataSet().getDocument(0).getSection(0).getOccurrence(0));
    	INumericValue nv = ne.generateValue();
    	nv.setValue(Double.valueOf(value));
    	resp.setValue(nv);
    	DocumentInstance docInst = r.getDocumentInstance(r.getDataSet().getDocument(0).getOccurrence(0));
    	docInst.addResponse(resp);
    }

    private void addNumericCodeResponse(Record r){
    	NumericEntry ne = (NumericEntry)r.getDataSet().getDocument(0).getEntry(0);
    	BasicResponse resp = ne.generateInstance(r.getDataSet().getDocument(0).getSection(0).getOccurrence(0));
    	INumericValue nv = ne.generateValue();
    	nv.setStandardCode(codes.get(0));
    	resp.setValue(nv);
    	DocumentInstance docInst = r.getDocumentInstance(r.getDataSet().getDocument(0).getOccurrence(0));
    	docInst.addResponse(resp);
    }

    private void addNumericNullResponse1(Record r){
    	NumericEntry ne = (NumericEntry)r.getDataSet().getDocument(0).getEntry(0);
    	BasicResponse resp = ne.generateInstance(r.getDataSet().getDocument(0).getSection(0).getOccurrence(0));
    	DocumentInstance docInst = r.getDocumentInstance(r.getDataSet().getDocument(0).getOccurrence(0));
    	docInst.addResponse(resp);
    }

    private void addNumericNullResponse2(Record r){
    	NumericEntry ne = (NumericEntry)r.getDataSet().getDocument(0).getEntry(0);
    	BasicResponse resp = ne.generateInstance(r.getDataSet().getDocument(0).getSection(0).getOccurrence(0));
    	INumericValue nv = ne.generateValue();
    	resp.setValue(nv);
    	DocumentInstance docInst = r.getDocumentInstance(r.getDataSet().getDocument(0).getOccurrence(0));
    	docInst.addResponse(resp);
    }

    private void addIntegerResponse(Record r, int value){
    	IntegerEntry ie = (IntegerEntry)r.getDataSet().getDocument(0).getEntry(1);
    	BasicResponse resp = ie.generateInstance(r.getDataSet().getDocument(0).getSection(0).getOccurrence(0));
    	IIntegerValue iv = ie.generateValue();
    	iv.setValue(Integer.valueOf(value));
    	resp.setValue(iv);
    	DocumentInstance docInst = r.getDocumentInstance(r.getDataSet().getDocument(0).getOccurrence(0));
    	docInst.addResponse(resp);
    }
    private void addIntegerCodeResponse(Record r){
    	IntegerEntry ie = (IntegerEntry)r.getDataSet().getDocument(0).getEntry(1);
    	BasicResponse resp = ie.generateInstance(r.getDataSet().getDocument(0).getSection(0).getOccurrence(0));
    	IIntegerValue iv = ie.generateValue();
    	iv.setStandardCode(codes.get(1));
    	resp.setValue(iv);
    	DocumentInstance docInst = r.getDocumentInstance(r.getDataSet().getDocument(0).getOccurrence(0));
    	docInst.addResponse(resp);
    }
    private void addIntegerNullResponse1(Record r){
    	IntegerEntry ie = (IntegerEntry)r.getDataSet().getDocument(0).getEntry(1);
    	BasicResponse resp = ie.generateInstance(r.getDataSet().getDocument(0).getSection(0).getOccurrence(0));
    	DocumentInstance docInst = r.getDocumentInstance(r.getDataSet().getDocument(0).getOccurrence(0));
    	docInst.addResponse(resp);
    }
    private void addIntegerNullResponse2(Record r){
    	IntegerEntry ie = (IntegerEntry)r.getDataSet().getDocument(0).getEntry(1);
    	BasicResponse resp = ie.generateInstance(r.getDataSet().getDocument(0).getSection(0).getOccurrence(0));
    	IIntegerValue iv = ie.generateValue();
    	resp.setValue(iv);
    	DocumentInstance docInst = r.getDocumentInstance(r.getDataSet().getDocument(0).getOccurrence(0));
    	docInst.addResponse(resp);
    }
    private void addDateResponse(Record r, Date value){
    	DateEntry de = (DateEntry)r.getDataSet().getDocument(0).getEntry(2);
    	BasicResponse resp = de.generateInstance(r.getDataSet().getDocument(0).getSection(0).getOccurrence(0));
    	IDateValue dv = de.generateValue();
    	dv.setValue(value);
    	resp.setValue(dv);
    	DocumentInstance docInst = r.getDocumentInstance(r.getDataSet().getDocument(0).getOccurrence(0));
    	docInst.addResponse(resp);
    }
    private void addDateCodeResponse(Record r){
    	DateEntry de = (DateEntry)r.getDataSet().getDocument(0).getEntry(2);
    	BasicResponse resp = de.generateInstance(r.getDataSet().getDocument(0).getSection(0).getOccurrence(0));
    	IDateValue dv = de.generateValue();
    	dv.setStandardCode(codes.get(2));
    	resp.setValue(dv);
    	DocumentInstance docInst = r.getDocumentInstance(r.getDataSet().getDocument(0).getOccurrence(0));
    	docInst.addResponse(resp);
    }
    private void addDateNullResponse1(Record r){
    	DateEntry de = (DateEntry)r.getDataSet().getDocument(0).getEntry(2);
    	BasicResponse resp = de.generateInstance(r.getDataSet().getDocument(0).getSection(0).getOccurrence(0));
    	DocumentInstance docInst = r.getDocumentInstance(r.getDataSet().getDocument(0).getOccurrence(0));
    	docInst.addResponse(resp);
    }
    private void addDateNullResponse2(Record r){
    	DateEntry de = (DateEntry)r.getDataSet().getDocument(0).getEntry(2);
    	BasicResponse resp = de.generateInstance(r.getDataSet().getDocument(0).getSection(0).getOccurrence(0));
    	IDateValue dv = de.generateValue();
    	resp.setValue(dv);
    	DocumentInstance docInst = r.getDocumentInstance(r.getDataSet().getDocument(0).getOccurrence(0));
    	docInst.addResponse(resp);
    }
    private void addOptionResponse(Record r, int index){
    	OptionEntry oe = (OptionEntry)r.getDataSet().getDocument(0).getEntry(3);
    	BasicResponse resp = oe.generateInstance(r.getDataSet().getDocument(0).getSection(0).getOccurrence(0));
    	IOptionValue ov = oe.generateValue();
    	ov.setValue(oe.getOption(index));
    	resp.setValue(ov);
    	DocumentInstance docInst = r.getDocumentInstance(r.getDataSet().getDocument(0).getOccurrence(0));
    	docInst.addResponse(resp);
    }
    private void addOptionCodeResponse(Record r){
    	OptionEntry oe = (OptionEntry)r.getDataSet().getDocument(0).getEntry(3);
    	BasicResponse resp = oe.generateInstance(r.getDataSet().getDocument(0).getSection(0).getOccurrence(0));
    	IOptionValue ov = oe.generateValue();
    	ov.setStandardCode(codes.get(3));
    	resp.setValue(ov);
    	DocumentInstance docInst = r.getDocumentInstance(r.getDataSet().getDocument(0).getOccurrence(0));
    	docInst.addResponse(resp);
    }
    private void addOptionNullResponse1(Record r){
    	OptionEntry oe = (OptionEntry)r.getDataSet().getDocument(0).getEntry(3);
    	BasicResponse resp = oe.generateInstance(r.getDataSet().getDocument(0).getSection(0).getOccurrence(0));
    	DocumentInstance docInst = r.getDocumentInstance(r.getDataSet().getDocument(0).getOccurrence(0));
    	docInst.addResponse(resp);
    }
    private void addOptionNullResponse2(Record r){
    	OptionEntry oe = (OptionEntry)r.getDataSet().getDocument(0).getEntry(3);
    	BasicResponse resp = oe.generateInstance(r.getDataSet().getDocument(0).getSection(0).getOccurrence(0));
    	IOptionValue ov = oe.generateValue();
    	resp.setValue(ov);
    	DocumentInstance docInst = r.getDocumentInstance(r.getDataSet().getDocument(0).getOccurrence(0));
    	docInst.addResponse(resp);
    }
    
    private IQuery buildQuery(DataSet ds, String operator){
    	QueryFactory factory = new HibernateQueryFactory();
    	IQuery q = factory.createQuery();
    	q.setDataSet(ds);
    	for ( int i=0, c=ds.numGroups(); i<c; i++ ){
    		q.addGroup(ds.getGroup(i));
    	}
    	q.setOperator(operator);
    	return q;
    }
    
    private void addNumericStatement(IQuery q, QueryOperation operation, Double value){
    	QueryStatementValue queryStatementValue = new QueryStatementValue();
    	queryStatementValue.setDoubleValue(value);
    	NumericEntry ne = (NumericEntry)q.getDataSet().getDocument(0).getEntry(0);
    	INumericStatement s = (INumericStatement) ne.createStatement(queryStatementValue);
    	s.setOperator(operation);
    	s.setEntry(ne);
    	q.addStatement(s);
    }
    private void addIntegerStatement(IQuery q, QueryOperation operation, Integer value){
    	QueryStatementValue queryStatementValue = new QueryStatementValue();
    	queryStatementValue.setIntegerValue(value);
    	IntegerEntry ie = (IntegerEntry)q.getDataSet().getDocument(0).getEntry(1);
    	IIntegerStatement s = (IIntegerStatement) ie.createStatement(queryStatementValue);
    	s.setOperator(operation);
    	s.setEntry(ie);
    	q.addStatement(s);
    }
    private void addDateStatement(IQuery q, QueryOperation operation, Date value){
    	QueryStatementValue queryStatementValue = new QueryStatementValue();
    	queryStatementValue.setDateValue(value);
    	DateEntry de = (DateEntry)q.getDataSet().getDocument(0).getEntry(2);
    	IDateStatement s = (IDateStatement) de.createStatement(queryStatementValue);
    	s.setOperator(operation);
    	s.setEntry(de);
    	q.addStatement(s);
    }
    private void addOptionStatement(IQuery q, QueryOperation operation, Integer index){
    	OptionEntry oe = (OptionEntry)q.getDataSet().getDocument(0).getEntry(3);
    	Option o = null;
    	if ( null != index ){
    		o = oe.getOption(index);
    	}
    	QueryStatementValue queryStatementValue = new QueryStatementValue();
    	queryStatementValue.setOptionValue(o);
    	IOptionStatement s = (IOptionStatement) oe.createStatement(queryStatementValue);
    	s.setOperator(operation);
    	s.setEntry(oe);
    	q.addStatement(s);
    }
    private boolean idsInclude(String[] ids, String id){
    	for ( int i=0, c=ids.length; i<c; i++ ){
    		if ( id.equals(ids[i])){
    			return true;
    		}
    	}
    	return false;
    }

}
