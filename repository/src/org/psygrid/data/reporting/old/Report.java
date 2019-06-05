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

/**
 * Class to represent a report on the data in the repository.
 * 
 * @author Rob Harper
 *
 */
public class Report {

    /**
     * The general title of the report.
     */
    private String title;
    
    /**
     * The name of the entity that is being reported upon.
     * <p>
     * The headings of rows in the Report will all be different
     * states of the entity that is being reported on.
     */
    private String entity;
    
    /**
     * The names of the columns in the report.
     */
    private String[] columns = new String[0];
    
    /**
     * The rows of data in the report.
     */
    private ReportRow[] rows = new ReportRow[0];

    /**
     * Get the names of the columns in the report.
     * 
     * @return The names of the columns.
     */
    public String[] getColumns() {
        return columns;
    }

    /**
     * Set the names of the columns in the report.
     * 
     * @param columns The names of the columns.
     */
    public void setColumns(String[] columns) {
        this.columns = columns;
    }

    /**
     * Get the name of the entity that is being reported upon.
     * <p>
     * The headings of rows in the Report will all be different
     * states of the entity that is being reported on.
     * 
     * @return The name of the entity.
     */
    public String getEntity() {
        return entity;
    }

    /**
     * Set the name of the entity that is being reported upon.
     * <p>
     * The headings of rows in the Report will all be different
     * states of the entity that is being reported on.
     * 
     * @param entity The name of the entity.
     */
    public void setEntity(String entity) {
        this.entity = entity;
    }

    /**
     * Get the rows of data in the report.
     * 
     * @return The rows of data.
     */
    public ReportRow[] getRows() {
        return rows;
    }

    /**
     * Set the rows of data in the report.
     * 
     * @param rows The rows of data.
     */
    public void setRows(ReportRow[] rows) {
        this.rows = rows;
    }

    /**
     * Set the general title of the report.
     * 
     * @return The general title of the report.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the general title of the report.
     * 
     * @param title The general title of the report.
     */
    public void setTitle(String title) {
        this.title = title;
    }

}
