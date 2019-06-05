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

public class ConsentFormGroupTest {

    @Test()
	public void testToHibernate(){
            
            ConsentFormGroupDTO cfg = new ConsentFormGroupDTO();
            String desc = "Description";
            cfg.setDescription(desc);
            Long id = new Long(32);
            cfg.setId(id);
            PrimaryConsentFormDTO pcf1 = new PrimaryConsentFormDTO();
            String pcf1Desc = "PCF1 Desc";
            pcf1.setQuestion(pcf1Desc);
            PrimaryConsentFormDTO pcf2 = new PrimaryConsentFormDTO();
            String pcf2Desc = "PCF2 Desc";
            pcf2.setQuestion(pcf2Desc);
            cfg.setConsentForms(new PrimaryConsentFormDTO[2]);
            cfg.getConsentForms()[0] = pcf1;
            cfg.getConsentForms()[1] = pcf2;
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.ConsentFormGroup hCFG = cfg.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate consent form group is null", hCFG);
            AssertJUnit.assertEquals("Hibernate consent form group has the wrong id",id,hCFG.getId());
            AssertJUnit.assertEquals("Hibernate consent form group has the description",desc,hCFG.getDescription());
            AssertJUnit.assertEquals("Hibernate consent form group has the wrong number of consent forms",cfg.getConsentForms().length,hCFG.getConsentForms().size());
            AssertJUnit.assertEquals("Hibernate consent form group has the wrong description for consent form 1",pcf1Desc,hCFG.getConsentForms().get(0).getQuestion());
            AssertJUnit.assertEquals("Hibernate consent form group has the wrong description for consent form 2",pcf2Desc,hCFG.getConsentForms().get(1).getQuestion());            
    }
}
