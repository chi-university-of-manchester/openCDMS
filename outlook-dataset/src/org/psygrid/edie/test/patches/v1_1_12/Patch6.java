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

package org.psygrid.edie.test.patches.v1_1_12;

import java.util.ArrayList;
import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.DocumentGroup;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.model.hibernate.GenericState;
import org.psygrid.data.model.hibernate.HibernateFactory;
import org.psygrid.data.model.hibernate.Status;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch6 extends AbstractPatch {

	@Override
	public String getName() {
		return "Update dataset statuses to include generic states and document group state transitions";
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
		statConsented = null, statConsentRefused = null, statClinicianWithdrew = null, statActive = null,
		statComplete = null, statDeceased = null, statWithdrew = null, statLost = null;

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
			}if ("Ineligible".equals(status.getShortName())) {
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
			if ("Active".equals(status.getShortName())) {
				statActive = status;
			}
			if ("Complete".equals(status.getShortName())) {
				statComplete = status;
			}
			if ("Deceased".equals(status.getShortName())) {
				statDeceased = status;
			}
			if ("Withdrew".equals(status.getShortName())) {
				statWithdrew = status;
			}
			if ("Lost".equals(status.getShortName())) {
				statLost = status;
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
		statActive.setGenericState(GenericState.ACTIVE);
		statComplete.setGenericState(GenericState.COMPLETED);
		statDeceased.setGenericState(GenericState.LEFT);
		statWithdrew.setGenericState(GenericState.INACTIVE);
		statLost.setGenericState(GenericState.LEFT);

		/*
		 * Create new invalid state
		 */
		Status statInvalid = factory.createStatus("Invalid", "Invalid", 11);	//Record was added by mistake and shouldn't exist
		statInvalid.setInactive(true);
		statInvalid.setGenericState(GenericState.INVALID);

		dataSet.addStatus(statInvalid);


		/*
		 * Remove some transitions that don't make sense
		 */
		//EDIE Test doesn't seem to have this transition..
		//if (!statReferred.getStatusTransition(5).getShortName().equals(statActive.getShortName())) {
			//throw new Exception("This is not the active status!!");
		//}
		//statReferred.removeStatusTransition(5);


		/*
		 * Add status transitions to new invalid status
		 */
		statReferred.addStatusTransition(statInvalid); //referred -> invalid
		statConsented.addStatusTransition(statInvalid); //consented -> invalid
		statActive.addStatusTransition(statInvalid); //active -> invalid

		/*
		 * Set Document Groups
		 */
		DocumentGroup baselineMinus1 = null, baseline0 = null, oneMonth = null, twoMonths = null, threeMonths = null,
		fourMonths = null, fiveMonths = null, sixMonths = null, nineMonths = null, twelveMonths = null, fifteenMonths = null,
		eighteenMonths = null, twentyOneMonths = null, twentyFourMonths = null, studyTermination = null, transition = null;

		for (DocumentGroup group: dataSet.getDocumentGroups()) {
			if (group.getName().equals("Baseline -1 Group")) {
				baselineMinus1 = group;
			}
			if (group.getName().equals("Baseline 0 Group")) {
				baseline0 = group;
			}
			else if (group.getName().equals("1 month Group")) {
				oneMonth = group;
			}
			else if (group.getName().equals("2 months Group")) {
				twoMonths = group;
			}
			else if (group.getName().equals("3 months Group")) {
				threeMonths = group;
			}
			else if (group.getName().equals("4 months Group")) {
				fourMonths = group;
			}
			else if (group.getName().equals("5 months Group")) {
				fiveMonths = group;
			}
			else if (group.getName().equals("6 months Group")) {
				sixMonths = group;
			}
			else if (group.getName().equals("9 months Group")) {
				nineMonths = group;
			}
			else if (group.getName().equals("12 months")) {
				twelveMonths = group;
			}
			else if (group.getName().equals("15 months Group")) {
				fifteenMonths = group;
			}
			else if (group.getName().equals("18 months Group")) {
				eighteenMonths = group;
			}
			else if (group.getName().equals("21 months Group")) {
				twentyOneMonths = group;
			}
			else if (group.getName().equals("24 months Group")) {
				twentyFourMonths = group;
			}
			else if (group.getName().equals("Study termination")) {
				studyTermination = group;
			}
			else if (group.getName().equals("Transition")) {
				transition = group;
			}
		}

		/*
		 * Document Groups
		 */
		 baselineMinus1.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
		 baselineMinus1.setUpdateStatus(statActive);

		 baseline0.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
		 baseline0.addPrerequisiteGroup(baselineMinus1);

		 oneMonth.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
		 oneMonth.addPrerequisiteGroup(baseline0);

		 twoMonths.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
		 twoMonths.addPrerequisiteGroup(oneMonth);

		 threeMonths.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
		 threeMonths.addPrerequisiteGroup(twoMonths);

		 fourMonths.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
		 fourMonths.addPrerequisiteGroup(threeMonths);

		 fiveMonths.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
		 fiveMonths.addPrerequisiteGroup(fourMonths);

		 sixMonths.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
		 sixMonths.addPrerequisiteGroup(fiveMonths);

		 nineMonths.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
		 nineMonths.addPrerequisiteGroup(sixMonths);

		 twelveMonths.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
		 twelveMonths.addPrerequisiteGroup(sixMonths);

		 fifteenMonths.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
		 fifteenMonths.addPrerequisiteGroup(twelveMonths);

		 eighteenMonths.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
		 eighteenMonths.addPrerequisiteGroup(fifteenMonths);

		 twentyOneMonths.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
		 twentyOneMonths.addPrerequisiteGroup(eighteenMonths);

		 twentyFourMonths.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
		 twentyFourMonths.addPrerequisiteGroup(twentyOneMonths);
		 twentyFourMonths.setUpdateStatus(statComplete);

		 studyTermination.setAllowedRecordStatus(getStatuses(dataSet, GenericState.INACTIVE));
		 studyTermination.addAllowedRecordStatus(statLost);
		 studyTermination.addAllowedRecordStatus(statDeceased);

		 transition.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));

/*		 List<DocumentGroup> documentGroups = new ArrayList<DocumentGroup>();
		 documentGroups.add((DocumentGroup)baselineMinus1);
		 documentGroups.add((DocumentGroup)baseline0);
		 documentGroups.add((DocumentGroup)oneMonth);
		 documentGroups.add((DocumentGroup)twoMonths);
		 documentGroups.add((DocumentGroup)threeMonths);
		 documentGroups.add((DocumentGroup)fourMonths);
		 documentGroups.add((DocumentGroup)fiveMonths);
		 documentGroups.add((DocumentGroup)sixMonths);
		 documentGroups.add((DocumentGroup)nineMonths);
		 documentGroups.add((DocumentGroup)twelveMonths);
		 documentGroups.add((DocumentGroup)fifteenMonths);
		 documentGroups.add((DocumentGroup)eighteenMonths);
		 documentGroups.add((DocumentGroup)twentyOneMonths);
		 documentGroups.add((DocumentGroup)twentyFourMonths);
		 documentGroups.add((DocumentGroup)studyTermination);
		 documentGroups.add((DocumentGroup)transition);

		 ((DataSet)dataSet).setDocumentGroups(documentGroups);*/
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
