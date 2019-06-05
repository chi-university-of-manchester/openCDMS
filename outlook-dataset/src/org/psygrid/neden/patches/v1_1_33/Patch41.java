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

package org.psygrid.neden.patches.v1_1_33;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.DocumentGroup;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.model.hibernate.GenericState;
import org.psygrid.data.model.hibernate.HibernateFactory;
import org.psygrid.data.model.hibernate.Status;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch41 extends AbstractPatch {

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#applyPatch(org.psygrid.data.model.IDataSet, java.lang.String)
	 */
	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		Factory factory = new HibernateFactory();

		Status complete = factory.createStatus("Complete", "Complete", 12);
		complete.setGenericState(GenericState.COMPLETED);
		complete.setInactive(true);
		ds.addStatus(complete);

		Status twelveMonth = ds.getStatus(8);
		if ( !"12Month".equals(twelveMonth.getShortName())){
			throw new RuntimeException("This is not the 12Month status, it is "+twelveMonth.getShortName());
		}

		twelveMonth.setInactive(false);
		twelveMonth.setGenericState(GenericState.ACTIVE);
		twelveMonth.addStatusTransition(complete);

		DocumentGroup baseGroup = ds.getDocumentGroup(0);
		if ( !"Baseline Group".equals(baseGroup.getName())){
			throw new RuntimeException("This is not the Baseline Group, it is "+baseGroup.getName());
		}
		baseGroup.addAllowedRecordStatus(twelveMonth);

		DocumentGroup sixGroup = ds.getDocumentGroup(1);
		if ( !"6 months Group".equals(sixGroup.getName())){
			throw new RuntimeException("This is not the 6 months Group, it is "+sixGroup.getName());
		}
		sixGroup.addAllowedRecordStatus(twelveMonth);

		DocumentGroup twelveGroup = ds.getDocumentGroup(2);
		if ( !"12 months Group".equals(twelveGroup.getName())){
			throw new RuntimeException("This is not the 12 months Group, it is "+twelveGroup.getName());
		}
		twelveGroup.addAllowedRecordStatus(twelveMonth);
		twelveGroup.setUpdateStatus(twelveMonth);

		DocumentGroup sharedGroup = ds.getDocumentGroup(3);
		if ( !"Shared".equals(sharedGroup.getName())){
			throw new RuntimeException("This is not the Shared Group, it is "+sharedGroup.getName());
		}
		sharedGroup.addAllowedRecordStatus(twelveMonth);


	}

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#getName()
	 */
	@Override
	public String getName() {
		return "Unlock the shared folder at 12 Months";
	}

}
