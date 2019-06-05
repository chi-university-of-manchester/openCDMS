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
package org.psygrid.collection.entry.chooser;

import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.reporting.definition.IReport;

/**
 * Choosable implementation to represent a Report.
 * <p>
 * Is always at the bottom level of a Choosable hierarchy, so
 * cannot have children.
 * 
 * @author Rob Harper
 *
 */
public class ChoosableReport extends AbstractChoosable {
    
    private final IReport reportDefinition;
    private final Record record;
    
    public ChoosableReport(IReport reportDefinition, Record record, ChoosableReportRecord parent) {
        super(parent);
        this.reportDefinition = reportDefinition;
        this.record = record;
    }
    
    public final Record getRecord() {
        return record;
    }
    
    public final IReport getReportDefinition() {
        return reportDefinition;
    }

    public String getDisplayText() {
        return reportDefinition.getTitle();
    }

    public ChoosableType getType() {
        return ChoosableType.REPORT;
    }

}
