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

package org.psygrid.data.reporting.definition.hibernate;

import java.util.Map;

import org.hibernate.Session;
import org.psygrid.data.model.hibernate.Persistent;
import org.psygrid.data.model.hibernate.RetrieveDepth;
import org.psygrid.data.reporting.definition.IManagementChart;
import org.psygrid.data.reporting.definition.IManagementReport;

/**
 * Class to represent an abstract management chart.
 * <p>
 * A management chart is one that features data on the
 * project as a whole.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_mgmt_charts"
 * @hibernate.joined-subclass-key column="c_id"
 */
public abstract class ManagementChart extends Chart implements IManagementChart {

    /**
     * The report that the chart is associated with.
     */
    private ManagementReport report;
    
    public ManagementChart() {
        super();
    }

    public ManagementChart(String type, String title) {
        super(type, title);
    }

    /**
     * Get the report that the chart is associated with.
     * 
     * @return The report.
     * 
     * @hibernate.many-to-one class="org.psygrid.data.reporting.definition.hibernate.ManagementReport"
     *                        column="c_report_id"
     *                        not-null="true"
     *                        insert="false"
     *                        update="false"
     */
    public ManagementReport getReport() {
        return report;
    }

    /**
     * Set the report that the chart is associated with.
     * 
     * @param report The report.
     */
    public void setReport(IManagementReport report) {
        this.report = (ManagementReport)report;
    }

    /**
     * Generate a realization of this management chart
     * 
     * @param session Hibernate session.
     * @return The chart.
     */
    public abstract org.psygrid.data.reporting.Chart generateChart(Session session);   

    
    @Override
    public abstract org.psygrid.data.reporting.definition.dto.ManagementChart toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth);

   /* public void toDTO(org.psygrid.data.reporting.definition.dto.ManagementChart dtoC, Map<Persistent, org.psygrid.data.model.dto.Persistent> dtoRefs, RetrieveDepth depth) {
        super.toDTO(dtoC, dtoRefs, depth);
        dtoC.setReport(this.report.toDTO(dtoRefs, depth));
    }*/

}
