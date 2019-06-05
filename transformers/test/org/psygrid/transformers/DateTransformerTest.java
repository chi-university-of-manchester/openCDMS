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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import junit.framework.TestCase;

public class DateTransformerTest extends TestCase {

    public static final DateFormat fullDateFormatter = new SimpleDateFormat("dd/MM/yyyy");

    public void testGetMonthAndYear(){
        try{
            DateTransformerServiceLocator locator = new DateTransformerServiceLocator();
            DateTransformer transformer = locator.getdatetransformer();
            
            Calendar cal = Calendar.getInstance();
            cal.set(1977,6,20); //20th July 1977 (month is zero-based :-)
            String result = transformer.getMonthAndYear(fullDateFormatter.format(cal.getTime()));
            assertEquals("Date in month/year format is not correct","07/1977",result);
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }
    
}
