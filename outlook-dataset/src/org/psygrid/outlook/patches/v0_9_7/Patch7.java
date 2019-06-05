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

package org.psygrid.outlook.patches.v0_9_7;

import java.util.Calendar;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.DateValidationRule;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch7 extends AbstractPatch {

    public String getName() {
        return "Remove time component from 'after 1900' validation rule";
    }

    public void applyPatch(DataSet ds, String saml) throws Exception {

        DateValidationRule rule = (DateValidationRule)ds.getValidationRule(13);
        if ( !"After 1900".equals(rule.getDescription().trim()) ){
            throw new RuntimeException("This is not the 'After 1900' validation rule - it is '"+rule.getDescription()+"'");
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(rule.getAbsLowerLimit());
        cal.clear(Calendar.MILLISECOND);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.HOUR);
        cal.clear(Calendar.HOUR_OF_DAY);
        cal.clear(Calendar.AM_PM);
        rule.setAbsLowerLimit(cal.getTime());

    }

}
