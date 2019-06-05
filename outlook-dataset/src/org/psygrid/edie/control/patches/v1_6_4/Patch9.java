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

package org.psygrid.edie.control.patches.v1_6_4;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.model.hibernate.GenericState;
import org.psygrid.data.model.hibernate.HibernateFactory;
import org.psygrid.data.model.hibernate.Status;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch9 extends AbstractPatch {

	public String getName() {
		return "Add 'Left Study' status to EDIE2 Control dataset";
	}

	public void applyPatch(DataSet ds, String saml) throws Exception {
		Factory factory = new HibernateFactory();

		Status statWithdrew = null;
		Status statDeceased = null;
		Status statInvalid  = null;
		Status statConsented = null;
		Status statActive = null;

		for (int i = 0; i < ds.numStatus(); i++) {
			if ("Withdrew".equals(ds.getStatus(i).getLongName())) {
				statWithdrew = ds.getStatus(i);
			}
			if ("Deceased".equals(ds.getStatus(i).getLongName())) {
				statDeceased = ds.getStatus(i);
			}
			if ("Invalid".equals(ds.getStatus(i).getLongName())) {
				statInvalid = ds.getStatus(i);
			}
			if ("Consented".equals(ds.getStatus(i).getLongName())) {
				statConsented = ds.getStatus(i);
			}
			if ("Active".equals(ds.getStatus(i).getLongName())) {
				statActive = ds.getStatus(i);
			}
		}

		Status statLeft = factory.createStatus("Left", "Left Study", 12);
		statLeft.setGenericState(GenericState.LEFT);
		statLeft.setInactive(true);

		statLeft.addStatusTransition(statWithdrew);					//Left Study -> Withdrew
		statLeft.addStatusTransition(statDeceased);					//Left Study -> Deceased
		statLeft.addStatusTransition(statInvalid);					//Left Study -> Invalid

		statConsented.addStatusTransition(statLeft); //consented -> left study
		statActive.addStatusTransition(statLeft); //active -> left study

		ds.addStatus(statLeft);
	}

}
