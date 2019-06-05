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


package org.psygrid.drn.address.patches.v1_1_29;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.model.hibernate.GenericState;
import org.psygrid.data.model.hibernate.HibernateFactory;
import org.psygrid.data.model.hibernate.Status;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch8 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		Factory factory = new HibernateFactory();
		Status statTempWithdrawn = factory.createStatus("TempWithdrawn", "Temporarily Withdrawn", 13);
		statTempWithdrawn.setInactive(true);
		statTempWithdrawn.setGenericState(GenericState.INACTIVE);

		ds.addStatus(statTempWithdrawn);

		Status ref = ds.getStatus(0);
		if ( !"Referred".equals(ref.getShortName()) ){
			throw new RuntimeException("This is not the Referred status, it is "+ref.getShortName());
		}
		Status cons = ds.getStatus(1);
		if ( !"Consented".equals(cons.getShortName()) ){
			throw new RuntimeException("This is not the Consented status, it is "+cons.getShortName());
		}
		Status base = ds.getStatus(2);
		if ( !"Baseline".equals(base.getShortName()) ){
			throw new RuntimeException("This is not the Baseline status, it is "+base.getShortName());
		}
		Status s6m = ds.getStatus(3);
		if ( !"6Month".equals(s6m.getShortName()) ){
			throw new RuntimeException("This is not the 6Month status, it is "+s6m.getShortName());
		}
		Status s1y = ds.getStatus(4);
		if ( !"1Year".equals(s1y.getShortName()) ){
			throw new RuntimeException("This is not the 1Year status, it is "+s1y.getShortName());
		}
		Status s2y = ds.getStatus(5);
		if ( !"2Year".equals(s2y.getShortName()) ){
			throw new RuntimeException("This is not the 2Year status, it is "+s2y.getShortName());
		}
		Status s3y = ds.getStatus(6);
		if ( !"3Year".equals(s3y.getShortName()) ){
			throw new RuntimeException("This is not the 3Year status, it is "+s3y.getShortName());
		}
		Status s4y = ds.getStatus(7);
		if ( !"4Year".equals(s4y.getShortName()) ){
			throw new RuntimeException("This is not the 4Year status, it is "+s4y.getShortName());
		}
		Status s5y = ds.getStatus(8);
		if ( !"5Year".equals(s5y.getShortName()) ){
			throw new RuntimeException("This is not the 5Year status, it is "+s5y.getShortName());
		}
		Status dec = ds.getStatus(10);
		if ( !"Deceased".equals(dec.getShortName()) ){
			throw new RuntimeException("This is not the Deceased status, it is "+dec.getShortName());
		}
		Status with = ds.getStatus(11);
		if ( !"Withdrew".equals(with.getShortName()) ){
			throw new RuntimeException("This is not the Withdrew status, it is "+with.getShortName());
		}
		Status inv = ds.getStatus(12);
		if ( !"Invalid".equals(inv.getShortName()) ){
			throw new RuntimeException("This is not the Invalid status, it is "+inv.getShortName());
		}

		ref.addStatusTransition(statTempWithdrawn);
		cons.addStatusTransition(statTempWithdrawn);
		base.addStatusTransition(statTempWithdrawn);
		s6m.addStatusTransition(statTempWithdrawn);
		s1y.addStatusTransition(statTempWithdrawn);
		s2y.addStatusTransition(statTempWithdrawn);
		s3y.addStatusTransition(statTempWithdrawn);
		s4y.addStatusTransition(statTempWithdrawn);
		s5y.addStatusTransition(statTempWithdrawn);

		statTempWithdrawn.addStatusTransition(ref);
		statTempWithdrawn.addStatusTransition(cons);
		statTempWithdrawn.addStatusTransition(base);
		statTempWithdrawn.addStatusTransition(s6m);
		statTempWithdrawn.addStatusTransition(s1y);
		statTempWithdrawn.addStatusTransition(s2y);
		statTempWithdrawn.addStatusTransition(s3y);
		statTempWithdrawn.addStatusTransition(s4y);
		statTempWithdrawn.addStatusTransition(s5y);
		statTempWithdrawn.addStatusTransition(with);
		statTempWithdrawn.addStatusTransition(dec);
		statTempWithdrawn.addStatusTransition(inv);

	}

	@Override
	public String getName() {
		return "Add Temporarily Withdrawn status";
	}

}
