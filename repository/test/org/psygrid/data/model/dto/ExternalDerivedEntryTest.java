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

public class ExternalDerivedEntryTest {

	@Test()
	@SuppressWarnings("unchecked")
    public void testToHibernate(){
			//FIXME finish writing test case
            ExternalDerivedEntryDTO ede = new ExternalDerivedEntryDTO();
            
            //ede.setUnits(new Unit[1]);
           // ede.getUnits()[0] = new Unit();
            
            String keyX = "x";
            BasicEntryDTO varX = new NumericEntryDTO();
            Long varXId = new Long(8);
            varX.setId(varXId);
            String keyY = "y";
            BasicEntryDTO varY = new NumericEntryDTO();
            Long varYId = new Long(9);
            varY.setId(varYId);
            ede.setVariables(new BasicEntryDTO[2]);
            ede.setVariableKeys(new String[2]);
            ede.getVariables()[0] = varX;
            ede.getVariableKeys()[0] = keyX;
            ede.getVariables()[1] = varY;
            ede.getVariableKeys()[1] = keyY;
            
           // CompositeEntry ce = new CompositeEntry();
            //Long ceId = new Long(10);
           // ce.setId(ceId);
           // ede.setComposite(ce);

            //TODO finish this - create an Opcrit transformer
            org.psygrid.data.model.dto.TransformerDTO transformer = new org.psygrid.data.model.dto.TransformerDTO();
            ede.setExternalTransformer(transformer);
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.ExternalDerivedEntry hDE = ede.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate external derived entry is null", hDE);
           // assertEquals("Hibernate external derived entry does not have the right number of units",ede.getUnits().length,hDE.getUnits().size());
            AssertJUnit.assertEquals("Hibernate external derived entry does not have the correct number of variables",ede.getVariables().length,hDE.getVariables().size());
            AssertJUnit.assertEquals("Hibernate external derived entry does not have the the correct variable for key "+keyX,varXId,((org.psygrid.data.model.hibernate.BasicEntry)hDE.getVariables().get(keyX)).getId());
            AssertJUnit.assertEquals("Hibernate external derived entry does not have the the correct variable for key "+keyY,varYId,((org.psygrid.data.model.hibernate.BasicEntry)hDE.getVariables().get(keyY)).getId());
            //assertEquals("Hibernate derived entry does not have the the correct composite", ceId, hDE.getComposite().getId());
            AssertJUnit.assertNotNull("Hibernate external derived entry is null", hDE.getExternalTransformer());                              
    }
}
