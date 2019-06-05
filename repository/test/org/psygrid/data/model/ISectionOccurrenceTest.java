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

package org.psygrid.data.model;

import org.psygrid.data.model.hibernate.ModelException;
import org.psygrid.data.model.hibernate.SecOccInstance;
import org.psygrid.data.model.hibernate.SectionOccurrence;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.AssertJUnit;

public class ISectionOccurrenceTest extends ModelTest {

    @Test()
	public void testGenerateInstance(){
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            so.setMultipleAllowed(true);
            SecOccInstance soi = so.generateInstance();
            AssertJUnit.assertEquals("Generated sec occ inst does not correctly reference the sec occ",so,soi.getSectionOccurrence());

            so.setMultipleAllowed(false);
            try{
                so.generateInstance();
                Assert.fail("Exception should have been thrown when trying to call generateInstance for a sec occ that doesn't allow runtime instances");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
}
