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


package org.psygrid.neden.patches.v1_1_6;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Status;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch12 extends AbstractPatch {

	@Override
	public String getName() {
		return "Make Record state changes more flexible";
	}

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		Status referred = ds.getStatus(0);
		if ( !"Referred".equals(referred.getShortName()) ){
			throw new RuntimeException("This is not the Referred status - it is "+referred.getShortName());
		}
		Status consented = ds.getStatus(3);
		if ( !"Consented".equals(consented.getShortName()) ){
			throw new RuntimeException("This is not the Consented status - it is "+consented.getShortName());
		}
		Status baseline = ds.getStatus(6);
		if ( !"Baseline".equals(baseline.getShortName()) ){
			throw new RuntimeException("This is not the Baseline status - it is "+baseline.getShortName());
		}
		Status sixMonth = ds.getStatus(7);
		if ( !"6Month".equals(sixMonth.getShortName()) ){
			throw new RuntimeException("This is not the 6Month status - it is "+sixMonth.getShortName());
		}
		Status twelveMonth = ds.getStatus(8);
		if ( !"12Month".equals(twelveMonth.getShortName()) ){
			throw new RuntimeException("This is not the 12Month status - it is "+twelveMonth.getShortName());
		}
		Status deceased = ds.getStatus(9);
		if ( !"Deceased".equals(deceased.getShortName()) ){
			throw new RuntimeException("This is not the Deceased status - it is "+deceased.getShortName());
		}
		Status withdrew = ds.getStatus(10);
		if ( !"Withdrew".equals(withdrew.getShortName()) ){
			throw new RuntimeException("This is not the Withdrew status - it is "+withdrew.getShortName());
		}

		referred.addStatusTransition(baseline);
		referred.addStatusTransition(sixMonth);
		referred.addStatusTransition(twelveMonth);
		referred.addStatusTransition(deceased);
		referred.addStatusTransition(withdrew);

		int numTrans = consented.numStatusTransitions();
		consented.removeStatusTransition(numTrans-1);
		consented.removeStatusTransition(numTrans-2);
		consented.addStatusTransition(sixMonth);
		consented.addStatusTransition(twelveMonth);
		consented.addStatusTransition(deceased);
		consented.addStatusTransition(withdrew);

		numTrans = baseline.numStatusTransitions();
		baseline.removeStatusTransition(numTrans-1);
		baseline.removeStatusTransition(numTrans-2);
		baseline.addStatusTransition(twelveMonth);
		baseline.addStatusTransition(deceased);
		baseline.addStatusTransition(withdrew);

	}

}
