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


package org.psygrid.data.export;

import java.util.ArrayList;
import java.util.List;

import org.psygrid.data.dao.DAOTest;
import org.psygrid.data.export.ExportDAO;
import org.psygrid.data.export.hibernate.ExportDocument;
import org.psygrid.data.export.hibernate.ExportRequest;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.repository.dao.RepositoryDAO;

/**
 * @author Rob Harper
 *
 */
public class ExportDAOTest extends DAOTest {

    private ExportDAO dao = null;
    private Factory factory = null;
    public static final String projectCode = "NED";
    
    protected void setUp() throws Exception {
        super.setUp();
        dao = (ExportDAO) ctx.getBean("exportDAO");
        factory = (Factory) ctx.getBean("factory");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        dao = null;
        factory = null;
    }

    public void testGetProjectForExportRequest(){
    	try{
    		List<String> groups = new ArrayList<String>();
    		groups.add("002001");
    		String project = "FOOBAR";
    		List<ExportDocument> docOccs = new ArrayList<ExportDocument>();
        	ExportDocument docOne = new ExportDocument();
        	docOne.setDocOccId(Long.valueOf(0));
        	docOccs.add(docOne);
    		ExportRequest req = new ExportRequest("NoUser", project, groups, "single", false);
    		req.setDocOccs(docOccs);
    		List<String> statuses = new ArrayList<String>();
    		statuses.add("Incomplete");
    		statuses.add("Complete");
    		statuses.add("Rejected");
    		statuses.add("Approved");
    		req.setDocumentStatuses(statuses);
    		req.setStatus(ExportRequest.STATUS_ERROR);
    		req = dao.updateExportRequest(req.toDTO()).toHibernate();
    		
    		String testProject = dao.getProjectForExportRequest(req.getId());
    		assertEquals("Project is incorrect", project, testProject);
    	}
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
}
