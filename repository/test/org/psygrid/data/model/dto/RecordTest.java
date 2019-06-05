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

public class RecordTest {

    @Test()
	public void testToHibernate(){
            RecordDTO r = new RecordDTO();
            
            IdentifierDTO identifier = new IdentifierDTO();
            identifier.setIdentifier("ID");
            r.setIdentifier(identifier);
            
            r.setConsents(new ConsentDTO[2]);
            ConsentDTO c1 = new ConsentDTO();
            Long c1id = new Long(2);
            c1.setId(c1id);
            r.getConsents()[0] = c1;
            ConsentDTO c2 = new ConsentDTO();
            Long c2id = new Long(3);
            c2.setId(c2id);
            r.getConsents()[1] = c2;
            
            Long dsId = new Long(4);
            r.setDataSetId(dsId);
            
            r.setDocInstances(new DocumentInstanceDTO[2]);
            DocumentInstanceDTO di1 = new DocumentInstanceDTO();
            Long di1id = new Long(5);
            di1.setId(di1id);
            r.getDocInstances()[0] = di1;
            DocumentInstanceDTO di2 = new DocumentInstanceDTO();
            Long di2id = new Long(6);
            di2.setId(di2id);
            r.getDocInstances()[1] = di2;
            
            boolean deleted = true;
            r.setDeleted(deleted);
            
            String primaryId = "Primary";
            r.setPrimaryIdentifier(primaryId);
            
            String secondaryId = "Secondary";
            r.setSecondaryIdentifier(secondaryId);
            
            RecordDataDTO rd = new RecordDataDTO();
            Long rdId = new Long(7);
            rd.setId(rdId);
            r.setTheRecordData(rd);
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.Record hR = r.toHibernate(hRefs);
        
            AssertJUnit.assertNotNull("Hibernate record is null", hR);
            AssertJUnit.assertEquals("Hibernate record has the wrong identifier",identifier.getIdentifier(),hR.getIdentifier().getIdentifier());
            AssertJUnit.assertEquals("Hibernate record has the wrong number of consents",r.getConsents().length,hR.getConsents().size());
            for ( org.psygrid.data.model.hibernate.Consent hC : hR.getConsents() ){
                AssertJUnit.assertNotNull("Hibernate record has null consent", hC);
                AssertJUnit.assertTrue("Hibernate record has the wrong consent", hC.getId().equals(c1id) || hC.getId().equals(c2id));
            }
            AssertJUnit.assertEquals("Hibernate record has the wrong dataset",dsId,hR.getDataSetId());
            AssertJUnit.assertEquals("Hibernate record has the wrong number of document instances",r.getDocInstances().length,hR.getDocInstances().size());
            for ( org.psygrid.data.model.hibernate.DocumentInstance hDI : hR.getDocInstances() ){
                AssertJUnit.assertNotNull("Hibernate record has null document instance", hDI);
                AssertJUnit.assertTrue("Hibernate record has the wrong document instance", hDI.getId().equals(di1id) || hDI.getId().equals(di2id));
            }
            AssertJUnit.assertEquals("Hibernate record has the wrong record data",rdId,hR.getTheRecordData().getId());
            AssertJUnit.assertEquals("Hibernate record has the wrong deleted",deleted,hR.isDeleted());
            AssertJUnit.assertEquals("Hibernate record has the wrong primary identifier",primaryId,hR.getPrimaryIdentifier());
            AssertJUnit.assertEquals("Hibernate record has the wrong secondary identifier",secondaryId,hR.getSecondaryIdentifier());
    }
}
