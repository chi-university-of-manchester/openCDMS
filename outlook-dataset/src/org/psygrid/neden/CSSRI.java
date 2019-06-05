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

package org.psygrid.neden;

import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.outlook.AssessmentForm;

public class CSSRI extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document doc = factory.createDocument(
                "CSSRI",
                "Client Sociodemographic and Service Receipt Inventory (CSSRI - EU)");

        createDocumentStatuses(factory, doc);

        //add section 1 (Living situation)
        org.psygrid.outlook.CSSRI.addSociodemographicsSection(doc, factory, 1);

        //add section 2 (Living situation)
        org.psygrid.outlook.CSSRI.addLivingSituationSection(doc, factory, 2, false);

        //add section 3 (Employment and Income)
        org.psygrid.outlook.CSSRI.addEmploymentSection(doc, factory, 3, false);

        //add section 4 (Service Receipt)
        org.psygrid.outlook.CSSRI.addServiceReceiptSection(doc, factory, 4, false);

        //add section 5 (Medication Profile)
        org.psygrid.outlook.CSSRI.addMedicationProfileSection(doc, factory, 5, false);

        //add section 6 (Informal Care and Help)
        org.psygrid.outlook.CSSRI.addInformalCareSection(doc, factory, 6, false);

        //add section 7 (Hospital Or Community Accomodation Details)
        org.psygrid.outlook.CSSRI.addAccomodationDetails(doc, factory, 7);

        return doc;
    }

}
