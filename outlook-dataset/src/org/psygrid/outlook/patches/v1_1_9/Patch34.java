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


package org.psygrid.outlook.patches.v1_1_9;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch34 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		Document baf = ds.getDocument(2);
		if ( !"Baseline Audit".equals(baf.getName())){
			throw new RuntimeException("This is not the Baseline Audit document, this is "+baf.getName());
		}

		Entry e1 = baf.getEntry(2);
		if ( !"Age 14-35".equals(e1.getName())){
			throw new RuntimeException("This is not the Age 14-35 entry, this is "+e1.getName());
		}
		e1.setName("Age 14-65");
		e1.setDisplayText("Age 14-65?");

		Entry e2 = baf.getEntry(24);
		if ( !"Age 14-35".equals(e2.getName())){
			throw new RuntimeException("This is not the Age 14-35 entry, this is "+e2.getName());
		}
		e2.setName("Age 14-65");
		e2.setDisplayText("Age 14-65?");

		Document who = ds.getDocument(3);
		if ( !"WHO Screening Schedule".equals(who.getName())){
			throw new RuntimeException("This is not the WHO Screening Schedule document, this is "+who.getName());
		}

		Entry e3 = who.getEntry(1);
		if ( !"Patient's age".equals(e3.getName())){
			throw new RuntimeException("This is not the Patient's age entry, this is "+e3.getName());
		}
		e3.setDisplayText("Is this patient's age below 14 or above 65?");

	}

	@Override
	public String getName() {
		return "Change age criteria from 35 to 65";
	}

}
