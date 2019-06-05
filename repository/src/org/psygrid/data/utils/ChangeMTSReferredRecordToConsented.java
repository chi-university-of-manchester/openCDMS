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

import java.util.List;

import org.psygrid.data.model.dto.DataSetDTO;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Status;
import org.psygrid.data.repository.dao.RepositoryDAO;
import org.psygrid.data.repository.dao.SpecialJdbcDao;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Rob Harper
 *
 */
public class ChangeMTSReferredRecordToConsented {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

        try{
            ApplicationContext ctx = null;
            String[] paths = {"applicationCtx.xml"};
            ctx = new ClassPathXmlApplicationContext(paths);

            SpecialJdbcDao dao = (SpecialJdbcDao)ctx.getBean("jdbcSpecialDaoService");
            RepositoryDAO rdao = (RepositoryDAO)ctx.getBean("repositoryDAOService");
            
            DataSetDTO[] datasets = rdao.getDataSets();
            DataSet ds = null;
            for ( DataSetDTO dtods: datasets ){
            	if ( dtods.getProjectCode().equals("MTS")){
            		ds = rdao.getDataSet(dtods.getId()).toHibernate();
            		break;
            	}
            }
            
            if ( null == ds || !ds.getProjectCode().equals("MTS") ){
            	throw new RuntimeException("Couldn't find MTS dataset");
            }
            
            Long statusId = null;
            for ( int i=0, c=ds.numStatus(); i<c; i++ ){
            	Status s = ds.getStatus(i);
            	if ( s.getShortName().equals("Consented")){
            		statusId = s.getId();
            		break;
            	}
            }
            
            if ( null == statusId ){
            	throw new RuntimeException("Couldn't find the Consented status");
            }
            
            List<Long> records = dao.getRecordsInStatusReferredByProject("MTS");
            for ( Long id: records ){
            	System.out.println("Changing status of record "+id);
            	rdao.changeStatus(id, statusId, "CN=Matisse CPM, OU=users, O=psygrid, C=uk");
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
