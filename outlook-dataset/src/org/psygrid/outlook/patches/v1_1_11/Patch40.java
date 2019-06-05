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


package org.psygrid.outlook.patches.v1_1_11;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Status;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch40 extends AbstractPatch {

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#getName()
	 */
	@Override
	public String getName() {
		return "Remove Referred status - RUN SQL TO AMEND RECORDS FIRST!";
	}

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#applyPatch(org.psygrid.data.model.IDataSet, java.lang.String)
	 */
	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		int referredIndex = 0;
		int ineligibleIndex = 1;
		int unableIndex = 2;
		int refusedIndex = 4;
		int withdrawnIndex = 5;

		Status withdrawnStatus = ds.getStatus(withdrawnIndex);
		if ( !"Withdrawn".equals(withdrawnStatus.getShortName())){
			throw new RuntimeException("This is not the Withdrawn status, it is "+withdrawnStatus.getShortName());
		}
		ds.removeStatus(withdrawnIndex);

		Status refusedStatus = ds.getStatus(refusedIndex);
		if ( !"Refused".equals(refusedStatus.getShortName())){
			throw new RuntimeException("This is not the Withdrawn status, it is "+refusedStatus.getShortName());
		}
		ds.removeStatus(refusedIndex);

		Status unableStatus = ds.getStatus(unableIndex);
		if ( !"Unable".equals(unableStatus.getShortName())){
			throw new RuntimeException("This is not the Withdrawn status, it is "+unableStatus.getShortName());
		}
		ds.removeStatus(unableIndex);

		Status ineligibleStatus = ds.getStatus(ineligibleIndex);
		if ( !"Ineligible".equals(ineligibleStatus.getShortName())){
			throw new RuntimeException("This is not the Withdrawn status, it is "+ineligibleStatus.getShortName());
		}
		ds.removeStatus(ineligibleIndex);

		Status referredStatus = ds.getStatus(referredIndex);
		if ( !"Referred".equals(referredStatus.getShortName())){
			throw new RuntimeException("This is not the Withdrawn status, it is "+referredStatus.getShortName());
		}
		ds.removeStatus(referredIndex);

	}

}
