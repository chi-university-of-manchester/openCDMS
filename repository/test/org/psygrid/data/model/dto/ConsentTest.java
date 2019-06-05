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

package org.psygrid.data.model.dto;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import java.util.HashMap;
import java.util.Map;

public class ConsentTest {

    @Test()
	public void testToHibernate(){
            ConsentDTO c = new ConsentDTO();
            BinaryObjectDTO cDoc = new BinaryObjectDTO();
            Long cDocId = new Long(32);
            cDoc.setId(cDocId);
            c.setConsentDoc(cDoc);
            Long cfId = new Long(87);
            c.setConsentFormId(cfId);
            boolean cons = true;
            c.setConsentGiven(cons);
            Long id = new Long(56);
            c.setId(id);
            String loc = "Location";
            c.setLocation(loc);
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.Consent hC = c.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate consent is null", hC);
            AssertJUnit.assertNotNull("Hibernate consent has null consent document",hC.getConsentDoc());
            AssertJUnit.assertEquals("Hibernate consent has the consent document with the wrong id",cDocId,hC.getConsentDoc().getId());
            AssertJUnit.assertEquals("Hibernate consent has the consent form with the wrong id",cfId,hC.getConsentFormId());
            AssertJUnit.assertEquals("Hibernate consent has the wrong id",id,hC.getId());
            AssertJUnit.assertEquals("Hibernate consent has the wrong consent given",cons,hC.isConsentGiven());
            AssertJUnit.assertEquals("Hibernate consent has the wrong location",loc,hC.getLocation());
    }
}
