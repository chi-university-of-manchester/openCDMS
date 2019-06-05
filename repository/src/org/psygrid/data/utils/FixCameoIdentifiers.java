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

import java.util.Map;
import java.util.Map.Entry;

import org.psygrid.data.model.dto.DataSetDTO;
import org.psygrid.data.model.dto.IdentifierDTO;
import org.psygrid.data.model.dto.RecordDTO;
import org.psygrid.data.model.hibernate.RetrieveDepth;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.data.repository.dao.JdbcDAO;
import org.psygrid.data.repository.dao.RepositoryDAO;
import org.psygrid.data.repository.dao.SpecialDAO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Utility method to change the identifier of a record to the newly specified group
 * (Was created because client data was entered using the wrong group)
 * 
 * Current defaulted to Outlook and set to user Susannah Redhead
 * 
 * @author Pauline Whelan
 */
public class FixCameoIdentifiers {

	/**
	 * @param args
	 * first arg is the full old identifier
	 * second arg is the new group
	 */
	public static void main(String[] args) {
		try{
			String oldIdentifier = args[0];
			String newGroup = args[1];
			
			String project = "OLK";
			String user = "CN=Susannah Redhead, OU=users, O=psygrid, C=uk";
			
            ApplicationContext ctx = null;
            String[] paths = {"applicationCtx.xml"};
            ctx = new ClassPathXmlApplicationContext(paths);
            RepositoryDAO dao = (RepositoryDAO)ctx.getBean("repositoryDAOService");
            JdbcDAO jdbcDao = (JdbcDAO)ctx.getBean("jdbcDaoService");
            SpecialDAO sDao = (SpecialDAO)ctx.getBean("specialDAOService");
            
            RecordDTO r = dao.getRecord(oldIdentifier, RetrieveDepth.RS_SUMMARY);
            IdentifierDTO oldIdent = r.getIdentifier();
//            System.out.println("Retrieved old identifier " + oldIdent);
            
			//generate the relevant identifiers for the "new" group
            DataSetDTO ds = dao.getSummaryForProjectCode(project, RetrieveDepth.DS_SUMMARY);
            Integer maxSuffix = jdbcDao.reserveIdentifierSpace(ds.getId(), newGroup, 1);
            IdentifierDTO[] ids = dao.generateIdentifiers(project, newGroup, 1, maxSuffix, user);			
            
			//update the record to give it the new identifiers
			Map<String, String> idMap = sDao.updateIdentifier(oldIdentifier, ids[0]);
			
			for ( Entry<String, String> e: idMap.entrySet()){
				System.out.println(e.getKey()+" is now "+e.getValue());
			}
			
    		RecordDTO record = new RecordDTO();
    		record.setDataSetId(ds.getId());
    		record.setIdentifier(oldIdent);
			dao.saveRecord(record, false, null, user);
			dao.deleteRecord(oldIdent.getIdentifier());
			System.out.println("Old record with identifier " + oldIdentifier + " set to deleted.");
		}
        catch(Exception ex){
            ex.printStackTrace();
            //make sure that the command exits with return code
            //other than zero
            throw new RuntimeException();
        }

	}

}
