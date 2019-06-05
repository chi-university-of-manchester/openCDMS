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

package org.psygrid.data.reporting;


/**
 * Class to represent a single record report to render.
 * 
 * @author Lucy Bridges
 */

public class RecordReport extends Report {

	
	   /**
     * The subject of the report (e.g. patient identifier)
     */
    private String subject;
    
    /**
     * The name of the user who requested the report.
     */
    private String requestor;
    

    /**
     * Get the name of the user who requested the report.
     * 
     * @return The name of the user who requested the report.
     */
    public String getRequestor() {
        return requestor;
    }

    /**
     * Set the name of the user who requested the report.
     * 
     * @param requestor The name of the user who requested the report.
     */
    public void setRequestor(String requestor) {
        this.requestor = requestor;
    }

    /**
     * Get the subject of the report (e.g. patient identifier)
     * 
     * @return The subject of the report
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Set the subject of the report (e.g. patient identifier)
     * 
     * @param subject The subject of the report
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }
}
