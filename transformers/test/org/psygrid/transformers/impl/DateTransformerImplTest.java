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

package org.psygrid.transformers.impl;

import org.psygrid.transformers.TransformerException;

import junit.framework.TestCase;

public class DateTransformerImplTest extends TestCase {

    public void testGetMonthAndYear(){
        try{
            String result = DateTransformerImpl.getMonthAndYear("20/07/1977");
            assertEquals("Date in month/year format is not correct","07/1977",result);
            
            try{
                result = DateTransformerImpl.getMonthAndYear("32/12/2005");
                fail("Exception should have been thrown when trying to transform invalid date");
            }
            catch(TransformerException ex){
                //do nothing
            }
            
            result = DateTransformerImpl.getMonthAndYear("12/2005");
            assertEquals("Transformed date is not correct", "12/2005", result);
            
            result = DateTransformerImpl.getMonthAndYear("2005");
            assertEquals("Transformed date is not correct", "2005", result);
            
            result = DateTransformerImpl.getMonthAndYear(null);
            assertNull("Transformed date should be null for null input", result);
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }
    
}
