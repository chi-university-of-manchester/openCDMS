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

package org.psygrid.edie.patches.v1_1_12;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.edie.BAPS;
import org.psygrid.edie.BriefCoreSchemaScales;
import org.psygrid.edie.IVI;
import org.psygrid.edie.MCQ30;
import org.psygrid.edie.PADS10;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch27 extends AbstractPatch {

	public String getName() {
		return "Add the ED2 follow on study documents";
	}

	public void applyPatch(DataSet ds, String saml) throws Exception {


		Factory factory = new HibernateFactory();
		{
			Document baps = BAPS.createDocument(factory);

			ds.addDocument(baps);
			ConsentFormGroup cfg = ds.getAllConsentFormGroup(0);
			if (!"Main client consent".equals(cfg.getDescription())) {
				throw new RuntimeException(
						"This is not the Main Client Consent consent form group - it is "
								+ cfg.getDescription());
			}
			baps.addConsentFormGroup(cfg);
			DocumentOccurrence baps1month = factory
					.createDocumentOccurrence("1 Month");
			baps1month.setDisplayText("1 Month");
			DocumentGroup grp1m = ds.getDocumentGroup(2);
			if (!"1 month Group".equals(grp1m.getName())) {
				throw new RuntimeException(
						"This is not the 1 month Group - it is "
								+ grp1m.getName());
			}
			baps1month.setDocumentGroup(grp1m);
			baps.addOccurrence(baps1month);

			DocumentOccurrence baps6months = factory
					.createDocumentOccurrence("6 Months");
			baps6months.setDisplayText("6 Months");
			DocumentGroup grp6m = ds.getDocumentGroup(7);
			if (!"6 months Group".equals(grp6m.getName())) {
				throw new RuntimeException(
						"This is not the 6 months Group - it is "
								+ grp6m.getName());
			}
			baps6months.setDocumentGroup(grp6m);
			baps.addOccurrence(baps6months);
		}

		{
			Document pads10 = PADS10.createDocument(factory);

			ds.addDocument(pads10);
			ConsentFormGroup cfg = ds.getAllConsentFormGroup(0);
			if (!"Main client consent".equals(cfg.getDescription())) {
				throw new RuntimeException(
						"This is not the Main Client Consent consent form group - it is "
								+ cfg.getDescription());
			}
			pads10.addConsentFormGroup(cfg);
			DocumentOccurrence pads101month = factory
					.createDocumentOccurrence("1 Month");
			pads101month.setDisplayText("1 Month");
			DocumentGroup grp1m = ds.getDocumentGroup(2);
			if (!"1 month Group".equals(grp1m.getName())) {
				throw new RuntimeException(
						"This is not the 1 month Group - it is "
								+ grp1m.getName());
			}
			pads101month.setDocumentGroup(grp1m);
			pads10.addOccurrence(pads101month);

			DocumentOccurrence pads106months = factory
					.createDocumentOccurrence("6 Months");
			pads106months.setDisplayText("6 Months");
			DocumentGroup grp6m = ds.getDocumentGroup(7);
			if (!"6 months Group".equals(grp6m.getName())) {
				throw new RuntimeException(
						"This is not the 6 months Group - it is "
								+ grp6m.getName());
			}
			pads106months.setDocumentGroup(grp6m);
			pads10.addOccurrence(pads106months);
		}

		{
			Document mcg30 = MCQ30.createDocument(factory);

			ds.addDocument(mcg30);
			ConsentFormGroup cfg = ds.getAllConsentFormGroup(0);
			if (!"Main client consent".equals(cfg.getDescription())) {
				throw new RuntimeException(
						"This is not the Main Client Consent consent form group - it is "
								+ cfg.getDescription());
			}
			mcg30.addConsentFormGroup(cfg);
			DocumentOccurrence mcg301month = factory
					.createDocumentOccurrence("1 Month");
			mcg301month.setDisplayText("1 Month");
			DocumentGroup grp1m = ds.getDocumentGroup(2);
			if (!"1 month Group".equals(grp1m.getName())) {
				throw new RuntimeException(
						"This is not the 1 month Group - it is "
								+ grp1m.getName());
			}
			mcg301month.setDocumentGroup(grp1m);
			mcg30.addOccurrence(mcg301month);

			DocumentOccurrence mcg306months = factory
					.createDocumentOccurrence("6 Months");
			mcg306months.setDisplayText("6 Months");
			DocumentGroup grp6m = ds.getDocumentGroup(7);
			if (!"6 months Group".equals(grp6m.getName())) {
				throw new RuntimeException(
						"This is not the 6 months Group - it is "
								+ grp6m.getName());
			}
			mcg306months.setDocumentGroup(grp6m);
			mcg30.addOccurrence(mcg306months);
		}

		{
			Document bccs = BriefCoreSchemaScales
					.createDocument(factory);

			ds.addDocument(bccs);
			ConsentFormGroup cfg = ds.getAllConsentFormGroup(0);
			if (!"Main client consent".equals(cfg.getDescription())) {
				throw new RuntimeException(
						"This is not the Main Client Consent consent form group - it is "
								+ cfg.getDescription());
			}
			bccs.addConsentFormGroup(cfg);
			DocumentOccurrence bccs1month = factory
					.createDocumentOccurrence("1 Month");
			bccs1month.setDisplayText("1 Month");
			DocumentGroup grp1m = ds.getDocumentGroup(2);
			if (!"1 month Group".equals(grp1m.getName())) {
				throw new RuntimeException(
						"This is not the 1 month Group - it is "
								+ grp1m.getName());
			}
			bccs1month.setDocumentGroup(grp1m);
			bccs.addOccurrence(bccs1month);

			DocumentOccurrence bccs6months = factory
					.createDocumentOccurrence("6 Months");
			bccs6months.setDisplayText("6 Months");
			DocumentGroup grp6m = ds.getDocumentGroup(7);
			if (!"6 months Group".equals(grp6m.getName())) {
				throw new RuntimeException(
						"This is not the 6 months Group - it is "
								+ grp6m.getName());
			}
			bccs6months.setDocumentGroup(grp6m);
			bccs.addOccurrence(bccs6months);
		}

		{
			Document ivi = IVI.createDocument(factory);

			ds.addDocument(ivi);
			ConsentFormGroup cfg = ds.getAllConsentFormGroup(0);
			if (!"Main client consent".equals(cfg.getDescription())) {
				throw new RuntimeException(
						"This is not the Main Client Consent consent form group - it is "
								+ cfg.getDescription());
			}
			ivi.addConsentFormGroup(cfg);
			DocumentOccurrence ivi1month = factory
					.createDocumentOccurrence("1 Month");
			ivi1month.setDisplayText("1 Month");
			DocumentGroup grp1m = ds.getDocumentGroup(2);
			if (!"1 month Group".equals(grp1m.getName())) {
				throw new RuntimeException(
						"This is not the 1 month Group - it is "
								+ grp1m.getName());
			}
			ivi1month.setDocumentGroup(grp1m);
			ivi.addOccurrence(ivi1month);

			DocumentOccurrence ivi6months = factory
					.createDocumentOccurrence("6 Months");
			ivi6months.setDisplayText("6 Months");
			DocumentGroup grp6m = ds.getDocumentGroup(7);
			if (!"6 months Group".equals(grp6m.getName())) {
				throw new RuntimeException(
						"This is not the 6 months Group - it is "
								+ grp6m.getName());
			}
			ivi6months.setDocumentGroup(grp6m);
			ivi.addOccurrence(ivi6months);
		}
	}
}
