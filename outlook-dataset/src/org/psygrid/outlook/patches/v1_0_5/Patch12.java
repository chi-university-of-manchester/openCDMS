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

public class Patch12 extends AbstractPatch {

    @Override
    public String getName() {
        return "Personal Details - Add Unemployed option for Mother, Father, Partner occupation";
    }

    @Override
    public void applyPatch(DataSet ds, String saml) throws Exception {

        Factory factory = new HibernateFactory();

        Document doc = ds.getDocument(5);
        if ( !"Personal Details".equals(doc.getName())){
            throw new RuntimeException("This is not the Personal Details form, it is "+doc.getName());
        }

        OptionEntry mother = (OptionEntry)doc.getEntry(17);
        if ( !"Mother's Occupation".equals(mother.getName())){
            throw new RuntimeException("This is not the Mother's Occupation entry, it is "+mother.getName());
        }
        mother.addOption(factory.createOption("Unemployed", 10));

        OptionEntry father = (OptionEntry)doc.getEntry(18);
        if ( !"Father's Occupation".equals(father.getName())){
            throw new RuntimeException("This is not the Father's Occupation entry, it is "+father.getName());
        }
        father.addOption(factory.createOption("Unemployed", 10));

        OptionEntry partner = (OptionEntry)doc.getEntry(19);
        if ( !"Partner's Occupation".equals(partner.getName())){
            throw new RuntimeException("This is not the Partner's Occupation entry, it is "+partner.getName());
        }
        partner.addOption(factory.createOption("Unemployed", 10));

    }

}
