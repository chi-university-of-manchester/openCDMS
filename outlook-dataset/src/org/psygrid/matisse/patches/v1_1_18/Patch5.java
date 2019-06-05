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


package org.psygrid.matisse.patches.v1_1_18;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.matisse.FollowUpQuestionnaire;
import org.psygrid.matisse.MatisseDataset;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch5 extends AbstractPatch {

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#applyPatch(org.psygrid.data.model.IDataSet, java.lang.String)
	 */
	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {

		Factory factory = new HibernateFactory();

		ConsentFormGroup cfg = ds.getAllConsentFormGroup(0);
		DocumentGroup twelveMonths = ds.getDocumentGroup(1);
		if ( !"12 months".equals(twelveMonths.getName()) ){
			throw new RuntimeException("This is not the 12 months doc group, it is "+twelveMonths.getName());
		}
		DocumentGroup twentyFourMonths = ds.getDocumentGroup(2);
		if ( !"24 months".equals(twentyFourMonths.getName()) ){
			throw new RuntimeException("This is not the 24 months doc group, it is "+twentyFourMonths.getName());
		}

		Document followUpQuestionnaire = FollowUpQuestionnaire.createDocument(factory);
		ds.addDocument(followUpQuestionnaire);
		followUpQuestionnaire.addConsentFormGroup(cfg);
		DocumentOccurrence fuq12MonthPrimary =
			factory.createDocumentOccurrence("12 Month (Primary)");
		fuq12MonthPrimary.setDisplayText("12 Month (Primary)");
		followUpQuestionnaire.addOccurrence(fuq12MonthPrimary);
		fuq12MonthPrimary.setDocumentGroup(twelveMonths);
		DocumentOccurrence fuq12MonthSecondary =
			factory.createDocumentOccurrence("12 Month (Secondary)");
		fuq12MonthSecondary.setDisplayText("12 Month (Secondary)");
		followUpQuestionnaire.addOccurrence(fuq12MonthSecondary);
		fuq12MonthSecondary.setDocumentGroup(twelveMonths);

		//email settings
		MatisseDataset.createTwelveMonthReminders(fuq12MonthPrimary, factory);
		MatisseDataset.createTwelveMonthReminders(fuq12MonthSecondary, factory);

		DocumentOccurrence fuq24MonthPrimary =
			factory.createDocumentOccurrence("24 Month (Primary)");
		fuq24MonthPrimary.setDisplayText("24 Month (Primary)");
		followUpQuestionnaire.addOccurrence(fuq24MonthPrimary);
		fuq24MonthPrimary.setDocumentGroup(twentyFourMonths);
		DocumentOccurrence fuq24MonthSecondary =
			factory.createDocumentOccurrence("24 Month (Secondary)");
		fuq24MonthSecondary.setDisplayText("24 Month (Secondary)");
		followUpQuestionnaire.addOccurrence(fuq24MonthSecondary);
		fuq24MonthSecondary.setDocumentGroup(twentyFourMonths);

		//monthly emails
		MatisseDataset.createTwentyFourMonthReminders(fuq24MonthPrimary, factory);
		MatisseDataset.createTwentyFourMonthReminders(fuq24MonthSecondary, factory);

	}

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#getName()
	 */
	@Override
	public String getName() {
		return "Add follow-up questionnaire";
	}

}
