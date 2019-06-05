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

package org.psygrid.data.model.hibernate.visitors;

import org.psygrid.data.model.hibernate.DateEntry;
import org.psygrid.data.model.hibernate.DateValue;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

public class GetValueVisitorTest {

    @Test()
	public void testVisit_Date(){
        DateEntry de = new DateEntry("DE");
        
        DateValue dv = (DateValue)de.generateValue();
        dv.setMonth(new Integer(7));
        dv.setYear(new Integer(1977));
        
        GetValueVisitor getVisitor = new GetValueVisitor();
        dv.accept(getVisitor);
        AssertJUnit.assertEquals("Text value is incorrect","08/1977",getVisitor.getValue());
    }
    
}
