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

public class RecordTest {

    @Test()
	public void testToDTO(){
            Record r = new Record();
            
            Identifier identifier = new Identifier();
            identifier.setIdentifier("ID");
            r.setIdentifier(identifier);
            
            Consent c1 = new Consent();
            Long c1id = new Long(2);
            c1.setId(c1id);
            r.getConsents().add(c1);
            Consent c2 = new Consent();
            Long c2id = new Long(3);
            c2.setId(c2id);
            r.getConsents().add(c2);
            
            DataSet ds = new DataSet();
            Long dsId = new Long(4);
            ds.setId(dsId);
            r.setDataSet(ds);
            
            DocumentInstance di1 = new DocumentInstance();
            Long di1id = new Long(5);
            di1.setId(di1id);
            r.getDocInstances().add(di1);
            DocumentInstance di2 = new DocumentInstance();
            Long di2id = new Long(6);
            di2.setId(di2id);
            r.getDocInstances().add(di2);

            boolean deleted = true;
            r.setDeleted(deleted);
            
            String primaryId = "Primary";
            r.setPrimaryIdentifier(primaryId);
            
            String secondaryId = "Secondary";
            r.setSecondaryIdentifier(secondaryId);
            
            RecordData rd = new RecordData();
            Long rdId = new Long(7);
            rd.setId(rdId);
            r.setTheRecordData(rd);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.RecordDTO dtoR = r.toDTO(dtoRefs, RetrieveDepth.RS_COMPLETE);
        
            AssertJUnit.assertNotNull("DTO record is null", dtoR);
            AssertJUnit.assertEquals("DTO record has the wrong identifier",identifier.getIdentifier(),dtoR.getIdentifier().getIdentifier());
            AssertJUnit.assertEquals("DTO record has the wrong number of consents",r.getConsents().size(),dtoR.getConsents().length);
            for ( org.psygrid.data.model.dto.ConsentDTO dtoC : dtoR.getConsents() ){
                AssertJUnit.assertNotNull("DTO record has null consent", dtoC);
                AssertJUnit.assertTrue("DTO record has the wrong consent", dtoC.getId().equals(c1id) || dtoC.getId().equals(c2id));
            }
            AssertJUnit.assertEquals("DTO record has the wrong dataset",dsId,dtoR.getDataSetId());
            AssertJUnit.assertEquals("DTO record has the wrong number of document instances",r.getDocInstances().size(),dtoR.getDocInstances().length);
            for ( org.psygrid.data.model.dto.DocumentInstanceDTO dtoDI : dtoR.getDocInstances() ){
                AssertJUnit.assertNotNull("DTO record has null document instance", dtoDI);
                AssertJUnit.assertTrue("DTO record has the wrong document instance", dtoDI.getId().equals(di1id) || dtoDI.getId().equals(di2id));
            }
            AssertJUnit.assertEquals("DTO record has the wrong record data",rdId,dtoR.getTheRecordData().getId());
            AssertJUnit.assertEquals("DTO record has the wrong deleted",deleted,dtoR.isDeleted());
            AssertJUnit.assertEquals("DTO record has the wrong primary identifier",primaryId,dtoR.getPrimaryIdentifier());
            AssertJUnit.assertEquals("DTO record has the wrong secondary identifier",secondaryId,dtoR.getSecondaryIdentifier());
    }
}
