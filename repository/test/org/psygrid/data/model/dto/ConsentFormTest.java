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

public class ConsentFormTest {

    @Test()
	public void testToHibernate(){
            ConsentFormDTO cf = new PrimaryConsentFormDTO();
            BinaryObjectDTO edoc = new BinaryObjectDTO();
            String edocDesc = "EDoc Desc";
            edoc.setDescription(edocDesc);
            cf.setElecDoc(edoc);
            Long id = new Long(43);
            cf.setId(id);
            String question = "Question";
            cf.setQuestion(question);
            String ref = "Ref";
            cf.setReferenceNumber(ref);
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.ConsentForm hCF = cf.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate associated consent form is null", hCF);
            AssertJUnit.assertEquals("Hibernate associated consent form has the wrong id",id,hCF.getId());
            AssertJUnit.assertEquals("Hibernate associated consent form has the wrong question",question,hCF.getQuestion());
            AssertJUnit.assertEquals("Hibernate associated consent form has the wrong reference",ref,hCF.getReferenceNumber());
            AssertJUnit.assertNotNull("Hibernate associated consent form has null electronic document",hCF.getElectronicDocument());
            AssertJUnit.assertEquals("Hibernate associated consent form has electronic document with the wrong description",edocDesc,hCF.getElectronicDocument().getDescription());
    }
}
