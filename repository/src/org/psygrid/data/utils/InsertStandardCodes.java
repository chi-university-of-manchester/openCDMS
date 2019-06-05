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

import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.model.hibernate.StandardCode;
import org.psygrid.data.repository.dao.RepositoryDAO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class InsertStandardCodes {

    public static void main(String[] args){
        
        try{
            ApplicationContext ctx = null;
            String[] paths = {"applicationCtx.xml"};
            ctx = new ClassPathXmlApplicationContext(paths);
    
            Factory factory = (Factory)ctx.getBean("factory");
            
            StandardCode sc1 = factory.createStandardCode("Data not known", 960);
            StandardCode sc2 = factory.createStandardCode("Not applicable", 970);
            StandardCode sc3 = factory.createStandardCode("Refused to answer", 980);
            StandardCode sc4 = factory.createStandardCode("Data unable to be captured", 999);
            sc4.setUsedForDerivedEntry(true);
            
            RepositoryDAO dao = (RepositoryDAO)ctx.getBean("repositoryDAOService");
            dao.saveStandardCode(sc1.toDTO());
            dao.saveStandardCode(sc2.toDTO());
            dao.saveStandardCode(sc3.toDTO());
            dao.saveStandardCode(sc4.toDTO());
        }
        catch(Exception ex){
            ex.printStackTrace();
            //make sure that the command exists with return code
            //other than zero
            throw new RuntimeException();
        }
    }
}
