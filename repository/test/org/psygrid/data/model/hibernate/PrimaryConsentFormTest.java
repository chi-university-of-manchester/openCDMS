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

package org.psygrid.data.model.hibernate;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import java.util.HashMap;
import java.util.Map;

public class PrimaryConsentFormTest {

    @Test()
	public void testToDTO(){
            PrimaryConsentForm pcf = new PrimaryConsentForm();
            AssociatedConsentForm acf1 = new AssociatedConsentForm();
            Long acf1id = new Long(2);
            acf1.setId(acf1id);
            pcf.getAssociatedConsentForms().add(acf1);
            AssociatedConsentForm acf2 = new AssociatedConsentForm();
            Long acf2id = new Long(3);
            acf2.setId(acf2id);
            pcf.getAssociatedConsentForms().add(acf2);
            ConsentFormGroup cfg = new ConsentFormGroup();
            Long cfgId = new Long(4);
            cfg.setId(cfgId);
            pcf.setGroup(cfg);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.PrimaryConsentFormDTO dtoPCF = pcf.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO primary consent form is null", dtoPCF);
            AssertJUnit.assertEquals("DTO primary consent form has the wrong number of associated consent forms",pcf.getAssociatedConsentForms().size(),dtoPCF.getAssociatedConsentForms().length);
            AssertJUnit.assertNotNull("DTO primary consent form has null associated consent form at index 0", dtoPCF.getAssociatedConsentForms()[0]);
            AssertJUnit.assertEquals("DTO primary consent form has the wrong associated consent form at index 0",acf1id,dtoPCF.getAssociatedConsentForms()[0].getId());
            AssertJUnit.assertNotNull("DTO primary consent form has null associated consent form at index 1", dtoPCF.getAssociatedConsentForms()[1]);
            AssertJUnit.assertEquals("DTO primary consent form has the wrong associated consent form at index 1",acf2id,dtoPCF.getAssociatedConsentForms()[1].getId());
            AssertJUnit.assertEquals("DTO primary consent form has the wrong consent form group",cfgId,dtoPCF.getGroup().getId());
    }

    @Test()
	public void testGetBasicCopy(){
            PrimaryConsentForm pcf = new PrimaryConsentForm();
            AssociatedConsentForm acf1 = new AssociatedConsentForm();
            Long acf1id = new Long(2);
            acf1.setId(acf1id);
            pcf.getAssociatedConsentForms().add(acf1);
            AssociatedConsentForm acf2 = new AssociatedConsentForm();
            Long acf2id = new Long(3);
            acf2.setId(acf2id);
            pcf.getAssociatedConsentForms().add(acf2);
            ConsentFormGroup cfg = new ConsentFormGroup();
            Long cfgId = new Long(4);
            cfg.setId(cfgId);
            pcf.setGroup(cfg);
            
            PrimaryConsentForm copy = pcf.getBasicCopy();
            AssertJUnit.assertEquals("PCF copy has the wrong id", pcf.getId(), copy.getId());
            AssertJUnit.assertNull("PCF copy has a reference to its parent group", copy.getGroup());
            AssertJUnit.assertNull("PCF copy has a reference to its electronic document", copy.getElecDoc());
    }

}
