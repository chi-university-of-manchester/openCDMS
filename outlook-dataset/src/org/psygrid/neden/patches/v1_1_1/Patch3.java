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

package org.psygrid.neden.patches.v1_1_1;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Status;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch3 extends AbstractPatch {

    @Override
    public String getName() {
        return "Add inactive=true to relevant DataSet statuses";
    }

    @Override
    public void applyPatch(DataSet ds, String saml) throws Exception {

        Status ineligible = ds.getStatus(1);
        if ( !"Ineligible".equals(ineligible.getShortName()) ){
            throw new RuntimeException("This isn't the Ineligible status, it is "+ineligible.getShortName());
        }
        ineligible.setInactive(true);

        Status unableToConsent = ds.getStatus(2);
        if ( !"Unable".equals(unableToConsent.getShortName()) ){
            throw new RuntimeException("This isn't the Unable status, it is "+unableToConsent.getShortName());
        }
        unableToConsent.setInactive(true);

        Status refused = ds.getStatus(4);
        if ( !"Refused".equals(refused.getShortName()) ){
            throw new RuntimeException("This isn't the Refused status, it is "+refused.getShortName());
        }
        refused.setInactive(true);

        Status withdrawn = ds.getStatus(5);
        if ( !"Withdrawn".equals(withdrawn.getShortName()) ){
            throw new RuntimeException("This isn't the Withdrawn status, it is "+withdrawn.getShortName());
        }
        withdrawn.setInactive(true);

        Status twelveMonth = ds.getStatus(8);
        if ( !"12Month".equals(twelveMonth.getShortName()) ){
            throw new RuntimeException("This isn't the 12Month status, it is "+twelveMonth.getShortName());
        }
        twelveMonth.setInactive(true);

        Status deceased = ds.getStatus(9);
        if ( !"Deceased".equals(deceased.getShortName()) ){
            throw new RuntimeException("This isn't the Deceased status, it is "+deceased.getShortName());
        }
        deceased.setInactive(true);

        Status withdrew = ds.getStatus(10);
        if ( !"Withdrew".equals(withdrew.getShortName()) ){
            throw new RuntimeException("This isn't the Withdrew status, it is "+withdrew.getShortName());
        }
        withdrew.setInactive(true);

    }

}
