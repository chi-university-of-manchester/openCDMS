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

public class TransformerTest {

    @Test()
	public void testToHibernate(){
            TransformerDTO t = new TransformerDTO();
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
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.Transformer hT = t.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate transformer rule is null", hT);
            AssertJUnit.assertEquals("Hibernate transformer rule has the wrong url",url,hT.getWsUrl());
            AssertJUnit.assertEquals("Hibernate transformer rule has the wrong namespace",namespace,hT.getWsNamespace());
            AssertJUnit.assertEquals("Hibernate transformer rule has the wrong operation",operation,hT.getWsOperation());
            AssertJUnit.assertEquals("Hibernate transformer rule has the wrong result class",resultClass,hT.getResultClass());
            AssertJUnit.assertEquals("Hibernate transformer rule has the wrong id",id,hT.getId());
            AssertJUnit.assertEquals("Hibernate transformer rule has the wrong viewable output",viewable,hT.isViewableOutput());
    }
    
}
