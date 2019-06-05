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

package org.psygrid.matisse.patches.v1_1_12;

import java.util.ArrayList;
import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.DocumentGroup;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.model.hibernate.GenericState;
import org.psygrid.data.model.hibernate.HibernateFactory;
import org.psygrid.data.model.hibernate.Status;
import org.psygrid.outlook.patches.AbstractPatch;


public class Patch1 extends AbstractPatch {

	@Override
	public String getName() {
		return "Update dataset Statuses and DocumentGroups to include generic states";
	}

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		DataSet dataSet = (DataSet)ds;

		//Add new Statuses
		updateStatuses(dataSet);
	}

	private void updateStatuses(DataSet dataSet) throws Exception {
		Factory factory = new HibernateFactory();

		List<Status> statuses = dataSet.getStatuses();

		Status statReferred = null, statScreenInelig = null, statUnableToConsent = null,
		statConsented = null, statConsentRefused = null, statClinicianWithdrew = null, statBaseline = null,
		stat12Month = null, stat24Month = null, statDeceased = null, statWithdrew = null;

		/*
		 * Get statuses from the dataset
		 */
		for (Status status: statuses) {
			System.out.println("Found status id "+status.getId());
			//if status matches set to above status..
			if ("Referred".equals(status.getShortName())) {
				statReferred = status;
			}
			if ("Unable".equals(status.getShortName())) {
				statUnableToConsent = status;
			}
			if ("Ineligible".equals(status.getShortName())) {
				statScreenInelig = status;
			}
			if ("Consented".equals(status.getShortName())) {
				statConsented = status;
			}
			if ("Refused".equals(status.getShortName())) {
				statConsentRefused = status;
			}
			if ("Withdrawn".equals(status.getShortName())) {
				statClinicianWithdrew = status;
			}
			if ("Baseline".equals(status.getShortName())) {
				statBaseline = status;
			}
			if ("12Month".equals(status.getShortName())) {
				stat12Month = status;
			}
			if ("24Month".equals(status.getShortName())) {
				stat24Month = status;
			}
			if ("Deceased".equals(status.getShortName())) {
				statDeceased = status;
			}
			if ("Withdrew".equals(status.getShortName())) {
				statWithdrew = status;
			}

		}


		/*
		 * Add generic states to statuses
		 */
		statReferred.setGenericState(GenericState.REFERRED);
		statScreenInelig.setGenericState(GenericState.INACTIVE);
		statUnableToConsent.setGenericState(GenericState.INACTIVE);
		statConsented.setGenericState(GenericState.ACTIVE);
		statConsentRefused.setGenericState(GenericState.INACTIVE);
		statClinicianWithdrew.setGenericState(GenericState.INACTIVE);
		statBaseline.setGenericState(GenericState.ACTIVE);
		stat12Month.setGenericState(GenericState.ACTIVE);
		stat24Month.setGenericState(GenericState.COMPLETED);
		statDeceased.setGenericState(GenericState.LEFT);
		statWithdrew.setGenericState(GenericState.INACTIVE);


		/*
		 * Create new invalid state
		 */
		Status statInvalid = factory.createStatus("Invalid", "Invalid", 11);	//Record was added by mistake and shouldn't exist
		statInvalid.setInactive(true);
		statInvalid.setGenericState(GenericState.INVALID);

		dataSet.addStatus(statInvalid);

		/*
		 * Add status transitions to new invalid status
		 */
		statReferred.addStatusTransition(statInvalid);             //referred -> invalid
		statConsented.addStatusTransition(statInvalid);            //consented -> invalid
		statBaseline.addStatusTransition(statInvalid);           //baseline completed -> invalid
		stat12Month.addStatusTransition(statInvalid);           //12 month completed -> invalid
		stat24Month.addStatusTransition(statInvalid);            //24 month completed -> invalid


		/*
		 * Remove some transitions that don't make sense
		 */
		if (!statReferred.getStatusTransition(5).getShortName().equals(statBaseline.getShortName())) {
			throw new Exception("This is not the Baseline status!!");
		}
		if (!statReferred.getStatusTransition(6).getShortName().equals(stat12Month.getShortName())) {
			throw new Exception("This is not the 12Month status!!");
		}
		if (!statReferred.getStatusTransition(7).getShortName().equals(stat24Month.getShortName())) {
			throw new Exception("This is not the 24Month status!!");
		}
		statReferred.removeStatusTransition(5);
		statReferred.removeStatusTransition(6);
		statReferred.removeStatusTransition(7);


		/*
		 * Set Document Groups
		 */
		DocumentGroup baseline = null, twelveMonths = null, twentyFourMonths = null;

		for (DocumentGroup group: dataSet.getDocumentGroups()) {
			if (group.getName().equals("Baseline")) {
				baseline = group;
			}
			else if (group.getName().equals("12 months")) {
				twelveMonths = group;
			}
			else if (group.getName().equals("24 months")) {
				twentyFourMonths = group;
			}
		}


		/*
		 * Update Document Groups
		 */
		baseline.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
		baseline.setUpdateStatus(statBaseline);

		twelveMonths.addAllowedRecordStatus(statBaseline);
		twelveMonths.addAllowedRecordStatus(stat12Month);
		twelveMonths.addPrerequisiteGroup(baseline);
		twelveMonths.setUpdateStatus(stat12Month);

		twentyFourMonths.addAllowedRecordStatus(stat12Month);
		twentyFourMonths.addPrerequisiteGroup(twelveMonths);
		twentyFourMonths.setUpdateStatus(stat24Month);
	}

	/**
	 * Get all DataSet statuses having the given generic state.
	 *
	 * @param dataSet
	 * @param genericState
	 * @return statuses
	 */
	private static List<Status> getStatuses(DataSet dataSet, GenericState genericState) {
		List<Status> statuses = new ArrayList<Status>();
		String state = genericState.toString();
		for (Status status: ((DataSet)dataSet).getStatuses()) {
			if (status.getGenericState() != null && state.equals(status.getGenericState().toString())) {
				statuses.add(status);
			}
		}
		return statuses;
	}
}
