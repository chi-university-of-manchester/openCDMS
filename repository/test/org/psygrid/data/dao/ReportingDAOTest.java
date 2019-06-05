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

import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.reporting.ReportingDAO;
import org.psygrid.data.reporting.old.Report;
import org.psygrid.data.repository.dao.DAOException;
import org.psygrid.data.repository.dao.RepositoryDAO;

public class ReportingDAOTest extends DAOTest {

    private RepositoryDAO dao = null;

    private ReportingDAO reportingDAO = null;

	private Factory factory = null;
    
    protected void setUp() throws Exception {
        super.setUp();
		dao = (RepositoryDAO)ctx.getBean("repositoryDAO");
		reportingDAO = (ReportingDAO)ctx.getBean("reportingDAO");
        factory = (Factory) ctx.getBean("factory");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        dao = null;
        factory = null;
    }
    
    public void testReportByStatus(){
        try{
            String name = "testReportByStatus - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            Group grp1 = factory.createGroup("FOO");
            ds.addGroup(grp1);
            
            String s1name = "Status 1";
            int s1code = 1;
            Status s1 = factory.createStatus(s1name, s1code);
            String s2name = "Status 2";
            int s2code = 2;
            Status s2 = factory.createStatus(s2name, s2code);
            String s3name = "Status 3";
            int s3code = 3;
            Status s3 = factory.createStatus(s3name, s3code);
            String s4name = "Status 4";
            int s4code = 4;
            Status s4 = factory.createStatus(s4name, s4code);
            s1.addStatusTransition(s2);
            s1.addStatusTransition(s3);
            s1.addStatusTransition(s4);
            ds.addStatus(s1);
            ds.addStatus(s2);
            ds.addStatus(s3);
            ds.addStatus(s4);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            s1 = ds.getStatus(0);
            s2 = ds.getStatus(1);
            s3 = ds.getStatus(2);
            s4 = ds.getStatus(3);
            
            Report report = reportingDAO.reportByStatus(dsId);
            
            assertNotNull("Report object is null", report);
            assertEquals("Row 1 does not have the correct heading", s1name, report.getRows()[0].getHeading());
            assertEquals("Row 1 does not have the correct number", 0.0, report.getRows()[0].getValues()[0]);
            assertEquals("Row 2 does not have the correct heading", s2name, report.getRows()[1].getHeading());
            assertEquals("Row 2 does not have the correct number", 0.0, report.getRows()[1].getValues()[0]);
            assertEquals("Row 3 does not have the correct heading", s3name, report.getRows()[2].getHeading());
            assertEquals("Row 3 does not have the correct number", 0.0, report.getRows()[2].getValues()[0]);
            assertEquals("Row 4 does not have the correct heading", s4name, report.getRows()[3].getHeading());
            assertEquals("Row 4 does not have the correct number", 0.0, report.getRows()[3].getValues()[0]);

            //generate identifiers
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 6, "FOO");
            
            //record 1 leave with default status s1
            Record r1 = ds.generateInstance();
            r1.setIdentifier(ids[0]);
            dao.saveRecord(r1.toDTO(), true, null, "NoUser");
            
            //record 2 leave with default status s1
            Record r2 = ds.generateInstance();
            r2.setIdentifier(ids[1]);
            dao.saveRecord(r2.toDTO(), true, null, "NoUser");
            
            //record 3 leave with default status s1
            Record r3 = ds.generateInstance();
            r3.setIdentifier(ids[2]);
            dao.saveRecord(r3.toDTO(), true, null, "NoUser");
            
            //record 4 change status to s2
            Record r4 = ds.generateInstance();
            r4.setIdentifier(ids[3]);
            Long r4Id = dao.saveRecord(r4.toDTO(), true, null, "NoUser");
            dao.changeStatus(r4Id, s2.getId(), "NoUser");
            
            //record 5 change status to s2
            Record r5 = ds.generateInstance();
            r5.setIdentifier(ids[4]);
            Long r5Id = dao.saveRecord(r5.toDTO(), true, null, "NoUser");
            dao.changeStatus(r5Id, s2.getId(), "NoUser");
            
            //record 6 change status to s3
            Record r6 = ds.generateInstance();
            r6.setIdentifier(ids[5]);
            Long r6Id = dao.saveRecord(r6.toDTO(), true, null, "NoUser");
            dao.changeStatus(r6Id, s3.getId(), "NoUser");
            
            report = reportingDAO.reportByStatus(dsId);
            
            assertNotNull("Report object is null", report);
            assertEquals("Row 1 does not have the correct heading", s1name, report.getRows()[0].getHeading());
            assertEquals("Row 1 does not have the correct number", 3.0, report.getRows()[0].getValues()[0]);
            assertEquals("Row 2 does not have the correct heading", s2name, report.getRows()[1].getHeading());
            assertEquals("Row 2 does not have the correct number", 2.0, report.getRows()[1].getValues()[0]);
            assertEquals("Row 3 does not have the correct heading", s3name, report.getRows()[2].getHeading());
            assertEquals("Row 3 does not have the correct number", 1.0, report.getRows()[2].getValues()[0]);
            assertEquals("Row 4 does not have the correct heading", s4name, report.getRows()[3].getHeading());
            assertEquals("Row 4 does not have the correct number", 0.0, report.getRows()[3].getValues()[0]);
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
    
    public void testReportByStatus_Invalid(){
        try{
            try{
            	reportingDAO.reportByStatus(-1L);
                fail("Exception should have been thrown when trying to generate report using an invalid id");
            }
            catch(DAOException ex){
                //do nothing
            }
            
            String name = "testReportByStatus_Invalid - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            ds.setProjectCode(guid.toString());
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            Group grp1 = factory.createGroup("FOO");
            ds.addGroup(grp1);

            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 1, "FOO");
            
            Record r = ds.generateInstance();
            r.setIdentifier(ids[0]);
            dao.saveRecord(r.toDTO(), true, null, "NoUser");
            
            try{
            	reportingDAO.reportByStatus(dsId);
                fail("Exception should have been thrown when trying to generate report for DataSet with no statuses");
            }
            catch(DAOException ex){
                //do nothing
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
    
    public void testNumbersByStatus(){
        try{
            String name = "testNumbersByStatus - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            Group grp1 = factory.createGroup("FOO");
            ds.addGroup(grp1);
            
            String s1name = "Status 1";
            int s1code = 1;
            Status s1 = factory.createStatus(s1name, s1code);
            String s2name = "Status 2";
            int s2code = 2;
            Status s2 = factory.createStatus(s2name, s2code);
            String s3name = "Status 3";
            int s3code = 3;
            Status s3 = factory.createStatus(s3name, s3code);
            String s4name = "Status 4";
            int s4code = 4;
            Status s4 = factory.createStatus(s4name, s4code);
            s1.addStatusTransition(s2);
            s1.addStatusTransition(s3);
            s1.addStatusTransition(s4);
            ds.addStatus(s1);
            ds.addStatus(s2);
            ds.addStatus(s3);
            ds.addStatus(s4);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            s1 = ds.getStatus(0);
            s2 = ds.getStatus(1);
            s3 = ds.getStatus(2);
            s4 = ds.getStatus(3);
            
            {
                org.psygrid.data.reporting.Report report = reportingDAO.numbersByStatus(projectCode);
                assertNotNull("Report object is null", report);
                org.psygrid.data.reporting.Chart chart = report.getCharts()[0];
                assertEquals("Chart has the wrong number of rows",0,chart.getRows().length);
            }
            
            //generate identifiers
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 6, "FOO");
            
            //record 1 leave with default status s1
            Record r1 = ds.generateInstance();
            r1.setIdentifier(ids[0]);
            dao.saveRecord(r1.toDTO(), true, null, "NoUser");
            
            //record 2 leave with default status s1
            Record r2 = ds.generateInstance();
            r2.setIdentifier(ids[1]);
            dao.saveRecord(r2.toDTO(), true, null, "NoUser");
            
            //record 3 leave with default status s1
            Record r3 = ds.generateInstance();
            r3.setIdentifier(ids[2]);
            dao.saveRecord(r3.toDTO(), true, null, "NoUser");
            
            //record 4 change status to s2
            Record r4 = ds.generateInstance();
            r4.setIdentifier(ids[3]);
            Long r4Id = dao.saveRecord(r4.toDTO(), true, null, "NoUser");
            dao.changeStatus(r4Id, s2.getId(), "NoUser");
            
            //record 5 change status to s2
            Record r5 = ds.generateInstance();
            r5.setIdentifier(ids[4]);
            Long r5Id = dao.saveRecord(r5.toDTO(), true, null, "NoUser");
            dao.changeStatus(r5Id, s2.getId(), "NoUser");
            
            //record 6 change status to s3
            Record r6 = ds.generateInstance();
            r6.setIdentifier(ids[5]);
            Long r6Id = dao.saveRecord(r6.toDTO(), true, null, "NoUser");
            dao.changeStatus(r6Id, s3.getId(), "NoUser");
            
            {
                org.psygrid.data.reporting.Report report = reportingDAO.numbersByStatus(projectCode);
                assertNotNull("Report object is null", report);
                org.psygrid.data.reporting.Chart chart = report.getCharts()[0];
                assertEquals("Chart has the wrong number of rows",3,chart.getRows().length);
                org.psygrid.data.reporting.ChartRow row0 = chart.getRows()[0];
                assertEquals("Row 0 has the wrong label", s1name, row0.getLabel());
                assertEquals("Row 0 has the wrong number of points", 1, row0.getSeries().length);
                assertEquals("Row 0 point has the wrong value", "3", row0.getSeries()[0].getPoints()[0].getValue());
                org.psygrid.data.reporting.ChartRow row1 = chart.getRows()[1];
                assertEquals("Row 1 has the wrong label", s2name, row1.getLabel());
                assertEquals("Row 1 has the wrong number of points", 1, row1.getSeries().length);
                assertEquals("Row 1 point has the wrong value", "2", row1.getSeries()[0].getPoints()[0].getValue());
                org.psygrid.data.reporting.ChartRow row2 = chart.getRows()[2];
                assertEquals("Row 2 has the wrong label", s3name, row2.getLabel());
                assertEquals("Row 2 has the wrong number of points", 1, row2.getSeries().length);
                assertEquals("Row 2 point has the wrong value", "1", row2.getSeries()[0].getPoints()[0].getValue());
            }
                        
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
    
    public void testNumbersByStatusAndGroups(){
        try{
            String name = "testNumbersByStatusAndGroups - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            String grpFoo = "FOO";
            Group grp1 = factory.createGroup(grpFoo);
            ds.addGroup(grp1);
            String grpBar = "BAR";
            Group grp2 = factory.createGroup(grpBar);
            ds.addGroup(grp2);
            String grpMoo = "MOO";
            Group grp3 = factory.createGroup(grpMoo);
            ds.addGroup(grp3);
            
            String s1name = "Status 1";
            int s1code = 1;
            Status s1 = factory.createStatus(s1name, s1code);
            String s2name = "Status 2";
            int s2code = 2;
            Status s2 = factory.createStatus(s2name, s2code);
            String s3name = "Status 3";
            int s3code = 3;
            Status s3 = factory.createStatus(s3name, s3code);
            String s4name = "Status 4";
            int s4code = 4;
            Status s4 = factory.createStatus(s4name, s4code);
            s1.addStatusTransition(s2);
            s1.addStatusTransition(s3);
            s1.addStatusTransition(s4);
            ds.addStatus(s1);
            ds.addStatus(s2);
            ds.addStatus(s3);
            ds.addStatus(s4);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            s1 = ds.getStatus(0);
            s2 = ds.getStatus(1);
            s3 = ds.getStatus(2);
            s4 = ds.getStatus(3);
            
            {
                org.psygrid.data.reporting.Report report = reportingDAO.numbersByStatus(projectCode);
                assertNotNull("Report object is null", report);
                org.psygrid.data.reporting.Chart chart = report.getCharts()[0];
                assertEquals("Chart has the wrong number of rows",0,chart.getRows().length);
            }
            
            //generate identifiers
            Identifier[] idsFoo = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 6, grpFoo);
            Identifier[] idsBar = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 6, grpBar);
            Identifier[] idsMoo = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 6, grpMoo);
            
            /*
             * Records for group "Foo"
             */
            //record 1 leave with default status s1
            Record r1 = ds.generateInstance();
            r1.setIdentifier(idsFoo[0]);
            dao.saveRecord(r1.toDTO(), true, null, "NoUser");
            
            //record 2 leave with default status s1
            Record r2 = ds.generateInstance();
            r2.setIdentifier(idsFoo[1]);
            dao.saveRecord(r2.toDTO(), true, null, "NoUser");
            
            //record 3 leave with default status s1
            Record r3 = ds.generateInstance();
            r3.setIdentifier(idsFoo[2]);
            dao.saveRecord(r3.toDTO(), true, null, "NoUser");
            
            //record 4 change status to s2
            Record r4 = ds.generateInstance();
            r4.setIdentifier(idsFoo[3]);
            Long r4Id = dao.saveRecord(r4.toDTO(), true, null, "NoUser");
            dao.changeStatus(r4Id, s2.getId(), "NoUser");
            
            //record 5 change status to s3
            Record r5 = ds.generateInstance();
            r5.setIdentifier(idsFoo[4]);
            Long r5Id = dao.saveRecord(r5.toDTO(), true, null, "NoUser");
            dao.changeStatus(r5Id, s3.getId(), "NoUser");
            
            //record 6 change status to s4
            Record r6 = ds.generateInstance();
            r6.setIdentifier(idsFoo[5]);
            Long r6Id = dao.saveRecord(r6.toDTO(), true, null, "NoUser");
            dao.changeStatus(r6Id, s4.getId(), "NoUser");
            
            /*
             * Records for group "Bar"
             */
            //record 7 leave with default status s1
            Record r7 = ds.generateInstance();
            r7.setIdentifier(idsBar[0]);
            dao.saveRecord(r7.toDTO(), true, null, "NoUser");
            
            //record 8 leave with default status s1
            Record r8 = ds.generateInstance();
            r8.setIdentifier(idsBar[1]);
            dao.saveRecord(r8.toDTO(), true, null, "NoUser");
            
            //record 9 change status to s3
            Record r9 = ds.generateInstance();
            r9.setIdentifier(idsBar[2]);
            Long r9Id = dao.saveRecord(r9.toDTO(), true, null, "NoUser");
            dao.changeStatus(r9Id, s3.getId(), "NoUser");
            
            //record 10 change status to s3
            Record r10 = ds.generateInstance();
            r10.setIdentifier(idsBar[3]);
            Long r10Id = dao.saveRecord(r10.toDTO(), true, null, "NoUser");
            dao.changeStatus(r10Id, s3.getId(), "NoUser");
            
            //record 11 change status to s4
            Record r11 = ds.generateInstance();
            r11.setIdentifier(idsBar[4]);
            Long r11Id = dao.saveRecord(r11.toDTO(), true, null, "NoUser");
            dao.changeStatus(r11Id, s4.getId(), "NoUser");
            
            //record 12 change status to s4
            Record r12 = ds.generateInstance();
            r12.setIdentifier(idsBar[5]);
            Long r12Id = dao.saveRecord(r12.toDTO(), true, null, "NoUser");
            dao.changeStatus(r12Id, s4.getId(), "NoUser");
            
            /*
             * Records for group "Moo"
             */
            //record 13 leave with default status s1
            Record r13 = ds.generateInstance();
            r13.setIdentifier(idsMoo[0]);
            Long r13Id = dao.saveRecord(r13.toDTO(), true, null, "NoUser");
            dao.changeStatus(r13Id, s2.getId(), "NoUser");
            
            //record 14 leave with default status s1
            Record r14 = ds.generateInstance();
            r14.setIdentifier(idsMoo[1]);
            Long r14Id = dao.saveRecord(r14.toDTO(), true, null, "NoUser");
            dao.changeStatus(r14Id, s2.getId(), "NoUser");
            
            //record 15 change status to s3
            Record r15 = ds.generateInstance();
            r15.setIdentifier(idsMoo[2]);
            Long r15Id = dao.saveRecord(r15.toDTO(), true, null, "NoUser");
            dao.changeStatus(r15Id, s2.getId(), "NoUser");
            
            //record 16 change status to s3
            Record r16 = ds.generateInstance();
            r16.setIdentifier(idsMoo[3]);
            Long r16Id = dao.saveRecord(r16.toDTO(), true, null, "NoUser");
            dao.changeStatus(r16Id, s2.getId(), "NoUser");
            
            //record 17 change status to s4
            Record r17 = ds.generateInstance();
            r17.setIdentifier(idsMoo[4]);
            Long r17Id = dao.saveRecord(r17.toDTO(), true, null, "NoUser");
            dao.changeStatus(r17Id, s4.getId(), "NoUser");
            
            //record 18 change status to s4
            Record r18 = ds.generateInstance();
            r18.setIdentifier(idsMoo[5]);
            Long r18Id = dao.saveRecord(r18.toDTO(), true, null, "NoUser");
            dao.changeStatus(r18Id, s4.getId(), "NoUser");
            
            //Test for group "FOO" only
            {
                org.psygrid.data.reporting.Report report = reportingDAO.numbersByStatusForGroups(projectCode, new String[]{grpFoo});
                assertNotNull("Report object is null", report);
                org.psygrid.data.reporting.Chart chart = report.getCharts()[0];
                assertEquals("Chart has the wrong number of rows",4,chart.getRows().length);
                org.psygrid.data.reporting.ChartRow row0 = chart.getRows()[0];
                assertEquals("Row 0 has the wrong label", s1name, row0.getLabel());
                assertEquals("Row 0 has the wrong number of points", 1, row0.getSeries().length);
                assertEquals("Row 0 point has the wrong value", "3", row0.getSeries()[0].getPoints()[0].getValue());
                org.psygrid.data.reporting.ChartRow row1 = chart.getRows()[1];
                assertEquals("Row 1 has the wrong label", s2name, row1.getLabel());
                assertEquals("Row 1 has the wrong number of points", 1, row1.getSeries().length);
                assertEquals("Row 1 point has the wrong value", "1", row1.getSeries()[0].getPoints()[0].getValue());
                org.psygrid.data.reporting.ChartRow row2 = chart.getRows()[2];
                assertEquals("Row 2 has the wrong label", s3name, row2.getLabel());
                assertEquals("Row 2 has the wrong number of points", 1, row2.getSeries().length);
                assertEquals("Row 2 point has the wrong value", "1", row2.getSeries()[0].getPoints()[0].getValue());
                org.psygrid.data.reporting.ChartRow row3 = chart.getRows()[3];
                assertEquals("Row 2 has the wrong label", s4name, row3.getLabel());
                assertEquals("Row 2 has the wrong number of points", 1, row3.getSeries().length);
                assertEquals("Row 2 point has the wrong value", "1", row3.getSeries()[0].getPoints()[0].getValue());
            }
                        
            //Test for groups "FOO" and "MOO"
            {
                org.psygrid.data.reporting.Report report = reportingDAO.numbersByStatusForGroups(projectCode, new String[]{grpFoo, grpMoo});
                assertNotNull("Report object is null", report);
                org.psygrid.data.reporting.Chart chart = report.getCharts()[0];
                assertEquals("Chart has the wrong number of rows",4,chart.getRows().length);
                org.psygrid.data.reporting.ChartRow row0 = chart.getRows()[0];
                assertEquals("Row 0 has the wrong label", s1name, row0.getLabel());
                assertEquals("Row 0 has the wrong number of points", 1, row0.getSeries().length);
                assertEquals("Row 0 point has the wrong value", "3", row0.getSeries()[0].getPoints()[0].getValue());
                org.psygrid.data.reporting.ChartRow row1 = chart.getRows()[1];
                assertEquals("Row 1 has the wrong label", s2name, row1.getLabel());
                assertEquals("Row 1 has the wrong number of points", 1, row1.getSeries().length);
                assertEquals("Row 1 point has the wrong value", "5", row1.getSeries()[0].getPoints()[0].getValue());
                org.psygrid.data.reporting.ChartRow row2 = chart.getRows()[2];
                assertEquals("Row 2 has the wrong label", s3name, row2.getLabel());
                assertEquals("Row 2 has the wrong number of points", 1, row2.getSeries().length);
                assertEquals("Row 2 point has the wrong value", "1", row2.getSeries()[0].getPoints()[0].getValue());
                org.psygrid.data.reporting.ChartRow row3 = chart.getRows()[3];
                assertEquals("Row 2 has the wrong label", s4name, row3.getLabel());
                assertEquals("Row 2 has the wrong number of points", 1, row3.getSeries().length);
                assertEquals("Row 2 point has the wrong value", "3", row3.getSeries()[0].getPoints()[0].getValue());
            }
                        
            //Test for groups "FOO", "BAR" and "MOO"
            {
                org.psygrid.data.reporting.Report report = reportingDAO.numbersByStatusForGroups(projectCode, new String[]{grpFoo, grpBar, grpMoo});
                assertNotNull("Report object is null", report);
                org.psygrid.data.reporting.Chart chart = report.getCharts()[0];
                assertEquals("Chart has the wrong number of rows",4,chart.getRows().length);
                org.psygrid.data.reporting.ChartRow row0 = chart.getRows()[0];
                assertEquals("Row 0 has the wrong label", s1name, row0.getLabel());
                assertEquals("Row 0 has the wrong number of points", 1, row0.getSeries().length);
                assertEquals("Row 0 point has the wrong value", "5", row0.getSeries()[0].getPoints()[0].getValue());
                org.psygrid.data.reporting.ChartRow row1 = chart.getRows()[1];
                assertEquals("Row 1 has the wrong label", s2name, row1.getLabel());
                assertEquals("Row 1 has the wrong number of points", 1, row1.getSeries().length);
                assertEquals("Row 1 point has the wrong value", "5", row1.getSeries()[0].getPoints()[0].getValue());
                org.psygrid.data.reporting.ChartRow row2 = chart.getRows()[2];
                assertEquals("Row 2 has the wrong label", s3name, row2.getLabel());
                assertEquals("Row 2 has the wrong number of points", 1, row2.getSeries().length);
                assertEquals("Row 2 point has the wrong value", "3", row2.getSeries()[0].getPoints()[0].getValue());
                org.psygrid.data.reporting.ChartRow row3 = chart.getRows()[3];
                assertEquals("Row 2 has the wrong label", s4name, row3.getLabel());
                assertEquals("Row 2 has the wrong number of points", 1, row3.getSeries().length);
                assertEquals("Row 2 point has the wrong value", "5", row3.getSeries()[0].getPoints()[0].getValue());
            }
                        
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
    
}
