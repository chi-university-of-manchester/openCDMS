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

package org.psygrid.outlook.patches.v1_1_6;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.neden.RelapseRating;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch23 extends AbstractPatch {

    public String getName() {
        return "Add Relapse Rating assessment form";
    }

    public void applyPatch(DataSet ds, String saml) throws Exception {
        Factory factory = new HibernateFactory();
        Document relapseRating = RelapseRating.createDocument(factory);
        ds.addDocument(relapseRating);
        relapseRating.addConsentFormGroup(ds.getAllConsentFormGroup(0));
        DocumentOccurrence rrb = factory.createDocumentOccurrence("Shared");
        rrb.setDisplayText("Shared");
        relapseRating.addOccurrence(rrb);
        DocumentGroup group = ds.getDocumentGroup(6);
        if ( !"Shared".equals(group.getName()) ){
            throw new RuntimeException("This is not the Shared document group - it is "+group.getName());
        }
        rrb.setDocumentGroup(group);
    }

}
