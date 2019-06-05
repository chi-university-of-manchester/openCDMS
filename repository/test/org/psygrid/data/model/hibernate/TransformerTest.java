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

public class TransformerTest {
    
    @Test()
	public void testToDTO(){
            Transformer t = new Transformer();
            String url = "URL";
            t.setWsUrl(url);
            String namespace = "Namespace";
            t.setWsNamespace(namespace);
            String operation = "Operation";
            t.setWsOperation(operation);
            String resultClass = "org.psygrid.data.model.hibernate.TextValue";
            t.setResultClass(resultClass);
            Long id = new Long(5);
            t.setId(id);
            boolean viewable = true;
            t.setViewableOutput(viewable);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.TransformerDTO dtoT = t.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
            AssertJUnit.assertNotNull("Hibernate transformer rule is null", dtoT);
            AssertJUnit.assertEquals("Hibernate transformer rule has the wrong url",url,dtoT.getWsUrl());
            AssertJUnit.assertEquals("Hibernate transformer rule has the wrong namespace",namespace,dtoT.getWsNamespace());
            AssertJUnit.assertEquals("Hibernate transformer rule has the wrong operation",operation,dtoT.getWsOperation());
            AssertJUnit.assertEquals("Hibernate transformer rule has the wrong result class",resultClass,dtoT.getResultClass());
            AssertJUnit.assertEquals("Hibernate transformer rule has the wrong id",id,dtoT.getId());
            AssertJUnit.assertEquals("Hibernate transformer rule has the wrong viewable output",viewable,dtoT.isViewableOutput());
    }
    
}
