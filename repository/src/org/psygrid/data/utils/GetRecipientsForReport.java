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

import java.util.Date;

import org.psygrid.data.reporting.ManagementReport;
import org.psygrid.data.reporting.ReportingDAO;
import org.psygrid.data.repository.dao.RepositoryDAO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Rob Harper
 *
 */
public class GetRecipientsForReport {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try{
	        ApplicationContext ctx = null;
	        String[] paths = {"applicationCtx.xml"};
	        ctx = new ClassPathXmlApplicationContext(paths);
	
	        long reportId = Long.parseLong(args[0]);
	        
	        ReportingDAO dao = (ReportingDAO)ctx.getBean("reportingDAO");
	        ManagementReport report = dao.getRecipientsForReport("CN=John Ainsworth, OU=Informatics, O=PsyGrid, C=UK", new Date(), reportId);
	    	System.out.println("Report: "+report.getTitle());
	    	for ( String email: report.getRecipients() ){
	    		System.out.println("  "+email);
	    	}
	
	    }
	    catch(Exception ex){
	        ex.printStackTrace();
	        //make sure that the command exits with return code
	        //other than zero
	        throw new RuntimeException();
	    }
	}

}
