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

public class PrimaryConsentFormTest {

    @Test()
	public void testToHibernate(){
            PrimaryConsentFormDTO pcf = new PrimaryConsentFormDTO();
            pcf.setAssociatedConsentForms(new AssociatedConsentFormDTO[2]);
            AssociatedConsentFormDTO acf1 = new AssociatedConsentFormDTO();
            Long acf1id = new Long(2);
            acf1.setId(acf1id);
            pcf.getAssociatedConsentForms()[0] = acf1;
            AssociatedConsentFormDTO acf2 = new AssociatedConsentFormDTO();
            Long acf2id = new Long(3);
            acf2.setId(acf2id);
            pcf.getAssociatedConsentForms()[1] = acf2;
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.PrimaryConsentForm hPCF = pcf.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate primary consent form is null", hPCF);
            AssertJUnit.assertEquals("Hibernate primary consent form has the wrong number of associated consent forms",pcf.getAssociatedConsentForms().length,hPCF.getAssociatedConsentForms().size());
            AssertJUnit.assertNotNull("Hibernate primary consent form has null associated consent form at index 0", hPCF.getAssociatedConsentForms().get(0));
            AssertJUnit.assertEquals("Hibernate primary consent form has the wrong associated consent form at index 0",acf1id,hPCF.getAssociatedConsentForms().get(0).getId());
            AssertJUnit.assertNotNull("Hibernate primary consent form has null associated consent form at index 1", hPCF.getAssociatedConsentForms().get(1));
            AssertJUnit.assertEquals("Hibernate primary consent form has the wrong associated consent form at index 1",acf2id,hPCF.getAssociatedConsentForms().get(1).getId());
    }
}
