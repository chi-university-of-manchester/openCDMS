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

package org.psygrid.outlook.patches.v1_6_2;

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
public class Patch64 extends AbstractPatch {

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#applyPatch(org.psygrid.data.model.IDataSet, java.lang.String)
	 */
	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		Factory factory = new HibernateFactory();

		Status complete = factory.createStatus("Complete", "Complete", 10);
		complete.setGenericState(GenericState.COMPLETED);
		complete.setInactive(true);
		ds.addStatus(complete);

		Status deceased = ds.getStatus(7);
		if ( !"Deceased".equals(deceased.getShortName())){
			throw new RuntimeException("This is not the Deceased status, it is "+deceased.getShortName());
		}
		Status withdrew = ds.getStatus(8);
		if ( !"Withdrew".equals(withdrew.getShortName())){
			throw new RuntimeException("This is not the Withdrew status, it is "+withdrew.getShortName());
		}
		Status invalid = ds.getStatus(9);
		if ( !"Invalid".equals(invalid.getShortName())){
			throw new RuntimeException("This is not the Invalid status, it is "+invalid.getShortName());
		}
		Status twelveMonth = ds.getStatus(6);
		if ( !"12Month".equals(twelveMonth.getShortName())){
			throw new RuntimeException("This is not the 12Month status, it is "+twelveMonth.getShortName());
		}
		twelveMonth.setInactive(false);
		twelveMonth.setGenericState(GenericState.ACTIVE);
		twelveMonth.addStatusTransition(complete);
		twelveMonth.addStatusTransition(deceased);
		twelveMonth.addStatusTransition(withdrew);
		twelveMonth.addStatusTransition(invalid);

		DocumentGroup baseAGroup = ds.getDocumentGroup(0);
		if ( !"Baseline Sec A Group".equals(baseAGroup.getName())){
			throw new RuntimeException("This is not the Baseline Sec A Group, it is "+baseAGroup.getName());
		}
		baseAGroup.addAllowedRecordStatus(twelveMonth);

		DocumentGroup baseBGroup = ds.getDocumentGroup(1);
		if ( !"Baseline Sec B Group".equals(baseBGroup.getName())){
			throw new RuntimeException("This is not the Baseline Sec B Group, it is "+baseBGroup.getName());
		}
		baseBGroup.addAllowedRecordStatus(twelveMonth);

		DocumentGroup baseCGroup = ds.getDocumentGroup(2);
		if ( !"Baseline Sec C Group".equals(baseCGroup.getName())){
			throw new RuntimeException("This is not the Baseline Sec C Group, it is "+baseCGroup.getName());
		}
		baseCGroup.addAllowedRecordStatus(twelveMonth);

		DocumentGroup sixGroup = ds.getDocumentGroup(3);
		if ( !"6 months Group".equals(sixGroup.getName())){
			throw new RuntimeException("This is not the 6 months Group, it is "+sixGroup.getName());
		}
		sixGroup.addAllowedRecordStatus(twelveMonth);

		DocumentGroup twelveGroup = ds.getDocumentGroup(4);
		if ( !"12 months".equals(twelveGroup.getName())){
			throw new RuntimeException("This is not the 12 months Group, it is "+twelveGroup.getName());
		}
		twelveGroup.addAllowedRecordStatus(twelveMonth);

		DocumentGroup sharedGroup = ds.getDocumentGroup(6);
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
		return "Add complete status";
	}

}
