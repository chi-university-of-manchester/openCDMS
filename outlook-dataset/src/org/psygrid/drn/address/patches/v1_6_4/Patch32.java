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

package org.psygrid.drn.address.patches.v1_6_4;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.model.hibernate.GenericState;
import org.psygrid.data.model.hibernate.HibernateFactory;
import org.psygrid.data.model.hibernate.Status;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch32 extends AbstractPatch {

	public String getName() {
		return "Add 'Left Study' status to Address dataset";
	}

	public void applyPatch(DataSet ds, String saml) throws Exception {
		Factory factory = new HibernateFactory();

		Status statWithdrew = null;
		Status statDeceased = null;
		Status statInvalid  = null;
		Status statTempWithdrawn  = null;
		Status statConsented = null;
		Status statBaseline  = null;
		Status stat6M = null;
		Status stat1Y = null;
		Status stat2Y = null;
		Status stat3Y = null;
		Status stat4Y = null;
		Status stat5Y = null;

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
			if ("Temporarily Withdrawn".equals(ds.getStatus(i).getLongName())) {
				statTempWithdrawn = ds.getStatus(i);
			}
			if ("Consented".equals(ds.getStatus(i).getLongName())) {
				statConsented = ds.getStatus(i);
			}
			if ("Baseline completed".equals(ds.getStatus(i).getLongName())) {
				statBaseline = ds.getStatus(i);
			}
			if ("6 Month Follow-up completed".equals(ds.getStatus(i).getLongName())) {
				stat6M = ds.getStatus(i);
			}
			if ("1 Year Follow-up completed".equals(ds.getStatus(i).getLongName())) {
				stat1Y = ds.getStatus(i);
			}
			if ("2 Year Follow-up completed".equals(ds.getStatus(i).getLongName())) {
				stat2Y = ds.getStatus(i);
			}
			if ("3 Year Follow-up completed".equals(ds.getStatus(i).getLongName())) {
				stat3Y = ds.getStatus(i);
			}
			if ("4 Year Follow-up completed".equals(ds.getStatus(i).getLongName())) {
				stat4Y = ds.getStatus(i);
			}
			if ("5 Year Follow-up completed".equals(ds.getStatus(i).getLongName())) {
				stat5Y = ds.getStatus(i);
			}
		}

		if (statTempWithdrawn != null) {
			//Update the temporarily withdrawn statuses
			statTempWithdrawn.setGenericState(GenericState.LEFT);
		}

		Status statLeft = factory.createStatus("Left", "Left Study", 14);
		statLeft.setGenericState(GenericState.LEFT);
		statLeft.setInactive(true);

		statLeft.addStatusTransition(statWithdrew);					//Left Study -> Withdrew
		statLeft.addStatusTransition(statDeceased);					//Left Study -> Deceased
		statLeft.addStatusTransition(statInvalid);					//Left Study -> Invalid

		//Update the existing statuses transitions
		statConsented.addStatusTransition(statLeft);             //consented -> left study
		statBaseline.addStatusTransition(statLeft);            	//Baseline completed -> left study
		stat6M.addStatusTransition(statLeft);            		//6M FU completed -> left study
		stat1Y.addStatusTransition(statLeft);            		//1Y FU completed -> left study
		stat2Y.addStatusTransition(statLeft);            		//2Y FU completed -> left study
		stat3Y.addStatusTransition(statLeft);            		//3Y FU completed -> left study
		stat4Y.addStatusTransition(statLeft);            		//4Y FU completed -> left study
		stat5Y.addStatusTransition(statLeft);            		//5Y FU completed -> Left Study
		if (statTempWithdrawn != null) {
			statTempWithdrawn.addStatusTransition(statLeft);
		}

		ds.addStatus(statLeft);
	}

}
