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
package org.psygrid.outlook;

import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Factory;

public class CSSRI_Followup extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document doc = factory.createDocument(
                "CSSRI",
                "Client Sociodemographic and Service Receipt Inventory (CSSRI - EU) - Follow Up");

        createDocumentStatuses(factory, doc);

        //add section 1 (Living situation)
        CSSRI.addLivingSituationSection(doc, factory, 1, true);

        //add section 2 (Employment and Income)
        CSSRI.addEmploymentSection(doc, factory, 2, true);

        //add section 3 (Service Receipt)
        CSSRI.addServiceReceiptSection(doc, factory, 3, true);

        //add section 4 (Medication Profile)
        CSSRI.addMedicationProfileSection(doc, factory, 4, true);

        //add section 5 (Informal Care and Help)
        CSSRI.addInformalCareSection(doc, factory, 5, true);

        //add section 6 (Hospital Or Community Accomodation Details)
        CSSRI.addAccomodationDetails(doc, factory, 6);

        return doc;
    }

}
