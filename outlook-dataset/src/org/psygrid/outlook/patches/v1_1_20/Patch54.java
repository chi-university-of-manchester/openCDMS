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

	package org.psygrid.outlook.patches.v1_1_20;

	import java.util.ArrayList;
import java.util.List;

    import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.DocumentGroup;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.model.hibernate.GenericState;
import org.psygrid.data.model.hibernate.HibernateFactory;
import org.psygrid.data.model.hibernate.Status;
import org.psygrid.outlook.patches.AbstractPatch;

	public class Patch54 extends AbstractPatch {


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
			statInterview2 = null, statInterview3 = null, stat6Month = null;

			/*
			 * Get the record statuses from the dataset
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


			}


			/*
			 * Set Document Groups
			 */
			DocumentGroup baselineA = null,baselineB = null, baselineC = null, sixMonths = null;

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
			}

			/*
			 * Update Document Groups
			 */
			//Remove the prerequisite groups so that the document groups can be completed at the same time as BaselineA
			baselineB.setPrerequisiteGroups(new ArrayList<DocumentGroup>());
			baselineC.setPrerequisiteGroups(new ArrayList<DocumentGroup>());

			//Add baselineA and BaselineB as prerequisites, as these are no longer completed linearly
			sixMonths.addPrerequisiteGroup(baselineA);
			sixMonths.addPrerequisiteGroup(baselineB);

			baselineB.addAllowedRecordStatus(statConsented);

			baselineC.addAllowedRecordStatus(statConsented);
			baselineC.addAllowedRecordStatus(statInterview1);

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
