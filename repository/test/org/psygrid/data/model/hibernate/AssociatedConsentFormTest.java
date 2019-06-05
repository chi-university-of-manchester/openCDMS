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

public class AssociatedConsentFormTest {

    @Test()
	public void testToDTO(){
            AssociatedConsentForm acf = new AssociatedConsentForm();
            Long id = new Long(1);
            acf.setId(id);
            int version = 1;
            acf.setVersion(version);
            String question = "Question";
            acf.setQuestion(question);
            String ref = "Ref";
            acf.setReferenceNumber(ref);
            acf.setElectronicDocument(new BinaryObject());
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.AssociatedConsentFormDTO dtoACF = acf.toDTO(dtoRefs,RetrieveDepth.DS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO associated consent form is null", dtoACF);
            AssertJUnit.assertEquals("DTO associated consent form has the wrong id",id,dtoACF.getId());
            AssertJUnit.assertEquals("DTO associated consent form has the wrong version",version,dtoACF.getVersion());
            AssertJUnit.assertEquals("DTO associated consent form has the wrong question",question,dtoACF.getQuestion());
            AssertJUnit.assertEquals("DTO associated consent form has the wrong reference",ref,dtoACF.getReferenceNumber());
            AssertJUnit.assertNotNull("DTO associated consent form has null electronic document",dtoACF.getElecDoc());
    }
    
    @Test()
	public void testGetBasicCopy(){
            AssociatedConsentForm acf = new AssociatedConsentForm();
            Long id = new Long(2);
            acf.setId(id);
            int version = 1;
            acf.setVersion(version);
            String question = "Question";
            acf.setQuestion(question);
            String ref = "Ref";
            acf.setReferenceNumber(ref);
            acf.setElectronicDocument(new BinaryObject());
            PrimaryConsentForm pcf = new PrimaryConsentForm();
            pcf.setId(new Long(3));
            acf.setPrimaryConsentForm(pcf);
            
            AssociatedConsentForm copy = acf.getBasicCopy();
            AssertJUnit.assertEquals("ACF copy has the wrong id", acf.getId(), copy.getId());
            AssertJUnit.assertNull("ACF copy has a reference to its parent PCF", copy.getPrimaryConsentForm());
            AssertJUnit.assertNull("ACF copy has a reference to its electonic document", copy.getElecDoc());
    }
}
