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

package org.psygrid.outlook.patches.v0_9_4;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.DerivedEntry;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * Patch to correct the formula of the "Problem List Total" derived
 * entry in the "Drug Check" document.
 *
 * @author Rob Harper
 *
 */
public class Patch1 extends AbstractPatch {

    private static final String PATCH_NAME = "Problem List Total formula correction for Drug Check";

    public String getName() {
        return PATCH_NAME;
    }

    public void applyPatch(DataSet ds, String saml) throws Exception {
        Document drugCheck = ds.getDocument(8);
        //check this the correct document
        if ( !"Drug Check".equals(drugCheck.getName().trim()) ){
            throw new RuntimeException("This is not the Drug Check document - it is "+drugCheck.getName());
        }
        DerivedEntry probListTotal = (DerivedEntry)drugCheck.getEntry(55);
        //check this is the correct entry
        if ( !"Problem List Total".equals(probListTotal.getName())){
            throw new RuntimeException("This is not the Problem List Total entry - it is "+probListTotal.getName());
        }
        probListTotal.setFormula("a + b + c + d + e + f + g + h + i + j + k + l + m");
    }

}
