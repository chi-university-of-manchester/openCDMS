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

public class AssociatedConsentFormTest {

    @Test()
	public void testToHibernate(){
            AssociatedConsentFormDTO acf = new AssociatedConsentFormDTO();
            Long id = new Long(1);
            acf.setId(id);
            int version = 1;
            acf.setVersion(version);
            String question = "Question";
            acf.setQuestion(question);
            String ref = "Ref";
            acf.setReferenceNumber(ref);
            acf.setElecDoc(new BinaryObjectDTO());
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.AssociatedConsentForm hACF = acf.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate associated consent form is null", hACF);
            AssertJUnit.assertEquals("Hibernate associated consent form has the wrong id",id,hACF.getId());
            AssertJUnit.assertEquals("Hibernate associated consent form has the wrong version",version,hACF.getVersion());
            AssertJUnit.assertEquals("Hibernate associated consent form has the wrong question",question,hACF.getQuestion());
            AssertJUnit.assertEquals("Hibernate associated consent form has the wrong reference",ref,hACF.getReferenceNumber());
            AssertJUnit.assertNotNull("Hibernate associated consent form has null electronic document",hACF.getElectronicDocument());
    }
}
