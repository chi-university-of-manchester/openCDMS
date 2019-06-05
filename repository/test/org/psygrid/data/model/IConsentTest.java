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

import org.psygrid.data.model.hibernate.Consent;
import org.psygrid.data.model.hibernate.PrimaryConsentForm;
import org.testng.annotations.Test;
import org.testng.AssertJUnit;

public class IConsentTest extends ModelTest {

    @Test()
	public void testGetBasicCopy(){
            //TODO this test isn't really sufficient, but the interfaces
            //don't expose enough of the methods to do a full test
            PrimaryConsentForm pcf1 = factory.createPrimaryConsentForm();
            pcf1.setQuestion("PCF1");
            
            Consent c = pcf1.generateConsent();
            c.setConsentGiven(true);
            
            Consent bc = c.getBasicCopy();
            
            AssertJUnit.assertEquals("Wrong consent given value",c.isConsentGiven(), bc.isConsentGiven());
    }
}
