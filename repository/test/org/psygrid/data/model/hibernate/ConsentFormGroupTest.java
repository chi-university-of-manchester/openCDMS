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

public class ConsentFormGroupTest {

    @Test()
	public void testToDTO(){
            ConsentFormGroup cfg = new ConsentFormGroup();
            String desc = "Description";
            cfg.setDescription(desc);
            Long id = new Long(32);
            cfg.setId(id);
            PrimaryConsentForm pcf1 = new PrimaryConsentForm();
            String pcf1Desc = "PCF1 Desc";
            pcf1.setQuestion(pcf1Desc);
            PrimaryConsentForm pcf2 = new PrimaryConsentForm();
            String pcf2Desc = "PCF2 Desc";
            pcf2.setQuestion(pcf2Desc);
            cfg.getConsentForms().add(pcf1);
            cfg.getConsentForms().add(pcf2);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.ConsentFormGroupDTO dtoCFG = cfg.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO consent form group is null", dtoCFG);
            AssertJUnit.assertEquals("DTO consent form group has the wrong id",id,dtoCFG.getId());
            AssertJUnit.assertEquals("DTO consent form group has the description",desc,dtoCFG.getDescription());
            AssertJUnit.assertEquals("DTO consent form group has the wrong number of consent forms",cfg.getConsentForms().size(),dtoCFG.getConsentForms().length);
            AssertJUnit.assertEquals("DTO consent form group has the wrong description for consent form 1",pcf1Desc,dtoCFG.getConsentForms()[0].getQuestion());
            AssertJUnit.assertEquals("DTO consent form group has the wrong description for consent form 2",pcf2Desc,dtoCFG.getConsentForms()[1].getQuestion());
    }
}
