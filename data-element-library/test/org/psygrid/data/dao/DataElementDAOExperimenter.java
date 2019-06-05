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

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.psygrid.data.model.dto.DataSetDTO;
import org.psygrid.data.model.dto.ElementDTO;

public class DataElementDAOExperimenter {
	
	protected ApplicationContext ctx = null;
	
	private DataElementDAO dao = null;
	
	public DataElementDAOExperimenter(){
        String[] paths = {"applicationContext.xml"};
        ctx = new ClassPathXmlApplicationContext(paths);
        dao = (DataElementDAO)ctx.getBean("myDataElementDAO");
        int debugpoint =1;
	}
	
	public void doExperiment(){
		
		/*
		final String dataSetLSID = "7R7C9Z7S6O";
		Element el = dao.getElement(dataSetLSID);
		
		if(el instanceof DataSet){
			DataSet ds = (DataSet)el;
			
			//remove the reference to DataSet - and see if affect's the link 
			//between the element and its reference to a dataSet.
			
			ds.getDocuments()[0].setMyDataSet(null);

			
			el = dao.getElement(dataSetLSID);	
		
			
			int debugPoint = 1;
			
		}
		*/
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		DataElementDAOExperimenter exp = new DataElementDAOExperimenter();
		exp.doExperiment();
	}

}
