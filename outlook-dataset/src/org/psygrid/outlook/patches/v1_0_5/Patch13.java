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

package org.psygrid.outlook.patches.v1_0_5;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.model.hibernate.HibernateFactory;
import org.psygrid.data.model.hibernate.OptionEntry;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch13 extends AbstractPatch {

    @Override
    public String getName() {
        return "Add Inpatient option to Family History, Type of Care Received";
    }

    @Override
    public void applyPatch(DataSet ds, String saml) throws Exception {
        Factory factory = new HibernateFactory();

        Document doc = ds.getDocument(14);
        if ( !"EIS Family History".equals(doc.getName())){
            throw new RuntimeException("This is not the EIS Family History document, it is "+doc.getName());
        }

        OptionEntry typeOfCare = (OptionEntry)doc.getEntry(5);
        if ( !"Type of Care Received Option".equals(typeOfCare.getName())){
            throw new RuntimeException("This is not the Type of Care Received Option entry, it is "+typeOfCare.getName());
        }

        typeOfCare.addOption(factory.createOption("Inpatient", "Inpatient", 3));
    }

}
