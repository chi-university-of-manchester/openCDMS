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

package org.psygrid.outlook.patches.v1_6_1;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.DocumentGroup;
import org.psygrid.data.model.hibernate.Status;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch61 extends AbstractPatch {

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#applyPatch(org.psygrid.data.model.IDataSet, java.lang.String)
	 */
	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		Status consented = ds.getStatus(1);
		if ( !"Consented".equals(consented.getShortName()) ){
			throw new RuntimeException("This is not the Consented status it is "+consented.getShortName());
		}
		Status iv1 = ds.getStatus(2);
		if ( !"Interview1".equals(iv1.getShortName()) ){
			throw new RuntimeException("This is not the Interview1 status it is "+iv1.getShortName());
		}
		Status iv2 = ds.getStatus(3);
		if ( !"Interview2".equals(iv2.getShortName()) ){
			throw new RuntimeException("This is not the Interview2 status it is "+iv2.getShortName());
		}
		Status iv3 = ds.getStatus(4);
		if ( !"Interview3".equals(iv3.getShortName()) ){
			throw new RuntimeException("This is not the Interview3 status it is "+iv3.getShortName());
		}
		boolean sixMonthFound = false;
		boolean twelveMonthFound = false;
		for ( int i=0, c=ds.numDocumentGroups(); i<c; i++ ){
			DocumentGroup grp = ds.getDocumentGroup(i);
			grp.getPrerequisiteGroups().clear();
			if ( grp.getName().equals("6 months Group") ){
				sixMonthFound = true;
				grp.addAllowedRecordStatus(consented);
				grp.addAllowedRecordStatus(iv1);
				grp.addAllowedRecordStatus(iv2);
			}
			if ( grp.getName().equals("12 months") ){
				twelveMonthFound = true;
				grp.addAllowedRecordStatus(consented);
				grp.addAllowedRecordStatus(iv1);
				grp.addAllowedRecordStatus(iv2);
				grp.addAllowedRecordStatus(iv3);
			}
		}
		if ( !sixMonthFound ){
			throw new RuntimeException("Six month group not found");
		}
		if ( !twelveMonthFound ){
			throw new RuntimeException("Twelve month group not found");
		}
	}

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#getName()
	 */
	@Override
	public String getName() {
		return "Unlock all study stages";
	}

}
