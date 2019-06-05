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

package org.psygrid.transformers;

import org.apache.axis.AxisFault;

import junit.framework.TestCase;

/**
 * Use this unit test if the postcode database has been populated
 * with dummy data i.e. with LoadTestDatabase.
 * RemoteExce
 * @author Rob Harper
 *
 */
public class PostCodeTransformerTest extends TestCase {

    public void testGetSOA(){
        try{
            PostCodeTransformerServiceLocator locator = new PostCodeTransformerServiceLocator();
            PostCodeTransformer transformer = locator.getpostcodetransformer();
            
            String lowerSoa1 = transformer.getSOA("AA1 1AD");
            assertEquals("Lower SOA for postcode AA1 1AD is not correct","EO1000002",lowerSoa1);
            
            String lowerSoa2 = transformer.getSOA("AA1 1AA");
            assertEquals("Lower SOA for postcode AA1 1AA is not correct","EO1000001",lowerSoa2);
            
            String lowerSoa3 = transformer.getSOA("AA1 1AF");
            assertEquals("Lower SOA for postcode AA1 1AF is not correct","EO1000002",lowerSoa3);
            
            String lowerSoa4 = transformer.getSOA("AA1 1AH");
            assertEquals("Lower SOA for postcode AA1 1AH is not correct","EO1000003",lowerSoa4);
            
            try{
                transformer.getSOA("ZY999ZZ");
                fail("Exception should have been thrown when trying to get SOA for postcode 'ZY999ZZ'");
            }
            catch(AxisFault ex){
                //do nothing
            }
                
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
    
}
