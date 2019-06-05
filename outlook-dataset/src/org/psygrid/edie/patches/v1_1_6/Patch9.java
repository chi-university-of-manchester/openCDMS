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


package org.psygrid.edie.patches.v1_1_6;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Status;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch9 extends AbstractPatch {

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
		Status active = ds.getStatus(6);
		if ( !"Active".equals(active.getShortName()) ){
			throw new RuntimeException("This is not the Active status - it is "+active.getShortName());
		}
		Status complete = ds.getStatus(7);
		if ( !"Complete".equals(complete.getShortName()) ){
			throw new RuntimeException("This is not the Complete status - it is "+complete.getShortName());
		}
		Status deceased = ds.getStatus(8);
		if ( !"Deceased".equals(deceased.getShortName()) ){
			throw new RuntimeException("This is not the Deceased status - it is "+deceased.getShortName());
		}
		Status withdrew = ds.getStatus(9);
		if ( !"Withdrew".equals(withdrew.getShortName()) ){
			throw new RuntimeException("This is not the Withdrew status - it is "+withdrew.getShortName());
		}
		Status lost = ds.getStatus(10);
		if ( !"Lost".equals(lost.getShortName()) ){
			throw new RuntimeException("This is not the Lost status - it is "+lost.getShortName());
		}

		int numTrans = referred.numStatusTransitions();
		referred.removeStatusTransition(numTrans-1);
		referred.addStatusTransition(active);
		referred.addStatusTransition(complete);
		referred.addStatusTransition(deceased);
		referred.addStatusTransition(withdrew);
		referred.addStatusTransition(lost);

		numTrans = consented.numStatusTransitions();
		consented.removeStatusTransition(numTrans-1);
		consented.removeStatusTransition(numTrans-2);
		consented.removeStatusTransition(numTrans-3);
		consented.addStatusTransition(complete);
		consented.addStatusTransition(deceased);
		consented.addStatusTransition(withdrew);
		consented.addStatusTransition(lost);

	}

}
