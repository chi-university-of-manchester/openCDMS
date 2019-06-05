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

	package org.psygrid.outlook.test.patches.v1_1_12;

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
			return "Update dataset Statuses and DocumentGroups to include generic states and state transitions";
		}

		@Override
		public void applyPatch(DataSet ds, String saml) throws Exception {
			DataSet dataSet = (DataSet)ds;

			//Add new Statuses
			updateStatuses(dataSet);
		}

		private void updateStatuses(DataSet dataSet) {
			Factory factory = new HibernateFactory();

			List<Status> statuses = dataSet.getStatuses();

			Status statConsented = null, statInterview1 = null,
			statInterview2 = null, statInterview3 = null, stat6Month = null, stat12Month = null,
			statDeceased = null, statWithdrew = null;

			/*
			 * Get the new record statuses from the dataset
			 */
			for (Status status: statuses) {
				System.out.println("Found status id "+status.getId());
				//if status matches set to above status..

				if ("Consented".equals(status.getShortName())) {
					statConsented = status;
				}
				if ("Interview1".equals(status.getShortName())) {
					statInterview1 = status;
				}
				if ("Interview2".equals(status.getShortName())) {
					statInterview2 = status;
				}
				if ("Interview3".equals(status.getShortName())) {
					statInterview3 = status;
				}
				if ("6Month".equals(status.getShortName())) {
					stat6Month = status;
				}
				if ("12Month".equals(status.getShortName())) {
					stat12Month = status;
				}
				if ("Deceased".equals(status.getShortName())) {
					statDeceased = status;
				}
				if ("Withdrew".equals(status.getShortName())) {
					statWithdrew = status;
				}

			}

			/*
			 * Update statuses to add a GenericState
			 */
			statConsented.setGenericState(GenericState.ACTIVE);
			statInterview1.setGenericState(GenericState.ACTIVE);
			statInterview2.setGenericState(GenericState.ACTIVE);
			statInterview3.setGenericState(GenericState.ACTIVE);
			stat6Month.setGenericState(GenericState.ACTIVE);
			stat12Month.setGenericState(GenericState.COMPLETED);
			statDeceased.setGenericState(GenericState.LEFT);
			statWithdrew.setGenericState(GenericState.INACTIVE);

			/*
			 * Add new Invalid state
			 */
			Status statInvalid = factory.createStatus("Invalid", "Invalid", 9);	//Record was added by mistake and shouldn't exist
			statInvalid.setInactive(true);
			statInvalid.setGenericState(GenericState.INVALID);

			dataSet.addStatus(statInvalid);

			/*
			 * Add status transitions to new invalid status
			 */
			statConsented.addStatusTransition(statInvalid);             //consented -> invalid
			statInterview1.addStatusTransition(statInvalid);            //interview 1 completed -> invalid
			statInterview2.addStatusTransition(statInvalid);            //interview 2 completed -> invalid
			statInterview3.addStatusTransition(statInvalid);            //interview 3 completed -> invalid
			stat6Month.addStatusTransition(statInvalid);                //6 month follow-up completed -> invalid


			/*
			 * Re-add referred state
			 */
			Status statReferred = factory.createStatus("Referred", "Referred", 0);
			statReferred.setGenericState(GenericState.REFERRED);
			statReferred.addStatusTransition(statConsented);            //referred -> consented
			statReferred.addStatusTransition(statDeceased);             //referred -> deceased
			statReferred.addStatusTransition(statWithdrew);             //referred -> withdrew
			statReferred.addStatusTransition(statInvalid);              //referred -> invalid

			//Add to the dataset as the first status in the list
			dataSet.getStatuses().add(0, (Status)statReferred);


			/*
			 * Set Document Groups
			 */
			DocumentGroup baselineA = null,baselineB = null, baselineC = null, sixMonths = null,
			twelveMonths = null, studyTermination = null, shared = null;

			for (DocumentGroup group: dataSet.getDocumentGroups()) {
				if (group.getName().equals("Baseline Sec A Group")) {
					baselineA = group;
				}
				else if (group.getName().equals("Baseline Sec B Group")) {
					baselineB = group;
				}
				else if (group.getName().equals("Baseline Sec C Group")) {
					baselineC = group;
				}
				else if (group.getName().equals("6 months Group")) {
					sixMonths = group;
				}
				else if (group.getName().equals("12 months")) {
					twelveMonths = group;
				}
				else if (group.getName().equals("Study termination")) {
					studyTermination = group;
				}
				else if (group.getName().equals("Shared")) {
					shared = group;
				}
			}

			/*
			 * Update Document Groups
			 */
			baselineA.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
			baselineA.setUpdateStatus(statInterview1);	//interview 1 completed

			//any state from completion of baselineA (inteview1), inc 12Month completed, can view documents in this group
			baselineB.addAllowedRecordStatus(statInterview1);
			baselineB.addAllowedRecordStatus(statInterview2);
			baselineB.addAllowedRecordStatus(statInterview3);
			baselineB.addAllowedRecordStatus(stat6Month);
			baselineB.addPrerequisiteGroup(baselineA);	//Baseline A must be completed first
			baselineB.setUpdateStatus(statInterview2);

			baselineC.addAllowedRecordStatus(statInterview2);
			baselineC.addAllowedRecordStatus(statInterview3);
			baselineC.addAllowedRecordStatus(stat6Month);
			baselineC.addPrerequisiteGroup(baselineB);
			baselineC.setUpdateStatus(statInterview3);

			sixMonths.addAllowedRecordStatus(statInterview3);
			sixMonths.addAllowedRecordStatus(stat6Month);
			sixMonths.addPrerequisiteGroup(baselineC);
			sixMonths.setUpdateStatus(stat6Month); //auto record update

			twelveMonths.addAllowedRecordStatus(stat6Month);
			twelveMonths.addPrerequisiteGroup(sixMonths);
			twelveMonths.setUpdateStatus(stat12Month);

			studyTermination.setAllowedRecordStatus(getStatuses(dataSet, GenericState.INACTIVE));
			studyTermination.addAllowedRecordStatus(statDeceased);

			shared.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));


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
