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
import org.psygrid.data.model.hibernate.RetrieveDepth;
import org.psygrid.data.repository.dao.JdbcDAO;
import org.psygrid.data.repository.dao.RepositoryDAO;
import org.psygrid.data.repository.dao.SpecialDAO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Rob Harper
 *
 */
public class FixEastAngliaIdentifiers {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			String project = "OLK";
            String oldGroup = "002003";
            String newGroup = "002002";
            String user = "CN=Lindsay Weetman, OU=users, O=psygrid, C=uk";
			
            ApplicationContext ctx = null;
            String[] paths = {"applicationCtx.xml"};
            ctx = new ClassPathXmlApplicationContext(paths);
            RepositoryDAO dao = (RepositoryDAO)ctx.getBean("repositoryDAOService");
            JdbcDAO jdbcDao = (JdbcDAO)ctx.getBean("jdbcDaoService");

            SpecialDAO sDao = (SpecialDAO)ctx.getBean("specialDAOService");
			//find number of records in the "old" group
            int count = sDao.getNumberOfRecordsInGroup(project, oldGroup);
            System.out.println(count+" Records found for project="+project+", group="+oldGroup);
			
			//generate the relevant identifiers for the "new" group
            DataSetDTO ds = dao.getSummaryForProjectCode(project, RetrieveDepth.DS_SUMMARY);
            Integer maxSuffix = jdbcDao.reserveIdentifierSpace(ds.getId(), newGroup, count);
            IdentifierDTO[] ids = dao.generateIdentifiers(project, newGroup, count, maxSuffix, user);			
            
			//update the records to give them the new identifiers
			Map<String, String> idMap = sDao.updateIdentifiersforProjectAndGroup(project, oldGroup, ids);
			
			for ( Entry<String, String> e: idMap.entrySet()){
				System.out.println(e.getKey()+" is now "+e.getValue());
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
