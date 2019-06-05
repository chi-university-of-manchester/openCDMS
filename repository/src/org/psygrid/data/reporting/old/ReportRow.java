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

package org.psygrid.data.reporting.old;

public class ReportRow {

    /**
     * The heading of the row.
     * <p>
     * This should represent a state of the entity being reported on,
     * as specified by the entity property of the Report object that
     * this row is a part of.
     */
    private String heading;
    
    /**
     * The values for the row.
     * <p>
     * The number of values should be the same as the number of 
     * columns defined for the Report that this row is a part of.
     */
    private Double[] values = new Double[0];

    /**
     * Get the heading of the row.
     * <p>
     * This should represent a state of the entity being reported on,
     * as specified by the entity property of the Report object that
     * this row is a part of.
     * 
     * @return The heading of the row.
     */
    public String getHeading() {
        return heading;
    }

    /**
     * Set the heading of the row.
     * <p>
     * This should represent a state of the entity being reported on,
     * as specified by the entity property of the Report object that
     * this row is a part of.
     * 
     * @param heading The heading of the row.
     */
    public void setHeading(String heading) {
        this.heading = heading;
    }

    /**
     * Get the values for the row.
     * <p>
     * The number of values should be the same as the number of 
     * columns defined for the Report that this row is a part of.
     * 
     * @return The values for the row.
     */
    public Double[] getValues() {
        return values;
    }

    /**
     * Set the values for the row.
     * <p>
     * The number of values should be the same as the number of 
     * columns defined for the Report that this row is a part of.
     * 
     * @param values The values for the row.
     */
    public void setValues(Double[] values) {
        this.values = values;
    }

}
