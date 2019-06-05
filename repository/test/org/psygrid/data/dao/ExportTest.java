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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.psygrid.data.export.CsvExportFormatter;
import org.psygrid.data.export.hibernate.ExportDocument;
import org.psygrid.data.export.hibernate.ExportSecurityActionMap;
import org.psygrid.data.repository.dao.RepositoryDAO;

/**
 * @author Rob Harper
 *
 */
public class ExportTest extends DAOTest {

    private RepositoryDAO dao = null;
    public static final String projectCode = "NED";
    
    protected void setUp() throws Exception {
        super.setUp();
        dao = (RepositoryDAO) ctx.getBean("repositoryDAOService");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        dao = null;
    }

    public void testExportAsXML() {
    	
    	try {
        	List<String> groups = new ArrayList<String>();
        	groups.add("001001");
        	groups.add("001002");
        	groups.add("002001");
        	groups.add("002002");
        	
        	List<ExportDocument> docOccs = new ArrayList<ExportDocument>();
        	ExportDocument docOne = new ExportDocument();
        	docOne.setDocOccId(Long.valueOf(242));
        	docOccs.add(docOne);
        	ExportDocument docTwo = new ExportDocument();
        	docOne.setDocOccId(Long.valueOf(310));
        	docOccs.add(docTwo);
        	ExportDocument docThree = new ExportDocument();
        	docOne.setDocOccId(Long.valueOf(588));
        	docOccs.add(docThree);
        	ExportDocument docFour = new ExportDocument();
        	docOne.setDocOccId(Long.valueOf(673));
        	docOccs.add(docFour);
        	ExportDocument docFive = new ExportDocument();
        	docOne.setDocOccId(Long.valueOf(694));
        	docOccs.add(docFive);
        	
        	OutputStream out = new FileOutputStream("export-test.xml");
        	
        	org.psygrid.data.export.metadata.DataSetMetaData metaData = new org.psygrid.data.export.metadata.DataSetMetaData();
        	
        	org.psygrid.data.export.hibernate.ExportRequest export = new org.psygrid.data.export.hibernate.ExportRequest("NoUser", "OLK", groups, "single", true);
    		export.setDocOccs(docOccs);
    		List<String> statuses = new ArrayList<String>();
    		statuses.add("Incomplete");
    		statuses.add("Complete");
    		statuses.add("Rejected");
    		statuses.add("Approved");
    		export.setDocumentStatuses(statuses);
    		dao.exportToXml(export, "001001", new ArrayList<ExportSecurityActionMap>(), out, metaData);
    		
    		File[] files = {new File("export-test.xml")};
    		CsvExportFormatter.toMultipleCsv(files, metaData, "export-test",true,true,true);
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		fail();
    	}
    }
    
    
    public void testExportAsXMLPROCAS() {
    	
    	try {
        	List<String> groups = new ArrayList<String>();
        	groups.add("GMR");
        	
        	List<ExportDocument> docOccs = new ArrayList<ExportDocument>();
        	
        	ExportDocument docOne = new ExportDocument();
        	docOne.setDocOccId(Long.valueOf(82));
        	docOne.addEntryId(Long.valueOf(26));
        	docOne.addEntryId(Long.valueOf(27));
        	docOccs.add(docOne);
        	
        	ExportDocument docTwo = new ExportDocument();
        	docTwo.setDocOccId(Long.valueOf(244));
        	docOccs.add(docTwo);
        	        	
        	File xmlFile = new File("c:/aaa/export-test.xml");
        	File outFile = new File("c:/aaa/export-test.csv");
        	OutputStream out = new FileOutputStream(xmlFile);
        	
        	org.psygrid.data.export.metadata.DataSetMetaData metaData = new org.psygrid.data.export.metadata.DataSetMetaData();
        	
        	org.psygrid.data.export.hibernate.ExportRequest export = new org.psygrid.data.export.hibernate.ExportRequest("NoUser", "PRC", groups, "single", true);
    		export.setDocOccs(docOccs);

    		List<String> statuses = new ArrayList<String>();
    		statuses.add("Incomplete");
    		statuses.add("Complete");
    		statuses.add("Rejected");
    		statuses.add("Approved");
    		export.setDocumentStatuses(statuses);
    		
    		dao.exportToXml(export, "GMR", new ArrayList<ExportSecurityActionMap>(), out, metaData);
    		
    		File[] files = {xmlFile};
    		CsvExportFormatter.toSingleCSV(files, metaData, outFile,true,true,true);
    		//CsvExportFormatter.toMultipleCsv(files, metaData, "export-test");
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		fail();
    	}
    }

}
