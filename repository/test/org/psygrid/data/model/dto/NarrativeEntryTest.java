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

import org.psygrid.data.model.hibernate.NarrativeStyle;

public class NarrativeEntryTest {

    @Test()
	public void testToHibernate(){
            NarrativeEntryDTO ne = new NarrativeEntryDTO();
            String name = "Foo bar";
            ne.setName(name);
            
            NarrativeStyle style = NarrativeStyle.HEADER;
            ne.setStyle(style.toString());
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.NarrativeEntry hNE = ne.toHibernate(hRefs);

            AssertJUnit.assertNotNull("Hibernate narrative entry is null", hNE);
            AssertJUnit.assertEquals("Hibernate narrative entry does not have the correct name",name,hNE.getName());
            AssertJUnit.assertEquals("Hibernate narrative entry does not have the correct style",style,hNE.getStyle());
    }
}
