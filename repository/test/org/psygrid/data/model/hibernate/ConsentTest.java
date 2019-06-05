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

public class ConsentTest {

    @Test()
	public void testToDTO(){
            Consent c = new Consent();
            BinaryObject cDoc = new BinaryObject();
            Long cDocId = new Long(32);
            cDoc.setId(cDocId);
            c.setConsentDoc(cDoc);
            ConsentForm cf = new PrimaryConsentForm();
            Long cfId = new Long(87);
            cf.setId(cfId);
            c.setConsentForm(cf);
            boolean cons = true;
            c.setConsentGiven(cons);
            Long id = new Long(56);
            c.setId(id);
            String loc = "Location";
            c.setLocation(loc);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.ConsentDTO dtoC = c.toDTO(dtoRefs, RetrieveDepth.RS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO consent is null", dtoC);
            AssertJUnit.assertNotNull("DTO consent has null consent document",dtoC.getConsentDoc());
            AssertJUnit.assertEquals("DTO consent has the consent document with the wrong id",cDocId,dtoC.getConsentDoc().getId());
            AssertJUnit.assertEquals("DTO consent has the consent form with the wrong id",cfId,dtoC.getConsentFormId());
            AssertJUnit.assertEquals("DTO consent has the wrong id",id,dtoC.getId());
            AssertJUnit.assertEquals("DTO consent has the wrong consent given",cons,dtoC.isConsentGiven());
            AssertJUnit.assertEquals("DTO consent has the wrong location",loc,dtoC.getLocation());
    }
    
    @Test()
	public void testGetBasicCopy(){
            Consent c = new Consent();
            BinaryObject cDoc = new BinaryObject();
            Long cDocId = new Long(32);
            cDoc.setId(cDocId);
            c.setConsentDoc(cDoc);
            ConsentForm cf = new PrimaryConsentForm();
            Long cfId = new Long(87);
            cf.setId(cfId);
            c.setConsentForm(cf);
            boolean cons = true;
            c.setConsentGiven(cons);
            Long id = new Long(56);
            c.setId(id);
            String loc = "Location";
            c.setLocation(loc);
            
            Consent copy = (Consent)c.getBasicCopy();
            AssertJUnit.assertEquals("Consent copy has the wrong consent given value", cons, copy.isConsentGiven());
            AssertJUnit.assertEquals("Consent copy has the wrong location", loc, copy.getLocation());
            AssertJUnit.assertEquals("Consent copy has the wrong id", id, copy.getId());
            AssertJUnit.assertEquals("Consent copy has the wrong consent form", cfId, copy.getConsentForm().getId());
    }
}
