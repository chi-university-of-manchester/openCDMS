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

import org.psygrid.transformers.TransformerException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;

/**
 * Run this unit test of the postcode database has been fully populate 
 * from the All Fields Postcode data file i.e with LoadDatabase.
 * 
 * @author Rob Harper
 *
 */
public class PostCodeDAORealTest extends TestCase {

    protected ApplicationContext ctx = null;
    
    private PostCodeDAO dao = null;
    
    public PostCodeDAORealTest() {
        String[] paths = {"applicationContext.xml"};
        ctx = new ClassPathXmlApplicationContext(paths);
    }

    protected void setUp() throws Exception {
        super.setUp();
        dao = (PostCodeDAO)ctx.getBean("postcodeDAOService");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        dao = null;
    }
    
    public void testGetLowerSoaForPostcode(){
        try{
            String lowerSoa1 = dao.getLowerSoaForPostcode("M13 9PT");
            assertEquals("Lower SOA for postcode 'M13 9PT' is not correct","E01005062",lowerSoa1);
            
            String lowerSoa2 = dao.getLowerSoaForPostcode("M13  9PT");
            assertEquals("Lower SOA for postcode 'M13  9PT' is not correct","E01005062",lowerSoa2);
            
            String lowerSoa3 = dao.getLowerSoaForPostcode("M139PT");
            assertEquals("Lower SOA for postcode 'M139PT' is not correct","E01005062",lowerSoa3);
            
            String lowerSoa4 = dao.getLowerSoaForPostcode("m13 9pt");
            assertEquals("Lower SOA for postcode 'm13 9pt' is not correct","E01005062",lowerSoa4);
            
            try{
                dao.getLowerSoaForPostcode("Q13 9PT");
                fail("Exception should have been thrown due to invalid postcode");
            }
            catch(TransformerException ex){
                //do nothing
            }
            
            String lowerSoa5 = dao.getLowerSoaForPostcode(null);
            assertNull("Null input should produce null output",lowerSoa5);
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
    
}
