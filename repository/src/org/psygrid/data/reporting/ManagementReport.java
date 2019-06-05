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
 * Class to represent a single management report to render.
 * 
 * @author Rob Harper
 *
 */
public class ManagementReport extends Report {

    /**
     * The list of recipients for the report.
     */
    private String[] recipients = new String[0];

    /**
     * Flag to indicate whether raw data should accompany
     * the graphically rendered report.
     */
    private boolean withRawData;
    
    /**
     * Get the list of recipients for the report.
     * 
     * @return The recipients.
     */
    public String[] getRecipients() {
        return recipients;
    }

    /**
     * Set the list of recipients for the report.
     * 
     * @param recipients The recipients.
     */
    public void setRecipients(String[] recipients) {
        this.recipients = recipients;
    }

    /**
     * Get the flag to indicate whether raw data should accompany
     * the graphically rendered report.
     * 
     * @return The raw data flag.
     */
    public boolean isWithRawData() {
        return withRawData;
    }

    /**
     * Set the flag to indicate whether raw data should accompany
     * the graphically rendered report.
     * 
     * @param withRawData The raw data flag.
     */
    public void setWithRawData(boolean withRawData) {
        this.withRawData = withRawData;
    }
    
}
