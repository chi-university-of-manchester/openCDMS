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

package org.psygrid.transformers.impl.postcode;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;

public class LoadTestDatabase {

    protected ApplicationContext ctx = null;
    
    private PostCodeDAO dao = null;
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        
        LoadTestDatabase loader = new LoadTestDatabase();
        
        loader.populateDatabase();
        
    }

    public LoadTestDatabase(){
        String[] paths = {"applicationCtx.xml"};
        ctx = new ClassPathXmlApplicationContext(paths);
        dao = (PostCodeDAO)ctx.getBean("postcodeDAOService");
    }
    
    public void populateDatabase(){
        
        MiddleSOA msoa1 = new MiddleSOA();
        msoa1.setCode("E02000001");
        
        LowerSOA lsoa1 = new LowerSOA();
        lsoa1.setCode("EO1000001");
        msoa1.getLowerSoas().add(lsoa1);
        lsoa1.setMiddleSoa(msoa1);
        
        LowerSOA lsoa2 = new LowerSOA();
        lsoa2.setCode("EO1000002");
        msoa1.getLowerSoas().add(lsoa2);
        lsoa2.setMiddleSoa(msoa1);
        
        LowerSOA lsoa3 = new LowerSOA();
        lsoa3.setCode("EO1000003");
        msoa1.getLowerSoas().add(lsoa3);
        lsoa3.setMiddleSoa(msoa1);
        
        OutputArea oa1 = new OutputArea();
        oa1.setCode("OOAAFA0001");
        lsoa1.getOutputAreas().add(oa1);
        oa1.setLowerSoa(lsoa1);
        
        OutputArea oa2 = new OutputArea();
        oa2.setCode("OOAAFA0002");
        lsoa1.getOutputAreas().add(oa2);
        oa2.setLowerSoa(lsoa1);
        
        OutputArea oa3 = new OutputArea();
        oa3.setCode("OOAAFA0003");
        lsoa1.getOutputAreas().add(oa3);
        oa3.setLowerSoa(lsoa1);
        
        OutputArea oa4 = new OutputArea();
        oa4.setCode("OOAAFA0004");
        lsoa2.getOutputAreas().add(oa4);
        oa4.setLowerSoa(lsoa2);
        
        OutputArea oa5 = new OutputArea();
        oa5.setCode("OOAAFA0005");
        lsoa2.getOutputAreas().add(oa5);
        oa5.setLowerSoa(lsoa2);
        
        OutputArea oa6 = new OutputArea();
        oa6.setCode("OOAAFA0006");
        lsoa2.getOutputAreas().add(oa6);
        oa6.setLowerSoa(lsoa2);
        
        OutputArea oa7 = new OutputArea();
        oa7.setCode("OOAAFA0007");
        lsoa3.getOutputAreas().add(oa7);
        oa7.setLowerSoa(lsoa3);
        
        OutputArea oa8 = new OutputArea();
        oa8.setCode("OOAAFA0008");
        lsoa3.getOutputAreas().add(oa8);
        oa8.setLowerSoa(lsoa3);
        
        OutputArea oa9 = new OutputArea();
        oa9.setCode("OOAAFA0009");
        lsoa3.getOutputAreas().add(oa9);
        oa9.setLowerSoa(lsoa3);
        
        PostCode pc1 = new PostCode();
        pc1.setValue("AA1 1AA");
        oa1.getPostCodes().add(pc1);
        pc1.setOutputArea(oa1);
        
        PostCode pc2 = new PostCode();
        pc2.setValue("AA1 1AB");
        oa2.getPostCodes().add(pc2);
        pc2.setOutputArea(oa2);
        
        PostCode pc3 = new PostCode();
        pc3.setValue("AA1 1AC");
        oa3.getPostCodes().add(pc3);
        pc3.setOutputArea(oa3);
        
        PostCode pc4 = new PostCode();
        pc4.setValue("AA1 1AD");
        oa4.getPostCodes().add(pc4);
        pc4.setOutputArea(oa4);
        
        PostCode pc5 = new PostCode();
        pc5.setValue("AA1 1AE");
        oa5.getPostCodes().add(pc5);
        pc5.setOutputArea(oa5);
        
        PostCode pc6 = new PostCode();
        pc6.setValue("AA1 1AF");
        oa6.getPostCodes().add(pc6);
        pc6.setOutputArea(oa6);
        
        PostCode pc7 = new PostCode();
        pc7.setValue("AA1 1AG");
        oa7.getPostCodes().add(pc7);
        pc7.setOutputArea(oa7);
        
        PostCode pc8 = new PostCode();
        pc8.setValue("AA1 1AH");
        oa8.getPostCodes().add(pc8);
        pc8.setOutputArea(oa8);
        
        PostCode pc9 = new PostCode();
        pc9.setValue("AA1 1AI");
        oa9.getPostCodes().add(pc9);
        pc9.setOutputArea(oa9);
        
        try{
            dao.saveMiddleSoa(msoa1);
        }
        catch(DataIntegrityViolationException ex){
            //Assume that this error means that the database is 
            //already populated, so just write to stdout saying as such
            System.out.println("Database is already populated");
        }

    }
}
